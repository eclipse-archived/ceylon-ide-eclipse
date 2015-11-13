import com.redhat.ceylon.ide.common.model {
    CrossProjectBinaryUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

import java.lang.ref {
    SoftReference
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}
import org.eclipse.jdt.core {
    ITypeRoot,
    IJavaElement,
    IClassFile
}
shared class EclipseCrossProjectBinaryUnit(
    ITypeRoot typeRoot,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends CrossProjectBinaryUnit<IProject, IResource, IFolder, IFile, ITypeRoot, IJavaElement>(typeRoot, filename, relativePath, fullPath, pkg) 
        satisfies EclipseJavaModelAware {
    shared actual variable SoftReference<EclipseJavaModelAware.ResolvedElements> resolvedElementsRef = SoftReference<EclipseJavaModelAware.ResolvedElements>(null);
}