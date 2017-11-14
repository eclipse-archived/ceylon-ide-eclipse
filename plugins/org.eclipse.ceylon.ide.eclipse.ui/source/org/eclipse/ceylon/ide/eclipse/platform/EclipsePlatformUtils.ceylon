/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin
}
import org.eclipse.ceylon.ide.common.platform {
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
