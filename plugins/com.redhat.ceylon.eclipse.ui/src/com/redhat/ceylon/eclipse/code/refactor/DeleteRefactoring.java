package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedExplicitDeclaration;

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
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
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
            Declaration declaration = 
                    (Declaration) getDeclaration();
            if (ref==null) {
                return false;
            }
            else if (ref.equals(declaration)) {
                if (!declaration.isActual() || 
                        declaration.equals(refinedDeclaration)) {
                    return true;
                }
                else {
                    if (declaration instanceof TypedDeclaration &&
                            refinedDeclaration instanceof TypedDeclaration) {
                        //if it's a reference to a refining method or value
                        //we can safely delete unless it refines the return type
                        ProducedType type = 
                                ((TypedDeclaration) declaration).getType();
                        ProducedType refinedType = 
                                ((TypedDeclaration) refinedDeclaration).getType();
                        return type!=null && refinedType!=null && 
                                !type.isExactly(refinedType);
                    }
                    else {
                        return true;
                    }
                }
            }
            else {
                return deleteRefinements &&
                        ref.refines(declaration);
            }
        }
        @Override
        public void visit(Tree.InitializerParameter that) {
        	Tree.SpecifierExpression sie = that.getSpecifierExpression();
        	if (sie!=null) {
        		sie.visit(this);
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
        @Override
        public void visit(Tree.NamedArgument that) {
            if (!deleteRefinements &&
                    isReference(that.getParameter())) {
                getNodes().add(that);
            }
            else {
                //the supertype doesn't test deleteRefinements
                getNodes().remove(that);
            }
            super.visit(that);
        }
        @Override
        public void visit(Tree.PositionalArgument that) {
            if (!deleteRefinements &&
                    isReference(that.getParameter())) {
                getNodes().add(that);
            }
            super.visit(that);
        }
        @Override
        public void visit(Tree.SequencedArgument that) {
            if (!deleteRefinements &&
                    isReference(that.getParameter())) {
                getNodes().add(that);
            }
            super.visit(that);
        }
        @Override
        public void visit(Tree.AnyClass that) {
            super.visit(that);
            handleParameterRefinement(that);
        }
        @Override
        public void visit(Tree.AnyMethod that) {
            super.visit(that);
            handleParameterRefinement(that);
        }
        private void handleParameterRefinement(Tree.Declaration that) {
            Declaration declaration = (Declaration) getDeclaration();
            if (declaration.isParameter()) {
                Declaration parameterized = 
                        (Declaration) declaration.getContainer();
                Declaration current = that.getDeclarationModel();
                if (!parameterized.equals(current)) {
                    if (parameterized.getRefinedDeclaration().equals(current)) {
                        getNodes().add(that);
                    }
                    if (current.getRefinedDeclaration().equals(parameterized)) {
                        getNodes().add(that);
                    }
                }
            }
        }
        @Override
        public void visit(Tree.Import that) {}
    }
    
    //TODO: copy/pasted from RenameRefactoring!
    class FindDocLinkReferencesVisitor extends Visitor {
        private Declaration declaration;
        private List<Tree.DocLink> links = 
                new ArrayList<Tree.DocLink>();
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
                    declarationToDelete.isActual() &&
                    !declarationToDelete.isFormal() &&
                    refinedDeclaration.isFormal());
        }
    }
    
    private final Declaration refinedDeclaration;
    private final Declaration declarationToDelete;
    
    public Node getNode() {
        return node;
    }

    public DeleteRefactoring(IEditorPart editor) {
        super(editor);
        if (rootNode!=null) {
            declarationToDelete = (Declaration) 
                    getReferencedExplicitDeclaration(node, rootNode);
            if (declarationToDelete!=null) {
                refinedDeclaration = 
                        declarationToDelete.getRefinedDeclaration();
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
        Declaration declaration = (Declaration) frv.getDeclaration();
        FindRefinementsVisitor fdv =
                new FindDeletedRefinementsVisitor(declaration);
        FindDocLinkReferencesVisitor fdlrv =
                new FindDocLinkReferencesVisitor(declaration);
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
            deleteInFile(change, newDocumentChange(), rootNode);
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
            private void deleteArg(final TextChange tfc, Node that,
                    Declaration d, int start, int stop) {
                if (deleteRefinements &&
                        d.equals(declarationToDelete)) {
                    tfc.addEdit(new DeleteEdit(start, stop-start));
                }
            }
            @Override
            public void visit(Tree.NamedArgument that) {
                Parameter parameter = that.getParameter();
                if (parameter!=null) {
                    deleteArg(tfc, that, parameter.getModel(),
                            that.getStartIndex(), 
                            that.getStopIndex()+1);
                    super.visit(that);
                }
            }
            @Override
            public void visit(Tree.SequencedArgument that) {
                Parameter parameter = that.getParameter();
                if (parameter!=null) {
                    deleteArg(tfc, that, parameter.getModel(),
                            that.getStartIndex(), 
                            that.getStopIndex()+1);
                    super.visit(that);
                }
            }
            @Override
            public void visit(Tree.PositionalArgumentList that) {
                List<Tree.PositionalArgument> args = that.getPositionalArguments();
                for (int i=0; i<args.size(); i++) {
                    Tree.PositionalArgument arg = args.get(i);
                    Parameter parameter = arg.getParameter();
                    if (parameter!=null) {
                        int start, stop;
                        if (i>0) {
                            start = args.get(i-1).getStopIndex()+1;
                            stop = arg.getStopIndex()+1;
                        }
                        else if (i<args.size()-1) {
                            start = arg.getStartIndex();
                            stop = args.get(i+1).getStartIndex();
                        }
                        else {
                            start = arg.getStartIndex();
                            stop = arg.getStopIndex()+1;
                        }
                        deleteArg(tfc, that, parameter.getModel(),
                                start, stop);
                    }
                }
                super.visit(that);
            }
			private void deleteDec(final TextChange tfc, Node that,
					Declaration d) {
				if (d.equals(declarationToDelete) ||
                        (deleteRefinements &&
                                d.refines(declarationToDelete))) {
                    tfc.addEdit(new DeleteEdit(that.getStartIndex(), 
                            that.getStopIndex()-that.getStartIndex()+1));
                }
			}
            @Override
            public void visit(Tree.Declaration that) {
                deleteDec(tfc, that, that.getDeclarationModel());
                super.visit(that);
            }
            @Override
            public void visit(Tree.SpecifierStatement that) {
            	if (that.getRefinement()) {
            		deleteDec(tfc, that, that.getDeclaration());
            	}
                super.visit(that);
            }
            @Override
            public void visit(Tree.ParameterList that) {
                List<Tree.Parameter> parameters = that.getParameters();
                for (int i=0; i<parameters.size(); i++) {
                	Tree.Parameter param = parameters.get(i);
                	Declaration d = param.getParameterModel().getModel();
                	if (d.equals(declarationToDelete)) {
                		int start, stop;
                		if (i>0) {
                			Tree.Parameter previous = parameters.get(i-1);
                			start = previous.getStopIndex()+1;
                			stop = param.getStopIndex()+1;
                		}
                		else if (i<parameters.size()-1) {
                			Tree.Parameter next = parameters.get(i+1);
                			start = param.getStartIndex();
                			stop = next.getStartIndex();
                		}
                		else {
                			start = param.getStartIndex();
                			stop = param.getStopIndex()+1;
                		}
                		tfc.addEdit(new DeleteEdit(start, stop-start));
                		return;
                	}
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.Import that) {
                Tree.ImportMemberOrTypeList list = that.getImportMemberOrTypeList();
                if (list!=null && list.getImportMemberOrTypes().size()==1) {
                    Tree.ImportMemberOrType imp = list.getImportMemberOrTypes().get(0);
                    Declaration d = imp.getDeclarationModel();
                    if (d.equals(declarationToDelete)) {
                        tfc.addEdit(new DeleteEdit(that.getStartIndex(), 
                                that.getStopIndex()-that.getStartIndex()+1));
                        return;
                    }
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.ImportMemberOrTypeList that) {
                List<Tree.ImportMemberOrType> imports = that.getImportMemberOrTypes();
                for (int i=0; i<imports.size(); i++) {
                    Tree.ImportMemberOrType imp = imports.get(i);
                    Declaration d = imp.getDeclarationModel();
                    if (d.equals(declarationToDelete)) {
                        int start, stop;
                        if (i>0) {
                            Tree.ImportMemberOrType previous = imports.get(i-1);
                            start = previous.getStopIndex()+1;
                            stop = imp.getStopIndex()+1;
                        }
                        else if (i<imports.size()-1) {
                            Tree.ImportMemberOrType next = imports.get(i+1);
                            start = imp.getStartIndex();
                            stop = next.getStartIndex();
                        }
                        else {
                            start = imp.getStartIndex();
                            stop = imp.getStopIndex()+1;
                        }
                        tfc.addEdit(new DeleteEdit(start, stop-start));
                        return;
                    }
                }
                super.visit(that);
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
        Declaration declaration = (Declaration) frv.getDeclaration();
        FindDeletedRefinementsVisitor fdv = 
                new FindDeletedRefinementsVisitor(declaration);
        FindDocLinkReferencesVisitor fdlrv = 
                new FindDocLinkReferencesVisitor(declaration);
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
        return new CeylonSearchMatch(node, 
                fcv.getStatementOrArgument(), pu.getUnitFile());
    }

    public void setDeleteRefinements() {
        deleteRefinements = !deleteRefinements;
    }

}
