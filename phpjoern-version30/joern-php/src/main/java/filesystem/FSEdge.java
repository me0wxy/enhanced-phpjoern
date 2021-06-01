package filesystem;


import graphutils.Edge;

public class FSEdge extends Edge<FSNode> {
    public static final String TYPE_DIRECTORY_OF = "DIRECTORY_OF";
    public static final String TYPE_INCLUDE = "INCLUDE";
    private String label;

    public FSEdge(FSNode source, FSNode destination, String label) {
        super(source, destination);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getSource() + " ==[" + getLabel() + "]==> " + getDestination();
    }

    @Override
    public FSEdge reverse(){
        return new FSEdge(getDestination(), getSource(), getLabel());
    }
}
