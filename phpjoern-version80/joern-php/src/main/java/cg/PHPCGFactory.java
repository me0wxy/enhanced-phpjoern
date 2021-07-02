package cg;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ast.expressions.*;
import ast.php.declarations.ClassDef;
import ast.php.expressions.MethodCallExpression;
import ast.php.expressions.StaticCallExpression;
import ast.php.functionDef.Closure;
import ast.php.functionDef.Method;
import ast.php.functionDef.FunctionDef;
import ast.php.functionDef.TopLevelFunctionDef;
import ast.php.functionDef.Parameter;
import ast.php.statements.GlobalStatement;
import cfg.CFG;
import cfg.nodes.ASTNodeContainer;
import cfg.nodes.CFGNode;
import cg.CG;
import cg.CGEdge;
import cg.CGNode;
import filesystem.PHPIncludeMapFactory;
import inherit.PHPInheritFactory;
import inputModules.csv.PHPCSVNodeTypes;
import misc.MultiHashMap;
import misc.Pair;
import ast.ASTNode;

public class PHPCGFactory {

	// maintains a map of known function names (e.g., "B\foo" -> function foo() in namespace B)
	// private static HashMap<String,FunctionDef> functionDefs = new HashMap<String,FunctionDef>();
	private static MultiHashMap<String,FunctionDef> functionDefs = new MultiHashMap<String,FunctionDef>();
	// maintains a list of function calls
	private static LinkedList<CallExpressionBase> functionCalls = new LinkedList<CallExpressionBase>();


	// maintains a map of known static method names (e.g., "B\A::foo" -> static function foo() in class A in namespace B)
	private static HashMap<String,Method> staticMethodDefs = new HashMap<String,Method>();
	// maintains a list of static method calls
	private static LinkedList<StaticCallExpression> staticMethodCalls = new LinkedList<StaticCallExpression>();
	
	// maintains a map of known constructors (e.g., "B\A" -> static function __construct() in class A in namespace B)
	private static HashMap<String,Method> constructorDefs = new HashMap<String,Method>();
	// maintains a list of static method calls
	private static LinkedList<NewExpression> constructorCalls = new LinkedList<NewExpression>();
	
	// maintains a map of known non-static method names (e.g., "foo" -> {function foo() in class A, function foo() in class C}
	// this is a MultiHashMap, as these names are not necessarily unique; we could in theory use a similar
	// mapping as for static method defs (e.g., "A->foo -> function foo() in class A, B->foo -> function foo() in class B),
	// however that would make it inefficient to lookup the methods when inspecting the method calls
	private static MultiHashMap<String,Method> nonStaticMethodDefs = new MultiHashMap<String,Method>();
	// maintains a list of non-static method calls
	private static LinkedList<MethodCallExpression> nonStaticMethodCalls = new LinkedList<MethodCallExpression>();


	// mxy: for ast.so 70, there is no property "classname" in ast node, so delay these methodDef's handle
	private static LinkedList<Method> delayMethods = new LinkedList<>();

	// mxy: positive or negative parse mode
	private static boolean mode = true;
	// mxy: numbers to evaluate multiple func optimize
	private static int normalRight = 0;
	private static int corrected = 0;
	private static int stillProblem = 0;

	// mxy: some useful Maps
	private static MultiHashMap<Object, Pair<Object,String>> DDGParent = new MultiHashMap<>();
	private static HashSet<GlobalStatement> globalStatements = new HashSet<>();
	private static MultiHashMap<String, GlobalStatement> globalMap = new MultiHashMap<>();
	private static HashMap<Long, Pair<FunctionDef,Integer>> parameterMap = new HashMap<>();
	private static HashMap<Long, FunctionDef> funcidMap = new HashMap<>();
	private static HashMap<Long, CFG> cfgMap = new HashMap<>();

	/**
	 * Creates a new CG instance based on the lists of known function definitions and function calls.
	 * 
	 * Call this after all function definitions and calls have been added to the lists using
	 * addFunctionDef(FunctionDef) and addFunctionCall(CallExpression).
	 * 
	 * After a call graph has been constructed, these lists are automatically reset.
	 * 
	 * @return A new call graph instance.
	 */
	public static CG newInstance() {
		
		CG cg = new CG();

		// mxy:
		for(GlobalStatement globalStatement: globalStatements){
			globalMap.add(globalStatement.getVariable().getNameExpression().getEscapedCodeStr(), globalStatement);
		}
		globalStatements.clear();

		dealClassRelatedDef();

		createFunctionCallEdges(cg);
		createStaticMethodCallEdges(cg);
		createConstructorCallEdges(cg);
		createNonStaticMethodCallEdges(cg);
		
		reset();
		
		return cg;
	}

	private static void createFunctionCallEdges(CG cg) {
		int multipleConnect = 0;
		int dynamic = 0;
		for( CallExpressionBase functionCall : functionCalls) {

			// make sure the call target is statically known
			if( functionCall.getTargetFunc() instanceof Identifier) {
				
				Identifier callIdentifier = (Identifier)functionCall.getTargetFunc();
				
				// if call identifier is fully qualified,
				// just look for the function's definition right away
				if( callIdentifier.getFlags().contains( PHPCSVNodeTypes.FLAG_NAME_FQ)) {
					String functionKey = callIdentifier.getNameChild().getEscapedCodeStr();
					addCallEdgeIfDefinitionKnownMulti(cg, functionDefs, functionCall, functionKey);
				}

				// otherwise, i.e., if the call identifier is not fully qualified,
				// first look in the current namespace, then if the function is not found,
				// look in the global namespace
				// (see http://php.net/manual/en/language.namespaces.rules.php)
				else {
					boolean found = false;
					// note that looking in the current namespace first only makes
					// sense if we are not already in the global namespace anyway
					if( !callIdentifier.getEnclosingNamespace().isEmpty()) {
						String functionKey = callIdentifier.getEnclosingNamespace() + "\\"
								+ callIdentifier.getNameChild().getEscapedCodeStr();
						found = addCallEdgeIfDefinitionKnownMulti(cg, functionDefs, functionCall, functionKey);
					}
					
					// we did not find the function or were already in global namespace;
					// try to find the function in the global namespace
					if( !found) {
						String functionKey = callIdentifier.getNameChild().getEscapedCodeStr();
						found = addCallEdgeIfDefinitionKnownMulti(cg, functionDefs, functionCall, functionKey);

						// mxy: if still not found in global namespace, two cases:
						// no definitions: do nothing
						// multiple definitions and mode mode on, do multiple connect with includeinformations
						if(!found && functionDefs.containsKey(functionKey) && mode){
							multipleConnect++;
							for (FunctionDef functionKeyItem : functionDefs.get(functionKey)){
								addCallEdgeWithIncludeCondition(cg, functionCall, functionKeyItem, functionKeyItem.getFileId());
							}
						}

					}
				}
			}
			else{
				dynamic++;
				System.err.println("Statically unknown function call at node id " + functionCall.getNodeId() + "!");
			}
		}
		System.out.println();
		System.out.println("Summary Function call construction");
		System.out.println("----------------------------------------");
		System.out.println();
		System.out.println(String.format("For all %d Function calls, %d normalright, %d Statically unknown, %d built-in; \n%d corrected, %d still have problem",
				functionCalls.size(),normalRight,dynamic,(functionCalls.size()-normalRight-dynamic-corrected-stillProblem),corrected,stillProblem));
		System.out.println("And we do multiple connect for "+multipleConnect+" calls.");
		System.out.println();
	}
	
	private static void createStaticMethodCallEdges(CG cg) {
		
		for( StaticCallExpression staticCall : staticMethodCalls) {
			
			// make sure the call target is statically known
			if( staticCall.getTargetClass() instanceof Identifier
					&& staticCall.getTargetFunc() instanceof StringExpression) {
				
				Identifier classIdentifier = (Identifier)staticCall.getTargetClass();
				StringExpression methodName = (StringExpression)staticCall.getTargetFunc();
				
				// if class identifier is fully qualified,
				// just look for the static method's definition right away
				if( classIdentifier.getFlags().contains( PHPCSVNodeTypes.FLAG_NAME_FQ)) {
					String staticMethodKey = classIdentifier.getNameChild().getEscapedCodeStr()
							+ "::" + methodName.getEscapedCodeStr();
					addCallEdgeIfDefinitionKnown(cg, staticMethodDefs, staticCall, staticMethodKey);
				}

				// otherwise, i.e., if the call identifier is not fully qualified,
				// prepend the current namespace first and look for it there
				// (see http://php.net/manual/en/language.namespaces.rules.php)
				else {

					// note that prepending the current namespace only makes
					// sense if there is one
					if( !classIdentifier.getEnclosingNamespace().isEmpty()) {
						String staticMethodKey = classIdentifier.getEnclosingNamespace() + "\\"
								+ classIdentifier.getNameChild().getEscapedCodeStr()
								+ "::" + methodName.getEscapedCodeStr();
						addCallEdgeIfDefinitionKnown(cg, staticMethodDefs, staticCall, staticMethodKey);
					}
					
					// if we are in the global namespace, we should not accidentally prepend a backslash
					else {
						String staticMethodKey = classIdentifier.getNameChild().getEscapedCodeStr()
								+ "::" + methodName.getEscapedCodeStr();
						addCallEdgeIfDefinitionKnown(cg, staticMethodDefs, staticCall, staticMethodKey);
					}
				}
			}
			else
				System.err.println("Statically unknown static method call at node id " + staticCall.getNodeId() + "!");
		}
	}

	private static void createConstructorCallEdges(CG cg) {
		
		for( NewExpression constructorCall : constructorCalls) {
			
			// make sure the call target is statically known
			if( constructorCall.getTargetClass() instanceof Identifier) {
				
				Identifier classIdentifier = (Identifier)constructorCall.getTargetClass();
				
				// if class identifier is fully qualified,
				// just look for the constructor's definition right away
				if( classIdentifier.getFlags().contains( PHPCSVNodeTypes.FLAG_NAME_FQ)) {
					String constructorKey = classIdentifier.getNameChild().getEscapedCodeStr();
					addCallEdgeIfDefinitionKnown(cg, constructorDefs, constructorCall, constructorKey);
				}

				// otherwise, i.e., if the call identifier is not fully qualified,
				// prepend the current namespace first and look for it there
				// (see http://php.net/manual/en/language.namespaces.rules.php)
				else {

					// note that prepending the current namespace only makes
					// sense if there is one
					if( !classIdentifier.getEnclosingNamespace().isEmpty()) {
						String constructorKey = classIdentifier.getEnclosingNamespace() + "\\"
								+ classIdentifier.getNameChild().getEscapedCodeStr();
						addCallEdgeIfDefinitionKnown(cg, constructorDefs, constructorCall, constructorKey);
					}
					
					// if we are in the global namespace, we should not accidentally prepend a backslash
					else {
						String constructorKey = classIdentifier.getNameChild().getEscapedCodeStr();
						addCallEdgeIfDefinitionKnown(cg, constructorDefs, constructorCall, constructorKey);
					}
				}
			}
			else
				System.err.println("Statically unknown constructor call at node id " + constructorCall.getNodeId() + "!");
		}
	}
	
	private static void createNonStaticMethodCallEdges(CG cg) {
		
		int successfullyMapped = 0;
		int ambiguousNotMapped = 0;
		int notMappedBefore = 0;
		HashMap<String, String> globalCache = new HashMap<>();
		HashSet<String> notClasstype = new HashSet<String>(){{add("string");add("int");add("true");add("false");add("null");}};
		for( MethodCallExpression methodCall : nonStaticMethodCalls) {
			// make sure the call target is statically known
			if( methodCall.getTargetFunc() instanceof StringExpression) {
				StringExpression methodName = (StringExpression)methodCall.getTargetFunc();
				String methodKey = methodName.getEscapedCodeStr();
				// let's count the dynamic methods that could be mapped, and those that cannot
				if( nonStaticMethodDefs.containsKey(methodKey)) {
					// check whether there is only one matching function definition
					if( nonStaticMethodDefs.get(methodKey).size() == 1) {
						addCallEdge( cg, methodCall, nonStaticMethodDefs.get(methodKey).get(0));
						successfullyMapped++;
					}
					else { // there is more than one matching function definition
						// we can still map $this->foo(), though, because we know what $this is
						if( methodCall.getTargetObject() instanceof Variable
							&& ((Variable)methodCall.getTargetObject()).getNameExpression() instanceof StringExpression
							&& ((StringExpression)((Variable)methodCall.getTargetObject()).getNameExpression())
								.getEscapedCodeStr().equals("this")) {
							
							//String enclosingClass = methodCall.getEnclosingClass();
							ClassDef enclosingClassDef = PHPInheritFactory.getClassDef(methodCall.getClassid());
							if(enclosingClassDef == null) {
								ambiguousNotMapped++;
								continue;
							}
							String enclosingClass = enclosingClassDef.getName();
							for( Method methodDef : nonStaticMethodDefs.get(methodKey)) {
								if( enclosingClass.equals(getEnclosingClass(methodDef))) {
									addCallEdge( cg, methodCall, methodDef);
									successfullyMapped++;
									break;
								}
							}							
						}
						// mxy:
						else {
							boolean successFlag = false;
							if( methodCall.getTargetObject() instanceof Variable
									&& ((Variable)methodCall.getTargetObject()).getNameExpression() instanceof StringExpression){
								String callObjectName = ((Variable)methodCall.getTargetObject()).getNameExpression().getEscapedCodeStr();

								// get all expressions that may determine the type class
								HashSet<Object> potentialExpr = getPotentialClassDetermineExpr(methodCall, cg, callObjectName, 0);
								for(Object expr: potentialExpr){
									String enclosingClass = "";

									// global. check TypeHint in doccomment
									if(expr instanceof GlobalStatement && funcidMap.containsKey(Long.parseLong(((GlobalStatement) expr).getProperty("funcid")))){
										FunctionDef exprFunctionDef = funcidMap.get(Long.parseLong(((GlobalStatement) expr).getProperty("funcid")));
										String globalVarName = ((GlobalStatement) expr).getVariable().getNameExpression().getEscapedCodeStr();
										if(globalCache.containsKey(globalVarName))
											enclosingClass = globalCache.get(globalVarName);
										else if(exprFunctionDef.getDocComment() != null){
											Pattern r = Pattern.compile("@global\\s+([A-Za-z0-9_|]+)\\s+\\$"+globalVarName);
											Matcher m = r.matcher(exprFunctionDef.getDocComment());
											while(m.find()){
												String[] types = m.group(1).split("\\|");
												for(String type:types){
													if(!notClasstype.contains(type)){
														globalCache.put(globalVarName,type);
														enclosingClass = type;
														break;
													}
												}
											}
										}
									}
									// NEW assignment, like...
									else{
										// $a = new Classname();
										if(expr instanceof AssignmentExpression && ((AssignmentExpression)expr).getRight() instanceof NewExpression){
											try {
												Identifier identifier = (Identifier) (((NewExpression) ((AssignmentExpression) expr).getRight()).getTargetClass());
												enclosingClass = identifier.getNameChild().getEscapedCodeStr();
												if(!identifier.getEnclosingNamespace().isEmpty()){
													enclosingClass = identifier.getEnclosingNamespace()+"\\"+enclosingClass;
												}
											}
											catch (Exception e){
												//e.printStackTrace();
											}
										}
										// $a = func() check ReturnType and TypeHint
										else if(expr instanceof AssignmentExpression && ((AssignmentExpression)expr).getRight() instanceof CallExpressionBase){
											CGNode cgNode = new CGNode((CallExpressionBase) (((AssignmentExpression)expr).getRight()));
											if(!cg.contains(cgNode)) continue;
											for(CGEdge cgEdge: cg.outgoingEdges(cgNode)) {
												try {
													FunctionDef funcDef = (FunctionDef) (cgEdge.getDestination().getASTNode());
													if(funcDef.getReturnType() != null){
														enclosingClass = funcDef.getReturnType().getNameChild().getEscapedCodeStr();
													}
													else if(funcDef.getDocComment() != null){
														Pattern r = Pattern.compile("@return\\s+([A-Za-z0-9_|]+)\\s+");
														Matcher m = r.matcher(funcDef.getDocComment());
														while(m.find()){
															String[] types = m.group(1).split("\\|");
															for(String type:types){
																if(!notClasstype.contains(type)){
																	enclosingClass = type;
																	break;
																}
															}
														}
													}
												}
												catch (Exception e){
													//e.printStackTrace();
												}
											}
										}
									}
									// class not found
									// System.err.println(enclosingClass);
									if(enclosingClass.equals("")) continue;

									for( Method methodDef : nonStaticMethodDefs.get(methodKey)) {
										if( enclosingClass.equals(getEnclosingClass(methodDef)) ||
												(PHPInheritFactory.getHierarchyMultiHashMap(enclosingClass) != null &&
														PHPInheritFactory.getHierarchyMultiHashMap(enclosingClass).contains(getEnclosingClass(methodDef)))) {
											addCallEdge( cg, methodCall, methodDef);
											successFlag = true;
											break;
										}
									}
									if(successFlag) break;
								}
							}

							if(successFlag){
								successfullyMapped++;
								notMappedBefore++;
							}
							else{
								/*System.out.println(nonStaticMethodDefs.get(methodKey).size());
								System.out.println(PHPIncludeMapFactory.debug(methodCall));
								System.out.println(methodCall.getLocation().startLine);*/
								ambiguousNotMapped++;
							}
						}

					}
				}
			}
			else
				System.err.println("Statically unknown non-static method call at node id " + methodCall.getNodeId() + "!");
		}

		System.out.println();
		System.out.println("Summary dynamic method call construction");
		System.out.println("----------------------------------------");
		System.out.println();
		
		/* Statistics on method calls */
		System.out.println("Successfully mapped dynamic method calls: " + successfullyMapped);
		System.out.println("Ambiguous non-mapped dynamic method calls: " + ambiguousNotMapped);
		float mappedMethodCallsPercent = (successfullyMapped + ambiguousNotMapped) == 0 ? 100 :
			((float)successfullyMapped/((float)successfullyMapped+(float)ambiguousNotMapped)) * 100;
		System.out.println( "=> " + mappedMethodCallsPercent + "% " +
				"of non-static method calls could be successfully mapped.");
		System.out.println("The numbers before are "+(successfullyMapped-notMappedBefore)+" and "+(ambiguousNotMapped+notMappedBefore));
		float mappedMethodCallsPercentBefore = (successfullyMapped + ambiguousNotMapped) == 0 ? 100 :
				(((float)successfullyMapped-(float)notMappedBefore)/((float)successfullyMapped+(float)ambiguousNotMapped)) * 100;
		System.out.println( "=> After Optimization, it improves " + (mappedMethodCallsPercent-mappedMethodCallsPercentBefore) + "%.");
		System.out.println();
		

		/* Statistics on method defs */
		int uniqueDefs = 0, ambiguousDefs = 0;
		for( List<Method> methodList : nonStaticMethodDefs.values()) {
			if( methodList.size() == 1)
				uniqueDefs++;
			else
				ambiguousDefs++;
		}
		System.out.println("Unique method names: " + uniqueDefs);
		System.out.println("Duplicate method names: " + ambiguousDefs);
		float uniqueMethodNamesPercent = (uniqueDefs + ambiguousDefs) == 0 ? 100 :
			((float)uniqueDefs/((float)uniqueDefs+(float)ambiguousDefs)) * 100;
		System.out.println( "=> " + uniqueMethodNamesPercent + "% of all method names were unique.");
	}

	/**
	 * mxy: for non-static method call and more than one matching function definition,
	 * search statements that determine the call object's type potentially
	 *
	 * @return HashSet of potential statements
	 */
	private static HashSet<Object> getPotentialClassDetermineExpr(CallExpressionBase methodCall, CG cg, String targetVar, int depth){
		HashSet<Object> res = new HashSet<>();
		// recursive depth threshold
		if(depth > 2) return res;

		int lineno = methodCall.getLocation().startLine;
		for(Object Key: DDGParent.keySet()){
			// DDG is build on each line's root node, here use lineno to locate
			int start = ((ASTNode)Key).getLocation().startLine;
			int end = ((ASTNode)Key).getLocation().endLine;
			if(!(end == -1 && start == lineno) && !(start<=lineno && end >= lineno))
				continue;

			for(Pair<Object, String> def: DDGParent.get(Key)){
				// select the DDG parents of callobject ($a in $a->foo();)
				if(!def.getR().equals(targetVar))
					continue;
				Object expr = def.getL();
				// check the structure of DDG parents, three cases:
				// 1.$a = ...;
				// check right, handle $a = new A() only, $a = func() or others cannot handle
				if(expr instanceof AssignmentExpression){
				//if(expr instanceof AssignmentExpression && ((AssignmentExpression)expr).getRight() instanceof NewExpression){
					res.add(expr);
				}
				// 2.funcdef($a, $b){...};
				// backward in CG
				else if(expr instanceof Parameter && parameterMap.containsKey(((Parameter) expr).getNodeId())){
					FunctionDef funcDef = parameterMap.get(((Parameter) expr).getNodeId()).getL();
					int index = parameterMap.get(((Parameter) expr).getNodeId()).getR();
					CGNode cgNode = new CGNode(funcDef);
					if(!cg.contains(cgNode)) continue;
					for(CGEdge cgEdge: cg.incomingEdges(cgNode)) {
						try {
							CallExpressionBase funcCall = (CallExpressionBase) (cgEdge.getSource().getASTNode());
							if(funcCall == methodCall) continue;
							Expression arg = funcCall.getArgumentList().getArgument(index);
							if (arg instanceof Variable && ((Variable) arg).getNameExpression() instanceof StringExpression)
								res.addAll(getPotentialClassDetermineExpr(funcCall, cg, ((Variable) arg).getNameExpression().getEscapedCodeStr(),depth+1));
						}
						catch (Exception e){
							//e.printStackTrace();
						}
					}
				}
				// 3.global $a;
				else if(expr instanceof GlobalStatement){
					res.add(expr);
					if(globalMap.containsKey(((GlobalStatement) expr).getVariable().getNameExpression().getEscapedCodeStr())){
						// do CFG forward search in the same variables, try to get  New Assignments
						for(GlobalStatement globalStatement:
								globalMap.get(((GlobalStatement) expr).getVariable().getNameExpression().getEscapedCodeStr())){
							Long funcid = Long.parseLong(globalStatement.getProperty("funcid"));
							if(globalStatement == expr || !cfgMap.containsKey(funcid))
								continue;
							for(CFGNode cfgNode: cfgMap.get(funcid).getVertices()){
								Object astnode = ((ASTNodeContainer)cfgNode).getASTNode();
								if(astnode instanceof AssignmentExpression
										&& ((AssignmentExpression)astnode).getRight() instanceof NewExpression
										&& ((AssignmentExpression)astnode).getLeft() instanceof Variable
										&& ((Variable) ((AssignmentExpression)astnode).getLeft()).getNameExpression().getEscapedCodeStr().equals(targetVar)) {
									res.add(astnode);
									break;
								}
							}
						}
					}
				}
			}
		}
		return res;
	}

	/**
	 * Checks whether a given function key is known and if yes,
	 * adds a corresponding edge in the given call graph.
	 * 
	 * @return true if an edge was added, false otherwise
	 */
	private static boolean addCallEdgeIfDefinitionKnown(CG cg, HashMap<String,? extends FunctionDef> defSet, CallExpressionBase functionCall, String functionKey) {
		
		boolean ret = false;
		
		// check whether we know the called function
		if( defSet.containsKey(functionKey))		
			ret = addCallEdge( cg, functionCall, defSet.get(functionKey));
		
		return ret;
	}
	/**
	 * mxy:
	 * if a given function key is known and there are more than one function def,
	 * we will adds a corresponding edge in the given call graph.
	 *
	 * @return true if an edge was added, false otherwise
	 */
	private static boolean addCallEdgeIfDefinitionKnownMulti(CG cg, MultiHashMap<String,FunctionDef> defSet, CallExpressionBase functionCall, String functionKey) {

		boolean ret = false;

		if( defSet.containsKey(functionKey)) {
			List<FunctionDef> allfunctionDefs = defSet.get(functionKey);

			if(allfunctionDefs.size() == 1){
				ret = addCallEdge( cg, functionCall, allfunctionDefs.get(0));
				normalRight++;
			}
			else{
				// mxy: find function in files that functionCall's file included
				HashSet<Long> potentialDefFileId = PHPIncludeMapFactory.getIncludeFilesSet(functionCall.getFileId());
				int correctCount = 0;
				FunctionDef correctDef = null;
				for (FunctionDef functionKeyItem : allfunctionDefs) {
					if (potentialDefFileId.contains(functionKeyItem.getFileId())){
						correctCount++;
						correctDef = functionKeyItem;
					}
				}
				// note that it should have one and only one function in include set
				if(correctCount == 1) {
					ret = addCallEdge(cg, functionCall, correctDef);
					corrected++;
				}
				// no definitions in Include Set
				else if(correctCount == 0){
					//System.err.println("Function Call at node id "+functionCall.getNodeId()+" has "+allfunctionDefs.size()+" definitions, none in IncludeSet");
					stillProblem++;

				}
				// multiple definitions in Include Set (rarely)
				else{
					//System.err.println("Function Call at node id "+functionCall.getNodeId()+" has "+allfunctionDefs.size()+" definitions, "+correctCount+" in IncludeSet");
					stillProblem++;
				}
			}
		}
		return ret;
	}
	/**
	 * Adds an edge to a given call graph.
	 * 
	 * @return true if an edge was added, false otherwise
	 */
	private static boolean addCallEdge(CG cg, CallExpressionBase functionCall, FunctionDef functionDef) {
		
		boolean ret = false;
		
		CGNode caller = new CGNode(functionCall);
		CGNode callee = new CGNode(functionDef);
		ret = cg.addVertex(caller);
		// note that adding a callee node many times is perfectly fine:
		// CGNode overrides the equals() and hashCode() methods,
		// so it will actually only be added the first time
		cg.addVertex(callee);
		cg.addEdge(new CGEdge(caller, callee));
		
		return ret;
	}
	/**
	 * mxy Adds an edge(with the needed included file's id that the edge is true)to a given call graph.
	 *
	 * @return true if an edge was added, false otherwise
	 */
	private static boolean addCallEdgeWithIncludeCondition(CG cg, CallExpressionBase functionCall, FunctionDef functionDef, Long fileid) {

		boolean ret = false;

		CGNode caller = new CGNode(functionCall);
		CGNode callee = new CGNode(functionDef);
		ret = cg.addVertex(caller);
		// note that adding a callee node many times is perfectly fine:
		// CGNode overrides the equals() and hashCode() methods,
		// so it will actually only be added the first time
		cg.addVertex(callee);
		cg.addEdge(new CGEdge(caller, callee, fileid));

		return ret;
	}
	
	private static void reset() {
	
		functionDefs.clear();
		functionCalls.clear();
		
		staticMethodDefs.clear();
		staticMethodCalls.clear();
		
		constructorDefs.clear();
		constructorCalls.clear();
		
		nonStaticMethodDefs.clear();
		nonStaticMethodCalls.clear();
		// mxy
		DDGParent.clear();
		parameterMap.clear();
		funcidMap.clear();

	}
	
	/**
	 * Adds a new known function definition.
	 * 
	 * @param functionDef A PHP function definition. If a function definition with the same
	 *                    name was previously added, then the new function definition will
	 *                    be used for that name and the old function definition will be returned.
	 * @return If there already exists a PHP function definition with the same name,
	 *         then returns that function definition. Otherwise, returns null. For non-static method
	 *         definitions, always returns null.
	 */
	public static FunctionDef addFunctionDef( FunctionDef functionDef) {

		// artificial toplevel functions wrapping toplevel code cannot be called
		if( functionDef instanceof TopLevelFunctionDef)
			return null;
		
		// we also ignore closures as they do not have a statically known reference
		else if( functionDef instanceof Closure)
			return null;
		
		// finally, abstract methods cannot be called either
		else if( functionDef instanceof Method
				&& functionDef.getFlags().contains(PHPCSVNodeTypes.FLAG_MODIFIER_ABSTRACT))
			return null;
		
		// it's a static method
		else if( functionDef instanceof Method
				&& functionDef.getFlags().contains(PHPCSVNodeTypes.FLAG_MODIFIER_STATIC)) {
			delayMethods.add((Method) functionDef);
			return null;
		}
		
		// it's a constructor
		// Note that a PHP constructor cannot be static, so the previous case for static methods evaluates to false;
		// also note that there are two possible constructor names: __construct() (recommended) and ClassName() (legacy)
		else if( functionDef instanceof Method
				&& (functionDef.getName().equals("__construct")
						|| functionDef.getName().equals(getEnclosingClass((Method)functionDef)))){
			delayMethods.add((Method) functionDef);
			return null;
		}
		
		// other methods than the above are non-static methods
		else if( functionDef instanceof Method) {
			// use foo as key for a non-static method foo in any class in any namespace;
			// note that the enclosing namespace of a non-static method definition is irrelevant here,
			// as that is usually not known at the call site (neither is the class name, except
			// when the keyword $this is used)
			String methodKey = ((Method)functionDef).getName();

			if( nonStaticMethodDefs.containsKey(methodKey)) {
				System.err.println("Method definition for '" + methodKey + "' ambiguous: " +
						" already known method definitions are " + nonStaticMethodDefs.get(methodKey) +
						", now adding " + functionDef + ")");
			}
			
			nonStaticMethodDefs.add( methodKey, (Method)functionDef);
			funcidMap.put(functionDef.getNodeId(),functionDef);
			return null;
		}
		
		// it's a function (i.e., not inside a class)
		else {
			// use A\B\foo as key for a function foo() in namespace \A\B
			String functionKey = functionDef.getName();
			if( !functionDef.getEnclosingNamespace().isEmpty())
				functionKey = functionDef.getEnclosingNamespace() + "\\" + functionKey;
		
			if( functionDefs.containsKey(functionKey)) {
				System.out.println("Function definition '" + functionKey + "' ambiguous: There are at least two known " +
						" matching function definitions (id " + functionDefs.get(functionKey).get(0).getNodeId() +
						" and id " + functionDef.getNodeId() + ")");
			}
			functionDefs.add(functionKey, functionDef);
			// mxy: save informations to map
			for(int i = 0;i < functionDef.getParameterList().size();i++){
				parameterMap.put(functionDef.getParameterList().getParameter(i).getNodeId(),
						new Pair<>(functionDef,i));
			}
			funcidMap.put(functionDef.getNodeId(),functionDef);
			//return functionDefs.put( functionKey, functionDef);
			return null;
		}		
	}
	
	/**
	 * Adds a new function call.
	 * 
	 * @param callExpression A PHP function/method/constructor call. An arbitrary number of
	 *                     distinguished calls to the same function/method/constructor can
	 *                     be added.
	 */
	public static boolean addFunctionCall( CallExpressionBase callExpression) {
		
		// Note: we cannot access any of the CallExpression's getter methods here
		// because this method is called from the PHPCSVNodeInterpreter at the point
		// where it constructs the CallExpression. That is, this method is called for each
		// CallExpression *immediately* after its construction. At that point, the PHPCSVNodeInterpreter
		// has not called the CallExpression's setter methods  (as it has not yet interpreted the
		// corresponding CSV lines).
		// Hence, we only store the references to the CallExpression objects themselves.
	
		if( callExpression instanceof StaticCallExpression)
			return staticMethodCalls.add( (StaticCallExpression)callExpression);
		else if( callExpression instanceof NewExpression)
			return constructorCalls.add( (NewExpression)callExpression);
		else if( callExpression instanceof MethodCallExpression)
			return nonStaticMethodCalls.add( (MethodCallExpression)callExpression);
		else
			return functionCalls.add( callExpression);
	}

	// mxy
	public static void setMode(String b){
		mode = b.equals("relax");
	}
	public static void addDDGinfo(Object use, Object def, String identifier){ DDGParent.add(use, new Pair<>(def, identifier)); }
	public static boolean addGlobal(GlobalStatement globalStatement){
		return globalStatements.add(globalStatement);
	}
	public static void addCfg(Long funcid, CFG cfg){
		cfgMap.put(funcid, cfg);
	}

	// mxy: handle Methods that are delayed
	public static void dealClassRelatedDef(){
		for(Method functionDef: delayMethods){
			// it's a static method
			if(functionDef.getFlags().contains(PHPCSVNodeTypes.FLAG_MODIFIER_STATIC)) {
				// use A\B\C::foo as key for a static method foo in class A\B\C
				String staticMethodKey = getEnclosingClass(functionDef) + "::" + functionDef.getName();
				if( !functionDef.getEnclosingNamespace().isEmpty())
					staticMethodKey = functionDef.getEnclosingNamespace() + "\\" + staticMethodKey;

				if( staticMethodDefs.containsKey(staticMethodKey)) {
					System.err.println("Static method definition '" + staticMethodKey + "' ambiguous: There are at least two known " +
							" matching static method definitions (id " + staticMethodDefs.get(staticMethodKey).getNodeId() +
							" and id " + functionDef.getNodeId() + ")");
				}
				funcidMap.put(functionDef.getNodeId(),functionDef);
				staticMethodDefs.put( staticMethodKey, functionDef);
			}

			// it's a constructor
			// Note that a PHP constructor cannot be static, so the previous case for static methods evaluates to false;
			// also note that there are two possible constructor names: __construct() (recommended) and ClassName() (legacy)
			else if(functionDef.getName().equals("__construct")
					|| functionDef.getName().equals(getEnclosingClass(functionDef))){
				// use A\B\C as key for the unique constructor of a class A\B\C
				String constructorKey = getEnclosingClass(functionDef);
				if( !functionDef.getEnclosingNamespace().isEmpty())
					constructorKey = functionDef.getEnclosingNamespace() + "\\" + constructorKey;

				if( constructorDefs.containsKey(constructorKey)) {
					System.err.println("Constructor definition for '" + constructorKey + "' ambiguous: There are at least two known " +
							" constructor definitions (id " + constructorDefs.get(constructorKey).getNodeId() +
							" and id " + functionDef.getNodeId() + ")");
				}

				constructorDefs.put( constructorKey, functionDef);
			}
		}

	}
	// mxy: get Method's enclosingclass name from classid (for ast.so 70 or higher)
	private static String getEnclosingClass(Method method){
		ClassDef classDef = PHPInheritFactory.getClassDef(method.getClassid());
		return classDef != null ? classDef.getName() : "";
	}

}
