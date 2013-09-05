package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class UseAliasProposal extends ChangeCorrectionProposal {
    
    UseAliasProposal(IFile file, String alias, String name, 
            TextFileChange change) {
        super("Use alias '"+ alias + "' for '" + name + "'", 
                change, 10, CORRECTION);
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
    }
    
    static void addUseAliasProposal(Node node,  
            Collection<ICompletionProposal> proposals, 
            final Declaration dec, final String alias, IFile file, 
            Tree.CompilationUnit cu) {
        // better safe than throwing
        if(node.getStartIndex() == null || node.getStopIndex() == null)
            return;
        final TextFileChange change =  new TextFileChange("Use Alias", file);
        change.setEdit(new MultiTextEdit());
        cu.visit(new Visitor() {
            @Override
            public void visit(BaseMemberOrTypeExpression that) {
                super.visit(that);
//                if (dec.equals(that.getDeclaration())) {
                Tree.Identifier id = that.getIdentifier();
                if (id!=null && that.getDeclaration()==null && 
                        dec.getName().equals(id.getText())) {
                    change.addEdit(new ReplaceEdit(id.getStartIndex(), 
                            dec.getName().length(), alias));
                }
            }
            @Override
            public void visit(BaseType that) {
                super.visit(that);
//                if (dec.equals(that.getDeclarationModel())) {
                Tree.Identifier id = that.getIdentifier();
                if (id!=null && that.getDeclarationModel()==null &&
                        dec.getName().equals(id.getText())) {
                    change.addEdit(new ReplaceEdit(id.getStartIndex(), 
                            dec.getName().length(), alias));
                }
            }
        });
        if (change.getEdit().hasChildren()) {
            proposals.add(new UseAliasProposal(file, alias, dec.getName(), change));
        }
    }
    
}
