package inherit;

import graphutils.Edge;

public class InheritTraitEdge extends InheritExtendsEdge {

    public static final String DEFAULT_LABEL = "TRAIT";

    public InheritTraitEdge(InheritNode source, InheritNode destination)
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
