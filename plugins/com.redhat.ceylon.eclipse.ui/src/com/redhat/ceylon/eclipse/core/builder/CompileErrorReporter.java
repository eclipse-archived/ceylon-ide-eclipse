package com.redhat.ceylon.eclipse.core.builder;

import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

final class CompileErrorReporter implements
        DiagnosticListener<JavaFileObject> {
    
    private IProject project;
    private boolean errorReported;

    public CompileErrorReporter(IProject project) {
        this.project = project;
    }
    
    public void failed() {
        if (!errorReported) {
            setupMarker(project, null);
        }
    }

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        errorReported = true;
        JavaFileObject source = diagnostic.getSource();
        if (source == null) {
            // no source file
            if (!diagnostic.toString().startsWith("Note: Created module")) {
                setupMarker(project, diagnostic);
            }
        } 
        else {
            IFile file = getWorkspace().getRoot()
                    .getFileForLocation(new Path(source.getName()));
            if(file != null) {
                if (CeylonBuilder.isCeylon(file)){
                    try {
                        for (IMarker m: file.findMarkers(CeylonBuilder.PROBLEM_MARKER_ID, false, DEPTH_ZERO)) {
                            int sev = ((Integer) m.getAttribute(IMarker.SEVERITY)).intValue();
                            if (sev==IMarker.SEVERITY_ERROR) {
                                return;
                            }
                        }
                    } 
                    catch (CoreException e) {
                        e.printStackTrace();
                    }
                    setupMarker(file, diagnostic);
                }
            }else{
                setupMarker(project, diagnostic);
            }
        }
    }

    private void setupMarker(IResource r, Diagnostic<? extends JavaFileObject> diagnostic) {
        try {
            IMarker marker = r.createMarker(CeylonBuilder.PROBLEM_MARKER_ID+".backend");
            long line = diagnostic==null ? -1 : diagnostic.getLineNumber();
            if (line>=0) {
                //Javac doesn't have line number info for certain errors
                marker.setAttribute(IMarker.LINE_NUMBER, (int) line);
                marker.setAttribute(IMarker.CHAR_START, (int) diagnostic.getStartPosition());
                marker.setAttribute(IMarker.CHAR_END, (int) diagnostic.getEndPosition());
            }
            String message = diagnostic==null ? 
                    "unexplained compilation problem" : 
                    diagnostic.getMessage(Locale.getDefault());
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            switch (diagnostic==null ? Diagnostic.Kind.ERROR : diagnostic.getKind()) {
            case ERROR:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                break;
            case WARNING:
            case MANDATORY_WARNING:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                break;
            default:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
            }
        }
        catch (CoreException ce) {
            ce.printStackTrace();
        }
    }
}