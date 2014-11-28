package com.redhat.ceylon.eclipse.core.debug;

import static com.redhat.ceylon.eclipse.core.debug.CeylonDebugLabelUpdaterManager.getUpdater;
import static com.redhat.ceylon.eclipse.core.debug.CeylonPresentationContext.isCeylonContext;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.getStackFrameCeylonDeclaration;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.isMethodFilteredForCeylon;

import java.util.regex.Matcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.ui.variables.JavaStackFrameLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.graphics.RGB;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class CeylonStackFrameLabelProvider extends JavaStackFrameLabelProvider {

    @Override
    protected void retrieveLabel(ILabelUpdate update) throws CoreException {
        super.retrieveLabel(new CeylonLabelUpdate(update));
    }
    private String updateExistingLabel(TreePath elementPath, String existingLabel, IPresentationContext context) {
        Object element = elementPath.getLastSegment();
        if (element instanceof IJavaStackFrame) {
            IJavaStackFrame frame = (IJavaStackFrame) element;
            if (isCeylonContext(context)) {
                CeylonDebugLabelUpdater updater = getUpdater(frame);
                if (updater != null) {
                    Matcher matcher = updater.matches(existingLabel);
                    if (matcher != null) {
                        Declaration declaration = getStackFrameCeylonDeclaration(frame);
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
    protected RGB getForeground(TreePath elementPath,
            IPresentationContext presentationContext, String columnId)
            throws CoreException {
        RGB color = super.getForeground(elementPath, presentationContext, columnId);

        Object element = elementPath.getLastSegment();
        if (element instanceof JDIStackFrame) {
            JDIStackFrame frame = (JDIStackFrame) element;
            IThread thread = frame.getThread();
            if (thread instanceof CeylonJDIThread) {
                CeylonJDIThread ceylonJdiThread = (CeylonJDIThread) thread;
                if (ceylonJdiThread.isBeforeStart(frame) || isMethodFilteredForCeylon(frame.getUnderlyingMethod())) {
                    return new RGB(200, 200, 200);
                }
            }
        }
        
        return color;
    }
    
    @Override
    protected String getLabel(TreePath elementPath,
            IPresentationContext presentationContext, String columnId)
            throws CoreException {
        return updateExistingLabel(
                elementPath, 
                super.getLabel(elementPath, presentationContext, columnId),
                presentationContext);
    }

    @Override
    protected String getLabel(TreePath elementPath,
            IPresentationContext presentationContext, String columnId,
            int columnIndex) throws CoreException {
        return updateExistingLabel(
                elementPath, 
                super.getLabel(elementPath, presentationContext, columnId, columnIndex),
                presentationContext);
    }
}
