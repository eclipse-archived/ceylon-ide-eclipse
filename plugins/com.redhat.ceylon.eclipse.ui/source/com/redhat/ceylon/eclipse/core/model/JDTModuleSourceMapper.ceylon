import com.redhat.ceylon.compiler.typechecker.context {
    Context
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    IdeModuleManager,
    IdeModuleSourceMapper,
    BaseIdeModule
}

import org.eclipse.core.resources {
    IProject,
    ResourcesPlugin,
    IResource,
    IFolder,
    IFile
}
import com.redhat.ceylon.ide.common.util {
    platformUtils,
    Status
}

shared class JDTModuleSourceMapper(
            Context context, 
            CeylonProject<IProject,IResource,IFolder,IFile>? ceylonProject, 
            IdeModuleManager<IProject,IResource,IFolder,IFile> moduleManager)
        extends IdeModuleSourceMapper<IProject, IResource, IFolder,IFile>(context, moduleManager) {
    
    shared actual String defaultCharset => ResourcesPlugin.workspace.root.defaultCharset;
    
    shared actual void logModuleResolvingError(BaseIdeModule theModule, Exception e) {
        platformUtils.log(Status._ERROR, "Failed resolving module " + theModule.signature, e);
    }
    
}
