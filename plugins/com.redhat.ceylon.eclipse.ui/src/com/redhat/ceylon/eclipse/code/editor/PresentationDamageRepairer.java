package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class PresentationDamageRepairer implements IPresentationDamager, IPresentationRepairer {
	
	private final CeylonEditor editor;
	private Region applyImmediately;

	PresentationDamageRepairer(CeylonEditor ceylonEditor) {
		this.editor = ceylonEditor;
	}
	
	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, 
			boolean documentPartitioningChanged) {
		// TODO: figure how much of the document presentation 
		//       needs to be recomputed
		if (noTextChange(event)) {
			applyImmediately = new Region(event.getOffset(), event.getLength());
			return applyImmediately;
		}
		/*CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(event.getDocument().get()));
		CommonTokenStream cts = new CommonTokenStream(lexer);
		cts.fill();
		List<CommonToken> tokens = new ArrayList<CommonToken>(cts.getTokens().size());
		tokens.addAll(cts.getTokens());*/
		List<CommonToken> tokens = editor.getParseController().getTokens();
		if (tokens!=null) {
			int i = getTokenIndexAtCharacter(tokens, event.getOffset()-1);
			if (i<0) {
				i=-i;
			}
			CommonToken t = tokens.get(i);
			boolean withinToken = false;
			if (t.getStartIndex()<=event.getOffset() && 
					t.getStopIndex()>=event.getOffset()+event.getLength()-1) {
				withinToken = true;
				int type = t.getType();
				switch (type) {
				case CeylonLexer.WS:
					for (char c: event.getText().toCharArray()) {
					    if (!Character.isWhitespace(c)) {
					    	withinToken = false;
					    }
					}
				break;
				case CeylonLexer.UIDENTIFIER:
				case CeylonLexer.LIDENTIFIER:
					for (char c: event.getText().toCharArray()) {
					    if (!Character.isJavaIdentifierPart(c)) {
					    	withinToken = false;
					    }
					}
				break;
				case CeylonLexer.STRING_LITERAL:
					for (char c: event.getText().toCharArray()) {
					    if (c=='"') {
					    	withinToken = false;
					    }
					}
				break;
				case CeylonLexer.MULTI_COMMENT:
					for (char c: event.getText().toCharArray()) {
					    if (c=='/'||c=='*') {
					    	withinToken = false;
					    }
					}
				break;
				case CeylonLexer.LINE_COMMENT:
					for (char c: event.getText().toCharArray()) {
					    if (c=='\n'||c=='\f'||c=='\r') {
					    	withinToken = false;
					    }
					}
				break;
				default:
			    	withinToken = false;
			    }
				if (withinToken) {
					return new Region(event.getOffset(), 
							event.getText().length());
				}
			}
		}
		return partition;
	}

	public void createPresentation(TextPresentation presentation, 
			ITypedRegion damage) {
		try {
			PresentationController pc = editor.getPresentationController();
			if (pc!=null) {
				/*if (applyImmediately!=null &&
						applyImmediately.getOffset()==damage.getOffset() &&
						applyImmediately.getLength()==damage.getLength()) {
					//these updates represent hyperlink decorations
					//or editor annotation updates - we can't rely
					//upon the parser controller being called at
					//all so we have no choice but to repair it up
					//front, and hope that this doesn't screw
					//anything up
					applyImmediately = null;
					pc.repairDamage(editor.getParseController(), 
							null, damage); //null monitor here is important!
				}
				else {
					pc.registerDamage(damage);
				}*/
				List<CommonToken> tokens = parse(editor.getCeylonSourceViewer().getDocument().get());
				pc.aggregateTextPresentation(tokens, null, damage, presentation);
				pc.applyTextPresentationChange(presentation);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	boolean noTextChange(DocumentEvent event) {
		try {
			return event.getDocument()
					.get(event.getOffset(),event.getLength())
					.equals(event.getText());
		} 
		catch (BadLocationException e) {
			return false;
		}
	}
	
	List<CommonToken> parse(String text) {
		ANTLRStringStream input = new ANTLRStringStream(text);
        CeylonLexer lexer = new CeylonLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        
        CeylonParser parser = new CeylonParser(tokenStream);
        Tree.CompilationUnit cu;
        try {
            cu = parser.compilationUnit();
        }
        catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
        
        return tokenStream.getTokens(); 
	}
	
    public void setDocument(IDocument document) {}
}