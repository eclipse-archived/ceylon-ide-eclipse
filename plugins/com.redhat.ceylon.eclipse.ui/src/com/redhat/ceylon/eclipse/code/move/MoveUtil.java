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
import com.redhat.ceylon.eclipse.code.wizard.AddToUnitWizard;
import com.redhat.ceylon.eclipse.code.wizard.NewUnitWizard;

public class MoveUtil {

    public static void moveToUnit(CeylonEditor editor) 
            throws ExecutionException {
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu!=null) {
            Node node = getSelectedNode(editor);
            if (node instanceof Tree.Declaration) {
                try {
                    moveToUnit(editor, cu, node);
                } 
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void moveToUnit(CeylonEditor editor,
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
        boolean success = AddToUnitWizard.open(text, 
                getFile(editor.getEditorInput()), 
                "Move to New Unit", 
                "Create a new Ceylon compilation unit containing the selected declaration.");
        if (success) {
            TextChange tc = createChange(editor, document);
            tc.setEdit(new DeleteEdit(start, length));
            tc.initializeValidationData(null);
            new TextChangeOperation(tc).runOperation(editor);
        }
    }

    public static void moveToNewUnit(CeylonEditor editor) 
            throws ExecutionException {
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu!=null) {
            Node node = getSelectedNode(editor);
            if (node instanceof Tree.Declaration) {
                try {
                    moveToNewUnit(editor, cu, node);
                } 
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void moveToNewUnit(CeylonEditor editor,
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
            new TextChangeOperation(tc).runOperation(editor);
        }
    }

    private static TextChange createChange(CeylonEditor editor,
            IDocument document) {
        if (editor.isDirty()) {
            return new DocumentChange("Move to New Unit", 
                    document);
        }
        else {
            return new TextFileChange("Move to New Unit", 
                    getFile(editor.getEditorInput()));
        }
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
