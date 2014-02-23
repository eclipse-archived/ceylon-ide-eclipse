package com.redhat.ceylon.eclipse.code.move;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getFile;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelectedNode;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.performChange;
import static com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler.imports;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.correct.ImportProposals;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.wizard.SelectNewUnitWizard;
import com.redhat.ceylon.eclipse.code.wizard.SelectUnitWizard;

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
                catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void moveToUnit(CeylonEditor editor,
            Tree.CompilationUnit cu, Node node) 
                    throws BadLocationException,
                           ExecutionException, 
                           CoreException {
        IDocument document = editor.getDocumentProvider()
                .getDocument(editor.getEditorInput());
        SelectUnitWizard w = new SelectUnitWizard("Move to Unit", 
                "Select a Ceylon source file for the selected declaration.");
        if (w.open(getFile(editor.getEditorInput()))) {
            int start = node.getStartIndex();
            int length = node.getStopIndex()-start+1;
            String contents = document.get(start, length);
            CompositeChange change = new CompositeChange("Move to Unit");
            TextChange fc = new TextFileChange("Move to Unit", w.getFile());
            fc.setEdit(new MultiTextEdit());
            IDocument doc = fc.getCurrentDocument(null);
            int len = doc.getLength();
            String delim = getDefaultLineDelimiter(doc);
            String text = delim + contents;
            IProject project = w.getFile().getProject();
            String relpath = w.getFile().getFullPath()
                    .makeRelativeTo(w.getSourceDir().getPath())
                    .toPortableString();
            final Tree.CompilationUnit ncu = getProjectTypeChecker(project)
                    .getPhasedUnitFromRelativePath(relpath)
                    .getCompilationUnit();
            final Map<Declaration, String> imports = new HashMap<Declaration, String>();
            final Package p = ncu.getUnit().getPackage();
            node.visit(new Visitor() {
                private void add(Declaration d, Tree.Identifier id) {
                    if (d!=null && id!=null && d.isToplevel() &&
                            !d.getUnit().getPackage().equals(p) &&
                            !d.getUnit().getPackage().getNameAsString()
                                    .equals(Module.LANGUAGE_MODULE_NAME) &&
                            !ImportProposals.isImported(d, ncu)) {
                        imports.put(d, id.getText());
                    }
                }
                @Override
                public void visit(Tree.BaseType that) {
                    super.visit(that);
                    add(that.getDeclarationModel(), that.getIdentifier());
                }
                @Override
                public void visit(Tree.BaseMemberOrTypeExpression that) {
                    super.visit(that);
                    add(that.getDeclaration(), that.getIdentifier());
                }
                @Override
                public void visit(Tree.MemberLiteral that) {
                    add(that.getDeclaration(), that.getIdentifier());
                }
            });
            int il = applyImports(fc, imports, ncu, doc);
            fc.addEdit(new InsertEdit(len, text));
            change.add(fc);
            TextChange tc = createChange(editor, document);
            tc.setEdit(new DeleteEdit(start, length));
            change.add(tc);
            performChange(editor, document, change, "Move to Unit");
            gotoLocation(w.getFile().getFullPath(), 
                    len+il+delim.length());
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
                catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void moveToNewUnit(CeylonEditor editor,
            Tree.CompilationUnit cu, Node node) 
                    throws BadLocationException,
                           ExecutionException, 
                           CoreException {
        IDocument document = editor.getDocumentProvider()
                .getDocument(editor.getEditorInput());
        String suggestedUnitName = 
                ((Tree.Declaration) node).getIdentifier().getText();
        SelectNewUnitWizard w = new SelectNewUnitWizard("Move to New Unit", 
                "Create a new Ceylon source file for the selected declaration.",
                suggestedUnitName);
        if (w.open(getFile(editor.getEditorInput()))) {
            String imports = imports(node, cu.getImportList(), document);
            int start = node.getStartIndex();
            int length = node.getStopIndex()-start+1;
            String contents = document.get(start, length);
            String text = imports==null ? 
                    contents : 
                    imports + getDefaultLineDelimiter(document) + contents;
            CompositeChange change = new CompositeChange("Move to New Unit");
            change.add(new CreateUnitChange(w.getFile(), w.includePreamble(), text, w.getProject()));
            TextChange tc = createChange(editor, document);
            tc.setEdit(new DeleteEdit(start, length));
            change.add(tc);
            performChange(editor, document, change, "Move to New Unit");
            gotoLocation(w.getFile().getFullPath(), 0);
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
