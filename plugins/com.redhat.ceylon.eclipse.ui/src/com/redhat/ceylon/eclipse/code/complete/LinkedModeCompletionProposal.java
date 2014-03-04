package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class LinkedModeCompletionProposal 
        implements ICompletionProposal {
    
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
            document.replace(start, length, text);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
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
    
    private static final ICompletionProposal[] NO_COMPLETIONS = new ICompletionProposal[0];
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
        return nameProposals.toArray(NO_COMPLETIONS);
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