package inherit.fake.classdef;

import ast.ASTNode;
import inherit.InheritNode;

public class FakeClassNode
{
    /**
     * nodes.csv Fields:
     * id:int
     * labels:label
     * type
     * flags:string_array
     * lineno:int
     * code
     * childnum:int
     * funcid:int
     * classname
     * namespace
     * endlineno:int
     * name
     * doccomment
     * fileid:int
     * classid:int
     * <p>
     * The most important one is id:int and type
     */

    private String field_id = null;
    private String field_labels = null;
    private String field_type = null;
    private String field_flags = null;
    private String field_lineno = null;
    private String field_code = null;
    private String field_childnum = null;
    private String field_funcid = null;
    private String field_classname = null;
    private String field_namespace = null;
    private String field_endlineno = null;
    private String field_name = null;
    private String field_doccomment = null;
    private String field_fileid = null;
    private String field_classid = null;

    FakeClassNode(String id, String labels, String type, String flags, String lineno, String code, String childnum,
                  String funcid, String classname, String namespace, String endlineno, String name,
                  String doccomment, String fileid, String classid)
    {
        // super();
        this.field_id = id;
        this.field_labels = labels;
        this.field_type = type;
        this.field_flags = flags;
        this.field_lineno = lineno;
        this.field_code = code;
        this.field_childnum = childnum;
        this.field_funcid = funcid;
        this.field_classname = classname;
        this.field_namespace = namespace;
        this.field_endlineno = endlineno;
        this.field_name = name;
        this.field_doccomment = doccomment;
        this.field_fileid = fileid;
        this.field_classid = classid;
    }

    FakeClassNode()
    {
        // super();
    }

    public String getFieldId()
    {
        return this.field_id;
    }

    public String getFieldLabels()
    {
        return this.field_labels;
    }

    public String getFieldType()
    {
        return this.field_type;
    }

    public String getFieldFlags()
    {
        return this.field_flags;
    }

    public String getFieldLineno()
    {
        return this.field_lineno;
    }

    public String getFieldCode()
    {
        return this.field_code;
    }

    public String getFieldChildnum()
    {
        return this.field_childnum;
    }

    public String getFieldFuncid()
    {
        return this.field_funcid;
    }

    public String getFieldClassname()
    {
        return this.field_classname;
    }

    public String getFieldNamespace()
    {
        return this.field_namespace;
    }

    public String getFieldEndlineno()
    {
        return this.field_endlineno;
    }

    public String getFieldName()
    {
        return this.field_name;
    }

    public String getFieldDoccomment()
    {
        return this.field_doccomment;
    }

    public String getFieldFileid()
    {
        return this.field_fileid;
    }

    public String getFieldClassid()
    {
        return this.field_classid;
    }
}
