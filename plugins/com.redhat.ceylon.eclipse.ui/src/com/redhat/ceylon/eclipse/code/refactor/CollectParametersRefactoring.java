package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getEndOffset;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class CollectParametersRefactoring extends AbstractRefactoring {
    
    private Method declaration;
    private Node body;
    private List<Tree.Parameter> parameters = 
            new ArrayList<Tree.Parameter>();
    private List<MethodOrValue> models = 
            new ArrayList<MethodOrValue>();
    
    private class FindParametersVisitor extends Visitor {
        @Override
        public void visit(Tree.AnyMethod that) {
            for (Tree.ParameterList pl: that.getParameterLists()) {
                IRegion selection = editor.getSelection();
                int start = selection.getOffset();
                int end = selection.getOffset() + selection.getLength();
                if (start>pl.getStartIndex() &&
                    start<=pl.getStopIndex()) {
                    declaration = that.getDeclarationModel();
                    if (that instanceof Tree.MethodDefinition) {
                        body = ((Tree.MethodDefinition) that).getBlock();
                    }
                    else if (that instanceof Tree.MethodDeclaration) {
                        body = ((Tree.MethodDeclaration) that).getSpecifierExpression();
                    }
                    for (Tree.Parameter p: pl.getParameters()) {
                        if (p.getStartIndex()>=start && p.getStopIndex()<end) {
                            parameters.add(p);
                            models.add(p.getParameterModel().getModel());
                        }
                    }
                }
            }
        }
        
    }
    
    private static class FindInvocationsVisitor extends Visitor {
        private Declaration declaration;
        private final Set<Tree.PositionalArgumentList> results = 
                new HashSet<Tree.PositionalArgumentList>();
        Set<Tree.PositionalArgumentList> getResults() {
            return results;
        }
        private FindInvocationsVisitor(Declaration declaration) {
            this.declaration=declaration;
        }
        @Override
        public void visit(Tree.InvocationExpression that) {
            super.visit(that);
            Tree.Primary primary = that.getPrimary();
            if (primary instanceof Tree.MemberOrTypeExpression) {
                if (((Tree.MemberOrTypeExpression) primary).getDeclaration()
                        .equals(declaration)) {
                    Tree.PositionalArgumentList pal = 
                            that.getPositionalArgumentList();
                    if (pal!=null) {
                        results.add(pal);
                    }
                }
            }
        }
    }
    
    private static class FindArgumentsVisitor extends Visitor {
        private Declaration declaration;
        private final Set<Tree.MethodArgument> results = 
                new HashSet<Tree.MethodArgument>();
        Set<Tree.MethodArgument> getResults() {
            return results;
        }
        private FindArgumentsVisitor(Declaration declaration) {
            this.declaration=declaration;
        }
        @Override
        public void visit(Tree.MethodArgument that) {
            super.visit(that);
            Parameter p = that.getParameter();
            if (p!=null && p.getModel().equals(declaration)) {
                results.add(that);
            }
        }
    }

    private String newName;
        
    public String getNewName() {
        return newName;
    }
    
    public void setNewName(String newName) {
        this.newName = newName;
    }
    
    public CollectParametersRefactoring(ITextEditor editor) {
        super(editor);
        new FindParametersVisitor().visit(rootNode);
        if (declaration!=null) {
            newName = Character.toUpperCase(declaration.getName().charAt(0))
                    + declaration.getName().substring(1);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return declaration!=null && body!=null && 
                !parameters.isEmpty();
    }
    
    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindInvocationsVisitor frv = new FindInvocationsVisitor(declaration);
        FindRefinementsVisitor fdv = new FindRefinementsVisitor(declaration);
        FindArgumentsVisitor fav = new FindArgumentsVisitor(declaration);
        cu.visit(frv);
        cu.visit(fdv);
        cu.visit(fav);
        return frv.getResults().size() + 
                fdv.getDeclarationNodes().size() + 
                fav.getResults().size();
    }

    public String getName() {
        return "Collect Parameters";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, 
                   OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, 
                   OperationCanceledException {
        return new RefactoringStatus();
    }

    public CompositeChange createChange(IProgressMonitor pm) 
            throws CoreException,
                   OperationCanceledException {
        List<PhasedUnit> units = getAllUnits();
        pm.beginTask(getName(), units.size());
        CompositeChange cc = new CompositeChange(getName());
        int i=0;
        for (PhasedUnit pu: units) {
            if (searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange(pu);
                refactorInFile(tfc, cc, pu.getCompilationUnit());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            refactorInFile(dc, cc, 
                    editor.getParseController().getRootNode());
            pm.worked(i++);
        }
        pm.done();
        return cc;
    }

    private void refactorInFile(final TextChange tfc, CompositeChange cc, 
            Tree.CompilationUnit root) {
        tfc.setEdit(new MultiTextEdit());
        if (declaration!=null) {
            FindInvocationsVisitor fiv = new FindInvocationsVisitor(declaration);
            root.visit(fiv);
            for (Tree.PositionalArgumentList pal: fiv.getResults()) {
                List<Tree.PositionalArgument> pas = pal.getPositionalArguments();
                for (Tree.PositionalArgument pa: pas) {
                    MethodOrValue model = pa.getParameter().getModel();
                    if (model.equals(models.get(0))) {
                        tfc.addEdit(new InsertEdit(pa.getStartIndex(), newName + "("));
                    }
                    if (model.equals(models.get(models.size()-1))) {
                        int loc;
                        if (pa==pas.get(pas.size()-1)) {
                            loc = pa.getStopIndex()+1;
                        }
                        else {
                            loc = pa.getStopIndex();
                        }
                        tfc.addEdit(new InsertEdit(loc, ")"));
                    }
                }
            }
            FindRefinementsVisitor frv = new FindRefinementsVisitor(declaration);
            root.visit(frv);
            final String paramName = 
                    Character.toLowerCase(newName.charAt(0)) + newName.substring(1);
            for (Tree.StatementOrArgument decNode: frv.getDeclarationNodes()) {
                Tree.ParameterList pl;
                if (decNode instanceof Tree.AnyMethod) {
                    pl = ((Tree.AnyMethod) decNode).getParameterLists().get(0);
                }
                else if (decNode instanceof Tree.AnyClass) {
                    pl = ((Tree.AnyClass) decNode).getParameterList();
                }
                else if (decNode instanceof Tree.SpecifierStatement) {
                    Tree.Term bme = ((Tree.SpecifierStatement) decNode).getBaseMemberExpression();
                    if (bme instanceof Tree.ParameterizedExpression) {
                        pl = ((Tree.ParameterizedExpression) bme).getParameterLists().get(0);
                    }
                    else {
                        continue;
                    }
                }
                else {
                    continue;
                }
                List<Tree.Parameter> ps = pl.getParameters();
                int start=-1;
                for (Tree.Parameter pa: ps) {
                    MethodOrValue model = pa.getParameterModel().getModel();
                    if (model.equals(models.get(0))) {
                        tfc.addEdit(new InsertEdit(getStartOffset(pa), 
                                newName + " " + paramName));
                        start = getStartOffset(pa);
                    }
                    if (model.equals(models.get(models.size()-1))) {
                        tfc.addEdit(new DeleteEdit(start, getEndOffset(pa)-start));
                    }
                }
            }
            FindArgumentsVisitor fav = new FindArgumentsVisitor(declaration);
            root.visit(fav);
            for (Tree.MethodArgument decNode: fav.getResults()) {
                Tree.ParameterList pl = decNode.getParameterLists().get(0);
                List<Tree.Parameter> ps = pl.getParameters();
                int start=-1;
                for (Tree.Parameter pa: ps) {
                    MethodOrValue model = pa.getParameterModel().getModel();
                    if (model.equals(models.get(0))) {
                        tfc.addEdit(new InsertEdit(getStartOffset(pa), 
                                newName + " " + paramName));
                        start = getStartOffset(pa);
                    }
                    if (model.equals(models.get(models.size()-1))) {
                        tfc.addEdit(new DeleteEdit(start, getEndOffset(pa)-start));
                    }
                }
            }
            if (body.getUnit().equals(root.getUnit())) {
                body.visit(new Visitor() {
                    @Override
                    public void visit(Tree.BaseMemberExpression that) {
                        super.visit(that);
                        if (models.contains(that.getDeclaration())) {
                            tfc.addEdit(new InsertEdit(that.getStartIndex(), 
                                    paramName + "."));
                        }
                    }
                });
            }
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
}
