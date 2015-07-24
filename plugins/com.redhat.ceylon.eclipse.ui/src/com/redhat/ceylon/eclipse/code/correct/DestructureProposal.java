package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.LinkedModeCompletionProposal.getNameProposals;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCommandBinding;
import static com.redhat.ceylon.eclipse.util.Nodes.addNameProposals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.Escaping;
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
            List<Type> elementTypes = 
                    unit.getTupleElementTypes(type);
            StringBuilder builder = new StringBuilder();
            Set<String> used = new HashSet<String>();
            for (int i = 0; i<elementTypes.size(); i++) {
                Type elementType = elementTypes.get(i);
                if (builder.length()!=0) builder.append(", "); 
                String name = Escaping.toInitialLowercase(elementType.getDeclaration().getName(unit));
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
            nameProposals.add(getKeyProposals(unit, type));
            nameProposals.add(getItemProposals(unit, type));
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

    static String[] getItemProposals(Unit unit, Type type) {
        Type itemType = unit.getValueType(type);
        String itemName = Escaping.toInitialLowercase(itemType.getDeclaration().getName(unit));
        Set<String> itemMore = new LinkedHashSet<String>();
        itemMore.add("item");
        itemMore.add(itemName);
        addNameProposals(itemMore, false, itemName);
        return itemMore.toArray(new String[0]);
    }

    static String[] getKeyProposals(Unit unit, Type type) {
        Type keyType = unit.getKeyType(type);
        String keyName = Escaping.toInitialLowercase(keyType.getDeclaration().getName(unit));
        Set<String> keyMore = new LinkedHashSet<String>();
        keyMore.add("key");
        keyMore.add(keyName);
        addNameProposals(keyMore, false, keyName);
        return keyMore.toArray(new String[0]);
    }

    @Override
    protected int getExitPosition() {
        return exitPos;
    }
    
    @Override
    int getExitSequenceNumber() {
        return nameProposals.size()+1;
    }
    
    public DestructureProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Node node, int currentOffset) {
        super(ceylonEditor, cu, node, currentOffset);
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
    boolean isEnabled(Type resultType) {
        if (node==null || node.getUnit()==null) return false; 
        Class td = node.getUnit().getTupleDeclaration();
        Class ed = node.getUnit().getEntryDeclaration();
        TypeDeclaration rtd = resultType.getDeclaration();
        return rtd.inherits(td) || rtd.inherits(ed);
    }
    
    @Override
    public String getDisplayString() {
        return "Destructure expression";
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        TriggerSequence binding = 
                getCommandBinding("com.redhat.ceylon.eclipse.ui.action.destructure");
        String hint = binding==null ? "" : " (" + binding.format() + ")";
        return new StyledString(getDisplayString())
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

    static void addDestructureProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        DestructureProposal prop = 
                new DestructureProposal(ceylonEditor, cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}