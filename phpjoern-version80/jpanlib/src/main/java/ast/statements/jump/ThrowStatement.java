package ast.statements.jump;

import ast.ASTNode;
import ast.expressions.Expression;
import ast.logical.statements.JumpStatement;
import ast.walking.ASTNodeVisitor;

/**
 * As of PHP 8.0.0, the throw keyword is an expression and may be used in any expression context.
 * In prior versions it was a statement and was required to be on its own line.
 * AST_THROW: Statement => Expression
 */
public class ThrowStatement extends Expression
{
	private Expression throwExpression = null;
	
	public Expression getThrowExpression()
	{
		return this.throwExpression;
	}

	public void setThrowExpression(Expression expression)
	{
		this.throwExpression = expression;
		super.addChild(expression);
	}
	
	public void accept(ASTNodeVisitor visitor)
	{
		visitor.visit(this);
	}
	
	@Override
	public void addChild(ASTNode node)
	{
		if (node instanceof Expression)
			setThrowExpression((Expression) node);
		else
			super.addChild(node);
	}
}
