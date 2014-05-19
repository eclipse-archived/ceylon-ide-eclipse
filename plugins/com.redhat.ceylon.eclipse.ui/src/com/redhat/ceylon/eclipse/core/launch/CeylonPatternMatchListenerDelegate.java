package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;
import static java.lang.Integer.parseInt;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.FileEditorInput;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class CeylonPatternMatchListenerDelegate implements
        IPatternMatchListenerDelegate {

    private TextConsole console;
    
    @Override
    public void connect(TextConsole console) {
        this.console = console;
    }

    @Override
    public void disconnect() {
        console = null; 
    }

    @Override
    public void matchFound(PatternMatchEvent event) {
        try {
            String text = console.getDocument()
                    .get(event.getOffset()+3, event.getLength()-4);
            int j = text.indexOf("(");
            int i = text.indexOf(":", j);
            final String[] elems = text.substring(0,j).split("\\.");
            final String file = text.substring(j+1, i);
            final String line = text.substring(i+1);
            console.addHyperlink(new IHyperlink() {
                @Override
                public void linkExited() {}
                @Override
                public void linkEntered() {}
                
                @Override
                public void linkActivated() {
                    gotoFileAndLine(file, line, elems);
                }
            }, event.getOffset()+4+j, event.getLength()-5-j);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public static void gotoFileAndLine(String fileName, String line, String[] elems) {
        IPath path = new Path(fileName);
        IWorkspaceRoot root = getWorkspace().getRoot();
        IFile file = null;
        //TODO: 1. only look in Ceylon projects
        //      2. start bottom up looking for
        //         more-specific packages first
        for (IProject p: root.getProjects()) {
            try {
                if(!p.isAccessible())
                    continue;
                for (IPackageFragmentRoot pfr: JavaCore.create(p)
                        .getAllPackageFragmentRoots()) {
                    //if (pfr.getKind()==IPackageFragmentRoot.K_SOURCE))
                    IFolder folder = root.getFolder(pfr.getPath());
                    for (String elem: elems) {
                        if (folder.exists(path)) {
                            file = folder.getFile(path);
                            break;
                        }
                        folder=folder.getFolder(elem);
                        if (!folder.exists()) break;
                    }
                }
            } 
            catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        if( file != null ) {
            IEditorInput input = new FileEditorInput(file);
            IWorkbenchPage activePage = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
            try {
                CeylonEditor editor = (CeylonEditor) activePage.openEditor(input, EDITOR_ID, true);
                IRegion li = editor.getCeylonSourceViewer().getDocument()
                        .getLineInformation(parseInt(line)-1);
                editor.selectAndReveal(li.getOffset(), li.getLength());
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }    

}
