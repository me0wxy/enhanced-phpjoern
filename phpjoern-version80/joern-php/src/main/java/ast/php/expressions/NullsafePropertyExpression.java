package ast.php.expressions;

import ast.expressions.Expression;

/**
 * A nullsafe property read of the form `expr?->prop`. (children: expr, prop)
 */
public class NullsafePropertyExpression extends Expression
{
    private Expression leftExpression = null;
    private Expression property = null;

    public Expression getLeftExpression()
    {
        return this.leftExpression;
    }

    public void setLeftExpression(Expression leftExpression)
    {
        this.leftExpression = leftExpression;
        super.addChild(leftExpression);
    }

    public Expression getProperty()
    {
        return this.property;
    }

    public void setProperty(Expression property) {
        this.property = property;
        super.addChild(property);
    }
}
