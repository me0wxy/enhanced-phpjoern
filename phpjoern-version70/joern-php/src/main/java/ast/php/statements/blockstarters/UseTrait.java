package ast.php.statements.blockstarters;

import ast.ASTNodeProperties;
import ast.expressions.IdentifierList;
import ast.logical.statements.BlockStarter;

public class UseTrait extends BlockStarter
{
	private IdentifierList traits = null;
	private TraitAdaptations traitAdaptations = null;
	private String NamewithNS = "";
	private long classid = -1;

	public long getClassid()
	{
		return this.classid;
	}

	public void setClassid(long classid)
	{
		this.classid = classid;
	}

	public IdentifierList getTraits()
	{
		return this.traits;
	}

	public void setTraits(IdentifierList traits)
	{
		this.traits = traits;
		super.addChild(traits);
	}

	public TraitAdaptations getTraitAdaptations()
	{
		return this.traitAdaptations;
	}

	public void setTraitAdaptations(TraitAdaptations traitAdaptations)
	{
		this.traitAdaptations = traitAdaptations;
		super.addChild(traitAdaptations);
	}

	public String getEnclosingClass()
	{
		return getProperty(ASTNodeProperties.CLASSNAME);
	}

	public void setEnclosingClass(String classname)
	{
		setProperty(ASTNodeProperties.CLASSNAME, classname);
	}

	public String getEnclosingNamespace() {
		return getProperty(ASTNodeProperties.NAMESPACE);
	}

	public void setEnclosingNamespce(String namespace)
	{
		setProperty(ASTNodeProperties.NAMESPACE, namespace);
	}

	// Add namespace to FULL class name
	public void setNamewithNS(String namespace, String name) {
		// In case there is no namespace
		if ("".equals(namespace)) {
			this.NamewithNS = name;
		} else {
			this.NamewithNS = namespace + "\\" + name;
		}
	}

	public String getNamewithNS() {
		return this.NamewithNS;
	}
}
