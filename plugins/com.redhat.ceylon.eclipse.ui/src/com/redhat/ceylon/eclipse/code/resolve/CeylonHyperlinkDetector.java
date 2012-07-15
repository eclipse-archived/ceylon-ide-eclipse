package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;

import org.eclipse.imp.model.ISourceProject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

/**
 * Provides a method to detect hyperlinks originating from a
 * given region in the parse stream of a given parse controller.
 */
public class CeylonHyperlinkDetector implements IHyperlinkDetector {
	private CeylonEditor editor;
    
    public CeylonHyperlinkDetector(CeylonEditor editor) {
		this.editor = editor;
	}

    private final class CeylonNodeLink implements IHyperlink {
        private final Node node;
        private final Node id;
        private final CeylonParseController pc;

        private CeylonNodeLink(Node node, Node id, CeylonParseController pc) {
            this.node = node;
            this.id = id;
            this.pc = pc;
        }

        @Override
        public void open() {
        	ISourceProject project = pc.getProject();
			gotoNode(node, project==null ? null : project.getRawProject(), 
					pc.getTypeChecker());
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return null;
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                    id.getStopIndex()-id.getStartIndex()+1);
        }
    }

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, 
			boolean canShowMultipleHyperlinks) {
        CeylonParseController pc = editor.getParseController();
        if (pc==null||pc.getRootNode()==null) {
            return null;
        }
        else {
            Node node = findNode(pc.getRootNode(), region.getOffset(), 
                    region.getOffset()+region.getLength());
            Node id = getIdentifyingNode(node);
            if (node==null) {
                return null;
            }
            else {
                Tree.Declaration dec = getReferencedNode(node, pc);
                if (dec==null) {
                    return null;
                }
                else {
                	return new IHyperlink[] { new CeylonNodeLink(dec, id, pc) };
                }
            }
        }
	}
}
