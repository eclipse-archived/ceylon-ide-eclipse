package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnit;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getProject;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedExplicitDeclaration;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;

abstract class AbstractFindAction extends Action implements IObjectActionDelegate {
    
    private Shell shell;
    protected Referenceable declaration;
    protected IProject project;
    private ContentOutline outlineView;
    
    AbstractFindAction(String name) {
        super(name);
    }
    
    AbstractFindAction(String name, CeylonSearchResultPage page, ISelection selection) {
        super(name);
        shell = page.getSite().getShell();
        Object firstElement = ((IStructuredSelection) selection).getFirstElement();
        if (firstElement instanceof CeylonElement) {
            CeylonElement element = ((CeylonElement) firstElement);
            if (element.getVirtualFile() != null) {
                CeylonUnit unit = getUnit(element.getVirtualFile());
                Tree.CompilationUnit rn = unit.getCompilationUnit();
                Node node = findNode(rn, element.getStartOffset(), 
                        element.getEndOffset());
                if (node instanceof Tree.Declaration) {
                    declaration = ((Tree.Declaration) node).getDeclarationModel();
                }
            }
        }
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (outlineView==null) return;
        try {
            CeylonOutlineNode on = (CeylonOutlineNode) 
                    ((ITreeSelection) outlineView.getSelection()).getFirstElement();
            if (on!=null) {
                IEditorPart currentEditor = getCurrentEditor();
                if (currentEditor instanceof CeylonEditor) {
                    CeylonEditor ce = (CeylonEditor) currentEditor;
                    Tree.CompilationUnit rootNode = 
                            ce.getParseController().getRootNode();
                    if (rootNode!=null) {
                        Node node = findNode(rootNode, on.getStartOffset());
                        if (node instanceof Tree.Declaration) {
                            declaration = ((Tree.Declaration) node).getDeclarationModel();
                            project = getProject(currentEditor);
                            action.setEnabled(isValidSelection());
                            return; //early exit
                        }
                        else if (node instanceof Tree.ImportPath) {
                            declaration = ((Tree.ImportPath) node).getModel();
                            project = getProject(currentEditor);
                            action.setEnabled(isValidSelection());
                            return; //early exit
                        }
                    }
                }
            }
            action.setEnabled(false);
        }
        catch (Exception e) {
            action.setEnabled(false);
        }
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        outlineView = (ContentOutline) targetPart;
        shell = targetPart.getSite().getShell();
    }
    
    AbstractFindAction(String text, IEditorPart editor) {
        super(text);
        shell = editor.getSite().getShell();
        project = editor==null ? null : getProject(editor);
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            declaration = 
                    getReferencedExplicitDeclaration(ce.getSelectedNode(), 
                            ce.getParseController().getRootNode());
            setEnabled(isValidSelection());
        }
        else {
            setEnabled(false);
        }
    }
    
    AbstractFindAction(String text, IEditorPart editor, Declaration dec) {
        super(text);
        shell = editor.getSite().getShell();
        project = editor==null ? null : getProject(editor);
        declaration = dec;
        setEnabled(true);
    }
    
    @Override
    public void run(IAction action) {
        run();
    }
    
    @Override
    public void run() {
        if (isValidSelection()) {
            NewSearchUI.runQueryInBackground(createSearchQuery());
        }
        else {
            MessageDialog.openWarning(shell, "Ceylon Find Error", 
                    "No appropriate declaration name selected");
        }
    }

    abstract boolean isValidSelection();

    public abstract FindSearchQuery createSearchQuery();
    
}
