import com.redhat.ceylon.eclipse.ui {
    CeylonPlugin
}
import com.redhat.ceylon.ide.common.platform {
    IdeUtils,
    Status
}

import java.lang {
    Thread
}

import org.eclipse.core.runtime {
    Plugin,
    EclipseStatus=Status,
    IStatus,
    OperationCanceledException
}

object eclipsePlatformUtils satisfies IdeUtils {
    function toEcliseStatus(Status status) => 
            switch(status)
            case(Status._OK) IStatus.\iOK
            case(Status._INFO) IStatus.info
            case(Status._DEBUG) IStatus.info
            case(Status._ERROR) IStatus.error
            case(Status._WARNING) IStatus.warning;

    log(Status status, String message, Exception? e) =>
            (CeylonPlugin.instance of Plugin)
                .log.log(EclipseStatus(toEcliseStatus(status), CeylonPlugin.pluginId, message, e));

    newOperationCanceledException(String message) => 
            OperationCanceledException("Operation Cancelled : ``message``");
    
    isOperationCanceledException(Exception exception) =>
            exception is OperationCanceledException;

    pluginClassLoader => Thread.currentThread().contextClassLoader;
    
    isExceptionToPropagateInVisitors(Exception exception) => false;    
}
