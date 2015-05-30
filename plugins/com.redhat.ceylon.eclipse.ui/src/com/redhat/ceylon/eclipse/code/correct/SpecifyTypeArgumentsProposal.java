package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.REVEAL;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class SpecifyTypeArgumentsProposal extends CorrectionProposal {

    SpecifyTypeArgumentsProposal(String type, TextFileChange change) {
        super("Specify explicit type arguments '" + type + "'", change, null, REVEAL);
    }
    
    static void addSpecifyTypeArgumentsProposal(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IFile file) {
        Tree.MemberOrTypeExpression ref = (Tree.MemberOrTypeExpression) node;
        Tree.Identifier identifier;
        Tree.TypeArguments typeArguments;
        if (ref instanceof Tree.BaseMemberOrTypeExpression) {
            identifier = ((Tree.BaseMemberOrTypeExpression) ref).getIdentifier();
            typeArguments = ((Tree.BaseMemberOrTypeExpression) ref).getTypeArguments();
        }
        else if (ref instanceof Tree.QualifiedMemberOrTypeExpression) {
            identifier = ((Tree.QualifiedMemberOrTypeExpression) ref).getIdentifier();
            typeArguments = ((Tree.QualifiedMemberOrTypeExpression) ref).getTypeArguments();
        }
        else {
            return;
        }
        if (typeArguments instanceof Tree.InferredTypeArguments &&
                typeArguments.getTypeModels()!=null &&
                !typeArguments.getTypeModels().isEmpty()) {
            StringBuilder builder = new StringBuilder("<");
            for (Type arg: typeArguments.getTypeModels()) {
                if (isTypeUnknown(arg)) {
                    return;
                }
                if (builder.length()!=1) {
                    builder.append(",");
                }
                builder.append(arg.asSourceCodeString(node.getUnit()));
            }
            builder.append(">");
            TextFileChange change = new TextFileChange("Specify Explicit Type Arguments", file);
            change.setEdit(new InsertEdit(identifier.getStopIndex()+1, builder.toString())); 
            proposals.add(new SpecifyTypeArgumentsProposal(builder.toString(), change));
        }
    }
    
}
