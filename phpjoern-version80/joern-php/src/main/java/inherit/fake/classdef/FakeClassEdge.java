package inherit.fake.classdef;

import graphutils.Edge;
import inherit.InheritExtendsEdge;
import inherit.InheritNode;

public class FakeClassEdge extends InheritExtendsEdge {

    private static final String DEFAULT_LABEL = "FAKE_CHG_EDGE";

    public FakeClassEdge(InheritNode source, InheritNode destination) {
        super(source, destination);
    }

    public String getLabel() {
        return this.DEFAULT_LABEL;
    }

    @Override
    public String toString() {
        return getSource() + " == [" + DEFAULT_LABEL + "]==> " + getDestination();
    }
}
