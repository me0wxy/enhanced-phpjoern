package ast.php.statements;

import ast.expressions.Identifier;
import ast.logical.statements.Statement;
import ast.php.expressions.TypeHint;

public class PropertyGroup extends Statement
{
    // childnum : 0
    Identifier type = null;
    // childnum : 1
    PropertyDeclaration propertyDeclaration = null;

    public Identifier getType()
    {
        return this.type;
    }

    public void setType(Identifier type)
    {
        this.type = type;
        super.addChild(type);
    }

    public PropertyDeclaration getPropertyDeclaration()
    {
        return this.propertyDeclaration;
    }

    public void setPropertyDeclaration(PropertyDeclaration propertyDeclaration)
    {
        this.propertyDeclaration = propertyDeclaration;
        super.addChild(propertyDeclaration);
    }
}
