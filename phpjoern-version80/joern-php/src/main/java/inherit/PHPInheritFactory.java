package inherit;

import ast.ASTNode;
import ast.CodeLocation;
import ast.NullNode;
import ast.expressions.Identifier;
import ast.expressions.IdentifierList;
import ast.expressions.StringExpression;
import ast.php.declarations.ClassDef;
import ast.php.statements.blockstarters.TraitAdaptations;
import ast.php.statements.blockstarters.UseTrait;
import filesystem.PHPIncludeMapFactory;
import inherit.fake.FakeClassCreator;
import inherit.fake.FakeExtendsEdge;
import inherit.fake.FakeImplementsEdge;
import inherit.fake.FakeTraitEdge;
import misc.MultiHashMap;
import outputModules.common.Writer;
import tools.php.ast2cpg.CommandLineInterface;

import java.util.*;

public class PHPInheritFactory {

    // gather all AST_CLASS ASTNode
    private static LinkedList<ClassDef> classDefs = new LinkedList<>();

    // gather all AST_CLASS(flags=CLASS_TRAIT) ASTNode
    private static LinkedList<ClassDef> classDefUseTrait = new LinkedList<>();

    // gather all AST_USE_TRAIT ASTNode
    private static LinkedList<UseTrait> useTraits = new LinkedList<>();

    // Map classname and classid
    private static HashMap<Long, ClassDef> classIdNameMap = new HashMap<>();

    // for MXY
    private static MultiHashMap<String, String> ClassHierarchyMap = new MultiHashMap<String, String>();

    // count missing edges for extends/implements edges
    private static long baseLacked = 0;
    private static long baseLackedFlag = 0;

    // fake extends edges
    private static HashMap<ClassDef, String> baseLackedExtendsPairs = new HashMap<>();
    // fake implements edges
    private static HashMap<ClassDef, String> baseLackedImplementsParis = new HashMap<>();
    // fake trait edges
    private static HashMap<ClassDef, String> baseLackedTraitPairs = new HashMap<>();

    // * ambiguous edges can be solved by relax mode
    private static long baseAmbiguous = 0;
    private static Boolean baseAmbiguousFlag = false;

    // count missing edges for trait edges
    private static long traitLacked = 0;
    private static long traitLackedFlag = 0;

    private static long traitAmbiguous = 0;
    private static Boolean traitAmbiguousFlag = false;

    private static long baseCycled = 0;
    private static LinkedList<ClassDef> baseCycledList = new LinkedList<>();

    // gather all ambiguous AST_CLASS related with EXTENDS Edges
    private static HashMap<ClassDef, String> ambiguousClassExtendsNodes = new HashMap<>();

    // gather all ambiguous AST_CLASS related with IMPLEMENTS Edges
    private static HashMap<ClassDef, String> ambiguousClassImplementsNodes = new HashMap<>();

    // gather all ambiguous AST_CLASS related with TRAIT Edges
    private static HashMap<String, ClassDef> ambiguousTraitNodes = new HashMap<>();

    // create an Inherit Graph
    public static IG newInstance() {
        IG ig = new IG();

        // Connect Edges by parseMode
        String parseMode = CommandLineInterface.getParseMode();

        createInheritEdges(ig);

        if ("relax".equals(parseMode))
        {
             // Create Ambiguous Edges
             createInheritAmbiguousEdges(ig);
        }

        createTraitEdges(ig);

        if ("relax".equals(parseMode))
        {
            // Create Ambiguous Trait Edges
            createInheritAmbiguousTraitEdges(ig);
        }

        return ig;
    }

    // for mxy
    public static List<String> getHierarchyMultiHashMap(String className) {
        return ClassHierarchyMap.get(className);
    }


    public static void addClassIdNameMap(long classid, ClassDef classDef)
    {
        classIdNameMap.put(classid, classDef);
    }

    /**
     * Add Unique(without ambiguous classname after namespace added) ClassDef ASTNode to LinkedList<ClassDef>
     * @param classDef
     */
    public static void addClassDefNodes(ClassDef classDef) {
        classDefs.add(classDef);
    }

    /**
     * Add UseTrait ASTNode to LinkedList<UseTrait>
     * @param useTrait
     */
    public static void addUseTraitNodes(UseTrait useTrait) {
        useTraits.add(useTrait);
    }

    public static void addClassDefUseTraitNodes(ClassDef useTrait) {
        classDefUseTrait.add(useTrait);
    }


    /**
     * In this case, the Base Class and child Class is under different namespace
     * @param code
     * @return
     */
    private static ClassDef getParentClassByNamespace(String code) {

        ClassDef res = null;

        Integer count = 0;

        for (ClassDef classDef : classDefs) {
            String NamewithNS = classDef.getNamewithNS();

            if (code.equals(NamewithNS)) {
                res = classDef;
                count ++;
            }
        }

        // If count not equals to 1
        // it means there is more than 1 class meet the Parent Class
        // which refers to an ambiguous situation
        if (count != 1) {
            res = null;
            if (count >= 2)
                baseAmbiguousFlag = true;
            else if (count == 0)
                baseLackedFlag ++;
        }
        return res;
    }

    /**
     * In this case, the Base Class and Child Class in under the same namespace
     * @param code
     * @param childClass
     * @return
     */
    private static ClassDef getParentClassInSameNamespace(String code, ClassDef childClass) {
        ClassDef ret = null;

        int count = 0;

        String namespace = childClass.getNameSpace();
        String fullName = namespace + "\\" + code;

        for (ClassDef classDefItem : classDefs) {
            String NamewithNS = classDefItem.getNamewithNS();
            if (NamewithNS == null) continue;
            String classname = classDefItem.getName();
            if (classname.equals(NamewithNS))
                NamewithNS = "\\" + NamewithNS;
            if (NamewithNS.contains(code) && fullName.equals(NamewithNS)) {
                ret = classDefItem;
                count ++;
            }
        }

        if (count != 1) {
            ret = null;
            if (count >= 2)
                baseAmbiguousFlag = true;
            else if (count == 0)
                baseLackedFlag ++;
        }

        return ret;
    }

    /**
     * Find Parent Class by IncludeMap Info
     * @param childFileId
     */
    private static ClassDef getParentClassByInclude(Long childFileId, String code) {

        ClassDef res = null;

        int count = 0;

        HashSet<Long> targetIncludeFileIds = PHPIncludeMapFactory.getIncludeFilesSet(childFileId);

        // No include graph for childFileid, return null
        if (targetIncludeFileIds == null || targetIncludeFileIds.size() == 1) {
            baseLackedFlag ++;
            return null;
        }

        for (Long targetFileId : targetIncludeFileIds) {
            System.out.println(targetFileId);
            for (ClassDef classDef: classDefs) {
                Long candidateFileId = classDef.getFileId();
                if (candidateFileId.equals(targetFileId) == false)
                    continue;
                String NamewithNS = classDef.getNamewithNS();
                if (code.equals(NamewithNS)) {
                    res = classDef;
                    count ++;
                }
            }
        }

        if (count != 1) {
            res = null;
            if (count >=2)
                baseAmbiguousFlag = true;
            else if (count == 0)
                baseLackedFlag ++;
        }

        return res;
    }

    /**
     * Adds an EXTENDS edge to a given Inherit Graph
     * @param ig
     * @param srcClassDef subClass
     * @param destClassDef  baseClass
     * @return true if an edge is added, false otherwise
     */
    private static boolean addInheritExtendsEdge(IG ig, ClassDef srcClassDef, ClassDef destClassDef) {

        boolean ret = false;

        InheritNode src = new InheritNode(srcClassDef);
        InheritNode dest = new InheritNode(destClassDef);
        ret = ig.addVertex(src);
        ig.addVertex(dest);
        ig.addEdge(new InheritExtendsEdge(src, dest));

        return ret;
    }

    private static boolean addFakeCHGEdge(IG ig, ClassDef srcClassDef, ClassDef destClassDef, String edgeType) {

        boolean ret = false;

        InheritNode src = new InheritNode(srcClassDef);
        InheritNode dest = new InheritNode(destClassDef);

        ret = ig.addVertex(src);
        ig.addVertex(dest);
        // FakeClassEdge edge = new FakeClassEdge(srcNode, destNode);
        if (edgeType.equals(FakeExtendsEdge.getEdgeType()))
            ig.addEdge(new FakeExtendsEdge(src, dest));
        else if (edgeType.equals(FakeImplementsEdge.getEdgeType()))
            ig.addEdge(new FakeImplementsEdge(src, dest));
        else if (edgeType.equals(FakeTraitEdge.getEdgeType()))
            ig.addEdge(new FakeTraitEdge(src, dest));

        return ret;
    }

    /**
     * Adds an IMPLEMENTS edge to a given Inherit Graph
     * @param ig
     * @param srcClassDef
     * @param destClassDef
     * @return true if an edge is added, false otherwise
     */
    private static boolean addInheritImplementsEdges(IG ig, ClassDef srcClassDef, ClassDef destClassDef) {

        boolean ret = false;

        InheritNode src = new InheritNode(srcClassDef);
        InheritNode dest = new InheritNode(destClassDef);
        ret = ig.addVertex(src);
        ig.addVertex(dest);
        ig.addEdge(new InheritImplementsEdge(src, dest));

        return ret;
    }

    /**
     * Adds a Trait edge to a give IG
     * @param ig
     * @param srcClassDef AST_CLASS nodes which uses the Trait
     * @param destClassDef Trait
     * @return true if an edge is added, false otherwise
     */
    private static boolean addInheritTraitEdges(IG ig, ClassDef srcClassDef, ClassDef destClassDef) {

        boolean ret = false;

        InheritNode src = new InheritNode(srcClassDef);
        InheritNode dest = new InheritNode(destClassDef);

        ret = ig.addVertex(src);
        ig.addVertex(dest);
        ig.addEdge(new InheritTraitEdge(src, dest));

        return ret;
    }

    /**
     * Create Inherit Graph Edges, Treat ordinary Class and Interface differently
     * ordinary Class (Single-Inherit) : Identifier
     * interface (Multi-Inherit) : IdentifierList
     * @param ig
     */
    private static void createInheritEdges(IG ig) {
        long classDefNodesNum = classDefs.size();
        long inheritNodeNum = 0;
        long successfullyBuiltExtends = 0;
        long successfullyBuiltImplements = 0;
        long successfullyBuilt = 0;

        try {
            for (ClassDef classdef : classDefs) {

                String fullClassName = classdef.getNamewithNS();
                //System.out.println("Now we are visiting the AST_CLASS ASTNode : " + classdef.getName() + " ( " + fullClassName + " )");

                int childCount = classdef.getChildCount();
                Long fileid = classdef.getFileId();
                //System.out.println("The fileid is : " + fileid.toString());

                for (int i = 0; i < childCount; i++) {

                    ASTNode childNode = classdef.getChild(i);
                    // We do not deal with NullNode
                    if (childNode instanceof NullNode)
                        continue;

                    // class a extends b
                    // single-inheritance
                    if (childNode instanceof Identifier) {
                        baseLackedFlag = 0;
                        baseAmbiguousFlag = false;
                        inheritNodeNum ++;

                        // NAME_FQ or NAME_NOT_FQ
                        String identifierFlag = childNode.getFlags();

                        // [ Identifier ] ==> [ StringExpression ]
                        int stringExpCount = childNode.getChildCount();
                        if (0 == stringExpCount)
                            throw new ArrayIndexOutOfBoundsException("Identifier Node should have at least one child.");
                        StringExpression stringExpression = (StringExpression) childNode.getChild(0);
                        String code = stringExpression.getEscapedCodeStr();

//                        if (identifierFlag.equals(PHPCSVNodeTypes.FLAG_NAME_FQ))
//                            code = "\\" + code;

                        // get parent class
                        // ClassDef superClass = getClassDefByCode(namespace, code, fileid);
                        ClassDef superClass = getParentClassInSameNamespace(code, classdef);
                        ClassDef subClass = classdef;

                        if (superClass == null)
                            superClass = getParentClassByNamespace(code);

                        // look into the include relationships
                        if (superClass == null) {
                            superClass = getParentClassByInclude(fileid, code);
                        }

                        if (superClass != null) {
                            String superClassFullName = superClass.getNamewithNS();
                            String subClassFullName = subClass.getNamewithNS();

                            // Child Class cannot be anonymous class
                            if (subClassFullName != null && subClassFullName.equals(superClassFullName)) {
                                baseCycled++;
                                baseCycledList.add(superClass);
                                superClass = null;
                            }
                        }

                        if (superClass != null) {
                            addInheritExtendsEdge(ig, subClass, superClass);
                            successfullyBuiltExtends ++;
                            successfullyBuilt ++;

                            // ClassHierarchyMap.add(subClass.getNamewithNS(), superClass.getNamewithNS());

                            System.out.println("Child Class : " + subClass.getName() + "( " + subClass.getNamewithNS() +
                                    " ) ==> Base Class : " + superClass.getName() + "( " + superClass.getNamewithNS() + " )");
                        } else {
                            if (baseLackedFlag == 3)
                            {
                                baseLacked ++;
                                // save baseLacked parent-child class(ClassDef)-classname(String) Pair for extends chg edges
                                baseLackedExtendsPairs.put(subClass, code);
                            }
                            if (baseAmbiguousFlag) {
                                ambiguousClassExtendsNodes.put(classdef, code);
                                baseAmbiguous++;
                            }

                            System.out.println("Cannot find Base Class for " + classdef.getName() +
                                    " ( " + classdef.getNamewithNS() + " )");
                        }
                    }

                    // class a implements c(,d)
                    // multi-implementations
                    if (childNode instanceof IdentifierList) {

                        // [ IdentifierList ] ==> [ Identifier ] ==> [ StringExpression ]
                        int idfCount = childNode.getChildCount();
                        if (0 == idfCount)
                            throw new ArrayIndexOutOfBoundsException("Identifier Node should have at least one child");
                        for (int k = 0; k < idfCount; k++) {
                            baseLackedFlag = 0;
                            baseAmbiguousFlag = false;
                            inheritNodeNum ++;

                            Identifier identifier = (Identifier) childNode.getChild(k);

                            // NAME_FQ or NAME_NOT_FQ
                            String identifierFlag = identifier.getFlags();

                            StringExpression stringExpression = (StringExpression) identifier.getChild(0);
                            String code = stringExpression.getEscapedCodeStr();
//                            if (identifierFlag.equals(PHPCSVNodeTypes.FLAG_NAME_FQ))
//                                code = "\\" + code;

                            // get parent class
                            // ClassDef superClass = getClassDefByCode(namespace, code, fileid);
                            ClassDef superClass = getParentClassInSameNamespace(code, classdef);
                            ClassDef subClass = classdef;

                            if (superClass == null)
                                superClass = getParentClassByNamespace(code);

                            // look into the include relationships
                            if (superClass == null) {
                                superClass = getParentClassByInclude(fileid, code);
                            }

                            if (superClass != null) {
                                String superClassFullName = superClass.getNamewithNS();
                                String subClassFullName = subClass.getNamewithNS();
                                if (subClassFullName !=null && subClassFullName.equals(superClassFullName)) {
                                    baseCycled++;
                                    baseCycledList.add(superClass);
                                    superClass = null;
                                }
                            }

                            // In case that superClass is empty
                            if (superClass != null) {
                                addInheritImplementsEdges(ig, subClass, superClass);
                                successfullyBuiltImplements ++;
                                successfullyBuilt ++;

                                ClassHierarchyMap.add(subClass.getNamewithNS(), superClass.getNamewithNS());

                                System.out.println("Child Interface : " + subClass.getName() + "( " + subClass.getNamewithNS() +
                                        " ) ==> Base Interface : " + superClass.getName() + "( " + superClass.getNamewithNS() + " )");
                            } else {
                                if (baseLackedFlag == 3) {
                                    baseLacked++;
                                    // save baseLacked parent-child class(ClassDef)-classname(String) Pair for implements edges
                                    baseLackedImplementsParis.put(subClass, code);
                                }
                                if (baseAmbiguousFlag) {
                                    ambiguousClassImplementsNodes.put(classdef, code);
                                    baseAmbiguous++;
                                }

                                System.out.println("Cannot find Base Interface for " + classdef.getName() +
                                        " ( " + classdef.getNamewithNS() + " )");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Some error has occurred during the generation of class hierarchy Graph");
        }

        System.out.println();
        System.out.println("Summary Class Hierarchy Graph (Extends and Implements) construction");
        System.out.println("----------------------------------------");
        System.out.println("Total " + classDefNodesNum + " Classes in whole files.");
        System.out.println("Total " + inheritNodeNum + " EXTENDS/IMPLEMENTS statements in whole files.");
        System.out.println("Successfully connected: " + successfullyBuilt + " Edges.");
        System.out.println("Including: " + successfullyBuiltExtends + " Extends Edges. ");
        System.out.println("Including: " + successfullyBuiltImplements + " Implements Edges.");
        System.out.println(baseAmbiguous + " edges are ambiguous.");
        System.out.println(baseLacked + " edges lost due to lack of Base Definition node.");
        if (baseCycledList.size() != 0) {
            System.out.println("Base Cycled Classes are :");
            for (ClassDef item : baseCycledList) {
                System.out.println(item.getNamewithNS());
            }
        }

        float mappedExtendsPercent = inheritNodeNum == 0 ? 100 : ((float) successfullyBuilt / (float) inheritNodeNum) * 100;
        System.out.println( "=> " + mappedExtendsPercent + " % " +
                "of EXTENDS/IMPLEMENTS edges could be successfully connected.");
        float ambiguousPropotion = inheritNodeNum == 0 ? 0 : ((float) baseAmbiguous / (float) inheritNodeNum) * 100;
        System.out.println("Ambiguous not handled: " + ambiguousPropotion + " %.");
        float lackedProption = inheritNodeNum == 0 ? 0 : ((float) (baseLacked + baseCycled) / (float) inheritNodeNum) * 100;
        System.out.println("Lacked Base Definition not handled: " + lackedProption + " %.");


        addFakeEdgesToCHG(ig, baseLackedExtendsPairs, FakeExtendsEdge.getEdgeType());

        addFakeEdgesToCHG(ig, baseLackedImplementsParis, FakeImplementsEdge.getEdgeType());

    }

    public static ClassDef getClassTraitByNamespace(String traitName) {

        ClassDef ret = null;

        int count = 0;

        for (ClassDef classDefTrait: classDefUseTrait) {
            String name = classDefTrait.getNamewithNS();
            if (name.equals(traitName)) {
                ret = classDefTrait;
                count ++;
            }
        }

        if (count != 1) {
            if (count >= 2)
                traitAmbiguousFlag = true;
            else if (count == 0)
                traitLackedFlag ++;

            ret = null;
        }
        return ret;
    }

    /**
     *
     * @param traitName
     * @param useClass Class which uses Trait
     * @return
     */
    public static ClassDef getClassTraitInSameNamespace(String traitName, ClassDef useClass) {
        ClassDef ret = null;

        int count = 0;

        String namespace = useClass.getNameSpace();
        String fullName = namespace + "\\" + traitName;

        for (ClassDef classDefTrait: classDefUseTrait) {
            String NamewithNS = classDefTrait.getNamewithNS();
            if (NamewithNS.contains(traitName) && fullName.equals(NamewithNS)) {
                ret = classDefTrait;
                count ++;
            }
        }

        if (count != 1) {
            if (count >= 2)
                traitAmbiguousFlag = true;
            else if (count == 0)
                traitLackedFlag ++;

            ret = null;
        }

        return ret;
    }


    public static ClassDef ClassDefTraitByInclude(Long fileid, String code) {

        ClassDef ret = null;

        int count = 0;

        HashSet<Long> targetIncludeFileIds = PHPIncludeMapFactory.getIncludeFilesSet(fileid);

        if (targetIncludeFileIds.size() == 1) {
            traitLackedFlag ++;
            return null;
        }

        for (Long targetFileId : targetIncludeFileIds) {
            // System.out.println(fileId);
            for (ClassDef classTrait : classDefUseTrait) {
                Long classTraitFileId = classTrait.getFileId();
                if (!classTraitFileId.equals(targetFileId))
                    continue;
                String fullName = classTrait.getNamewithNS();
                if (code.equals(fullName)) {
                    ret = classTrait;
                    count ++;
                }
            }
        }

        // Still get ambiguous Trait
        if (count != 1) {
            if (count >= 2)
                traitAmbiguousFlag = true;
            else if (count == 0)
                traitLackedFlag ++;

            ret = null;
        }

        return ret;
    }

//    public static ClassDef getClassDef(String className) {
//
//        ClassDef ret = null;
//
//        for (ClassDef classDef: classDefs) {
//            String name = classDef.getNamewithNS();
//            if (name.equals(className)) {
//                return classDef;
//            }
//        }
//
//        return ret;
//    }

    public static ClassDef getClassDef(long classid)
    {
        ClassDef ret = null;
        ret = classIdNameMap.get(classid);
        return ret;
    }

    /**
     *
     * @param ig
     */
    public static void createTraitEdges(IG ig) {
        // Count the number when use trait happens
        long useTraitNum = 0;
        // AST_CLASS(flags=CLASS_TRAIT) ASTNode
        long traitNodeNum = classDefUseTrait.size();

        long successfullyBuilt = 0;

        try {
            for (UseTrait useTrait: useTraits) {
                // System.out.println("Now visiting CLASS_TRAIT Node : " + useTrait.getEnclosingClass() + " ( " + useTrait.getNamewithNS() + " )");
                // String className = useTrait.getNamewithNS();

                long classid = useTrait.getClassid();
                // Class which uses Trait
                ClassDef classCommon = getClassDef(classid);
                 Long fileid = classCommon.getFileId();

                int childCount = useTrait.getChildCount();

                for (int i = 0; i < childCount; i++) {
                    ASTNode childNode = useTrait.getChild(i);

                    if (childNode instanceof TraitAdaptations)
                        continue;
                    else if (childNode instanceof IdentifierList) {
                        // [ IdentifierList ] ==> [ Identifier ] ==> [ StringExpression ]
                        int idfCount = childNode.getChildCount();

                        if (0 == idfCount)
                            throw new ArrayIndexOutOfBoundsException("Identifier Node should have at least one child");
                        for (int k = 0; k < idfCount; k++) {
                            traitLackedFlag = 0;
                            traitAmbiguousFlag = false;
                            useTraitNum ++;

                            Identifier identifier = (Identifier) childNode.getChild(k);
                            StringExpression stringExpression = (StringExpression) identifier.getChild(0);
                            String traitName = stringExpression.getEscapedCodeStr();

                            // type = AST_CLASS, flags = CLASS_TRAIT
                            ClassDef classTrait = getClassTraitInSameNamespace(traitName, classCommon);

                            if (classTrait == null)
                                classTrait = getClassTraitByNamespace(traitName);

                            if (classTrait == null) {
                                classTrait = ClassDefTraitByInclude(fileid, traitName);
                            }

                            if (classTrait != null) {
                                addInheritTraitEdges(ig, classCommon, classTrait);
                                successfullyBuilt ++;


                                //System.out.println("Class : " + classCommon.getName() + " ( " + classCommon.getNamewithNS() +
                                //        " ) --> Trait : " + traitName);
                            } else {
                                if (traitLackedFlag == 3) {
                                    traitLacked++;
                                    baseLackedTraitPairs.put(classCommon, traitName);
                                }
                                if (traitAmbiguousFlag) {
                                    ambiguousTraitNodes.put(traitName, classCommon);
                                    traitAmbiguous++;
                                }

                                System.out.println("Cannot find definition node for Trait " + traitName);
                            }
                        }
                    } else if (childNode instanceof NullNode) {

                    } else {
                        throw new IllegalArgumentException("Wrong Type! ASTNode UseTrait's children type should be IdentifierList.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Some errors has occurred while adding the Trait Edges");
        }

        System.out.println();
        System.out.println("Summary Class Hierarchy Graph (Trait) construction");
        System.out.println("----------------------------------------");
        System.out.println("Total " + traitNodeNum + " Node(s) in whole files.");
        System.out.println("Total " +useTraitNum + " time(s) used by classes." );
        System.out.println("Successfully connected: " + successfullyBuilt + " Edges");
        System.out.println(traitAmbiguous + " edges are ambiguous.");
        System.out.println(traitLacked + " edges lost due to lack of Base Definition node.");

        float mappedExtendsPercent = traitNodeNum == 0 ? 100 : ((float) successfullyBuilt / (float) traitNodeNum) * 100;
        System.out.println( "=> " + mappedExtendsPercent + " % " +
                "of TRAIT edges could be successfully connected.");
        float ambiguousPropotion = traitNodeNum == 0 ? 0 : ((float) traitAmbiguous / (float) traitNodeNum) * 100;
        System.out.println("Ambiguous not handled: " + ambiguousPropotion + " %.");
        float lackedProption = traitNodeNum == 0 ? 0 : ((float) traitLacked / (float) traitNodeNum) * 100;
        System.out.println("Lacked Trait Definition not handled: " + lackedProption + "%.");

        addFakeEdgesToCHG(ig, baseLackedTraitPairs, FakeTraitEdge.getEdgeType());
    }


    /**
     * Write Inherit Edges to CSV file
     * @param ig
     */
    public static void writeInheritEdges(IG ig) {
        for (InheritExtendsEdge ieEdge : ig.getEdges()) {
            ClassDef src = (ClassDef) ieEdge.getSource().getAstNode();
            Long srcId = src.getNodeId();

            ClassDef dest = (ClassDef) ieEdge.getDestination().getAstNode();
            Long destId = dest.getNodeId();

            if (ieEdge instanceof InheritImplementsEdge) {
                // ieEdge = (InheritImplementsEdge) ieEdge;
                Writer.addEdge(srcId, destId, null, ((InheritImplementsEdge) ieEdge).getLabel());
                // System.out.println("InheritImplementsEdges");
            } else {
                Writer.addEdge(srcId, destId, null, ieEdge.getLabel());
                // System.out.print("InheritExntendsEdges");
            }
        }
    }


    /**
     * Connect EXTENDS or IMPLEMENTS edges in same namespace
     * @param ig
     * @param code Base Class's classname (without namespace)
     * @param childClass
     * @param edgeType
     * @return
     */
    public static long connectEdgeInSameNameSpace(IG ig, String code, ClassDef childClass, String edgeType)
    {
        long successfullyBuilt = 0;

        String namespace = childClass.getNameSpace();
        String fullName = namespace + "\\" + code;

        for (ClassDef classDefItem : classDefs)
        {
            String nameWithNS = classDefItem.getNamewithNS();
            if (nameWithNS.contains(code) && fullName.equals(nameWithNS))
            {
                if (edgeType.equals("EXTENDS")) {
                    addInheritExtendsEdge(ig, childClass, classDefItem);
                    successfullyBuilt ++;
                }
                else if (edgeType.equals("IMPLEMENTS")){
                    addInheritImplementsEdges(ig, childClass, classDefItem);
                    successfullyBuilt ++;
                }
            }
        }

        return successfullyBuilt;
    }

    /**
     * Connect EXTENDS or IMPLEMENTS edges in different namespace
     * @param ig
     * @param code  Base Class's name (with namespace)
     * @param childClass
     * @param edgeType
     * @return
     */
    public static long connectEdgeByNameSpace(IG ig, String code, ClassDef childClass, String edgeType)
    {
        long successfullyBuilt = 0;

        for (ClassDef classDefItem : classDefs)
        {
            String nameWithNS = classDefItem.getNamewithNS();

            if (code.equals(nameWithNS))
            {
                if (edgeType.equals("EXTENDS"))
                {
                    addInheritExtendsEdge(ig, childClass, classDefItem);
                    successfullyBuilt ++;
                }
                else if (edgeType.equals("IMPLEMENTS"))
                {
                    addInheritImplementsEdges(ig, childClass, classDefItem);
                    successfullyBuilt ++;
                }
            }
        }

        return successfullyBuilt;
    }


    /**
     * Connect EXTENDS or IMPLEMENTS edges by Include Map
     * @param ig
     * @param code Base Class's classname
     * @param childClass
     * @param edgeType edge types, EXTENDS or IMPLEMENTS
     * @return
     */
    public static long connectEdgeByInclude(IG ig, String code, ClassDef childClass, String edgeType)
    {
        long successfullyBuilt = 0;

        Long childFileId = childClass.getFileId();

        HashSet<Long> targetIncludeFiles = PHPIncludeMapFactory.getIncludeFilesSet(childFileId);

        // No include graph for childFileid, return null
        if (targetIncludeFiles == null || targetIncludeFiles.size() == 1)
            return 0;

        for (Long targetFileId : targetIncludeFiles)
        {
            for (ClassDef classDef : classDefs)
            {
                Long candidateFileId = classDef.getFileId();
                if (candidateFileId.equals(targetFileId) == false)
                    continue;
                String nameWithNS = classDef.getNamewithNS();
                if (code.equals(nameWithNS))
                {
                    if (edgeType.equals("EXTENDS"))
                        addInheritExtendsEdge(ig, childClass, classDef);
                    else if (edgeType.equals("IMPLEMENTS"))
                        addInheritImplementsEdges(ig, childClass, classDef);
                }
            }
        }

        return successfullyBuilt;
    }


    /**
     * Create EXTENDS or IMPLEMENTS edges for ambiguous Base Classes
     * @param ig
     */
    public static void createInheritAmbiguousEdges(IG ig)
    {
        long connectedEdges = 0;

        for (ClassDef ambiguousClass : ambiguousClassExtendsNodes.keySet())
        {
            String code = ambiguousClassExtendsNodes.get(ambiguousClass);
            connectedEdges += connectEdgeInSameNameSpace(ig, code, ambiguousClass, "EXTENDS");

            connectedEdges += connectEdgeByNameSpace(ig, code, ambiguousClass, "EXTENDS");

            connectedEdges += connectEdgeByInclude(ig, code, ambiguousClass, "EXTENDS");
        }

        for (ClassDef ambiguousClass : ambiguousClassImplementsNodes.keySet())
        {
            String code = ambiguousClassImplementsNodes.get(ambiguousClass);
            connectedEdges += connectEdgeInSameNameSpace(ig, code, ambiguousClass, "IMPLEMENTS");

            connectedEdges += connectEdgeByNameSpace(ig, code, ambiguousClass, "IMPLEMENTS");

            connectedEdges += connectEdgeByInclude(ig, code, ambiguousClass, "IMPLEMENTS");
        }

        System.out.println("Successfully connected " + connectedEdges + " ambiguous Extends/Implements Edges.");
    }


    /**
     * Connect Trait Edges in same namespace
     * @param ig
     * @param traitName
     * @param useClass Class(type=AST_CLASS) node which use the Trait
     * @return successfully connected edges number
     */
    public static long connectTraitEdgeInSameNameSpace(IG ig, String traitName, ClassDef useClass)
    {
        long successfullyBuilt = 0;

        String namespace = useClass.getNameSpace();
        String fullName = namespace + "\\" + traitName;

        for (ClassDef classDefTrait : classDefUseTrait)
        {
            String nameWithNS = classDefTrait.getNamewithNS();
            if (nameWithNS.contains(traitName) && fullName.equals(nameWithNS))
            {
                addInheritTraitEdges(ig, useClass, classDefTrait);
                successfullyBuilt ++;
            }
        }

        return successfullyBuilt;
    }

    /**
     * Connect Trait Edges In different namespace
     * @param ig
     * @param traitName
     * @param useClass Class(type=AST_CLASS) node which use the Trait
     * @return successfully connected edges number
     */
    public static long connectTraitEdgeByNameSpace(IG ig, String traitName, ClassDef useClass)
    {
        long successfullyBuilt = 0;

        for (ClassDef classDefTrait : classDefUseTrait)
        {
            String name = classDefTrait.getNamewithNS();
            if (name.equals(traitName))
            {
                addInheritTraitEdges(ig, useClass, classDefTrait);
                successfullyBuilt ++;
            }
        }

        return successfullyBuilt;
    }

    /**
     * Connect Trait Edges through Include Map
     * @param ig
     * @param traitName
     * @param useClass Class(type=AST_CLASS) node which use the Trait
     * @return successfully connected edges number
     */
    public static long connectTraitEdgeByInclude(IG ig, String traitName, ClassDef useClass)
    {
        long successfullyBuilt = 0;

        Long fileid = useClass.getFileId();

        HashSet<Long> targetIncludeFiles = PHPIncludeMapFactory.getIncludeFilesSet(fileid);

        for (Long targetFileId : targetIncludeFiles)
        {
            for (ClassDef classTrait : classDefUseTrait)
            {
                Long classTraitFileId = classTrait.getFileId();
                if (classTraitFileId.equals(targetFileId) == false)
                    continue;
                String fullName = classTrait.getNamewithNS();
                if (traitName.equals(fullName))
                {
                    addInheritTraitEdges(ig, useClass, classTrait);
                    successfullyBuilt ++;
                }
            }
        }


        return successfullyBuilt;
    }

    /**
     * create TRAIT edges for ambiguous trait nodes(type=AST_CLASS, flags=CLASS_TRAIT)
     * when we come across a trait name(String), it may has ambiguous TRAIT definitions.
     * @param ig
     */
    public static void createInheritAmbiguousTraitEdges(IG ig)
    {
        long connectedEdges = 0;

        for (String traitName : ambiguousTraitNodes.keySet())
        {
            ClassDef useClass = ambiguousTraitNodes.get(traitName);
            connectedEdges += connectTraitEdgeInSameNameSpace(ig, traitName, useClass);

            connectedEdges += connectTraitEdgeByNameSpace(ig, traitName, useClass);

            connectedEdges += connectTraitEdgeByInclude(ig, traitName, useClass);
        }

        System.out.println("Successfully connected " + connectedEdges + " ambiguous Trait Edges.");
    }

    private static void addFakeEdgesToCHG(IG ig, HashMap<ClassDef, String> baseLackedPairs, String edgeType)
    {
        // solve FAKE extends/implements edges
        for (ClassDef subclass : baseLackedPairs.keySet())
        {
            FakeClassCreator fakeClassCreator = new FakeClassCreator();
            String classname = baseLackedPairs.get(subclass);
            // the fake class gets the same funcid as subclass(Child Class)
            String funcid = subclass.getProperty("funcid");
            // the fake class gets the same fileid as subclass(Child Class)
            Long fileid = subclass.getFileId();

            CodeLocation codeloc = subclass.getLocation();
            // get lineno
            String lineno = Long.toString(codeloc.startLine);
            // get endlineno
            String endlineno = Long.toString(codeloc.endLine);

            String flags = "";

            if (edgeType.equals(FakeExtendsEdge.getEdgeType()))
                flags = FakeClassCreator.getClassClassFlags();
            else if (edgeType.equals(FakeImplementsEdge.getEdgeType()))
                flags = FakeClassCreator.getClassInterfaceFlags();
            else if (edgeType.equals(FakeTraitEdge.getEdgeType()))
                flags = FakeClassCreator.getClassTraitFlags();

            // create fake base class
            ClassDef fakeClassDef = fakeClassCreator.createClassDefNode(classname, flags, funcid, fileid, lineno, endlineno);

            addFakeCHGEdge(ig, subclass, fakeClassDef, edgeType);

        }
    }
}