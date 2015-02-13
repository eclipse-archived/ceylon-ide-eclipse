package com.redhat.ceylon.eclipse.core.debug.presentation;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.core.LaunchManager;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.eclipse.core.debug.DebugUtils;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget;

class CeylonPresentationContext implements IPresentationContext {
    private IPresentationContext delegate;

    public CeylonPresentationContext(IPresentationContext delegate) {
        this.delegate = delegate;
    }

    public String[] getColumns() {
        return delegate.getColumns();
    }

    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        delegate.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        delegate.removePropertyChangeListener(listener);
    }

    public String getId() {
        return delegate.getId();
    }

    public void setProperty(String property, Object value) {
        delegate.setProperty(property, value);
    }

    public Object getProperty(String property) {
        return delegate.getProperty(property);
    }

    public void dispose() {
        delegate.dispose();
    }

    public String[] getProperties() {
        return delegate.getProperties();
    }

    public IWorkbenchPart getPart() {
        return delegate.getPart();
    }

    public IWorkbenchWindow getWindow() {
        return delegate.getWindow();
    }

    static IPresentationContext toCeylonContextIfNecessary(IPresentationContext context, IViewerUpdate viewerUpdate) {
        if (context instanceof CeylonPresentationContext) {
            return context;
        }
        
        if (isCeylonContext(viewerUpdate)) {
            return new CeylonPresentationContext(context);
        }
        return context;
    }
    
    static boolean isCeylonContext(IPresentationContext context) {
        return context instanceof CeylonPresentationContext;
    }
    
    static IJavaStackFrame getCeylonStackFrame(IViewerUpdate viewerUpdate) {
        IJavaStackFrame frame = getStackFrame(viewerUpdate);
        if (frame != null && DebugUtils.isCeylonFrame(frame)) {
                return frame;
        }
        return null;
    }

    private static IJavaStackFrame getStackFrame(IViewerUpdate viewerUpdate) {
        Object input = viewerUpdate.getViewerInput();
        if (input instanceof IJavaStackFrame) {
            return (IJavaStackFrame) input;
        }
        if (input instanceof LaunchManager) {
            if (viewerUpdate.getElement() instanceof IJavaStackFrame) {
                return (IJavaStackFrame) viewerUpdate.getElement();
            }
        }
        IAdaptable context = DebugUITools.getDebugContext();
        if (context != null) {
            return (IJavaStackFrame) context.getAdapter(IJavaStackFrame.class);
        }
        return null;
    }
    
    static Boolean isInCeylonFile(IJavaReferenceType type) throws DebugException {
        String sourceName = null;
        sourceName = type.getSourceName();
        if (sourceName != null
                && sourceName.endsWith(".ceylon")) {
            return true;
        }
        return false;
    }

    static IJavaReferenceType getReferenceType(Object obj) throws DebugException {
        IJavaReferenceType type = null;
        if (obj instanceof IJavaReferenceType) {
            type = (IJavaReferenceType) obj;
        } else {
            IJavaValue value = null;
            if (obj instanceof IJavaValue) {
                value = (IJavaValue) obj;
            } else if (obj instanceof IJavaVariable) {
                value = (IJavaValue) ((IJavaVariable)obj).getValue();
            }
            if (value != null) {
                IJavaType valueType = value.getJavaType();
                if (valueType instanceof IJavaReferenceType) {
                    type = (IJavaReferenceType) valueType;
                }
            }
        }
        return type;
    }
    
    static Boolean isKnownAsCeylon(String typeName) throws DebugException {
        if (typeName.startsWith("ceylon.language.")) {
            return true;
        }
        return false;
    }

    static Boolean isKnownAsJava(String typeName) throws DebugException {
        if (typeName.startsWith("java.lang")) {
            return true;
        }
        return false;
    }

    static boolean isCeylonContext(IJavaValue value, PresentationType presentationType) {
        IAdaptable debugContext = DebugUITools.getDebugContext();
        if (! (debugContext instanceof IJavaStackFrame)) {
            return false;
        }
        
        return CeylonPresentationContext.isCeylonContext(
                (IJavaStackFrame) debugContext,
                value,
                presentationType);
    }

    static boolean isCeylonContext(IJavaVariable var, PresentationType presentationType) {
        IAdaptable debugContext = DebugUITools.getDebugContext();
        if (! (debugContext instanceof IJavaStackFrame)) {
            return false;
        }
        IJavaValue value = null;
        try {
            value = (IJavaValue) var.getValue();
        } catch (DebugException e) {
            return false;
        }
        if (value == null) {
            return false;
        }
        return CeylonPresentationContext.isCeylonContext(
                (IJavaStackFrame) debugContext,
                value,
                presentationType);
    }

    static boolean isCeylonContext(IViewerUpdate viewerUpdate) {
        return isCeylonContext(
                getStackFrame(viewerUpdate), 
                viewerUpdate.getElement(),
                viewerUpdate instanceof ILabelUpdate ? PresentationType.LABEL : PresentationType.CHILDREN);
    }
    
    public static enum PresentationType {
        LABEL,
        CHILDREN
    }
    
    static boolean isCeylonContext(IJavaStackFrame stackFrame, Object element, PresentationType presentationType) {
        if (stackFrame == null || ! DebugUtils.isCeylonFrame(stackFrame)) {
            // We are suspended in a Java file => adopt the Java presentations
            return false;
        }
        
        if (presentationType.equals(PresentationType.CHILDREN)) {
            IJavaReferenceType type;
            try {
                type = getReferenceType(element);
                if (type != null) {
                    // If the current model element has a Class (variable in the Variables View for example), 
                    // Analyze the Class to know whether we use the Java or the Ceylon presentation
                    if (isInCeylonFile(type)) {
                        return true;
                    }
                    String typeName = type.getName();
                    if (typeName != null) {
                        if (isKnownAsCeylon(typeName)) {
                            return true;
                        }

                        if (isKnownAsJava(typeName)) {
                            return false;
                        }
                    }

                    // We are in a Ceylon stackframe, this class is defined in Java, 
                    // and we don't know if it is a Ceylon class
                    // => look at the Ceylon annotation
                    IDebugTarget dt = type.getDebugTarget();
                    if (dt instanceof CeylonJDIDebugTarget) {
                        CeylonJDIDebugTarget target = (CeylonJDIDebugTarget) dt;
                        return target.isAnnotationPresent((IJavaReferenceType) type, Ceylon.class, 5000);
                    }
                    return false;
                }
            } catch (DebugException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}