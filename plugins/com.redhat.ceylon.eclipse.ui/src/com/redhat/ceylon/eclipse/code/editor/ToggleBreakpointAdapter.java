package com.redhat.ceylon.eclipse.code.editor;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager;

public class ToggleBreakpointAdapter implements IToggleBreakpointsTarget {

    private static final String JDT_DEBUG_PLUGIN_ID= "org.eclipse.jdt.debug";

    public ToggleBreakpointAdapter() {
    }

    public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
        if (selection instanceof ITextSelection) {
            ITextSelection textSel= (ITextSelection) selection;
            IEditorPart editorPart= (IEditorPart) part.getAdapter(IEditorPart.class);
            //TODO: handle org.eclipse.ui.ide.FileStoreEditorInput
            //      to set breakpoints in code from archives
            IEditorInput editorInput = editorPart.getEditorInput();
            final IFile origSrcFile;
            if (editorInput instanceof IFileEditorInput) {
                origSrcFile= ((IFileEditorInput)editorInput).getFile();
            } else if (editorInput instanceof FileStoreEditorInput) {
                URI uri = ((FileStoreEditorInput) editorInput).getURI();
                IResource resource = ExternalSourceArchiveManager.toResource(uri);
                if (resource instanceof IFile) {
                    origSrcFile = (IFile) resource;
                } else {
                    origSrcFile = null;
                }
            } else {
                origSrcFile = null;
            }
            final int lineNumber = textSel.getStartLine()+1;

            IWorkspaceRunnable wr= new IWorkspaceRunnable() {
                public void run(IProgressMonitor monitor) throws CoreException {
                    IMarker marker = findBreakpointMarker(origSrcFile, lineNumber);
                    if (marker != null) {
                        // The following will delete the associated marker
                        clearLineBreakpoint(origSrcFile, lineNumber);
                    } else {
                        // The following will create a marker as a side-effect
                        setLineBreakpoint(origSrcFile, lineNumber);
                    }
                }
            };
            try {
                getWorkspace().run(wr, null);
            } 
            catch (CoreException e) {
                throw new DebugException(e.getStatus());
            }
        }
    }
    
    private IMarker findBreakpointMarker(IFile srcFile, int lineNumber) throws CoreException {
        IMarker[] markers = srcFile.findMarkers(IBreakpoint.LINE_BREAKPOINT_MARKER, true, IResource.DEPTH_INFINITE);
        for (int k = 0; k < markers.length; k++ ){
            if (((Integer) markers[k].getAttribute(IMarker.LINE_NUMBER)).intValue() == lineNumber){
                return markers[k];
            }
        }
        return null;
    }

    public void setLineBreakpoint(IFile file, int lineNumber) throws CoreException {
        String srcFileName= file.getName();
        String typeName= srcFileName.substring(0, srcFileName.lastIndexOf('.'));
        Map<String,Object> bkptAttributes= new HashMap<String, Object>();
        bkptAttributes.put("org.eclipse.jdt.debug.core.sourceName", srcFileName);
        bkptAttributes.put("org.eclipse.jdt.debug.core.typeName", typeName);

        try {
            JDIDebugModel.createStratumBreakpoint(file, null, srcFileName, null, null, lineNumber, -1, -1, 0, true, bkptAttributes);
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public void clearLineBreakpoint(IFile file, int lineNumber) throws CoreException {
        try {
            IBreakpoint lineBkpt= findStratumBreakpoint(file, lineNumber);

            if (lineBkpt != null) {
                lineBkpt.delete();
            }
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public void disableLineBreakpoint(IFile file, int lineNumber) throws CoreException {
        try {
            IBreakpoint lineBkpt= findStratumBreakpoint(file, lineNumber);
            if (lineBkpt != null) {
                lineBkpt.setEnabled(false);
            }
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public void enableLineBreakpoint(IFile file, int lineNumber) throws CoreException {
        try {
            IBreakpoint lineBkpt= findStratumBreakpoint(file, lineNumber);
            if (lineBkpt != null) {
                lineBkpt.setEnabled(true);
            }
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a Java line breakpoint that is already registered with the breakpoint
     * manager for a type with the given name at the given line number.
     * 
     * @param typeName fully qualified type name
     * @param lineNumber line number
     * @return a Java line breakpoint that is already registered with the breakpoint
     *  manager for a type with the given name at the given line number or <code>null</code>
     * if no such breakpoint is registered
     * @exception CoreException if unable to retrieve the associated marker
     *  attributes (line number).
     */
    public static IJavaLineBreakpoint findStratumBreakpoint(IResource resource, int lineNumber) throws CoreException {
        String modelId= JDT_DEBUG_PLUGIN_ID;
        String markerType= "org.eclipse.jdt.debug.javaStratumLineBreakpointMarker";
        IBreakpointManager manager= DebugPlugin.getDefault().getBreakpointManager();
        IBreakpoint[] breakpoints= manager.getBreakpoints(modelId);
        for (int i = 0; i < breakpoints.length; i++) {
            if (!(breakpoints[i] instanceof IJavaLineBreakpoint)) {
                continue;
            }
            IJavaLineBreakpoint breakpoint = (IJavaLineBreakpoint) breakpoints[i];
            IMarker marker = breakpoint.getMarker();
            if (marker != null && marker.exists() && marker.getType().equals(markerType)) {
                if (breakpoint.getLineNumber() == lineNumber &&
                    resource.equals(marker.getResource())) {
                        return breakpoint;
                }
            }
        }
        return null;
    }
    
    public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
        return true;
    }

    public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
    }

    public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
        return false;
    }

    public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
    }

    public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
        return false;
    }
}
