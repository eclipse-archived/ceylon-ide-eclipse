package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ClassDefinition;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraint;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraintList;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;

public class AddConstraintSatisfiesProposalOnExpressionIsNotAssignable extends ChangeCorrectionProposal {
    
    public static void addConstraintSatisfiesProposals(CompilationUnit cu, Node node, Collection<ICompletionProposal> proposals, IProject project) {
        Term term = determineTreeTerm(node);
        
        TypeParameter typeParam = determineTypeParam(term);
        if( typeParam == null ) {
            return;
        }
        
        ProducedType missingSatisfiedType = determineMissingSatisfiedType(cu, term);
        if( missingSatisfiedType == null || 
            missingSatisfiedType.getDeclaration() == null || 
            !(missingSatisfiedType.getDeclaration() instanceof ClassOrInterface) ) {
            return;
        }
        
        for (PhasedUnit unit: getUnits(project)) {
            if (typeParam.getUnit().equals(unit.getUnit())) {
                Node typeParamCont = determineTypeParamContainer(cu, typeParam);

                if( typeParamCont instanceof Tree.ClassDefinition ) {
                    Tree.ClassDefinition classDefinition = (ClassDefinition) typeParamCont;
                    addConstraintSatisfiesProposals(typeParam, missingSatisfiedType, unit, proposals, classDefinition.getTypeConstraintList(), classDefinition.getClassBody().getStartIndex());
                }
                else if( typeParamCont instanceof Tree.InterfaceDefinition ) {
                    Tree.InterfaceDefinition interfaceDefinition = (Tree.InterfaceDefinition) typeParamCont;
                    addConstraintSatisfiesProposals(typeParam, missingSatisfiedType, unit, proposals, interfaceDefinition.getTypeConstraintList(), interfaceDefinition.getInterfaceBody().getStartIndex());
                }
                else if( typeParamCont instanceof Tree.MethodDefinition ) {
                    Tree.MethodDefinition methodDefinition = (Tree.MethodDefinition)typeParamCont;
                    addConstraintSatisfiesProposals(typeParam, missingSatisfiedType, unit, proposals, methodDefinition.getTypeConstraintList(), methodDefinition.getBlock().getStartIndex());
                }
                break;
            }
        }
    }

    private static void addConstraintSatisfiesProposals(TypeParameter typeParam, ProducedType missingSatisfiedType, PhasedUnit unit, Collection<ICompletionProposal> proposals, TypeConstraintList typeConstraints, Integer typeContainerBodyStartIndex) {
        String changeText = null;
        Integer changeIndex = null;

        if( typeConstraints != null ) {
            for( TypeConstraint typeConstraint : typeConstraints.getTypeConstraints() ) {
                if( typeConstraint.getDeclarationModel().equals(typeParam) ) {
                    changeText = " & " + missingSatisfiedType.getProducedTypeName();
                    changeIndex = typeConstraint.getStopIndex() + 1;
                    break;
                }
            }
        }
        if( changeText == null ) {
            changeText = "given "+ typeParam.getName() + " satisfies " + missingSatisfiedType.getProducedTypeName() + " ";
            changeIndex = typeContainerBodyStartIndex;
        }
        if( changeText != null ) {
            IFile file = CeylonBuilder.getFile(unit);
            TextFileChange change = new TextFileChange("Add generic type constraints", file);
            change.setEdit(new InsertEdit(changeIndex, changeText));
            AddConstraintSatisfiesProposalOnExpressionIsNotAssignable p = new AddConstraintSatisfiesProposalOnExpressionIsNotAssignable(typeParam, missingSatisfiedType, changeIndex, file, change);
            if ( !proposals.contains(p)) {
                proposals.add(p);
            }                               
        }
    }

    private static Term determineTreeTerm(Node node) {
        if (node instanceof Tree.SpecifierExpression) {
            node = ((Tree.SpecifierExpression) node).getExpression();
        }
        if (node instanceof Tree.Expression) {
            node = ((Tree.Expression) node).getTerm();
        }
        if (node instanceof Tree.Term) {
            return (Term) node;
        }
        return null;
    }

    private static TypeParameter determineTypeParam(Term term) {
        if (term != null) {
            ProducedType type = term.getUnit().denotableType(term.getTypeModel());
            if (type != null && type.getDeclaration() instanceof TypeParameter) {
                return (TypeParameter) type.getDeclaration();
            }
        }
        return null;
    }

    private static Node determineTypeParamContainer(CompilationUnit cu, TypeParameter typeParam) {
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

    private static ProducedType determineMissingSatisfiedType(CompilationUnit cu, Term term) {
        FindInvocationVisitor fav = new FindInvocationVisitor(term);
        fav.visit(cu);
        if( fav.parameter != null ) {
            return fav.parameter.getType();
        }
        return null;
    }

    private int offset; 
    private IFile file;
    private TypeParameter typeParam;
    private ProducedType missingSatisfiedType;
    
    private AddConstraintSatisfiesProposalOnExpressionIsNotAssignable(TypeParameter typeParam, ProducedType missingSatisfiedType, int offset, IFile file, TextFileChange change) {
        super("Add generic type constraints '" + typeParam.getName() + " satisfies " + missingSatisfiedType.getProducedTypeName() + "'", 
                change, 10, CORRECTION);
        this.file=file;
        this.offset=offset;
        this.typeParam = typeParam;
        this.missingSatisfiedType = missingSatisfiedType;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddConstraintSatisfiesProposalOnExpressionIsNotAssignable) {
            AddConstraintSatisfiesProposalOnExpressionIsNotAssignable that = (AddConstraintSatisfiesProposalOnExpressionIsNotAssignable) obj;
            return that.typeParam.equals(typeParam) && that.missingSatisfiedType.equals(missingSatisfiedType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return typeParam.hashCode() + missingSatisfiedType.hashCode();
    }

}
