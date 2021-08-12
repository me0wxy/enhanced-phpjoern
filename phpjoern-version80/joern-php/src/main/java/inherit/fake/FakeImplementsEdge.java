package inherit.fake;

import inherit.InheritExtendsEdge;
import inherit.InheritNode;

public class FakeImplementsEdge extends InheritExtendsEdge {

    private static final String DEFAULT_LABEL = "FAKE_IMPLEMENTS";

    public FakeImplementsEdge(InheritNode source, InheritNode destination) {
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