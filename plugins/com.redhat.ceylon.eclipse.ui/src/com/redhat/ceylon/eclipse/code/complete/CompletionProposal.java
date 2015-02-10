package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isNameMatching;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.COMPLETION;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;


public class CompletionProposal implements ICompletionProposal, 
        ICompletionProposalExtension2, ICompletionProposalExtension4, 
        ICompletionProposalExtension6, ICompletionProposalExtension3 {
    
    protected final String text;
    private final Image image;
    protected final String prefix;
    private final String description;
    protected int offset;
    private int length;
    private boolean toggleOverwrite;
    
    public CompletionProposal(int offset, String prefix, Image image,
            String desc, String text) {
        this.text = text;
        this.image = image;
        this.offset = offset;
        this.prefix = prefix;
        this.length = prefix.length();
        this.description = desc;
        Assert.isNotNull(description);
    }
    
    @Override
    public Image getImage() {
        return image;
    }
    
    @Override
    public Point getSelection(IDocument document) {
        return new Point(offset + text.length() - prefix.length(), 0);
    }
    
    public void apply(IDocument document) {
        try {
            document.replace(start(), length(document), 
                    withoutDupeSemi(document));
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    protected ReplaceEdit createEdit(IDocument document) {
        return new ReplaceEdit(start(), length(document), 
                withoutDupeSemi(document));
    }

    public int length(IDocument document) {
        String overwrite = EditorUtil.getPreferences().getString(COMPLETION);
        if ("overwrite".equals(overwrite)!=toggleOverwrite) {
            int length = prefix.length();
            try {
                for (int i=offset; 
                        i<document.getLength() && 
                        Character.isJavaIdentifierPart(document.getChar(i)); 
                        i++) {
                    length++;
                }
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
            return length;
        }
        else {
            return this.length;
        }
    }

    public int start() {
        return offset-prefix.length();
    }

    public String withoutDupeSemi(IDocument document) {
        try {
            if (text.endsWith(";") && 
                    document.getChar(offset)==';') {
                return text.substring(0,text.length()-1);
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        return text;
    }
    
    public String getDisplayString() {
        return description;
    }

    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public boolean isAutoInsertable() {
        return true;
    }
    
    protected boolean qualifiedNameIsPath() {
        return false;
    }

    @Override
    public StyledString getStyledDisplayString() {
        StyledString result = new StyledString();
        Highlights.styleProposal(result, getDisplayString(), 
                qualifiedNameIsPath());
        return result;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask,
            int offset) {
        toggleOverwrite = (stateMask&SWT.CTRL)!=0;
        length = prefix.length() + offset - this.offset;
        apply(viewer.getDocument());
    }

    @Override
    public void selected(ITextViewer viewer, boolean smartToggle) {}

    @Override
    public void unselected(ITextViewer viewer) {}

    @Override
    public boolean validate(IDocument document, int offset, DocumentEvent event) {
        if (offset<this.offset) {
            return false;
        }
        try {
            //TODO: really this strategy is only applicable
            //      for completion of declaration names, so
            //      move this implementation to subclasses
            int start = this.offset-prefix.length();
            String typedText = document.get(start, offset-start);
            return isNameMatching(typedText, text);
//            String typedText = document.get(this.offset, offset-this.offset);
//            return text.substring(prefix.length())
//                       .startsWith(typedText);
        }
        catch (BadLocationException e) {
            return false;
        }
    }

    @Override
    public IInformationControlCreator getInformationControlCreator() {
        return null;
    }

    @Override
    public CharSequence getPrefixCompletionText(IDocument document,
            int completionOffset) {
        return withoutDupeSemi(document);
    }

    @Override
    public int getPrefixCompletionStart(IDocument document, int completionOffset) {
        return start();
    }
    
}