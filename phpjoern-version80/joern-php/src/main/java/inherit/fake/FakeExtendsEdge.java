package inherit.fake;

import graphutils.Edge;
import inherit.InheritExtendsEdge;
import inherit.InheritNode;

public class FakeExtendsEdge extends InheritExtendsEdge {

    private static final String DEFAULT_LABEL = "FAKE_EXTENDS";

    public FakeExtendsEdge(InheritNode source, InheritNode destination) {
        super(source, destination);
    }

    public String getLabel() {
        return this.DEFAULT_LABEL;
    }

    public static String getEdgeType()
    {
        return DEFAULT_LABEL;
    }

    @Override
    public String toString() {
        return getSource() + " == [" + DEFAULT_LABEL + "]==> " + getDestination();
    }
}
