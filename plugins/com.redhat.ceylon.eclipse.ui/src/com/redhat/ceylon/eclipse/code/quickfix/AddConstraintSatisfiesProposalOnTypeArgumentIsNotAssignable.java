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
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class AddConstraintSatisfiesProposalOnTypeArgumentIsNotAssignable extends ChangeCorrectionProposal {

    public static void addConstraintSatisfiesProposals(Tree.CompilationUnit cu, Node node, Collection<ICompletionProposal> proposals, IProject project) {
        TypeParameter typeParam = determineTypeParam(node);
        if( typeParam == null ) {
            return;
        }
        
        Tree.ClassOrInterface typeParamContainer = determineTypeParamContainer(cu, node);
        if (!(typeParamContainer instanceof Tree.ClassDefinition) && !(typeParamContainer instanceof Tree.InterfaceDefinition)) {
            return;
        }
    
        List<TypeParameter> stTypeParams = determineSatisfiedTypesTypeParams(typeParam, typeParamContainer);
        if (stTypeParams.isEmpty()) {
            return;
        }
        
        List<ProducedType> missingSatisfiedTypes = determineMissingSatisfiedTypes(typeParam, stTypeParams);
        if( missingSatisfiedTypes.isEmpty() ) {
            return;
        }
        
        String changeText = createMissingSatisfiedTypesText(typeParam, stTypeParams, missingSatisfiedTypes);
        TextFileChange change = createMissingSatisfiedTypesChange(typeParam, typeParamContainer, changeText, project);
        
        AddConstraintSatisfiesProposalOnTypeArgumentIsNotAssignable p = new AddConstraintSatisfiesProposalOnTypeArgumentIsNotAssignable(typeParam, changeText, change);
        if ( !proposals.contains(p)) {
            proposals.add(p);
        }                               
    }

    private static TypeParameter determineTypeParam(Node node) {
        TypeParameter typeParameter = null;
        if (node instanceof Tree.BaseType) {
            TypeDeclaration baseTypeDecl = ((Tree.BaseType) node).getDeclarationModel();
            if (baseTypeDecl instanceof TypeParameter) {
                typeParameter = (TypeParameter) baseTypeDecl;
            }
        }
        if (node instanceof Tree.TypeParameterDeclaration) {
            typeParameter = ((Tree.TypeParameterDeclaration) node).getDeclarationModel();
        }
        return typeParameter;
    }
    
    private static Tree.ClassOrInterface determineTypeParamContainer(Tree.CompilationUnit cu, Node typeParamNode) {
        FindContainerVisitor fcv = new FindContainerVisitor(typeParamNode);
        fcv.visit(cu);
        if (fcv.getStatementOrArgument() instanceof Tree.ClassOrInterface) {
            return (Tree.ClassOrInterface) fcv.getStatementOrArgument();
        }
        return null;        
    }
    
    private static List<TypeParameter> determineSatisfiedTypesTypeParams(TypeParameter typeParam, Tree.ClassOrInterface typeParamContainer) {
        List<TypeParameter> stTypeParams = new ArrayList<TypeParameter>();
        if( typeParamContainer.getSatisfiedTypes() != null ) {
            for( Tree.SimpleType st : typeParamContainer.getSatisfiedTypes().getTypes() ) {
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
        }
        return stTypeParams;
    }
    
    private static List<ProducedType> determineMissingSatisfiedTypes(TypeParameter typeParam, List<TypeParameter> stTypeParams) {
        ProducedType typeParamType = typeParam.getType();
        Map<TypeParameter, ProducedType> substitutions = new HashMap<>();
        for (TypeParameter stTypeParam : stTypeParams) {
            substitutions.put(stTypeParam, typeParamType);
        }
        
        List<ProducedType> missingSatisfiedTypes = new ArrayList<ProducedType>();
        
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
        
        return missingSatisfiedTypes;
    }

    private static String createMissingSatisfiedTypesText(TypeParameter typeParam, List<TypeParameter> stTypeParams, List<ProducedType> missingSatisfiedTypes) {
        StringBuffer missingSatisfiedTypesText = new StringBuffer();
        for( ProducedType missingSatisfiedType : missingSatisfiedTypes ) {
            if( missingSatisfiedTypesText.length() != 0 ) {
                missingSatisfiedTypesText.append(" & ");
            }
            missingSatisfiedTypesText.append(missingSatisfiedType.getProducedTypeName());   
        }
        return missingSatisfiedTypesText.toString();
    }

    private static TextFileChange createMissingSatisfiedTypesChange(TypeParameter typeParam, Tree.ClassOrInterface typeParamContainer, String missingSatisfiedTypesText, IProject project) {
        String changeText = null;
        Integer changeIndex = null;

        if( typeParamContainer.getTypeConstraintList() != null ) {
            for( Tree.TypeConstraint typeConstraint : typeParamContainer.getTypeConstraintList().getTypeConstraints() ) {
                if( typeConstraint.getDeclarationModel().equals(typeParam) ) {
                    changeText = " & " + missingSatisfiedTypesText;
                    changeIndex = typeConstraint.getStopIndex() + 1;
                    break;
                }
            }
        }
        if( changeText == null ) {
            changeText = "given "+ typeParam.getName() + " satisfies " + missingSatisfiedTypesText + " ";
            
            if( typeParamContainer instanceof Tree.ClassDefinition ) {
                changeIndex = ((Tree.ClassDefinition)typeParamContainer).getClassBody().getStartIndex();
            }
            if( typeParamContainer instanceof Tree.InterfaceDefinition ) {
                changeIndex = ((Tree.InterfaceDefinition)typeParamContainer).getInterfaceBody().getStartIndex();
            }
        }
        
        for (PhasedUnit unit: getUnits(project)) {
            if (typeParam.getUnit().equals(unit.getUnit())) {
                if( changeText != null ) {
                    IFile file = CeylonBuilder.getFile(unit);
                    TextFileChange change = new TextFileChange("Add generic type constraints", file);
                    change.setEdit(new InsertEdit(changeIndex, changeText));
                    return change;
                }
                break;   
            }
        }
        
        return null;
    }

    private TextFileChange change;
    private TypeParameter typeParam;
    private String missingSatisfiedTypeText;

    private AddConstraintSatisfiesProposalOnTypeArgumentIsNotAssignable(TypeParameter typeParam, String missingSatisfiedTypeText, TextFileChange change) {
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
        if (obj instanceof AddConstraintSatisfiesProposalOnTypeArgumentIsNotAssignable) {
            AddConstraintSatisfiesProposalOnTypeArgumentIsNotAssignable that = (AddConstraintSatisfiesProposalOnTypeArgumentIsNotAssignable) obj;
            return that.typeParam.equals(typeParam) && that.missingSatisfiedTypeText.equals(missingSatisfiedTypeText);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return typeParam.hashCode() + missingSatisfiedTypeText.hashCode();
    }

}