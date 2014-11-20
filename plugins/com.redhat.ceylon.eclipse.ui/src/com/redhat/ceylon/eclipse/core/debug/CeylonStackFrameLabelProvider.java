package com.redhat.ceylon.eclipse.core.debug;

import static com.redhat.ceylon.eclipse.core.debug.CeylonDebugLabelUpdaterManager.getUpdater;

import java.util.regex.Matcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.ui.variables.JavaStackFrameLabelProvider;
import org.eclipse.jface.viewers.TreePath;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class CeylonStackFrameLabelProvider extends JavaStackFrameLabelProvider {

    private String updateExistingLabel(TreePath elementPath, String existingLabel) {
        Object element = elementPath.getLastSegment();
        if (element instanceof IJavaStackFrame) {
            IJavaStackFrame frame = (IJavaStackFrame) element;
            if (DebugUtils.isCeylonFrame(frame)) {
                CeylonDebugLabelUpdater updater = getUpdater(frame);
                if (updater != null) {
                    Matcher matcher = updater.matches(existingLabel);
                    if (matcher != null) {
                        Declaration declaration = DebugUtils.getStackFrameCeylonDeclaration(frame);
                        if (declaration != null) {
                            return updater.updateLabel(matcher, declaration);
                        }
                    }
                }
            }
        }
        return existingLabel;
    }
    
    @Override
    protected String getLabel(TreePath elementPath,
            IPresentationContext presentationContext, String columnId)
            throws CoreException {
        String existingLabel = super.getLabel(elementPath, presentationContext, columnId);
        updateExistingLabel(elementPath, existingLabel);
        return existingLabel;
    }

    @Override
    protected String getLabel(TreePath elementPath,
            IPresentationContext presentationContext, String columnId,
            int columnIndex) throws CoreException {
        String existingLabel = super.getLabel(elementPath, presentationContext, columnId, columnIndex);
        updateExistingLabel(elementPath, existingLabel);
        return existingLabel;
    }
}
