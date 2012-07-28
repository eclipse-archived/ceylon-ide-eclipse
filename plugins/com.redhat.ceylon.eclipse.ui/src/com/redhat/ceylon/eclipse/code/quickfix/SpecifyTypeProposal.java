package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;

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

class SpecifyTypeProposal extends ChangeCorrectionProposal {

    final int offset;
    final int length;
    final IFile file;
    
    SpecifyTypeProposal(int offset, IFile file, String type, TextFileChange change) {
        super("Specify type '" + type + "'", change, 10, CORRECTION);
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
        final Tree.Type type = (Tree.Type) node;
        TextFileChange change =  new TextFileChange("Specify Type", file);
        change.setEdit(new MultiTextEdit());
        Integer offset = node.getStartIndex();
        ProducedType infType = inferType(cu, type);
        String explicitType;
        int il;
        if (infType!=null) {
        	explicitType = infType.getProducedTypeName();
            il = importType(change, infType, cu, new HashSet<Declaration>());
        }
        else {
            explicitType = "Object";
            il = 0;
        }
        change.addEdit(new ReplaceEdit(offset, type.getText().length(), explicitType)); 
            //Note: don't use problem.getLength() because it's wrong from the problem list
        proposals.add(new SpecifyTypeProposal(offset+il, file, explicitType, change));
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
