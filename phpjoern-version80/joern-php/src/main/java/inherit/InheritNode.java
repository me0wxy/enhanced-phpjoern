package inherit;

import ast.ASTNode;
import ast.php.declarations.ClassDef;

import java.util.HashMap;

public class InheritNode {

    private ASTNode astNode;

    /**
     * Parent class
     */
    private boolean isParent;

    public InheritNode(ClassDef node) {
        init( node);
    }

    public InheritNode() {

    }

    private void init(ASTNode node) {
        if (null == node)
            throw new IllegalArgumentException( "Cannot construct a IGNode with a null node.");

        setASTNode( node);
    }

    private void setASTNode(ASTNode node) {
        this.astNode = node;
    }

    public ASTNode getAstNode() {
        return this.astNode;
    }

    @Override
    public int hashCode()
    {
        return getAstNode().hashCode();
    }

    @Override
    public String toString()
    {
        return getAstNode().toString();
    }
}
