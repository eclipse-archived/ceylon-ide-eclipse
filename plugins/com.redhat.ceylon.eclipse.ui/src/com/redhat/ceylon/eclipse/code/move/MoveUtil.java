package com.redhat.ceylon.eclipse.code.move;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getDocument;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.isImported;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getFile;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelectedNode;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.performChange;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoLocation;
import static com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler.imports;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeEndOffset;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeStartOffset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
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

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.wizard.SelectNewUnitWizard;
import com.redhat.ceylon.eclipse.code.wizard.SelectUnitWizard;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;

public class MoveUtil {

    public static void moveToUnit(CeylonEditor editor) 
            throws ExecutionException {
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu!=null) {
            Node node = getSelectedNode(editor);
            if (node instanceof Tree.Declaration) {
                try {
                    moveToUnit(editor, cu, (Tree.Declaration) node);
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
            Tree.CompilationUnit cu, Tree.Declaration node) 
                    throws BadLocationException,
                           ExecutionException, 
                           CoreException {
        IDocument document = editor.getDocumentProvider()
                .getDocument(editor.getEditorInput());
        SelectUnitWizard w = 
                new SelectUnitWizard("Move to Source File", 
                        "Select a Ceylon source file for the selected declaration.");
        IFile file = getFile(editor.getEditorInput());
        if (w.open(file)) {
            int start = node.getStartIndex();
            int length = node.getStopIndex()-start+1;
            String contents = document.get(start, length);
            CompositeChange change = 
                    new CompositeChange("Move to Source File");
            TextChange fc = 
                    new TextFileChange("Move to Source File", 
                            w.getFile());
            fc.setEdit(new MultiTextEdit());
            IDocument doc = fc.getCurrentDocument(null);
            int len = doc.getLength();
            String delim = getDefaultLineDelimiter(doc);
            String text = delim + contents;
            IProject project = w.getFile().getProject();
            String relpath = w.getFile().getFullPath()
                    .makeRelativeTo(w.getSourceDir().getPath())
                    .toPortableString();
            PhasedUnit npu = getProjectTypeChecker(project)
                    .getPhasedUnitFromRelativePath(relpath);
            Tree.CompilationUnit ncu = npu.getCompilationUnit();
            Set<String> packages = new HashSet<String>();
            int il = addImportEdits(node, fc, doc, ncu, packages,
                    node.getDeclarationModel());
            fc.addEdit(new InsertEdit(len, text));
            change.add(fc);
            TextChange tc = createChange(editor, document);
            tc.setEdit(new DeleteEdit(start, length));
            change.add(tc);
            String original = cu.getUnit().getPackage().getNameAsString();
            String moved = ncu.getUnit().getPackage().getNameAsString();
            refactorProjectImports(node, file, change, original, moved, packages);
            performChange(editor, document, change, "Move to Source File");
            gotoLocation(w.getFile().getFullPath(), len+il+delim.length());
        }
    }

    public static int addImportEdits(Tree.Declaration node, TextChange fc,
            IDocument doc, final Tree.CompilationUnit ncu, 
            final Set<String> packages, Declaration declaration) {
        final Package p = ncu.getUnit().getPackage();
        final Map<Declaration, String> imports = 
                new HashMap<Declaration, String>();
        node.visit(new Visitor() {
            private void add(Declaration d, Tree.Identifier id) {
                if (d!=null && id!=null && d.isToplevel() &&
                        !d.getUnit().getPackage().equals(p) &&
                        !d.getUnit().getPackage().getNameAsString()
                                .equals(Module.LANGUAGE_MODULE_NAME) &&
                        !isImported(d, ncu)) {
                    imports.put(d, id.getText());
                    packages.add(d.getUnit().getPackage().getNameAsString());
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
        return applyImports(fc, imports, ncu, doc, declaration);
    }

    public static void moveToNewUnit(CeylonEditor editor) 
            throws ExecutionException {
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu!=null) {
            Node node = getSelectedNode(editor);
            if (node instanceof Tree.Declaration) {
                try {
                    moveToNewUnit(editor, cu, (Tree.Declaration) node);
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
            Tree.CompilationUnit cu, Tree.Declaration node) 
                    throws BadLocationException,
                           ExecutionException, 
                           CoreException {
        IDocument document = editor.getDocumentProvider()
                .getDocument(editor.getEditorInput());
        String suggestedUnitName = node.getIdentifier().getText();
        SelectNewUnitWizard w = 
                new SelectNewUnitWizard("Move to New Source File", 
                        "Create a new Ceylon source file for the selected declaration.",
                        suggestedUnitName);
        IFile file = getFile(editor.getEditorInput());
        if (w.open(file)) {
            String imports = imports(node, cu.getImportList(), document);
            int start = node.getStartIndex();
            int length = node.getStopIndex()-start+1;
            String contents = document.get(start, length);
            String text = imports==null ? 
                    contents : 
                    imports + getDefaultLineDelimiter(document) + contents;
            CompositeChange change = 
                    new CompositeChange("Move to New Source File");
            change.add(new CreateUnitChange(w.getFile(), w.includePreamble(), 
                    text, w.getProject(), "Move to New Source File"));
            TextChange tc = createChange(editor, document);
            tc.setEdit(new DeleteEdit(start, length));
            change.add(tc);
            String original = cu.getUnit().getPackage().getNameAsString();
            String moved = w.getPackageFragment().getElementName();
            refactorProjectImports(node, file, change, original, moved, 
                    Collections.<String>emptySet());
            performChange(editor, document, change, "Move to New Source File");
            gotoLocation(w.getFile().getFullPath(), 0);
        }
    }

    public static void refactorProjectImports(Tree.Declaration node,
            IFile file, CompositeChange change, String original, String moved, 
            Set<String> packages) {
        if (!original.equals(moved)) {
            for (PhasedUnit pu: getAllUnits(file.getProject())) {
                IFile f = ((IFileVirtualFile) pu.getUnitFile()).getFile();
                TextFileChange tfc = new TextFileChange("Fix Import", f);
                tfc.setEdit(new MultiTextEdit());
                Declaration dec = node.getDeclarationModel();
                String pn = pu.getUnit().getPackage().getNameAsString();
                boolean inOriginalPackage = pn.equals(original);
                boolean inNewPackage = pn.equals(moved);
                boolean foundOriginal = removeImport(original, dec, pu, tfc, packages);
                if (foundOriginal && !inNewPackage || 
                        inOriginalPackage && !inNewPackage && 
                                isUsedInUnit(pu, dec)) {
                    addImport(moved, dec, pu, tfc);
                }
                if (tfc.getEdit().hasChildren()) {
                    change.add(tfc);
                }
            }
        }
    }

    public static boolean isUsedInUnit(PhasedUnit pu, final Declaration dec) {
        class DetectUsageVisitor extends Visitor {
            boolean detected;
            @Override
            public void visit(Tree.Declaration that) {
                if (!that.getDeclarationModel().equals(dec)) {
                    super.visit(that);
                }
            }
            @Override
            public void visit(Tree.BaseType that) {
                TypeDeclaration d = that.getDeclarationModel();
                if (d!=null && d.equals(dec)) {
                    detected = true;
                }
            }
            @Override
            public void visit(Tree.BaseMemberOrTypeExpression that) {
                Declaration d = that.getDeclaration();
                if (d!=null && d.equals(dec)) {
                    detected = true;
                }
            }
        }
        DetectUsageVisitor duv = new DetectUsageVisitor();
        duv.visit(pu.getCompilationUnit());
        boolean used = duv.detected;
        return used;
    }

    public static boolean removeImport(String original, final Declaration dec,
            PhasedUnit pu, TextFileChange tfc, Set<String> packages) {
        boolean foundOriginal = false;
        Tree.ImportList il = pu.getCompilationUnit().getImportList();
        for (Tree.Import imp: il.getImports()) {
            Referenceable model = imp.getImportPath().getModel();
            if (model!=null) {
                if (model.getNameAsString().equals(original)) {
                    Tree.ImportMemberOrTypeList imtl = 
                            imp.getImportMemberOrTypeList();
                    if (imtl!=null) {
                        List<ImportMemberOrType> imts = 
                                imtl.getImportMemberOrTypes();
                        for (int j=0; j<imts.size(); j++) {
                            Tree.ImportMemberOrType imt = imts.get(j);
                            Declaration d = imt.getDeclarationModel();
                            if (d!=null && d.equals(dec)) {
                                int offset;
                                int length;
                                if (j>0) {
                                    offset = getNodeEndOffset(imts.get(j-1));
                                    length = getNodeEndOffset(imt)-offset;
                                }
                                else if (j<imts.size()-1) {
                                    offset = getNodeStartOffset(imt);
                                    length = getNodeStartOffset(imts.get(j+1))-offset;
                                }
                                else {
                                    if (packages.contains(original)) { 
                                        //we're adding to this import statement,
                                        //so don't delete the whole import
                                        offset = getNodeStartOffset(imt);
                                        length = getNodeLength(imt);
                                    }
                                    else {
                                        offset = getNodeStartOffset(imp);
                                        length = getNodeLength(imp);
                                    }
                                }
                                tfc.addEdit(new DeleteEdit(offset,length));
                                foundOriginal = true;
                                break;
                                //TODO: return the alias!
                            }
                        }
                    }
                    break;
                }
            }
        }
        return foundOriginal;
    }

    private static void addImport(String moved, Declaration dec, 
            PhasedUnit pu, TextFileChange tfc) {
        String name = dec.getName();
        boolean foundMoved = false;
        Tree.ImportList il = pu.getCompilationUnit().getImportList();
        for (Tree.Import i: il.getImports()) {
            Referenceable model = i.getImportPath().getModel();
            if (model!=null) {
                if (model.getNameAsString().equals(moved)) {
                    Tree.ImportMemberOrTypeList imtl = 
                            i.getImportMemberOrTypeList();
                    if (imtl!=null) {
                        String addition;
                        int offset;
                        List<Tree.ImportMemberOrType> imts = 
                                imtl.getImportMemberOrTypes();
                        if (imts.isEmpty()) {
                            offset = getNodeStartOffset(imtl);
                            addition = " " + name;
                        }
                        else {
                            offset = getNodeEndOffset(imts.get(imts.size()-1));
                            addition = ", " + name;
                        }
                        //TODO: the alias!
                        tfc.addEdit(new InsertEdit(offset, addition));
                        foundMoved = true;
                    }
                    break;
                }
            }
        }
        if (!foundMoved) {
            String text = "import " + moved + " { " + name + " }" + 
                    getDefaultLineDelimiter(getDocument(tfc));
            tfc.addEdit(new InsertEdit(0, text));
        }
    }

    private static TextChange createChange(CeylonEditor editor,
            IDocument document) {
        if (editor.isDirty()) {
            return new DocumentChange("Move to New Source File", 
                    document);
        }
        else {
            return new TextFileChange("Move to New Source File", 
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

    public static String getDeclarationName(CeylonEditor editor) {
        Node node = getSelectedNode(editor);
        if (node instanceof Tree.Declaration) {
            return ((Tree.Declaration) node).getIdentifier().getText();
        }
        else {
            return null;
        }
    }

    static List<PhasedUnit> getAllUnits(IProject project) {
        List<PhasedUnit> units = new ArrayList<PhasedUnit>();
        units.addAll(getUnits(project));
        for (IProject p: project.getReferencingProjects()) {
            units.addAll(getUnits(p));
        }
        return units;
    }

}
