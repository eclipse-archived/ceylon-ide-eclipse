package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.createEditorChange;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.getImportText;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.isUnsharedUsedLocally;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.refactorDocLinks;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.refactorImports;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.refactorProjectImportsAndDocLinks;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getFile;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeStartOffset;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class MoveToNewUnitRefactoring extends Refactoring {
    
    private final CeylonEditor editor;
    private final Tree.CompilationUnit rootNode;
    private final Tree.Declaration node;
    private final IFile originalFile; 
    private final IDocument document;
    private IFile targetFile; 
    private IProject targetProject;
    private IPackageFragment targetPackage;
    private boolean includePreamble;
    private int offset;
    
    public void setTargetFile(IFile targetFile) {
        this.targetFile = targetFile;
    }
    
    public void setTargetPackage(IPackageFragment targetPackage) {
        this.targetPackage = targetPackage;
    }
    
    public void setIncludePreamble(boolean include) {
        includePreamble = include;
    }
    
    public void setTargetProject(IProject targetProject) {
        this.targetProject = targetProject;
    }
    
    public Tree.Declaration getNode() {
        return node;
    }

    public MoveToNewUnitRefactoring(CeylonEditor ceylonEditor) {
        editor = ceylonEditor;
        rootNode = editor.getParseController().getRootNode();
        document = editor.getDocumentProvider()
                .getDocument(editor.getEditorInput());
        originalFile = getFile(editor.getEditorInput());
        if (rootNode!=null) {
            Node node = editor.getSelectedNode();
            if (node instanceof Tree.Declaration) {
                this.node = (Tree.Declaration) node;
            }
            else {
                this.node = null;
            }
        }
        else {
            this.node = null;
        }
    }
    
    @Override
    public boolean getEnabled() {
        return node!=null;
    }

    @Override
    public String getName() {
        return "Move to New Source File";
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        RefactoringStatus refactoringStatus = new RefactoringStatus();
        if (targetFile.exists()) {
            refactoringStatus.addError("source file already exists");
        }
        Package originalPackage = rootNode.getUnit().getPackage();
        String originalPackageName = originalPackage.getNameAsString();
        String targetPackageName = targetPackage.getElementName();
        HashSet<String> packages = new HashSet<String>();
        Map<Declaration, String> imports = 
                MoveUtil.getImports(node, 
                        targetPackage.getElementName(), 
                        null, packages);
        for (Declaration d: imports.keySet()) {
            Package p = d.getUnit().getPackage();
            String packageName = p.getNameAsString();
            if (packageName.isEmpty()) {
                refactoringStatus.addWarning("moved declaration depends on declaration in the default package: " +
                        d.getName());
            }
            else {
                if (!d.isShared() && 
                        !packageName.equals(targetPackageName)) {
                    refactoringStatus.addWarning("moved declaration depends on unshared declaration: " + 
                            d.getName());
                }
                TypeChecker tc = getProjectTypeChecker(targetProject);
                if (tc!=null) {
                    for (PhasedUnit phasedUnit: tc.getPhasedUnits().getPhasedUnits()) {
                        if (phasedUnit.getPackage().getNameAsString().equals(targetPackage.getElementName())) {
                            if (phasedUnit.getPackage().getModule().getPackage(packageName)==null) {
                                refactoringStatus.addWarning("moved declaration depends on declaration in unimported module: " + 
                                        d.getName() + " in module " + p.getModule().getNameAsString());
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (isUnsharedUsedLocally(node, originalFile, 
                originalPackageName, targetPackageName)) {
            if (targetPackageName.isEmpty()) {
                refactoringStatus.addWarning("moving declaration used locally to default package");
            }
            else if (originalPackage.getModule().getPackage(targetPackageName)==null) {
                refactoringStatus.addWarning("moving declaration used locally to unimported module");
            }
        }
        return refactoringStatus;
    }

    @Override
    public Change createChange(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        String originalPackageName = rootNode.getUnit()
                .getPackage().getNameAsString();
        String targetPackageName = targetPackage.getElementName();
        int start = getNodeStartOffset(node);
        int length = getNodeLength(node);
        String delim = getDefaultLineDelimiter(document);

        CompositeChange change = 
                new CompositeChange("Move to New Source File");
        
        String contents;
        try {
            contents = document.get(start, length);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            throw new OperationCanceledException();
        }
        if (isUnsharedUsedLocally(node, originalFile, 
                originalPackageName, targetPackageName)) {
            contents = "shared " + contents;
        }
        //TODO: should we use this alternative when original==moved?
//        String importText = imports(node, cu.getImportList(), document);
        String importText = getImportText(node, targetPackageName, delim);
        String text = importText.isEmpty() ? 
                contents : importText + delim + contents;
        offset = importText.isEmpty() ? 
                0 : (importText + delim).length();
        CreateUnitChange newUnitChange = 
                new CreateUnitChange(targetFile, includePreamble, 
                        text, targetProject, 
                        "Create source file '" + 
                        targetFile.getProjectRelativePath() + "'");
        change.add(newUnitChange);
//        newUnitChange.setTextType("ceylon");
        
        TextChange originalUnitChange = 
                createEditorChange(editor, document);
        originalUnitChange.setEdit(new MultiTextEdit());
        refactorImports(node, originalPackageName, 
                targetPackageName, rootNode, originalUnitChange);
        refactorDocLinks(node, targetPackageName, rootNode, 
                originalUnitChange);
        originalUnitChange.addEdit(new DeleteEdit(start, length));
        originalUnitChange.setTextType("ceylon");
        change.add(originalUnitChange);
        
        refactorProjectImportsAndDocLinks(node, 
                originalFile, targetFile, 
                change, originalPackageName, targetPackageName);
        
        //TODO: DocLinks
        
        return change;
    }
    
    public int getOffset() {
        return offset;
    }

    public IPath getTargetPath() {
        return targetFile.getFullPath();
    }

}
