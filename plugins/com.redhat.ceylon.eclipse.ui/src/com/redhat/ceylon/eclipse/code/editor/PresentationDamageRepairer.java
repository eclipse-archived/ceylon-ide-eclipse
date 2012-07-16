package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.NullProgressMonitor;
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

class PresentationDamageRepairer implements IPresentationDamager, IPresentationRepairer {
	
	private final CeylonEditor editor;
	boolean applyImmediately;

	PresentationDamageRepairer(CeylonEditor ceylonEditor) {
		this.editor = ceylonEditor;
	}
	
	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, 
			boolean documentPartitioningChanged) {
		// TODO: figure how much of the document presentation 
		//       needs to be recomputed
		if (noTextChange(event)) {
			applyImmediately = true;
			return new Region(event.getOffset(), event.getLength());
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
	
	public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
		try {
			if (editor.getPresentationController()!=null) {
				editor.getPresentationController().damage(damage);
				if (applyImmediately) {
					//these updates represent hyperlink decorations
					//or editor annotation updates - we can't rely
					//upon the parser controller being called at
					//all so we have no choice but to repair it up
					//front, and hope that this doesn't screw
					//anything up
					editor.getPresentationController()
					        .repair(editor.getParseController(), 
							        new NullProgressMonitor());
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
    public void setDocument(IDocument document) {}
}