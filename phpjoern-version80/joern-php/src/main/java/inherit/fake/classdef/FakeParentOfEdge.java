package inherit.fake.classdef;

import graphutils.Edge;

public class FakeParentOfEdge{

    private long srcId;
    private long destId;

    FakeParentOfEdge(long srcId, long destId)
    {
        this.srcId = srcId;
        this.destId = destId;
    }

    FakeParentOfEdge() {

    }

    public long getSrcId() {
        return this.srcId;
    }

    public long getDestId() {
        return this.destId;
    }
}
