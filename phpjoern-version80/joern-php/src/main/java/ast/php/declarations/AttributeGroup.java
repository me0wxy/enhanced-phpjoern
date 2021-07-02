package ast.php.declarations;

import ast.ASTNode;

import java.util.Iterator;
import java.util.LinkedList;

public class AttributeGroup extends ASTNode implements Iterable<Attribute>
{
    private LinkedList<Attribute> attribute = new LinkedList<>();

    public void addChild(ASTNode node)
    {
        if (node instanceof Attribute)
            addAttribute((Attribute)node);
        else
            super.addChild(node);
    }

    public int size()
    {
        return this.attribute.size();
    }

    public Attribute getAttribute(int i)
    {
        return this.attribute.get(i);
    }

    public void addAttribute(Attribute attribute)
    {
        this.attribute.add(attribute);
        super.addChild(attribute);
    }

    @Override
    public Iterator<Attribute> iterator()
    {
        return this.attribute.iterator();
    }
}
