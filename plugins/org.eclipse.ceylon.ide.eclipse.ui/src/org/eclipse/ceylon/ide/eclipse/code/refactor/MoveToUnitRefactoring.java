/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.addImportEdits;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.createEditorChange;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.getImports;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.isUnsharedUsedLocally;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.refactorDocLinks;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.refactorImports;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.refactorProjectImportsAndDocLinks;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.removeImport;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getFile;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Package;

public class MoveToUnitRefactoring extends Refactoring {
    
    private final CeylonEditor editor;
    private final Tree.CompilationUnit rootNode;
    private final Tree.Declaration node;
    private final IFile originalFile; 
    private final IDocument document;
    private IFile targetFile;
    
    public void setTargetFile(IFile targetFile) {
        this.targetFile = targetFile;
    }
    
    public IFile getOriginalFile() {
        return originalFile;
    }
    
    public Tree.Declaration getNode() {
        return node;
    }
    
    public MoveToUnitRefactoring(CeylonEditor ceylonEditor) {
        editor = ceylonEditor;
        rootNode = 
                editor.getParseController()
                    .getTypecheckedRootNode();
        document = 
                editor.getDocumentProvider()
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
        return "Move to Source File";
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
        if (!targetFile.exists()) {
            refactoringStatus.addError("source file does not exist");
        }
        Tree.CompilationUnit targetRootNode = 
                getTargetRootNode();
        Package targetPackage = 
                targetRootNode.getUnit()
                    .getPackage();
        Package originalPackage = 
                rootNode.getUnit()
                    .getPackage();
        String originalPackageName = 
                originalPackage.getNameAsString();
        String targetPackageName = 
                targetPackage.getNameAsString();
        HashSet<String> packages = new HashSet<String>();
        Map<Declaration, String> imports = 
                getImports(node, targetPackageName, 
                        targetRootNode, packages);
        for (Declaration d: imports.keySet()) {
            Package p = d.getUnit().getPackage();
            String packageName = p.getNameAsString();
            if (packageName.isEmpty()) {
                refactoringStatus.addWarning(
                        "moved declaration depends on declaration in the default package: " +
                        d.getName());
            }
            else {
                if (!d.isShared() &&
                        !packageName.equals(targetPackageName)) {
                    refactoringStatus.addWarning(
                            "moved declaration depends on unshared declaration: " + 
                            d.getName());
                }
                if (targetPackage.getModule()
                        .getPackage(packageName)
                            ==null) {
                    refactoringStatus.addWarning(
                            "moved declaration depends on declaration in unimported module: " + 
                            d.getName() + " in module " + p.getModule().getNameAsString());
                }
            }
        }
        if (isUnsharedUsedLocally(node, originalFile, 
                originalPackageName, targetPackageName)) {
            if (targetPackageName.isEmpty()) {
                refactoringStatus.addWarning(
                        "moving declaration used locally to default package");
            }
            else if (originalPackage.getModule().getPackage(targetPackageName)==null) {
                refactoringStatus.addWarning(
                        "moving declaration used locally to unimported module");
            }
        }
        return refactoringStatus;
    }

    @Override
    public Change createChange(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        Tree.CompilationUnit targetRootNode = 
                getTargetRootNode();
        String originalPackageName = 
                rootNode.getUnit()
                    .getPackage()
                    .getNameAsString();
        String targetPackageName = 
                targetRootNode.getUnit()
                    .getPackage()
                    .getNameAsString();
        
        Declaration dec = node.getDeclarationModel();
        int start = node.getStartIndex();
        int length = node.getDistance();
        
        CompositeChange change = 
                new CompositeChange("Move to Source File");
        
        TextChange targetUnitChange = 
                new TextFileChange("Move to Source File", 
                        targetFile);
        targetUnitChange.setEdit(new MultiTextEdit());
        IDocument targetUnitDocument = 
                targetUnitChange.getCurrentDocument(null);
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
        String delim = 
                utilJ2C().indents()
                    .getDefaultLineDelimiter(targetUnitDocument);
        String text = delim + contents;
        Set<String> packages = new HashSet<String>();
        addImportEdits(node, 
                targetUnitChange, targetUnitDocument, 
                targetRootNode, packages, dec);
        removeImport(originalPackageName, dec, 
                targetRootNode, targetUnitChange, packages);
        targetUnitChange.addEdit(new InsertEdit(
                targetUnitDocument.getLength(), text));
        targetUnitChange.setTextType("ceylon");
        change.add(targetUnitChange);
        
        TextChange originalUnitChange = 
                createEditorChange(editor, document);
        originalUnitChange.setEdit(new MultiTextEdit());
        refactorImports(node, originalPackageName, 
                targetPackageName, rootNode, 
                originalUnitChange);
        refactorDocLinks(node, targetPackageName, rootNode, 
                originalUnitChange);
        originalUnitChange.addEdit(new DeleteEdit(start, length));
        originalUnitChange.setTextType("ceylon");
        change.add(originalUnitChange);
        
        refactorProjectImportsAndDocLinks(node, 
                originalFile, targetFile, change, 
                originalPackageName, targetPackageName);
        
        //TODO: DocLinks
        
        return change;
    }

    public Tree.CompilationUnit getTargetRootNode() {
        IProject project = targetFile.getProject();
        String path = 
                targetFile.getProjectRelativePath()
                    .removeFirstSegments(1)
                    .toPortableString();
        return getProjectTypeChecker(project)
                .getPhasedUnitFromRelativePath(path)
                .getCompilationUnit();
    }

    public int getOffset() {
        return 0; //TODO!!!
    }

    public IPath getTargetPath() {
        return targetFile.getFullPath();
    }

}
