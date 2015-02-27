package com.redhat.ceylon.eclipse.core.debug.actions;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyView.showHierarchyView;
import static com.redhat.ceylon.eclipse.util.JavaSearch.isCeylonDeclaration;
import static com.redhat.ceylon.eclipse.util.JavaSearch.toCeylonDeclaration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.debug.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public abstract class CeylonOpenTypeAction extends OpenTypeAction {
    
    @Override
    protected Object resolveSourceElement(Object e) throws CoreException {
        return super.resolveSourceElement(e);
    }
    
    @Override
    protected void openInEditor(Object sourceElement) 
            throws CoreException {
        if (sourceElement instanceof IJavaElement && 
                isCeylonDeclaration((IJavaElement) sourceElement)) {
            IJavaElement javaElement = (IJavaElement) sourceElement;
            IProject project = javaElement.getJavaProject().getProject();
            if (isHierarchy()) {
                Declaration declaration = 
                        toCeylonDeclaration(project, javaElement);
                if (declaration!=null) {
                    try {
                        showHierarchyView().focusOn(declaration);
                        return;
                    }
                    catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
                typeHierarchyError();
            }
            else {
                Declaration declaration = 
                        toCeylonDeclaration(project, javaElement);
                if (declaration != null) {
                    gotoDeclaration(declaration);
                    return;
                }
                showErrorMessage(ActionMessages.OpenTypeAction_2);
            }
        } else {
            super.openInEditor(sourceElement);
        }
    }
       
}
