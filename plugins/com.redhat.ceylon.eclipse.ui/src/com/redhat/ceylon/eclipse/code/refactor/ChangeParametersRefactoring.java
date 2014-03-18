package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedExplicitDeclaration;
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
import org.eclipse.text.edits.InsertEdit;
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
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;

public class ChangeParametersRefactoring extends AbstractRefactoring {
    
    private static class FindInvocationsVisitor extends Visitor {
        private Declaration declaration;
        private final Set<Tree.PositionalArgumentList> posResults = 
                new HashSet<Tree.PositionalArgumentList>();
        private final Set<Tree.NamedArgumentList> namedResults = 
                new HashSet<Tree.NamedArgumentList>();
        Set<Tree.PositionalArgumentList> getPositionalArgLists() {
            return posResults;
        }
        Set<Tree.NamedArgumentList> getNamedArgLists() {
            return namedResults;
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
                        posResults.add(pal);
                    }
                    Tree.NamedArgumentList nal = that.getNamedArgumentList();
                    if (nal!=null) {
                        namedResults.add(nal);
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
    private List<Boolean> defaulted = new ArrayList<Boolean>();
    
    private final Declaration declaration;
    private final List<Parameter> parameters;
    
    public List<Parameter> getParameters() {
        return parameters;
    }

    public Node getNode() {
        return node;
    }
    
    public List<Integer> getOrder() {
        return order;
    }
    
    public List<Boolean> getDefaulted() {
        return defaulted;
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
                        defaulted.add(parameters.get(i).isDefaulted());
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
        return frv.getPositionalArgLists().size() + 
                fdv.getDeclarationNodes().size() + 
                fav.getResults().size();
    }

    public String getName() {
        return "Change Parameter List";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        boolean foundDefaulted = false;
        for (int index=0; index<defaulted.size(); index++) {
            if (defaulted.get(index)) {
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
        Tree.Expression[] defaultArgs = getDefaultArgs();
        
        CompositeChange cc = new CompositeChange(getName());
        int i=0;
        for (PhasedUnit pu: units) {
            if (searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange(pu);
                refactorInFile(defaultArgs, tfc, cc, 
                        pu.getCompilationUnit());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            refactorInFile(defaultArgs, dc, cc, 
                    editor.getParseController().getRootNode());
            pm.worked(i++);
        }
        pm.done();
        return cc;
    }

    private Tree.Expression[] getDefaultArgs() {
        Node decNode = Nodes.getReferencedNode(declaration, 
                editor.getParseController());
        Tree.ParameterList pl=null;
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
        }
        if (pl!=null) {
            List<Tree.Parameter> ps = pl.getParameters();
            int size = ps.size();
            Tree.Expression[] defaultArgs = new Tree.Expression[size];
            for (int i=0; i<size; i++) {
                Tree.Parameter p = ps.get(i);
                if (p instanceof Tree.ValueParameterDeclaration) {
                    Tree.AttributeDeclaration att = (Tree.AttributeDeclaration) 
                            ((Tree.ValueParameterDeclaration) p).getTypedDeclaration();
                    Tree.SpecifierOrInitializerExpression sie = 
                            att.getSpecifierOrInitializerExpression();
                    if (sie!=null) {
                        defaultArgs[order.indexOf(i)] = sie.getExpression();
                    }
                }
                if (p instanceof Tree.InitializerParameter) {
                    Tree.SpecifierExpression se = 
                            ((Tree.InitializerParameter)p).getSpecifierExpression();
                    if (se!=null) {
                        defaultArgs[order.indexOf(i)] = se.getExpression();
                    }
                }
            }
            return defaultArgs;
        }
        else {
            return null;
        }
    }

    private void refactorInFile(Expression[] defaultArgs, TextChange tfc, 
            CompositeChange cc, Tree.CompilationUnit root) {
        tfc.setEdit(new MultiTextEdit());
        if (declaration!=null) {
            int requiredParams=-1;
            for (int i=0; i<defaulted.size(); i++) {
                if (!defaulted.get(i)) {
                    if (i>requiredParams) {
                        requiredParams = i;
                    }
                }
            }
            FindInvocationsVisitor fiv = 
                    new FindInvocationsVisitor(declaration);
            root.visit(fiv);
            for (Tree.PositionalArgumentList pal: fiv.getPositionalArgLists()) {
                List<Tree.PositionalArgument> pas = pal.getPositionalArguments();
                int existingArgs=0;
                for (int i=0; i<pas.size(); i++) {
                    Parameter p = pas.get(i).getParameter();
                    if (p!=null) {
                        int newLoc = order.indexOf(i);
                        if (newLoc>existingArgs) {
                            existingArgs = newLoc;
                        }
                    }
                }
                Tree.PositionalArgument[] args = 
                        new Tree.PositionalArgument[Math.max(requiredParams+1, existingArgs+1)];
                for (int i=0; i<pas.size(); i++) {
                    args[order.indexOf(i)] = pas.get(i);
                }
                tfc.addEdit(reorderEdit(pal, args, defaultArgs));
            }
            for (Tree.NamedArgumentList nal: fiv.getNamedArgLists()) {
                List<Tree.NamedArgument> nas = nal.getNamedArguments();
                for (int i=0; i<=requiredParams; i++) {
                    Parameter param = parameters.get(order.get(i));
                    boolean found = false;
                    for (Tree.NamedArgument na: nas) {
                        Parameter p = na.getParameter();
                        if (p!=null &&
                                p.getModel().equals(param.getModel())) {
                            found=true;
                            break;
                        }
                    }
                    if (!found) {
                        Tree.Expression arg = defaultArgs[i];
                        String argString = arg==null ? "nothing" : toString(arg);
                        tfc.addEdit(new InsertEdit(nal.getStopIndex(), 
                                param.getName() + " = " + argString + "; "));
                    }
                }
            }
            FindRefinementsVisitor frv = new FindRefinementsVisitor(declaration);
            root.visit(frv);
            for (Tree.StatementOrArgument decNode: frv.getDeclarationNodes()) {
                boolean actual;
                Tree.ParameterList pl;
                if (decNode instanceof Tree.AnyMethod) {
                    Tree.AnyMethod m = (Tree.AnyMethod) decNode;
                    pl = m.getParameterLists().get(0);
                    actual = m.getDeclarationModel().isActual();
                }
                else if (decNode instanceof Tree.AnyClass) {
                    Tree.AnyClass c = (Tree.AnyClass) decNode;
                    pl = c.getParameterList();
                    actual = c.getDeclarationModel().isActual();
                }
                else if (decNode instanceof Tree.SpecifierStatement) {
                    Tree.Term bme = ((Tree.SpecifierStatement) decNode).getBaseMemberExpression();
                    if (bme instanceof Tree.ParameterizedExpression) {
                        pl = ((Tree.ParameterizedExpression) bme).getParameterLists().get(0);
                        actual = true;
                    }
                    else {
                        continue;
                    }
                }
                else {
                    continue;
                }
                List<Tree.Parameter> ps = pl.getParameters();
                int size = ps.size();
                Tree.Parameter[] params = new Tree.Parameter[size];
                boolean[] removeDefault = new boolean[size];
                boolean[] addDefault = new boolean[size];
                for (int i=0; i<size; i++) {
                    int index = order.indexOf(i);
                    params[index] = ps.get(i);
                    removeDefault[index] = !actual &&
                            parameters.get(i).isDefaulted() &&
                            !defaulted.get(index);
                    addDefault[index] = !actual &&
                            !parameters.get(i).isDefaulted() &&
                            defaulted.get(index);
                }
                tfc.addEdit(reorderEdit(pl, params, removeDefault, addDefault));
            }
            FindArgumentsVisitor fav = new FindArgumentsVisitor(declaration);
            root.visit(fav);
            for (Tree.MethodArgument decNode: fav.getResults()) {
                Tree.ParameterList pl = decNode.getParameterLists().get(0);
                List<Tree.Parameter> ps = pl.getParameters();
                int size = ps.size();
                Tree.Parameter[] params = new Tree.Parameter[size];
                for (int i=0; i<size; i++) {
                    params[order.indexOf(i)] = ps.get(i);
                }
                tfc.addEdit(reorderEdit(pl, params, defaultArgs));
            }
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

    public ReplaceEdit reorderEdit(Node list, Node[] elements, 
            Expression[] defaultArgs) {
        StringBuilder sb = new StringBuilder("(");
        for (int i=0; i<elements.length; i++) {
            Node elem = elements[i];
            String argString;
            if (elem==null) {
                if (defaultArgs!=null && defaultArgs[i]!=null) {
                    argString = toString(defaultArgs[i]);
                }
                else {
                    argString = "nothing";
                }
            }
            else {
                argString = toString(elem);
            }
            sb.append(argString).append(", ");
        }
        sb.setLength(sb.length()-2);
        sb.append(")");
        return new ReplaceEdit(Nodes.getNodeStartOffset(list), 
                Nodes.getNodeLength(list), 
                sb.toString());
    }
    
    public ReplaceEdit reorderEdit(Node list, Node[] elements, 
            boolean[] removeDefault, boolean[] addDefault) {
        StringBuilder sb = new StringBuilder("(");
        for (int i=0; i<elements.length; i++) {
            Node elem = elements[i];
            String argString = toString(elem);
            if (removeDefault[i]) {
                int argIndex = argString.indexOf('=');
                argString = argString.substring(0, argIndex).trim(); //TODO: very fragile impl
            }
            if (addDefault[i]) {
                if (elem instanceof Tree.FunctionalParameterDeclaration) {
                    //TODO: this results in incorrectly-typed 
                    //      code for void functional parameters 
                    argString = argString + " => nothing";
                }
                else {
                    argString = argString + " = nothing";
                }
            }
            sb.append(argString).append(", ");
        }
        sb.setLength(sb.length()-2);
        sb.append(")");
        return new ReplaceEdit(Nodes.getNodeStartOffset(list), 
                Nodes.getNodeLength(list), 
                sb.toString());
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
}
