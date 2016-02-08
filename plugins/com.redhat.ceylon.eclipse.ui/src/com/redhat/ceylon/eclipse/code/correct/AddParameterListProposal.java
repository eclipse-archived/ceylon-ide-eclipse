package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.collectUninitializedMembers;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getDescription;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;

class AddParameterListProposal extends CorrectionProposal {
    
    AddParameterListProposal(int offset, 
            String desc, TextChange change) {
        super(desc, change, new Region(offset, 0));
    }

    @Deprecated
    static void addParameterListProposal(IFile file,
            Collection<ICompletionProposal> proposals, 
            Node node, Tree.CompilationUnit rootNode,
            boolean evenIfEmpty) {
        if (node instanceof Tree.TypedDeclaration) {
            node = findDeclarationWithBody(rootNode, node);
        }
        if (node instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition decNode = 
                    (Tree.ClassDefinition) node;
            Node n = CorrectionUtil.getBeforeParenthesisNode(decNode);
            if (n!=null && decNode.getParameterList()==null) {
                Declaration dec = decNode.getDeclarationModel();
                List<TypedDeclaration> uninitialized = 
                        collectUninitializedMembers(decNode.getClassBody());
                if (evenIfEmpty || !uninitialized.isEmpty()) {
                    StringBuilder params = new StringBuilder().append("(");
                    for (TypedDeclaration ud: uninitialized) {
                        if (params.length()>1) {
                            params.append(", ");
                        }
                        params.append(ud.getName());
                    }
                    params.append(")");
                    TextFileChange change = 
                            new TextFileChange("Add Parameter List", file);
                    int offset = n.getEndIndex();
                    change.setEdit(new InsertEdit(offset, params.toString()));
                    proposals.add(new AddParameterListProposal(offset+1, 
                            "Add initializer parameters '" + params + 
                                    "' to " + getDescription(dec), 
                            change));
                }
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "com.redhat.ceylon.eclipse.ui.action.addParameterList");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }
    
}