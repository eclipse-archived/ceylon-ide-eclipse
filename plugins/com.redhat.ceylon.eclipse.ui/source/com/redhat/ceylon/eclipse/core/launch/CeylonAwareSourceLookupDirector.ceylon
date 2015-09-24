import com.redhat.ceylon.eclipse.util {
    EditorUtil {
        getEditorInput,
        adjustEditorInput
    }
}

import org.eclipse.core.resources {
    IMarker
}
import org.eclipse.core.runtime {
    Path
}
import org.eclipse.debug.core {
    DebugPlugin
}
import org.eclipse.debug.core.sourcelookup.containers {
    LocalFileStorage,
    ZipEntryStorage
}
import org.eclipse.debug.ui {
    ISourcePresentation
}
import org.eclipse.jdt.core {
    IType,
    IJavaElement
}
import org.eclipse.jdt.debug.core {
    IJavaBreakpoint
}
import org.eclipse.jdt.internal.debug.ui {
    BreakpointUtils,
    LocalFileStorageEditorInput,
    ZipEntryStorageEditorInput
}
import org.eclipse.ui {
    IEditorDescriptor,
    IEditorInput,
    PartInitException
}
import org.eclipse.ui.ide {
    IDE
}
import org.eclipse.jdt.internal.launching {
    JavaSourceLookupDirector
}

shared interface CeylonAwareSourceLookupDirector satisfies ISourcePresentation {

    shared actual IEditorInput? getEditorInput(variable Object? item) {
        if (is IMarker marker=item) {
            item = DebugPlugin.default.breakpointManager.getBreakpoint(marker);
        }
        if (is IJavaBreakpoint breakpoint=item) {
            IType? type = BreakpointUtils.getType(breakpoint);
            if (exists type) {
                item = type;
            }
            else {
                item = breakpoint.marker?.resource;
            }
        }
        if (is LocalFileStorage fileStorage=item) {
            return adjustEditorInput(LocalFileStorageEditorInput(fileStorage));
        }
        if (is ZipEntryStorage entryStorage=item) {
            if (entryStorage.name.endsWith(".ceylon")) {
                assert(is ZipEntryStorage zes = item);
                value archivePath = Path.fromOSString("``zes.archive.name``!");
                variable IEditorInput input = EditorUtil.getEditorInput(archivePath.append(zes.zipEntry.name));
                return adjustEditorInput(input);
            }
            else {
                return ZipEntryStorageEditorInput(entryStorage);
            }
        }
        if (is IType type=item) {
            if (!(type of IJavaElement).\iexists()) {
                return null;
            }
        }
        return adjustEditorInput(EditorUtil.getEditorInput(item));
    }

    shared actual String? getEditorId(variable IEditorInput input, variable Object element) {
        try {
            variable IEditorDescriptor descriptor = IDE.getEditorDescriptor(input.name);
            return descriptor.id;
        }
        catch (PartInitException e) {
            return null;
        }
    }
}

shared class CeylonSourceLookupDirector()
        extends JavaSourceLookupDirector()
        satisfies CeylonAwareSourceLookupDirector {
    shared actual Object? getSourceElement(Object? o)
            => super.getSourceElement(o);
}

