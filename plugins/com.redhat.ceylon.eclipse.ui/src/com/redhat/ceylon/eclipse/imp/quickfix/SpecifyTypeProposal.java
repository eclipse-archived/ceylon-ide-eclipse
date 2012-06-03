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

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
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
    
    static void addSpecifyTypeProposal(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IFile file) {
        final Tree.Type type = (Tree.Type) node;
        InferTypeVisitor itv = new InferTypeVisitor() {
            { unit = type.getUnit(); }
            @Override public void visit(Tree.TypedDeclaration that) {
                if (that.getType()==type) {
                    dec = that.getDeclarationModel();
                    union(that.getType().getTypeModel());
                }
                super.visit(that);
            }            
        };
        itv.visit(cu);
        ProducedType it = itv.inferredType;
        String explicitType = it==null ? "Object" : node.getUnit().denotableType(it).getProducedTypeName();
        TextFileChange change =  new TextFileChange("Specify Type", file);
        change.setEdit(new ReplaceEdit(problem.getOffset(), type.getText().length(), 
                explicitType)); //Note: don't use problem.getLength() because it's wrong from the problem list
        proposals.add(new SpecifyTypeProposal(problem, file, explicitType, change));
    }
    
}
