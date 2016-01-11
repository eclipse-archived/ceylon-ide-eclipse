import com.redhat.ceylon.eclipse.util {
    withJavaModel
}
import com.redhat.ceylon.ide.common.model {
    JavaUnitUtils
}

import org.eclipse.core.resources {
    IFolder,
    IFile
}
import org.eclipse.jdt.core {
    IPackageFragmentRoot,
    IJavaElement,
    ITypeRoot
}

shared interface EclipseJavaUnitUtils
        satisfies JavaUnitUtils<IFolder, IFile, ITypeRoot> {
    shared actual IFile? javaClassRootToNativeFile(ITypeRoot javaClassRoot) =>
            withJavaModel {
                do() => if (is IFile file = javaClassRoot.correspondingResource)
                            then file
                            else null;
            };
    
    shared actual IFolder? javaClassRootToNativeRootFolder(ITypeRoot javaClassRoot) =>
            withJavaModel {
                do() => 
                        if (is IPackageFragmentRoot root = javaClassRoot.getAncestor(IJavaElement.\iPACKAGE_FRAGMENT_ROOT),
                            is IFolder folder=root.correspondingResource)
                        then folder
                        else null;
            };
}