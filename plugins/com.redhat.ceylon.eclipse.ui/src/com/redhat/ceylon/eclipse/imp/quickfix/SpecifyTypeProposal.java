package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.editor.Util;

class SpecifyTypeProposal extends ChangeCorrectionProposal {

    final int offset;
    final int length;
    final IFile file;
    
    SpecifyTypeProposal(ProblemLocation problem, IFile file, 
            String type, TextFileChange change) {
        super("Specify type '" + type + "'", change, 10, CORRECTION);
        offset = problem.getOffset();
        length = type.length();
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addSpecifyTypeProposal(Tree.CompilationUnit cu, Node node, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file) {
        final Tree.Type type = (Tree.Type) node;
        class InferTypeVisitor extends Visitor {
            Declaration dec;
            ProducedType inferredType;
            @Override public void visit(Tree.TypedDeclaration that) {
                super.visit(that);
                if (that.getType()==type) {
                    dec = that.getDeclarationModel();
                    inferredType = type.getTypeModel();
                }
            }
            @Override public void visit(Tree.SpecifierStatement that) {
                super.visit(that);
                Tree.Term bme = that.getBaseMemberExpression();
                if (bme instanceof Tree.BaseMemberExpression) {
                	if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                		inferredType = that.getSpecifierExpression().getExpression().getTypeModel();
                	}
                }
            }
            @Override public void visit(Tree.AssignmentOp that) {
                super.visit(that);
                if (that.getLeftTerm() instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = (Tree.BaseMemberExpression) that.getLeftTerm();
                    if (bme.getDeclaration().equals(dec)) {
                        //TODO: take a union if there are multiple assignments
                        inferredType = that.getRightTerm().getTypeModel();
                    }
                }
            }
        }
        InferTypeVisitor itv = new InferTypeVisitor();
        itv.visit(cu);
        String explicitType = itv.inferredType.getProducedTypeName();
        TextFileChange change =  new TextFileChange("Specify Type", file);
        change.setEdit(new ReplaceEdit(problem.getOffset(), type.getText().length(), 
                explicitType)); //Note: don't use problem.getLength() because it's wrong from the problem list
        proposals.add(new SpecifyTypeProposal(problem, file, explicitType, change));
    }
    
}