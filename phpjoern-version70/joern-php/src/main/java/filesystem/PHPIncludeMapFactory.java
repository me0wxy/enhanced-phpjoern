package filesystem;

import ast.ASTNode;
import ast.ASTNodeProperties;
import ast.expressions.*;
import ast.php.expressions.IncludeOrEvalExpression;
import ast.php.expressions.MagicConstant;
import ast.php.functionDef.FunctionDef;
import ast.php.statements.ClassConstantDeclaration;
import ast.php.statements.ConstantDeclaration;
import ast.php.statements.ConstantElement;
import inputModules.csv.PHPCSVNodeTypes;
import outputModules.common.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class PHPIncludeMapFactory {
    // filesystem in PHPJoern
    private static HashMap<Long, FSNode> FileNodes = new HashMap<Long, FSNode>();
    private static HashMap<Long, FSNode> DirectoryNodes = new HashMap<Long, FSNode>();
    private static LinkedList<FSEdge> DirectoryEdges = new LinkedList<>();
    // a Map to save all constants
    private static HashMap<String, Object> ConstMap = new HashMap<>();
    private static IncludeMap im;
    // collect some useful expressions/declarations
    private static LinkedList<IncludeOrEvalExpression> includeExpressions = new LinkedList<>();
    private static LinkedList<Object> constRelatedExpressions = new LinkedList<>();
    private static LinkedList<CallExpressionBase> functionCalls = new LinkedList<>();

    public static void newInstance(){
        im = new IncludeMap();
        if(includeExpressions.size() == 0)
            return;
        for(FSNode fsNode: FileNodes.values()){
            im.addVertex(fsNode);
        }
        for(FSNode fsNode: DirectoryNodes.values()){
            im.addVertex(fsNode);
        }
        for(FSEdge directoryEdge: DirectoryEdges){
            try{
                im.addEdge(directoryEdge);
            } catch (Exception e){
            }
        }
        im.setRoot();
        createConstantMap();
        createIncludeEdges();
        //return includeMap;
    }

    public static void addNewFileNode(FSNode fsNode){
        if(fsNode.getType().equals(PHPCSVNodeTypes.TYPE_FILE)){
            FileNodes.put(fsNode.getId(), fsNode);
        }
        else if(fsNode.getType().equals(PHPCSVNodeTypes.TYPE_DIRECTORY)){
            DirectoryNodes.put(fsNode.getId(), fsNode);
        }
    }

    public static void addNewDirectoryEdge(Long startid, Long endid){
        try{
            FSNode startNode = DirectoryNodes.get(startid);
            FSNode endNode = FileNodes.containsKey(endid) ? FileNodes.get(endid) : DirectoryNodes.get(endid);
            FSEdge fsEdge = new FSEdge(startNode, endNode, FSEdge.TYPE_DIRECTORY_OF);
            DirectoryEdges.add(fsEdge);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(String.format("invalid directory edge between filenode %d and %d",startid,endid));
        }
    }

    public static void addIncludeExpression(IncludeOrEvalExpression includeExpression){
        includeExpressions.add(includeExpression);
    }
    public static void addConstantRelatedExpression(Object constrelatedexpr){
        constRelatedExpressions.add(constrelatedexpr);
    }
    public static void addFunctionCall(CallExpressionBase callExpression){
        constRelatedExpressions.add(callExpression);
        functionCalls.add(callExpression);
    }

    private static void updateConstMap(String key, Object val){
        if(!ConstMap.containsKey(key)) ConstMap.put(key, val);
    }

    private static void createConstantMap(){
        // PHP default constants (may have others)
        updateConstMap("DIRECTORY_SEPARATOR","/");

        // Traverse all constant declarations
        // some constants may rely on others, so do traverse multiple times
        LinkedList<Object> waitToHandle = new LinkedList<>(constRelatedExpressions);
        int waitSize = waitToHandle.size();
        int lastWaitSize = waitSize;
        while(!waitToHandle.isEmpty()){
            try{
                if(waitSize == 0){
                    waitSize = waitToHandle.size();
                    if(lastWaitSize == waitSize){
                        // done, or we can't parse all constant declarations left
                        break;
                    }
                    lastWaitSize = waitSize;
                }
                Object expression = waitToHandle.removeFirst();
                --waitSize;
                // define("ADMIN_PATH","wp-admin");
                if(expression instanceof CallExpressionBase){
                    CallExpressionBase callexpr = (CallExpressionBase)expression;
                    if(callexpr.getTargetFunc() instanceof Identifier){
                        String funcName = ((Identifier)callexpr.getTargetFunc()).getNameChild().getEscapedCodeStr();
                        if(funcName.equals("define")){
                            String constKey = callexpr.getArgumentList().getArgument(0).getEscapedCodeStr();
                            String constVal = parseStrParam(callexpr.getArgumentList().getArgument(1));
                            if(constVal.contains("WAIT_TO_HANDLE"))
                                waitToHandle.addLast(expression);
                            else if(!constVal.contains("UNSOLVED_DYNAMIC_STRING"))
                                updateConstMap(constKey, constVal);
                        }
                    }
                }
                // const ABSPATH = ...;
                else if(expression instanceof ConstantDeclaration){
                    Iterator<ConstantElement> it = ((ConstantDeclaration)expression).iterator();
                    while(it.hasNext()){
                        ConstantElement constantElement = it.next();
                        String constKey = constantElement.getNameChild().getEscapedCodeStr();
                        String constVal = parseStrParam(constantElement.getValue());
                        if(constVal.contains("WAIT_TO_HANDLE"))
                            waitToHandle.addLast(expression);
                        else if(!constVal.contains("UNSOLVED_DYNAMIC_STRING"))
                            updateConstMap(constKey, constVal);
                    }
                }
                else if(expression instanceof ClassConstantDeclaration){
                    String classname = ((ClassConstantDeclaration)expression).getProperty(ASTNodeProperties.CLASSNAME);
                    Iterator<ConstantElement> it = ((ClassConstantDeclaration)expression).iterator();
                    while(it.hasNext()){
                        ConstantElement constantElement = it.next();
                        String constKey = classname+"::"+constantElement.getNameChild().getEscapedCodeStr();
                        String constVal = parseStrParam(constantElement.getValue());
                        if(constVal.contains("WAIT_TO_HANDLE"))
                            waitToHandle.addLast(expression);
                        else if(!constVal.contains("UNSOLVED_DYNAMIC_STRING"))
                            updateConstMap(constKey, constVal);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /*
    * To parse a Expression (usually in Constant Declatations and Include Expressions) to its string value
    *
    * @return String
    */
    public static String parseStrParam(Expression expression){
        // string or int, return directly
        if(expression instanceof StringExpression || expression instanceof IntegerExpression)
            return expression.getEscapedCodeStr();
        // string concat, parse left and right
        else if(expression instanceof BinaryOperationExpression){
            Expression left = ((BinaryOperationExpression)expression).getLeft();
            Expression right = ((BinaryOperationExpression)expression).getRight();
            return parseStrParam(left)+parseStrParam(right);
        }
        // constants, check in ConstMap
        else if(expression instanceof Constant){
            String constKey = ((Constant)expression).getIdentifier().getNameChild().getEscapedCodeStr();
            return ConstMap.containsKey(constKey) ? (String)ConstMap.get(constKey) : "WAIT_TO_HANDLE";
        }
        // __DIR__ and __FILE__ are magic constants
        else if(expression instanceof MagicConstant){
            String flags = expression.getProperty(ASTNodeProperties.FLAGS);
            switch (flags){
                case "MAGIC_DIR":{
                    String res = getAbsoluteFilePath(expression);
                    return res.substring(0,res.lastIndexOf("/"));
                }
                case "MAGIC_FILE":
                    return getAbsoluteFilePath(expression);
                default:
                    return flags;
            }
        }
        // function call, we only handle dirname() and getcwd()
        else if(expression instanceof CallExpressionBase){
            CallExpressionBase callexpr = (CallExpressionBase)expression;
            if(!(callexpr.getTargetFunc() instanceof Identifier))
                return "UNSOLVED_DYNAMIC_STRING";
            String funcName = ((Identifier)callexpr.getTargetFunc()).getNameChild().getEscapedCodeStr();
            if(funcName.equals("dirname")){
                String inner = parseStrParam(callexpr.getArgumentList().getArgument(0));
                if(inner.equals("."))
                    return "..";
                return inner.contains("/") ? inner.substring(0,inner.lastIndexOf("/")) : inner;
            }
            else if(funcName.equals("getcwd")){
                return ".";
            }
        }
        // others
        return "UNSOLVED_DYNAMIC_STRING";
    }

    private static HashMap<String, FSNode> getAppearOnceFilesMap(){
        // mxy: trick predecessor
        HashMap<String, FSNode> trickFileNode = new HashMap<>();
        HashMap<String, Integer> count = new HashMap<>();
        for(FSNode fsNode: FileNodes.values()){
            String filename = fsNode.getName();
            if(count.containsKey(filename)) {
                int temp = count.get(filename);
                count.put(filename,temp+1);
            }
            else
                count.put(filename,1);
        }
        for(FSNode fsNode: FileNodes.values()){
            if(count.get(fsNode.getName())==1){
                trickFileNode.put(fsNode.getName(),fsNode);
            }
        }
        return trickFileNode;
    }

    private static void createIncludeEdges(){
        int totalIncludeEdges = includeExpressions.size();
        int successfullyBuilt = 0;
        int dynamicNotHandled = 0;

        HashMap<String, FSNode> trickFileNode = getAppearOnceFilesMap();

        for(IncludeOrEvalExpression includexpr: includeExpressions){
            FSNode startFileNode = FileNodes.get(includexpr.getFileId());

            String filepath = parseStrParam(includexpr.getIncludeOrEvalExpression()).replace("//","/");

            if(filepath.contains("UNSOLVED_DYNAMIC_STRING") || filepath.contains("WAIT_TO_HANDLE")){
                // do a trick match, if the FILENAME of filepath appears only once, we will connect
                if(!filepath.contains("/")){
                    dynamicNotHandled++;
                    continue;
                }
                String trickfilename = filepath.substring(filepath.lastIndexOf("/")+1);
                if(trickFileNode.containsKey(trickfilename)){
                    FSEdge includeEdge = new FSEdge(startFileNode, trickFileNode.get(trickfilename), FSEdge.TYPE_INCLUDE);
                    im.addEdge(includeEdge);
                    successfullyBuilt++;
                }
                else{
                    dynamicNotHandled++;
                }
            }
            else{
                // to match filepath in IncludeGraph
                FSNode nowDirectory = im.getParentDirectory(startFileNode);
                for(String pathElem: filepath.split("/")){
                    // absolute path
                    if(im.getRoot() != null && pathElem.equals(im.getRoot().getName()))
                        nowDirectory = im.getRoot();
                    // relative path
                    else if(pathElem.equals(".."))
                        nowDirectory = im.getParentDirectory(nowDirectory);
                    else if(!pathElem.equals("."))
                        nowDirectory = im.getChildDirectoryOrFile(nowDirectory, pathElem);
                    if(nowDirectory == null){
                        break;
                    }
                }
                if(nowDirectory != null && nowDirectory.getType().equals("File")){
                    FSEdge includeEdge = new FSEdge(startFileNode, nowDirectory, FSEdge.TYPE_INCLUDE);
                    im.addEdge(includeEdge);
                    successfullyBuilt++;
                }
            }
        }
        System.out.println();
        System.out.println("Summary Include Map construction");
        System.out.println("----------------------------------------");
        System.out.println("Total "+totalIncludeEdges+" include expressions in "+im.numberOfFiles()+" files");
        System.out.println("Successfully connected: "+successfullyBuilt);
        float mappedMethodCallsPercent = totalIncludeEdges == 0 ? 100 : ((float)successfullyBuilt/(float)totalIncludeEdges)*100;
        System.out.println( "=> " + mappedMethodCallsPercent + "% " +
                "of Include edges could be successfully connected.");
        System.out.println("Dynamic not handled: "+dynamicNotHandled);
        float dynamicpropotion = totalIncludeEdges == 0 ? 0:((float)dynamicNotHandled/(float)totalIncludeEdges)*100;
        System.out.println( "=> " + dynamicpropotion + "% " +
                "of Include edges could not handled due to dynamic definition.");

        System.out.println();

    }

    public static void writeIncludeEdges(){
        for(FSEdge fsEdge:im.getEdges()){
            if(fsEdge.getLabel().equals(FSEdge.TYPE_INCLUDE)){
                Writer.addEdge(fsEdge.getSource().getId(), fsEdge.getDestination().getId(), null, FSEdge.TYPE_INCLUDE);
            }
        }
        Writer.reset();
    }

    public static HashSet<Long> getIncludeFilesSet(Long fileid){
        HashSet<Long> res = new HashSet<>();
        res.add(fileid);
        LinkedList<FSNode> wait = new LinkedList<>();
        wait.add(FileNodes.get(fileid));
        while(!wait.isEmpty()){
            for(FSNode fsNode: im.getInclude(wait.pop())){
                if(!res.contains(fsNode.getId())){
                    wait.add(fsNode);
                    res.add(fsNode.getId());
                }
            }
        }
        return res;
    }
    
    public static String getAbsoluteFilePath(Expression expression){
        FSNode fsNode = FileNodes.get(Long.parseLong(expression.getProperty(ASTNodeProperties.FILEID)));
        String res = fsNode.getName();
        FSNode parentNode = fsNode;
        do{
            fsNode = parentNode;
            parentNode = im.getParentDirectory(fsNode);
            res = parentNode.getName()+"/"+res;
        }while (parentNode != fsNode);
        return res;
    }

    public static String debug(ASTNode expression){
        return FileNodes.get(expression.getFileId()).getName();
    }
}
