package outputModules.common;

import java.util.HashMap;
import java.util.Map;

public class Writer
{
	static WriterImpl writerImpl;
	static Map<Object, Long> objectToId = new HashMap<Object, Long>();

	public static void setWriterImpl(WriterImpl impl)
	{
		writerImpl = impl;
	}

	public static void reset()
	{
		objectToId.clear();
	}

	public static Long getIdForObject(Object o)
	{
		return objectToId.get(o);
	}

	public static void setIdForObject(Object o, Long l)
	{
		objectToId.put(o, l);
	}

	public static void changeOutputDir(String dirNameForFileNode)
	{
		writerImpl.changeOutputDir(dirNameForFileNode);
	}

	public static long addNode(Object node, Map<String, Object> properties)
	{
		long nodeId = writerImpl.writeNode(node, properties);

		if (node != null)
			objectToId.put(node, nodeId);

		return nodeId;
	}

	public static void addEdge(long srcId, long dstId,
			Map<String, Object> properties, String edgeType)
	{
		writerImpl.writeEdge(srcId, dstId, properties, edgeType);
	}

	public static void appendRels(long srcId, long dstId,String edgeType)
	{
		writerImpl.writeRels(srcId, dstId, edgeType);
	}

	public static void appendNodes(String id, String labels, String type, String flags, String lineno,
								   String code, String childnum, String funcid, String classname, String namespace,
								   String endlineno, String name, String doccomment, String fileid, String classid) {
		writerImpl.writeNodes(id, labels, type, flags, lineno, code, childnum, funcid, classname, namespace,
								endlineno, name, doccomment, fileid, classid);
	}

	public static WriterImpl getWriterImpl()
	{
		return writerImpl;
	}

}
