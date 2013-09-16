package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.AdditionalAnnotationCreator.getRefinedDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.BOTTOM_LEFT;
import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.BOTTOM_RIGHT;
import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.TOP_LEFT;
import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.TOP_RIGHT;

import org.eclipse.jface.resource.ImageDescriptor;

import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.search.CeylonElement;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.ErrorCollectionVisitor;

public class CeylonEntityImageDecorator {
    
    public final static int WARNING = 1 << 2;
    public final static int ERROR = 1 << 3;
    private final static int REFINES = 1 << 4;
    private final static int IMPLEMENTS = 1 << 5;
    private final static int FORMAL = 1 << 6;
    private final static int ABSTRACT = 1 << 7;
    private final static int VARIABLE = 1 << 8;
    private final static int ANNOTATION = 1 << 9;
    private final static int ENUM = 1 << 10;
//    private final static int FINAL = 1 << 11;

    private static final ImageDescriptor WARNING_IMAGE = CeylonPlugin.getInstance().image("warning.gif");
    private static final ImageDescriptor ERROR_IMAGE = CeylonPlugin.getInstance().image("error.gif");
    private static final ImageDescriptor REFINES_IMAGE = CeylonPlugin.getInstance().image("over_tiny_co.gif");
    private static final ImageDescriptor IMPLEMENTS_IMAGE = CeylonPlugin.getInstance().image("implm_tiny_co.gif");
    private static final ImageDescriptor FINAL_IMAGE = CeylonPlugin.getInstance().image("final_co.gif");
    private static final ImageDescriptor ABSTRACT_IMAGE = CeylonPlugin.getInstance().image("abstract_co.gif");
    private static final ImageDescriptor VOLATILE_IMAGE = CeylonPlugin.getInstance().image("volatile_co.gif");
    private static final ImageDescriptor ANNOTATION_IMAGE = CeylonPlugin.getInstance().image("annotation_tsk.gif");
    private static final ImageDescriptor ENUM_IMAGE = CeylonPlugin.getInstance().image("enum_tsk.gif");
    
    private static final DecorationDescriptor[] DECORATIONS = new DecorationDescriptor[] {
        //TODO: cache the images in CeylonPlugin!
        new DecorationDescriptor(WARNING, WARNING_IMAGE, BOTTOM_LEFT),
        new DecorationDescriptor(ERROR, ERROR_IMAGE, BOTTOM_LEFT),
        new DecorationDescriptor(REFINES, REFINES_IMAGE, BOTTOM_RIGHT),
        new DecorationDescriptor(IMPLEMENTS, IMPLEMENTS_IMAGE, BOTTOM_RIGHT),
        new DecorationDescriptor(FORMAL, FINAL_IMAGE, TOP_RIGHT),
        new DecorationDescriptor(ABSTRACT, ABSTRACT_IMAGE, TOP_RIGHT),
        new DecorationDescriptor(VARIABLE, VOLATILE_IMAGE, TOP_LEFT),
        new DecorationDescriptor(ANNOTATION, ANNOTATION_IMAGE, TOP_LEFT),
        new DecorationDescriptor(ENUM, ENUM_IMAGE, TOP_LEFT)
//        new DecorationDescriptor(FINAL, CeylonPlugin.getInstance().image("del_stat.gif"), TOP_RIGHT)
    };
    
    public DecorationDescriptor[] getAllDecorations() {
        return DECORATIONS;
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
        if (model.isAnnotation()) {
            result |= ANNOTATION;
        }
        if ((model instanceof Value) && ((Value) model).isVariable()) {
            result |= VARIABLE;
        }
        if (model instanceof Class && ((Class) model).isAbstract()) {
            result |= ABSTRACT;
        }
//        if (model instanceof Class && ((Class) model).isFinal()) {
//            result |= FINAL;
//        }
        if (model instanceof TypeDeclaration && 
        		((TypeDeclaration) model).getCaseTypeDeclarations()!=null) {
            result |= ENUM;
        }
        Declaration refined = getRefinedDeclaration(model);
        if (refined!=null) {
            result |= refined.isFormal() ? IMPLEMENTS : REFINES;
        }
        return result;
    }
}
