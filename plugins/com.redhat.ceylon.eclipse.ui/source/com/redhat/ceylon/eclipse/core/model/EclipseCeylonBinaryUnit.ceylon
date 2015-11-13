import com.redhat.ceylon.ide.common.model {
    CeylonBinaryUnit
}
import org.eclipse.core.resources {
    IProject
}
import org.eclipse.jdt.core {
    ITypeRoot,
    IJavaElement,
    IClassFile
}
import java.lang.ref {
    SoftReference
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}
shared class EclipseCeylonBinaryUnit(
    ITypeRoot typeRoot,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends CeylonBinaryUnit<IProject, ITypeRoot, IJavaElement>(typeRoot, filename, relativePath, fullPath, pkg) 
        satisfies EclipseJavaModelAware {
    shared actual variable SoftReference<EclipseJavaModelAware.ResolvedElements> resolvedElementsRef = SoftReference<EclipseJavaModelAware.ResolvedElements>(null);
}