import com.redhat.ceylon.ide.common.util {
    unsafeCast,
    ProgressMonitorImpl
}

import org.eclipse.core.runtime {
    IProgressMonitor,
    SubMonitor,
    NullProgressMonitor
}

shared abstract class EclipseProgressMonitor
        extends ProgressMonitorImpl<IProgressMonitor> {
    
    shared new child(ProgressMonitorImpl<IProgressMonitor> parent, Integer allocatedWork)
                    extends super.child(parent, allocatedWork) {
    }
    
    shared new wrap(IProgressMonitor? monitor) 
                    extends super.wrap(monitor) {
    }
    
        shared actual formal SubMonitor wrapped;
    shared actual class Progress(Integer estimatedWork, String? taskName)
             extends super.Progress(estimatedWork, taskName) {
        shared actual EclipseProgressMonitor newChild(Integer allocatedWork)
            => outer.newChild(allocatedWork);
        shared actual SubMonitor wrapped
            => outer.wrapped;
    }
    shared actual formal EclipseProgressMonitor newChild(Integer allocatedWork);
}

class EclipseProgressMonitorImpl
        extends EclipseProgressMonitor {
    <IProgressMonitor&Identifiable>|Null nativeMonitor;
    shared actual late SubMonitor wrapped;

    new child(EclipseProgressMonitorImpl parent, Integer allocatedWork)
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
    shared actual Boolean cancelled => wrapped.canceled;
    shared actual EclipseProgressMonitor newChild(Integer allocatedWork) => 
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

shared EclipseProgressMonitor wrapProgressMonitor(IProgressMonitor monitor) =>
    EclipseProgressMonitorImpl.wrap(monitor);