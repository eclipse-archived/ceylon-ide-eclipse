import org.eclipse.ceylon.compiler.typechecker.context {
    Context
}
import org.eclipse.ceylon.ide.common.model {
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
import org.eclipse.ceylon.ide.common.platform {
    platformUtils,
    Status
}

shared class JDTModuleSourceMapper(
            Context context, 
            IdeModuleManager<IProject,IResource,IFolder,IFile> moduleManager)
        extends IdeModuleSourceMapper<IProject, IResource, IFolder,IFile>(context, moduleManager) {
    
    shared actual String defaultCharset => ResourcesPlugin.workspace.root.defaultCharset;
    
    shared actual void logModuleResolvingError(BaseIdeModule theModule, Exception e) {
        platformUtils.log(Status._ERROR, "Failed resolving module " + theModule.signature, e);
    }
    
}
