package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.LOCAL_NAME;
import static com.redhat.ceylon.eclipse.util.Nodes.addNameProposals;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;

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
            Tree.TypedDeclaration td = 
                    (Tree.TypedDeclaration) node;
            Tree.Type type = td.getType();
            Tree.Identifier id = td.getIdentifier();
            if (id==null || 
                    offset>=id.getStartIndex() && 
                    offset<=id.getEndIndex()) {
                node = type;
            }
            else {
                node = null;
            }
        }
        
        if (node instanceof Tree.SimpleType) {
            Tree.SimpleType simpleType = 
                    (Tree.SimpleType) node;
            addProposals(proposals, 
                    simpleType.getIdentifier(), 
                    simpleType.getTypeModel());
        }
        else if (node instanceof Tree.BaseTypeExpression) {
            Tree.BaseTypeExpression typeExpression = 
                    (Tree.BaseTypeExpression) node;
            addProposals(proposals,
                    typeExpression.getIdentifier(), 
                    getLiteralType(node, typeExpression));
        }
        else if (node instanceof Tree.QualifiedTypeExpression) {
            Tree.QualifiedTypeExpression typeExpression = 
                    (Tree.QualifiedTypeExpression) node;
            addProposals(proposals, 
                    typeExpression.getIdentifier(), 
                    getLiteralType(node, typeExpression));
        }
        else if (node instanceof Tree.OptionalType) {
            Tree.OptionalType ot = (Tree.OptionalType) node;
            Tree.StaticType et = ot.getDefiniteType();
            if (et instanceof Tree.SimpleType) {
                Tree.SimpleType set = (Tree.SimpleType) et;
                addProposals(proposals, 
                        set.getIdentifier(), 
                        ot.getTypeModel());
            }
        }
        else if (node instanceof Tree.SequenceType) {
            Tree.SequenceType st = (Tree.SequenceType) node;
            Tree.StaticType et = st.getElementType();
            if (et instanceof Tree.SimpleType) {
                Tree.SimpleType set = (Tree.SimpleType) et;
                addPluralProposals(proposals, 
                        set.getIdentifier(), 
                        st.getTypeModel());
            }
            proposals.add("sequence");
        }
        else if (node instanceof Tree.IterableType) {
            Tree.IterableType it = (Tree.IterableType) node;
            Tree.Type et = it.getElementType();
            if (et instanceof Tree.SequencedType) {
                Tree.SequencedType st = 
                        (Tree.SequencedType) et;
                et = st.getType();
            }
            if (et instanceof Tree.SimpleType) {
                Tree.SimpleType set = (Tree.SimpleType) et;
                addPluralProposals(proposals, 
                        set.getIdentifier(), 
                        it.getTypeModel());
            }
            proposals.add("iterable");
        }
        else if (node instanceof Tree.TupleType) {
            Tree.TupleType tt = (Tree.TupleType) node;
            List<Tree.Type> ets = tt.getElementTypes();
            if (ets.size()==1) {
                Tree.Type et = ets.get(0);
                if (et instanceof Tree.SequencedType) {
                    Tree.SequencedType st = 
                            (Tree.SequencedType) et;
                    et = st.getType();
                }
                if (et instanceof Tree.SimpleType) {
                    Tree.SimpleType set = 
                            (Tree.SimpleType) et;
                    addPluralProposals(proposals, 
                            set.getIdentifier(), 
                            tt.getTypeModel());
                }
                proposals.add("sequence");
            }
        }
        else if (node instanceof Tree.FunctionType) {
            Tree.FunctionType ft = (Tree.FunctionType) node;
            Tree.Type rt = ft.getReturnType();
            if (rt instanceof Tree.SimpleType) {
                Tree.SimpleType srt = (Tree.SimpleType) rt;
                addProposals(proposals, 
                        srt.getIdentifier(), 
                        rt.getTypeModel());
            }
            proposals.add("callable");
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

    private static Type getLiteralType(Node node,
            Tree.StaticMemberOrTypeExpression typeExpression) {
        Unit unit = node.getUnit();
        Type pt = typeExpression.getTypeModel();
        return unit.isCallableType(pt) ?
                unit.getCallableReturnType(pt) : pt;
    }

    private static void addProposals(Set<String> proposals,
            Tree.Identifier identifier, Type type) {
        addNameProposals(proposals, false, identifier.getText());
        if (!isTypeUnknown(type) &&
                identifier.getUnit().isIterableType(type)) {
            addPluralProposals(proposals, identifier, type);
        }
    }

    private static void addPluralProposals(Set<String> proposals,
            Tree.Identifier identifier, Type type) {
        if (!isTypeUnknown(type) && !type.isNothing()) {
            Unit unit = identifier.getUnit();
            addNameProposals(proposals, true, 
                    unit.getIteratedType(type)
                            .getDeclaration()
                            .getName(unit));
        }
    }

}
