package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class LinkedModeCompletionProposal 
        implements ICompletionProposal, ICompletionProposalExtension2 {
    
    private static final class NullProposal 
            implements ICompletionProposal, ICompletionProposalExtension2 {
        private List<ICompletionProposal> proposals;

        private NullProposal(List<ICompletionProposal> proposals) {
            this.proposals = proposals;
            
        }
        @Override
        public void apply(IDocument document) {}

        @Override
        public Point getSelection(IDocument document) {
            return null;
        }

        @Override
        public String getAdditionalProposalInfo() {
            return null;
        }

        @Override
        public String getDisplayString() {
            return "";
        }

        @Override
        public Image getImage() {
            return null;
        }

        @Override
        public IContextInformation getContextInformation() {
            return null;
        }

        @Override
        public void apply(ITextViewer viewer, char trigger, int stateMask,
                int offset) {}

        @Override
        public void selected(ITextViewer viewer, boolean smartToggle) {}

        @Override
        public void unselected(ITextViewer viewer) {}

        @Override
        public boolean validate(IDocument document, int offset,
                DocumentEvent event) {
            for (ICompletionProposal p: proposals) {
                if (p instanceof ICompletionProposalExtension2) {
                    ICompletionProposalExtension2 ext = 
                    		(ICompletionProposalExtension2) p;
					if (ext.validate(document, offset, event)) {
                        return true;
                    }
                }
                else {
                    return true;
                }
            }
            return false;
        }
    }

    private final String text;
    private final Image image;
    private final int offset;
    private int position;
    
    private LinkedModeCompletionProposal(int offset, String text, 
            int position) {
        this(offset, text, position, null);
    }
    
    private LinkedModeCompletionProposal(int offset, String text, 
            int position, Image image) {
        this.text=text;
        this.position = position;
        this.image = image;
        this.offset = offset;
    }
    
    @Override
    public Image getImage() {
        return image;
    }
    
    @Override
    public Point getSelection(IDocument document) {
        return new Point(offset + text.length(), 0);
    }
    
    public void apply(IDocument document) {
        try {
            IRegion region = getCurrentRegion(document);
            document.replace(region.getOffset(), 
                    region.getLength(), text);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    protected IRegion getCurrentRegion(IDocument document) 
            throws BadLocationException {
        int start = offset;
        int length = 0;
        int count = 0;
        for (int i=offset;
                i<document.getLength(); 
                i++) {
        	char ch = document.getChar(i);
            if (Character.isWhitespace(ch) || ch=='(') {
                if (count++==position) {
                    break;
                }
                else {
                    start = i+1;
                    length = -1;
                }
            }
            length++;
        }
        return new Region(start, length);
    }
    
    public String getDisplayString() {
        return text;
    }
    
    public String getAdditionalProposalInfo() {
        return null;
    }
    
    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
    @Override
    public void apply(ITextViewer viewer, char trigger, 
            int stateMask, int offset) {
        apply(viewer.getDocument());
    }
    
    @Override
    public void selected(ITextViewer viewer, boolean smartToggle) {}
    
    @Override
    public void unselected(ITextViewer viewer) {}
    
    @Override
    public boolean validate(IDocument document, int offset, 
            DocumentEvent event) {
        try {
            IRegion region = getCurrentRegion(document);
            String prefix = document.get(region.getOffset(), 
                    offset-region.getOffset());
            return text.startsWith(prefix);
        }
        catch (BadLocationException e) {
            return false;
        }
    }
    
    public static ICompletionProposal[] getNameProposals(int offset, 
            int seq, String[] names) {
        return getNameProposals(offset, seq, names, null);
    }
    
    public static ICompletionProposal[] getNameProposals(int offset, 
            int seq, String[] names, String defaultName) {
        List<ICompletionProposal> nameProposals = 
                new ArrayList<ICompletionProposal>();
        Set<String> proposedNames = new HashSet<String>();
        if (defaultName!=null) {
            LinkedModeCompletionProposal nameProposal = 
                    new LinkedModeCompletionProposal(offset, defaultName, seq);
            nameProposals.add(nameProposal);
            proposedNames.add(defaultName);
        }
        for (String name: names) {
        	if (proposedNames.add(name)) {
        		if (defaultName==null || !defaultName.equals(name)) {
        			LinkedModeCompletionProposal nameProposal = 
        					new LinkedModeCompletionProposal(offset, name, seq);
        			nameProposals.add(nameProposal);
        		}
        	}
        }
        ICompletionProposal[] proposals = 
                new ICompletionProposal[nameProposals.size() + 1];
        int i=0;
        proposals[i++] = new NullProposal(nameProposals);
        for (ICompletionProposal tp: nameProposals) {
            proposals[i++] = tp;
        }
        return proposals;
    }

    public static ICompletionProposal[] getSupertypeProposals(int offset, 
            Unit unit, ProducedType type, boolean includeValue, String kind) {
    	if (type==null) {
    	    return new ICompletionProposal[0];
    	}
        TypeDeclaration td = type.getDeclaration();
        List<TypeDeclaration> supertypes = isTypeUnknown(type) ?
                Collections.<TypeDeclaration>emptyList() :
                td.getSupertypeDeclarations();

        int size = supertypes.size();
        if (includeValue) size++;
        if (td instanceof UnionType || 
            td instanceof IntersectionType) {
            size++;
        }
        
        ICompletionProposal[] typeProposals = 
                new ICompletionProposal[size];
        int i=0;
        if (includeValue) {
            typeProposals[i++] =
                    new LinkedModeCompletionProposal(offset, kind, 0, 
                            getDecoratedImage(CEYLON_LITERAL, 0, false));
        }
        if (td instanceof UnionType || 
            td instanceof IntersectionType) {
            String typeName = 
                    type.getProducedTypeName(unit);
            typeProposals[i++] = 
                    new LinkedModeCompletionProposal(offset, typeName, 0, 
                            getImageForDeclaration(td));
        }
        for (int j=supertypes.size()-1; j>=0; j--) {
            ProducedType supertype = 
                    type.getSupertype(supertypes.get(j));
            String typeName = 
                    supertype.getProducedTypeName(unit);
            typeProposals[i++] = 
                    new LinkedModeCompletionProposal(offset, typeName, 0, 
                            getImageForDeclaration(supertype.getDeclaration()));
        }
        return typeProposals;
    }

    public static ICompletionProposal[] getCaseTypeProposals(int offset, Unit unit, ProducedType type) {
        if (type==null) {
            return new ICompletionProposal[0];
        }
        List<ProducedType> caseTypes = type.getCaseTypes();
        if (caseTypes==null) {
            return new ICompletionProposal[0];
        }
        ICompletionProposal[] typeProposals = 
                new ICompletionProposal[caseTypes.size()];
        for (int i=0; i<caseTypes.size(); i++) {
            ProducedType ct = caseTypes.get(i);
            String typeName = 
                    ct.getProducedTypeName(unit);
            typeProposals[i] = 
                    new LinkedModeCompletionProposal(offset, typeName, 0, 
                            getImageForDeclaration(ct.getDeclaration()));
        }
        return typeProposals;
    }
}