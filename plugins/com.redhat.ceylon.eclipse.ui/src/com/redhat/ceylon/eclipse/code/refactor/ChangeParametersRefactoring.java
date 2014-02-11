package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getNodeLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getNodeStartOffset;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedExplicitDeclaration;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class ChangeParametersRefactoring extends AbstractRefactoring {
    
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
                    Tree.PositionalArgumentList pal = that.getPositionalArgumentList();
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

    private List<Integer> order = new ArrayList<Integer>();
    
    private final Declaration declaration;
    private final List<Parameter> parameters;
    
    public Node getNode() {
        return node;
    }
    
    public List<Integer> getOrder() {
        return order;
    }

    public ChangeParametersRefactoring(ITextEditor editor) {
        super(editor);
        if (rootNode!=null) {
            Declaration refDec = getReferencedExplicitDeclaration(node, rootNode);
            if (refDec instanceof Functional) {
                refDec = refDec.getRefinedDeclaration();
                List<ParameterList> pls = ((Functional) refDec).getParameterLists();
                if (pls.isEmpty()) {
                    declaration = null;
                    parameters = null;
                }
                else {
                    declaration = refDec;
                    parameters = pls.get(0).getParameters();
                    for (int i=0; i<parameters.size(); i++) {
                        order.add(i);
                    }
                }
            }
            else {
                declaration = null;
                parameters = null;
            }
        }
        else {
            declaration = null;
            parameters = null;
        }
    }
    
    @Override
    public boolean isEnabled() {
        return declaration instanceof Functional &&
                project != null &&
                inSameProject(declaration);
    }

    public int getCount() {
        return declaration==null ? 
                0 : countDeclarationOccurrences();
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
        return "Change Parameters";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        boolean foundDefaulted = false;
        for (int index: order) {
            Parameter parameter = parameters.get(index);
            if (parameter.isDefaulted()) {
                foundDefaulted = true;
            }
            else {
                if (foundDefaulted) {
                    return createWarningStatus("defaulted parameters occur before required parameters");
                }
            }
        }
        return new RefactoringStatus();
    }

    public CompositeChange createChange(IProgressMonitor pm) throws CoreException,
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
            refactorInFile(dc, cc, editor.getParseController().getRootNode());
            pm.worked(i++);
        }
        pm.done();
        return cc;
    }

    private void refactorInFile(TextChange tfc, CompositeChange cc, Tree.CompilationUnit root) {
        tfc.setEdit(new MultiTextEdit());
        if (declaration!=null) {
            FindInvocationsVisitor fiv = new FindInvocationsVisitor(declaration);
            root.visit(fiv);
            for (Tree.PositionalArgumentList pal: fiv.getResults()) {
                List<Tree.PositionalArgument> pas = pal.getPositionalArguments();
                int size = pas.size();
                Tree.PositionalArgument[] args = new Tree.PositionalArgument[size];
                for (int i=0; i<size; i++) {
                    args[order.get(i)] = pas.get(i);
                }
                tfc.addEdit(reorderEdit(pal, args));
            }
            FindRefinementsVisitor frv = new FindRefinementsVisitor(declaration);
            root.visit(frv);
            for (Tree.Declaration decNode: frv.getDeclarationNodes()) {
                Tree.ParameterList pl;
                if (decNode instanceof Tree.AnyMethod) {
                    pl = ((Tree.AnyMethod) decNode).getParameterLists().get(0);
                }
                else if (decNode instanceof Tree.AnyClass) {
                    pl = ((Tree.AnyClass) decNode).getParameterList();
                }
                else {
                    continue;
                }
                List<Tree.Parameter> ps = pl.getParameters();
                int size = ps.size();
                Tree.Parameter[] params = new Tree.Parameter[size];
                for (int i=0; i<size; i++) {
                    params[order.get(i)] = ps.get(i);
                }
                tfc.addEdit(reorderEdit(pl, params));
            }
            FindArgumentsVisitor fav = new FindArgumentsVisitor(declaration);
            root.visit(fav);
            for (Tree.MethodArgument decNode: fav.getResults()) {
                Tree.ParameterList pl = decNode.getParameterLists().get(0);
                List<Tree.Parameter> ps = pl.getParameters();
                int size = ps.size();
                Tree.Parameter[] params = new Tree.Parameter[size];
                for (int i=0; i<size; i++) {
                    params[order.get(i)] = ps.get(i);
                }
                tfc.addEdit(reorderEdit(pl, params));
            }
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

    public ReplaceEdit reorderEdit(Node list, Node[] elements) {
        StringBuilder sb = new StringBuilder("(");
        for (Node elem: elements) {
            sb.append(toString(elem)).append(", ");
        }
        sb.setLength(sb.length()-2);
        sb.append(")");
        return new ReplaceEdit(getNodeStartOffset(list), 
                getNodeLength(list), 
                sb.toString());
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
}
