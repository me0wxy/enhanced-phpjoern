package outputModules.csv.exporters;

import inputModules.csv.KeyedCSV.KeyedCSVRow;
import inputModules.csv.PHPCSVEdgeTypes;
import outputModules.common.Writer;

import java.util.Collection;
import java.util.List;

import static inputModules.csv.csvFuncExtractor.CSVFixExtractor.*;

public class EdgesCSVFixExporter
{

    public void addReorderEdges() {

        for (Long fileid : parentOfEdgeMap.keySet()) {
            // System.out.println(fileid);
            List<KeyedCSVRow> keyedCSVRows = parentOfEdgeMap.get(fileid);
            for (KeyedCSVRow currKeyedCSVRow : keyedCSVRows) {
                String edgeType = currKeyedCSVRow.getFieldForKey(PHPCSVEdgeTypes.TYPE);
                long start = Long.parseLong(currKeyedCSVRow.getFieldForKey(PHPCSVEdgeTypes.START_ID));
                long end = Long.parseLong(currKeyedCSVRow.getFieldForKey(PHPCSVEdgeTypes.END_ID));
                Writer.appendRels(start, end, edgeType);
            }
            // then write FILE_OF edges
            KeyedCSVRow fileOfKeyedCSVRow = fileOfEdgeMap.get(fileid);
            String edgeType = fileOfKeyedCSVRow.getFieldForKey(PHPCSVEdgeTypes.TYPE);
            long start = Long.parseLong(fileOfKeyedCSVRow.getFieldForKey(PHPCSVEdgeTypes.START_ID));
            long end = Long.parseLong(fileOfKeyedCSVRow.getFieldForKey(PHPCSVEdgeTypes.END_ID));
            Writer.appendRels(start, end, edgeType);
        }

        for (KeyedCSVRow currKeyedRow : directoryOfEdgeList) {
            String edgeType = currKeyedRow.getFieldForKey(PHPCSVEdgeTypes.TYPE);
            long start = Long.parseLong(currKeyedRow.getFieldForKey(PHPCSVEdgeTypes.START_ID));
            long end = Long.parseLong(currKeyedRow.getFieldForKey(PHPCSVEdgeTypes.END_ID));
            Writer.appendRels(start, end, edgeType);
        }
    }

}
