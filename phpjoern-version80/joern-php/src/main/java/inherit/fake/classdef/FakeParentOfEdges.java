package inherit.fake.classdef;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Record the new Fake !!!! PARENT_OF !!!! edges for ClassDef Nodes(AST_CLASS node has 6 children nodes)
 */
public class FakeParentOfEdges
{

    public static LinkedList<FakeParentOfEdge> fakeParentOfEdges = new LinkedList<>();

    public static void addFakeEdges(long srcId, long destId)
    {
        fakeParentOfEdges.add(new FakeParentOfEdge(srcId, destId));
    }

}
