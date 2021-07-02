package ast.php.declarations;

import ast.expressions.ArgumentList;
import ast.expressions.Identifier;
import ast.statements.ExpressionHolder;

import java.util.LinkedList;

/**
 * Two children node: name(AST_NAME) and args(AST_ARG_LIST)
 */
public class Attribute extends AttributeBase
{
    private Identifier name = null;
    private ArgumentList argumentList = null;

    public Identifier getAttrName() {
        return this.name;
    }

    public void setAttrName(Identifier name)
    {
        this.name = name;
        super.addChild(name);
    }

    public ArgumentList getArgumentList()
    {
        return this.argumentList;
    }

    public void setArgumentList(ArgumentList argList)
    {
        this.argumentList = argList;
        super.addChild(argList);
    }
}
