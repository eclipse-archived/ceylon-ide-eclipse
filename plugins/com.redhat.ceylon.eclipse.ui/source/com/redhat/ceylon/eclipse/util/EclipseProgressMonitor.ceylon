import org.eclipse.core.runtime {
    IProgressMonitor,
    SubMonitor
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitor,
    unsafeCast
}
shared class EclipseProgressMonitor 
        extends ProgressMonitor<IProgressMonitor> {
    shared actual SubMonitor wrapped;
    shared new(IProgressMonitor nativeMonitor) 
            extends ProgressMonitor<IProgressMonitor>() {
        if (is SubMonitor nativeMonitor) {
            wrapped = nativeMonitor;
        } else {
            wrapped = SubMonitor.convert(nativeMonitor);
        }
    }
    
    shared actual void worked(Integer amount) => wrapped.worked(amount);
    shared actual void subTask(String? desc) => wrapped.subTask(desc);
    shared actual Boolean cancelled => wrapped.canceled;
    shared actual ProgressMonitor<IProgressMonitor> convert(Integer work, String taskName) =>
            let (subMonitor = SubMonitor.convert(wrapped, taskName, work))
            if (subMonitor === unsafeCast<Identifiable>(wrapped))
            then this
            else EclipseProgressMonitor(subMonitor);
    
    shared actual ProgressMonitor<IProgressMonitor> newChild(Integer work) => 
            EclipseProgressMonitor(wrapped.newChild(work));
    
    shared actual void updateRemainingWork(Integer remainingWork) =>
            wrapped.setWorkRemaining(remainingWork);
}