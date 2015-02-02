package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.defaultValue;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importTypes;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.LOCAL_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.LOCAL_METHOD;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AssignmentOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierStatement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.UnaryOperatorExpression;

class ValueFunctionDefinitionGenerator extends DefinitionGenerator {
    
    private final String brokenName;
    private final MemberOrTypeExpression node;
    private final CompilationUnit rootNode;
    private final String desc;
    private final Image image;
    private final ProducedType returnType;
    private final LinkedHashMap<String, ProducedType> parameters;
    private final Boolean isVariable;
    
    @Override
    String getBrokenName() {
        return brokenName;
    }
    
    @Override
    ProducedType getReturnType() {
        return returnType;
    }
    
    @Override
    LinkedHashMap<String, ProducedType> getParameters() {
        return parameters;
    }
    
    @Override
    String getDescription() {
        return desc;
    }
    
    @Override
    Image getImage() {
        return image;
    }

    @Override
    Tree.CompilationUnit getRootNode() {
        return rootNode;
    }

    @Override
    Node getNode() {
        return node;
    }

    private ValueFunctionDefinitionGenerator(String brokenName, 
            Tree.MemberOrTypeExpression node, 
            Tree.CompilationUnit rootNode,
            String desc,
            Image image,
            ProducedType returnType,
            LinkedHashMap<String, ProducedType> paramTypes,
            Boolean isVariable) {
        this.brokenName = brokenName;
        this.node = node;
        this.rootNode = rootNode;
        this.desc = desc;
        this.image = image;
        this.returnType = returnType;
        this.parameters = paramTypes;
        this.isVariable = isVariable;
    }
        
    String generateShared(String indent, String delim) {
        return "shared " + generateInternal(indent, delim, false);
    }
    
    String generate(String indent, String delim) {
        return generateInternal(indent, delim, false);
    }
    
    String generateSharedFormal(String indent, String delim) {
        return "shared formal "+ generateInternal(indent, delim, true);
    }
    
    boolean isFormalSupported(){
        return true;
    }
    
    private String generateInternal(String indent, String delim, boolean isFormal) {
        StringBuffer def = new StringBuffer();
        boolean isVoid = returnType==null;
        Unit unit = node.getUnit();
        if (parameters!=null) {            
            List<TypeParameter> typeParams = new ArrayList<TypeParameter>();
            StringBuilder typeParamDef = new StringBuilder();
            StringBuilder typeParamConstDef = new StringBuilder();
            appendTypeParams(typeParams, typeParamDef, typeParamConstDef, returnType);
            appendTypeParams(typeParams, typeParamDef, typeParamConstDef, parameters.values());
            if (typeParamDef.length() > 0) {
                typeParamDef.insert(0, "<");
                typeParamDef.setLength(typeParamDef.length() - 1);
                typeParamDef.append(">");
            }

            if (isVoid) {
                def.append("void");
            }
            else {
                if (isTypeUnknown(returnType)) {
                    def.append("function");
                }
                else {
                    def.append(returnType.getProducedTypeName(unit));
                }
            }
            def.append(" ")
            .append(brokenName).append(typeParamDef);
            appendParameters(parameters, def,
                    unit.getAnythingDeclaration());
            def.append(typeParamConstDef);
            if(isFormal){
                def.append(";");
            }
            else if (isVoid) {
                def.append(" {}");
            }
            else {
                //removed because it's ugly for parameters:
                //delim + indent + defIndent + defIndent +
                def.append(" => ")
                .append(defaultValue(unit, returnType))
                .append(";");
            }
        }
        else {
            if(isVariable){
                def.append("variable ");
            }
            if (isVoid) {
                def.append("Anything");
            }
            else {
                if (isTypeUnknown(returnType)) {
                    def.append("value");
                }
                else {
                    def.append(returnType.getProducedTypeName(unit));
                }
            }
            def.append(" ")
               .append(brokenName);
            if(!isFormal){
                def.append(" = ")
                   .append(defaultValue(unit, returnType));
            }
            def.append(";");
        }
        return def.toString();
    }
    
    Set<Declaration> getImports() {
        Set<Declaration> imports = new HashSet<Declaration>();
        importType(imports, returnType, rootNode);
        if (parameters!=null) {
            importTypes(imports, parameters.values(), rootNode);
        }
        return imports;
    }
    
    static ValueFunctionDefinitionGenerator create(String brokenName, 
            Tree.MemberOrTypeExpression node, 
            Tree.CompilationUnit rootNode) {
        boolean isUpperCase = Character.isUpperCase(brokenName.charAt(0));
        if (isUpperCase) return null;
        FindValueFunctionVisitor fav = new FindValueFunctionVisitor(node);
        rootNode.visit(fav);
        ProducedType et = fav.expectedType;
        final boolean isVoid = et==null;
        ProducedType returnType = isVoid ? null : node.getUnit().denotableType(et);
        StringBuilder params = new StringBuilder();
        LinkedHashMap<String, ProducedType> paramTypes = getParameters(fav);
        if (paramTypes!=null) {         
            String desc = "function '" + brokenName + params + "'";
            return new ValueFunctionDefinitionGenerator(brokenName, node, rootNode, 
                    desc, LOCAL_METHOD, returnType, paramTypes, null);
        }
        else {
            String desc = "value '" + brokenName + "'";
            return new ValueFunctionDefinitionGenerator(brokenName, node, rootNode, 
                    desc, LOCAL_ATTRIBUTE, returnType, null, fav.isVariable);
        }
    }
    
    private static class FindValueFunctionVisitor extends FindArgumentsVisitor{

        boolean isVariable = false;
        
        FindValueFunctionVisitor(MemberOrTypeExpression smte) {
            super(smte);
        }
        
        @Override
        public void visit(AssignmentOp that) {
            isVariable = ((Tree.AssignmentOp) that).getLeftTerm() == smte;
            super.visit(that);
        }
        
        @Override
        public void visit(UnaryOperatorExpression that) {
            isVariable = ((Tree.UnaryOperatorExpression) that).getTerm() == smte;
            super.visit(that);
        }

        @Override
        public void visit(SpecifierStatement that) {
            isVariable = ((Tree.SpecifierStatement) that).getBaseMemberExpression() == smte;
            super.visit(that);
        }
        
    }

}