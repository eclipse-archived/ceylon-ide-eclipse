import com.redhat.ceylon.ide.common.model {
    CeylonBinaryUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

import java.lang.ref {
    SoftReference
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.jdt.core {
    ITypeRoot,
    IJavaElement
}
shared class EclipseCeylonBinaryUnit(
    ITypeRoot theTypeRoot,
    String theFilename,
    String theRelativePath,
    String theFullPath,
    Package pkg)
        extends CeylonBinaryUnit<IProject, ITypeRoot, IJavaElement>(theTypeRoot, theFilename, theRelativePath, theFullPath, pkg) 
        satisfies EclipseJavaModelAware {
    shared actual variable SoftReference<EclipseJavaModelAware.ResolvedElements> resolvedElementsRef = SoftReference<EclipseJavaModelAware.ResolvedElements>(null);
}