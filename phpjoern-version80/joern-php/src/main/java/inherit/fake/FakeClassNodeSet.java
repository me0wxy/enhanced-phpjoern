package inherit.fake;

import ast.FakeNode;

import java.util.LinkedList;

/**
 * Record the new created FakeClassNode nodes
 */
public class FakeClassNodeSet {

    final String SEPARATOR = "\t";

    public static LinkedList<FakeNode> fakeClassNodes = new LinkedList<>();

    public static void addFakeClassNodes(FakeNode fakeNode)
    {
        fakeClassNodes.add(fakeNode);
    }

}
