package ast.php.expressions;

import ast.expressions.Expression;
import ast.logical.statements.Statement;
import ast.php.statements.MatchARMList;

/**
 * A match expression of the form `match(cond) { stmts }` (children: cond, stmts)
 */
public class MatchExpression extends Expression
{
    public Expression condition = null;
    public MatchARMList matchARMList = null;

    public Expression getCondition()
    {
        return this.condition;
    }

    public void setCondition(Expression condition)
    {
        this.condition = condition;
        super.addChild(condition);
    }

    public Statement getMatchARMList()
    {
        return this.matchARMList;
    }

    public void setMatchARMList(MatchARMList matchARMList)
    {
        this.matchARMList = matchARMList;
        super.addChild(matchARMList);
    }
}
