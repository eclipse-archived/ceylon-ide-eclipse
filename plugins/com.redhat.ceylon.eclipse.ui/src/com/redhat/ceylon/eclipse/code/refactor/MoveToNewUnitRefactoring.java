package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.createEditorChange;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.getImportText;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.refactorImports;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.refactorProjectImports;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getFile;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeStartOffset;

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
    public boolean isEnabled() {
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
        // TODO Auto-generated method stub
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        String original = rootNode.getUnit().getPackage().getNameAsString();
        String moved = targetPackage.getElementName();
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
        //TODO: should we use this alternative when original==moved?
//        String importText = imports(node, cu.getImportList(), document);
        String importText = getImportText(node, moved, delim);
        String text = importText.isEmpty() ? 
                contents : importText + delim + contents;
        offset = importText.isEmpty() ? 0 : (importText + delim).length();
        CreateUnitChange newUnitChange = 
                new CreateUnitChange(targetFile, includePreamble, 
                        text, targetProject, 
                        "Create source file '" + targetFile.getProjectRelativePath() + "'");
        change.add(newUnitChange);
//        newUnitChange.setTextType("ceylon");
        
        TextChange originalUnitChange = createEditorChange(editor, document);
        originalUnitChange.setEdit(new MultiTextEdit());
        refactorImports(node, originalUnitChange, original, moved, rootNode);
        originalUnitChange.addEdit(new DeleteEdit(start, length));
        originalUnitChange.setTextType("ceylon");
        change.add(originalUnitChange);
        
        refactorProjectImports(node, originalFile, targetFile, change, original, moved);
        
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
