package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.editor.FilteredTypesSelectionDialog;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.quickfix.QuickFixAssistant;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.ICeylonResources;

class OpenDeclarationAction extends Action {
    private final UniversalEditor editor;
    
    OpenDeclarationAction(UniversalEditor editor) {
        this("Open Declaration...", editor);
    }
    
    OpenDeclarationAction(String text, UniversalEditor editor) {
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
        dialog.setInitialPattern(editor.getSelectionText());
        dialog.open();
        Object[] types = dialog.getResult();
        if (types != null && types.length > 0) {
            CeylonParseController cpc = (CeylonParseController) editor.getParseController();
            Tree.Declaration node = CeylonReferenceResolver.getDeclarationNode(cpc, 
                    (Declaration) types[0]);
            ISourcePositionLocator locator = cpc.getSourcePositionLocator();
            IPath path = locator.getPath(node).removeFirstSegments(1);
            int targetOffset = locator.getStartOffset(node);
            IResource file = cpc.getProject().getRawProject().findMember(path);
            QuickFixAssistant.gotoChange(file, targetOffset, 0);
        }
    }
}