package cg;

import graphutils.Edge;

import java.util.HashMap;
import java.util.Map;

public class CGEdge extends Edge<CGNode> {

	private static final String DEFAULT_LABEL = "CALL";
	private Map<String, Object> properties;
	
	public CGEdge(CGNode source, CGNode destination) {
		super(source, destination);
	}

	public CGEdge(CGNode source, CGNode destination, Long fileid){
		super(source, destination);
		this.properties = new HashMap<String, Object>();
		this.properties.put("NeedIncludeSet", fileid);
	}

	@Override
	public Map<String, Object> getProperties()
	{
		if (this.properties == null)
		{
			this.properties = new HashMap<String, Object>();
		}
		return this.properties;
	}
	
	@Override
	public String toString() {
		return getSource() + " ==[" + DEFAULT_LABEL + "]==> " + getDestination();
	}
}
