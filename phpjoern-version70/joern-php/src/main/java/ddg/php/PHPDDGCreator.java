package ddg.php;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ast.ASTNode;
import ast.expressions.AssignmentExpression;
import ast.expressions.Expression;
import ast.expressions.IntegerExpression;
import ast.expressions.StringExpression;
import cg.PHPCGFactory;
import ddg.DataDependenceGraph.DDG;
import ddg.DefUseCFG.DefUseCFG;
import misc.HashMapOfSets;
import misc.Pair;

public class PHPDDGCreator
{

    DefUseCFG cfg;

    HashMapOfSets in = new HashMapOfSets();
    HashMapOfSets out = new HashMapOfSets();
    HashMapOfSets gen = new HashMapOfSets();
    HashSet<Object> changedNodes;

    private class Definition
    {
        public Object statement;
        public String identifier;
        public boolean weight;
        public Object weakenby;

        public Definition(Object aStatement, String aIdentifier)
        {
            statement = aStatement;
            identifier = aIdentifier;
            weight = true;
            weakenby = null;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(identifier);
            sb.append(" of node ");
            sb.append(String.valueOf(((ASTNode)statement).getNodeId()));
            sb.append(" weight ");
            sb.append(String.valueOf(weight));
            return sb.toString();
        }

/*		public boolean equals(Definition d){
			if(this == d) return true;
			if(d == null) return false;
			return (statement.equals(d.statement) && identifier.equals(d.identifier));
		}
		public int hashCode(){
			return Objects.hash(statement, identifier);
		}*/

    };
    public class DefComparator implements Comparator<Definition>{
        @Override
        public int compare(Definition d1, Definition d2) {
            if(d1.identifier.length() == d2.identifier.length()){
                int w1 = d1.weight ? 1 : 0;
                int w2 = d2.weight ? 1 : 0;
                return w2-w1;
            }
            else{
                return d1.identifier.length() > d2.identifier.length() ? -1:1;
            }
        }
    }

    public DDG createForDefUseCFG(DefUseCFG aCfg)
    {
        cfg = aCfg;
        calculateReachingDefs();
        return createDDGFromReachingDefs();
    }

    private void calculateReachingDefs()
    {
        initReachingDefs();

        while (!changedNodes.isEmpty())
        {

            Object currentBlock = popFromChangedNodes();

            updateIn(currentBlock);
            boolean changed = updateOut(currentBlock);

            if (!changed)
                continue;

            List<Object> children = cfg.getChildBlocks()
                    .get(currentBlock);
            if (children == null)
                continue;

            for (Object o : children)
                changedNodes.add(o);

        }

    }

    private void initReachingDefs()
    {
        initOut();
        initGenFromOut();
        changedNodes = new HashSet<Object>();
        changedNodes.addAll(cfg.getStatements());
    }

    private Object popFromChangedNodes()
    {
        Object x = changedNodes.iterator().next();
        changedNodes.remove(x);
        return x;
    }

    private void initOut()
    {
        for (Object statement : cfg.getStatements())
        {

            // this has the nice side-effect that an
            // empty hash is created for the statement.

            out.removeAllForKey(statement);

            List<Object> symsDefined = cfg.getSymbolsDefined()
                    .get(statement);
            if (symsDefined == null)
                continue;

            for (Object s : symsDefined)
            {
                String symbol = (String) s;
                out.add(statement, new Definition(statement, symbol));
            }
        }
    }

    private void initGenFromOut()
    {
        for (Object statement : cfg.getStatements())
        {
            for (Object o : out.getListForKey(statement))
                gen.add(statement, o);
        }
    }

    private void updateIn(Object x)
    {
        List<Object> parents = cfg.getParentBlocks().get(x);
        if (parents == null)
            return;

        in.removeAllForKey(x);

        // in(x) = union(out(p))_{p in parents(x)}
        for (Object parent : parents)
        {
            HashSet<Object> parentOut = out.getListForKey(parent);
            if (parentOut == null)
                continue;
            for (Object o : parentOut){
                //if(!in.getListForKey(x).contains(o) || ((Definition)o).weight)
                in.add(x, o);
            }
        }
    }

    private boolean updateOut(Object x)
    {
        HashSet<Object> listForKey = out.getListForKey(x);
        HashSet<Object> oldOut = new HashSet<Object>(listForKey);

        out.removeAllForKey(x);

        // in(x)
        HashSet<Object> inForX = in.getListForKey(x);
        if (inForX != null)
        {
            for (Object o : inForX)
            {
                out.add(x, o);
            }
        }

        // -kill(x)
        List<Object> killX = cfg.getSymbolsDefined().get(x);
        if (killX != null)
        {
            HashSet<Object> weakenIn = new HashSet<>();
            Iterator<Object> it = out.getListForKey(x).iterator();
            while (it.hasNext())
            {
                Definition def = (Definition) it.next();
                // mxy: try to do simple constant propagation
                if(def.identifier.contains("$")){
                    def.identifier = SimpleConstantPropagation(def.identifier, inForX);
                }
                for(Object killsymbol: killX){
                    // mxy: a[0][1] will be killed by a[0]
                    if(killsymbol.equals(def.identifier) || isArrayPrefix((String)killsymbol, def.identifier) || isPropertyPrefix((String)killsymbol, def.identifier)){
                        it.remove();
                        break;
                    }
                    // mxy: a[0] will be weaken by a[0][1]
                    else if(isArrayPrefix(def.identifier, (String)killsymbol) || isPropertyPrefix(def.identifier, (String)killsymbol)){
                        it.remove();
                        def.weight = false;
                        def.weakenby = x;
                        weakenIn.add(def);
                        break;
                    }
                }
            }
            for(Object o: weakenIn)
                out.add(x, o);
        }

        // gen(X)
        HashSet<Object> genX = gen.getListForKey(x);

        if (genX != null)
        {
            for (Object o : genX)
            {
                // mxy: try to do simple constant propagation
                Definition def = (Definition) o;
                if(def.identifier.contains("$")){
                    def.identifier = SimpleConstantPropagation(def.identifier, inForX);
                }
                out.add(x, def);
            }
        }

        return !oldOut.equals(out.getListForKey(x));
    }

    private DDG createDDGFromReachingDefs()
    {
        DDG ddg = new DDG();

        for (Object statement : cfg.getStatements())
        {
            HashSet<Object> inForBlock = in.getListForKey(statement);
            if (inForBlock == null)
                continue;
            List<Object> usedSymbols = cfg.getSymbolsUsed()
                    .get(statement);
            if (usedSymbols == null)
                continue;
            else{
                for(Object symbol: usedSymbols){
                    String s = (String)symbol;
                    boolean specUse = false;
                    if(s.charAt(0) == '#'){
                        specUse = true;
                        s = s.substring(1);
                    }
                    // mxy: simple constant propagation
                    if(s.contains("$")){
                        s = SimpleConstantPropagation(s, inForBlock);
                    }
                    List<Definition> potentialDefs = new ArrayList<>();
                    HashSet<Object> recover = new HashSet<>();
                    for (Object d : inForBlock){
                        Definition def = (Definition) d;
                        // mxy: connect DEF a[0][1] to USE a[0]
                        if(!specUse && (isArrayPrefix(s, def.identifier) || isPropertyPrefix(s, def.identifier))){
                            ddg.add(def.statement, statement, def.identifier);
                            recover.add(def.statement);
                        }
                        // mxy: more judge for DEF a[0] and USE a[0][1]
                        else if(def.identifier.equals(s) || isArrayPrefix(def.identifier, s) || isPropertyPrefix(def.identifier, s)){
                            potentialDefs.add(def);
                        }
                    }
                    Collections.sort(potentialDefs, new DefComparator());
                    for(int i = 0;i < potentialDefs.size();i++){
                        Definition def = potentialDefs.get(i);
                        if(def.weakenby != null && recover.contains(def.weakenby))
                            def.weight = true;
                        if(def.weight || i == 0 || def.identifier.equals(s)){
                            ddg.add(def.statement, statement, def.identifier);
                            if(def.identifier.equals(s))
                                PHPCGFactory.addDDGinfo(statement, def.statement, def.identifier);
                        }
                    }
                }
            }
        }

        return ddg;
    }
    // mxy: judge array or property prefix
    private boolean isArrayPrefix(String pre, String arr){
        if(pre.equals(arr)) return false;
        if(!arr.contains("["))
            return false;
        String arrName = arr.substring(0,arr.indexOf("["));
        return arr.startsWith(pre) && pre.startsWith(arrName);
    }
    private boolean isPropertyPrefix(String pre, String property){
        if(pre.equals(property)) return false;
        if(!property.contains("->"))
            return false;
        String objectName = property.substring(0, property.indexOf("->"));
        return property.startsWith(pre) && pre.startsWith(objectName);
    }

    private String SimpleConstantPropagation(String s, HashSet<Object> DefBlock){
        if(DefBlock == null) return s;
        String pattern = "(\\$\\w+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        while(m.find()){
            String propagateVar = m.group(1);
            List<Definition> propagateVarDef = new ArrayList<>();
            for(Object d: DefBlock){
                Definition definition = (Definition)d;
                if(definition.identifier.equals(propagateVar.substring(1))){
                    propagateVarDef.add(definition);
                }
            }
            // mxy: only propagate when definition is unique
            if(propagateVarDef.size() == 1 &&
                    propagateVarDef.get(0).statement instanceof AssignmentExpression){
                AssignmentExpression expression = (AssignmentExpression)(propagateVarDef.get(0).statement);
                Expression rightExpression = expression.getRight();
                if(rightExpression instanceof IntegerExpression){
                    s = s.replace(propagateVar, rightExpression.getEscapedCodeStr());
                }
                else if(rightExpression instanceof StringExpression){
                    String f = s.charAt(s.indexOf(propagateVar)-1) == '[' ? "'%s'":"%s";
                    s = s.replace(propagateVar,
                            String.format(f, rightExpression.getEscapedCodeStr()));
                }
            }
        }
        return s;
    }

}
