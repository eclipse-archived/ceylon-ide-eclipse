import com.redhat.ceylon.ide.common.platform {
    PlatformServices,
    ModelServices,
    IdePlatformUtils
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast
}

object eclipsePlatformServices satisfies PlatformServices {
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile> model<NativeProject, NativeResource, NativeFolder, NativeFile>() => 
            unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(eclipseModelServices);
    
    shared actual IdePlatformUtils utils() => eclipsePlatformUtils;
}