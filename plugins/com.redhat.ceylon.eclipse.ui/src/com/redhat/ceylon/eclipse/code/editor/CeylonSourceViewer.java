package com.redhat.ceylon.eclipse.code.editor;

/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/


import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class CeylonSourceViewer extends ProjectionViewer {
    /**
     * Text operation code for requesting the outline for the current input.
     */
    public static final int SHOW_OUTLINE= 51;

    /**
     * Text operation code for requesting the outline for the element at the current position.
     */
    public static final int OPEN_STRUCTURE= 52;

    /**
     * Text operation code for requesting the hierarchy for the current input.
     */
    public static final int SHOW_HIERARCHY= 53;

    /**
     * Text operation code for requesting the code for the current input.
     */
    public static final int SHOW_CODE= 56;

    /**
     * Text operation code for toggling the commenting of a selected range of text, or the current line.
     */
    public static final int TOGGLE_COMMENT= 54;
    
    public static final int ADD_BLOCK_COMMENT= 57;

    public static final int REMOVE_BLOCK_COMMENT= 58;

    /**
     * Text operation code for toggling the display of "occurrences" of the
     * current selection, whatever that means to the current language.
     */
    public static final int MARK_OCCURRENCES= 55;

    /**
     * Text operation code for correcting the indentation of the currently selected text.
     */
    public static final int CORRECT_INDENTATION= 60;

    private IInformationPresenter outlinePresenter;
    private IInformationPresenter structurePresenter;
    private IInformationPresenter hierarchyPresenter;
    private IInformationPresenter codePresenter;
    private IAutoEditStrategy autoEditStrategy;

    public CeylonSourceViewer(Composite parent, IVerticalRuler verticalRuler, 
    		IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles) {
        super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
    }

    public boolean canDoOperation(int operation) {
        switch(operation) {
        case SHOW_OUTLINE:
            return outlinePresenter!=null;
        case OPEN_STRUCTURE:
            return structurePresenter!=null;
        case SHOW_HIERARCHY:
            return hierarchyPresenter!=null;
        case SHOW_CODE:
        	return codePresenter!=null;
        case ADD_BLOCK_COMMENT: //TODO: check if something is selected! 
        case REMOVE_BLOCK_COMMENT: //TODO: check if there is a block comment in the selection!
        case TOGGLE_COMMENT:
            return true;
        case CORRECT_INDENTATION:
            return autoEditStrategy!=null;
        }
        return super.canDoOperation(operation);
    }

    public void doOperation(int operation) {
        if (getTextWidget() == null)
            return;
        switch (operation) {
        case SHOW_OUTLINE:
            if (outlinePresenter!=null)
                outlinePresenter.showInformation();
            return;
        case OPEN_STRUCTURE:
            if (structurePresenter!=null)
                structurePresenter.showInformation();
            return;
        case SHOW_HIERARCHY:
            if (hierarchyPresenter!=null)
                hierarchyPresenter.showInformation();
            return;
        case SHOW_CODE:
        	if (codePresenter!=null)
        		codePresenter.showInformation();
        	return;
        case TOGGLE_COMMENT:
            doToggleComment();
            return;
        case ADD_BLOCK_COMMENT:
            addBlockComment();
            return;
        case REMOVE_BLOCK_COMMENT:
            return;
        case CORRECT_INDENTATION:
            doCorrectIndentation();
            return;
        }
        super.doOperation(operation);
    }
    
    private void addBlockComment() {
        IDocument doc= this.getDocument();
        DocumentRewriteSession rewriteSession= null;
        Point p= this.getSelectedRange();

        if (doc instanceof IDocumentExtension4) {
            IDocumentExtension4 extension= (IDocumentExtension4) doc;
            rewriteSession= extension.startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
        }

        try {
            final int selStart= p.x;
            final int selLen= p.y;
            final int selEnd= selStart + selLen;
            doc.replace(selStart, 0, "/*");
            doc.replace(selEnd+2, 0, "*/");
        } catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            if (doc instanceof IDocumentExtension4) {
                IDocumentExtension4 extension= (IDocumentExtension4) doc;
                extension.stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }
    private void doToggleComment() {
        IDocument doc= this.getDocument();
        DocumentRewriteSession rewriteSession= null;
        Point p= this.getSelectedRange();
        final String lineCommentPrefix= "//";

    	if (doc instanceof IDocumentExtension4) {
    	    IDocumentExtension4 extension= (IDocumentExtension4) doc;
    	    rewriteSession= extension.startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
    	}

    	try {
            final int selStart= p.x;
            final int selLen= p.y;
            final int selEnd= selStart + selLen;
            final int startLine= doc.getLineOfOffset(selStart);
            int endLine= doc.getLineOfOffset(selEnd);

            if (selLen > 0 && lookingAtLineEnd(doc, selEnd))
                endLine--;

            boolean linesAllHaveCommentPrefix= linesHaveCommentPrefix(doc, lineCommentPrefix, startLine, endLine);
        	boolean useCommonLeadingSpace= true; // take from a preference?
			int leadingSpaceToUse= useCommonLeadingSpace ? calculateLeadingSpace(doc, startLine, endLine) : 0;

            for(int line= startLine; line <= endLine; line++) {
                int lineStart= doc.getLineOffset(line);
                int lineEnd= lineStart + doc.getLineLength(line) - 1;

                if (linesAllHaveCommentPrefix) {
                	// remove the comment prefix from each line, wherever it occurs in the line
                	int offset= lineStart;
                    while (Character.isWhitespace(doc.getChar(offset)) && offset < lineEnd) {
                        offset++;
                    }
                    // The first non-whitespace characters *must* be the single-line comment prefix
                    doc.replace(offset, lineCommentPrefix.length(), "");
                } else {
                	// add the comment prefix to each line, after however many spaces leadingSpaceToAdd indicates
                	int offset= lineStart + leadingSpaceToUse;
                	doc.replace(offset, 0, lineCommentPrefix);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            if (doc instanceof IDocumentExtension4) {
                IDocumentExtension4 extension= (IDocumentExtension4) doc;
                extension.stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }

    private int calculateLeadingSpace(IDocument doc, int startLine, int endLine) {
    	try {
        	int result= Integer.MAX_VALUE;
        	for(int line= startLine; line <= endLine; line++) {
        		int lineStart= doc.getLineOffset(line);
        		int lineEnd= lineStart + doc.getLineLength(line) - 1;
        		int offset= lineStart;
        		while (Character.isWhitespace(doc.getChar(offset)) && offset < lineEnd) {
        			offset++;
        		}
        		int leadingSpaces= offset - lineStart;
				result= Math.min(result, leadingSpaces);
        	}
    		return result;
    	} catch (BadLocationException e) {
    		return 0;
    	}
	}

	/**
     * @return true, if the given inclusive range of lines all start with the single-line comment prefix,
     * even if they have different amounts of leading whitespace
     */
    private boolean linesHaveCommentPrefix(IDocument doc, String lineCommentPrefix, int startLine, int endLine) {
    	try {
    		int docLen= doc.getLength();

    		for(int line= startLine; line <= endLine; line++) {
                int lineStart= doc.getLineOffset(line);
                int lineEnd= lineStart + doc.getLineLength(line) - 1;
                int offset= lineStart;

                while (Character.isWhitespace(doc.getChar(offset)) && offset < lineEnd) {
                    offset++;
                }
                if (docLen - offset > lineCommentPrefix.length() && doc.get(offset, lineCommentPrefix.length()).equals(lineCommentPrefix)) {
                	// this line starts with the single-line comment prefix
                } else {
                	return false;
                }
            }
    	} catch (BadLocationException e) {
    		return false;
    	}
		return true;
	}

	private void doCorrectIndentation() {
        IDocument doc= getDocument();
        DocumentRewriteSession rewriteSession= null;
        Point p= this.getSelectedRange();

        if (doc instanceof IDocumentExtension4) {
            IDocumentExtension4 extension= (IDocumentExtension4) doc;
            rewriteSession= extension.startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
        }

        try {
            final int selStart= p.x;
            final int selLen= p.y;
            final int selEnd= selStart + selLen;
            final int startLine= doc.getLineOfOffset(selStart);
            int endLine= doc.getLineOfOffset(selEnd);

        	// If the selection extends just to the beginning of the next line, don't indent that one too
            if (selLen > 0 && lookingAtLineEnd(doc, selEnd)) {
                endLine--;
            }

            // Indent each line using the AutoEditStrategy
            for(int line= startLine; line <= endLine; line++) {
                int lineStartOffset= doc.getLineOffset(line);

                // Replace the existing indentation with the desired indentation.
                // Use the language-specific AutoEditStrategy, which requires a DocumentCommand.
                DocumentCommand cmd= new DocumentCommand() { };
                cmd.offset= lineStartOffset;
                cmd.length= 0;
                cmd.text= Character.toString('\t');
                cmd.doit= true;
                cmd.shiftsCaret= false;
//              boolean saveMode= fAutoEditStrategy.setFixMode(true);
                autoEditStrategy.customizeDocumentCommand(doc, cmd);
//              fAutoEditStrategy.setFixMode(saveMode);
                doc.replace(cmd.offset, cmd.length, cmd.text);
            }
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            if (doc instanceof IDocumentExtension4) {
                IDocumentExtension4 extension= (IDocumentExtension4) doc;
                extension.stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }

    private boolean lookingAtLineEnd(IDocument doc, int pos) {
        String[] legalLineTerms= doc.getLegalLineDelimiters();
        try {
            for(String lineTerm: legalLineTerms) {
                int len= lineTerm.length();
                if (pos > len && doc.get(pos - len, len).equals(lineTerm)) {
                    return true;
                }
            }
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void configure(SourceViewerConfiguration configuration) {
        /*
         * Prevent access to colors disposed in unconfigure(), see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=53641
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=86177
         */
        StyledText textWidget= getTextWidget();
        if (textWidget != null && !textWidget.isDisposed()) {
            Color foregroundColor= textWidget.getForeground();
            if (foregroundColor != null && foregroundColor.isDisposed())
                textWidget.setForeground(null);
            Color backgroundColor= textWidget.getBackground();
            if (backgroundColor != null && backgroundColor.isDisposed())
                textWidget.setBackground(null);
        }
        super.configure(configuration);
        getTextHoveringController().setSizeConstraints(80, 30, false, true);
        if (configuration instanceof CeylonSourceViewerConfiguration) {
            CeylonSourceViewerConfiguration svc= (CeylonSourceViewerConfiguration) configuration;

            outlinePresenter= svc.getOutlinePresenter(this);
            if (outlinePresenter!=null)
                outlinePresenter.install(this);

            structurePresenter= svc.getOutlinePresenter(this);
            if (structurePresenter!=null)
                structurePresenter.install(this);

            hierarchyPresenter= svc.getHierarchyPresenter(this, true);
            if (hierarchyPresenter!=null)
                hierarchyPresenter.install(this);
            
            codePresenter = svc.getCodePresenter(this);
            if (codePresenter!=null)
            	codePresenter.install(this);

            autoEditStrategy = new CeylonAutoEditStrategy(svc.editor);
            
            fQuickAssistAssistant = svc.getQuickAssistAssistant(this);
            if (fQuickAssistAssistant != null)
            	fQuickAssistAssistant.install(this);
        }
        //	if (fPreferenceStore != null) {
        //	    fPreferenceStore.addPropertyChangeListener(this);
        //	    initializeViewerColors();
        //	}
        
        setTextHover(configuration.getTextHover(this,DEFAULT_CONTENT_TYPE), 
        		DEFAULT_CONTENT_TYPE);
    }

    public void unconfigure() {
        if (outlinePresenter != null) {
            outlinePresenter.uninstall();
            outlinePresenter= null;
        }
        if (structurePresenter != null) {
            structurePresenter.uninstall();
            structurePresenter= null;
        }
        if (hierarchyPresenter != null) {
            hierarchyPresenter.uninstall();
            hierarchyPresenter= null;
        }
        // if (fForegroundColor != null) {
        // fForegroundColor.dispose();
        // fForegroundColor= null;
        // }
        // if (fBackgroundColor != null) {
        // fBackgroundColor.dispose();
        // fBackgroundColor= null;
        //	}
        //	if (fPreferenceStore != null)
        //	    fPreferenceStore.removePropertyChangeListener(this);
        super.unconfigure();
    }
    
}
