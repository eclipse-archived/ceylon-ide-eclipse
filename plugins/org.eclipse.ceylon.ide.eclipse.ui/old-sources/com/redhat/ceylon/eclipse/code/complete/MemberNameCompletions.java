package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.LOCAL_NAME;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.addNameProposals;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.common.util.escaping_;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Unit;

@Deprecated
public class MemberNameCompletions {
    
    static void addMemberNameProposals(final int offset,
            final CeylonParseController controller, 
            final Node node,
            final List<ICompletionProposal> result) {
        final Integer startIndex2 = node.getStartIndex();
        final Tree.CompilationUnit upToDateAndTypechecked = 
                controller.getTypecheckedRootNode();
        if (upToDateAndTypechecked != null) {
            new Visitor() {
                @Override
                public void visit(
                        Tree.StaticMemberOrTypeExpression that) {
                    Tree.TypeArguments tal = 
                            that.getTypeArguments();
                    Integer startIndex = 
                            tal==null ? null : 
                                tal.getStartIndex();
                    if (startIndex!=null && startIndex2!=null &&
                        startIndex.intValue()==startIndex2.intValue()) {
                        addMemberNameProposal(offset, "", that, 
                                result, upToDateAndTypechecked);
                    }
                    super.visit(that);
                }
                @Override
                public void visit(
                        Tree.SimpleType that) {
                    Tree.TypeArgumentList tal = 
                            that.getTypeArgumentList();
                    Integer startIndex = 
                            tal==null ? null : 
                                tal.getStartIndex();
                    if (startIndex!=null && startIndex2!=null &&
                        startIndex.intValue()==startIndex2.intValue()) {
                        addMemberNameProposal(offset, "", that, 
                                result, upToDateAndTypechecked);
                    }
                    super.visit(that);
                }
            }.visit(upToDateAndTypechecked);
        }
    }
    
    static void addMemberNameProposal(int offset, String prefix,
            final Node previousNode, 
            List<ICompletionProposal> result,
            Tree.CompilationUnit rootNode) {
        class FindCompoundTypeVisitor extends Visitor {
            Node result = previousNode;
            @Override
            public void visit(Tree.Type that) {
                if (that.getStartIndex()<=previousNode.getStartIndex() &&
                        that.getEndIndex()>=previousNode.getEndIndex()) {
                    result = that;
                }
            }
        }
        FindCompoundTypeVisitor fcv = 
                new FindCompoundTypeVisitor();
        fcv.visit(rootNode);
        Node node = fcv.result;
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
        
        addProposalsForType(node, proposals);
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
            String unquotedPrefix = 
                    prefix.startsWith("\\i") ? 
                            prefix.substring(2) : prefix;
            if (name.startsWith(unquotedPrefix)) {
                String unquotedName = 
                        name.startsWith("\\i") ? 
                                name.substring(2) : name;
                result.add(new CompletionProposal(offset, prefix, 
                        LOCAL_NAME, unquotedName, name));
            }
        }
    }

    public static void addProposalsForType(Node node, 
            Set<String> proposals) {
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
            addProposalsForType(ot.getDefiniteType(), proposals);
            for (String text: proposals) {
                if (text.startsWith("\\i")) {
                    text = text.substring(2);
                }
                proposals.add("maybe" + escaping_.get_().toInitialUppercase(text));
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
            proposals.add("stream");
            proposals.add("iterable");
        }
        else if (node instanceof Tree.TupleType) {
            Tree.TupleType tt = (Tree.TupleType) node;
            List<Tree.Type> ets = tt.getElementTypes();
            if (ets.isEmpty()) {
                proposals.add("none");
                proposals.add("empty");
            }
            else if (ets.size()==1) {
                Tree.Type et = ets.get(0);
                if (et instanceof Tree.SequencedType) {
                    Tree.SequencedType st = 
                            (Tree.SequencedType) et;
                    et = st.getType();
                    if (et instanceof Tree.SimpleType) {
                        Tree.SimpleType set = 
                                (Tree.SimpleType) et;
                        addPluralProposals(proposals, 
                                set.getIdentifier(), 
                                tt.getTypeModel());
                    }
                    proposals.add("sequence");
                }
                else {
                    addProposalsForType(et, proposals);
                    proposals.add("singleton");
                }
            }
            else {
                addCompoundTypeProposal(ets, proposals, "With");
                if (ets.size()==2) {
                    proposals.add("pair");
                }
                else if (ets.size()==3) {
                    proposals.add("triple");
                }
                proposals.add("tuple");
            }
        }
        else if (node instanceof Tree.FunctionType) {
            Tree.FunctionType ft = (Tree.FunctionType) node;
            addProposalsForType(ft.getReturnType(), proposals);
            proposals.add("callable");
        }
        else if (node instanceof Tree.UnionType) {
            Tree.UnionType ut = (Tree.UnionType) node;
            addCompoundTypeProposal(ut.getStaticTypes(), 
                    proposals, "Or");
        }
        else if (node instanceof Tree.IntersectionType) {
            Tree.IntersectionType it = (Tree.IntersectionType) node;
            addCompoundTypeProposal(it.getStaticTypes(), 
                    proposals, "And");
        }
    }

    public static void addCompoundTypeProposal(
            List<? extends Tree.Type> ets, 
            Set<String> proposals, String join) {
        StringBuilder sb = new StringBuilder();
        for (Tree.Type t: ets) {
            Set<String> set = new LinkedHashSet<String>();
            addProposalsForType(t, set);
            String text = set.iterator().next();
            if (text!=null) {
                if (text.startsWith("\\i")) {
                    text = text.substring(2);
                }
                if (sb.length()>0) {
                    sb.append(join)
                      .append(escaping_.get_().toInitialUppercase(text));
                }
                else {
                    sb.append(text);
                }
            }
            else {
                sb = null;
                break;
            }
        }
        if (sb!=null) {
            proposals.add(sb.toString());
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
        if (!isTypeUnknown(type)) {
            if (identifier.getUnit().isIterableType(type)) {
                addPluralProposals(proposals, identifier, type);
            }
            if (type.isString()) {
                proposals.add("text");
                proposals.add("name");
            }
            else if (type.isInteger()) {
                proposals.add("count");
                proposals.add("size");
                proposals.add("index");
            }
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
