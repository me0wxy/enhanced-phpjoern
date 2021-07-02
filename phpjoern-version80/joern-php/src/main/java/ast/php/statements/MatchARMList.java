package ast.php.statements;

import ast.ASTNode;
import ast.logical.statements.Statement;
import ast.php.expressions.MatchARMExpression;
import ast.walking.ASTNodeVisitor;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Numerically indexed children of the kind `ast\AST_MATCH_ARM` for the statements of a match expression
 */
public class MatchARMList extends Statement implements Iterable<MatchARMExpression>
{
    private LinkedList<MatchARMExpression>  matchARMExpressions = new LinkedList<>();

    public void addChild(ASTNode astNode)
    {
        if (astNode instanceof MatchARMExpression)
            addMatchARMExpression((MatchARMExpression)astNode);
        else
            super.addChild(astNode);
    }

    public int size()
    {
        return this.matchARMExpressions.size();
    }

    public MatchARMExpression getMatchARMExpression(int i)
    {
        return this.matchARMExpressions.get(i);
    }

    public void addMatchARMExpression(MatchARMExpression matchARMExpression)
    {
        this.matchARMExpressions.add(matchARMExpression);
        super.addChild(matchARMExpression);
    }

    @Override
    public void accept(ASTNodeVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public Iterator<MatchARMExpression> iterator() {
        return this.matchARMExpressions.iterator();
    }
}
