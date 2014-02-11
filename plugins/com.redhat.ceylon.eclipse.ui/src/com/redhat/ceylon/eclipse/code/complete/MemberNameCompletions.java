package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.LOCAL_NAME;
import static com.redhat.ceylon.eclipse.util.Escaping.escape;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class MemberNameCompletions {
    
    static void addMemberNameProposals(final int offset,
            final CeylonParseController cpc, final Node node,
            final List<ICompletionProposal> result) {
        final Integer startIndex2 = node.getStartIndex();
        new Visitor() {
            @Override
            public void visit(Tree.StaticMemberOrTypeExpression that) {
                Integer startIndex = that.getTypeArguments().getStartIndex();
                if (startIndex!=null && startIndex2!=null &&
                    startIndex.intValue()==startIndex2.intValue()) {
                    addMemberNameProposal(offset, "", that, result);
                }
                super.visit(that);
            }
            public void visit(Tree.SimpleType that) {
                Integer startIndex = that.getTypeArgumentList().getStartIndex();
                if (startIndex!=null && startIndex2!=null &&
                    startIndex.intValue()==startIndex2.intValue()) {
                    addMemberNameProposal(offset, "", that, result);
                }
                super.visit(that);
            }
        }.visit(cpc.getRootNode());
    }
    
    static void addMemberNameProposal(int offset, String prefix,
            Node node, List<ICompletionProposal> result) {
        String suggestedName = null;
        if (node instanceof Tree.TypeDeclaration) {
            /*Tree.TypeDeclaration td = (Tree.TypeDeclaration) node;
            prefix = td.getIdentifier()==null ? 
                    "" : td.getIdentifier().getText();
            suggestedName = prefix;*/
            //TODO: dictionary completions?
            return;
        }
        else if (node instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td = (Tree.TypedDeclaration) node;
            if (td.getType() instanceof Tree.SimpleType) {
                Tree.Identifier id = td.getIdentifier();
                if (id==null || offset>=id.getStartIndex() && offset<=id.getStopIndex()+1) {
                    suggestedName = ((Tree.SimpleType) td.getType()).getIdentifier().getText();
                }
            }
        }
        else if (node instanceof Tree.SimpleType) {
            suggestedName = ((Tree.SimpleType) node).getIdentifier().getText();
        }
        else if (node instanceof Tree.BaseTypeExpression) {
            suggestedName = ((Tree.BaseTypeExpression) node).getIdentifier().getText();
        }
        else if (node instanceof Tree.QualifiedTypeExpression) {
            suggestedName = ((Tree.QualifiedTypeExpression) node).getIdentifier().getText();
        }
        if (suggestedName!=null) {
            suggestedName = lower(suggestedName);
            if (!suggestedName.startsWith(prefix)) {
                suggestedName = prefix + upper(suggestedName);
            }
            result.add(new CompletionProposal(offset, prefix, LOCAL_NAME,
                    suggestedName, escape(suggestedName)));
        }
    }

    private static String lower(String suggestedName) {
        return Character.toLowerCase(suggestedName.charAt(0)) + 
                suggestedName.substring(1);
    }

    private static String upper(String suggestedName) {
        return Character.toUpperCase(suggestedName.charAt(0)) + 
                suggestedName.substring(1);
    }
    

}
