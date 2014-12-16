package com.redhat.ceylon.eclipse.core.debug.actions;

import static com.redhat.ceylon.eclipse.code.outline.HierarchyView.showHierarchyView;
import static com.redhat.ceylon.eclipse.util.JavaSearch.toCeylonDeclaration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.debug.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.util.JavaSearch;

public abstract class CeylonOpenTypeAction extends OpenTypeAction {
    @Override
    protected Object resolveSourceElement(Object e) throws CoreException {
        return super.resolveSourceElement(e);
    }
    
    @Override
    protected void openInEditor(Object sourceElement) throws CoreException {
        if (sourceElement instanceof IJavaElement && 
                JavaSearch.isCeylonDeclaration((IJavaElement) sourceElement)) {
            IJavaElement javaElement = (IJavaElement) sourceElement;
            IProject project = javaElement.getJavaProject().getProject();
            if (isHierarchy()) {
                Declaration d = JavaSearch.toCeylonDeclaration(project, javaElement);
                if (d != null) {
                    try {
                        showHierarchyView().focusOn(project, d);
                        return;
                    }
                    catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
                typeHierarchyError();
            } else {
                Declaration d = toCeylonDeclaration(project, javaElement);
                if (d != null) {
                    Navigation.gotoDeclaration(d, project);
                    return;
                }
                showErrorMessage(ActionMessages.OpenTypeAction_2);
            }
        } else {
            super.openInEditor(sourceElement);
        }
    }
    
    
}
