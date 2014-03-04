package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.keys.IBindingService;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public abstract class RefactorLinkedMode extends AbstractLinkedMode {

//    private static final ICompletionProposal[] NO_COMPLETIONS = new ICompletionProposal[0];
//    private static final Pattern IDPATTERN = Pattern.compile("(^|[A-Z])([A-Z]*)([_a-z]+)");
    
    void addTypeProposals(IDocument document,
            List<ProducedType> supertypes, 
            int offset, int length) {
        Unit unit = editor.getParseController().getRootNode().getUnit();
        ICompletionProposal[] proposals = 
                new ICompletionProposal[supertypes.size()];
        for (int i=0; i<supertypes.size(); i++) {
            ProducedType type = supertypes.get(i);
            String typeName = type.getProducedTypeName(unit);
            proposals[i] = new LinkedModeCompletionProposal(offset, typeName, 0,
                    getImageForDeclaration(type.getDeclaration()));
        }
        ProposalPosition linkedPosition = 
                new ProposalPosition(document, offset, length, 2, proposals);
        try {
            addLinkedPosition(linkedModeModel, linkedPosition);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    protected String originalName;

    protected LinkedPosition namePosition;
    protected LinkedPositionGroup linkedPositionGroup;
    
    protected final String openDialogKeyBinding;
    
    public RefactorLinkedMode(CeylonEditor editor) {
        super(editor);
        openDialogKeyBinding = getOpenDialogBinding(getActionName());
    }
    
    protected abstract String getActionName();
    
    protected abstract String getName();
    
    protected int init(IDocument document) {
        return 0;
    }
    
    protected abstract int getIdentifyingOffset();
    
    public void start() {
        ISourceViewer viewer = editor.getCeylonSourceViewer();
        final IDocument document = viewer.getDocument();
        int offset = originalSelection.x;
        final int adjust = init(document);        
        originalName = getName();
        try {
            createLinkedModeModel(document, adjust);
            addAdditionalLinkedPositionGroups(document);
            enterLinkedMode(document, offset, adjust);
            openPopup();
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void createLinkedModeModel(IDocument document,
            int adjust) 
                    throws BadLocationException {
        
        linkedPositionGroup = new LinkedPositionGroup();
        int offset = getIdentifyingOffset();
        namePosition = new LinkedPosition(document, offset, 
                originalName.length(), 0);
//        namePosition = 
//                new ProposalPosition(document, offset, originalName.length(), 
//                        0, getNameProposals(offset));
//        
        linkedPositionGroup.addPosition(namePosition);
        
        addLinkedPositions(document, 
                editor.getParseController().getRootNode(), 
                adjust, linkedPositionGroup);

        linkedModeModel.addGroup(linkedPositionGroup);
    }

//    private ICompletionProposal[] getNameProposals(int offset) {
//        List<ICompletionProposal> nameProposals = 
//                new ArrayList<ICompletionProposal>();
//        Matcher matcher = IDPATTERN.matcher(originalName);
//        while (matcher.find()) {
//            int loc = matcher.start(2);
//            String initial = originalName.substring(matcher.start(1), loc);
//            if (Character.isLowerCase(originalName.charAt(0))) {
//                initial = initial.toLowerCase();
//            }
//            String nameProposal = initial + originalName.substring(loc);
//            nameProposals.add(new LinkedModeCompletionProposal(offset, nameProposal, 1));
//        }
//        return nameProposals.toArray(NO_COMPLETIONS);
//    }

    protected void addAdditionalLinkedPositionGroups(IDocument document) {}

    protected abstract void addLinkedPositions(IDocument document, 
            Tree.CompilationUnit rootNode, int adjust, 
            LinkedPositionGroup linkedPositionGroup);
    
    protected String getOriginalName() {
        return originalName;
    }
    
    protected String getNewName() {
        try {
            return namePosition.getContent();
        }
        catch (BadLocationException e) {
            return originalName;
        }
    }

    public boolean isOriginalName() {
        return originalName.equals(getNewName());
    }

    public boolean isEnabled() {
        String newName = getNewName();
        return !originalName.equals(newName) &&
                newName.matches("^\\w(\\w|\\d)*$") &&
                !CeylonTokenColorer.keywords.contains(newName);
    }

    /**
     * WARNING: only works in workbench window context!
     */
    private String getOpenDialogBinding(String actionName) {
        if (actionName==null) {
            return "";
        }
        else {
            IBindingService bindingService= (IBindingService) getWorkbench()
                    .getAdapter(IBindingService.class);
            if (bindingService == null) {
                return "";
            }
            else {
                String binding= bindingService.getBestActiveBindingFormattedFor(actionName);
                return binding == null ? "" : binding;
            }
        }
    }
    
}