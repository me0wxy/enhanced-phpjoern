package ast.php.functionDef;

import ast.ASTNode;
import ast.expressions.Expression;
import ast.expressions.Identifier;
import ast.expressions.StringExpression;
import ast.functionDef.ParameterBase;
import ast.php.declarations.AttributeList;

public class Parameter extends ParameterBase
{
	private Identifier type = null;
	private StringExpression name = null;
	private Expression defaultvalue = null;
	// ast.so 80 2 new children nodes (attribute and docComment)
	private AttributeList attributeList = null;
	private StringExpression docComment = null;

	@Override
	public Identifier getType()
	{
		return this.type;
	}
	
	public void setType(ASTNode type)
	{
		if( type instanceof Identifier)
			this.type = (Identifier)type;
		super.addChild(type);
	}
	
	@Override
	public String getName() {
		return getNameChild().getEscapedCodeStr();
	}
	
	public StringExpression getNameChild()
	{
		return this.name;
	}
	
	public void setNameChild(StringExpression name)
	{
		this.name = name;
		super.addChild(name);
	}
	
	public Expression getDefault()
	{
		return this.defaultvalue;
	}
	
	public void setDefault(Expression defaultvalue)
	{
		this.defaultvalue = defaultvalue;
		super.addChild(defaultvalue);
	}

	// ast.so 80 New Feature
	public AttributeList getAttributeList()
	{
		return this.attributeList;
	}

	public void setAttributeList(AttributeList attributeList)
	{
		this.attributeList = attributeList;
		super.addChild(attributeList);
	}

	public StringExpression getDocComment()
	{
		return this.docComment;
	}

	public void setDocComment(StringExpression docComment)
	{
		this.docComment = docComment;
		super.addChild(docComment);
	}
}
