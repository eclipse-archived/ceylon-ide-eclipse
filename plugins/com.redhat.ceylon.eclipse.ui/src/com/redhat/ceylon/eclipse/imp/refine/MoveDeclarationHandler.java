package com.redhat.ceylon.eclipse.imp.refine;

import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

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
                TextChange tc;
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
                PerformChangeOperation op = new PerformChangeOperation(tc);
                /*op.setUndoManager(RefactoringCore.getUndoManager(), 
                		"Move to New Unit");*/
				ResourcesPlugin.getWorkspace().run(op, new NullProgressMonitor());
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
            catch (CoreException e) {
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
