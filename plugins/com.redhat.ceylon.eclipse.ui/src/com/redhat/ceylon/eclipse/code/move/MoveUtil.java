package com.redhat.ceylon.eclipse.code.move;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getDocument;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.isImported;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getFile;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelectedNode;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.performChange;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoLocation;
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
            IProject project = w.getFile().getProject();
            String relpath = w.getFile().getFullPath()
                    .makeRelativeTo(w.getSourceDir().getPath())
                    .toPortableString();
            PhasedUnit npu = getProjectTypeChecker(project)
                    .getPhasedUnitFromRelativePath(relpath);
            Tree.CompilationUnit ncu = npu.getCompilationUnit();
            String original = cu.getUnit().getPackage().getNameAsString();
            String moved = ncu.getUnit().getPackage().getNameAsString();
            
            Declaration dec = node.getDeclarationModel();
            int start = getNodeStartOffset(node);
            int length = getNodeLength(node);
            
            CompositeChange change = 
                    new CompositeChange("Move to Source File");
            
            TextChange targetUnitChange = 
                    new TextFileChange("Move to Source File", 
                            w.getFile());
            targetUnitChange.setEdit(new MultiTextEdit());
            IDocument targetUnitDocument = targetUnitChange.getCurrentDocument(null);
            String contents = document.get(start, length);
            String delim = getDefaultLineDelimiter(targetUnitDocument);
            String text = delim + contents;
            Set<String> packages = new HashSet<String>();
            addImportEdits(node, targetUnitChange, targetUnitDocument, 
                    ncu, packages, dec);
            removeImport(original, dec, ncu, targetUnitChange, packages);
            targetUnitChange.addEdit(new InsertEdit(targetUnitDocument.getLength(), text));
            change.add(targetUnitChange);
            
            TextChange originalUnitChange = createChange(editor, document);
            originalUnitChange.setEdit(new MultiTextEdit());
            refactorImports(node, originalUnitChange, original, moved, cu);
            originalUnitChange.addEdit(new DeleteEdit(start, length));
            change.add(originalUnitChange);
            
            refactorProjectImports(node, file, w.getFile(), change, original, moved);
            
            performChange(editor, document, change, "Move to Source File");
            gotoLocation(w.getFile().getFullPath(), 
                    document.getLength()-contents.length());
        }
    }

    public static int addImportEdits(Tree.Declaration node, TextChange fc,
            IDocument doc, final Tree.CompilationUnit ncu, 
            final Set<String> packages, Declaration declaration) {
        Package p = ncu.getUnit().getPackage();
        Map<Declaration, String> imports = 
                getImports(node, p.getNameAsString(), ncu, packages);
        return applyImports(fc, imports, ncu, doc, declaration);
    }

    public static Map<Declaration,String> getImports(Tree.Declaration node,
            final String packageName, final Tree.CompilationUnit ncu,
            final Set<String> packages) {
        final Map<Declaration, String> imports = 
                new HashMap<Declaration, String>();
        node.visit(new Visitor() {
            private void add(Declaration d, Tree.Identifier id) {
                if (d!=null && id!=null) {
                    String pn = d.getUnit().getPackage().getNameAsString();
                    if (d.isToplevel() &&
                            !pn.equals(packageName) &&
                            !pn.equals(Module.LANGUAGE_MODULE_NAME) &&
                            (ncu==null || !isImported(d, ncu))) {
                        imports.put(d, id.getText());
                        packages.add(pn);
                    }
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
        return imports;
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
            String original = cu.getUnit().getPackage().getNameAsString();
            String moved = w.getPackageFragment().getElementName();
            int start = getNodeStartOffset(node);
            int length = getNodeLength(node);
            String delim = getDefaultLineDelimiter(document);

            CompositeChange change = 
                    new CompositeChange("Move to New Source File");
            
            String contents = document.get(start, length);
            //TODO: should we use this alternative when original==moved?
//            String importText = imports(node, cu.getImportList(), document);
            String importText = getImportText(node, moved, delim);
            String text = importText.isEmpty() ? 
                    contents : importText + delim + contents;
            CreateUnitChange newUnitChange = 
                    new CreateUnitChange(w.getFile(), w.includePreamble(), 
                            text, w.getProject(), "Move to New Source File");
            change.add(newUnitChange);
            
            TextChange originalUnitChange = createChange(editor, document);
            originalUnitChange.setEdit(new MultiTextEdit());
            refactorImports(node, originalUnitChange, original, moved, cu);
            originalUnitChange.addEdit(new DeleteEdit(start, length));
            change.add(originalUnitChange);
            
            refactorProjectImports(node, file, w.getFile(), change, original, moved);
            
            performChange(editor, document, change, "Move to New Source File");
            gotoLocation(w.getFile().getFullPath(), 0);
        }
    }

    public static String getImportText(Tree.Declaration node, 
            String targetPackage, String delim) {
        HashSet<String> packages = new HashSet<String>();
        Map<Declaration, String> imports = 
                getImports(node, targetPackage, null, packages);
        StringBuilder sb = new StringBuilder();
        for (String p: packages) {
            sb.append("import ").append(p).append(" { ");
            for (Map.Entry<Declaration, String> e: imports.entrySet()) {
                Declaration d = e.getKey();
                boolean first = true;
                String pn = d.getUnit().getPackage()
                        .getQualifiedNameString();
                if (pn.equals(p)) {
                    String name = d.getName();
                    String alias = e.getValue();
                    if (!first) sb.append(", ");
                    if (!name.equals(alias)) {
                        sb.append(alias).append("=");
                    }
                    sb.append(name);
                    first = false;
                }
            }
            sb.append(" }").append(delim);
        }
        return sb.toString();
    }

    public static void refactorProjectImports(Tree.Declaration node,
            IFile originalFile, IFile targetFile, CompositeChange change, 
            String originalPackage, String targetPackage) {
        if (!originalPackage.equals(targetPackage)) {
            for (PhasedUnit pu: getAllUnits(originalFile.getProject())) {
//                if (!node.getUnit().equals(pu.getUnit())) {
                    IFile file = ((IFileVirtualFile) pu.getUnitFile()).getFile();
                    if (!file.equals(originalFile) && !file.equals(targetFile)) {
                        TextFileChange tfc = new TextFileChange("Fix Import", file);
                        tfc.setEdit(new MultiTextEdit());
                        refactorImports(node, tfc, originalPackage, targetPackage, 
                                pu.getCompilationUnit());
                        if (tfc.getEdit().hasChildren()) {
                            change.add(tfc);
                        }
                    }
//                }
            }
        }
    }

    public static void refactorImports(Tree.Declaration node, TextChange tc, 
            String originalPackage, String targetPackage,
            Tree.CompilationUnit cu) {
        Declaration dec = node.getDeclarationModel();
        String pn = cu.getUnit().getPackage().getNameAsString();
        boolean inOriginalPackage = pn.equals(originalPackage);
        boolean inNewPackage = pn.equals(targetPackage);
        boolean foundOriginal = 
                removeImport(originalPackage, dec, cu, tc, 
                        Collections.<String>emptySet());
        if (foundOriginal && !inNewPackage || 
                inOriginalPackage && !inNewPackage && 
                        isUsedInUnit(cu, dec)) {
            addImport(targetPackage, dec, cu, tc);
        }
    }

    public static boolean isUsedInUnit(Tree.CompilationUnit rootNode, 
            final Declaration dec) {
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
        duv.visit(rootNode);
        boolean used = duv.detected;
        return used;
    }

    public static boolean removeImport(String originalPackage, final Declaration dec,
            Tree.CompilationUnit cu, TextChange tc, Set<String> packages) {
        boolean foundOriginal = false;
        Tree.ImportList il = cu.getImportList();
        for (Tree.Import imp: il.getImports()) {
            Referenceable model = imp.getImportPath().getModel();
            if (model!=null) {
                if (model.getNameAsString().equals(originalPackage)) {
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
                                    if (packages.contains(originalPackage)) { 
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
                                tc.addEdit(new DeleteEdit(offset,length));
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

    private static void addImport(String targetPackage, Declaration dec, 
            Tree.CompilationUnit cu, TextChange tc) {
        String name = dec.getName();
        boolean foundMoved = false;
        Tree.ImportList il = cu.getImportList();
        for (Tree.Import i: il.getImports()) {
            Referenceable model = i.getImportPath().getModel();
            if (model!=null) {
                if (model.getNameAsString().equals(targetPackage)) {
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
                        tc.addEdit(new InsertEdit(offset, addition));
                        foundMoved = true;
                    }
                    break;
                }
            }
        }
        if (!foundMoved) {
            String text = "import " + targetPackage + " { " + name + " }" + 
                    getDefaultLineDelimiter(getDocument(tc));
            tc.addEdit(new InsertEdit(0, text));
        }
    }

    private static TextChange createChange(CeylonEditor editor,
            IDocument document) {
        if (editor.isDirty()) {
            return new DocumentChange("Move from Source File", 
                    document);
        }
        else {
            return new TextFileChange("Move from Source File", 
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
