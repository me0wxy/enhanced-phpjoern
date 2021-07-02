package ast.php.expressions;

import ast.expressions.Expression;
import ast.php.functionDef.Parameter;

/**
 * An arm of a match expression of the form `cond => expr` (children: cond, expr)
 */
public class MatchARMExpression extends Expression
{
    private Expression condition = null;
    private Expression expression = null;

    public Expression getCondition()
    {
        return this.condition;
    }

    public void setCondition(Expression condition)
    {
        this.condition = condition;
        super.addChild(condition);
    }

    public Expression getExpression()
    {
        return this.expression;
    }

    public void setExpression(Expression expression)
    {
        this.expression = expression;
        super.addChild(expression);
    }
}
