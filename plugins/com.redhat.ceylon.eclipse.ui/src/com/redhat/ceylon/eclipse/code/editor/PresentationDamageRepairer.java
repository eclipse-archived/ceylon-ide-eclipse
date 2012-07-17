package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getEndOffset;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIterator;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

class PresentationDamageRepairer implements IPresentationDamager, IPresentationRepairer {
	
	private final CeylonEditor editor;
    private final ISourceViewer sourceViewer;
    private final CeylonTokenColorer tokenColorer;

	PresentationDamageRepairer(CeylonEditor ceylonEditor) {
		editor = ceylonEditor;
		sourceViewer = editor.getCeylonSourceViewer();
		tokenColorer = new CeylonTokenColorer();
	}
	
	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, 
			boolean documentPartitioningChanged) {

		if (noTextChange(event)) {
			return new Region(event.getOffset(), event.getLength());
		}
		
		List<CommonToken> tokens = editor.getParseController().getTokens();
		
		//TODO: Can we instead tokenize and cache the tokens here?
		//      Do we have a guarantee that we aren't called in
		//      parellel by different threads?
		/*CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(event.getDocument().get()));
		CommonTokenStream cts = new CommonTokenStream(lexer);
		cts.fill();
		List<CommonToken> tokens = new ArrayList<CommonToken>(cts.getTokens().size());
		tokens.addAll(cts.getTokens());*/		
		
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
		List<CommonToken> tokens = parse(editor.getCeylonSourceViewer().getDocument().get());
		aggregateTextPresentation(tokens, null, damage, presentation);
		applyTextPresentationChange(presentation);
	}
	
	private void applyTextPresentationChange(TextPresentation newPresentation) {
		if (sourceViewer!=null) {
			// The document might have changed since the presentation was computed, so
			// trim the presentation's "result window" to the current document's extent.
			// This avoids upsetting SWT, but there's still a question as to whether
			// this is really the right thing to do. i.e., this assumes that the
			// presentation will get recomputed later on, when the new document change
			// gets noticed. But will it?
			IDocument doc = sourceViewer.getDocument();
			int newDocLength= doc!=null ? doc.getLength() : 0;
			IRegion presExtent= newPresentation.getExtent();
			if (presExtent.getOffset() + presExtent.getLength() > newDocLength) {
				newPresentation.setResultWindow(new Region(presExtent.getOffset(), 
						newDocLength - presExtent.getOffset()));
			}
			sourceViewer.changeTextPresentation(newPresentation, true);
		}
	}
	
	private void aggregateTextPresentation(List<CommonToken> tokens, 
    		IProgressMonitor monitor, IRegion damage, 
    		TextPresentation presentation) {
        int prevStartOffset= -1;
        int prevEndOffset= -1;
        Iterator<CommonToken> iter= getTokenIterator(tokens, damage);
        if (iter!=null) {
        	while (iter.hasNext()) {
        		if (monitor!=null && monitor.isCanceled()) {
        			break;
        		}
        		CommonToken token= iter.next();
        		int startOffset= getStartOffset(token);
        		int endOffset= getEndOffset(token);
        		if (startOffset <= prevEndOffset && 
        				endOffset >= prevStartOffset) {
        			continue;
        		}
        		changeTokenPresentation(presentation, 
        				tokenColorer.getColoring(token), 
        				startOffset, endOffset);
        		prevStartOffset= startOffset;
        		prevEndOffset= endOffset;
        	}
        }
    }

    private void changeTokenPresentation(TextPresentation presentation, 
    		TextAttribute attribute, int startOffset, int endOffset) {
    	        
		StyleRange styleRange= new StyleRange(startOffset, 
        		endOffset-startOffset+1,
                attribute == null ? null : attribute.getForeground(),
                attribute == null ? null : attribute.getBackground(),
                attribute == null ? SWT.NORMAL : attribute.getStyle());

        // Negative (possibly 0) length style ranges will cause an 
        // IllegalArgumentException in changeTextPresentation(..)
        if (styleRange.length <= 0 || 
        		styleRange.start+styleRange.length > 
                        sourceViewer.getDocument().getLength()) {
        	//do nothing
        } 
        else {
            presentation.addStyleRange(styleRange);
        }
    }

    private boolean noTextChange(DocumentEvent event) {
		try {
			return event.getDocument()
					.get(event.getOffset(),event.getLength())
					.equals(event.getText());
		} 
		catch (BadLocationException e) {
			return false;
		}
	}
	
	private List<CommonToken> parse(String text) {
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