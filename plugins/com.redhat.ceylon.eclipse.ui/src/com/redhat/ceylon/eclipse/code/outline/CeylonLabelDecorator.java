package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.AdditionalAnnotationCreator.getRefinedDeclaration;
import static org.eclipse.jface.viewers.IDecoration.BOTTOM_LEFT;
import static org.eclipse.jface.viewers.IDecoration.BOTTOM_RIGHT;
import static org.eclipse.jface.viewers.IDecoration.TOP_LEFT;
import static org.eclipse.jface.viewers.IDecoration.TOP_RIGHT;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.open.DeclarationWithProject;
import com.redhat.ceylon.eclipse.code.search.CeylonElement;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.ErrorCollectionVisitor;

/**
 * This class works, but we don't use it because it decorates the
 * images asynchronously, which looks just horrible in the UI.
 * However, we're still using its static utility methods directly
 * from CeylonLabelProvider.
 *
 */
public class CeylonLabelDecorator implements ILightweightLabelDecorator {
    
    private final ImageRegistry imageRegistry = CeylonPlugin.getInstance().getImageRegistry();
    
    public final static int WARNING = 1 << 2;
    public final static int ERROR = 1 << 3;
    private final static int REFINES = 1 << 4;
    private final static int IMPLEMENTS = 1 << 5;
    private final static int FORMAL = 1 << 6;
    private final static int ABSTRACT = 1 << 7;
    private final static int VARIABLE = 1 << 8;
    private final static int ANNOTATION = 1 << 9;
    private final static int ENUM = 1 << 10;
    private final static int ALIAS = 1 << 11;
    private final static int DEPRECATED = 1 << 12;
//    private final static int FINAL = 1 << 13;
    
    public static final String WARNING_IMAGE = "warning.gif";
    public static final String ERROR_IMAGE = "error.gif";
    public static final String REFINES_IMAGE = "over_tiny_co.gif";
    public static final String IMPLEMENTS_IMAGE = "implm_tiny_co.gif";
    public static final String FINAL_IMAGE = "final_co.gif";
    public static final String ABSTRACT_IMAGE = "abstract_co.gif";
    public static final String VARIABLE_IMAGE = "volatile_co.gif";
    public static final String ANNOTATION_IMAGE = "annotation_tsk.gif";
    public static final String ENUM_IMAGE = "enum_tsk.gif";
    public static final String ALIAS_IMAGE = "linked_co.gif";
    public static final String DEPRECATED_IMAGE = "deprecated.gif";
    
    static final DecorationDescriptor[] DECORATIONS = new DecorationDescriptor[] {
        new DecorationDescriptor(WARNING, WARNING_IMAGE, BOTTOM_LEFT),
        new DecorationDescriptor(ERROR, ERROR_IMAGE, BOTTOM_LEFT),
        new DecorationDescriptor(REFINES, REFINES_IMAGE, BOTTOM_RIGHT),
        new DecorationDescriptor(IMPLEMENTS, IMPLEMENTS_IMAGE, BOTTOM_RIGHT),
        new DecorationDescriptor(FORMAL, FINAL_IMAGE, TOP_RIGHT),
        new DecorationDescriptor(ABSTRACT, ABSTRACT_IMAGE, TOP_RIGHT),
        new DecorationDescriptor(VARIABLE, VARIABLE_IMAGE, TOP_LEFT),
        new DecorationDescriptor(ANNOTATION, ANNOTATION_IMAGE, TOP_LEFT),
        new DecorationDescriptor(ENUM, ENUM_IMAGE, TOP_LEFT),
        new DecorationDescriptor(ALIAS, ALIAS_IMAGE, TOP_LEFT),
        new DecorationDescriptor(DEPRECATED, DEPRECATED_IMAGE, IDecoration.UNDERLAY)
//        new DecorationDescriptor(FINAL, CeylonPlugin.getInstance().image("..."), TOP_RIGHT)
    };
    
    
    @Override
    public void addListener(ILabelProviderListener listener) {}
    
    @Override
    public void dispose() {}

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {}

    @Override
    public void decorate(Object element, IDecoration decoration) {
        int adornmentFlags = getDecorationAttributes(element);
        for (DecorationDescriptor d: DECORATIONS) {
            if (d.hasDecoration(adornmentFlags)) {
                decoration.addOverlay(imageRegistry.getDescriptor(d.getImageKey()), 
                        d.getQuadrant());
            }
        }
    }
    
    public static int getDecorationAttributes(Object entity) {
        if (entity instanceof IProject) {
            //TODO: add a Ceylon decoration to the project icon!
        }
        if (entity instanceof IResource) {
            int sev = getMaxProblemMarkerSeverity((IResource) entity, IResource.DEPTH_ONE);
            switch (sev) {
            case IMarker.SEVERITY_ERROR:
                return ERROR;
            case IMarker.SEVERITY_WARNING:
                return WARNING;
            default: 
                return 0;
            }
        }
        if (entity instanceof CeylonOutlineNode) {
            return getNodeDecorationAttributes(((CeylonOutlineNode) entity).getTreeNode());
        }
        if (entity instanceof DeclarationWithProject) {
            return getDecorationAttributes(((DeclarationWithProject) entity).getDeclaration());
        }
        if (entity instanceof CeylonElement) {
            return ((CeylonElement) entity).getDecorations();
        }
        if (entity instanceof Declaration) {
            return getDecorationAttributes((Declaration) entity);
        }
        if (entity instanceof Node) {
            return getNodeDecorationAttributes((Node) entity);
        }
        return 0;
    }

    public static int getNodeDecorationAttributes(Node node) {
        int result = 0;
        if (node instanceof Tree.Declaration || node instanceof Tree.Import) {
            ErrorCollectionVisitor ev = new ErrorCollectionVisitor(node, true);
            node.visit(ev);
            boolean warnings=false;
            boolean errors=false;
            for (Message m: ev.getErrors()) {
                if (m instanceof UsageWarning) {
                    warnings = true;
                }
                else {
                    errors = true;
                }
            }
            if (errors) {
                result |= ERROR;
            }
            else if (warnings) {
                result |= WARNING;
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
        if (model.isDeprecated()) {
            result |= DEPRECATED;
        }
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
        if (model instanceof TypeDeclaration) {
            TypeDeclaration td = (TypeDeclaration) model;
            if(td.getCaseTypeDeclarations()!=null) {
                result |= ENUM;
            }
            if (td.isAlias()) {
                result |= ALIAS;
            }
        }
        Declaration refined = getRefinedDeclaration(model);
        if (refined!=null) {
            result |= refined.isFormal() ? IMPLEMENTS : REFINES;
        }
        return result;
    }

    /**
     * Returns the maximum problem marker severity for the given resource, and, if
     * depth is IResource.DEPTH_INFINITE, its children. The return value will be
     * one of IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO
     * or 0, indicating that no problem markers exist on the given resource.
     * @param depth TODO
     */
    private static int getMaxProblemMarkerSeverity(IResource res, int depth) {
        if (res == null || !res.isAccessible())
            return 0;
    
        boolean hasWarnings= false; // if resource has errors, will return error image immediately
        IMarker[] markers= null;
        try {
            markers= res.findMarkers(IMarker.PROBLEM, true, depth);
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
        if (markers == null)
            return 0; // don't know - say no errors/warnings/infos
    
        for (int i= 0; i < markers.length; i++) {
            IMarker m= markers[i];
            int priority= m.getAttribute(IMarker.SEVERITY, -1);
            if (priority == IMarker.SEVERITY_WARNING) {
                hasWarnings= true;
            } 
            else if (priority == IMarker.SEVERITY_ERROR) {
                return IMarker.SEVERITY_ERROR;
            }
        }
        return hasWarnings ? IMarker.SEVERITY_WARNING : 0;
    }

}
