import com.redhat.ceylon.eclipse.core.model {
    EclipseCeylonBinaryUnit,
    EclipseJavaClassFile,
    EclipseJavaCompilationUnit,
    EclipseCrossProjectBinaryUnit,
    EclipseCrossProjectJavaCompilationUnit
}
import com.redhat.ceylon.eclipse.core.model.mirror {
    JDTClass
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    JavaModelServices
}
import com.redhat.ceylon.model.loader.mirror {
    ClassMirror
}
import com.redhat.ceylon.model.loader.model {
    LazyPackage
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}

import org.eclipse.jdt.core {
    ITypeRoot,
    IType
}

object eclipseJavaModelServices 
        satisfies JavaModelServices<ITypeRoot> {
    shared actual ITypeRoot? getJavaClassRoot(ClassMirror classMirror) {
        if (is JDTClass jdtClass=classMirror) {
            IType? type = jdtClass.type;
            if (exists type) {
                return type.typeRoot;
            }
        }
        return null;
    }
    
    shared actual Unit newCeylonBinaryUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseCeylonBinaryUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newCrossProjectBinaryUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseCrossProjectBinaryUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newJavaClassFile(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseJavaClassFile(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newJavaCompilationUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseJavaCompilationUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newCrossProjectJavaCompilationUnit(BaseCeylonProject ceylonProject, ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseCrossProjectJavaCompilationUnit(ceylonProject, typeRoot, fileName, relativePath, fullPath, pkg);

}