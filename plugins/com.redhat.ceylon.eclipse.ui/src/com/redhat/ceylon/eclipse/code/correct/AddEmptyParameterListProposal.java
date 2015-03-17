package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getBeforeParenthesisNode;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getDescription;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class AddEmptyParameterListProposal extends CorrectionProposal {
    
    AddEmptyParameterListProposal(Declaration dec, int offset, 
            String desc, TextFileChange change) {
        super(desc, change, new Region(offset, 0));
    }
    
    static void addEmptyParameterListProposal(IFile file,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        Node n = getBeforeParenthesisNode(decNode);
        if (n!=null) {
            Declaration dec = decNode.getDeclarationModel();
            TextFileChange change = 
                    new TextFileChange("Add Empty Parameter List", file);
            int offset = n.getStopIndex();
            change.setEdit(new InsertEdit(offset+1, "()"));
            proposals.add(new AddEmptyParameterListProposal(dec, offset+2, 
                    "Add empty parameter list to " + getDescription(dec), 
                    change));
        }
    }
}