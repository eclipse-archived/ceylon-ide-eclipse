package com.redhat.ceylon.eclipse.core.launch;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.debug.core.sourcelookup.containers.ZipEntryStorage;
import org.eclipse.debug.ui.ISourcePresentation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.internal.debug.ui.BreakpointUtils;
import org.eclipse.jdt.internal.debug.ui.LocalFileStorageEditorInput;
import org.eclipse.jdt.internal.debug.ui.ZipEntryStorageEditorInput;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonSourceLookupDirector extends JavaSourceLookupDirector implements ISourcePresentation {

    @Override
    public IEditorInput getEditorInput(Object item) {
        if (item instanceof IMarker) {
            item = DebugPlugin.getDefault().getBreakpointManager().getBreakpoint((IMarker)item);
        }
        if (item instanceof IJavaBreakpoint) {
            IType type = BreakpointUtils.getType((IJavaBreakpoint)item);
            if (type == null) {
                // if the breakpoint is not associated with a type, use its resource
                item = ((IJavaBreakpoint)item).getMarker().getResource();
            } else {
                item = type;
            }
        }
        if (item instanceof LocalFileStorage) {
            return new LocalFileStorageEditorInput((LocalFileStorage)item);
        }
        if (item instanceof ZipEntryStorage) {
            if (((ZipEntryStorage) item).getName().endsWith(".ceylon")) {
                ZipEntryStorage zes = (ZipEntryStorage) item;
                IPath archivePath = Path.fromOSString(zes.getArchive().getName() + "!");            
                IEditorInput input = EditorUtil.getEditorInput(archivePath.append(zes.getZipEntry().getName()));
                return input;
            } else {
                return new ZipEntryStorageEditorInput((ZipEntryStorage)item);
            }
        }
        // for types that correspond to external files, return null so we do not
        // attempt to open a non-existing workspace file on the breakpoint (bug 184934)
        if (item instanceof IType) {
            IType type = (IType) item;
            if (!type.exists()) {
                return null;
            }
        }
        return EditorUtil.getEditorInput(item);
    }

    @Override
    public String getEditorId(IEditorInput input, Object element) {
        try {
            IEditorDescriptor descriptor= IDE.getEditorDescriptor(input.getName());
            return descriptor.getId();
        } catch (PartInitException e) {
            return null;
        }
    }

}
