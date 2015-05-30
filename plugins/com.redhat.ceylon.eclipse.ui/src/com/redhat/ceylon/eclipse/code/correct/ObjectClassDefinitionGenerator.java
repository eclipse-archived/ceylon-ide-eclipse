package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.overloads;
import static com.redhat.ceylon.eclipse.code.complete.RefinementCompletionProposal.getRefinedProducedReference;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importTypes;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.LOCAL_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.LOCAL_CLASS;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.intersectionType;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Unit;

class ObjectClassDefinitionGenerator extends DefinitionGenerator {
    
    private final String brokenName;
    private final boolean isUpperCase;
    private final MemberOrTypeExpression node;
    private final CompilationUnit rootNode;
    private final String desc;
    private final Image image;
    private final Type returnType;
    private final LinkedHashMap<String, Type> parameters;
   

    @Override
    String getBrokenName() {
        return brokenName;
    }
    
    @Override
    Type getReturnType() {
        return returnType;
    }
    
    @Override
    LinkedHashMap<String, Type> getParameters() {
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

    private ObjectClassDefinitionGenerator(String brokenName, 
            Tree.MemberOrTypeExpression node, 
            Tree.CompilationUnit rootNode,
            String desc,
            Image image,
            Type returnType,
            LinkedHashMap<String, Type> paramTypes) {
        this.brokenName = brokenName;
        this.isUpperCase = Character.isUpperCase(brokenName.codePointAt(0));
        this.node = node;
        this.rootNode = rootNode;
        this.desc = desc;
        this.image = image;
        this.returnType = returnType;
        this.parameters = paramTypes;
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
        return isClassGenerator();
    }
    
    private String generateInternal(String indent, String delim, boolean isFormal) {
        StringBuffer def = new StringBuffer();
        boolean isVoid = returnType==null;
        if (isClassGenerator()) {
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
            String defIndent = getDefaultIndent();
            String supertype = isVoid ? 
                    null : supertypeDeclaration(returnType);
            def.append("class ").append(brokenName).append(typeParamDef);
            appendParameters(parameters, def, 
                    getDefaultedSupertype());
            if (supertype!=null) {
                def.append(delim).append(indent).append(defIndent).append(defIndent)
                    .append(supertype);
            }
            def.append(typeParamConstDef);
            def.append(" {").append(delim);
            if (!isVoid) {
                appendMembers(indent, delim, def, defIndent);
            }
            def.append(indent).append("}");            
        }
        else if (isObjectGenerator()) {
            String defIndent = getDefaultIndent();
            String supertype = isVoid ? 
                    null : supertypeDeclaration(returnType);
            def.append("object ").append(brokenName);
            if (supertype!=null) {
                def.append(delim).append(indent).append(defIndent).append(defIndent)
                    .append(supertype);
            }
            def.append(" {").append(delim);
            if (!isVoid) {
                appendMembers(indent, delim, def, defIndent);
            }
            def.append(indent).append("}");
        }
        else {
            return null;
        }
        return def.toString();
    }
    
    private boolean isClassGenerator(){
        return isUpperCase && parameters!=null;
    }
    
    private boolean isObjectGenerator(){
        return !isUpperCase && parameters==null;
    }
    
    Set<Declaration> getImports() {
        Set<Declaration> imports = new HashSet<Declaration>();
        importType(imports, returnType, rootNode);
        if (parameters!=null) {
            importTypes(imports, parameters.values(), rootNode);
        }
        if (returnType!=null) {
            importMembers(imports);            
        }
        return imports;
    }

    private void importMembers(Set<Declaration> imports) {
        //TODO: this is a major copy/paste from appendMembers() below
        TypeDeclaration td = getDefaultedSupertype();
        Set<String> ambiguousNames = new HashSet<String>();
        Collection<DeclarationWithProximity> members = 
                td.getMatchingMemberDeclarations(rootNode.getUnit(), 
                        null, "", 0).values();
        for (DeclarationWithProximity dwp: members) {
            Declaration dec = dwp.getDeclaration();
            for (Declaration d: overloads(dec)) {
                if (d.isFormal() /*&& td.isInheritedFromSupertype(d)*/) {
                    importSignatureTypes(d, rootNode, imports);
                    ambiguousNames.add(d.getName());
                }
            }
        }
        for (TypeDeclaration superType: td.getSupertypeDeclarations()) {
            for (Declaration m: superType.getMembers()) {
                if (m.isShared()) {
                    Declaration r = td.getMember(m.getName(), null, false);
                    if (r==null || 
                            !r.refines(m) && 
//                                !r.getContainer().equals(ut) && 
                            !ambiguousNames.add(m.getName())) {
                        importSignatureTypes(m, rootNode, imports);
                    }
                }
            }
        }
    }

    private void appendMembers(String indent, String delim, StringBuffer def,
            String defIndent) {
        TypeDeclaration td = getDefaultedSupertype();
        Set<String> ambiguousNames = new HashSet<String>();
        Collection<DeclarationWithProximity> members = 
                td.getMatchingMemberDeclarations(rootNode.getUnit(),
                        null, "", 0).values();
        for (DeclarationWithProximity dwp: members) {
            Declaration dec = dwp.getDeclaration();
            if (ambiguousNames.add(dec.getName())) {
                for (Declaration d: overloads(dec)) {
                    if (d.isFormal() /*&& td.isInheritedFromSupertype(d)*/) {
                        appendRefinementText(indent, delim, def,
                                defIndent, d);
                    }
                }
            }
        }
        for (TypeDeclaration superType: td.getSupertypeDeclarations()) {
            for (Declaration m: superType.getMembers()) {
                if (m.isShared()) {
                    Declaration r = td.getMember(m.getName(), null, false);
                    if ((r==null || 
                            !r.refines(m)) && 
//                            !r.getContainer().equals(ut)) && 
                            ambiguousNames.add(m.getName())) {
                        appendRefinementText(indent, delim, def,
                                defIndent, m);
                    }
                }
            }
        }
    }

    private TypeDeclaration getDefaultedSupertype() {
        if (isNotBasic(returnType)) {
            return returnType.getDeclaration();
        }
        else {
            Unit unit = rootNode.getUnit();
            return intersectionType(returnType, 
                    unit.getBasicDeclaration().getType(),
                    unit).getDeclaration();
        }
    }

    private void appendRefinementText(String indent, String delim,
            StringBuffer def, String defIndent, Declaration d) {
        Reference pr = 
                getRefinedProducedReference(returnType, d);
        String text = getRefinementTextFor(d, pr, node.getUnit(), 
                false, null, "", false);
        if (parameters.containsKey(d.getName())) {
            text = text.substring(0, text.indexOf(" =>")) + ";";
        }
        def.append(indent).append(defIndent).append(text).append(delim);
    }

    static ObjectClassDefinitionGenerator create(String brokenName, 
            Tree.MemberOrTypeExpression node, 
            Tree.CompilationUnit rootNode) {
        boolean isUpperCase = Character.isUpperCase(brokenName.charAt(0));
        FindArgumentsVisitor fav = new FindArgumentsVisitor(node);
        rootNode.visit(fav);
        Unit unit = node.getUnit();
        Type returnType = unit.denotableType(fav.expectedType);
        StringBuilder params = new StringBuilder();
        LinkedHashMap<String, Type> paramTypes = getParameters(fav);
        if (returnType!=null) {
            if(unit.isOptionalType(returnType)){
                returnType = returnType.eliminateNull();
            }
            TypeDeclaration rtd = returnType.getDeclaration();
            if ( (rtd instanceof Class) && (
                    rtd.equals(unit.getObjectDeclaration()) || 
                    rtd.equals(unit.getAnythingDeclaration()))
            ) {
                returnType = null;
            }
        }
        if (!isValidSupertype(returnType)) {
            return null;
        }
        if (paramTypes!=null && isUpperCase) {
            String supertype = supertypeDeclaration(returnType);
            if (supertype==null) supertype = "";
            String desc = "class '" + brokenName + params + supertype + "'";
            return new ObjectClassDefinitionGenerator(brokenName, node, rootNode, 
                    desc, LOCAL_CLASS, returnType, paramTypes);
        }
        else if (paramTypes==null && !isUpperCase) {
            String desc = "object '" + brokenName + "'";
            return new ObjectClassDefinitionGenerator(brokenName, node, rootNode, 
                    desc, LOCAL_ATTRIBUTE, returnType, null);
        }
        else {
            return null;
        }
    }

    private static String supertypeDeclaration(Type returnType) {
        if (isTypeUnknown(returnType)) {
            return null;
        }
        else {
            if (returnType.isClass()) {
                return " extends " + returnType.asString() + "()"; //TODO: supertype arguments!
            }
            else if (returnType.isInterface()) {
                return " satisfies " + returnType.asString();
            }
            else if (returnType.isIntersection()) {
                String extendsClause = "";
                StringBuilder satisfiesClause = new StringBuilder();
                for (Type st: returnType.getSatisfiedTypes()) {
                    if (st.isClass()) {
                        extendsClause = " extends " + st.asString() + "()"; //TODO: supertype arguments!
                    }
                    else if (st.isInterface()) {
                        if (satisfiesClause.length()==0) {
                            satisfiesClause.append(" satisfies ");
                        }
                        else {
                            satisfiesClause.append(" & ");
                        }
                        satisfiesClause.append(st.asString());
                    }
                }
                return extendsClause+satisfiesClause;
            }
            else {
                return null;
            }
        }
    }

    private static boolean isValidSupertype(Type returnType) {
        if (isTypeUnknown(returnType)) {
            return true;
        }
        else {
            if (returnType.getCaseTypes()!=null) {
                return false;
            }
            if (returnType.isClass()) {
                return !returnType.getDeclaration().isFinal();
            }
            else if (returnType.isInterface()) {
                Unit unit = returnType.getDeclaration().getUnit();
                return !returnType.getDeclaration()
                        .equals(unit.getCallableDeclaration());
            }
            else if (returnType.isIntersection()) {
                for (Type st: returnType.getSatisfiedTypes()) {
                    if (!isValidSupertype(st)) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
    }

    private static boolean isNotBasic(Type returnType) {
        if (isTypeUnknown(returnType)) {
            return false;
        }
        else {
            Unit unit = returnType.getDeclaration().getUnit();
            if (returnType.isClass()) {
                return returnType.getSupertype(unit.getBasicDeclaration())==null;
            }
            else if (returnType.isInterface()) {
                return false;
            }
            else if (returnType.isIntersection()) {
                for (Type st: returnType.getSatisfiedTypes()) {
                    if (st.isClass()) {
                        return returnType.getSupertype(unit.getBasicDeclaration())==null;
                    }
                }
                return false;
            }
            else {
                return false;
            }
        }
    }

}