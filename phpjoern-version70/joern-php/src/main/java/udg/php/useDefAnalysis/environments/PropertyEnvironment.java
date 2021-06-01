package udg.php.useDefAnalysis.environments;

import java.util.Collection;
import java.util.LinkedList;

import ast.expressions.StringExpression;
import ast.expressions.Variable;
import udg.ASTNodeASTProvider;
import udg.ASTProvider;
import udg.useDefAnalysis.environments.UseDefEnvironment;
import udg.useDefGraph.UseOrDef;

public class PropertyEnvironment extends UseDefEnvironment
{
	private boolean emitUse = false;

	// simply return the list of symbols added earlier by addChildSymbols
	@Override
	public LinkedList<String> upstreamSymbols()
	{	
		//return this.symbols;
		LinkedList<String> updatedsymbols = new LinkedList<String>();
		for(String symbol: this.symbols){
			StringBuffer sb = new StringBuffer(symbol);
			sb.append("->");
			// mxy: to get the name of object's property
			if(((ASTNodeASTProvider)astProvider.getChild(1)).getASTNode() instanceof StringExpression) {
				String code = astProvider.getChild(1).getEscapedCodeStr();
				sb.append(code);
			}
			else if(((ASTNodeASTProvider)astProvider.getChild(1)).getASTNode() instanceof Variable){
				String code = astProvider.getChild(1).getChild(0).getEscapedCodeStr();
				sb.append("$");
				sb.append(code);
			}
			updatedsymbols.add(sb.toString());
		}
		return updatedsymbols;
	}
	
	// add the *object's name* of the property access expression to the child symbols
	public void addChildSymbols(LinkedList<String> childSymbols, ASTProvider child)
	{
		int childNum = child.getChildNumber();
		// Only add the left child but never the right child
		// e.g., in $foo->bar, we only add $foo but not $bar.
		if( 0 == childNum)
			this.symbols.addAll(childSymbols);
	}
	
	public Collection<UseOrDef> useOrDefsFromSymbols(ASTProvider child)
	{
		if( this.emitUse) {
			LinkedList<UseOrDef> retval = createUsesForAllSymbols(upstreamSymbols());
			return retval;
		}
		else
			return super.useOrDefsFromSymbols(child);
	}
	
	public void setEmitUse( boolean emitUse) {
		this.emitUse = emitUse;
	}
}
