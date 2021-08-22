package inputModules.csv.helper;

import inputModules.csv.KeyedCSV.KeyedCSVRow;
import inputModules.csv.PHPCSVNodeTypes;

import java.util.Comparator;

public class NodesComparator implements Comparator<KeyedCSVRow>
{

    public int compare(KeyedCSVRow record1, KeyedCSVRow record2) {
        if ( record1.getFieldForKey(PHPCSVNodeTypes.FILEID).equals(record2.getFieldForKey(PHPCSVNodeTypes.FILEID))) {
            // If the KeyedCSVRows get the same fileid, compare them with id
            return (int) ((Long.parseLong(record1.getFieldForKey(PHPCSVNodeTypes.NODE_ID))) - (Long.parseLong(record2.getFieldForKey(PHPCSVNodeTypes.NODE_ID))));
        } else {
            // the the KeyedCSVRow with smaller fileid
            return (int) ((Long.parseLong(record1.getFieldForKey(PHPCSVNodeTypes.FILEID))) - (Long.parseLong(record2.getFieldForKey(PHPCSVNodeTypes.FILEID))));
        }
    }

}
