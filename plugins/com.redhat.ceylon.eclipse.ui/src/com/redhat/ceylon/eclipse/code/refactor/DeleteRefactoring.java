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
import com.redhat.ceylon.eclipse.code.search.CeylonElement;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class DeleteRefactoring extends AbstractRefactoring {
    
    private static class FindReferencesVisitor 
            extends FindReferenceVisitor {
        private FindReferencesVisitor(Declaration declaration) {
            super(declaration);
        }
        @Override
        protected boolean isReference(Declaration ref) {
            return super.isReference(ref) ||
                    ref!=null && ref.refines(getDeclaration());
        }
        @Override
        protected boolean isReference(Declaration ref, String id) {
            return isReference(ref) && id!=null &&
                    getDeclaration().getName().equals(id); //TODO: really lame way to tell if it's an alias!
        }
    }
    
    private final Declaration declaration;
    
    public Node getNode() {
        return node;
    }

    public DeleteRefactoring(ITextEditor editor) {
        super(editor);
        if (rootNode!=null) {
            Declaration refDec = getReferencedExplicitDeclaration(node, rootNode);
            if (refDec!=null) {
                declaration = refDec.getRefinedDeclaration();
            }
            else {
                declaration = null;
            }
        }
        else {
            declaration = null;
        }
    }
    
    @Override
    public boolean isEnabled() {
        return declaration!=null &&
                project != null &&
                inSameProject(declaration);
    }

    public int getCount() {
        return declaration==null ? 0 : countDeclarationOccurrences();
    }
    
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
    }

    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindReferencesVisitor frv = 
                new FindReferencesVisitor(declaration);
        FindRefinementsVisitor fdv = 
                new FindRefinementsVisitor(frv.getDeclaration()) {
            @Override
            protected boolean isRefinement(Declaration dec) {
                return !dec.equals(declaration) && super.isRefinement(dec);
            }
        };
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
    
    /*public List<Node> getNodesToRename(Tree.CompilationUnit root) {
        ArrayList<Node> list = new ArrayList<Node>();
        FindReferencesVisitor frv = new FindReferencesVisitor(declaration);
        root.visit(frv);
        list.addAll(frv.getNodes());
        FindRefinementsVisitor fdv = new FindRefinementsVisitor(frv.getDeclaration());
        root.visit(fdv);
        list.addAll(fdv.getDeclarationNodes());
        return list;
    }
    
    public List<Region> getStringsToReplace(Tree.CompilationUnit root) {
        final List<Region> result = new ArrayList<Region>();
        new Visitor() {
            private void visitIt(String name, int offset, Declaration dec) {
                if (dec!=null && dec.equals(declaration)) {
                    result.add(new Region(offset, name.length()));
                }
            }
            @Override
            public void visit(Tree.DocLink that) {
                String text = that.getText();
                Integer offset = that.getStartIndex();
                
                int pipeIndex = text.indexOf("|");
                if (pipeIndex > -1) {
                    text = text.substring(pipeIndex + 1);
                    offset += pipeIndex + 1;
                }
                
                int scopeIndex = text.indexOf("::");
                int start = scopeIndex<0 ? 0 : scopeIndex+2;
                Declaration base = that.getBase();
                if (base!=null) {
                    int index = text.indexOf('.', start);
                    String name = index<0 ? 
                            text.substring(start) : 
                            text.substring(start, index);
                    visitIt(name, offset+start, base);
                    start = index+1;
                    int i=0;
                    List<Declaration> qualified = that.getQualified();
                    if (qualified!=null) {
                        while (start>0 && i<qualified.size()) {
                            index = text.indexOf('.', start);
                            name = index<0 ? 
                                    text.substring(start) : 
                                    text.substring(start, index);
                            visitIt(name, offset+start, qualified.get(i++));
                            start = index+1;
                        }
                    }
                }
            }
        }.visit(root);
        return result;
    }*/

    public Declaration getDeclaration() {
        return declaration;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        List<PhasedUnit> units = getAllUnits();
        if (rootNode.getUnit().equals(declaration.getUnit()) &&
                searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            dc.setEdit(getDeleteEdit(rootNode));
            return dc;
        }
        for (PhasedUnit pu: units) {
            if (pu.getUnit().equals(declaration.getUnit()) && 
                    searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange(pu);
                tfc.setEdit(getDeleteEdit(pu.getCompilationUnit()));
                return tfc;
            }
        }
        return null;
    }

    private DeleteEdit getDeleteEdit(Tree.CompilationUnit compilationUnit) {
        Node node = getReferencedNode(declaration, compilationUnit);
        return new DeleteEdit(node.getStartIndex(), 
                node.getStopIndex()-node.getStartIndex()+1);
    }
    
    List<CeylonElement> getReferences() {
        List<CeylonElement> list = 
                new ArrayList<CeylonElement>();
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
            List<CeylonElement> list, PhasedUnit pu) {
        FindReferencesVisitor frv = 
                new FindReferencesVisitor(declaration);
        FindRefinementsVisitor fdv = 
                new FindRefinementsVisitor(frv.getDeclaration()) {
            @Override
            protected boolean isRefinement(Declaration dec) {
                return !dec.equals(declaration) && super.isRefinement(dec);
            }
        };
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

    private CeylonElement findContainer(Node node,
            Tree.CompilationUnit cu, PhasedUnit pu) {
        FindContainerVisitor fcv = new FindContainerVisitor(node);
        cu.visit(fcv);
        return new CeylonElement(fcv.getStatementOrArgument(), 
                pu.getUnitFile(), node.getToken().getLine());
    }

}
