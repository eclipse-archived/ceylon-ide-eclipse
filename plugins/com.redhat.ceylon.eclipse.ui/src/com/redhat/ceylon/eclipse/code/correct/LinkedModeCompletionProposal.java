package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class LinkedModeCompletionProposal 
        implements ICompletionProposal, 
                   ICompletionProposalExtension2,
                   ICompletionProposalExtension6 {
    
    static final class NullProposal 
            implements ICompletionProposal, ICompletionProposalExtension2 {
        private List<ICompletionProposal> proposals;

        NullProposal(List<ICompletionProposal> proposals) {
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
    private String description;
    private String breakChars;
    private String regionChars;
    
    LinkedModeCompletionProposal(String name,
            int offset, int position) {
        this(name, offset, name, position, null);
        breakChars = " (<";
        regionChars = "\\";
    }
    
    LinkedModeCompletionProposal(Type type,
            Unit unit, int offset, int position) {
        this(type.asString(unit), 
                offset, 
                type.asSourceCodeString(unit), 
                position, 
                getImageForDeclaration(type.getDeclaration()));
    }
    
    LinkedModeCompletionProposal(String description,
            int offset, String text, int position, Image image) {
        this.description = description;
        this.text=text;
        this.position = position;
        this.image = image;
        this.offset = offset;
        regionChars = "<>[](){}*+.,|&?\\";
        breakChars = " ";
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
        boolean lastWasWs = false;
        for (int i=offset;
                i<document.getLength(); 
                i++) {
            char ch = document.getChar(i);
            if (Character.isJavaIdentifierPart(ch) 
                    || regionChars.indexOf(ch)>=0) {
                lastWasWs = false;
                length++;
            }
            else if (Character.isWhitespace(ch) 
                    || breakChars.indexOf(ch)>=0) {
                if (!lastWasWs) {
                    if (count++==position) {
                        break;
                    }
                    lastWasWs = true;
                }
                start = i+1;
                length = 0;
            }
        }
        return new Region(start, length);
    }
    
    public String getDisplayString() {
        return description;
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        StyledString result = new StyledString();
        Highlights.styleFragment(result, 
                getDisplayString(), false, null, 
                CeylonPlugin.getCompletionFont());
        return result;
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
            String prefix = 
                    document.get(region.getOffset(), 
                            offset-region.getOffset());
            String filter = prefix.trim().toLowerCase();
            return ModelUtil.isNameMatching(prefix, text) ||
                    text.toLowerCase().startsWith(filter);
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
                    new LinkedModeCompletionProposal(defaultName, offset, seq);
            nameProposals.add(nameProposal);
            proposedNames.add(defaultName);
        }
        for (String name: names) {
            if (proposedNames.add(name)) {
                if (defaultName==null || !defaultName.equals(name)) {
                    LinkedModeCompletionProposal nameProposal = 
                            new LinkedModeCompletionProposal(name, offset, seq);
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
            Unit unit, final Type type, boolean includeValue, String kind,
            final LinkedModeImporter importer) {
        if (type==null) {
            return new ICompletionProposal[0];
        }
        TypeDeclaration td = type.getDeclaration();
        List<TypeDeclaration> supertypes = 
                isTypeUnknown(type) || 
                type.isTypeConstructor() ?
                    Collections.<TypeDeclaration>emptyList() :
                    td.getSupertypeDeclarations();

        int size = supertypes.size();
        if (includeValue) size++;
        if (type.isTypeConstructor() ||
                type.isUnion() || 
                type.isIntersection()) {
            size++;
        }
        
        ICompletionProposal[] typeProposals = 
                new ICompletionProposal[size];
        int i=0;
        if (includeValue) {
            typeProposals[i++] =
                    new LinkedModeCompletionProposal(kind, offset, kind, 0, 
                            getDecoratedImage(CEYLON_LITERAL, 0, false)) {
                @Override
                public void apply(IDocument document) {
                    super.apply(document);
                    importer.setImportedType(null);
                }
            };
        }
        if (type.isTypeConstructor() ||
                type.isUnion() || 
                type.isIntersection()) {
            typeProposals[i++] = 
                    new LinkedModeCompletionProposal(type, unit, offset, 0) {
                @Override
                public void apply(IDocument document) {
                    super.apply(document);
                    importer.setImportedType(type);
                }
            };
        }
        Collections.sort(supertypes, 
                new Comparator<TypeDeclaration>() {
            @Override
            public int compare(TypeDeclaration x, 
                    TypeDeclaration y) {
                if (x.inherits(y)) {
                    return 1;
                }
                if (y.inherits(x)) {
                    return -1;
                }
                return y.getName().compareTo(x.getName());
            }
        });
        for (int j=supertypes.size()-1; j>=0; j--) {
            final Type supertype = 
                    type.getSupertype(supertypes.get(j));
            typeProposals[i++] = 
                    new LinkedModeCompletionProposal(supertype, unit, offset, 0) {
                @Override
                public void apply(IDocument document) {
                    super.apply(document);
                    importer.setImportedType(supertype);
                }
            };
        }
        return typeProposals;
    }

    public static ICompletionProposal[] getCaseTypeProposals(int offset, 
            Unit unit, Type type) {
        if (type==null) {
            return new ICompletionProposal[0];
        }
        List<Type> caseTypes = type.getCaseTypes();
        if (caseTypes==null) {
            return new ICompletionProposal[0];
        }
        ICompletionProposal[] typeProposals = 
                new ICompletionProposal[caseTypes.size()];
        for (int i=0; i<caseTypes.size(); i++) {
            Type ct = caseTypes.get(i);
            typeProposals[i] = 
                    new LinkedModeCompletionProposal(ct, unit, offset, 0);
        }
        return typeProposals;
    }
}