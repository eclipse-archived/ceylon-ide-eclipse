package com.redhat.ceylon.eclipse.code.editor;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.RunToLineHandler;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaStratumLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.jdt.internal.debug.ui.BreakpointUtils;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;

public class ToggleBreakpointAdapter implements IToggleBreakpointsTarget, IRunToLineTarget {

    private static final String JDT_DEBUG_PLUGIN_ID = "org.eclipse.jdt.debug";

    public ToggleBreakpointAdapter() {}

    static interface BreakpointAction {
        void doIt(ITextSelection selection, Node node, boolean isValidLocation, int lineNumber, IFile sourceFile) throws CoreException;
    }
    

    public void doOnBreakpoints(IWorkbenchPart part, ISelection selection, BreakpointAction action) throws CoreException {
        if (selection instanceof ITextSelection) {
            ITextSelection textSel= (ITextSelection) selection;
            IEditorPart editorPart= (IEditorPart) part.getAdapter(IEditorPart.class);
            //TODO: handle org.eclipse.ui.ide.FileStoreEditorInput
            //      to set breakpoints in code from archives
            IEditorInput editorInput = editorPart.getEditorInput();
            final IFile origSrcFile = getSourceFile(editorInput);
            int line = textSel.getStartLine();
            boolean emptyLine = false;
            Node node = null;
            try {
                CeylonEditor editor = (CeylonEditor) editorPart;
                IDocument document = editor.getCeylonSourceViewer().getDocument();
                Tree.CompilationUnit rootNode = editor.getParseController().getRootNode();
                final IRegion lineInformation = document.getLineInformation(line);
                String text = 
                        document.get(lineInformation.getOffset(), 
                                lineInformation.getLength()).trim();
                emptyLine = text.isEmpty();
                if (!emptyLine) {
                    node = getNode(rootNode, lineInformation);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            final boolean breakable = !emptyLine && node!=null;
            final int lineNumber = line+1;
            action.doIt(textSel, node, breakable, lineNumber, origSrcFile);
        }
    }
    
    public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
        doOnBreakpoints(part, selection, new BreakpointAction() {

            @Override
            public void doIt(final ITextSelection selection, final Node node,
                    final boolean isValidLocation, final int lineNumber, final IFile sourceFile) throws DebugException {
                IWorkspaceRunnable wr = new IWorkspaceRunnable() {
                    public void run(IProgressMonitor monitor) throws CoreException {
                        IMarker marker = findBreakpointMarker(sourceFile, lineNumber);
                        if (marker!=null) {
                            // The following will delete the associated marker
                            clearLineBreakpoint(sourceFile, lineNumber);
                        } else if (isValidLocation) {
                            // The following will create a marker as a side-effect
                            createLineBreakpoint(sourceFile, lineNumber, null, false);
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
        });
    }

    private IFile getSourceFile(IEditorInput editorInput) {
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
        return origSrcFile;
    }

    private static Node getNode(Tree.CompilationUnit rootNode,
            final IRegion lineInformation) {
        class BreakpointVisitor extends Visitor {
            Node result;
            int start = lineInformation.getOffset();
            int end = start + lineInformation.getLength()+1;
            boolean in(Node node) {
                Integer startIndex = node.getStartIndex();
                Integer stopIndex = node.getStopIndex();
                if (startIndex != null && stopIndex != null) {
                    stopIndex ++;
                    return startIndex<=start && stopIndex>=end ||
                            startIndex>=start && startIndex<end ||
                            stopIndex>=start && stopIndex<end;
                }
                return false;
            }
            @Override
            public void visit(Tree.Annotation that) {}
//            @Override
//            public void visit(Tree.MethodDefinition that) {
//                if (in(that.getIdentifier())) {
//                    result = that;
//                }
//                super.visit(that);
//            }
//            @Override
//            public void visit(Tree.ClassDefinition that) {
//                if (in(that.getIdentifier())) {
//                    result = that;
//                }
//                super.visit(that);
//            }
//            @Override
//            public void visit(Tree.Constructor that) {
//                if (in(that.getIdentifier())) {
//                    result = that;
//                }
//                super.visit(that);
//            }
//            @Override
//            public void visit(Tree.AttributeGetterDefinition that) {
//                if (in(that.getIdentifier())) {
//                    result = that;
//                }
//                super.visit(that);
//            }
//            @Override
//            public void visit(Tree.AttributeSetterDefinition that) {
//                if (in(that.getIdentifier())) {
//                    result = that;
//                }
//                super.visit(that);
//            }
            @Override
            public void visit(Tree.ExecutableStatement that) {
                if (in(that)) {
                    result = that;
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.SpecifierOrInitializerExpression that) {
                if (in(that)) {
                    result = that;
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.Expression that) {
                if (in(that)) {
                    result = that;
                }
                super.visit(that);
            }
        };
        BreakpointVisitor visitor = new BreakpointVisitor();
        visitor.visit(rootNode);
        return visitor.result;
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

    public IJavaStratumLineBreakpoint createLineBreakpoint(IFile file, int lineNumber, Map<String,Object> attributes, boolean isForRunToLine) throws CoreException {
        String srcFileName = file.getName();
        String relativePath = null;
        if (ExternalSourceArchiveManager.isInSourceArchive(file)) {
            relativePath = file.getProjectRelativePath().removeFirstSegments(1).toString();
        } else {
            IResourceAware unit = CeylonBuilder.getUnit(file);
            if (unit != null) {
                relativePath = ((Unit)unit).getRelativePath();
            }
        }
        
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        try {
            return JDIDebugModel.createStratumBreakpoint(isForRunToLine ? getWorkspace().getRoot() : file, null, srcFileName,
                    relativePath, null, lineNumber, -1, -1, 0, !isForRunToLine, attributes);
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearLineBreakpoint(IFile file, int lineNumber) throws CoreException {
        try {
            IBreakpoint lineBkpt = findStratumBreakpoint(file, lineNumber);
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
            IBreakpoint lineBkpt = findStratumBreakpoint(file, lineNumber);
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
            IBreakpoint lineBkpt = findStratumBreakpoint(file, lineNumber);
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
        String modelId = JDT_DEBUG_PLUGIN_ID;
        String markerType = "org.eclipse.jdt.debug.javaStratumLineBreakpointMarker";
        IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
        IBreakpoint[] breakpoints = manager.getBreakpoints(modelId);
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

    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.actions.IRunToLineTarget#runToLine(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection, org.eclipse.debug.core.model.ISuspendResume)
     */
    public void runToLine(IWorkbenchPart part, ISelection selection, final ISuspendResume target) throws CoreException {
        doOnBreakpoints(part, selection, new BreakpointAction() {
            @Override
            public void doIt(final ITextSelection textSelection, final Node node,
                    final boolean isValidLocation, final int lineNumber, final IFile sourceFile) throws CoreException {
                String errorMessage;  //$NON-NLS-1$
                if (isValidLocation) {
                    IBreakpoint breakpoint= null;
                    Map<String, Object> attributes = new HashMap<String, Object>(4);
                    BreakpointUtils.addRunToLineAttributes(attributes);
                    breakpoint = createLineBreakpoint(sourceFile, lineNumber, attributes, true);

                    errorMessage = "Unable to locate debug target";  //$NON-NLS-1$
                    if (target instanceof IAdaptable) {
                        IDebugTarget debugTarget = (IDebugTarget) ((IAdaptable)target).getAdapter(IDebugTarget.class);
                        if (debugTarget != null) {
                            RunToLineHandler handler = new RunToLineHandler(debugTarget, target, breakpoint);
                            handler.run(new NullProgressMonitor());
                            return;
                        }
                    }
                } else {
                    // invalid line
                    if (textSelection.getLength() > 0) {
                        errorMessage = "Selected line is not a valid location to run to";  //$NON-NLS-1$
                    } else {
                        errorMessage = "Cursor position is not a valid location to run to";  //$NON-NLS-1$
                    }
                }
                throw new CoreException(new Status(IStatus.ERROR, JDIDebugUIPlugin.getUniqueIdentifier(), IJavaDebugUIConstants.INTERNAL_ERROR,
                        errorMessage, null));
            }
        });
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.actions.IRunToLineTarget#canRunToLine(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection, org.eclipse.debug.core.model.ISuspendResume)
     */
    public boolean canRunToLine(IWorkbenchPart part, ISelection selection, ISuspendResume target) {
        if (target instanceof IDebugElement && target.canResume()) {
            IDebugElement element = (IDebugElement) target;
            IJavaDebugTarget adapter = (IJavaDebugTarget) element.getDebugTarget().getAdapter(IJavaDebugTarget.class);
            return adapter != null;
        }
        return false;
    }
}
