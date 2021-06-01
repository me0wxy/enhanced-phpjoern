package ast.php.statements;

import ast.logical.statements.Statement;
import ast.php.expressions.TypeHint;

public class PropertyGroup extends Statement
{
    // childnum : 0
    TypeHint typeHint = null;
    // childnum : 1
    PropertyDeclaration propertyDeclaration = null;

    public TypeHint getTypeHint()
    {
        return this.typeHint;
    }

    public void setTypeHint(TypeHint typeHint)
    {
        this.typeHint = typeHint;
        super.addChild(typeHint);
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
