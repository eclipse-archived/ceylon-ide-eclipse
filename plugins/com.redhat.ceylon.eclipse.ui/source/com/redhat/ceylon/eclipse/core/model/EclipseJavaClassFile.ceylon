import org.eclipse.ceylon.ide.common.model {
    JavaClassFile
}
import org.eclipse.ceylon.model.loader.model {
    LazyPackage
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
    String theFilename,
    String theRelativePath,
    String theFullPath,
    LazyPackage pkg)
        extends JavaClassFile<IProject, IFolder, IFile, ITypeRoot, IJavaElement>(typeRoot, theFilename, theRelativePath, theFullPath, pkg) 
        satisfies EclipseJavaModelAware
        & EclipseJavaUnitUtils {
    shared actual variable SoftReference<EclipseJavaModelAware.ResolvedElements> resolvedElementsRef = 
            SoftReference<EclipseJavaModelAware.ResolvedElements>(null);
    
}