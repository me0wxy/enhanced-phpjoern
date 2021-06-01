package ast.php.declarations;

import ast.ASTNodeProperties;
import ast.declarations.ClassDefStatement;
import ast.expressions.Identifier;
import ast.expressions.IdentifierList;
import ast.expressions.IntegerExpression;
import ast.expressions.StringExpression;
import ast.php.functionDef.TopLevelFunctionDef;

public class ClassDef extends ClassDefStatement
{

	private Identifier parent = null;
	private IdentifierList interfaces = null;
	private TopLevelFunctionDef toplevelfunc = null;
	private StringExpression classname = null;
	private StringExpression classDocComment = null;
	private IntegerExpression offset = null;
	private String NamewithNS = null;

	public StringExpression getClassname()
	{
		return this.classname;
	}

	public void setClassname(StringExpression classname)
	{
		this.classname = classname;
		super.addChild(classname);
	}

	public StringExpression getClassDocComment()
	{
		return this.classDocComment;
	}

	public void setClassDocComment(StringExpression classDocComment)
	{
		this.classDocComment = classDocComment;
		super.addChild(classDocComment);
	}

	public IntegerExpression getOffset()
	{
		return this.offset;
	}

	public void setOffset(IntegerExpression offset)
	{
		this.offset = offset;
		super.addChild(offset);
	}

	public String getName() {
		return getProperty(ASTNodeProperties.CODE);
	}

	public void setName(String name) {
		setProperty(ASTNodeProperties.CODE, name);
	}

	// Add namespace to FULL class name
	public void setNamewithNS(String namespace, String name) {
		// In case there is no namespace
		if ("".equals(namespace)) {
			this.NamewithNS = name;
		} else {
			this.NamewithNS = namespace + "\\" + name;
		}
	}

	public String getNamewithNS() {
		return this.NamewithNS;
	}

	public String getDocComment() {
		return getProperty(ASTNodeProperties.DOCCOMMENT);
	}

	public void setDocComment(String doccomment) {
		setProperty(ASTNodeProperties.DOCCOMMENT, doccomment);
	}

	public Identifier getExtends()
	{
		return this.parent;
	}

	public void setExtends(Identifier parent)
	{
		this.parent = parent;
		super.addChild(parent);
	}

	public IdentifierList getImplements()
	{
		return this.interfaces;
	}

	public void setImplements(IdentifierList interfaces)
	{
		this.interfaces = interfaces;
		super.addChild(interfaces);
	}

	public TopLevelFunctionDef getTopLevelFunc()
	{
		return this.toplevelfunc;
	}

	public void setTopLevelFunc(TopLevelFunctionDef toplevelfunc)
	{
		this.toplevelfunc = toplevelfunc;
		super.addChild(toplevelfunc);
	}
}
