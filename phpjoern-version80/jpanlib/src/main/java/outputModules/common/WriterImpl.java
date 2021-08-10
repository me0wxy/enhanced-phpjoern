package outputModules.common;

import java.util.Map;

public interface WriterImpl
{

	public long writeNode(Object node, Map<String, Object> properties);

	public void writeEdge(long srcId, long dstId,
			Map<String, Object> properties, String edgeType);

	public void writeRels(long src, long dstId, String edges);

	public void writeNodes(String id, String labels, String type, String flags, String lineno,
						   String code, String childnum, String funcid, String classname, String namespace,
						   String endlineno, String name, String doccomment, String fileid, String classid);

	public void changeOutputDir(String dirNameForFileNode);

	public void shutdown();

}
