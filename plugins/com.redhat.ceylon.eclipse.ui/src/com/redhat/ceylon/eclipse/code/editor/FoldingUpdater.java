package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

/**
 * FolderBase is an abstract base type for a source-text folding service.
 * It is intended to support extensions for language-specific folders.
 * The class is abstract only with respect to a method that sends a
 * visitor to an AST, as both the visitor and AST node types are language
 * specific.
 * 
 * @author suttons@us.ibm.com
 * @author rfuhrer@watson.ibm.com
 * @author Gavin King
 */
public class FoldingUpdater {
	
	// Maps new annotations to positions
    protected HashMap<Annotation,Position> newAnnotations = new HashMap<Annotation, Position>();

    // Lists the new annotations, which are the keys for newAnnotations
    protected List<Annotation> annotations = new ArrayList<Annotation>();

    protected CeylonParseController parseController = null;
    protected CeylonSourceViewer sourceViewer = null;

    // Used to support checking of whether annotations have
    // changed between invocations of updateFoldingStructure
    // (because, if they haven't, then it's probably best not
    // to update the folding structure)
    private ArrayList<Annotation> oldAnnotationsList = null;
    private Annotation[] oldAnnotationsArray;

    // Methods to make annotations will typically be called by visitor methods
    // in the language-specific concrete subtype

    public FoldingUpdater(CeylonSourceViewer sourceViewer) {
		this.sourceViewer = sourceViewer;
	}

	/**
     * Make a folding annotation that corresponds to the extent of text
     * represented by a given program entity. Usually, this will be an
     * AST node, but it can be anything for which the language's
     * ISourcePositionLocator can produce an offset/end offset.
     * 
     * @param n an Object representing a program entity
     */
    public void makeAnnotation(Object n) {
    	makeAnnotation(n, false);
    }
    
    /**
     * Make a folding annotation that corresponds to the extent of text
     * represented by a given program entity. Usually, this will be an
     * AST node, but it can be anything for which the language's
     * ISourcePositionLocator can produce an offset/end offset.
     * 
     * @param n an Object representing a program entity
     */
    public void makeAnnotation(Object n, boolean collapsed) {
		makeAnnotation(getStartOffset(n), getLength(n), collapsed);
    }

    /**
     * Make a folding annotation that corresponds to the given range of text.
     * 
     * @param start		The starting offset of the text range
     * @param len		The length of the text range
     */
    public ProjectionAnnotation makeAnnotation(int start, int len) {
		ProjectionAnnotation annotation= new ProjectionAnnotation();
		len = advanceToEndOfLine(start, len);
		newAnnotations.put(annotation, new Position(start, len));
		annotations.add(annotation);
		return annotation;
    }

	protected int advanceToEndOfLine(int start, int len) {
		IDocument doc = sourceViewer.getDocument();
		try {
			int line = doc.getLineOfOffset(start+len);
			while (start+len<doc.getLength() && 
					Character.isWhitespace(doc.get(start+len,1).charAt(0)) &&
					doc.getLineOfOffset(start+len)==line) {
				len++;
			}
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		return len;
	}
    
    /**
     * Make a folding annotation that corresponds to the given range of text.
     * 
     * @param start		The starting offset of the text range
     * @param len		The length of the text range
     */
    public void makeAnnotation(int start, int len, boolean collapsed) {
		ProjectionAnnotation annotation= new ProjectionAnnotation(collapsed);
		len = advanceToEndOfLine(start, len);
		newAnnotations.put(annotation, new Position(start, len));
		annotations.add(annotation);
    }

	/**
	 * Update the folding structure for a source text, where the text and its
	 * AST are represented by a given parse controller and the folding structure
	 * is represented by annotations in a given annotation model.
	 * 
	 * This is the principal routine of the folding updater.
	 * 
	 * The implementation provided here makes use of a local class
	 * FoldingUpdateStrategy, to which the task of updating the folding
	 * structure is delegated.
	 * 
	 * updateFoldingStructure is synchronized because, at least on file opening,
	 * it can be called more than once before the first invocation has completed.
	 * This can lead to inconsistent calculations resulting in the absence of
	 * folding annotations in newly opened files.
	 * 
	 * @param parseController		A parse controller through which the AST for
	 *								the source text can be accessed
	 * @param annotationModel		A structure of projection annotations that
	 *								represent the foldable elements in the source
	 *								text
	 */
	public synchronized void updateFoldingStructure(CeylonParseController parseController, 
			ProjectionAnnotationModel annotationModel) {
		if (parseController != null)
			this.parseController = parseController;
		
		try {
			Node ast = parseController.getRootNode();
			
			if (ast == null) {
				// We can't create annotations without an AST
				return;
			}
		
			// But, since here we have the AST ...
			sendVisitorToAST(newAnnotations, annotations, ast);

			// Update the annotation model if there have been changes
			// but not otherwise (since update leads to redrawing of the	
			// source in the editor, which is likely to be unwelcome if
			// there haven't been any changes relevant to folding)
			boolean updateNeeded = false;
			if (oldAnnotationsList == null) {
				// Should just be the first time through
				updateNeeded = true;
			} 
			else {
				// Check to see whether the current and previous annotations
				// differ in any significant way; if not, then there's no
				// reason to update the annotation model.
				// Note:  This test may be implemented in various ways that may
				// be more or less simple, efficient, correct, etc.  (The
				// default test provided below is simplistic although quick and
				// usually effective.)
				updateNeeded = differ(oldAnnotationsList, annotations);
			}
			if (updateNeeded) {
				// Save the current annotations to compare for changes the next time
				oldAnnotationsList = new ArrayList<Annotation>();
				for (int i = 0; i < annotations.size(); i++) {
					oldAnnotationsList.add(annotations.get(i));	
				}
			} else {
			}
		
			// Need to curtail calls to modifyAnnotations() because these lead to calls
			// to fireModelChanged(), which eventually lead to calls to updateFoldingStructure,
			// which lead back here, which would lead to another call to modifyAnnotations()
			// (unless those were curtailed)
			if (updateNeeded) {
				annotationModel.modifyAnnotations(oldAnnotationsArray, newAnnotations, null);
				// Capture the latest set of annotations in a form that can be used the next
				// time that it is necessary to modify the annotations
				oldAnnotationsArray = (Annotation[]) annotations.toArray(new Annotation[annotations.size()]);
			}

			newAnnotations.clear();
			annotations.clear();			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}	


	/**
	 * A method to test whether there has been a significant change in the folding
	 * annotations for a source text.  The method works by comparing two lists of
	 * annotations, nominally the "old" and "new" annotations.  It returns true iff
	 * there is considered to be a "significant" difference in the two lists, where
	 * the meaning of "significant" is defined by the implementation of this method.
	 * 
	 * The default implementation provided here is a simplistic test of the difference
	 * between two lists, considering only their size.  This may work well enough much
	 * of the time as the comparisons between lists should be made very frequently,
	 * actually more frequently than the rate at which the typical human user will
	 * edit the program text so as to affect the AST so as to affect the lists.  Thus
	 * most changes of lists will entail some change in the number of elements at some
	 * point that will be observed here.  This will not work for certain very rapid
	 * edits of source text (e.g., rapid replacement of elements).
	 * 
	 * This method should be overridden in language-specific implementations of the
	 * folding updater where a more sophisticated test is desired.	
	 * 	
	 * @param list1		A list of annotations (nominally the "old" annotations)
	 * @param list2		A list of annotations (nominally the "new" annotations)
	 * @return			true iff there has been a "significant" difference in the
	 * 					two given lists of annotations
	 * 
	 */
	protected boolean differ(List<Annotation> list1, List<Annotation> list2) {
		if (list1.size() != list2.size()) {
			return true;
		}
		return false;
	}

	/**
	 * Send a visitor to an AST representing a program in order to construct the
	 * folding annotations.  Both the visitor type and the AST node type are language-
	 * dependent, so this method is abstract.
	 * 
	 * @param newAnnotations	A map of annotations to text positions
	 * @param annotations		A listing of the annotations in newAnnotations, that is,
	 * 							a listing of keys to the map of text positions
	 * @param ast				An Object that will be taken to represent an AST node
	 */
	public void sendVisitorToAST(HashMap<Annotation,Position> newAnnotations, 
	        final List<Annotation> annotations, Object ast) {
        for (CommonToken token: getTokens()) {
            int type = token.getType();
			if (type==MULTI_COMMENT ||
                type==STRING_LITERAL ||
                type==ASTRING_LITERAL||
                type==VERBATIM_STRING||
                type==AVERBATIM_STRING) {
                if (isMultilineToken(token)) {
                    makeAnnotation(token, token);
                    //TODO: initially collapse copyright notice
                }
            }
        }
		Tree.CompilationUnit cu = (Tree.CompilationUnit) ast;
        new Visitor() {
            @Override 
            public void visit(Tree.ImportList importList) {
                super.visit(importList);
                if (!importList.getImports().isEmpty()) {
                    ProjectionAnnotation ann = foldIfNecessary(importList);
					if (ann!=null) {
                        ann.markCollapsed();
                    }
                }
            }
            /*@Override 
            public void visit(Tree.Import that) {
                super.visit(that);
                foldIfNecessary(that);
            }*/
			@Override 
			public void visit(Tree.Body that) {
                super.visit(that);
                if (that.getToken()!=null) { //for "else if"
                    foldIfNecessary(that);
                }
			}
            @Override 
            public void visit(Tree.NamedArgumentList that) {
                super.visit(that);
                foldIfNecessary(that);
            }
			@Override 
			public void visit(Tree.ModuleDescriptor that) {
                super.visit(that);
                foldIfNecessary(that);
			}
		}.visit(cu);
	}

    private ProjectionAnnotation foldIfNecessary(Node node) {
        CommonToken token = (CommonToken) node.getToken();
        CommonToken endToken = (CommonToken) node.getEndToken();
        if (endToken.getLine()-token.getLine()>0) {
            return makeAnnotation(token, endToken);
        }
        else {
        	return null;
        }
    }
    
    private boolean isMultilineToken(CommonToken token) {
        return token.getText().indexOf('\n')>0 ||
                token.getText().indexOf('\r')>0;
    }

    private ProjectionAnnotation makeAnnotation(CommonToken token, CommonToken endToken) {
        return makeAnnotation(token.getStartIndex(), 
                endToken.getStopIndex()-token.getStartIndex()+1);
    }

    private List<CommonToken> getTokens() {
        return ((CeylonParseController) parseController)
                .getTokens();
    }

}