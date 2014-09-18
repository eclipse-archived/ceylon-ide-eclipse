package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER;

import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.redhat.ceylon.compiler.java.launcher.Main;
import com.redhat.ceylon.compiler.java.launcher.Main.ExitState;

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
                if (CeylonBuilder.isCeylon(file)){
                    try {
                        for (IMarker m: file.findMarkers(PROBLEM_MARKER_ID, true, DEPTH_ZERO)) {
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
                        for (IMarker m: file.findMarkers(JAVA_MODEL_PROBLEM_MARKER, false, DEPTH_ZERO)) {
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
                marker.setAttribute(IMarker.CHAR_START, 
                        (int) diagnostic.getStartPosition());
                marker.setAttribute(IMarker.CHAR_END, 
                        (int) diagnostic.getEndPosition());
            }
            if (markerId.equals(JAVA_MODEL_PROBLEM_MARKER)) {
                marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
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