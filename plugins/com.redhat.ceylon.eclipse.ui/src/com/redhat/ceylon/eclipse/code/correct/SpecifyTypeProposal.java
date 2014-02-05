package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CreateProposal.getDocument;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;

public class SpecifyTypeProposal extends ChangeCorrectionProposal {

    final int offset;
    final int length;
    final IFile file;
    
    SpecifyTypeProposal(int offset, IFile file, String type, TextFileChange change) {
        super("Specify type '" + type + "'", change);
        this.offset = offset;
        length = type.length();
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addSpecifyTypeProposal(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IFile file) {
        proposals.add(create(cu, node, file));
    }

	public static SpecifyTypeProposal create(Tree.CompilationUnit cu,
			Node node, IFile file) {
		final Tree.Type type = (Tree.Type) node;
        TextFileChange change = new TextFileChange("Specify Type", file);
        change.setEdit(new MultiTextEdit());
        Integer offset = node.getStartIndex();
        ProducedType infType = inferType(cu, type);
        String explicitType;
        int il;
        if (infType==null || infType.containsUnknowns()) {
            explicitType = "Object";
            il = 0;
        } 
        else {
        	explicitType = infType.getProducedTypeName();
            HashSet<Declaration> decs = new HashSet<Declaration>();
			importType(decs, infType, cu);
			il = applyImports(change, decs, cu, getDocument(change));
        }
        change.addEdit(new ReplaceEdit(offset, type.getText().length(), explicitType)); 
            //Note: don't use problem.getLength() because it's wrong from the problem list
        return new SpecifyTypeProposal(offset+il, file, explicitType, change);
	}

    static ProducedType inferType(Tree.CompilationUnit cu,
            final Tree.Type type) {
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
        return itv.inferredType;
    }
    
}
