package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getNameProposals;
import static com.redhat.ceylon.eclipse.util.Nodes.addNameProposals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.LinkedMode;

class DestructureProposal extends LocalProposal {
    
    private List<String[]> nameProposals = new ArrayList<String[]>();
    
    private boolean isTupleType() {
        Class td = node.getUnit().getTupleDeclaration();
        return type.getDeclaration().inherits(td);
    }
    
    protected DocumentChange createChange(IDocument document, Node expanse,
            Integer stopIndex) {
        DocumentChange change = 
                new DocumentChange("Destructure", document);
        change.setEdit(new MultiTextEdit());
        Unit unit = expanse.getUnit();
        String text;
        if (isTupleType()) {
            List<ProducedType> elementTypes = 
                    unit.getTupleElementTypes(type);
            StringBuilder builder = new StringBuilder();
            Set<String> used = new HashSet<String>();
            for (int i = 0; i<elementTypes.size(); i++) {
                ProducedType elementType = elementTypes.get(i);
                if (builder.length()!=0) builder.append(", "); 
                String name = lower(elementType.getDeclaration().getName(unit));
                if (!used.add(name)) {
                    name = name + i;
                }
                Set<String> more = new LinkedHashSet<String>();
                more.add(name);
                addNameProposals(more, false, name);
                nameProposals.add(more.toArray(new String[0]));
                builder.append(name);
            }
            text = "value [" + builder + "] = ";
        }
        else {
            ProducedType keyType = unit.getKeyType(type);
            String keyName = lower(keyType.getDeclaration().getName(unit));
            Set<String> keyMore = new LinkedHashSet<String>();
            keyMore.add("key");
            keyMore.add(keyName);
            addNameProposals(keyMore, false, keyName);
            nameProposals.add(keyMore.toArray(new String[0]));
            ProducedType itemType = unit.getValueType(type);
            String itemName = lower(itemType.getDeclaration().getName(unit));
            Set<String> itemMore = new LinkedHashSet<String>();
            itemMore.add("item");
            itemMore.add(itemName);
            addNameProposals(itemMore, false, itemName);
            nameProposals.add(itemMore.toArray(new String[0]));
            text = "value key -> item = ";
        }
        change.addEdit(new InsertEdit(offset, text));

        String terminal = expanse.getEndToken().getText();
        if (!terminal.equals(";")) {
            change.addEdit(new InsertEdit(stopIndex+1, ";"));
            exitPos = stopIndex+2+text.length();
        }
        else {
            exitPos = stopIndex+1+text.length();
        }
        return change;
    }

    String lower(String name) {
        return Character.toLowerCase(name.charAt(0)) + 
                name.substring(1);
    }
    
    @Override
    protected int getExitPosition() {
        return exitPos;
    }
    
    @Override
    int getExitSequenceNumber() {
        return nameProposals.size()+1;
    }
    
    public DestructureProposal(Tree.CompilationUnit cu, 
            Node node, int currentOffset) {
        super(cu, node, currentOffset);
    }
    
    protected void addLinkedPositions(IDocument document, Unit unit)
            throws BadLocationException {
        int startOffset = isTupleType() ? offset+7 : offset+6;
        int nameOffset = startOffset;
        for (int i=0; i<nameProposals.size(); i++) {
            String[] proposalList = nameProposals.get(i);
            int length = proposalList[0].length();
            ProposalPosition namePosition = 
                    new ProposalPosition(document, nameOffset, length, i, 
                            getNameProposals(startOffset, i, proposalList));
            LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
            nameOffset += isTupleType() ? length + 2 : length + 4;
        }
    }
    
    @Override
    boolean isEnabled(ProducedType resultType) {
        Class td = node.getUnit().getTupleDeclaration();
        Class ed = node.getUnit().getEntryDeclaration();
        TypeDeclaration rtd = resultType.getDeclaration();
        return rtd.inherits(td) || rtd.inherits(ed);
    }
    
    @Override
    public String getDisplayString() {
        return "Destructure expression";
    }
    
    static void addDestructureProposal(Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        DestructureProposal prop = 
                new DestructureProposal(cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}