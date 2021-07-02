package ast.php.statements;

import ast.expressions.Identifier;
import ast.logical.statements.Statement;
import ast.php.declarations.AttributeList;
import ast.php.expressions.TypeHint;
import org.w3c.dom.Attr;

public class PropertyGroup extends Statement
{
    // childnum : 0
    protected Identifier type = null;
    // childnum : 1
    protected PropertyDeclaration propertyDeclaration = null;
    // childnum : 2 ast.so 80
    protected AttributeList attributeList = null;

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

    public AttributeList getAttributeList()
    {
        return this.attributeList;
    }

    public void setAttributeList(AttributeList attributeList)
    {
        this.attributeList = attributeList;
        super.addChild(attributeList);
    }
}
