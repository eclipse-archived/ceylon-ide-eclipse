package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.formatAnnotationList;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationsForLine;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.HTMLPrinter;

/**
 * Helper class for implementing "hover help" which encapsulates the process of locating
 * the AST node under the cursor, and asking the documentation provider for the relevant
 * information to show in the hover.<br>
 * No longer an extension point itself, this class is instantiated directly by the
 * HoverHelpController and initialized with a Language so that it can instantiate the
 * correct documentation provider.
 * @author rfuhrer
 * @author awtaylor, per attachment to bug #322427
 */
public class HoverHelper {

    public HoverHelper() {}

    public String getHoverHelpAt(CeylonParseController parseController, ISourceViewer srcViewer, int offset) {
		try {
			int lineOfOffset= srcViewer.getDocument().getLineOfOffset(offset);
            List<Annotation> annotations= getAnnotationsForLine(srcViewer, lineOfOffset);
			if (annotations != null && annotations.size() > 0) {
				String annString = formatAnnotationList(annotations);

				if (annString != null && annString.length() > 0) {
					return annString;
				}
			}
		} 
		catch (BadLocationException e) {
			e.printStackTrace();
		}

        Node root= parseController.getRootNode();
        if (root == null) return null;
        Node selNode = parseController.getSourcePositionLocator().findNode(root, offset);
        if (selNode == null) return null;
        // determine whether this is a reference to something else 
       	Node target = getReferencedNode(selNode, parseController);

       	// if target is null, we're hovering over a declaration whose javadoc is right before us, but
       	// showing it can still be useful for previewing the javadoc formatting
       	// OR if target is null we're hovering over something not a decl or ref (e.g. integer literal)
       	// ==>  show information if something other than the source is available:
       	// 1. if target != src, show something
       	// 2. if target == src, and docProvider gives something, then show it
       	// 3. if target == src, and docProvider doesn't give anything, don't show anything
       	//
       	
       	// if this is not a reference, provide info for it anyway 
       	if (target==null) target=selNode;

       	String doc= new CeylonDocumentationProvider().getDocumentation(target, parseController);			
       	if (doc!=null) return doc;

       	if (target==selNode) return null;

       	StringBuffer buffer= new StringBuffer();
       	HTMLPrinter.addSmallHeader(buffer, target.toString());
       	HTMLPrinter.addParagraph(buffer, doc);
       	return buffer.toString();
    }
}
