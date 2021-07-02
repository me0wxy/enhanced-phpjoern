package ast.php.declarations;

import ast.ASTNode;

import java.util.Iterator;
import java.util.LinkedList;

public class AttributeList extends ASTNode implements Iterable<AttributeGroup>
{
    private LinkedList<AttributeGroup> attributeGroup = new LinkedList<>();

    public int size()
    {
        return this.attributeGroup.size();
    }

    public AttributeGroup getAttributeGroup(int i)
    {
        return this.attributeGroup.get(i);
    }

    public void addAttributeGroup(AttributeGroup attributeGroup)
    {
        this.attributeGroup.add(attributeGroup);
        super.addChild(attributeGroup);
    }

    @Override
    public Iterator<AttributeGroup> iterator()
    {
        return this.attributeGroup.iterator();
    }
}
