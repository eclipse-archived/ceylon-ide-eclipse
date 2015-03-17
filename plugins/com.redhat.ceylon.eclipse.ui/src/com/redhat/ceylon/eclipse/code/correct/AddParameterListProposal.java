package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.AddConstructorProposal.collectUninitializedMembers;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class AddParameterListProposal extends CorrectionProposal {
    
    AddParameterListProposal(Declaration dec, int offset, 
            String desc, TextFileChange change) {
        super(desc, change, new Region(offset, 0));
    }

    static void addAddInitializerProposal(IFile file,
            Collection<ICompletionProposal> proposals, 
            Node node, Tree.CompilationUnit rootNode) {
        if (node instanceof Tree.TypedDeclaration) {
            node = findDeclarationWithBody(rootNode, node);
        }
        if (node instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition decNode = 
                    (Tree.ClassDefinition) node;
            Node n = getBeforeParenthesisNode(decNode);
            if (n!=null && decNode.getParameterList()==null) {
                Declaration dec = decNode.getDeclarationModel();
                List<TypedDeclaration> uninitialized = 
                        collectUninitializedMembers(decNode.getClassBody());
                if (!uninitialized.isEmpty()) {
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
                    int offset = n.getStopIndex();
                    change.setEdit(new InsertEdit(offset+1, params.toString()));
                    proposals.add(new AddParameterListProposal(dec, offset+2, 
                            "Add initializer parameters '" + params + 
                                    "' to " + getDescription(dec), 
                            change));
                }
            }
        }
    }

    static void addAddParenthesesProposal(IFile file,
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
            proposals.add(new AddParameterListProposal(dec, offset+2, 
                    "Add empty parameter list to " + getDescription(dec), 
                    change));
        }
    }

    private static String getDescription(Declaration dec) {
        String desc = "'" + dec.getName() + "'";
        Scope container = dec.getContainer();
        if (container instanceof TypeDeclaration) {
            desc += " in '" + ((TypeDeclaration) container).getName() + "'";
        }
        return desc;
    }

    private static Node getBeforeParenthesisNode(Tree.Declaration decNode) {
        Node n = decNode.getIdentifier();
        if (decNode instanceof Tree.TypeDeclaration) {
            Tree.TypeParameterList tpl = 
                    ((Tree.TypeDeclaration) decNode)
                            .getTypeParameterList();
            if (tpl!=null) {
                n = tpl;
            }
        }
        if (decNode instanceof Tree.AnyMethod) {
            Tree.TypeParameterList tpl = 
                    ((Tree.AnyMethod) decNode)
                            .getTypeParameterList();
            if (tpl!=null) {
                n = tpl;
            }
        }
        return n;
    }
}