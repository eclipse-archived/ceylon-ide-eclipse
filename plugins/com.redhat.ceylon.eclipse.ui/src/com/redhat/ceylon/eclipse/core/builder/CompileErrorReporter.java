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
import org.eclipse.jdt.core.IJavaModelMarker;

import com.redhat.ceylon.compiler.java.launcher.Main;
import com.redhat.ceylon.compiler.java.launcher.Main.ExitState;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

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

    public void failed(final ExitState exitState) {
        Diagnostic<? extends JavaFileObject> diagnostic = null;
        if (exitState.javacExitCode == Main.EXIT_ABNORMAL) {
            diagnostic = new Diagnostic<JavaFileObject>() {
                @Override
                public javax.tools.Diagnostic.Kind getKind() {
                    return javax.tools.Diagnostic.Kind.ERROR;
                }
                @Override
                public JavaFileObject getSource() {
                    return null;
                }
                @Override
                public long getPosition() {
                    return 0;
                }
                @Override
                public long getStartPosition() {
                    return 0;
                }
                @Override
                public long getEndPosition() {
                    return 0;
                }
                @Override
                public long getLineNumber() {
                    return 0;
                }
                @Override
                public long getColumnNumber() {
                    return 0;
                }
                @Override
                public String getCode() {
                    return null;
                }
                @Override
                public String getMessage(Locale locale) {
                    return "The Ceylon Java backend compiler failed abormally" + 
                            (exitState.ceylonCodegenExceptionCount > 0 ? "\n  with " + exitState.ceylonCodegenExceptionCount + " code generation exceptions" : "") +
                            (exitState.ceylonCodegenErroneousCount > 0 ? "\n  with " + exitState.ceylonCodegenErroneousCount + " erroneous code generations" : "") +
                            (exitState.ceylonCodegenGarbageCount > 0 ? "\n  with " + exitState.ceylonCodegenGarbageCount + " malformed Javac tree cases" : "") +
                            (exitState.abortingException != null ? "\n  with a throwable : " + exitState.abortingException.toString() : "") +
                            "";
                }
            };
        }
        if (!errorReported || diagnostic != null) {
            setupMarker(project, diagnostic);
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
                        for (IMarker m: file.findMarkers(CeylonBuilder.PROBLEM_MARKER_ID, true, DEPTH_ZERO)) {
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
                if (CeylonBuilder.isJava(file)){
                    try {
                        for (IMarker m: file.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, DEPTH_ZERO)) {
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
            String markerId = CeylonBuilder.PROBLEM_MARKER_ID+".backend";
            if (r instanceof IFile) {
                if (CeylonBuilder.isJava((IFile)r)) {
                    markerId = IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER;
                }
            }
            IMarker marker = r.createMarker(markerId);
            long line = diagnostic==null ? -1 : diagnostic.getLineNumber();
            if (line>=0) {
                //Javac doesn't have line number info for certain errors
                marker.setAttribute(IMarker.LINE_NUMBER, (int) line);
                marker.setAttribute(IMarker.CHAR_START, (int) diagnostic.getStartPosition());
                marker.setAttribute(IMarker.CHAR_END, (int) diagnostic.getEndPosition());
            }
            if (markerId.equals(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER)) {
                marker.setAttribute(IMarker.SOURCE_ID, CeylonPlugin.PLUGIN_ID);
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