/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.common.util {
    unsafeCast,
    ProgressMonitorImpl,
    ProgressMonitor,
    ProgressMonitorChild
}

import org.eclipse.core.runtime {
    IProgressMonitor,
    SubMonitor,
    NullProgressMonitor
}

class EclipseProgressMonitorImpl
        extends ProgressMonitorImpl<IProgressMonitor> {
    <IProgressMonitor&Identifiable>? nativeMonitor;
    
    shared late actual SubMonitor wrapped;
    
    shared new child(EclipseProgressMonitorImpl parent, Integer allocatedWork)
        extends super.child(parent, allocatedWork) {
        nativeMonitor = null;
        wrapped = parent.wrapped.newChild(allocatedWork, SubMonitor.\iSUPPRESS_NONE);
    }
    
    shared new wrap(IProgressMonitor? monitor)
            extends super.wrap(monitor) {
        nativeMonitor = unsafeCast<IProgressMonitor&Identifiable>(monitor else NullProgressMonitor());
    }

    shared actual String? taskName => super.taskName;
    assign taskName {
        super.taskName = taskName;
        wrapped.setTaskName(taskName);
    }
    
    shared actual void initialze(Integer estimatedWork, String? taskName) {
        if (exists nativeMonitor) {
            if (!is SubMonitor nativeMonitor) {
                wrapped = SubMonitor.convert(nativeMonitor of IProgressMonitor, taskName, estimatedWork);
            } else {
                wrapped = nativeMonitor;
            }
        }
        super.initialze(estimatedWork, taskName);
    }
    
    shared actual void worked(Integer amount) => wrapped.worked(amount);
    shared actual void subTask(String subTaskDescription) => wrapped.subTask(subTaskDescription);
    shared actual Boolean cancelled => 
            if (exists nativeMonitor) 
            then nativeMonitor.canceled 
            else wrapped.canceled;
    
    shared actual ProgressMonitorChild<IProgressMonitor> newChild(Integer allocatedWork) => 
            EclipseProgressMonitorImpl.child(this, allocatedWork);
    
    shared actual void updateRemainingWork(Integer remainingWork) =>
            wrapped.setWorkRemaining(remainingWork);
    
    shared actual void done() {
        super.done();
        if (exists nativeMonitor,
            !(nativeMonitor === wrapped)) {
                nativeMonitor.done();
            }
        wrapped.done();
    }
}

shared alias EclipseProgressMonitor => ProgressMonitor<IProgressMonitor>;
shared alias EclipseProgressMonitorChild => ProgressMonitorChild<IProgressMonitor>;

shared ProgressMonitor<IProgressMonitor> wrapProgressMonitor(IProgressMonitor monitor) =>
    EclipseProgressMonitorImpl.wrap(monitor);
