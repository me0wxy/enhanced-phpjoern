package outputModules.csv.exporters;

import inputModules.csv.KeyedCSV.KeyedCSVRow;
import inputModules.csv.PHPCSVEdgeTypes;
import inputModules.csv.PHPCSVNodeTypes;
import inputModules.csv.csvFuncExtractor.CSVFixExtractor;
import outputModules.common.Writer;

public class NodesCSVFixExporter
{

    public void addReorderNodes() {

        while ( !CSVFixExtractor.nodesQueue.isEmpty()) {
            KeyedCSVRow currKeyedCSVRow = CSVFixExtractor.nodesQueue.poll();
            Writer.appendNodes(
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.NODE_ID),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.LABEL),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.TYPE),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.FLAGS),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.LINENO),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.CODE),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.CHILDNUM),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.FUNCID),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.CLASSNAME),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.NAMESPACE),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.ENDLINENO),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.NAME),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.DOCCOMMENT),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.FILEID),
                    currKeyedCSVRow.getFieldForKey(PHPCSVNodeTypes.CLASSID)
            );
        }
    }
}
