package ast.php.expressions;

import ast.expressions.Expression;
import ast.expressions.StringExpression;

public class NamedArgument extends Expression
{
    private StringExpression parameterName = null;
    private StringExpression argumentValue = null;

    public StringExpression getParameterName()
    {
        return this.parameterName;
    }

    public void setParameterName(StringExpression parameterName)
    {
        this.parameterName = parameterName;
        super.addChild(parameterName);
    }

    public StringExpression getArgumentValue()
    {
        return this.argumentValue;
    }

    public void setArgumentValue(StringExpression argumentValue)
    {
        this.argumentValue = argumentValue;
        super.addChild(argumentValue);
    }
}
