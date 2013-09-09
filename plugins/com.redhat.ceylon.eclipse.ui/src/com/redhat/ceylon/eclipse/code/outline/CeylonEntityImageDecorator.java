package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.AdditionalAnnotationCreator.getRefinedDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.BOTTOM_LEFT;
import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.BOTTOM_RIGHT;
import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.TOP_LEFT;
import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.TOP_RIGHT;

import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.search.CeylonElement;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.ErrorCollectionVisitor;

public class CeylonEntityImageDecorator {
    
    private final static int WARNING = 1 << 2;
    private final static int ERROR = 1 << 3;
    private final static int REFINES = 1 << 4;
    private final static int IMPLEMENTS = 1 << 5;
    private final static int FORMAL = 1 << 6;
    private final static int ABSTRACT = 1 << 7;
    private final static int VARIABLE = 1 << 8;

    public DecorationDescriptor[] getAllDecorations() {
        return new DecorationDescriptor[] {
        		//TODO: cache the images in CeylonPlugin!
                new DecorationDescriptor(WARNING, CeylonPlugin.getInstance().image("warning.gif"), BOTTOM_LEFT),
                new DecorationDescriptor(ERROR, CeylonPlugin.getInstance().image("error.gif"), BOTTOM_LEFT),
                new DecorationDescriptor(REFINES, CeylonPlugin.getInstance().image("over_tiny_co.gif"), BOTTOM_RIGHT),
                new DecorationDescriptor(IMPLEMENTS, CeylonPlugin.getInstance().image("implm_tiny_co.gif"), BOTTOM_RIGHT),
                new DecorationDescriptor(FORMAL, CeylonPlugin.getInstance().image("final_co.gif"), TOP_RIGHT),
                new DecorationDescriptor(ABSTRACT, CeylonPlugin.getInstance().image("abstract_co.gif"), TOP_RIGHT),
                new DecorationDescriptor(VARIABLE, CeylonPlugin.getInstance().image("volatile_co.gif"), TOP_LEFT)
            };
    }
    
    public int getDecorationAttributes(Object entity) {
        if (entity instanceof CeylonOutlineNode) {
            return getNodeDecorationAttributes((Node)((CeylonOutlineNode) entity).getTreeNode());
        }
        if (entity instanceof CeylonElement) {
            return getNodeDecorationAttributes(((CeylonElement) entity).getNode());
        }
        if (entity instanceof Declaration) {
            return getDecorationAttributes((Declaration) entity);
        }
        if (entity instanceof Node) {
            return getNodeDecorationAttributes((Node) entity);
        }
        return 0;
    }

    private int getNodeDecorationAttributes(Node node) {
    	int result = 0;
        if (node instanceof Tree.Declaration || node instanceof Tree.Import) {
            ErrorCollectionVisitor ev = new ErrorCollectionVisitor(node, true);
            node.visit(ev);
            for (Message m: ev.getErrors()) {
            	if (m instanceof UsageWarning) {
            		result |= WARNING;
            	}
            	else {
            		result |= ERROR;
            	}
            }
            if (node instanceof Tree.Declaration) {
            	Tree.Declaration dec = (Tree.Declaration) node;
            	result |= getDecorationAttributes(dec.getDeclarationModel());
            }
        }
        return result;
    }

    private static int getDecorationAttributes(Declaration model) {
        if (model == null) {
            return 0;
        }
        
        int result = 0;
        if (model.isFormal()) {
            result |= FORMAL;
        }
        if ((model instanceof Value) && ((Value) model).isVariable()) {
            result |= VARIABLE;
        }
        if (model instanceof Class && ((Class) model).isAbstract()) {
            result |= ABSTRACT;
        }
        Declaration refined = getRefinedDeclaration(model);
        if (refined!=null) {
            result |= refined.isFormal() ? IMPLEMENTS : REFINES;
        }
        return result;
    }
}
