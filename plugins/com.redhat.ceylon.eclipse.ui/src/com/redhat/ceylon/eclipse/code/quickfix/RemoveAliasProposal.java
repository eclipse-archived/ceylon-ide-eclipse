package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

class RemoveAliasProposal extends ChangeCorrectionProposal {
        
    RemoveAliasProposal(IFile file, Declaration dec, TextFileChange change) {
        super("Remove alias of '" + dec.getName() + "'", change, 10, CORRECTION);
    }
    
    static void addRemoveAliasProposal(Tree.ImportMemberOrType node,  
            Collection<ICompletionProposal> proposals, 
            final Declaration dec, IFile file, Tree.CompilationUnit cu, 
            CeylonEditor editor) {
        final TextFileChange change =  new TextFileChange("Remove Alias", file);
        change.setEdit(new MultiTextEdit());
        final Identifier aid = node.getAlias().getIdentifier();
        change.addEdit(new DeleteEdit(aid.getStartIndex(), 
                node.getIdentifier().getStartIndex()-aid.getStartIndex()));
        editor.getParseController().getRootNode().visit(new Visitor() {
            @Override
            public void visit(BaseMemberOrTypeExpression that) {
                super.visit(that);
                addLinkedPosition(that.getIdentifier(), 
                        that.getDeclaration());
            }
            @Override
            public void visit(BaseType that) {
                super.visit(that);
                addLinkedPosition(that.getIdentifier(), 
                        that.getDeclarationModel());
            }
            protected void addLinkedPosition(Identifier id, Declaration d) {
                if (id!=null && d!=null && dec.equals(d) && 
                        id.getText().equals(aid.getText())) {
                    change.addEdit(new ReplaceEdit(id.getStartIndex(), 
                            id.getText().length(), dec.getName()));
                }
            }
        });
        proposals.add(new RemoveAliasProposal(file, dec, change));
    }
    
}
