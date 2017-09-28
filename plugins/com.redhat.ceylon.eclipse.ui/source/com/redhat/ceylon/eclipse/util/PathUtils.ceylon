import org.eclipse.core.runtime {
    IPath,
    EclipsePath = Path
}
import org.eclipse.ceylon.ide.common.util {
    Path
}

shared IPath toEclipsePath(Path commonPath) =>
        EclipsePath(commonPath.string);

shared Path fromEclipsePath(IPath eclipsePath) =>
        Path(eclipsePath.string);