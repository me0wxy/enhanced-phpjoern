package inherit;

public class InheritImplementsEdge extends InheritExtendsEdge {

    private static final String DEFAULT_LABEL = "IMPLEMENTS";

    public InheritImplementsEdge(InheritNode source, InheritNode destination) {
        super(source, destination);
    }

    public String getLabel() {
        return this.DEFAULT_LABEL;
    }

    @Override
    public String toString() {
        return super.getSource() + " ==[" + DEFAULT_LABEL + "]==> " + getDestination();
    }
}
