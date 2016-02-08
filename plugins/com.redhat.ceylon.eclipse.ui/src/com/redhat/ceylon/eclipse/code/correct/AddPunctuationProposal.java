package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getBeforeParenthesisNode;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getDescription;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

@Deprecated
class AddPunctuationProposal extends CorrectionProposal {
    
    AddPunctuationProposal(int offset, int len, String desc, 
            TextFileChange change) {
        super(desc, change, new Region(offset, len));
    }
    
    static void addEmptyParameterListProposal(IFile file,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        Node n = getBeforeParenthesisNode(decNode);
        if (n!=null) {
            Declaration dec = decNode.getDeclarationModel();
            TextFileChange change = 
                    new TextFileChange(
                            "Add Empty Parameter List", 
                            file);
            int offset = n.getEndIndex();
            change.setEdit(new InsertEdit(offset, "()"));
            proposals.add(new AddPunctuationProposal(
                    offset+1, 0, 
                    "Add '()' empty parameter list to " + 
                    getDescription(dec), 
                    change));
        }
    }

    static void addImportWildcardProposal(IFile file,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        if (node instanceof Tree.ImportMemberOrTypeList) {
            Tree.ImportMemberOrTypeList imtl = 
                    (Tree.ImportMemberOrTypeList) node;
            TextFileChange change = 
                    new TextFileChange(
                            "Add Import Wildcard", 
                            file);
            int offset = imtl.getStartIndex();
            int length = imtl.getDistance();
            change.setEdit(new ReplaceEdit(
                    offset, length, 
                    "{ ... }"));
            proposals.add(new AddPunctuationProposal(
                    offset+2, 3, 
                    "Add '...' import wildcard", 
                    change));
        }
    }
}