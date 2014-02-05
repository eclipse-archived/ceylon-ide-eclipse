package com.redhat.ceylon.eclipse.code.editor;

import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class Util {
    
    public static IProject getProject(IEditorInput input) {
        if (input instanceof IFileEditorInput) {
            return ((IFileEditorInput) input).getFile().getProject();
        }
        else {
            return null;
        }
    }
    
    public static IProject getProject(IEditorPart editor) {
        IProject project = getProject(editor.getEditorInput());
        /*if (project==null && editor instanceof CeylonEditor) {
            TypeChecker tc = ((CeylonEditor) editor).getParseController().getTypeChecker();
            for (IProject p: CeylonBuilder.getProjects()) {
                TypeChecker ptc = CeylonBuilder.getProjectTypeChecker(p);
                if (ptc==tc) {
                    return p;
                }
            }
        }*/
        return project;
    }

    public static IFile getFile(IEditorInput input) {
        if (input instanceof IFileEditorInput) {
            return ((IFileEditorInput) input).getFile();
        }
        else {
            return null;
        }
    }

    public static ITextSelection getSelection(ITextEditor textEditor) {
        ISelectionProvider sp = textEditor.getSelectionProvider();
		return sp==null ? null : (ITextSelection) sp.getSelection();
    }
    
    public static String getSelectionText(ITextEditor textEditor) {
        ITextSelection sel = getSelection(textEditor);
        IDocument document = textEditor.getDocumentProvider()
                .getDocument(textEditor.getEditorInput());
        try {
            return document.get(sel.getOffset(), sel.getLength());
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void gotoLocation(final IResource file, final int offset) {
        gotoLocation(file, offset, 0);
    }
    
    public static void gotoLocation(final IResource file, final int offset, int length) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(IMarker.CHAR_START, offset);
        map.put(IMarker.CHAR_END, offset+length);
        if (file instanceof IFile && CeylonBuilder.isCeylon((IFile) file)) {
            map.put(IDE.EDITOR_ID_ATTR, CeylonPlugin.EDITOR_ID);
        }
        try {
            IMarker marker = file.createMarker(IMarker.TEXT);
            marker.setAttributes(map);
            IDE.openEditor(getActivePage(), marker);
            marker.delete();
        }
        catch (CoreException ce) {} //deliberately swallow it
        /*try {
            IEditorPart editor = EditorUtility.isOpenInEditor(path);
            if (editor == null) {
                editor = EditorUtility.openInEditor(path);
            }
            EditorUtility.revealInEditor(editor, targetOffset, 0);
        }
        catch (PartInitException e) {
            e.printStackTrace();
        }*/
    }
    
    public static IEditorPart getCurrentEditor() {
        IWorkbenchPage page = getActivePage();
        return page==null ? null : page.getActiveEditor();
    }

    public static IWorkbenchPage getActivePage() {
        try {
            IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
            return window==null ? null : window.getActivePage();
        }
        catch (IllegalStateException ise) {
            return null;
        }
    }
    
    public static Shell getShell() {
    	return getWorkbench().getActiveWorkbenchWindow().getShell();
    }

	public static IPreferenceStore getPreferences() {
	    try {
	        return EditorsUI.getPreferenceStore();
	    }
	    catch (Exception e) {
	        return null;
	    }
	}
}
