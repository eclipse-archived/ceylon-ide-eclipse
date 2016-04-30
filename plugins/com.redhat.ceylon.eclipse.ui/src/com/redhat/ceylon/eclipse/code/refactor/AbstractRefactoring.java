package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.model.CrossProjectBinaryUnit;
import com.redhat.ceylon.ide.common.model.CrossProjectSourceFile;
import com.redhat.ceylon.ide.common.model.EditedSourceFile;
import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.model.ProjectSourceFile;
import com.redhat.ceylon.ide.common.typechecker.ModifiablePhasedUnit;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Unit;

abstract class AbstractRefactoring extends Refactoring {
    
    final IProject project;
    final IFile sourceFile;
    final List<CommonToken> tokens;
    final IDocument document;
    final CeylonEditor editor;
    final Tree.CompilationUnit rootNode;
    Node node;
   
    /*public AbstractRefactoring(IQuickFixInvocationContext context) {
        sourceFile = context.getModel().getFile();
        project = sourceFile.getProject();
        PhasedUnit pu = CeylonBuilder.getPhasedUnit(sourceFile);
        rootNode = pu.getCompilationUnit();
        tokenStream = pu.getTokenStream();
        node = CeylonSourcePositionLocator.findNode(rootNode, context.getOffset(),
                context.getOffset()+context.getLength());
    }*/
    
    public AbstractRefactoring(IEditorPart editor) {
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            this.editor = ce;
            document = 
                    ce.getDocumentProvider()
                        .getDocument(editor.getEditorInput());
            project = EditorUtil.getProject(editor);
            CeylonParseController cpc = ce.getParseController();
            tokens = cpc.getTokens();
            rootNode = cpc.getTypecheckedRootNode();
            IEditorInput input = editor.getEditorInput();
            if (rootNode!=null && input instanceof IFileEditorInput) {
                sourceFile = EditorUtil.getFile(input);
                node = findNode(rootNode, tokens, getSelection(ce));
            }
            else {
                sourceFile = null;
                node = null;
            }
        }
        else {
            this.editor = null;
            document = null;
            tokens = null;
            rootNode = null;
            sourceFile = null;
            node = null;
            project = null;
        }
    }
    
    boolean inSameProject(Declaration declaration) {
        Unit unit = declaration.getUnit();
        if (unit instanceof CrossProjectSourceFile ||
            unit instanceof CrossProjectBinaryUnit) {
            return false;
        }
        if (unit instanceof IResourceAware) {
            IResourceAware<IProject, IFolder, IFile> ra = 
                    (IResourceAware<IProject,IFolder,IFile>) unit;
            IProject project = ra.getResourceProject();
            if (project==null) {
                return false;
            }
            else {
                return project.equals(this.project);
            }
        }
        return false;
    }

    boolean getEditable() {
        return rootNode.getUnit() instanceof EditedSourceFile ||
                rootNode.getUnit() instanceof ProjectSourceFile;
    }
    
    Tree.Term unparenthesize(Tree.Term term) {
        if (term instanceof Tree.Expression) {
            Expression e = (Tree.Expression) term;
            if (!(e.getTerm() instanceof Tree.Tuple)) {
                return unparenthesize(e.getTerm());
            }
        }
        return term;
    }
    
    DocumentChange newDocumentChange() {
        DocumentChange dc = 
                new DocumentChange(
                        editor.getEditorInput().getName() + 
                        " \u2014 current editor", 
                        document);
        dc.setTextType("ceylon");
        return dc;
    }
    
    TextFileChange newTextFileChange(
            IResourceAware<IProject,IFolder,IFile> pu) {
        TextFileChange tfc = 
                new TextFileChange(getName(), 
                        pu.getResourceFile());
        tfc.setTextType("ceylon");
        return tfc;
    }

    protected boolean searchInEditor() {
        return editor!=null && editor.isDirty();
    }

    protected boolean searchInFile(PhasedUnit pu) {
        return editor==null || !editor.isDirty() || rootNode == null ||
                !pu.getUnit().equals(rootNode.getUnit());
    }
    
    protected boolean inSamePackage(PhasedUnit pu) {
        Package editorPackage = 
                editor.getParseController()
                    .getLastCompilationUnit()
                    .getUnit()
                    .getPackage();
        return pu.getPackage()
                    .equals(editorPackage);
    }
    
    TextChange newLocalChange() {
        TextChange tc = searchInEditor() ?
                new DocumentChange(getName(), document) :
                new TextFileChange(getName(), sourceFile);
        tc.setTextType("ceylon");
        return tc;
    }

    protected List<PhasedUnit> getAllUnits() {
        List<PhasedUnit> units = new ArrayList<PhasedUnit>();
        units.addAll(getUnits(project));
        for (IProject p: project.getReferencingProjects()) {
            units.addAll(getUnits(p));
        }
        return units;
    }
    
    protected abstract boolean isAffectingOtherFiles();

    protected int countDeclarationOccurrences() {
        int count = 0;
        if (isAffectingOtherFiles()) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu)) {
                    count += countReferences(pu.getCompilationUnit());
                }
            }
        }
        if (!isAffectingOtherFiles() || searchInEditor()) {
            count += countReferences(rootNode);
        }
        return count;
    }
    
    int countReferences(Tree.CompilationUnit cu) {
        return 0;
    }
    
    int getSaveMode() {
        return isAffectingOtherFiles() ? 
                RefactoringSaveHelper.SAVE_CEYLON_REFACTORING : 
                RefactoringSaveHelper.SAVE_NOTHING;
    }
    
    public Change createChange(IProgressMonitor pm) 
            throws CoreException,
                   OperationCanceledException {
        CompositeChange change = 
                new CompositeChange(getName());
        
        int i=0;
        if (isAffectingOtherFiles()) {
            List<PhasedUnit> units = getAllUnits();
            pm.beginTask(getName(), units.size());
            for (PhasedUnit pu: units) {
                if (searchInFile(pu)) {
                    ProjectPhasedUnit ppu = 
                            (ProjectPhasedUnit) pu;
                    refactorInFile(newTextFileChange(ppu), 
                            change, 
                            pu.getCompilationUnit(), 
                            pu.getTokens());
                    pm.worked(i++);
                }
            }
        }
        else {
            pm.beginTask(getName(), 1);
            PhasedUnit pu =
                    editor.getParseController()
                        .getLastPhasedUnit();
            if (searchInFile(pu)) {
                ModifiablePhasedUnit ppu = 
                        (ModifiablePhasedUnit) pu;
                refactorInFile(newTextFileChange(ppu), 
                        change, 
                        ppu.getCompilationUnit(),
                        ppu.getTokens());
                pm.worked(i++);
            }
        }
        
        if (searchInEditor()) {
            CeylonParseController pc = 
                    editor.getParseController();
            refactorInFile(newDocumentChange(), 
                    change, 
                    pc.getLastCompilationUnit(),
                    pc.getTokens());
            pm.worked(i++);
        }
        
        pm.done();
        return change;
    }

    abstract void refactorInFile(TextChange textChange, 
            CompositeChange compositChange, 
            Tree.CompilationUnit rootNode,
            List<CommonToken> tokens);

}
