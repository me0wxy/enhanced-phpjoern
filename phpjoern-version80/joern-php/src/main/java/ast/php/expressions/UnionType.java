package ast.php.expressions;

import ast.expressions.Identifier;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * UnionType(type=AST_TYPE_UNION) node can have multiple children nodes
 * So it's children nodes should be LinkedList
 */
public class UnionType extends Identifier implements Iterable<Identifier>
{
    private LinkedList<Identifier> types = new LinkedList<>();

    public int size()
    {
        return this.types.size();
    }

    public Identifier getUnionTypeElement(int i)
    {
        return this.types.get(i);
    }

    public void addUnionTypeElement(Identifier type)
    {
        this.types.add(type);
        super.addChild(type);
    }

    @Override
    public Iterator<Identifier> iterator()
    {
        return this.types.iterator();
    }
}
