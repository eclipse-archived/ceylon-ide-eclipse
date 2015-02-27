package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoNode;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;


public class CeylonHyperlinkDetector implements IHyperlinkDetector {
    private CeylonParseController parseController;
    
    public CeylonHyperlinkDetector(CeylonParseController pc) {
        this.parseController = pc;
    }

    private final class CeylonNodeLink implements IHyperlink {
        private final Node node;
        private final Node id;

        private CeylonNodeLink(Node node, Node id) {
            this.node = node;
            this.id = id;
        }

        @Override
        public void open() {
            gotoNode(node);
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "Ceylon Declaration";
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
        if (parseController==null ||
                parseController.getRootNode()==null) {
            return null;
        }
        else {
            Node node = 
                    findNode(parseController.getRootNode(), 
                            region.getOffset(), 
                            region.getOffset()+region.getLength());
            Node id = getIdentifyingNode(node);
            if (node==null) {
                return null;
            }
            else {
                Node r = getReferencedNode(node);
                if (r==null) {
                    return null;
                }
                else {
                    return new IHyperlink[] { new CeylonNodeLink(r, id) };
                }
            }
        }
    }
}
