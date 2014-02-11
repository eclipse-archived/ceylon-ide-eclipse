package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getCompilationUnit;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class OpenTypeAction extends Action {
    private final IEditorPart editor;
    
    public OpenTypeAction(IEditorPart editor) {
        this("Open Ceylon or Java Type...", editor);
    }
    
    public OpenTypeAction(String text, IEditorPart editor) {
        super(text);
        this.editor = editor;
        setActionDefinitionId(PLUGIN_ID + ".action.openType");
        setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry()
                .getDescriptor(CeylonResources.CEYLON_OPEN_DECLARATION));
    }
    
    @Override
    public void run() {
        Shell shell = CeylonPlugin.getInstance().getWorkbench()
                .getActiveWorkbenchWindow().getShell();
        OpenTypeDialog dialog = new OpenTypeDialog(shell, editor);
        dialog.setTitle("Open Ceylon or Java Type");
        dialog.setMessage("Select a Ceylon or Java type to open:");
        if (editor instanceof ITextEditor) {
            dialog.setInitialPattern(EditorUtil.getSelectionText((ITextEditor) editor));
        }
        dialog.open();
        Object[] types = dialog.getResult();
        if (types != null && types.length > 0) {
            gotoDeclaration((DeclarationWithProject) types[0]);
        }
    }

    public void gotoDeclaration(DeclarationWithProject dwp) {
        Declaration dec = dwp.getDeclaration();
        IProject project = dwp.getProject();
        if (project!=null) {
            //TODO: lots of copy/paste from OpenDeclarationAction
            if (editor instanceof CeylonEditor) {
                CeylonEditor ce = (CeylonEditor) editor;
                IProject ep = ce.getParseController().getProject();
                if (ep!=null && ep.equals(project)) {
                    CeylonParseController cpc = ce.getParseController();
                    Tree.Declaration node = (Tree.Declaration) getReferencedNode(dec, 
                            getCompilationUnit(cpc, dec));
                    if (node!=null) {
                        gotoNode(node, project, cpc.getTypeChecker());
                        return;
                    }
                }
            }

            if (dec.getUnit() instanceof CeylonUnit) {
                CeylonUnit ceylonUnit = (CeylonUnit) dec.getUnit();
                Tree.Declaration node = (Tree.Declaration) getReferencedNode(dec, 
                        ceylonUnit.getCompilationUnit());
                if (node!=null) {
                    gotoNode(node, project, getProjectTypeChecker(project));
                } else {
                    if (ceylonUnit instanceof CeylonBinaryUnit) {
                        CeylonBinaryUnit binaryUnit = (CeylonBinaryUnit) ceylonUnit;
                        if (JavaCore.isJavaLikeFileName(binaryUnit.getSourceRelativePath())) {
                            gotoJavaNode(dec, null, project);
                        }
                    }
                }
            }
            else {
                gotoJavaNode(dec, null, project);
            }
        }
        else {
            //it's coming from the "unversioned" JDK module, which
            //we don't display multiple choices for, so just pick
            //the first available project
            gotoJavaNode(dec, null, getProjects().iterator().next());
        }
    }

}