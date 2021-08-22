package inputModules.csv.csvFuncExtractor;

import ast.php.functionDef.Parameter;
import inputModules.csv.KeyedCSV.KeyedCSVRow;
import inputModules.csv.KeyedCSV.exceptions.InvalidCSVFile;
import inputModules.csv.PHPCSVEdgeTypes;
import inputModules.csv.PHPCSVNodeTypes;
import inputModules.csv.csv2ast.CSVAST;
import inputModules.csv.helper.NodesComparator;
import misc.MultiHashMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class CSVFixExtractor extends CSVFunctionExtractor
{
    public static Queue<KeyedCSVRow> nodesQueue = new PriorityQueue<>(new NodesComparator());

    // maintains the node that in the same files
    // Key: type=Filesystem, Value: nodes except from type=Filesystem
    HashMap<Long, Long> nodesMap = new HashMap<>();

    // key: start Value: edge
    public static HashMap<Long, KeyedCSVRow> fileOfEdgeMap = new HashMap<>();
    // maintains the Directory_OF edges
    public static LinkedList<KeyedCSVRow> directoryOfEdgeList = new LinkedList<>();

    public static MultiHashMap<Long, KeyedCSVRow> parentOfEdgeMap = new MultiHashMap<Long, KeyedCSVRow>();


    private void addNodeRows() throws InvalidCSVFile {

        // read the first line of nodes.csv
        KeyedCSVRow currNodeRow;

        while ( nodeReader.hasNextRow()) {
            currNodeRow = nodeReader.getNextRow();
            if (currNodeRow.getFieldForKey(PHPCSVNodeTypes.LABEL).equals(PHPCSVNodeTypes.TYPE_FILESYSTEM)) {
                String fileid = currNodeRow.getFieldForKey(PHPCSVNodeTypes.NODE_ID);
                currNodeRow.setFieldForKey(PHPCSVNodeTypes.FILEID, fileid);
            }
            nodesQueue.add(currNodeRow);

            if ( !currNodeRow.getFieldForKey(PHPCSVNodeTypes.LABEL).equals(PHPCSVNodeTypes.TYPE_FILESYSTEM)) {
                Long fileid = Long.parseLong(currNodeRow.getFieldForKey(PHPCSVNodeTypes.FILEID));
                Long nodeid = Long.parseLong(currNodeRow.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
                nodesMap.put(nodeid, fileid);
            }
        }

//        while (!nodesQueue.isEmpty()) {
//            KeyedCSVRow top = nodesQueue.poll();
//            System.out.println(top.getFieldForKey(PHPCSVNodeTypes.FILEID));
//        }
    }

    private void addEdgeRows() throws InvalidCSVFile {

        // read the first line of rels.csv
        KeyedCSVRow currEdgeRow;

        while ( edgeReader.hasNextRow()) {
            currEdgeRow = edgeReader.getNextRow();
            Long start = Long.parseLong(currEdgeRow.getFieldForKey(PHPCSVEdgeTypes.START_ID));
            // end id is useless
            // Long end = Long.parseLong(currEdgeRow.getFieldForKey(PHPCSVEdgeTypes.END_ID));
            String edgeType = currEdgeRow.getFieldForKey(PHPCSVEdgeTypes.TYPE);

            if ( edgeType.equals(PHPCSVEdgeTypes.TYPE_FILE_OF)) {
                fileOfEdgeMap.put(start, currEdgeRow);
            }
            else if ( edgeType.equals(PHPCSVEdgeTypes.TYPE_DIRECTORY_OF)) {
                directoryOfEdgeList.add(currEdgeRow);
            } else {
                Long belongedFileid = nodesMap.get(start);
                parentOfEdgeMap.add(belongedFileid, currEdgeRow);
            }
        }
    }

    public void addCSVRows() throws InvalidCSVFile {

        addNodeRows();
        addEdgeRows();
    }

}
