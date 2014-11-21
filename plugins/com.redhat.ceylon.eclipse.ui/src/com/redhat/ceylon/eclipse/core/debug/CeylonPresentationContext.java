package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;

class CeylonPresentationContext implements IPresentationContext {
    private IPresentationContext delegate;
    private IViewerUpdate viewerUpdate;

    public CeylonPresentationContext(IPresentationContext delegate,
            IViewerUpdate viewerUpdate) {
        this.delegate = delegate;
        this.viewerUpdate = viewerUpdate;
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

    public boolean isCeylonContext() {
        return isCeylonContext(viewerUpdate);
    }

    static boolean isCeylonContext(IViewerUpdate viewerUpdate) {
        Object input = viewerUpdate.getViewerInput();
        if (input instanceof IJavaStackFrame) {
            final IJavaStackFrame frame = (IJavaStackFrame) input;
            if (DebugUtils.isCeylonFrame(frame)) {
                // if the current object is a Java Class, show the children with
                // the JDT presentation
                Object element = viewerUpdate.getElement();
                if (element instanceof IJavaVariable) {
                    IJavaType type;
                    try {
                        IJavaValue value = (IJavaValue) ((IJavaVariable) element)
                                .getValue();
                        type = value.getJavaType();
                        if (type instanceof IJavaReferenceType) {
                            String sourceName = ((IJavaReferenceType) type)
                                    .getSourceName();
                            if (sourceName != null
                                    && sourceName.endsWith(".ceylon")) {
                                return true;
                            }

                            String typeName = type.getName();
                            if (typeName != null) {
                                
                                if (typeName.startsWith("ceylon.language.")) {
                                    return true;
                                }
                                
                                if (typeName.startsWith("java.lang")) {
                                    return false;
                                }
                            }
                            
                            if (type instanceof IJavaReferenceType) {
                                IDebugTarget dt = type.getDebugTarget();
                                if (dt instanceof CeylonJDIDebugTarget) {
                                    CeylonJDIDebugTarget target = (CeylonJDIDebugTarget) dt;
                                    if (target.isAnnotationPresent(frame, (IJavaReferenceType) type, Ceylon.class)) {
                                        return true;
                                    }
                                }
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
            ;
        }
        return false;
    }
}