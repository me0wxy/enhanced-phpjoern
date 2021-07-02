package ast.php.declarations;

import ast.ASTNode;
import ast.walking.ASTNodeVisitor;

public abstract class AttributeBase extends ASTNode
{
    @Override
    public void accept(ASTNodeVisitor visitor)
    {
        visitor.visit(this);
    }
}
