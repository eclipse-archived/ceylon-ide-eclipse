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
    String theFilename,
    String theRelativePath,
    String theFullPath,
    Package pkg)
        extends CeylonBinaryUnit<IProject, ITypeRoot, IJavaElement>(typeRoot, theFilename, theRelativePath, theFullPath, pkg) 
        satisfies EclipseJavaModelAware {
    shared actual variable SoftReference<EclipseJavaModelAware.ResolvedElements> resolvedElementsRef = SoftReference<EclipseJavaModelAware.ResolvedElements>(null);
}