import com.redhat.ceylon.ide.common.model {
    JavaClassFile
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

import java.lang.ref {
    SoftReference
}

import org.eclipse.core.resources {
    IProject,
    IFolder,
    IFile
}
import org.eclipse.jdt.core {
    ITypeRoot,
    IJavaElement
}
shared class EclipseJavaClassFile(
    ITypeRoot typeRoot,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends JavaClassFile<IProject, IFolder, IFile, ITypeRoot, IJavaElement>(typeRoot, filename, relativePath, fullPath, pkg) 
        satisfies EclipseJavaModelAware
        & EclipseJavaUnitUtils {
    shared actual variable SoftReference<EclipseJavaModelAware.ResolvedElements> resolvedElementsRef = 
            SoftReference<EclipseJavaModelAware.ResolvedElements>(null);
    
}