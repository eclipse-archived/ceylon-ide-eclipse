package com.redhat.ceylon.eclipse.imp.outline;

import static com.redhat.ceylon.eclipse.imp.editor.EditorAnnotationService.getRefinedDeclaration;

import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.services.DecorationDescriptor;
import org.eclipse.imp.services.DecorationDescriptor.Quadrant;
import org.eclipse.imp.services.IEntityImageDecorator;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.search.CeylonElement;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.ErrorCollectionVisitor;

public class CeylonEntityImageDecorator implements IEntityImageDecorator, ILanguageService {
    
    //private final static int WARNING = 1 << 2;
    private final static int ERROR = 1 << 3;
    private final static int REFINES = 1 << 4;
    private final static int IMPLEMENTS = 1 << 5;
    private final static int FORMAL = 1 << 6;
    //private final static int ABSTRACT = 1 << 7;
    private final static int DEFAULT = 1 << 7;

    @Override
    public DecorationDescriptor[] getAllDecorations() {
        return new DecorationDescriptor[] {
                new DecorationDescriptor(ERROR, CeylonPlugin.getInstance().getBundle(), 
                        "/icons/error.gif", Quadrant.BOTTOM_LEFT),
                new DecorationDescriptor(REFINES, CeylonPlugin.getInstance().getBundle(), 
                        "/icons/ceylon_decorator_actual.png", Quadrant.BOTTOM_RIGHT),
                new DecorationDescriptor(IMPLEMENTS, CeylonPlugin.getInstance().getBundle(), 
                        "/icons/ceylon_decorator_actual.png", Quadrant.BOTTOM_RIGHT),
                new DecorationDescriptor(FORMAL, CeylonPlugin.getInstance().getBundle(), 
                        "/icons/ceylon_decorator_formal.png", Quadrant.TOP_RIGHT),
                        new DecorationDescriptor(DEFAULT, CeylonPlugin.getInstance().getBundle(),
                                "/icons/ceylon_decorator_default.png", Quadrant.TOP_RIGHT)
                /*new DecorationDescriptor(ABSTRACT, CeylonPlugin.getInstance().getBundle(), 
                        "/icons/abstract_co.gif", Quadrant.TOP_RIGHT),*/
            };
    }
    
    @Override
    public int getDecorationAttributes(Object entity) {
        if (entity instanceof ModelTreeNode) {
            return getNodeDecorationAttributes((Node)((ModelTreeNode) entity).getASTNode());
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
        if (node instanceof Tree.Declaration) {
            Tree.Declaration dec = (Tree.Declaration) node;
            int result = getDecorationAttributes(dec.getDeclarationModel());
            ErrorCollectionVisitor ev = new ErrorCollectionVisitor(dec, true);
            dec.visit(ev);
            if (!ev.getErrors().isEmpty()) {
                result |= ERROR;
            }
            return result;
        }
        else {
            return 0;
        }
    }

    private static int getDecorationAttributes(Declaration model) {
        int result = 0;
        if (model.isFormal()) {
            result |= FORMAL;
        }
        if (model.isDefault()) {
            result |= DEFAULT;
        }
        /*if (model instanceof Class && ((Class) model).isAbstract()) {
            result |= ABSTRACT;
        }*/
        Declaration refined = getRefinedDeclaration(model);
        if (refined!=null) {
            result |= refined.isFormal() ? IMPLEMENTS : REFINES;
        }
        return result;
    }
    
}
