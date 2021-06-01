package inherit;

import graphutils.Edge;

public class InheritExtendsEdge extends Edge<InheritNode> {

    public static final String DEFAULT_LABEL = "EXTENDS";

    public InheritExtendsEdge(InheritNode source, InheritNode destination)
    {
        super(source, destination);
    }

    public String getLabel() {
        return this.DEFAULT_LABEL;
    }

    @Override
    public String toString() {
        return getSource() + " ==[" + DEFAULT_LABEL + "]==> " + getDestination();
    }
}
