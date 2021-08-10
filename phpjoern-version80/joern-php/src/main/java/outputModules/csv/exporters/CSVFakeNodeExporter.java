package outputModules.csv.exporters;

import inherit.fake.classdef.FakeClassNode;
import inherit.fake.classdef.FakeClassNodeSet;
import outputModules.common.Writer;
import outputModules.csv.common.FakeNodeExporter;

public class CSVFakeNodeExporter extends FakeNodeExporter {

    public void addFakeNode(FakeClassNode fakeClassNode)
    {
        Writer.appendNodes(
                fakeClassNode.getFieldId(),
                fakeClassNode.getFieldLabels(),
                fakeClassNode.getFieldType(),
                fakeClassNode.getFieldFlags(),
                fakeClassNode.getFieldLineno(),
                fakeClassNode.getFieldCode(),
                fakeClassNode.getFieldChildnum(),
                fakeClassNode.getFieldFuncid(),
                fakeClassNode.getFieldClassname(),
                fakeClassNode.getFieldNamespace(),
                fakeClassNode.getFieldEndlineno(),
                fakeClassNode.getFieldName(),
                fakeClassNode.getFieldDoccomment(),
                fakeClassNode.getFieldFileid(),
                fakeClassNode.getFieldClassid()
        );
    }

    /**
     * Simple method that add fake nodes to nodes.csv
     */
    public void appendFakeNode()
    {
        for (FakeClassNode fakeClassNode : FakeClassNodeSet.fakeClassNodes)
        {
            addFakeNode(fakeClassNode);
        }
    }
}
