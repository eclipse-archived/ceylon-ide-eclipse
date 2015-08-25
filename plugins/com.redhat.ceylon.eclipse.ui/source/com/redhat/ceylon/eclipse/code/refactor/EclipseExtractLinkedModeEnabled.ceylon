import com.redhat.ceylon.ide.common.refactoring {
    ExtractLinkedModeEnabled
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.jface.text {
    IRegion
}
shared interface EclipseExtractLinkedModeEnabled
        satisfies ExtractLinkedModeEnabled<IRegion> {
    shared formal void extractInFile(TextChange tfc);
}