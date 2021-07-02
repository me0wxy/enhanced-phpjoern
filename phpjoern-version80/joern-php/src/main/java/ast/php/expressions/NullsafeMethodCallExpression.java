package ast.php.expressions;

import ast.expressions.ArgumentList;
import ast.expressions.Expression;

/**
 * A nullsafe method call of the form `expr?->method(args)`. (children: expr, method, args)
 */
public class NullsafeMethodCallExpression extends Expression
{
    private Expression leftExpression = null;
    private Expression method = null;
    private ArgumentList argumentList = null;

    public Expression getLeftExpression()
    {
        return this.leftExpression;
    }

    public void setLeftExpression(Expression leftExpression)
    {
        this.leftExpression = leftExpression;
        super.addChild(leftExpression);
    }

    public Expression getMethod()
    {
        return this.method;
    }

    public void setMethod(Expression method)
    {
        this.method = method;
        super.addChild(method);
    }

    public ArgumentList getArgumentList()
    {
        return this.argumentList;
    }

    public void setArgumentList(ArgumentList argumentList)
    {
        this.argumentList = argumentList;
        super.addChild(argumentList);
    }
}
