package ast.php.expressions;

import ast.expressions.Expression;
import ast.expressions.Identifier;
import ast.expressions.IntegerExpression;
import ast.expressions.StringExpression;
import ast.functionDef.ParameterList;
import ast.logical.statements.CompoundStatement;
import ast.logical.statements.Statement;
import ast.php.functionDef.Parameter;
import ast.statements.jump.ReturnStatement;

/**
 * https://www.php.net/manual/zh/functions.arrow.php
 * version 70 ( PHP 7.4 new feature )
 * AST_ARROW_FUNC:       name, docComment, params, stmts, returnType
 */
public class ArrowFuncExpression extends Expression
{
    private StringExpression name = null;
    private StringExpression docComment = null;
    private ParameterList parameterList = null;
    private ReturnStatement content = null;
    private Identifier returnType = null;
    private IntegerExpression offset = null;

    public StringExpression getName()
    {
        return this.name;
    }

    public void setName(StringExpression name)
    {
        this.name = name;
        super.addChild(name);
    }

    public StringExpression getDocComment()
    {
        return this.docComment;
    }

    public void setDocComment(StringExpression docComment)
    {
        this.docComment = docComment;
        super.addChild(docComment);
    }

    public ParameterList getParameterList()
    {
        return this.parameterList;
    }

    public void setParameterList(ParameterList parameterList)
    {
        this.parameterList = parameterList;
        super.addChild(parameterList);
    }

    public ReturnStatement getContent()
    {
        return this.content;
    }

    public void setContent(ReturnStatement content)
    {
        this.content = content;
        super.addChild(content);
    }

    public Identifier getReturnType()
    {
        return this.returnType;
    }

    public void setReturnType(Identifier returnType)
    {
        this.returnType = returnType;
        super.addChild(returnType);
    }

    public IntegerExpression getOffset()
    {
        return this.offset;
    }

    public void setOffset(IntegerExpression offset)
    {
        this.offset = offset;
        super.addChild(offset);
    }
}
