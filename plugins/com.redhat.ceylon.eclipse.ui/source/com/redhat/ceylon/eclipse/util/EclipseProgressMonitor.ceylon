import org.eclipse.core.runtime {
    IProgressMonitor
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitor
}
shared class EclipseProgressMonitor(IProgressMonitor wrapped) satisfies ProgressMonitor {

    shared actual variable Integer workRemaining = 0;
    
    shared actual void worked(Integer amount) => wrapped.worked(amount);

    shared actual void subTask(String? desc) => wrapped.subTask(desc);
}