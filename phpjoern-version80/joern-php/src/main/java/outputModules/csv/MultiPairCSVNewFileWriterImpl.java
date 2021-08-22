package outputModules.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.SerializablePermission;

public class MultiPairCSVNewFileWriterImpl extends CSVWriterImpl
{
    PrintWriter nodeWriter;
    PrintWriter edgeWriter;

    final String SEPARATOR = "\t";

    public void createNewEdgeFile(String outDir, String fileName)
    {
        String path = outDir + File.separator + fileName;
        edgeWriter = createWriter(path);
        String joined = "start" + SEPARATOR + "end" + SEPARATOR + "type";
        edgeWriter.println(joined);
    }

    public void createNewNodeFile(String outDir, String fileName)
    {
        String path = outDir + File.separator + fileName;
        nodeWriter = createWriter(path);
        String joined = "id:int" + SEPARATOR + "labels:label" + SEPARATOR +
                "type" + SEPARATOR + "flags:string_array" + SEPARATOR +
                "lineno:int" + SEPARATOR + "code" + SEPARATOR +
                "childnum:int" + SEPARATOR + "funcid:int" + SEPARATOR +
                "classname" + SEPARATOR + "namespace" + SEPARATOR +
                "endlineno:int" + SEPARATOR + "name" + SEPARATOR +
                "doccomment" + SEPARATOR + "fileid:int" + SEPARATOR + "classid:int";
        nodeWriter.println(joined);

    }

    protected PrintWriter createWriter(String path)
    {
        try
        {
            return new PrintWriter(path);
        } catch (FileNotFoundException e)
        {
            throw new RuntimeException("Cannot create file: " + path);
        }
    }

    public void writeRels(long srcId, long dstId,
                          String edgeType)
    {
        // PARENT_OF edge also added by this function
        edgeWriter.print(srcId);
        edgeWriter.print(SEPARATOR);
        edgeWriter.print(dstId);
        edgeWriter.print(SEPARATOR);
        edgeWriter.print(edgeType);

        edgeWriter.write("\n");

    }

    @Override
    public void writeNodes(String id, String labels, String type, String flags, String lineno,
                           String code, String childnum, String funcid, String classname, String namespace,
                           String endlineno, String name, String doccomment, String fileid, String classid)
    {
        nodeWriter.print(id);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(labels);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(type);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(flags);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(lineno);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(code);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(childnum);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(funcid);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(classname);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(namespace);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(endlineno);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(name);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(doccomment);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(fileid);
        nodeWriter.print(SEPARATOR);
        nodeWriter.print(classid);
        // nodeWriter.print(SEPARATOR);

        nodeWriter.write("\n");
    }

    @Override
    public void changeOutputDir(String dirNameForFileNode) {

    }

    @Override
    public void shutdown() {
        closeEdgeFile();
        closeNodeFile();
    }

    public void closeRelsFile()
    {
        if ( edgeWriter != null)
            edgeWriter.close();
    }

    public void closeNodesFile()
    {
        if ( nodeWriter != null)
            nodeWriter.close();
    }
}
