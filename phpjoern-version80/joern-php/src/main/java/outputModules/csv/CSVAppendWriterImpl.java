package outputModules.csv;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.util.Map;

public class CSVAppendWriterImpl extends CSVWriterImpl {

    PrintWriter relsWriter;
    PrintWriter fakeNodesWriter;

    private static String escape(String propValue)
    {
        return StringEscapeUtils.escapeCsv(propValue.replace("\\", "\\\\"));
    }


    @Override
    public void changeOutputDir(String dirNameForFileNode) {

    }

    @Override
    public void shutdown() {
        closeEdgeFile();
        closeNodeFile();
    }

    // Append PARENT_OF edges to rels.csv
    protected void openRelsFile(String outDir)
    {
        System.out.println(outDir);
        openEdgeFile(outDir, "rels.csv");
    }

    public void writeRels(long srcId, long dstId,
                          String edgeType)
    {
        // PARENT_OF edge also added by this function
        relsWriter.print(srcId);
        relsWriter.print(SEPARATOR);
        relsWriter.print(dstId);
        relsWriter.print(SEPARATOR);
        relsWriter.print(edgeType);

        relsWriter.write("\n");

    }

    @Override
    public void writeNodes(String id, String labels, String type, String flags, String lineno,
                           String code, String childnum, String funcid, String classname, String namespace,
                           String endlineno, String name, String doccomment, String fileid, String classid)
    {
        fakeNodesWriter.print(id);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(labels);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(type);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(flags);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(lineno);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(code);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(childnum);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(funcid);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(classname);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(namespace);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(endlineno);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(name);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(doccomment);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(fileid);
        fakeNodesWriter.print(SEPARATOR);
        fakeNodesWriter.print(classid);
        // fakeNodesWriter.print(SEPARATOR);

        fakeNodesWriter.write("\n");
    }

    public void openRelsFile(String outDir, String fileName)
    {
        String path = outDir + File.separator + fileName;
        relsWriter = createWriter(path);

    }

    public void openNodesFile(String outDir, String fileName)
    {
        String path = outDir + File.separator + fileName;
        fakeNodesWriter = createWriter(path);
    }

    protected PrintWriter createWriter(String path) {
        try
        {
            /**
             * FileWriter(path, true) : true means append to the end of the file
             */
            return new PrintWriter(new FileWriter(path, true));
        } catch (IOException e)
        {
            throw new RuntimeException("Cannot create PrintWriter: " + path);
        }
    }

    public void closeRelsFile()
    {
        if (relsWriter != null)
            relsWriter.close();
    }

    public void closeNodesFile()
    {
        if (fakeNodesWriter != null)
            fakeNodesWriter.close();
    }
}
