package cg.fake;

import ast.php.functionDef.FunctionDef;
import inputModules.csv.csv2ast.ASTUnderConstruction;

public class FakeFunctionCreator {

    private FunctionDef newNode;

    private static final ASTUnderConstruction ast = new ASTUnderConstruction();


    public FunctionDef createFunctionDefNode(String functionKey, String funcid) {

        this.newNode = new FunctionDef();

        return this.newNode;
    }
}
