package com.redhat.ceylon.eclipse.imp.refine;

import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.wizard.NewUnitWizard;

public class MoveDeclarationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu==null) return null;
        Node node = getSelectedNode(editor);
        if (node instanceof Tree.Declaration) {
            String contents;
            try {
                IDocument document = editor.getDocumentProvider()
                        .getDocument(editor.getEditorInput());
                final TextChange tc;
                if (editor.isDirty()) {
                    tc = new DocumentChange("Move to New Unit", document);
                }
                else {
                    tc = new TextFileChange("Move to New Unit", 
                            Util.getFile(editor.getEditorInput()));
                }
                int start = node.getStartIndex();
                int length = node.getStopIndex()-start+1;
                contents = document.get(start, length);
                tc.setEdit(new DeleteEdit(start, length));
                NewUnitWizard.open(contents, Util.getFile(editor.getEditorInput()), 
                        ((Tree.Declaration) node).getIdentifier().getText(), "Move to New Unit", 
                        "Create a new Ceylon compilation unit containing the selected declaration.");
                tc.initializeValidationData(null);
                AbstractOperation op = new TextChangeOperation(tc);
				IWorkbenchOperationSupport os = getWorkbench().getOperationSupport();
				op.addContext(os.getUndoContext());
	            os.getOperationHistory().execute(op, new NullProgressMonitor(), 
                        		getUIInfoAdapter(editor.getSite().getShell()));
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }        
		return null;
	}

    //TODO: copy/pasted from AbstractFindAction
    private static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        return cpc.getRootNode()==null ? null : 
            findNode(cpc.getRootNode(), 
                (ITextSelection) editor.getSelectionProvider().getSelection());
    }

    //TODO: copy/pasted from RefineFormalMembersHandler
    @Override
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && 
                editor instanceof CeylonEditor &&
                editor.getEditorInput() instanceof IFileEditorInput) {
            Node node = getSelectedNode((CeylonEditor) editor);
            return node instanceof Tree.Declaration &&
                    ((Tree.Declaration) node).getDeclarationModel().isToplevel();
        }
        else {
            return false;
        }
    }
}
