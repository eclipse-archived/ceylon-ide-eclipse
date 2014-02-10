package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.REMOVE_CORR;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

class RemoveAliasProposal extends CorrectionProposal {
        
    protected static final class AliasRemovalVisitor extends Visitor {
        private final Declaration dec;
        private final TextFileChange change;
        private final Identifier aid;

        protected AliasRemovalVisitor(Declaration dec, TextFileChange change,
                Identifier aid) {
            this.dec = dec;
            this.change = change;
            this.aid = aid;
        }

        @Override
        public void visit(Tree.StaticMemberOrTypeExpression that) {
            super.visit(that);
            addRemoval(that.getIdentifier(), 
                    that.getDeclaration());
        }

        @Override
        public void visit(Tree.SimpleType that) {
            super.visit(that);
            addRemoval(that.getIdentifier(), 
                    that.getDeclarationModel());
        }

        @Override
        public void visit(Tree.MemberLiteral that) {
            super.visit(that);
            addRemoval(that.getIdentifier(), 
                    that.getDeclaration());
        }

        protected void addRemoval(Identifier id, Declaration d) {
            if (id!=null && d!=null && dec.equals(d) && 
                    id.getText().equals(aid.getText())) {
                change.addEdit(new ReplaceEdit(id.getStartIndex(), 
                        id.getText().length(), dec.getName()));
            }
        }
    }

    private RemoveAliasProposal(IFile file, Declaration dec, TextFileChange change) {
        super("Remove alias of '" + dec.getName() + "'", change, REMOVE_CORR);
    }
    
    static void addRemoveAliasProposal(Tree.ImportMemberOrType node,  
            Collection<ICompletionProposal> proposals, 
            final Declaration dec, IFile file, CeylonEditor editor) {
        final TextFileChange change =  new TextFileChange("Remove Alias", file);
        change.setEdit(new MultiTextEdit());
        final Identifier aid = node.getAlias().getIdentifier();
        change.addEdit(new DeleteEdit(aid.getStartIndex(), 
                node.getIdentifier().getStartIndex()-aid.getStartIndex()));
        editor.getParseController().getRootNode()
                .visit(new AliasRemovalVisitor(dec, change, aid));
        proposals.add(new RemoveAliasProposal(file, dec, change));
    }
    
}
