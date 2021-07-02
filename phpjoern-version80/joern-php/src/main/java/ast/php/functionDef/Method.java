package ast.php.functionDef;

import ast.ASTNodeProperties;

public class Method extends FunctionDef
{
	private long classid = -1;
	public long getClassid(){return classid;}
	public void setClassid(long id){classid = id;}
	// useless in ast.so 70
	public String getEnclosingClass() {
		return getProperty(ASTNodeProperties.CLASSNAME);
	}

	public void setEnclosingClass(String classname) {
		setProperty(ASTNodeProperties.CLASSNAME, classname);
	}


}
