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


import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importEdit;
import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Import;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

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
    private CeylonEditor editor;

    public CeylonSourceViewer(CeylonEditor ceylonEditor, Composite parent, IVerticalRuler verticalRuler, 
    		IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles) {
        super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
        this.editor = ceylonEditor;
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
        StyledText textWidget= getTextWidget();
        if (textWidget == null) return;
        String selectedText = editor.getSelectionText(); //or textWidget.getSelectionText()
        Map<Declaration,String> imports = null;
        
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
            removeBlockComment();
            return;
        case CORRECT_INDENTATION:
            doCorrectIndentation(getSelectedRange());
            return;
        case PASTE:
            if (localPaste(textWidget)) return;
            break;
        case CUT:
        case COPY:
            imports = copyImports();
            break;
        }
        super.doOperation(operation);
        switch (operation) {
        case CUT:
        case COPY:
            afterCopyCut(textWidget, selectedText, imports);
            break;
        /*case PASTE:
            afterPaste(textWidget);
            break;*/
        }
    }

    private void afterCopyCut(StyledText textWidget, String selection,
            Map<Declaration,String> imports) {
        if (!editor.isBlockSelectionModeEnabled()) {
            Clipboard clipboard= new Clipboard(textWidget.getDisplay());
            try {
                Object text = clipboard.getContents(TextTransfer.getInstance());
                try {
                    if (imports==null) return;
                    Object[] data = new Object[] { text, imports, selection };
                    Transfer[] dataTypes = new Transfer[] { TextTransfer.getInstance(), ImportsTransfer.INSTANCE, SourceTransfer.INSTANCE };
                    clipboard.setContents(data, dataTypes);
                } 
                catch (SWTError e) {
                    if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
                        throw e;
                    }
                    e.printStackTrace();
                }       
            }
            finally {
                clipboard.dispose();
            }
        }
    }
    
    private boolean localPaste(StyledText textWidget) {
        if (!editor.isBlockSelectionModeEnabled()) {
            Clipboard clipboard= new Clipboard(textWidget.getDisplay());
            try {
                String text = (String) clipboard.getContents(SourceTransfer.INSTANCE);
                if (text==null) {
                    return false;
                }
                else {
                    Map<Declaration,String> imports = (Map<Declaration,String>) clipboard.getContents(ImportsTransfer.INSTANCE);
                    IRegion selection = editor.getSelection();
                    
                    IDocument doc= this.getDocument();
                    DocumentRewriteSession rewriteSession= null;
                    if (doc instanceof IDocumentExtension4) {
                        IDocumentExtension4 extension= (IDocumentExtension4) doc;
                        rewriteSession= extension.startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
                    }
                    
                    try {
                    	//TODO: no good reason to use Edits here
                        MultiTextEdit edit = new MultiTextEdit();
//                        DocumentChange c = new DocumentChange("paste", getDocument());
//                        c.setEdit(edit);
                        if (imports!=null) {
                            pasteImports(imports, edit);
                        }
                        edit.addChild(new ReplaceEdit(selection.getOffset(), selection.getLength(), text));
                        edit.apply(doc);
//                        c.perform(new NullProgressMonitor());
                        getTextWidget().setSelection(selection.getOffset()+text.length());
                        if (EditorsPlugin.getDefault().getPreferenceStore()
                        		.getBoolean(CeylonSourceViewerConfiguration.PASTE_CORRECT_INDENTATION)) {
                        	correctSourceIndentation(new Point(selection.getOffset(), text.length()+1), doc);
                        }
                        return true;
                    } 
                    catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    finally {
                        if (doc instanceof IDocumentExtension4) {
                            IDocumentExtension4 extension= (IDocumentExtension4) doc;
                            extension.stopRewriteSession(rewriteSession);
                        }
                        restoreSelection();
                    }
                }
            }
            finally {
                clipboard.dispose();
            }
        }
        else {
            return false;
        }
    }

    /*private void afterPaste(StyledText textWidget) {
        Clipboard clipboard= new Clipboard(textWidget.getDisplay());
        try {
            List<Declaration> imports = (List<Declaration>) clipboard.getContents(ImportsTransfer.INSTANCE);
            if (imports!=null) {
                MultiTextEdit edit = new MultiTextEdit();
                pasteImports(imports, edit);
                if (edit.hasChildren()) {
                    try {
                        edit.apply(getDocument());
                    } 
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        finally {
            clipboard.dispose();
        }
    }*/
    
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
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        } 
        finally {
            if (doc instanceof IDocumentExtension4) {
                IDocumentExtension4 extension= (IDocumentExtension4) doc;
                extension.stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }
    
    private void removeBlockComment() {
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
            String text = doc.get();
            int open = text.indexOf("/*", selStart);
            if (open>selEnd) open = -1;
            if (open<0) {
                open = text.lastIndexOf("/*", selStart);
            }
            int close=-1;
            if (open>=0) {
                close = text.indexOf("*/", open);
            }
            if (close+2<selStart) close = -1;
            if (open>=0&&close>=0) {
                doc.replace(open, 2, "");
                doc.replace(close-2, 2, "");
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

	private void doCorrectIndentation(Point range) {
		
        IDocument doc= getDocument();
        DocumentRewriteSession rewriteSession= null;
        if (doc instanceof IDocumentExtension4) {
            IDocumentExtension4 extension= (IDocumentExtension4) doc;
            rewriteSession= extension.startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
        }

        try {
            correctSourceIndentation(range, doc);
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

	public void correctSourceIndentation(Point range, IDocument doc)
			throws BadLocationException {
		final int selStart= range.x;
		final int selLen= range.y;
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
    
    Map<Declaration,String> copyImports() {
        CeylonParseController pc = editor.getParseController();
        if (pc==null || pc.getRootNode()==null) return null;
        Tree.CompilationUnit cu = pc.getRootNode();
        final IRegion selection = editor.getSelection();
        class SelectedImportsVisitor extends Visitor {
            Map<Declaration,String> results = new HashMap<Declaration,String>();
            boolean inSelection(Node node) {
                return node.getStartIndex()>=selection.getOffset() &&
                        node.getStopIndex()<selection.getOffset()+selection.getLength();
            }
            void addDeclaration(Declaration d, Tree.Identifier id) {
                if (d!=null && id!=null && d.isToplevel() && 
                        !d.getUnit().getPackage().getNameAsString()
                                .equals(Module.LANGUAGE_MODULE_NAME)) {
                    results.put(d, id.getText());
                }
            }
            @Override
            public void visit(Tree.BaseMemberExpression that) {
                if (inSelection(that)) {
                    addDeclaration(that.getDeclaration(), that.getIdentifier());
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.BaseTypeExpression that) {
                if (inSelection(that)) {
                    addDeclaration(that.getDeclaration(), that.getIdentifier());
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.BaseType that) {
                if (inSelection(that)) {
                    addDeclaration(that.getDeclarationModel(), that.getIdentifier());
                }
                super.visit(that);
            }
        }
        SelectedImportsVisitor v = new SelectedImportsVisitor();
        cu.visit(v);
        return v.results;
    }
    
    void pasteImports(Map<Declaration,String> map, MultiTextEdit edit) {
        if (!map.isEmpty()) {
            CeylonParseController pc = editor.getParseController();
            if (pc==null || pc.getRootNode()==null) return;
            Tree.CompilationUnit cu = pc.getRootNode();
            //copy them, so as to not affect the clipboard
            Map<Declaration,String> imports = new LinkedHashMap<Declaration,String>(); 
            imports.putAll(map);
            for (Iterator<Map.Entry<Declaration,String>> i=imports.entrySet().iterator(); 
                    i.hasNext();) {
                Map.Entry<Declaration,String> e = i.next();
                if (cu.getUnit().getPackage().equals(e.getKey().getUnit().getPackage())) {
                    i.remove();
                }
                else {
                    for (Import ip: cu.getUnit().getImports()) {
                        if (ip.getDeclaration().equals(e.getKey())) {
                            i.remove();
                            break;
                        }
                    }
                }
            }
            if (!imports.isEmpty()) {
                List<InsertEdit> edits = importEdit(cu, imports.keySet(), imports.values(), null);
                for (InsertEdit importEdit: edits) {
                    edit.addChild(importEdit);                    
                }
            }
        }
    }

}
