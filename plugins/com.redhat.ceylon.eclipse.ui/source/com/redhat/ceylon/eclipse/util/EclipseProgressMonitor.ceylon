import org.eclipse.core.runtime {
    IProgressMonitor,
    SubMonitor
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitor
}
shared class EclipseProgressMonitor(IProgressMonitor? nativeMonitor)
        extends ProgressMonitor<IProgressMonitor>() {
    assert(is Identifiable? theNativeMonitor=nativeMonitor);
    shared actual SubMonitor wrapped;
    if (is SubMonitor nativeMonitor) {
        wrapped = nativeMonitor;
    } else {
        wrapped = SubMonitor.convert(nativeMonitor);
    }
    
    shared actual void worked(Integer amount) => wrapped.worked(amount);
    shared actual void subTask(String? desc) => wrapped.subTask(desc);
    shared actual Boolean cancelled => wrapped.canceled;
    shared actual ProgressMonitor<IProgressMonitor> convert(Integer work, String taskName) =>
            let (subMonitor = SubMonitor.convert(wrapped, taskName, work))
            if (subMonitor === wrapped)
            then this
            else EclipseProgressMonitor(subMonitor);
    
    shared actual ProgressMonitor<IProgressMonitor> newChild(Integer work, Boolean prependMainLabelToSubtask) => 
            EclipseProgressMonitor(wrapped.newChild(
                work, 
                if (prependMainLabelToSubtask) 
                then SubMonitor.\iSUPPRESS_SETTASKNAME 
                else SubMonitor.\iSUPPRESS_BEGINTASK));
    
    shared actual void updateRemainingWork(Integer remainingWork) =>
            wrapped.setWorkRemaining(remainingWork);
    
    shared actual void done() {
        if (exists theNativeMonitor) {
            if (!(theNativeMonitor === wrapped)) {
                wrapped.done();
            }
            theNativeMonitor.done();
        } else {
            wrapped.done();
        }
    }
}