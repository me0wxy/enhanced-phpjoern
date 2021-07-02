package ast.php.expressions;

import java.util.Iterator;
import java.util.LinkedList;

import ast.expressions.Expression;

/**
 * Replace ArrayElement to Expression to fit ...$array syntax in PHP 7.4
 * ... => AST_UNPACK => UnpackExpression => UnaryExpression (which only gets 1 child)
 */
public class ArrayExpression extends Expression implements Iterable<Expression>
{

	private LinkedList<Expression> arrayElements = new LinkedList<Expression>();

	public int size()
	{
		return this.arrayElements.size();
	}
	
	public Expression getArrayElement(int i) {
		return this.arrayElements.get(i);
	}

	public void addArrayElement(Expression arrayElement)
	{
		this.arrayElements.add(arrayElement);
		super.addChild(arrayElement);
	}

	@Override
	public Iterator<Expression> iterator() {
		return this.arrayElements.iterator();
	}
}
