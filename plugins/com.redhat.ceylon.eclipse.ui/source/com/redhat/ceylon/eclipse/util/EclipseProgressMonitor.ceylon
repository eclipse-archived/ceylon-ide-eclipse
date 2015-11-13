import org.eclipse.core.runtime {
    IProgressMonitor
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitor
}
shared class EclipseProgressMonitor(IProgressMonitor eclipseMonitor) 
        extends ProgressMonitor<IProgressMonitor>(eclipseMonitor) {

    shared actual void worked(Integer amount) => wrapped.worked(amount);
    shared actual void subTask(String? desc) => wrapped.subTask(desc);
}