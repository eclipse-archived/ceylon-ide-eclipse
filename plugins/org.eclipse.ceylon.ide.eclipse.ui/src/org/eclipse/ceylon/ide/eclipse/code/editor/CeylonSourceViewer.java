package org.eclipse.ceylon.ide.eclipse.code.editor;

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


import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_MID;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static org.eclipse.ceylon.ide.eclipse.code.outline.HierarchyView.showHierarchyView;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PASTE_CORRECT_INDENTATION;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PASTE_ESCAPE_QUOTED;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PASTE_IMPORTS;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getTokenStrictlyContainingOffset;
import static java.lang.Character.isWhitespace;
import static org.eclipse.jface.text.DocumentRewriteSessionType.SEQUENTIAL;

import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.PartInitException;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonInterpolatingLexer;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.util.NewlineFixingStringStream;
import org.eclipse.ceylon.ide.eclipse.code.correct.correctJ2C;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.common.imports.SelectedImportsVisitor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

public class CeylonSourceViewer extends ProjectionViewer {
    /**
     * Text operation code for requesting the outline for 
     * the current input.
     */
    public static final int SHOW_OUTLINE= 51;

    /**
     * Text operation code for requesting the outline for 
     * the element at the current position.
     */
    public static final int OPEN_STRUCTURE= 52;

    /**
     * Text operation code for requesting the hierarchy for 
     * the current input.
     */
    public static final int SHOW_HIERARCHY= 53;

    /**
     * Text operation code for requesting the code for the 
     * current input.
     */
    public static final int SHOW_DEFINITION= 56;

    /**
     * Text operation code for requesting the references for 
     * the current input.
     */
    public static final int SHOW_REFERENCES= 59;

    /**
     * Text operation code for toggling the commenting of a 
     * selected range of text, or the current line.
     */
    public static final int TOGGLE_COMMENT= 54;
    
    public static final int ADD_BLOCK_COMMENT= 57;

    public static final int REMOVE_BLOCK_COMMENT= 58;

    /**
     * Text operation code for toggling the display of 
     * "occurrences" of the current selection.
     */
    public static final int MARK_OCCURRENCES= 55;

    /**
     * Text operation code for correcting the indentation of 
     * the currently selected text.
     */
    public static final int CORRECT_INDENTATION= 60;

    /**
     * Text operation code for requesting the hierarchy for 
     * the current input.
     */
    public static final int SHOW_IN_HIERARCHY_VIEW= 70;

    private IInformationPresenter outlinePresenter;
    private IInformationPresenter structurePresenter;
    private IInformationPresenter hierarchyPresenter;
    private IInformationPresenter definitionPresenter;
    private IInformationPresenter referencesPresenter;
    private IAutoEditStrategy autoEditStrategy;
    private CeylonEditor editor;

    public CeylonSourceViewer(CeylonEditor ceylonEditor, 
            Composite parent, IVerticalRuler verticalRuler, 
            IOverviewRuler overviewRuler, 
            boolean showAnnotationsOverview, int styles) {
        super(parent, verticalRuler, overviewRuler, 
                showAnnotationsOverview, styles);
        this.editor = ceylonEditor;
    }

    public CeylonSourceViewer(Composite parent, 
            IVerticalRuler verticalRuler, 
            IOverviewRuler overviewRuler, 
            boolean showAnnotationsOverview, int styles) {
        this(null, parent, verticalRuler, overviewRuler, 
                showAnnotationsOverview, styles);
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
                doCorrectIndentation(selectedRange.x, 
                        selectedRange.y);
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

    public void showHierarchy() 
            throws PartInitException {
        showHierarchyView().focusOnSelection(editor);
    }
    
    private void afterCopyCut(String selectedText, 
            Map<Declaration,String> imports) {
        if (imports!=null && 
                !editor.isBlockSelectionModeEnabled()) {
            IRegion selection = editor.getSelection();
            int offset = selection.getOffset();
            IDocument doc = this.getDocument();
            CommonToken token = 
                    getContainingToken(offset, doc);
            boolean quoted;
            if (token == null) {
                quoted = false;
            }
            else {
                int tt = token.getType();
                //don't include verbatim strings!
                quoted = 
                        tt==ASTRING_LITERAL ||
                        tt==STRING_LITERAL || 
                        tt==STRING_END || 
                        tt==STRING_END || 
                        tt==STRING_MID;
            }
            char c = quoted ? '"' : '{';
            Display display = getTextWidget().getDisplay();
            Clipboard clipboard = new Clipboard(display);
            try {
                Object text = 
                        clipboard.getContents(
                                TextTransfer.getInstance());
                Object rtf = 
                        clipboard.getContents(
                                RTFTransfer.getInstance());
                
                try {
                    Object[] data;
                    Transfer[] dataTypes;
                    if (rtf==null) {
                        data = new Object[] { text, 
                                              imports, 
                                              c + selectedText };
                        dataTypes = new Transfer[] {
                                TextTransfer.getInstance(),
                                ImportsTransfer.INSTANCE, 
                                SourceTransfer.INSTANCE
                            };
                    }
                    else {
                        data = new Object[] { text, 
                                              rtf, 
                                              imports, 
                                              c + selectedText };
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
                String text = (String) 
                        clipboard.getContents(
                                SourceTransfer.INSTANCE);
                boolean fromStringLiteral;
                if (text==null) {
                    fromStringLiteral = false;
                    text = (String) 
                            clipboard.getContents(
                                    TextTransfer.getInstance());
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
                    Map<Declaration,String> imports = (Map) 
                            clipboard.getContents(
                                    ImportsTransfer.INSTANCE);
                    IRegion selection = editor.getSelection();
                    int offset = selection.getOffset();
                    int length = selection.getLength();
                    int endOffset = offset+length;
                    
                    IDocument doc = this.getDocument();
                    DocumentRewriteSession rewriteSession = null;
                    if (doc instanceof IDocumentExtension4) {
                        rewriteSession = 
                                ((IDocumentExtension4) doc)
                                    .startRewriteSession(SEQUENTIAL);
                    }
                    
                    CommonToken token = 
                            getContainingToken(offset, doc);
                    boolean quoted;
                    boolean verbatim;
//                    int startOfTokenInLine;
                    if (token == null) {
                        quoted = false;
                        verbatim = false;
//                        startOfTokenInLine = -1;
                    }
                    else {
                        int tt = token.getType();
                        quoted = 
                                tt==ASTRING_LITERAL ||
                                tt==STRING_LITERAL || 
                                tt==STRING_END || 
                                tt==STRING_END || 
                                tt==STRING_MID ||
                                tt==VERBATIM_STRING ||
                                tt==AVERBATIM_STRING;
                        verbatim = 
                                tt==VERBATIM_STRING ||
                                tt==AVERBATIM_STRING;
//                        startOfTokenInLine = 
//                                token.getCharPositionInLine() + 
//                                (verbatim ? 3 : 1);
                    }
                    
                    try {
                        boolean startOfLine = isStartOfLine(offset, doc);
                        IPreferenceStore prefs = CeylonPlugin.getPreferences();
                        try {
                            MultiTextEdit edit = new MultiTextEdit();
                            if (!quoted && imports!=null &&
                                    prefs.getBoolean(PASTE_IMPORTS)) {
                                pasteImports(imports, edit, text, doc);
                            }
                            if (quoted && !verbatim && 
                                    !fromStringLiteral &&
                                    prefs.getBoolean(PASTE_ESCAPE_QUOTED)) {
                                text = text
                                        .replace("\\", "\\\\")
                                        .replace("\t", "\\t")
                                        .replace("\"", "\\\"")
                                        .replace("`", "\\`");
                            }
                            if ((!quoted || verbatim) && 
                                    fromStringLiteral &&
                                    prefs.getBoolean(PASTE_ESCAPE_QUOTED)) {
                                text = text
                                        .replace("\\\"", "\"")
                                        .replace("\\`", "`")
                                        .replace("\\t", "\t")
                                        .replace("\\\\", "\\");
                            }
                            edit.addChild(new ReplaceEdit(offset, length, text));
                            edit.apply(doc);
                            IRegion region = edit.getRegion();
                            endOffset = 
                                    region.getOffset() + 
                                    region.getLength();
                        } 
                        catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        try {
                            if (startOfLine && 
                                    prefs.getBoolean(PASTE_CORRECT_INDENTATION)) {
                                endOffset = 
                                        correctSourceIndentation(
                                                endOffset-text.length(), 
                                                text.length(), doc) 
                                                    + 1;
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
                            ((IDocumentExtension4) doc)
                                .stopRewriteSession(rewriteSession);
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

    public CommonToken getContainingToken(int offset, IDocument doc) {
        ANTLRStringStream stream = 
                new NewlineFixingStringStream(doc.get());
        CeylonLexer lexer = new CeylonLexer(stream);
        CommonTokenStream tokens = 
                new CommonTokenStream(
                        new CeylonInterpolatingLexer(lexer));
        tokens.fill();
        return getTokenStrictlyContainingOffset(offset, 
                        (List) tokens.getTokens());
    }

    private static boolean isStartOfLine(
            int offset, IDocument doc) {
        try {
            int lineStart = 
                    doc.getLineInformationOfOffset(offset)
                       .getOffset();
            int positionInLine = offset-lineStart;
            return doc.get(lineStart, positionInLine)
                       .trim().isEmpty();
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void addBlockComment() {
        IDocument doc = this.getDocument();
        DocumentRewriteSession rewriteSession = null;
        Point p = this.getSelectedRange();

        if (doc instanceof IDocumentExtension4) {
            rewriteSession = 
                    ((IDocumentExtension4) doc)
                        .startRewriteSession(SEQUENTIAL);
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
                ((IDocumentExtension4) doc)
                    .stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }
    
    private void removeBlockComment() {
        IDocument doc = this.getDocument();
        DocumentRewriteSession rewriteSession = null;
        Point p = this.getSelectedRange();

        if (doc instanceof IDocumentExtension4) {
            rewriteSession = 
                    ((IDocumentExtension4) doc)
                        .startRewriteSession(SEQUENTIAL);
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
                ((IDocumentExtension4) doc)
                    .stopRewriteSession(rewriteSession);
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
            rewriteSession = 
                    ((IDocumentExtension4) doc)
                        .startRewriteSession(SEQUENTIAL);
        }

        try {
            final int selStart = p.x;
            final int selLen = p.y;
            final int selEnd = selStart+selLen;
            final int startLine = doc.getLineOfOffset(selStart);
            int endLine = doc.getLineOfOffset(selEnd);

            if (selLen>0 && lookingAtLineEnd(doc, selEnd))
                endLine--;

            boolean linesAllHaveCommentPrefix = 
                    linesHaveCommentPrefix(doc, 
                            lineCommentPrefix, 
                            startLine, endLine);
            boolean useCommonLeadingSpace = true; // take from a preference?
            int leadingSpaceToUse = 
                    useCommonLeadingSpace ? 
                            calculateLeadingSpace(doc, 
                                    startLine, endLine) : 0;

            for (int line = startLine; line<=endLine; line++) {
                
                int lineStart = doc.getLineOffset(line);
                int lineEnd = lineStart+doc.getLineLength(line)-1;

                if (linesAllHaveCommentPrefix) {
                    // remove the comment prefix from each line, wherever it occurs in the line
                    int offset = lineStart;
                    while (isWhitespace(doc.getChar(offset)) && 
                            offset<lineEnd) {
                        offset++;
                    }
                    // The first non-whitespace characters *must* be the single-line comment prefix
                    doc.replace(offset, 
                            lineCommentPrefix.length(), 
                            "");
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
                ((IDocumentExtension4) doc)
                    .stopRewriteSession(rewriteSession);
            }
            restoreSelection();
        }
    }

    private int calculateLeadingSpace(IDocument doc, 
            int startLine, int endLine) {
        try {
            int result = Integer.MAX_VALUE;
            for (int line=startLine; line<=endLine; line++) {
                
                int lineStart = doc.getLineOffset(line);
                int lineEnd = lineStart + doc.getLineLength(line) - 1;
                int offset = lineStart;
                
                while (isWhitespace(doc.getChar(offset)) && 
                        offset < lineEnd) {
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
     * @return true, if the given inclusive range of lines 
     * all start with the single-line comment prefix, even 
     * if they have different amounts of leading whitespace
     */
    private boolean linesHaveCommentPrefix(IDocument doc, 
            String lineCommentPrefix, int startLine, int endLine) {
        try {
            int docLen = doc.getLength();

            for (int line=startLine; line<=endLine; line++) {
                
                int lineStart = doc.getLineOffset(line);
                int lineEnd = lineStart + doc.getLineLength(line) - 1;
                int offset = lineStart;

                while (isWhitespace(doc.getChar(offset)) && 
                        offset < lineEnd) {
                    offset++;
                }
                
                if (docLen-offset > lineCommentPrefix.length() && 
                    doc.get(offset, lineCommentPrefix.length())
                            .equals(lineCommentPrefix)) {
                    // this line starts with the single-line comment prefix
                }
                else {
                    return false;
                }
            }
        }
        catch (BadLocationException e) {
            return false;
        }
        return true;
    }

    private void doCorrectIndentation(int offset, int len) {
        
        IDocument doc = getDocument();
        DocumentRewriteSession rewriteSession = null;
        if (doc instanceof IDocumentExtension4) {
            rewriteSession = 
                    ((IDocumentExtension4) doc)
                        .startRewriteSession(SEQUENTIAL);
        }
        
        
        Point selectedRange = getSelectedRange();
        boolean emptySelection = 
                selectedRange==null || selectedRange.y==0;
        
        try {
            correctSourceIndentation(offset, len, doc);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        finally {
            if (doc instanceof IDocumentExtension4) {
                ((IDocumentExtension4) doc)
                    .stopRewriteSession(rewriteSession);
            }
            restoreSelection();
            if (emptySelection) {
                selectedRange = getSelectedRange();
                setSelectedRange(selectedRange.x, 0);
            }
        }
    }

    public int correctSourceIndentation(int selStart, int selLen, 
            IDocument doc)
            throws BadLocationException {
        int selEnd = selStart + selLen;
        int startLine = doc.getLineOfOffset(selStart);
        int endLine = doc.getLineOfOffset(selEnd);

        // If the selection extends just to the beginning of the next line, don't indent that one too
        if (selLen > 0 && 
                lookingAtLineEnd(doc, selEnd)) {
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
                if (pos>len && 
                        doc.get(pos-len,len).equals(lineTerm)) {
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
        super.configure(configuration);
        
        AbstractInformationControlManager hoverController = 
                getTextHoveringController();
        if (hoverController!=null) { //null in a merge viewer
            hoverController.setSizeConstraints(80, 30, false, true);
        }
        
        if (configuration instanceof CeylonSourceViewerConfiguration) {
            CeylonSourceViewerConfiguration svc = 
                    (CeylonSourceViewerConfiguration) configuration;
            
            outlinePresenter = 
                    svc.getOutlinePresenter(this);
            if (outlinePresenter!=null) {
                outlinePresenter.install(this);
            }
            
            structurePresenter = 
                    svc.getOutlinePresenter(this);
            if (structurePresenter!=null) {
                structurePresenter.install(this);
            }
            
            hierarchyPresenter = 
                    svc.getHierarchyPresenter(this);
            if (hierarchyPresenter!=null) {
                hierarchyPresenter.install(this);
            }
            
            definitionPresenter = 
                    svc.getDefinitionPresenter(this);
            if (definitionPresenter!=null) {
                definitionPresenter.install(this);
            }
            
            referencesPresenter = 
                    svc.getReferencesPresenter(this);
            if (referencesPresenter!=null) {
                referencesPresenter.install(this);
            }
            
            autoEditStrategy = new CeylonAutoEditStrategy();
            
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
        super.unconfigure();
    }
    
    Map<Declaration,String> copyImports() {
    	try {
    		CeylonParseController controller = 
    		        editor.getParseController();
    		if (controller==null) {
    		    return null;
    		}
    		Tree.CompilationUnit cu = 
    				controller.getTypecheckedRootNode();
    		if (cu == null) {
    		    return null;
    		}
    		IRegion selection = editor.getSelection();
    		SelectedImportsVisitor v = 
    		        new SelectedImportsVisitor(
    		        		selection.getOffset(), 
    		        		selection.getLength());
    		cu.visit(v);
    		return v.getCopiedReferencesMap();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    void pasteImports(Map<Declaration,String> map, MultiTextEdit edit, 
            String pastedText, IDocument doc) {
        if (!map.isEmpty()) {
            CeylonParseController controller = 
                    editor.getParseController();
            if (controller==null || 
                    controller.getLastCompilationUnit()==null) {
                return;
            }
            new correctJ2C().pasteImports(map, edit, doc, 
            		controller.getLastCompilationUnit());
        }
    }

    public IPresentationReconciler getPresentationReconciler() {
        return fPresentationReconciler;
    }
    
    public CeylonContentAssistant getContentAssistant() {
        return (CeylonContentAssistant) fContentAssistant;
    }
    
}
