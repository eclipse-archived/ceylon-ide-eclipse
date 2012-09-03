package com.redhat.ceylon.eclipse.core.builder;

import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.redhat.ceylon.compiler.java.codegen.CeylonFileObject;

final class CompileErrorReporter implements
		DiagnosticListener<JavaFileObject> {
	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		JavaFileObject source = diagnostic.getSource();
		if (source instanceof CeylonFileObject) {
			IFile file = getWorkspace().getRoot()
					.getFileForLocation(new Path(source.getName()));
			try {
				for (IMarker m: file.findMarkers(CeylonBuilder.PROBLEM_MARKER_ID, false, DEPTH_ZERO)) {
					if (((Integer)m.getAttribute(IMarker.SEVERITY)).intValue()==IMarker.SEVERITY_ERROR) {
						return;
					}
				}
				IMarker marker = file.createMarker(CeylonBuilder.PROBLEM_MARKER_ID+".backend");
				long line = diagnostic.getLineNumber();
				if (line>=0) {
					//Javac doesn't have line number 
					//info for certain errors
					marker.setAttribute(IMarker.LINE_NUMBER, (int)line);
					marker.setAttribute(IMarker.CHAR_START, (int)diagnostic.getStartPosition());
					marker.setAttribute(IMarker.CHAR_END, (int)diagnostic.getEndPosition());
				}
				marker.setAttribute(IMarker.MESSAGE, diagnostic.getMessage(Locale.getDefault()));
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			} 
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}