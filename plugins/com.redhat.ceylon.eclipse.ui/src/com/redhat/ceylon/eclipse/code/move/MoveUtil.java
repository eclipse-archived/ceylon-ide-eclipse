package com.redhat.ceylon.eclipse.code.move;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getFile;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelectedNode;
import static com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler.imports;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.wizard.NewUnitWizard;

public class MoveUtil {

    public static void moveDeclaration(CeylonEditor editor) 
            throws ExecutionException {
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu!=null) {
            Node node = getSelectedNode(editor);
            if (node instanceof Tree.Declaration) {
                try {
                    moveDeclaration(editor, cu, node);
                } 
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void moveDeclaration(CeylonEditor editor,
            Tree.CompilationUnit cu, Node node) 
                    throws BadLocationException,
                           ExecutionException {
        IDocument document = editor.getDocumentProvider()
                .getDocument(editor.getEditorInput());
        int start = node.getStartIndex();
        int length = node.getStopIndex()-start+1;
        String contents = document.get(start, length);
        String imports = imports(node, cu.getImportList(), document);
        final String text = imports==null ? 
                    contents : 
                    imports + getDefaultLineDelimiter(document) + contents;
        boolean success = NewUnitWizard.open(text, 
                getFile(editor.getEditorInput()), 
                ((Tree.Declaration) node).getIdentifier().getText(), 
                "Move to New Unit", 
                "Create a new Ceylon compilation unit containing the selected declaration.");
        if (success) {
            TextChange tc = createChange(editor, document);
            tc.setEdit(new DeleteEdit(start, length));
            tc.initializeValidationData(null);
            TextChangeOperation.runOperation(editor, new TextChangeOperation(tc));
        }
    }

    private static TextChange createChange(CeylonEditor editor,
            IDocument document) {
        final TextChange tc;
        if (editor.isDirty()) {
            tc = new DocumentChange("Move to New Unit", 
                    document);
        }
        else {
            tc = new TextFileChange("Move to New Unit", 
                    getFile(editor.getEditorInput()));
        }
        return tc;
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
