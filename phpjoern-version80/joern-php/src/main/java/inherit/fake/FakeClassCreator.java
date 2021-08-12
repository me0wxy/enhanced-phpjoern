package inherit.fake;

import ast.FakeNode;
import ast.NullNode;
import ast.expressions.IntegerExpression;
import ast.expressions.StringExpression;
import ast.logical.statements.CompoundStatement;
import ast.php.declarations.ClassDef;
import ast.php.functionDef.TopLevelFunctionDef;
import inputModules.csv.PHPCSVNodeTypes;
import inputModules.csv.csv2ast.ASTUnderConstruction;
import tools.php.ast2cpg.PHPCSVNodeInterpreter;

import java.util.HashMap;

/**
 * Fake ClassDef Node
 */
public class FakeClassCreator
{

    private ClassDef newNode;

    private static final ASTUnderConstruction ast = new ASTUnderConstruction();

    private static final String TYPE = "AST_CLASS";
    private static final String INTEGER_TYPE = "integer";
    private static final String NULL_TYPE = "NULL";
    private static final String STRING_TYPE = "string";

    private static final String LEAF_CHILDNUM = "0";

    private static final String CLASS_INTERFACE = "CLASS_INTERFACE";
    private static final String CLASS_TRAIT = "CLASS_TRAIT";
    private static final String CLASS_CLASS = "";   // class has no flags


    public static String getClassClassFlags()
    {
        return CLASS_CLASS;
    }

    public static String getClassInterfaceFlags()
    {
        return CLASS_INTERFACE;
    }

    public static String getClassTraitFlags()
    {
        return CLASS_TRAIT;
    }

    // maintains a file's a least stmt_node(ordered by id)
    private static HashMap<Long, CompoundStatement> fileLeastStmtPairs = new HashMap<>();


    // Create single class definition node -> ClassDef

    /**
     *
     * @param classname
     * @param flags
     * @return
     */
    public ClassDef createClassDefNode(String classname, String flags, String funcid, Long fileid, String lineno, String endlineno) {

        // ASTUnderConstruction ast = new ASTUnderConstruction();

        this.newNode = new ClassDef();

        // set type: whatever class, interface or trait, their types are always AST_CLASS
        this.newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), TYPE);
        // set labels: AST
        this.newNode.setProperty(PHPCSVNodeTypes.LABEL.getName(), PHPCSVNodeTypes.LABEL_AST);

        // set funcid
        this.newNode.setProperty(PHPCSVNodeTypes.FUNCID.getName(), funcid);

        // set fileid
        this.newNode.setFileId(fileid);

        // set flags:
        // class: no flags
        // interface: CLASS_INTERFACE
        // trait: CLASS_TRAIT
        if (flags.equals(CLASS_CLASS))
            this.newNode.setFlags(CLASS_CLASS);
        else if (flags.equals(CLASS_INTERFACE))
            this.newNode.setFlags(CLASS_INTERFACE);
        else if (flags.equals(CLASS_TRAIT))
            this.newNode.setFlags(CLASS_TRAIT);

        this.newNode.setName(classname);

        // add id counter
        PHPCSVNodeInterpreter.max_retval ++;
        // id = classid
        long id = PHPCSVNodeInterpreter.max_retval;
        Long classid = Long.parseLong(String.valueOf(id));

        ast.addNodeWithId(this.newNode, id);
        this.newNode.setNodeId(id);

        CompoundStatement parentStmtNode = fileLeastStmtPairs.get(fileid);
        FakeParentOfEdges.addFakeEdges(parentStmtNode.getNodeId(), id);

        // add newNode's information to FakeClassNode
        FakeNode fakeNode = new FakeNode(
                Long.toString(id),  // id
                this.newNode.getProperty(PHPCSVNodeTypes.LABEL.getName()),  // labels
                this.newNode.getProperty(PHPCSVNodeTypes.TYPE.getName()),   // type
                this.newNode.getFlags(),    // flags
                lineno, // lineno
                "", // code
                LEAF_CHILDNUM, // childnum
                this.newNode.getProperty(PHPCSVNodeTypes.FUNCID.getName()), // funcid
                "", // classname
                "", // namespace
                endlineno,
                this.newNode.getName(),
                "",
                Long.toString(fileid),
                Long.toString(classid)
        );

        FakeClassNodeSet.addFakeClassNodes(fakeNode);

        createChildNode(funcid, fileid, classid, lineno, endlineno);
        //System.out.println(FakeClassNodes.keys);

        return this.newNode;
    }

    /**
     * ClassDef(AST_CLASS) node has 6 children nodes
     *
     */
    private void createChildNode(String funcid, Long fileid, Long classid, String lineno, String endlineno)
    {
        long childnum = 0;

        // name child
        StringExpression nameEndNode = createNameChild(this.newNode.getName(), funcid, fileid, classid, lineno, childnum ++);
        this.newNode.setClassname(nameEndNode);
        FakeParentOfEdges.addFakeEdges(this.newNode.getNodeId(), nameEndNode.getNodeId());

        // docComment child
        NullNode docCommentEndNode = createNullChild(funcid, fileid, classid, lineno, childnum ++);
        this.newNode.addChild(docCommentEndNode);
        FakeParentOfEdges.addFakeEdges(this.newNode.getNodeId(), docCommentEndNode.getNodeId());

        // extends child: NullNode
        NullNode extendsEndNode = createNullChild(funcid, fileid, classid, lineno, childnum ++);
        this.newNode.addChild(extendsEndNode);
        FakeParentOfEdges.addFakeEdges(this.newNode.getNodeId(), extendsEndNode.getNodeId());

        // implements child: NullNode
        NullNode implementsEndNode = createNullChild(funcid, fileid, classid, lineno, childnum ++);
        this.newNode.addChild(implementsEndNode);
        FakeParentOfEdges.addFakeEdges(this.newNode.getNodeId(), implementsEndNode.getNodeId());

        // toplevel child: TopLevelFunctionDef
        TopLevelFunctionDef stmtEndNode = createTopLevelFunctionDefChild(funcid, fileid, classid, lineno, endlineno, childnum ++);
        this.newNode.addChild(stmtEndNode);
        FakeParentOfEdges.addFakeEdges(this.newNode.getNodeId(), stmtEndNode.getNodeId());

        // attributes child: NullNode
        NullNode attributes = createNullChild(funcid, fileid, classid, lineno, childnum ++);
        this.newNode.addChild(attributes);
        FakeParentOfEdges.addFakeEdges(this.newNode.getNodeId(), attributes.getNodeId());

        // integer child: We don't care integer child
        IntegerExpression integerExpression = createIntegerChild(funcid, fileid, classid, lineno, childnum ++);
        this.newNode.setOffset(integerExpression);
        FakeParentOfEdges.addFakeEdges(this.newNode.getNodeId(), integerExpression.getNodeId());

    }

    private StringExpression createNameChild(String classname, String funcid, Long fileid, Long classid, String lineno, long childnum)
    {

        StringExpression newNode = new StringExpression();

        newNode.setCodeStr(classname);
        newNode.setProperty(PHPCSVNodeTypes.LABEL.getName(), PHPCSVNodeTypes.LABEL_AST);
        newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), STRING_TYPE);
        newNode.setProperty(PHPCSVNodeTypes.FUNCID.getName(), funcid);
        newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), LEAF_CHILDNUM);
        newNode.setFileId(fileid);

        // add id counter
        PHPCSVNodeInterpreter.max_retval ++;
        long id = PHPCSVNodeInterpreter.max_retval;
        ast.addNodeWithId(newNode, id);
        newNode.setNodeId(id);

        // add newNode's information to FakeClassNode
        FakeNode fakeNode = new FakeNode(
                Long.toString(id),  // id
                newNode.getProperty(PHPCSVNodeTypes.LABEL.getName()),  // labels
                newNode.getProperty(PHPCSVNodeTypes.TYPE.getName()),   // type
                "",    // flags
                lineno, // lineno
                newNode.getEscapedCodeStr(), // code
                Long.toString(childnum), // childnum
                funcid, // funcid
                "", // classname
                "", // namespace
                "",
                classname,
                "",
                Long.toString(fileid),
                Long.toString(classid)
        );

        FakeClassNodeSet.addFakeClassNodes(fakeNode);

        return newNode;
    }

    private TopLevelFunctionDef createTopLevelFunctionDefChild(String funcid, Long fileid, Long classid, String lineno, String endlineno, long childnum)
    {
        // ASTUnderConstruction ast = new ASTUnderConstruction();

        TopLevelFunctionDef newNode = new TopLevelFunctionDef();

        newNode.setProperty(PHPCSVNodeTypes.LABEL.getName(), PHPCSVNodeTypes.LABEL_AST);
        newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), PHPCSVNodeTypes.TYPE_TOPLEVEL);
        newNode.setFlags(PHPCSVNodeTypes.FLAG_TOPLEVEL_CLASS);
        newNode.setProperty(PHPCSVNodeTypes.FUNCID.getName(), funcid);
        newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), LEAF_CHILDNUM);
        newNode.setFileId(fileid);

        PHPCSVNodeInterpreter.max_retval ++;
        long id = PHPCSVNodeInterpreter.max_retval;
        ast.addNodeWithId(newNode, id);
        newNode.setNodeId(id);

        // add newNode's information to FakeClassNode
        FakeNode fakeNode = new FakeNode(
                Long.toString(id),  // id
                newNode.getProperty(PHPCSVNodeTypes.LABEL.getName()),  // labels
                newNode.getProperty(PHPCSVNodeTypes.TYPE.getName()),   // type
                newNode.getFlags(),    // flags
                lineno, // lineno
                "", // code
                Long.toString(childnum), // childnum
                funcid, // funcid
                "", // classname
                "", // namespace
                endlineno,
                "",
                "",
                Long.toString(fileid),
                Long.toString(classid)
        );

        FakeClassNodeSet.addFakeClassNodes(fakeNode);

        return newNode;
    }

    private NullNode createNullChild(String funcid, Long fileid, Long classid, String lineno, long childnum)
    {
        // ASTUnderConstruction ast = new ASTUnderConstruction();

        NullNode newNode = new NullNode();

        newNode.setProperty(PHPCSVNodeTypes.LABEL.getName(), PHPCSVNodeTypes.LABEL_AST);
        newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), NULL_TYPE);
        newNode.setProperty(PHPCSVNodeTypes.FUNCID.getName(), funcid);
        newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), LEAF_CHILDNUM);
        newNode.setFileId(fileid);

        PHPCSVNodeInterpreter.max_retval ++;
        long id = PHPCSVNodeInterpreter.max_retval;
        ast.addNodeWithId(newNode, id);
        newNode.setNodeId(id);

        // add newNode's information to FakeClassNode
        FakeNode fakeNode = new FakeNode(
                Long.toString(id),  // id
                newNode.getProperty(PHPCSVNodeTypes.LABEL.getName()),  // labels
                newNode.getProperty(PHPCSVNodeTypes.TYPE.getName()),   // type
                "",    // flags
                lineno, // lineno
                "", // code
                Long.toString(childnum), // childnum
                funcid, // funcid
                "", // classname
                "", // namespace
                "",
                "",
                "",
                Long.toString(fileid),
                Long.toString(classid)
        );

        FakeClassNodeSet.addFakeClassNodes(fakeNode);

        return newNode;
    }

    private IntegerExpression createIntegerChild(String funcid, Long fileid, Long classid, String lineno, long childnum)
    {
        IntegerExpression newNode = new IntegerExpression();

        String code = "1";

        newNode.setProperty(PHPCSVNodeTypes.LABEL.getName(), PHPCSVNodeTypes.LABEL_AST);
        newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), INTEGER_TYPE);
        newNode.setProperty(PHPCSVNodeTypes.FUNCID.getName(), funcid);
        newNode.setCodeStr(code);
        newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), LEAF_CHILDNUM);
        newNode.setFileId(fileid);

        PHPCSVNodeInterpreter.max_retval ++;
        long id = PHPCSVNodeInterpreter.max_retval;
        ast.addNodeWithId(newNode, id);
        newNode.setNodeId(id);

        // add newNode's information to FakeClassNode
        FakeNode fakeNode = new FakeNode(
                Long.toString(id),  // id
                newNode.getProperty(PHPCSVNodeTypes.LABEL.getName()),  // labels
                newNode.getProperty(PHPCSVNodeTypes.TYPE.getName()),   // type
                "",    // flags
                lineno, // lineno
                "", // code
                Long.toString(childnum), // childnum
                funcid, // funcid
                "", // classname
                "", // namespace
                "",
                "",
                "",
                Long.toString(fileid),
                Long.toString(classid)
        );

        FakeClassNodeSet.addFakeClassNodes(fakeNode);

        return newNode;
    }

    public static void updateFileLeastStmtPairs(Long fileid, CompoundStatement stmtNode)
    {
        if ( !fileLeastStmtPairs.containsKey(fileid))
            fileLeastStmtPairs.put(fileid, stmtNode);
        else {
            CompoundStatement oldStmtNode = fileLeastStmtPairs.get(fileid);
            long oldStmtId = oldStmtNode.getNodeId();

            long newStmtId = stmtNode.getNodeId();

            if (newStmtId < oldStmtId)
                fileLeastStmtPairs.put(fileid, stmtNode);
        }
    }
}
