package com.redhat.ceylon.eclipse.code.editor;

import static org.eclipse.core.runtime.Platform.getPreferencesService;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;

public class CeylonAutoEditStrategy implements IAutoEditStrategy {
	
    public static String getDefaultIndent() {
        StringBuilder result = new StringBuilder();
        initialIndent(result);
        return result.toString();
    }
    
    static int getIndentSpaces() {
        IPreferencesService ps = getPreferencesService();
        return ps==null ? 4 :
            ps.getInt("org.eclipse.ui.editors", 
                    EDITOR_TAB_WIDTH, 4, null);
    }
    
    static boolean getIndentWithSpaces() {
        IPreferencesService ps = getPreferencesService();
        return ps==null ? false :
            ps.getBoolean("org.eclipse.ui.editors", 
                    EDITOR_SPACES_FOR_TABS, false, null);
    }
    
    static void initialIndent(StringBuilder buf) {
        //guess an initial indent level
        if (getIndentWithSpaces()) {
            int spaces = getIndentSpaces();
            for (int i=1; i<=spaces; i++) {
                buf.append(' ');
            }
        }
        else {
            buf.append('\t');
        }
    }

    //TODO: when pasting inside an existing string literal, should
    //      we automagically escape unescaped quotes in the pasted
    //      text?
    
	 public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		 List<CommonToken> tokens=null;
//		 if (editor!=null) {
//			 CeylonParseController pc = editor.getParseController();
//			 if (pc.getTokens()!=null) {
//				 tokens = pc.getTokens();
//			 }
//		 }
		 if (tokens==null) {
			 CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(document.get()));
			 CommonTokenStream ts = new CommonTokenStream(lexer);
			 ts.fill();
             try {
                new CeylonParser(ts).compilationUnit();
             } 
             catch (RecognitionException e) {}
			 tokens = ts.getTokens();
		 }
		 new AutoEdit(document, tokens, command).customizeDocumentCommand();
	 }
	 
}
