package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.RENAME;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.ChangeVersionLinkedMode;
import com.redhat.ceylon.eclipse.util.Highlights;

class RenameVersionProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private final Tree.ModuleDescriptor node;
    private final CeylonEditor editor;
    
    RenameVersionProposal(Tree.ModuleDescriptor node, 
            CeylonEditor editor) {
        this.node = node;
        this.editor = editor;
    }
    
    @Override
    public void apply(IDocument document) {
        new ChangeVersionLinkedMode(node.getVersion(), node.getImportPath(), editor).start();
    }
    
    static void addRenameVersionProposals(Node node, 
            Collection<ICompletionProposal> proposals, 
            Tree.CompilationUnit cu, CeylonEditor editor) {
        for (Tree.ModuleDescriptor md: cu.getModuleDescriptors()) {
            if (md.getVersion()==node || md==node || md.getImportPath()==node) {
                proposals.add(new RenameVersionProposal(md, editor));
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Change version " + node.getVersion().getText() + 
                " of module"; //TOOD: name of module
    }

    @Override
    public Image getImage() {
        return RENAME;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
}
