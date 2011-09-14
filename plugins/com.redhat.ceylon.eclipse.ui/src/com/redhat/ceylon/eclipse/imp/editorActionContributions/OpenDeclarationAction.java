package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getCompilationUnit;
import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedNode;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.editor.DeclarationWithProject;
import com.redhat.ceylon.eclipse.imp.editor.FilteredTypesSelectionDialog;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.ICeylonResources;
import com.redhat.ceylon.eclipse.util.Util;

class OpenDeclarationAction extends Action {
    private final IEditorPart editor;
    
    OpenDeclarationAction(IEditorPart editor) {
        this("Open Declaration...", editor);
    }
    
    OpenDeclarationAction(String text, IEditorPart editor) {
        super(text);
        this.editor = editor;
        setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry()
                .getDescriptor(ICeylonResources.CEYLON_OPEN_DECLARATION));
    }
    
    @Override
    public void run() {
        Shell shell = CeylonPlugin.getInstance().getWorkbench()
                .getActiveWorkbenchWindow().getShell();
        FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(shell, editor);
        dialog.setTitle("Open Declaration");
        if (editor instanceof ITextEditor) {
            dialog.setInitialPattern(Util.getSelectionText((ITextEditor) editor));
        }
        dialog.open();
        Object[] types = dialog.getResult();
        if (types != null && types.length > 0) {
            gotoDeclaration((DeclarationWithProject) types[0]);
        }
    }

    public void gotoDeclaration(DeclarationWithProject dwp) {
        Tree.Declaration node;
        PhasedUnits units;
        IProject project = dwp.getProject();
        Declaration dec = dwp.getDeclaration();
        if (editor instanceof UniversalEditor) {
            CeylonParseController cpc = (CeylonParseController) ((UniversalEditor) editor).getParseController();
            node = getReferencedNode(dec, getCompilationUnit(cpc, dec));
            units = cpc.getPhasedUnits();
        }
        else {
            node = getReferencedNode(dec, getCompilationUnit(project, dec));
            units = CeylonBuilder.getProjectTypeChecker(project).getPhasedUnits();
        }
        IPath path = CeylonSourcePositionLocator.getNodePath(node, units)
                .removeFirstSegments(1);
        int targetOffset = CeylonSourcePositionLocator.getNodeStartOffset(node);
        IResource file = project.findMember(path);
        Util.gotoLocation(file, targetOffset, 0);
    }

}