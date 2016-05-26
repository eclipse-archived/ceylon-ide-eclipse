import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject,
    CrossProjectJavaCompilationUnit
}
import com.redhat.ceylon.model.loader.model {
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

shared class EclipseCrossProjectJavaCompilationUnit(
    BaseCeylonProject ceylonProject,
    ITypeRoot typeRoot,
    String theFilename,
    String theRelativePath,
    String theFullPath,
    LazyPackage pkg)
        extends CrossProjectJavaCompilationUnit<IProject, IFolder, IFile, ITypeRoot, IJavaElement>(ceylonProject, typeRoot, theFilename, theRelativePath, theFullPath, pkg) 
        satisfies EclipseJavaModelAware
        & EclipseJavaUnitUtils {
    shared actual variable SoftReference<EclipseJavaModelAware.ResolvedElements> resolvedElementsRef = 
            SoftReference<EclipseJavaModelAware.ResolvedElements>(null);
    
}