package ast.php.expressions;

import ast.expressions.Identifier;

public class NullableType extends Identifier
{
    private TypeHint typeHint = null;

    public TypeHint getTypeHint()
    {
        return this.typeHint;
    }

    public void setTypeHint(TypeHint typeHint)
    {
        this.typeHint = typeHint;
        super.addChild(typeHint);
    }
}
