package com.redhat.ceylon.eclipse.core.debug;

import static com.redhat.ceylon.eclipse.core.debug.CeylonPresentationContext.isCeylonContext;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.internal.debug.ui.variables.JavaVariableLabelProvider;
import org.eclipse.jface.viewers.TreePath;

import com.redhat.ceylon.compiler.java.codegen.Naming;

public class CeylonVariableLabelProvider extends JavaVariableLabelProvider{

    public CeylonVariableLabelProvider() {
        super();
        fLabelProvider = new CeylonJDIModelPresentation();
    }
    
    public CeylonJDIModelPresentation getCeylonJDIModelPresentation() {
        return (CeylonJDIModelPresentation) fLabelProvider;
    }

    @Override
    protected void retrieveLabel(final ILabelUpdate update) throws CoreException {
        super.retrieveLabel(new CeylonLabelUpdate(update));
    }
    
    @Override
    protected String getVariableName(IVariable variable,
            IPresentationContext context) throws CoreException {
        String name = super.getVariableName(variable, context);
        if (isCeylonContext(context)) {
            if (name.charAt(0) == '$') {
                if (Naming.isJavaKeyword(name, 1, name.length())) {
                    name = name.substring(1);
                }
            }
        }
        return name;
    }

    @Override
    protected String getValueTypeName(IVariable variable, IValue value,
            IPresentationContext context) throws CoreException {
        String name = super.getValueTypeName(variable, value, context);

        if (isCeylonContext(context)) {
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
    protected String getLabel(TreePath elementPath,
            IPresentationContext context, String columnId) throws CoreException {
        if (columnId == null) {
            // when no columns, handle special escaping ourselves
            IDebugModelPresentation presentation = getCeylonJDIModelPresentation();
            if (presentation != null) {
                return presentation.getText(elementPath.getLastSegment());
            }
        }
        return super.getLabel(elementPath, context, columnId);
    }

    @Override
    protected String getValueText(IVariable variable, IValue value,
            IPresentationContext context) throws CoreException {
        String valueText = super.getValueText(variable, value, context);
        return valueText;
    }
    
    @Override
    protected String getVariableTypeName(IVariable variable,
            IPresentationContext context) throws CoreException {
        String name = super.getVariableTypeName(variable, context);
        if (isCeylonContext(context)) {
            name = renameObjectTypeName(name);
        }
        return name;
    }

    @Override
    protected String getColumnText(IVariable variable, IValue value,
            IPresentationContext context, String columnId) throws CoreException {
        if (CeylonVariableColumnPresentation.COLUMN_REIFIED_TYPE.equals(columnId) && isCeylonContext(context)) {
            return getCeylonJDIModelPresentation().getCeylonReifiedTypeName(value);
        }
        return super.getColumnText(variable, value, context, columnId);
    }
}
