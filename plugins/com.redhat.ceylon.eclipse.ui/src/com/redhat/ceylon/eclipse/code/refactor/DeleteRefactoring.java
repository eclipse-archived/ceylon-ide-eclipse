package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedExplicitDeclaration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.search.CeylonSearchMatch;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;

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
                        TypedDeclaration typedDeclaration = 
                                (TypedDeclaration) declaration;
                        TypedDeclaration refinedTypedDeclaration = 
                                (TypedDeclaration) refinedDeclaration;
                        Type type = 
                                typedDeclaration.getType();
                        Type refinedType = 
                                refinedTypedDeclaration.getType();
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
        	Tree.SpecifierExpression sie = 
        	        that.getSpecifierExpression();
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
            Declaration declaration = 
                    (Declaration) getDeclaration();
            if (declaration.isParameter()) {
                Declaration parameterized = 
                        (Declaration) 
                            declaration.getContainer();
                Declaration current = that.getDeclarationModel();
                if (!parameterized.equals(current)) {
                    if (parameterized.getRefinedDeclaration()
                            .equals(current)) {
                        getNodes().add(that);
                    }
                    if (current.getRefinedDeclaration()
                            .equals(parameterized)) {
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
            Referenceable ref = 
                    getReferencedExplicitDeclaration(node, rootNode);
            if (ref instanceof Declaration) {
                declarationToDelete = (Declaration) ref;
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
        else {
            declarationToDelete = null;
            refinedDeclaration = null;
        }
    }
    
    @Override
    public boolean getEnabled() {
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
        if (visibleOutsideUnit()) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu)) {
                    count += countRefinements(pu.getCompilationUnit());
                }
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
        if (visibleOutsideUnit()) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu)) {
                    count += countUsages(pu.getCompilationUnit());
                }
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
        Declaration declaration = 
                (Declaration) frv.getDeclaration();
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
        CompositeChange change = 
                new CompositeChange("Safe Delete");
        if (searchInEditor()) {
            deleteInFile(change, newDocumentChange(), rootNode);
        }
        if (visibleOutsideUnit()) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu)) {
                    ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = 
                            (ProjectPhasedUnit<IProject,IResource,IFolder,IFile>)pu;
                    deleteInFile(change, 
                            newTextFileChange(ppu), 
                            pu.getCompilationUnit());
                }
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
                            that.getEndIndex());
                    super.visit(that);
                }
            }
            @Override
            public void visit(Tree.SequencedArgument that) {
                Parameter parameter = that.getParameter();
                if (parameter!=null) {
                    deleteArg(tfc, that, parameter.getModel(),
                            that.getStartIndex(), 
                            that.getEndIndex());
                    super.visit(that);
                }
            }
            @Override
            public void visit(Tree.PositionalArgumentList that) {
                List<Tree.PositionalArgument> args = 
                        that.getPositionalArguments();
                for (int i=0; i<args.size(); i++) {
                    Tree.PositionalArgument arg = args.get(i);
                    Parameter parameter = arg.getParameter();
                    if (parameter!=null) {
                        int start, stop;
                        if (i>0) {
                            start = args.get(i-1).getEndIndex();
                            stop = arg.getEndIndex();
                        }
                        else if (i<args.size()-1) {
                            start = arg.getStartIndex();
                            stop = args.get(i+1).getStartIndex();
                        }
                        else {
                            start = arg.getStartIndex();
                            stop = arg.getEndIndex();
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
                            that.getDistance()));
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
                List<Tree.Parameter> parameters = 
                        that.getParameters();
                for (int i=0; i<parameters.size(); i++) {
                	Tree.Parameter param = parameters.get(i);
                	Declaration d = 
                	        param.getParameterModel()
                	            .getModel();
                	if (d.equals(declarationToDelete)) {
                		int start, stop;
                		if (i>0) {
                			Tree.Parameter previous = 
                			        parameters.get(i-1);
                			start = previous.getEndIndex();
                			stop = param.getEndIndex();
                		}
                		else if (i<parameters.size()-1) {
                			Tree.Parameter next = 
                			        parameters.get(i+1);
                			start = param.getStartIndex();
                			stop = next.getStartIndex();
                		}
                		else {
                			start = param.getStartIndex();
                			stop = param.getEndIndex();
                		}
                		tfc.addEdit(new DeleteEdit(start, stop-start));
                		return;
                	}
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.Import that) {
                Tree.ImportMemberOrTypeList list = 
                        that.getImportMemberOrTypeList();
                if (list!=null && 
                        list.getImportMemberOrTypes().size()==1) {
                    Tree.ImportMemberOrType imp = 
                            list.getImportMemberOrTypes()
                                .get(0);
                    Declaration d = imp.getDeclarationModel();
                    if (d.equals(declarationToDelete)) {
                        tfc.addEdit(new DeleteEdit(that.getStartIndex(), 
                                that.getDistance()));
                        return;
                    }
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.ImportMemberOrTypeList that) {
                List<Tree.ImportMemberOrType> imports = 
                        that.getImportMemberOrTypes();
                for (int i=0; i<imports.size(); i++) {
                    Tree.ImportMemberOrType imp = 
                            imports.get(i);
                    Declaration d = imp.getDeclarationModel();
                    if (d.equals(declarationToDelete)) {
                        int start, stop;
                        if (i>0) {
                            Tree.ImportMemberOrType previous = 
                                    imports.get(i-1);
                            start = previous.getEndIndex();
                            stop = imp.getEndIndex();
                        }
                        else if (i<imports.size()-1) {
                            Tree.ImportMemberOrType next = 
                                    imports.get(i+1);
                            start = imp.getStartIndex();
                            stop = next.getStartIndex();
                        }
                        else {
                            start = imp.getStartIndex();
                            stop = imp.getEndIndex();
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
        if (visibleOutsideUnit()) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu)) {
                    addReferences(pu.getCompilationUnit(), list, pu);
                }
            }
        }
        if (searchInEditor()) {
            String relpath = 
                    editor.getParseController()
                        .getLastPhasedUnit()
                        .getPathRelativeToSrcDir();
            addReferences(rootNode, list, 
                    getProjectTypeChecker(project)
                        .getPhasedUnitFromRelativePath(relpath));
        }
        return list;
    }
    
    private void addReferences(Tree.CompilationUnit cu, 
            List<CeylonSearchMatch> list, PhasedUnit pu) {
        FindDeletedReferencesVisitor frv = 
                new FindDeletedReferencesVisitor(declarationToDelete);
        Declaration declaration = 
                (Declaration) frv.getDeclaration();
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
        return CeylonSearchMatch.create(node, cu, pu.getUnitFile());
    }

    public void setDeleteRefinements() {
        deleteRefinements = !deleteRefinements;
    }

    @Override
    public boolean visibleOutsideUnit() {
        if (declarationToDelete==null) {
            return false;
        }
        if (declarationToDelete.isToplevel() ||
                declarationToDelete.isShared()) {
            return true;
        }
        if (declarationToDelete.isParameter()) {
            FunctionOrValue fov = 
                    (FunctionOrValue) declarationToDelete;
            Declaration container = 
                    (Declaration) fov.getContainer();
            if (container.isToplevel() || 
                container.isShared()) {
                return true;
            }
        }
        return false;
    }

}
