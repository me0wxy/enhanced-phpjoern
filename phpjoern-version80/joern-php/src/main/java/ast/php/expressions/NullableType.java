package ast.php.expressions;

import ast.expressions.Expression;
import ast.expressions.Identifier;

public class NullableType extends Identifier
{
    private Identifier type = null;

    public Identifier getType()
    {
        return this.type;
    }

    public void setType(Identifier type)
    {
        this.type = type;
        super.addChild(type);
    }
}
