package ast.php.expressions;

import ast.expressions.Expression;

/**
 * An element of an array literal. The key is `null` if there is no key (children: value, key)
 */
public class ArrayElement extends Expression
{
	private Expression value = null;
	private Expression key = null;

	public Expression getValue()
	{
		return this.value;
	}

	public void setValue(Expression value)
	{
		this.value = value;
		super.addChild(value);
	}
	
	public Expression getKey()
	{
		return this.key;
	}
	
	public void setKey(Expression key)
	{
		this.key = key;
		super.addChild(key);
	}
}
