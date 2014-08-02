package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.LOCAL_NAME;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Nodes;

public class MemberNameCompletions {
    
    static void addMemberNameProposals(final int offset,
            final CeylonParseController cpc, final Node node,
            final List<ICompletionProposal> result) {
        final Integer startIndex2 = node.getStartIndex();
        new Visitor() {
            @Override
            public void visit(Tree.StaticMemberOrTypeExpression that) {
                Tree.TypeArguments tal = that.getTypeArguments();
                Integer startIndex = tal==null ? null : tal.getStartIndex();
                if (startIndex!=null && startIndex2!=null &&
                    startIndex.intValue()==startIndex2.intValue()) {
                    addMemberNameProposal(offset, "", that, result);
                }
                super.visit(that);
            }
            public void visit(Tree.SimpleType that) {
                Tree.TypeArgumentList tal = that.getTypeArgumentList();
                Integer startIndex = tal==null ? null : tal.getStartIndex();
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
        Set<String> proposals = new LinkedHashSet<String>();
        if (node instanceof Tree.TypeDeclaration) {
            //TODO: dictionary completions?
            return;
        }
        else if (node instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td = (Tree.TypedDeclaration) node;
            Tree.Type type = td.getType();
            if (type instanceof Tree.OptionalType) {
                type = ((Tree.OptionalType) type).getDefiniteType();
            }
            if (type instanceof Tree.SimpleType) {
                Tree.Identifier id = td.getIdentifier();
                if (id==null || offset>=id.getStartIndex() && offset<=id.getStopIndex()+1) {
                    Tree.SimpleType simpleType = (Tree.SimpleType) type;
                    addProposals(proposals, simpleType.getIdentifier(), 
                            simpleType.getTypeModel());
                }
            }
            else {
                if (type instanceof Tree.TupleType) {
                    proposals.add("sequence");
                    proposals.add("tuple");
                }
            }
        }
        else if (node instanceof Tree.SimpleType) {
            Tree.SimpleType simpleType = (Tree.SimpleType) node;
            addProposals(proposals, simpleType.getIdentifier(), 
                    simpleType.getTypeModel());
        }
        else if (node instanceof Tree.BaseTypeExpression) {
            Tree.BaseTypeExpression typeExpression = (Tree.BaseTypeExpression) node;
            addProposals(proposals, typeExpression.getIdentifier(), 
                    node.getUnit().getCallableReturnType(typeExpression.getTypeModel()));
        }
        else if (node instanceof Tree.QualifiedTypeExpression) {
            Tree.QualifiedTypeExpression typeExpression = (Tree.QualifiedTypeExpression) node;
            addProposals(proposals, typeExpression.getIdentifier(), 
                    node.getUnit().getCallableReturnType(typeExpression.getTypeModel()));
        }
        else if (node instanceof Tree.Tuple) {
            proposals.add("sequence");
            proposals.add("tuple");
        }
        /*if (suggestedName!=null) {
            suggestedName = lower(suggestedName);
            String unquoted = prefix.startsWith("\\i")||prefix.startsWith("\\I") ?
                    prefix.substring(2) : prefix;
            if (!suggestedName.startsWith(unquoted)) {
                suggestedName = prefix + upper(suggestedName);
            }
            result.add(new CompletionProposal(offset, prefix, LOCAL_NAME,
                    suggestedName, escape(suggestedName)));
        }*/
        /*if (proposals.isEmpty()) {
            proposals.add("it");
        }*/
        for (String name: proposals) {
            String unquotedPrefix = prefix.startsWith("\\i") ? 
                    prefix.substring(2) : prefix;
            if (name.startsWith(unquotedPrefix)) {
                String unquotedName = name.startsWith("\\i") ? 
                        name.substring(2) : name;
                result.add(new CompletionProposal(offset, prefix, 
                        LOCAL_NAME, unquotedName, name));
            }
        }
    }

    private static void addProposals(Set<String> proposals,
            Tree.Identifier identifier, ProducedType type) {
        Nodes.addNameProposals(proposals, false, identifier.getText());
        if (!isTypeUnknown(type) &&
                identifier.getUnit().isIterableType(type)) {
            Nodes.addNameProposals(proposals, true, 
                    identifier.getUnit().getIteratedType(type)
                            .getDeclaration().getName());
        }
    }

    /*private static String lower(String suggestedName) {
        return Character.toLowerCase(suggestedName.charAt(0)) + 
                suggestedName.substring(1);
    }

    private static String upper(String suggestedName) {
        return Character.toUpperCase(suggestedName.charAt(0)) + 
                suggestedName.substring(1);
    }*/
    

}
