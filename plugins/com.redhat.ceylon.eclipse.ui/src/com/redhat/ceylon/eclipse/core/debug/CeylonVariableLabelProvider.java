package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jdt.internal.debug.ui.variables.JavaVariableLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import com.redhat.ceylon.compiler.java.codegen.Naming;

public class CeylonVariableLabelProvider extends JavaVariableLabelProvider{
    @Override
    protected void retrieveLabel(final ILabelUpdate update) throws CoreException {
        super.retrieveLabel(new ILabelUpdate() {
            @Override
            public IPresentationContext getPresentationContext() {
                return new CeylonPresentationContext(update.getPresentationContext(), this);
            }
            @Override
            public Object getElement() {
                return update.getElement();
            }
            @Override
            public TreePath getElementPath() {
                return update.getElementPath();
            }
            @Override
            public Object getViewerInput() {
                return update.getViewerInput();
            }
            @Override
            public void setStatus(IStatus status) {
                update.setStatus(status);
            }
            @Override
            public IStatus getStatus() {
                return update.getStatus();
            }
            @Override
            public void done() {
                update.done();
            }
            @Override
            public void cancel() {
                update.cancel();
            }
            @Override
            public boolean isCanceled() {
                return update.isCanceled();
            }
            @Override
            public String[] getColumnIds() {
                return update.getColumnIds();
            }
            @Override
            public void setLabel(String text, int columnIndex) {
                update.setLabel(text, columnIndex);
            }
            @Override
            public void setFontData(FontData fontData, int columnIndex) {
                update.setFontData(fontData, columnIndex);
            }
            @Override
            public void setImageDescriptor(ImageDescriptor image,
                    int columnIndex) {
                update.setImageDescriptor(image, columnIndex);
            }
            @Override
            public void setForeground(RGB foreground, int columnIndex) {
                update.setForeground(foreground, columnIndex);
            }
            @Override
            public void setBackground(RGB background, int columnIndex) {
                update.setBackground(background, columnIndex);
            }
        });
    }
    
    @Override
    protected String getVariableName(IVariable variable,
            IPresentationContext context) throws CoreException {
        String name = super.getVariableName(variable, context);
        if (context instanceof CeylonPresentationContext) {
            if (((CeylonPresentationContext) context).isCeylonContext()) {
                if (name.charAt(0) == '$') {
                    if (Naming.isJavaKeyword(name, 1, name.length())) {
                        name = name.substring(1);
                    }
                }
            }
        }
        return name;
    }

    @Override
    protected String getValueTypeName(IVariable variable, IValue value,
            IPresentationContext context) throws CoreException {
        String name = super.getValueTypeName(variable, value, context);

        if (((CeylonPresentationContext) context).isCeylonContext()) {
            name = renameObjectTypeName(name);
        }
        return name;
    }

    private String renameObjectTypeName(String typeName)
            throws DebugException {
        int index = typeName.lastIndexOf('.');
        if (index > 0) {
            typeName = typeName.substring(index+1);
        }
        
        if (! Character.isUpperCase(typeName.charAt(0)) &&
                typeName.endsWith("_")) {
            typeName = typeName.substring(0, typeName.length() - 1);
        }
        return typeName;
    }

    @Override
    protected String getValueText(IVariable variable, IValue value,
            IPresentationContext context) throws CoreException {
        String valueText = super.getValueText(variable, value, context);
        if (((CeylonPresentationContext) context).isCeylonContext()) {
            String valueTypeName = super.getValueTypeName(variable, value, context);
            if (valueText.contains(valueTypeName)) {
                valueText = valueText.replace(valueTypeName, renameObjectTypeName(valueTypeName));
            }
        }
        return valueText;
    }
    
    @Override
    protected String getVariableTypeName(IVariable variable,
            IPresentationContext context) throws CoreException {
        String name = super.getVariableTypeName(variable, context);
        if (((CeylonPresentationContext) context).isCeylonContext()) {
            name = renameObjectTypeName(name);
        }
        return name;
    }
}
