package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler;
import com.redhat.ceylon.eclipse.code.wizard.NewUnitWizard;

public class MoveDeclarationHandler extends AbstractHandler {
    
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        moveDeclaration((CeylonEditor) getCurrentEditor());        
		return null;
	}

    public static void moveDeclaration(CeylonEditor editor) throws ExecutionException {
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu==null) return;
        Node node = getSelectedNode(editor);
        if (node instanceof Tree.Declaration) {
            try {
                IDocument document = editor.getDocumentProvider()
                        .getDocument(editor.getEditorInput());
                int start = node.getStartIndex();
                int length = node.getStopIndex()-start+1;
                String contents = document.get(start, length);
                String imports = CleanImportsHandler.imports(node, cu.getImportList());
                boolean success = NewUnitWizard.open(imports==null ? 
                            contents : imports + "\n" + contents, 
                        Util.getFile(editor.getEditorInput()), 
                        ((Tree.Declaration) node).getIdentifier().getText(), "Move to New Unit", 
                        "Create a new Ceylon compilation unit containing the selected declaration.");
                if (success) {
                    final TextChange tc;
                    if (editor.isDirty()) {
                        tc = new DocumentChange("Move to New Unit", document);
                    }
                    else {
                        tc = new TextFileChange("Move to New Unit", 
                                Util.getFile(editor.getEditorInput()));
                    }
                    tc.setEdit(new DeleteEdit(start, length));
                    tc.initializeValidationData(null);
                    AbstractOperation op = new TextChangeOperation(tc);
    				IWorkbenchOperationSupport os = getWorkbench().getOperationSupport();
    				op.addContext(os.getUndoContext());
    	            os.getOperationHistory().execute(op, new NullProgressMonitor(), 
                            		getUIInfoAdapter(editor.getSite().getShell()));
                }
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected boolean isEnabled(CeylonEditor editor) {
        return canMoveDeclaration(editor);
    }

    public static boolean canMoveDeclaration(CeylonEditor editor) {
        Node node = getSelectedNode(editor);
        if (node instanceof Tree.Declaration) {
        	Declaration d = ((Tree.Declaration) node).getDeclarationModel();
        	return d!=null && d.isToplevel();
        }
        else {
        	return false;
        }
    }
}
