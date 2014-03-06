package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class LinkedModeCompletionProposal 
        implements ICompletionProposal,  ICompletionProposalExtension2 {
    
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
                    if (((ICompletionProposalExtension2) p).validate(document, offset, event)) {
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
    
    public LinkedModeCompletionProposal(int offset, String text, 
            int position) {
        this(offset, text, position, null);
    }
    
    public LinkedModeCompletionProposal(int offset, String text, 
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
            if (Character.isWhitespace(document.getChar(i))) {
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
    public void apply(ITextViewer viewer, char trigger, int stateMask,
            int offset) {
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
    
    private static final Pattern IDPATTERN = Pattern.compile("(^|[A-Z])([A-Z]*)([_a-z]+)");
    
    public static ICompletionProposal[] getNameProposals(int offset, 
            int seq, String name) {
        return getNameProposals(offset, seq, name, null);
    }
    
    public static ICompletionProposal[] getNameProposals(int offset, 
            int seq, String name, String defaultName) {
        List<ICompletionProposal> nameProposals = 
                new ArrayList<ICompletionProposal>();
        if (defaultName!=null) {
            LinkedModeCompletionProposal nameProposal = 
                    new LinkedModeCompletionProposal(offset, defaultName, seq);
            nameProposals.add(nameProposal);
        }
        Matcher matcher = IDPATTERN.matcher(name);
        while (matcher.find()) {
            int loc = matcher.start(2);
            String initial = name.substring(matcher.start(1), loc);
            if (Character.isLowerCase(name.charAt(0))) {
                initial = initial.toLowerCase();
            }
            String subname = initial + name.substring(loc);
            if (defaultName==null || !defaultName.equals(subname)) {
                LinkedModeCompletionProposal nameProposal = 
                        new LinkedModeCompletionProposal(offset, subname, seq);
                nameProposals.add(nameProposal);
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
            Unit unit, ProducedType type, boolean includeValue) {
        List<ProducedType> supertypes = isTypeUnknown(type) ?
                Collections.<ProducedType>emptyList() :
                    type.getSupertypes();
        ICompletionProposal[] typeProposals = 
                new ICompletionProposal[supertypes.size() + (includeValue?1:0)];
        int i=0;
        if (includeValue) {
            typeProposals[i++] =
                    new LinkedModeCompletionProposal(offset, "value", 0, null);
        }
        for (int j=0; j<supertypes.size(); j++) {
            ProducedType supertype = supertypes.get(j);
            String typeName = supertype.getProducedTypeName(unit);
            typeProposals[i++] = 
                    new LinkedModeCompletionProposal(offset, typeName, 0, 
                            getImageForDeclaration(supertype.getDeclaration()));
        }
        return typeProposals;
    }

}