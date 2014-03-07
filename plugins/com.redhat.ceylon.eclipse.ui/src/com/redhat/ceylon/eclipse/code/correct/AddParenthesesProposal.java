package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class AddParenthesesProposal extends CorrectionProposal {
    
    AddParenthesesProposal(Declaration dec, int offset, 
            TextFileChange change) {
        super("Add empty parameter list to '" + dec.getName() + "'" + 
                (dec.getContainer() instanceof TypeDeclaration?
                        "in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'" : ""), 
                        change, new Point(offset, 0));
    }

    static void addAddParenthesesProposal(ProblemLocation problem, IFile file,
            Collection<ICompletionProposal> proposals, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        Node n = getBeforeParenthesisNode(decNode);
        if (n!=null) {
            int offset = n.getStopIndex();
            TextFileChange change = new TextFileChange("Add Empty Parameter List", file);
            change.setEdit(new InsertEdit(offset+1, "()"));
            proposals.add(new AddParenthesesProposal(decNode.getDeclarationModel(), 
                    offset+2, change));
        }
    }

    private static Node getBeforeParenthesisNode(Tree.Declaration decNode) {
        Node n = decNode.getIdentifier();
        if (decNode instanceof Tree.TypeDeclaration) {
            Tree.TypeParameterList tpl = ((Tree.TypeDeclaration) decNode).getTypeParameterList();
            if (tpl!=null) {
                n = tpl;
            }
        }
        if (decNode instanceof Tree.AnyMethod) {
            Tree.TypeParameterList tpl = ((Tree.AnyMethod) decNode).getTypeParameterList();
            if (tpl!=null) {
                n = tpl;
            }
        }
        return n;
    }
}