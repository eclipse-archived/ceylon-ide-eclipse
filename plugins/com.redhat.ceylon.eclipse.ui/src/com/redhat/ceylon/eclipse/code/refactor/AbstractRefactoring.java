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
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
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
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
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
            ProjectPhasedUnit<IProject,IResource,IFolder,IFile> pu) {
        TextFileChange tfc = 
                new TextFileChange(getName(), 
                        pu.getResourceFile());
        tfc.setTextType("ceylon");
        return tfc;
    }

    protected boolean searchInEditor() {
        return editor!=null && editor.isDirty();
    }

    boolean searchInFile(PhasedUnit pu) {
        return editor==null || !editor.isDirty() || rootNode == null ||
                !pu.getUnit().equals(rootNode.getUnit());
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
    
    protected abstract boolean visibleOutsideUnit();

    protected int countDeclarationOccurrences() {
        int count = 0;
        if (visibleOutsideUnit()) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu)) {
                    count += countReferences(pu.getCompilationUnit());
                }
            }
        }
        if (searchInEditor()) {
            count += countReferences(rootNode);
        }
        return count;
    }
    
    int countReferences(Tree.CompilationUnit cu) {
        return 0;
    }
    
    int getSaveMode() {
        return visibleOutsideUnit() ? 
                RefactoringSaveHelper.SAVE_CEYLON_REFACTORING : 
                RefactoringSaveHelper.SAVE_NOTHING;
    }

}
