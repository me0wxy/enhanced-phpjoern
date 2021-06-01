package udg.php.useDefAnalysis.environments;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import udg.ASTProvider;
import udg.useDefAnalysis.environments.UseDefEnvironment;
import udg.useDefGraph.UseOrDef;

public class AssignmentEnvironment extends UseDefEnvironment
{
	private Collection<String> defSymbols = new LinkedList<String>();
	private Collection<String> useSymbols = new LinkedList<String>();
	
	public void addChildSymbols( LinkedList<String> childSymbols, ASTProvider child)
	{
		this.symbols.addAll( childSymbols);
			
		// additionally collect defs and uses symbols in separate lists,
		// to be used for useOrDefsFromSymbols
		if( isDef( child))
			defSymbols.addAll( childSymbols);
		if( isUse( child))
			useSymbols.addAll( childSymbols);
	}

	public Collection<UseOrDef> useOrDefsFromSymbols(ASTProvider child)
	{
		LinkedList<UseOrDef> retval = new LinkedList<UseOrDef>();

		if( isDef( child)){
			retval.addAll(createDefsForAllSymbols(defSymbols));
			// mxy: for array assignment, add USE specially
			Collection<String> updateDefsymbols = new LinkedList<String>();
			for(String defsymbol:defSymbols){
				if(defsymbol.contains("[")){
					updateDefsymbols.add("#"+defsymbol.substring(0,
							defsymbol.lastIndexOf("[")));
				}
				else if(defsymbol.contains("->")){
					updateDefsymbols.add("#"+defsymbol.substring(0,
							defsymbol.lastIndexOf("->")));
				}
			}
			if(updateDefsymbols.size() != 0)
				retval.addAll(createUsesForAllSymbols(updateDefsymbols));
		}

		if( isUse( child))
			retval.addAll(createUsesForAllSymbols(useSymbols));

		return retval;
	}
	
	@Override
	public boolean isDef( ASTProvider child)
	{
		int childNum = child.getChildNumber();
		return 0 == childNum ? true : false;
	}
	
	@Override
	public boolean isUse( ASTProvider child)
	{
		int childNum = child.getChildNumber();
		return 1 == childNum ? true : false;
	}
}
