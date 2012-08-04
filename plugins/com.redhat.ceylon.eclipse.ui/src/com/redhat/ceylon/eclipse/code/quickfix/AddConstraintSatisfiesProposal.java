package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ClassDefinition;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SimpleType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraint;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraintList;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;

/**
 * Add generic type constraints proposal for following code:
 * <pre>
 * interface Foo&lt;T&gt; given T satisfies Comparable&lt;T&gt; {}
 * 
 * class Bar&lt;T&gt;() satisfies Foo&lt;T&gt; {}
 * 
 * ------
 * 
 * void foo&lt;T&gt;(T t) {
 *     Bar b = t;
 * }
 * 
 * ------
 * 
 * void foo&lt;T&gt;(T t) {
 *     Entry&lt;Integer, T&gt; e;
 * }
 * </pre>
 */
public class AddConstraintSatisfiesProposal extends ChangeCorrectionProposal {

    public static void addConstraintSatisfiesProposals(Tree.CompilationUnit cu, Node node, Collection<ICompletionProposal> proposals, IProject project) {
        node = determineNode(node);
        if( node == null ) {
            return;
        }

        TypeParameter typeParam = determineTypeParam(node);
        if( typeParam == null ) {
            return;
        }

        List<ProducedType> missingSatisfiedTypes = determineMissingSatisfiedTypes(cu, node, typeParam);
        if( missingSatisfiedTypes.isEmpty() ) {
            return;
        }

        String changeText = createMissingSatisfiedTypesText(typeParam, missingSatisfiedTypes);

        for (PhasedUnit unit: getUnits(project)) {
            if (typeParam.getUnit().equals(unit.getUnit())) {
                Node typeParamCont = determineTypeParamCont(cu, typeParam);

                if( typeParamCont instanceof Tree.ClassDefinition ) {
                    Tree.ClassDefinition classDefinition = (ClassDefinition) typeParamCont;
                    addConstraintSatisfiesProposals(typeParam, changeText, unit, proposals, classDefinition.getTypeConstraintList(), classDefinition.getClassBody().getStartIndex());
                }
                else if( typeParamCont instanceof Tree.InterfaceDefinition ) {
                    Tree.InterfaceDefinition interfaceDefinition = (Tree.InterfaceDefinition) typeParamCont;
                    addConstraintSatisfiesProposals(typeParam, changeText, unit, proposals, interfaceDefinition.getTypeConstraintList(), interfaceDefinition.getInterfaceBody().getStartIndex());
                }
                else if( typeParamCont instanceof Tree.MethodDefinition ) {
                    Tree.MethodDefinition methodDefinition = (Tree.MethodDefinition)typeParamCont;
                    addConstraintSatisfiesProposals(typeParam, changeText, unit, proposals, methodDefinition.getTypeConstraintList(), methodDefinition.getBlock().getStartIndex());
                }
                break;
            }
        }        
    }

    private static void addConstraintSatisfiesProposals(TypeParameter typeParam, String missingSatisfiedType, PhasedUnit unit, Collection<ICompletionProposal> proposals, TypeConstraintList typeConstraints, Integer typeContainerBodyStartIndex) {
        String changeText = null;
        Integer changeIndex = null;
    
        if( typeConstraints != null ) {
            for( TypeConstraint typeConstraint : typeConstraints.getTypeConstraints() ) {
                if( typeConstraint.getDeclarationModel().equals(typeParam) ) {
                    changeText = " & " + missingSatisfiedType;
                    changeIndex = typeConstraint.getStopIndex() + 1;
                    break;
                }
            }
        }
        if( changeText == null ) {
            changeText = "given "+ typeParam.getName() + " satisfies " + missingSatisfiedType + " ";
            changeIndex = typeContainerBodyStartIndex;
        }
        if( changeText != null ) {
            IFile file = CeylonBuilder.getFile(unit);
            TextFileChange change = new TextFileChange("Add generic type constraints", file);
            change.setEdit(new InsertEdit(changeIndex, changeText));
            AddConstraintSatisfiesProposal p = new AddConstraintSatisfiesProposal(typeParam, missingSatisfiedType, change);
            if ( !proposals.contains(p)) {
                proposals.add(p);
            }                               
        }
    }

    private static Node determineNode(Node node) {
        if (node instanceof Tree.SpecifierExpression) {
            node = ((Tree.SpecifierExpression) node).getExpression();
        }
        if (node instanceof Tree.Expression) {
            node = ((Tree.Expression) node).getTerm();
        }
        return node;
    }

    private static TypeParameter determineTypeParam(Node node) {
        TypeParameter typeParam = null;
        if (node instanceof Tree.TypeParameterDeclaration) {
            typeParam = ((Tree.TypeParameterDeclaration) node).getDeclarationModel();
        }
        else if (node instanceof Tree.BaseType) {
            TypeDeclaration baseTypeDecl = ((Tree.BaseType) node).getDeclarationModel();
            if (baseTypeDecl instanceof TypeParameter) {
                typeParam = (TypeParameter) baseTypeDecl;
            }
        }
        else if (node instanceof Tree.Term) {
            ProducedType type = node.getUnit().denotableType(((Tree.Term)node).getTypeModel());
            if (type != null && type.getDeclaration() instanceof TypeParameter) {
                typeParam = (TypeParameter) type.getDeclaration();
            }
        }
        return typeParam;
    }

    private static Node determineTypeParamCont(CompilationUnit cu, TypeParameter typeParam) {
        FindDeclarationVisitor fdv = new FindDeclarationVisitor(typeParam);
        fdv.visit(cu);
        Tree.Declaration typeParamDecl = fdv.getDeclarationNode();
        if (typeParamDecl != null) {
            FindContainerVisitor fcv = new FindContainerVisitor(typeParamDecl);
            fcv.visit(cu);
            return fcv.getStatementOrArgument();
        }
        return null;
    }

    private static List<ProducedType> determineMissingSatisfiedTypes(CompilationUnit cu, Node node, TypeParameter typeParam) {
        List<ProducedType> missingSatisfiedTypes = new ArrayList<>();
    
        if( node instanceof Tree.Term ) {
            FindInvocationVisitor fav = new FindInvocationVisitor(node);
            fav.visit(cu);
            if( fav.parameter != null ) {
                ProducedType type = fav.parameter.getType();
                if( type != null && type.getDeclaration() != null && type.getDeclaration() instanceof ClassOrInterface ) {
                    missingSatisfiedTypes.add(type);
                }
            }
        }
        else {
            List<TypeParameter> stTypeParams = determineSatisfiedTypesTypeParams(cu, node, typeParam);
            if (!stTypeParams.isEmpty()) {
                ProducedType typeParamType = typeParam.getType();
                Map<TypeParameter, ProducedType> substitutions = new HashMap<>();
                for (TypeParameter stTypeParam : stTypeParams) {
                    substitutions.put(stTypeParam, typeParamType);
                }
    
                for(TypeParameter stTypeParam : stTypeParams) {
                    for(ProducedType stTypeParamSatisfiedType : stTypeParam.getSatisfiedTypes()) {
                        stTypeParamSatisfiedType = stTypeParamSatisfiedType.substitute(substitutions);
    
                        boolean isMissing = true;
    
                        for (ProducedType typeParamSatisfiedType : typeParam.getSatisfiedTypes()) {
                            if (stTypeParamSatisfiedType.isSupertypeOf(typeParamSatisfiedType)) {
                                isMissing = false;
                                break;
                            }
                        }
    
                        if( isMissing ) {
                            for(ProducedType missingSatisfiedType : missingSatisfiedTypes) {
                                if( missingSatisfiedType.isExactly(stTypeParamSatisfiedType) ) {
                                    isMissing = false;
                                    break;
                                }
                            }
                        }
    
                        if( isMissing ) {
                            missingSatisfiedTypes.add(stTypeParamSatisfiedType);
                        }
                    }
                }
            }
    
        }
    
        return missingSatisfiedTypes;
    }

    private static List<TypeParameter> determineSatisfiedTypesTypeParams(Tree.CompilationUnit cu, Node typeParamNode, TypeParameter typeParam) {
        List<TypeParameter> stTypeParams = new ArrayList<TypeParameter>();

        FindContainerVisitor fcv = new FindContainerVisitor(typeParamNode);
        fcv.visit(cu);

        if (fcv.getStatementOrArgument() instanceof Tree.ClassOrInterface) {
            Tree.ClassOrInterface coi = (Tree.ClassOrInterface) fcv.getStatementOrArgument();
            if( coi.getSatisfiedTypes() != null ) {
                for( Tree.SimpleType st : coi.getSatisfiedTypes().getTypes() ) {
                    determineSatisfiedTypesTypeParams(typeParam, st, stTypeParams);
                }
            }
        }
        else if( fcv.getStatementOrArgument() instanceof Tree.AttributeDeclaration ) {
            Tree.AttributeDeclaration ad = (Tree.AttributeDeclaration) fcv.getStatementOrArgument();
            Tree.SimpleType st = (SimpleType) ad.getType();
            determineSatisfiedTypesTypeParams(typeParam, st, stTypeParams);
        }

        return stTypeParams;
    }

    private static void determineSatisfiedTypesTypeParams(TypeParameter typeParam, Tree.SimpleType st, List<TypeParameter> stTypeParams) {
        if( st.getTypeArgumentList() != null ) {
            List<Tree.Type> stTypeArguments = st.getTypeArgumentList().getTypes();
            for (int i = 0; i < stTypeArguments.size(); i++) {
                Tree.SimpleType stTypeArgument = (Tree.SimpleType)stTypeArguments.get(i);
                if (typeParam.getName().equals(
                        stTypeArgument.getDeclarationModel().getName())) {
                    TypeDeclaration stDecl = st.getDeclarationModel();
                    if( stDecl != null ) {
                        if( stDecl.getTypeParameters() != null && stDecl.getTypeParameters().size() > i ) {
                            stTypeParams.add(stDecl.getTypeParameters().get(i));
                        }
                    }                            
                }
            }                    
        }
    }

    private static String createMissingSatisfiedTypesText(TypeParameter typeParam, List<ProducedType> missingSatisfiedTypes) {
        StringBuffer missingSatisfiedTypesText = new StringBuffer();
        for( ProducedType missingSatisfiedType : missingSatisfiedTypes ) {
            if( missingSatisfiedTypesText.length() != 0 ) {
                missingSatisfiedTypesText.append(" & ");
            }
            missingSatisfiedTypesText.append(missingSatisfiedType.getProducedTypeName());   
        }
        return missingSatisfiedTypesText.toString();
    }

    private TextFileChange change;
    private TypeParameter typeParam;
    private String missingSatisfiedTypeText;

    private AddConstraintSatisfiesProposal(TypeParameter typeParam, String missingSatisfiedTypeText, TextFileChange change) {
        super("Add generic type constraints '" + typeParam.getName() + " satisfies " + missingSatisfiedTypeText + "'", change, 10, CORRECTION);
        this.change = change;
        this.typeParam = typeParam;
        this.missingSatisfiedTypeText = missingSatisfiedTypeText;
    }

    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(change.getFile(), change.getEdit().getOffset());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddConstraintSatisfiesProposal) {
            AddConstraintSatisfiesProposal that = (AddConstraintSatisfiesProposal) obj;
            return that.typeParam.equals(typeParam) && that.missingSatisfiedTypeText.equals(missingSatisfiedTypeText);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return typeParam.hashCode() + missingSatisfiedTypeText.hashCode();
    }

}