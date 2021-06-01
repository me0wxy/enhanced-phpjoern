package ast.expressions;

import ast.walking.ASTNodeVisitor;

public class CoalesceExpression extends BinaryExpression
{
    public void accept(ASTNodeVisitor visitor)
    {
        visitor.visit(this);
    }
}
