package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedExplicitDeclaration;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
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
    
    private class FindDeletedReferencesVisitor 
            extends FindReferencesVisitor {
        private FindDeletedReferencesVisitor(Declaration declaration) {
            super(declaration);
        }
        @Override
        protected boolean isReference(Declaration ref) {
            return ref!=null && ref.equals(getDeclaration());
        }
        @Override
        public void visit(Tree.Declaration that) {
            if (!that.getDeclarationModel()
                    .equals(declarationToDelete)) {
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
                    super.isRefinement(dec);
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
        List<PhasedUnit> units = getAllUnits();
        if (rootNode.getUnit().equals(declarationToDelete.getUnit()) &&
                searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            dc.setEdit(getDeleteEdit(rootNode));
            return dc;
        }
        for (PhasedUnit pu: units) {
            if (pu.getUnit().equals(declarationToDelete.getUnit()) && 
                    searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange(pu);
                tfc.setEdit(getDeleteEdit(pu.getCompilationUnit()));
                return tfc;
            }
        }
        return null;
    }

    private DeleteEdit getDeleteEdit(Tree.CompilationUnit compilationUnit) {
        Node node = getReferencedNode(declarationToDelete, compilationUnit);
        return new DeleteEdit(node.getStartIndex(), 
                node.getStopIndex()-node.getStartIndex()+1);
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
            addReferences(rootNode, list, 
                    editor.getParseController().getPhasedUnit());
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

}
