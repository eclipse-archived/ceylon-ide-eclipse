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


import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importEdits;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.PASTE_CORRECT_INDENTATION;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyView.showHierarchyView;
import static com.redhat.ceylon.eclipse.util.Nodes.getTokenStrictlyContainingOffset;
import static org.eclipse.jface.text.DocumentRewriteSessionType.SEQUENTIAL;
import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.redhat.ceylon.compiler.java.tools.NewlineFixingStringStream;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Import;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CeylonSourceViewer extends ProjectionViewer implements IPropertyChangeListener {
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
    public static final int SHOW_DEFINITION= 56;

    /**
     * Text operation code for requesting the references for the current input.
     */
    public static final int SHOW_REFERENCES= 59;

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

    /**
     * Text operation code for requesting the hierarchy for the current input.
     */
    public static final int SHOW_IN_HIERARCHY_VIEW= 70;

    private IInformationPresenter outlinePresenter;
    private IInformationPresenter structurePresenter;
    private IInformationPresenter hierarchyPresenter;
    private IInformationPresenter definitionPresenter;
    private IInformationPresenter referencesPresenter;
    private IAutoEditStrategy autoEditStrategy;
    private CeylonEditor editor;

    private Color foregroundColor;
    private Color backgroundColor;
    private Color selectionForegroundColor;
    private Color selectionBackgroundColor;
    
    public CeylonSourceViewer(CeylonEditor ceylonEditor, Composite parent, IVerticalRuler verticalRuler, 
            IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles) {
        super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
        this.editor = ceylonEditor;
    }

    public CeylonSourceViewer(Composite parent, IVerticalRuler verticalRuler, 
            IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles) {
        this(null, parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
    }

    public boolean canDoOperation(int operation) {
        switch(operation) {
        case SHOW_OUTLINE:
            return outlinePresenter!=null;
        case OPEN_STRUCTURE:
            return structurePresenter!=null;
        case SHOW_HIERARCHY:
            return hierarchyPresenter!=null;
        case SHOW_DEFINITION:
            return definitionPresenter!=null;
        case SHOW_REFERENCES:
            return referencesPresenter!=null;
        case SHOW_IN_HIERARCHY_VIEW:
            return true;
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
        try {
            if (getTextWidget() == null) {
                super.doOperation(operation);
                return;
            }
            String selectedText = editor.getSelectionText();
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
            case SHOW_DEFINITION:
                if (definitionPresenter!=null)
                    definitionPresenter.showInformation();
                return;
            case SHOW_REFERENCES:
                if (referencesPresenter!=null)
                    referencesPresenter.showInformation();
                return;
            case SHOW_IN_HIERARCHY_VIEW:
                showHierarchy();
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
                Point selectedRange = getSelectedRange();
                doCorrectIndentation(selectedRange.x, selectedRange.y);
                return;
            case PASTE:
                if (localPaste()) return;
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
                afterCopyCut(selectedText, imports);
                break;
            /*case PASTE:
                afterPaste(textWidget);
                break;*/
            }
        }
        catch (Exception e) {
            //never propagate exceptions
            e.printStackTrace();
        }
    }

    public void showHierarchy() throws PartInitException {
        showHierarchyView().focusOnSelection(editor);
    }
    
    private void afterCopyCut(String selection, Map<Declaration,String> imports) {
        if (imports!=null && !editor.isBlockSelectionModeEnabled()) {
            char c = 
                    editor.getSelectedNode() 
                        instanceof Tree.Literal ? 
                            '"' : '{';
            Clipboard clipboard = 
                    new Clipboard(getTextWidget().getDisplay());
            try {
                Object text = 
                        clipboard.getContents(TextTransfer.getInstance());
                Object rtf = 
                        clipboard.getContents(RTFTransfer.getInstance());
                
                try {
                    Object[] data;
                    Transfer[] dataTypes;
                    if (rtf==null) {
                        data = new Object[] { text, imports, c + selection };
                        dataTypes = new Transfer[] {
                                TextTransfer.getInstance(),
                                ImportsTransfer.INSTANCE, 
                                SourceTransfer.INSTANCE
                            };
                    }
                    else {
                        data = new Object[] { text, rtf, imports, c + selection };
                        dataTypes = new Transfer[] {
                                TextTransfer.getInstance(),
                                RTFTransfer.getInstance(),
                                ImportsTransfer.INSTANCE, 
                                SourceTransfer.INSTANCE
                            };
                    }
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
    
    private boolean localPaste() {
        if (!editor.isBlockSelectionModeEnabled()) {
            Clipboard clipboard = 
                    new Clipboard(getTextWidget().getDisplay());
            try {
                String text = (String) clipboard.getContents(SourceTransfer.INSTANCE);
                boolean fromStringLiteral;
                if (text==null) {
                    fromStringLiteral = false;
                    text = (String) clipboard.getContents(TextTransfer.getInstance());
                }
                else {
                    fromStringLiteral = text.charAt(0)=='"';
                    text = text.substring(1);
                }
                if (text==null) {
                    return false;
                }
                else {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Map<Declaration,String> imports = 
                            (Map) clipboard.getContents(ImportsTransfer.INSTANCE);
                    IRegion selection = editor.getSelection();
                    int offset = selection.getOffset();
                    int length = selection.getLength();
                    int endOffset = offset+length;
                    
                    IDocument doc = this.getDocument();
                    DocumentRewriteSession rewriteSession= null;
                    if (doc instanceof IDocumentExtension4) {
                        rewriteSession = 
                                ((IDocumentExtension4) doc).startRewriteSession(SEQUENTIAL);
                    }
                    
                    ANTLRStringStream stream = 
                            new NewlineFixingStringStream(doc.get());
                    CeylonLexer lexer = new CeylonLexer(stream);
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    tokens.fill();
                    CommonToken token = getTokenStrictlyContainingOffset(offset, 
                            tokens.getTokens());
                    boolean quoted;
                    boolean verbatim;
                    if (token == null) {
                        quoted = false;
                        verbatim = false;
                    }
                    else {
                        int tt = token.getType();
                        quoted = tt==CeylonLexer.STRING_LITERAL || 
                                tt==CeylonLexer.STRING_END || 
                                tt==CeylonLexer.STRING_END || 
                                tt==CeylonLexer.STRING_MID;
                        verbatim = token.getText().startsWith("\"\"\"");
                    }
                    
                    try {
                        boolean startOfLine = false;
                        try {
                            int lineStart = doc.getLineInformationOfOffset(offset).getOffset();
                            startOfLine = doc.get(lineStart, offset-lineStart).trim().isEmpty();
                        }
                        catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                        try {
                            MultiTextEdit edit = new MultiTextEdit();
                            if (!quoted && imports!=null) {
                                pasteImports(imports, edit, text, doc);
                            }
                            if (quoted && !verbatim && !fromStringLiteral) {
                                text = text
                                        .replace("\\", "\\\\")
                                        .replace("\t", "\\t")
                                        .replace("\"", "\\\"")
                                        .replace("`", "\\`");
                            }
                            edit.addChild(new ReplaceEdit(offset, length, text));
                            edit.apply(doc);
                            endOffset = edit.getRegion().getOffset()+edit.getRegion().getLength();
                        } 
                        catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        try {
                            if (startOfLine && 
                                    EditorsUI.getPreferenceStore().getBoolean(PASTE_CORRECT_INDENTATION)) {
                                endOffset = correctSourceIndentation(endOffset-text.length(), text.length(), doc)+1;
                            }
                            return true;
                        } 
                        catch (Exception e) {
                            e.printStackTrace();
                            return true;
                        }
                    }
                    finally {
                        if (doc instanceof IDocumentExtension4) {
                            ((IDocumentExtension4) doc).stopRewriteSession(rewriteSession);
                        }
                        setSelectedRange(endOffset, 0);
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
        IDocument doc = this.getDocument();
        DocumentRewriteSession rewriteSession = null;
        Point p = this.getSelectedRange();

        if (doc instanceof IDocumentExtension4) {
            rewriteSession= ((IDocumentExtension4) doc).startRewriteSession(SEQUENTIAL);
        }

        try {
            final int selStart = p.x;
            final int selLen = p.y;
            final int selEnd = selStart+selLen;
            doc.replace(selStart, 0, "/*");
            doc.replace(selEnd+2, 0, "*/");
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        } 
        finally {
            if (doc instanceof IDocumentExtension4) {
                ((IDocumentExtension4) doc).stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }
    
    private void removeBlockComment() {
        IDocument doc = this.getDocument();
        DocumentRewriteSession rewriteSession = null;
        Point p = this.getSelectedRange();

        if (doc instanceof IDocumentExtension4) {
            IDocumentExtension4 extension= (IDocumentExtension4) doc;
            rewriteSession= extension.startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
        }

        try {
            final int selStart = p.x;
            final int selLen = p.y;
            final int selEnd = selStart+selLen;
            String text = doc.get();
            int open = text.indexOf("/*", selStart);
            if (open>selEnd) open = -1;
            if (open<0) {
                open = text.lastIndexOf("/*", selStart);
            }
            int close = -1;
            if (open>=0) {
                close = text.indexOf("*/", open);
            }
            if (close+2<selStart) close = -1;
            if (open>=0&&close>=0) {
                doc.replace(open, 2, "");
                doc.replace(close-2, 2, "");
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        finally {
            if (doc instanceof IDocumentExtension4) {
                ((IDocumentExtension4) doc).stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }
    
    private void doToggleComment() {
        IDocument doc = this.getDocument();
        DocumentRewriteSession rewriteSession = null;
        Point p = this.getSelectedRange();
        final String lineCommentPrefix = "//";

        if (doc instanceof IDocumentExtension4) {
            rewriteSession= ((IDocumentExtension4) doc).startRewriteSession(SEQUENTIAL);
        }

        try {
            final int selStart = p.x;
            final int selLen = p.y;
            final int selEnd = selStart+selLen;
            final int startLine= doc.getLineOfOffset(selStart);
            int endLine= doc.getLineOfOffset(selEnd);

            if (selLen>0 && lookingAtLineEnd(doc, selEnd))
                endLine--;

            boolean linesAllHaveCommentPrefix = linesHaveCommentPrefix(doc, lineCommentPrefix, startLine, endLine);
            boolean useCommonLeadingSpace = true; // take from a preference?
            int leadingSpaceToUse = useCommonLeadingSpace ? calculateLeadingSpace(doc, startLine, endLine) : 0;

            for(int line = startLine; line<=endLine; line++) {
                int lineStart = doc.getLineOffset(line);
                int lineEnd = lineStart+doc.getLineLength(line)-1;

                if (linesAllHaveCommentPrefix) {
                    // remove the comment prefix from each line, wherever it occurs in the line
                    int offset = lineStart;
                    while (Character.isWhitespace(doc.getChar(offset)) && offset<lineEnd) {
                        offset++;
                    }
                    // The first non-whitespace characters *must* be the single-line comment prefix
                    doc.replace(offset, lineCommentPrefix.length(), "");
                }
                else {
                    // add the comment prefix to each line, after however many spaces leadingSpaceToAdd indicates
                    int offset = lineStart+leadingSpaceToUse;
                    doc.replace(offset, 0, lineCommentPrefix);
                }
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        finally {
            if (doc instanceof IDocumentExtension4) {
                ((IDocumentExtension4) doc).stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }

    private int calculateLeadingSpace(IDocument doc, int startLine, int endLine) {
        try {
            int result = Integer.MAX_VALUE;
            for(int line = startLine; line <= endLine; line++) {
                int lineStart = doc.getLineOffset(line);
                int lineEnd = lineStart + doc.getLineLength(line) - 1;
                int offset = lineStart;
                while (Character.isWhitespace(doc.getChar(offset)) && offset < lineEnd) {
                    offset++;
                }
                int leadingSpaces = offset - lineStart;
                result = Math.min(result, leadingSpaces);
            }
            return result;
        }
        catch (BadLocationException e) {
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
                if (docLen - offset > lineCommentPrefix.length() && 
                    doc.get(offset, lineCommentPrefix.length()).equals(lineCommentPrefix)) {
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

    private void doCorrectIndentation(int offset, int len) {
        
        IDocument doc = getDocument();
        DocumentRewriteSession rewriteSession= null;
        if (doc instanceof IDocumentExtension4) {
            rewriteSession = ((IDocumentExtension4) doc).startRewriteSession(SEQUENTIAL);
        }
        
        
        Point selectedRange = getSelectedRange();
        boolean emptySelection = selectedRange==null || selectedRange.y==0;
        
        try {
            correctSourceIndentation(offset, len, doc);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        finally {
            if (doc instanceof IDocumentExtension4) {
                ((IDocumentExtension4) doc).stopRewriteSession(rewriteSession);
            }
            restoreSelection();
            if (emptySelection) {
                selectedRange = getSelectedRange();
                setSelectedRange(selectedRange.x/*+selectedRange.y*/, 0);
            }
        }
    }

    public int correctSourceIndentation(int selStart, int selLen, IDocument doc)
            throws BadLocationException {
        int selEnd = selStart + selLen;
        int startLine = doc.getLineOfOffset(selStart);
        int endLine = doc.getLineOfOffset(selEnd);

        // If the selection extends just to the beginning of the next line, don't indent that one too
        if (selLen > 0 && lookingAtLineEnd(doc, selEnd)) {
            endLine--;
        }
        
        int endOffset = selStart+selLen-1;
        // Indent each line using the AutoEditStrategy
        for (int line=startLine; line<=endLine; line++) {
            int lineStartOffset = doc.getLineOffset(line);

            // Replace the existing indentation with the desired indentation.
            // Use the language-specific AutoEditStrategy, which requires a DocumentCommand.
            DocumentCommand cmd = new DocumentCommand() { };
            cmd.offset = lineStartOffset;
            cmd.length = 0;
            cmd.text = Character.toString('\t');
            cmd.doit = true;
            cmd.shiftsCaret = false;
            autoEditStrategy.customizeDocumentCommand(doc, cmd);
            if (cmd.text!=null) {
                doc.replace(cmd.offset, cmd.length, cmd.text);
                endOffset += cmd.text.length()-cmd.length;
            }
        }
        return endOffset;
    }

    private boolean lookingAtLineEnd(IDocument doc, int pos) {
        String[] legalLineTerms = doc.getLegalLineDelimiters();
        try {
            for(String lineTerm: legalLineTerms) {
                int len = lineTerm.length();
                if (pos>len && doc.get(pos-len,len).equals(lineTerm)) {
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
//        StyledText textWidget = getTextWidget();
//        if (textWidget != null && !textWidget.isDisposed()) {
//            Color foregroundColor = textWidget.getForeground();
//            if (foregroundColor != null && foregroundColor.isDisposed()) {
//                textWidget.setForeground(null);
//            }
//            Color backgroundColor = textWidget.getBackground();
//            if (backgroundColor != null && backgroundColor.isDisposed()) {
//                textWidget.setBackground(null);
//            }
//        }
        
        super.configure(configuration);
        
        AbstractInformationControlManager hoverController = getTextHoveringController();
        if (hoverController!=null) { //null in a merge viewer
            hoverController.setSizeConstraints(80, 30, false, true);
        }
        
        if (configuration instanceof CeylonSourceViewerConfiguration) {
            CeylonSourceViewerConfiguration svc = (CeylonSourceViewerConfiguration) configuration;
            
            outlinePresenter = svc.getOutlinePresenter(this);
            if (outlinePresenter!=null) {
                outlinePresenter.install(this);
            }
            
            structurePresenter = svc.getOutlinePresenter(this);
            if (structurePresenter!=null) {
                structurePresenter.install(this);
            }
            
            hierarchyPresenter = svc.getHierarchyPresenter(this);
            if (hierarchyPresenter!=null) {
                hierarchyPresenter.install(this);
            }
            
            definitionPresenter = svc.getDefinitionPresenter(this);
            if (definitionPresenter!=null) {
                definitionPresenter.install(this);
            }
            
            referencesPresenter = svc.getReferencesPresenter(this);
            if (referencesPresenter!=null) {
                referencesPresenter.install(this);
            }
            
            autoEditStrategy = new CeylonAutoEditStrategy();
            
        }
        //    if (fPreferenceStore != null) {
        //        fPreferenceStore.addPropertyChangeListener(this);
        //        initializeViewerColors();
        //    }
        
        ITextHover textHover = configuration.getTextHover(this, DEFAULT_CONTENT_TYPE);
        if (textHover!=null) {
            setTextHover(textHover, DEFAULT_CONTENT_TYPE);
        }
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
        // }
        // if (fPreferenceStore != null)
        // fPreferenceStore.removePropertyChangeListener(this);
        super.unconfigure();
    }
    
    Map<Declaration,String> copyImports() {
    	try {
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
    				if (d!=null && id!=null && d.isToplevel()) {
    					String pname = d.getUnit().getPackage().getNameAsString();
    					if (!pname.isEmpty() &&
    							!pname.equals(Module.LANGUAGE_MODULE_NAME)) {
    						results.put(d, id.getText());
    					}
    				}
    			}
    			@Override
    			public void visit(Tree.BaseMemberOrTypeExpression that) {
    				if (inSelection(that)) {
    					addDeclaration(that.getDeclaration(), 
    							that.getIdentifier());
    				}
    				super.visit(that);
    			}
    			@Override
    			public void visit(Tree.BaseType that) {
    				if (inSelection(that)) {
    					addDeclaration(that.getDeclarationModel(), 
    							that.getIdentifier());
    				}
    				super.visit(that);
    			}
    			@Override
    			public void visit(Tree.MemberLiteral that) {
    				if (inSelection(that) && that.getType()==null) {
    					addDeclaration(that.getDeclaration(), 
    							that.getIdentifier());
    				}
    				super.visit(that);
    			}
    		}
    		SelectedImportsVisitor v = new SelectedImportsVisitor();
    		cu.visit(v);
    		return v.results;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    void pasteImports(Map<Declaration,String> map, MultiTextEdit edit, 
            String pastedText, IDocument doc) {
        if (!map.isEmpty()) {
            CeylonParseController pc = editor.getParseController();
            if (pc==null || pc.getRootNode()==null) return;
            Tree.CompilationUnit cu = pc.getRootNode();
            Unit unit = cu.getUnit();
            //copy them, so as to not affect the clipboard
            Map<Declaration,String> imports = 
                    new LinkedHashMap<Declaration,String>(); 
            imports.putAll(map);
            for (Iterator<Map.Entry<Declaration,String>> i=imports.entrySet().iterator(); 
                    i.hasNext();) {
                Map.Entry<Declaration,String> e = i.next();
                Declaration declaration = e.getKey();
                Package declarationPackage = declaration.getUnit().getPackage();
                Pattern pattern = Pattern.compile("\\bimport\\s+" + 
                        declarationPackage.getNameAsString().replace(".", "\\.") + 
                        "\\b[^.]");
                if (unit.getPackage().equals(declarationPackage)) {
                    //the declaration belongs to this package
                    i.remove();
                }
                else if (pattern.matcher(pastedText).find()) {
                    //i.e. the pasted text probably already contains the import
                    i.remove();
                }
                else {
                    for (Import ip: unit.getImports()) {
                        //compare qualified names, treating
                        //overloaded versions as identical
                        if (ip.getDeclaration().getQualifiedNameString()
                                .equals(declaration.getQualifiedNameString())) {
                            i.remove();
                            break;
                        }
                    }
                }
            }
            if (!imports.isEmpty()) {
                List<InsertEdit> edits = 
                        importEdits(cu, imports.keySet(), imports.values(), 
                                null, doc);
                for (InsertEdit importEdit: edits) {
                    edit.addChild(importEdit);                    
                }
            }
        }
    }
    
    public IPresentationReconciler getPresentationReconciler() {
        return fPresentationReconciler;
    }
    
    public ContentAssistant getContentAssistant() {
        return (ContentAssistant) fContentAssistant;
    }
 
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR.equals(property))
        {
            initializeViewerColors();
        }
    }
    
    protected void initializeViewerColors() {
        if (EditorsUI.getPreferenceStore() != null) {

            StyledText styledText= getTextWidget();

            // ----------- foreground color --------------------
            Color color= EditorsUI.getPreferenceStore().getBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT)
            ? null
            : createColor(EditorsUI.getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, styledText.getDisplay());
            styledText.setForeground(color);

            if (foregroundColor != null)
                foregroundColor.dispose();

            foregroundColor= color;

            // ---------- background color ----------------------
            color= EditorsUI.getPreferenceStore().getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT)
            ? null
            : createColor(EditorsUI.getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, styledText.getDisplay());
            styledText.setBackground(color);

            if (backgroundColor != null)
                backgroundColor.dispose();

            backgroundColor= color;

            // ----------- selection foreground color --------------------
            color= EditorsUI.getPreferenceStore().getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR)
                ? null
                : createColor(EditorsUI.getPreferenceStore(), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_COLOR, styledText.getDisplay());
            styledText.setSelectionForeground(color);

            if (selectionForegroundColor != null)
                selectionForegroundColor.dispose();

            selectionForegroundColor= color;

            // ---------- selection background color ----------------------
            color= EditorsUI.getPreferenceStore().getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR)
                ? null
                : createColor(EditorsUI.getPreferenceStore(), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_COLOR, styledText.getDisplay());
            styledText.setSelectionBackground(color);

            if (selectionBackgroundColor != null)
                selectionBackgroundColor.dispose();

            selectionBackgroundColor= color;
        }
    }
    
    private Color createColor(IPreferenceStore store, String key, Display display) {

        RGB rgb= null;

        if (store.contains(key)) {

            if (store.isDefault(key))
                rgb= PreferenceConverter.getDefaultColor(store, key);
            else
                rgb= PreferenceConverter.getColor(store, key);

            if (rgb != null)
                return new Color(display, rgb);
        }

        return null;
    }    
}
