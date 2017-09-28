package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;

import org.eclipse.ceylon.model.typechecker.model.Declaration;

public interface IJavaModelAware extends IProjectAware {
    ITypeRoot getTypeRoot();
    IJavaElement toJavaElement(Declaration ceylonDeclaration, IProgressMonitor monitor);
    IJavaElement toJavaElement(Declaration ceylonDeclaration);
}
