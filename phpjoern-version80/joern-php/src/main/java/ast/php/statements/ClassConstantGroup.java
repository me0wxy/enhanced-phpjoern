package ast.php.statements;

import ast.logical.statements.Statement;
import ast.php.declarations.AttributeList;
import ast.php.functionDef.Parameter;

/**
 * AST_CLASS_CONST_GROUP nodes are emitted for class constant declarations wrapping the AST_CLASS_CONST_DECL and any attributes.
 * Previously, AST_CLASS_CONST_DECL would be emitted.
 */
public class ClassConstantGroup extends Statement
{
    protected ClassConstantDeclaration classConstantDeclaration = null;
    protected AttributeList attributeList = null;

    public ClassConstantDeclaration getClassConstantDeclaration()
    {
        return this.classConstantDeclaration;
    }

    public void setClassConstantDeclaration(ClassConstantDeclaration classConstantDeclaration)
    {
        this.classConstantDeclaration = classConstantDeclaration;
        super.addChild(classConstantDeclaration);
    }

    public AttributeList getAttributeList(AttributeList attributeList)
    {
        return this.attributeList;
    }

    public void setAttributeList(AttributeList attributeList)
    {
        this.attributeList = attributeList;
        super.addChild(attributeList);
    }
}
