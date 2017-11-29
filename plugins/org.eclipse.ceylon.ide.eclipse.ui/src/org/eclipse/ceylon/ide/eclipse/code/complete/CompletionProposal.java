/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.COMPLETION;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isNameMatching;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.common.doc.Icons;
import org.eclipse.ceylon.model.typechecker.model.Declaration;


public class CompletionProposal implements ICompletionProposal, 
        ICompletionProposalExtension2, ICompletionProposalExtension4, 
        ICompletionProposalExtension6, ICompletionProposalExtension3,
        ICompletionProposalExtension5 {
    
    protected final String text;
    private final ImageRetriever imageRetriever;
    protected final String prefix;
    private final String description;
    protected int offset;
    private int length;
    private boolean toggleOverwrite;
    protected String currentPrefix;
    
    public static interface ImageRetriever {
        Image getImage();
    }
    
    public static class DeclarationImageRetriever implements ImageRetriever {
        private Declaration decl;
        public DeclarationImageRetriever(Declaration declaration) {
            decl = declaration;
        }
        @Override
        public Image getImage() {
            return getImageForDeclaration(decl);
        }
    }

    public static class IconImageRetriever implements ImageRetriever {
        private Icons icon;
        public IconImageRetriever(Icons icon) {
            this.icon = icon;
        }
        @Override
        public Image getImage() {
            return org.eclipse.ceylon.ide.eclipse.util.eclipseIcons_.get_().fromIcons(icon);
        }
    }

    public static class FixedImageRetriever implements ImageRetriever {
        private Image image;
        public FixedImageRetriever(Image image) {
            this.image = image;
        }
        @Override
        public Image getImage() {
            return image;
        }
    }

    public CompletionProposal(int offset, String prefix, ImageRetriever imageRetriever,
            String desc, String text) {
        this.text = text;
        this.imageRetriever = imageRetriever;
        this.offset = offset;
        this.prefix = prefix;
        currentPrefix = prefix;
        this.length = prefix.length();
        this.description = desc;
        Assert.isNotNull(description);
    }
    
    @Override
    public Image getImage() {
        return imageRetriever.getImage();
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
        String overwrite = CeylonPlugin.getPreferences().getString(COMPLETION);
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

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
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
        Highlights.styleFragment(result, 
        		getDisplayString(), 
                qualifiedNameIsPath(), 
                currentPrefix,
                CeylonPlugin.getCompletionFont());
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
        currentPrefix = getCurrentPrefix(document, offset);
        return currentPrefix==null ? false :
        	isProposalMatching(currentPrefix, text);
    }
    
    /**
     * To be overridden by subclasses
     */
    protected boolean isProposalMatching(String currentPrefix, String text){
        return isNameMatching(currentPrefix, text);
    }

	String getCurrentPrefix(IDocument document, int offset) {
		try {
			int start = this.offset-prefix.length();
			return document.get(start, offset-start);
		}
		catch (BadLocationException e) {
			return null;
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