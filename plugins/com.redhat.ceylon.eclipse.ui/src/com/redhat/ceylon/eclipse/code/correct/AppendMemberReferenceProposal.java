package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.ide.common.util.RequiredType;
import com.redhat.ceylon.ide.common.util.types_;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Value;

@Deprecated
class AppendMemberReferenceProposal extends CorrectionProposal  {
    
    private static final List<Type> NO_TYPES = 
            Collections.<Type>emptyList();
    
    private AppendMemberReferenceProposal(Node node, 
            String name, String type, TextFileChange change) {
        super("Append reference to member '" + name + 
                "' of type '" + type + "'", 
                change, 
                new Region(node.getEndIndex(), name.length()+1), 
                MINOR_CHANGE);
    }
    
    @Deprecated
    private static void addAppendMemberReferenceProposal(
            Node node,
            Collection<ICompletionProposal> proposals, 
            IFile file,
            TypedDeclaration dec, Type type,
            Tree.CompilationUnit rootNode) {
        TextFileChange change = 
                new TextFileChange("Append Member Reference", 
                        file);
        int problemOffset = node.getEndIndex();
        change.setEdit(new InsertEdit(problemOffset, 
                "." + dec.getName()));
        proposals.add(new AppendMemberReferenceProposal(
                node, dec.getName(), type.asString(), change));
    }
    
    @Deprecated
    static void addAppendMemberReferenceProposals(
            Tree.CompilationUnit rootNode, 
            Node node, ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IFile file) {
        Node id = getIdentifyingNode(node);
        if (id!=null) {
            if (node instanceof Tree.StaticMemberOrTypeExpression) {
                Tree.StaticMemberOrTypeExpression mte = 
                        (Tree.StaticMemberOrTypeExpression) 
                            node;
                Type t = mte.getTypeModel();
                if (t!=null) {
                    CommonToken token = 
                            (CommonToken) 
                                id.getToken();
                    RequiredType required = types_.get_()
                            .getRequiredType(rootNode, node, token);
                    Type requiredType = required.getType();
                    if (requiredType!=null) {
                        TypeDeclaration type = t.getDeclaration();
                        Collection<DeclarationWithProximity> dwps = 
                                type.getMatchingMemberDeclarations(
                                        node.getUnit(), 
                                        node.getScope(), 
                                        "", 0)
                                    .values();
                        for (DeclarationWithProximity dwp: dwps) {
                            Declaration dec = dwp.getDeclaration();
                            if (dec instanceof Value) {
                                Value value = (Value) dec;
                                Type vt = 
                                        value.appliedReference(t, NO_TYPES)
                                            .getType();
                                if (!isTypeUnknown(vt) 
                                        && vt.isSubtypeOf(requiredType)) {
                                    addAppendMemberReferenceProposal(
                                            id, proposals, file, 
                                            value, t, rootNode);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }
    
}