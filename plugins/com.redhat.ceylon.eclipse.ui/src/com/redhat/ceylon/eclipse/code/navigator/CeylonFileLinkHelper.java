package com.redhat.ceylon.eclipse.code.navigator;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.navigator.JavaFileLinkHelper;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.ResourceUtil;

import com.redhat.ceylon.eclipse.code.editor.SourceArchiveEditorInput;
import com.redhat.ceylon.eclipse.core.external.CeylonArchiveFileStore;
import com.redhat.ceylon.eclipse.util.EditorUtil;

@SuppressWarnings("restriction")
public class CeylonFileLinkHelper extends JavaFileLinkHelper {

    @Override
    public void activateEditor(IWorkbenchPage page,
            IStructuredSelection selection) {
        if (selection.getFirstElement() instanceof CeylonArchiveFileStore) {
            IEditorInput input = EditorUtil.getEditorInput(selection.getFirstElement());
            IWorkbenchPage p= JavaPlugin.getActivePage();
            if (p != null) {
                IEditorPart editor = p.findEditor(input);
                if (editor != null) {
                    page.bringToTop(editor);
                }
            }
            return;            
        }
        super.activateEditor(page, selection);
    }

    @Override
    public IStructuredSelection findSelection(IEditorInput input) {
        if (input instanceof SourceArchiveEditorInput) {
            IFile file = ResourceUtil.getFile(input);
            IFileStore store = ((Resource) file).getStore();
            if (store instanceof CeylonArchiveFileStore) {
                return (file != null) ? new StructuredSelection(store) : StructuredSelection.EMPTY;
            }
            return null;
        }
        return super.findSelection(input);
    }

}
