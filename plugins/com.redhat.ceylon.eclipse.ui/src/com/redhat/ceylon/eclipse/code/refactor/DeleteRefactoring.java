package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedExplicitDeclaration;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.search.CeylonSearchMatch;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class DeleteRefactoring extends AbstractRefactoring {
    
    private boolean deleteRefinements;
    
    private class FindDeletedReferencesVisitor 
            extends FindReferencesVisitor {
        private FindDeletedReferencesVisitor(Declaration declaration) {
            super(declaration);
        }
        @Override
        protected boolean isReference(Declaration ref) {
            Declaration declaration = getDeclaration();
            if (ref==null) {
                return false;
            }
            else if (ref.equals(declaration)) {
                return !declaration.isActual() || 
                        declaration.equals(refinedDeclaration); //TODO: should check that it doesn't refine the return type
            }
            else {
                return deleteRefinements &&
                        ref.refines(declaration);
            }
        }
        @Override
        public void visit(Tree.Declaration that) {
            Declaration dec = that.getDeclarationModel();
            if (!dec.equals(declarationToDelete) &&
                    (!deleteRefinements || 
                            !dec.refines(declarationToDelete))) {
                super.visit(that);                
            }
        }
    }
    
    //TODO: copy/pasted from RenameRefactoring!
    class FindDocLinkReferencesVisitor extends Visitor {
        private Declaration declaration;
        private List<Tree.DocLink> links = new ArrayList<Tree.DocLink>();
        List<Tree.DocLink> getLinks() {
            return links;
        }
        FindDocLinkReferencesVisitor(Declaration declaration) {
            this.declaration = declaration;
        }
        @Override
        public void visit(Tree.DocLink that) {
            if (that.getBase()!=null) {
                if (that.getBase().equals(declaration)) {
                    links.add(that);
                }
                else if (that.getQualified()!=null) {
                    if (that.getQualified().contains(declaration)) {
                        links.add(that);
                    }
                }
            }
        }
        @Override
        public void visit(Tree.Declaration that) {
            if (!that.getDeclarationModel()
                    .equals(declarationToDelete)) {
                super.visit(that);                
            }
        }
    }
    
    private class FindDeletedRefinementsVisitor
            extends FindRefinementsVisitor {
        public FindDeletedRefinementsVisitor(Declaration declaration) {
            super(declaration);
        }
        @Override
        protected boolean isRefinement(Declaration dec) {
            return !dec.equals(declarationToDelete) &&
                    (super.isRefinement(dec) &&
                    !deleteRefinements ||
                    dec.equals(refinedDeclaration) &&
                    dec.isFormal());
        }
    }
    
    private final Declaration refinedDeclaration;
    private final Declaration declarationToDelete;
    
    public Node getNode() {
        return node;
    }

    public DeleteRefactoring(ITextEditor editor) {
        super(editor);
        if (rootNode!=null) {
            declarationToDelete = getReferencedExplicitDeclaration(node, rootNode);
            if (declarationToDelete!=null) {
                refinedDeclaration = declarationToDelete.getRefinedDeclaration();
            }
            else {
                refinedDeclaration = null;
            }
        }
        else {
            declarationToDelete = null;
            refinedDeclaration = null;
        }
    }
    
    @Override
    public boolean isEnabled() {
        return declarationToDelete!=null &&
                project != null &&
                inSameProject(declarationToDelete);
    }

    public int getCount() {
        return declarationToDelete==null ? 
                0 : countDeclarationOccurrences();
    }
    
    int countRefinements() {
        int count = 0;
        for (PhasedUnit pu: getAllUnits()) {
            if (searchInFile(pu)) {
                count += countRefinements(pu.getCompilationUnit());
            }
        }
        if (searchInEditor()) {
            count += countRefinements(rootNode);
        }
        return count;
    }
    
    private int countRefinements(Tree.CompilationUnit cu) {
        FindDeletedRefinementsVisitor fdv =
                new FindDeletedRefinementsVisitor(declarationToDelete);
        cu.visit(fdv);
        return fdv.getDeclarationNodes().size();
    }
    
    int countUsages() {
        int count = 0;
        for (PhasedUnit pu: getAllUnits()) {
            if (searchInFile(pu)) {
                count += countUsages(pu.getCompilationUnit());
            }
        }
        if (searchInEditor()) {
            count += countUsages(rootNode);
        }
        return count;
    }
    
    private int countUsages(Tree.CompilationUnit cu) {
        FindDeletedReferencesVisitor frv =
                new FindDeletedReferencesVisitor(declarationToDelete);
        cu.visit(frv);
        return frv.getNodes().size();
    }
    
    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindDeletedReferencesVisitor frv =
                new FindDeletedReferencesVisitor(declarationToDelete);
        FindRefinementsVisitor fdv =
                new FindDeletedRefinementsVisitor(frv.getDeclaration());
        FindDocLinkReferencesVisitor fdlrv =
                new FindDocLinkReferencesVisitor(frv.getDeclaration());
        cu.visit(frv);
        cu.visit(fdv);
        cu.visit(fdlrv);
        return frv.getNodes().size() + 
                fdv.getDeclarationNodes().size() + 
                fdlrv.getLinks().size();
    }

    public String getName() {
        return "Safe Delete";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }
    
    public Declaration getDeclaration() {
        return declarationToDelete;
    }
    
    public Declaration getRefinedDeclaration() {
        return refinedDeclaration;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        CompositeChange change = new CompositeChange("Safe Delete");
        List<PhasedUnit> units = getAllUnits();
        if (searchInEditor()) {
            deleteInFile(change, newDocumentChange(), 
                    rootNode);
        }
        for (PhasedUnit pu: units) {
            if (searchInFile(pu)) {
                deleteInFile(change, newTextFileChange(pu), 
                        pu.getCompilationUnit());
            }
        }
        return change;
    }

    private void deleteInFile(CompositeChange change, 
            final TextChange tfc, Tree.CompilationUnit cu) {
        tfc.setEdit(new MultiTextEdit());
        new Visitor() {
            public void visit(Tree.Declaration that) {
                super.visit(that);
                Declaration d = that.getDeclarationModel();
                if (d.equals(declarationToDelete) ||
                        (deleteRefinements &&
                                d.refines(declarationToDelete))) {
                    tfc.addEdit(new DeleteEdit(that.getStartIndex(), 
                            that.getStopIndex()-that.getStartIndex()+1));
                }
            }
        }.visit(cu);
        if (tfc.getEdit().hasChildren()) {
            change.add(tfc);
        }
    }
    
    List<CeylonSearchMatch> getReferences() {
        List<CeylonSearchMatch> list = 
                new ArrayList<CeylonSearchMatch>();
        for (PhasedUnit pu: getAllUnits()) {
            if (searchInFile(pu)) {
                addReferences(pu.getCompilationUnit(), list, pu);
            }
        }
        if (searchInEditor()) {
            String relpath = editor.getParseController().getPhasedUnit().getPathRelativeToSrcDir();
            addReferences(rootNode, list, 
                    getProjectTypeChecker(project).getPhasedUnitFromRelativePath(relpath));
        }
        return list;
    }
    
    private void addReferences(Tree.CompilationUnit cu, 
            List<CeylonSearchMatch> list, PhasedUnit pu) {
        FindDeletedReferencesVisitor frv = 
                new FindDeletedReferencesVisitor(declarationToDelete);
        FindDeletedRefinementsVisitor fdv = 
                new FindDeletedRefinementsVisitor(frv.getDeclaration());
        FindDocLinkReferencesVisitor fdlrv = 
                new FindDocLinkReferencesVisitor(frv.getDeclaration());
        cu.visit(frv);
        cu.visit(fdv);
        cu.visit(fdlrv);
        for (Node node: frv.getNodes()) {
            list.add(findContainer(node, cu, pu));
        }
        for (Node node: fdv.getDeclarationNodes()) {
            list.add(findContainer(node, cu, pu));
        }
        for (Node node: fdlrv.getLinks()) {
            list.add(findContainer(node, cu, pu));
        }
    }

    private CeylonSearchMatch findContainer(Node node,
            Tree.CompilationUnit cu, PhasedUnit pu) {
        FindContainerVisitor fcv = new FindContainerVisitor(node);
        cu.visit(fcv);
        return new CeylonSearchMatch(fcv.getStatementOrArgument(), 
                pu.getUnitFile(), node);
    }

    public void setDeleteRefinements() {
        deleteRefinements = !deleteRefinements;
    }

}
