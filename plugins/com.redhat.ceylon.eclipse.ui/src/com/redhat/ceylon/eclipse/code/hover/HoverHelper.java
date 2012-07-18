package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.formatAnnotationList;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationsForOffset;

import java.util.List;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

/**
 * Encapsulates the process of locating the AST node under the 
 * cursor, and asking the documentation provider for the relevant 
 * information to show in the hover.
 * 
 * @author rfuhrer
 * @author awtaylor, per attachment to bug #322427
 */
public class HoverHelper {

    public HoverHelper() {}

    public String getHoverHelpAt(CeylonParseController parseController, 
    		ISourceViewer srcViewer, int offset) {
    	//try {
    		/*int lineOfOffset= srcViewer.getDocument().getLineOfOffset(offset);
    		List<Annotation> annotations= getAnnotationsForLine(srcViewer, lineOfOffset);*/
    		List<Annotation> annotations= getAnnotationsForOffset(srcViewer, offset);
    		if (annotations!=null && !annotations.isEmpty()) {
    			String annString = formatAnnotationList(annotations);
    			if (annString!=null && !annString.isEmpty()) {
    				return annString;
    			}
    		}
    	/*} 
    	catch (BadLocationException e) {
    		e.printStackTrace();
    	}*/

    	Tree.CompilationUnit root= parseController.getRootNode();
    	if (root!=null) {
    		Node selNode = findNode(root, offset);
    		if (selNode!=null) {
    			Node target = getReferencedNode(selNode, parseController);
    			if (target==null) target=selNode;
    			return new CeylonDocumentationProvider()
    			        .getDocumentation(target, parseController);			
    		}
    	}
    	return null;
    }
}
