package inherit.fake.classdef;

import java.util.LinkedList;

/**
 * Record the new created FakeClassNode nodes
 */
public class FakeClassNodeSet {

    final String SEPARATOR = "\t";

    public static LinkedList<FakeClassNode> fakeClassNodes = new LinkedList<>();

    public static void addFakeClassNodes(FakeClassNode fakeNode)
    {
        fakeClassNodes.add(fakeNode);
    }

}
