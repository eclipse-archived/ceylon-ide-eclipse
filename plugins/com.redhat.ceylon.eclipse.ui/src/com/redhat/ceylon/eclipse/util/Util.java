package com.redhat.ceylon.eclipse.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class Util {
    
    public static int getLevenshteinDistance(String x, String y) {
              
        int n = x.length(); // length of s
        int m = y.length(); // length of t
              
        if (n == 0) return m;
        if (m == 0) return n;

        int p[] = new int[n+1]; //'previous' cost array, horizontally
        int d[] = new int[n+1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i<=n; i++) {
           p[i] = i;
        }
              
        for (j = 1; j<=m; j++) {
           t_j = y.charAt(j-1);
           d[0] = j;
              
           for (i=1; i<=n; i++) {
              cost = x.charAt(i-1)==t_j ? 0 : 1;
              // minimum of cell to the left+1, to the top+1, diagonally left and up +cost                
              d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);  
           }

           // copy current distance counts to 'previous row' distance counts
           _d = p;
           p = d;
           d = _d;
        } 
              
        // our last action in the above loop was to switch d and p, so p now 
        // actually has the most recent cost counts
        return p[n];
    }
    
    public static IProject getProject(IEditorInput editor) {
        if (editor instanceof IFileEditorInput) {
            return ((IFileEditorInput) editor).getFile().getProject();
        }
        else {
            return null;
        }
    }

    public static IFile getFile(IEditorInput editor) {
        if (editor instanceof IFileEditorInput) {
            return ((IFileEditorInput) editor).getFile();
        }
        else {
            return null;
        }
    }

    public static Point getSelection(ITextEditor textEditor) {
        ISelection sel= textEditor.getSelectionProvider().getSelection();
        ITextSelection textSel= (ITextSelection) sel;
        return new Point(textSel.getOffset(), textSel.getLength());
    }
    
    public static String getSelectionText(ITextEditor textEditor) {
        Point sel= getSelection(textEditor);
        IFileEditorInput fileEditorInput= (IFileEditorInput) textEditor.getEditorInput();
        IDocument document= textEditor.getDocumentProvider().getDocument(fileEditorInput);
        try {
            return document.get(sel.x, sel.y);
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
        IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(IMarker.CHAR_START, offset);
        map.put(IMarker.CHAR_END, offset+length);
        map.put(IDE.EDITOR_ID_ATTR, "org.eclipse.imp.runtime.impEditor");
        try {
            IMarker marker = file.createMarker(IMarker.TEXT);
            marker.setAttributes(map);
            IDE.openEditor(page, marker);
            marker.delete();
        }
        catch (CoreException ce) {} //deliberately swallow it
        /*try {
            IEditorPart editor = EditorUtility.isOpenInEditor(path);
            if (editor == null) {
                editor = EditorUtility.openInEditor(path);
            }
            EditorUtility.revealInEditor(editor, targetOffset, 0);
        } catch (PartInitException e) {
            RuntimePlugin.getInstance().logException("Unable to open declaration", e);
        }*/
    }
    
}
