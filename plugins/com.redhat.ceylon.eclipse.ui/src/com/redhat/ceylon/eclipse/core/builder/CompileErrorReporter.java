package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER;

import java.util.List;
import java.util.Locale;

import com.redhat.ceylon.javax.tools.Diagnostic;
import com.redhat.ceylon.javax.tools.DiagnosticListener;
import com.redhat.ceylon.javax.tools.JavaFileObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.redhat.ceylon.compiler.java.launcher.Main.ExitState;
import com.redhat.ceylon.compiler.java.launcher.Main.ExitState.CeylonState;

final class CompileErrorReporter implements
        DiagnosticListener<JavaFileObject> {
    
    private IProject project;
    private boolean errorReported;
    private List<IFolder> sourceDirectories;

    public CompileErrorReporter(IProject project) {
        this.project = project;
        sourceDirectories = CeylonBuilder.getSourceFolders(project);
    }
    
    public void failed() {
        if (!errorReported) {
            setupMarker(project, null);
        }
    }

    public void failed(final ExitState exitState) {
        Diagnostic<? extends JavaFileObject> diagnostic = null;
        if (exitState != null 
                && exitState.ceylonState != null
                && (exitState.ceylonState.equals(CeylonState.BUG)
                        || exitState.ceylonState.equals(CeylonState.SYS))) {
            diagnostic = new Diagnostic<JavaFileObject>() {
                @Override
                public com.redhat.ceylon.javax.tools.Diagnostic.Kind getKind() {
                    return com.redhat.ceylon.javax.tools.Diagnostic.Kind.ERROR;
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
                    return exitState.ceylonState.equals(CeylonState.BUG) ? 
                            "The Ceylon Java backend compilation failed" + 
                            (exitState.ceylonCodegenExceptionCount > 0 ? "\n  with " + exitState.ceylonCodegenExceptionCount + " code generation exceptions" : "") +
                            (exitState.ceylonCodegenErroneousCount > 0 ? "\n  with " + exitState.ceylonCodegenErroneousCount + " erroneous code generations" : "") +
                            (exitState.ceylonCodegenGarbageCount > 0 ? "\n  with " + exitState.ceylonCodegenGarbageCount + " malformed Javac tree cases" : "") +
                            (exitState.abortingException != null ? "\n  with a throwable : " + exitState.abortingException.toString() : "")
                            :
                            "The Ceylon Java backend compilation was aborted" +
                            (exitState.abortingException != null ? "\n  due to the following event : " + exitState.abortingException.toString() : ".");
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
        if (Diagnostic.Kind.NOTE.equals(diagnostic.getKind()) 
                && diagnostic.toString().startsWith("Note: Created module")) {
            return;
        }
        if (source == null) {
            setupMarker(project, diagnostic);
        } 
        else {
            IPath absolutePath = new Path(source.getName());
            IFile file = null;
            for (IFolder sourceDirectory : sourceDirectories) {
                IPath sourceDirPath = sourceDirectory.getLocation();
                if (sourceDirPath.isPrefixOf(absolutePath)) {
                    IResource r = sourceDirectory.findMember(absolutePath.makeRelativeTo(sourceDirPath));
                    if (r instanceof IFile) {
                        file = (IFile) r;
                    }
                }
            }
            if (file == null) {
                file = getWorkspace().getRoot()
                        .getFileForLocation(new Path(source.getName()));
            }
            if(file != null) {
                long diagnosticLineNumber = diagnostic.getLineNumber();
                if (CeylonBuilder.isCeylon(file)){
                    int backendErrorSeverity = kindToSeverity(diagnostic.getKind());
                    try {
                        for (IMarker m: file.findMarkers(PROBLEM_MARKER_ID, true, DEPTH_ZERO)) {
                            if (m.getAttribute(IMarker.LINE_NUMBER, -1) == diagnosticLineNumber) {
                                int existingSeverity = ((Integer) m.getAttribute(IMarker.SEVERITY)).intValue();
                                if (existingSeverity >= backendErrorSeverity) {
                                    return;
                                }
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
                        for (IMarker m: file.findMarkers(JAVA_MODEL_PROBLEM_MARKER, false, DEPTH_ZERO)) {
                            if (m.getAttribute(IMarker.LINE_NUMBER, -1) == diagnosticLineNumber) {
                                int sev = ((Integer) m.getAttribute(IMarker.SEVERITY)).intValue();
                                if (sev==IMarker.SEVERITY_ERROR) {
                                    return;
                                }
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

    private int kindToSeverity(Diagnostic.Kind kind) {
        switch (kind) {
        case ERROR:
            return IMarker.SEVERITY_ERROR;
        case WARNING:
        case MANDATORY_WARNING:
            return IMarker.SEVERITY_WARNING;
        default:
            return IMarker.SEVERITY_INFO;
        }
    }
    
    private void setupMarker(IResource resource, Diagnostic<? extends JavaFileObject> diagnostic) {
        try {
            long line = diagnostic==null ? -1 : diagnostic.getLineNumber();
            String markerId = PROBLEM_MARKER_ID + ".backend";
            if (resource instanceof IFile) {
                if (CeylonBuilder.isJava((IFile)resource)) {
                    markerId = JAVA_MODEL_PROBLEM_MARKER;
                }
//                if (line<0) {
                    //TODO: use the Symbol to get a location for the javac error
//                    String name = ((Symbol)((JCDiagnostic) diagnostic).getArgs()[0]).name.toString();
//                    Declaration member = CeylonBuilder.getPackage((IFile)resource).getDirectMember(name, null, false);
//                }

            }
            IMarker marker = resource.createMarker(markerId);
            if (line>=0) {
                //Javac doesn't have line number info for certain errors
                marker.setAttribute(IMarker.LINE_NUMBER, (int) line);
                long startPosition = diagnostic.getStartPosition();
                long endPosition = diagnostic.getEndPosition() + 1;
                marker.setAttribute(IMarker.CHAR_START, 
                        (int) startPosition);
                marker.setAttribute(IMarker.CHAR_END, 
                        (int) endPosition);
            }
            if (markerId.equals(JAVA_MODEL_PROBLEM_MARKER)) {
                marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
            }
            String message = diagnostic==null ? 
                    "unexplained compilation problem" : 
                    diagnostic.getMessage(Locale.getDefault());
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            int severity = kindToSeverity(diagnostic==null ? Diagnostic.Kind.ERROR : diagnostic.getKind());
            marker.setAttribute(IMarker.SEVERITY, severity);
        }
        catch (CoreException ce) {
            ce.printStackTrace();
        }
    }
}