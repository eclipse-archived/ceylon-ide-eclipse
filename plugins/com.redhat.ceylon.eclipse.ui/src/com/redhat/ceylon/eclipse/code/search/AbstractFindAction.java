package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.editor.Util.getProject;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.imageRegistry;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedExplicitDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_DECS;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_REFS;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

abstract class AbstractFindAction extends Action implements IObjectActionDelegate {
    
    public static ImageDescriptor REFS = imageRegistry.getDescriptor(CEYLON_REFS);
    public static ImageDescriptor DECS = imageRegistry.getDescriptor(CEYLON_DECS);
    
    private IWorkbenchPartSite site;
    protected Declaration declaration;
    protected IProject project;
    private ContentOutline outlineView;
    
    AbstractFindAction() {}
    
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (outlineView==null) return;
		try {
			CeylonOutlineNode on = (CeylonOutlineNode) ((ITreeSelection) outlineView.getSelection()).getFirstElement();
			if (on!=null) {
			    IEditorPart currentEditor = getCurrentEditor();
			    if (currentEditor instanceof CeylonEditor) {
			        CompilationUnit rootNode = ((CeylonEditor) currentEditor).getParseController().getRootNode();
			        if (rootNode!=null) {
			            Node node = findNode(rootNode, on.getStartOffset());
			            if (node instanceof Tree.Declaration) {
			                declaration = ((Tree.Declaration) node).getDeclarationModel();
			                project =  getProject(currentEditor);
			                action.setEnabled(isValidSelection());
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
		site = targetPart.getSite();
	}
    
    AbstractFindAction(String text, IEditorPart editor) {
        super(text);
        this.site = editor.getSite();
        project = editor==null ? null : getProject(editor);
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
			declaration = getReferencedExplicitDeclaration(getSelectedNode(ce), 
					ce.getParseController().getRootNode());
            setEnabled(isValidSelection());
        }
        else {
            setEnabled(false);
        }
    }
    
    AbstractFindAction(String text, IEditorPart editor, Declaration dec) {
        super(text);
        this.site = editor.getSite();
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
            MessageDialog.openWarning(site.getShell(), 
                    "Ceylon Find Error", 
                    "No appropriate declaration name selected");
        }
    }
    
    private static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        return cpc.getRootNode()==null ? null : 
            findNode(cpc.getRootNode(), 
                (ITextSelection) editor.getSelectionProvider().getSelection());
    }

    abstract boolean isValidSelection();

    public abstract FindSearchQuery createSearchQuery();
    
}
