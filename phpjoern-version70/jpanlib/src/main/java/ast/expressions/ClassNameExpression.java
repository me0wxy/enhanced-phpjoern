package ast.expressions;

public class ClassNameExpression extends Expression
{

    Expression classExpression = null;

    public Expression getClassExpression()
    {
        return this.classExpression;
    }

    public void setClassExpression(Expression classExpression)
    {
        this.classExpression = classExpression;
        super.addChild(classExpression);
    }
}
