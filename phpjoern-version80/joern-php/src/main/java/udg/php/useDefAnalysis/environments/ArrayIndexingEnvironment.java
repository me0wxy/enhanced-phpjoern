package udg.php.useDefAnalysis.environments;

import java.util.Collection;
import java.util.LinkedList;

import ast.expressions.StringExpression;
import ast.expressions.IntegerExpression;
import ast.expressions.Variable;
import udg.ASTNodeASTProvider;
import udg.ASTProvider;
import udg.useDefAnalysis.environments.UseDefEnvironment;
import udg.useDefGraph.UseOrDef;

public class ArrayIndexingEnvironment extends UseDefEnvironment
{
	private Collection<String> useSymbols = new LinkedList<String>();
	
	private boolean emitUse = false;
	
	// pass the 'code' of the array upstream (i.e., the array's name)
	// by recursion, this is already contained in the symbols field
	@Override
	public LinkedList<String> upstreamSymbols()
	{
		// return symbols;
		LinkedList<String> updatedsymbols = new LinkedList<String>();
		for(String symbol: symbols){
			StringBuffer sb = new StringBuffer(symbol);
			// mxy: to get the name of array's index
            if(((ASTNodeASTProvider)astProvider.getChild(1)).getASTNode() instanceof StringExpression) {
                String code = astProvider.getChild(1).getEscapedCodeStr();
                sb.append("[\"");
                sb.append(code);
                sb.append("\"]");
            }
			else if(((ASTNodeASTProvider)astProvider.getChild(1)).getASTNode() instanceof IntegerExpression) {
				String code = astProvider.getChild(1).getEscapedCodeStr();
				sb.append("[");
				sb.append(code);
				sb.append("]");
			}
			else if(((ASTNodeASTProvider)astProvider.getChild(1)).getASTNode() instanceof Variable){
				String code = astProvider.getChild(1).getChild(0).getEscapedCodeStr();
				sb.append("[$");
				sb.append(code);
				sb.append("]");
			}
			updatedsymbols.add(sb.toString());
		}
		return updatedsymbols;
	}
	
	public void addChildSymbols( LinkedList<String> childSymbols, ASTProvider child)
	{
		// add the index element(s) to the useSymbols 
		if( isUse( child))
			useSymbols.addAll( childSymbols);
		
		// the name of the array is a symbol to be passed upstream
		else
			symbols.addAll(childSymbols);
	}

	public Collection<UseOrDef> useOrDefsFromSymbols(ASTProvider child)
	{
		LinkedList<UseOrDef> retval = new LinkedList<UseOrDef>();

		if( isUse( child))
			retval.addAll(createUsesForAllSymbols(useSymbols));

		// if we are analyzing a standalone array access, then the
		// array's name should also be emitted as USE
		if( this.emitUse)
			retval.addAll(createUsesForAllSymbols(upstreamSymbols()));
		
		return retval;
	}
	
	@Override
	public boolean isUse( ASTProvider child)
	{
		int childNum = child.getChildNumber();
		return 1 == childNum ? true : false;
	}
	
	public void setEmitUse( boolean emitUse) {
		this.emitUse = emitUse;
	}
}
