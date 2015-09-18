package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.InvocationCompletionProposal.addInvocationProposals;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class TypeArgumentListCompletions {
    
    public static void addTypeArgumentListProposal(final int offset,
            final CeylonParseController cpc, final Node node,
            final Scope scope, final IDocument document,
            final List<ICompletionProposal> result) {
        final Integer startIndex2 = node.getStartIndex();
        final Integer stopIndex2 = node.getEndIndex();
        final String typeArgText;
        try {
            typeArgText = document.get(startIndex2, stopIndex2-startIndex2);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
            return;
        }
        Tree.CompilationUnit upToDateAndTypechecked = cpc.getTypecheckedRootNode();
        if (upToDateAndTypechecked == null) {
            return;
        }
        new Visitor() {
            @Override
            public void visit(Tree.StaticMemberOrTypeExpression that) {
                Tree.TypeArguments tal = that.getTypeArguments();
                Integer startIndex = tal==null ? null : tal.getStartIndex();
                if (startIndex!=null && startIndex2!=null &&
                    startIndex.intValue()==startIndex2.intValue()) {
                    Reference pr = that.getTarget();
                    Declaration d = that.getDeclaration();
                    if (d instanceof Functional && pr!=null) {
                        try {
                            String pref = document.get(that.getIdentifier().getStartIndex(), 
                                    that.getEndIndex()-that.getIdentifier().getStartIndex());
                            for (Declaration dec: CompletionUtil.overloads(d)) {
                                addInvocationProposals(offset, pref, cpc, result, null, dec, 
                                        pr, scope, null, typeArgText, false);
                            }
                        } 
                        catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                super.visit(that);
            }
            public void visit(Tree.SimpleType that) {
                Tree.TypeArgumentList tal = that.getTypeArgumentList();
                Integer startIndex = tal==null ? null : tal.getStartIndex();
                if (startIndex!=null && startIndex2!=null &&
                    startIndex.intValue()==startIndex2.intValue()) {
                    Declaration d = that.getDeclarationModel();
                    if (d instanceof Functional) {
                        try {
                            String pref = document.get(that.getStartIndex(), 
                                    that.getDistance());
                            for (Declaration dec: CompletionUtil.overloads(d)) {
                                addInvocationProposals(offset, pref, cpc, result, null, dec, 
                                        that.getTypeModel(), scope, null, typeArgText, 
                                        false);
                            }
                        }
                        catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                super.visit(that);
            }
        }.visit(upToDateAndTypechecked);
    }

}
