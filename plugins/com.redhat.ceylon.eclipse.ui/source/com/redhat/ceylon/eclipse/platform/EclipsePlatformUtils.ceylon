import com.redhat.ceylon.eclipse.ui {
    CeylonPlugin
}
import com.redhat.ceylon.ide.common.platform {
    IdeUtils,
    Status
}

import java.lang {
    RuntimeException,
    ClassLoader,
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
            case(Status._INFO) IStatus.\iINFO
            case(Status._DEBUG) IStatus.\iINFO
            case(Status._ERROR) IStatus.\iERROR
            case(Status._WARNING) IStatus.\iWARNING;

    shared actual void log(Status status, String message, Exception? e) =>
            (CeylonPlugin.instance of Plugin)
                .log.log(EclipseStatus(toEcliseStatus(status), CeylonPlugin.\iPLUGIN_ID, message, e));

    shared actual RuntimeException newOperationCanceledException(String message) => 
            OperationCanceledException("Operation Cancelled : ``message``");
    
    shared actual Boolean isOperationCanceledException(Exception exception) =>
            exception is OperationCanceledException;

    shared actual ClassLoader pluginClassLoader => Thread.currentThread().contextClassLoader;
}