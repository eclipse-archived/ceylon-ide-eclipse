package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Nodes.findToplevelStatement;

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
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Util;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;

public class CollectParametersRefactoring extends AbstractRefactoring {
    
    private Declaration declaration;
    private int parameterListIndex;
    private List<Tree.Parameter> parameters = 
            new ArrayList<Tree.Parameter>();
    private Set<MethodOrValue> models = 
            new HashSet<MethodOrValue>();
    private int firstParam=-1;
    private int lastParam;
    
    private class FindParametersVisitor extends Visitor {
        private void handleParamList(Tree.Declaration that, int i,
                Tree.ParameterList pl) {
            if (pl==null) return;
            IRegion selection = editor.getSelection();
            int start = selection.getOffset();
            int end = selection.getOffset() + selection.getLength();
            if (start>pl.getStartIndex() &&
                start<=pl.getStopIndex()) {
                parameterListIndex = i;
                declaration = that.getDeclarationModel();
                for (int j=0; j<pl.getParameters().size(); j++) {
                    Tree.Parameter p = pl.getParameters().get(j);
                    if (p.getStartIndex()>=start && p.getStopIndex()<end) {
                        parameters.add(p);
                        models.add(p.getParameterModel().getModel());
                        if (firstParam==-1) firstParam=j;
                        lastParam=j;
                    }
                }
            }
        }
        @Override
        public void visit(Tree.AnyMethod that) {
            for (int i=0; i<that.getParameterLists().size(); i++) {
                handleParamList(that, i, 
                        that.getParameterLists().get(i));
            }
            super.visit(that);
        }
        @Override
        public void visit(Tree.ClassDefinition that) {
            handleParamList(that, 0, 
                    that.getParameterList());
            super.visit(that);
        }        
    }
    
    private static class FindInvocationsVisitor extends Visitor {
        private Declaration declaration;
        private final Set<Tree.ArgumentList> results = 
                new HashSet<Tree.ArgumentList>();
        Set<Tree.ArgumentList> getResults() {
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
                    Tree.NamedArgumentList nal = 
                            that.getNamedArgumentList();
                    if (nal!=null) {
                        results.add(nal);
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
    
    public CollectParametersRefactoring(IEditorPart editor) {
        super(editor);
        new FindParametersVisitor().visit(rootNode);
        if (declaration!=null) {
            newName = Character.toUpperCase(declaration.getName().charAt(0))
                    + declaration.getName().substring(1);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return declaration!=null && 
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
            String paramName = 
                    Escaping.toInitialLowercase(newName);
            FindInvocationsVisitor fiv = 
                    new FindInvocationsVisitor(declaration);
            root.visit(fiv);
            for (Tree.ArgumentList pal: fiv.getResults()) {
                refactorInvocation(tfc, paramName, pal);
            }
            FindRefinementsVisitor frv = 
                    new FindRefinementsVisitor(declaration);
            root.visit(frv);
            for (Tree.StatementOrArgument decNode: 
                    frv.getDeclarationNodes()) {
                refactorDeclaration(tfc, paramName, decNode);
            }
            FindArgumentsVisitor fav = 
                    new FindArgumentsVisitor(declaration);
            root.visit(fav);
            for (Tree.MethodArgument decNode: fav.getResults()) {
                refactorArgument(tfc, paramName, decNode);
            }
            createNewClassDeclaration(tfc, root);
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

    private void refactorInvocation(TextChange tfc,
            String paramName, Tree.ArgumentList al) {
        if (al instanceof Tree.PositionalArgumentList) {
            Tree.PositionalArgumentList pal = 
                    (Tree.PositionalArgumentList) al;
            List<Tree.PositionalArgument> pas = 
                    pal.getPositionalArguments();
            if (pas.size()>firstParam) {
                Integer startIndex = 
                        pas.get(firstParam).getStartIndex();
                tfc.addEdit(new InsertEdit(startIndex, newName + "("));
                Integer stopIndex = pas.size()>lastParam && 
                            !pas.get(lastParam).getParameter().isSequenced() ?
                        pas.get(lastParam).getStopIndex()+1:
                        pas.get(pas.size()-1).getStopIndex()+1;
                tfc.addEdit(new InsertEdit(stopIndex, ")"));
            }
        }
        else if (al instanceof Tree.NamedArgumentList) {
            Tree.NamedArgumentList nal = 
                    (Tree.NamedArgumentList) al;
            List<Tree.NamedArgument> nas = 
                    nal.getNamedArguments();
            List<Tree.StatementOrArgument> results = 
                    new ArrayList<Tree.StatementOrArgument>();
            Tree.NamedArgument prev = null;
            for (Tree.NamedArgument na: nas) {
                if (models.contains(na.getParameter().getModel())) {
                    int fromOffset = results.isEmpty() ? 
                            Nodes.getNodeStartOffset(na) : 
                            Nodes.getNodeEndOffset(prev);
                    int toOffset = Nodes.getNodeEndOffset(na);
                    tfc.addEdit(new DeleteEdit(fromOffset, 
                            toOffset-fromOffset));
                    results.add(na);
                }
                prev = na;
            }
            Tree.SequencedArgument sa = nal.getSequencedArgument();
            if (sa!=null) {
                if (models.contains(sa.getParameter().getModel())) {
                    int fromOffset = Nodes.getNodeStartOffset(sa);
                    int toOffset = Nodes.getNodeEndOffset(sa);
                    tfc.addEdit(new DeleteEdit(fromOffset, 
                            toOffset-fromOffset));
                    results.add(sa);
                }
            }
            if (!results.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append(paramName).append(" = ").append(newName).append(" { ");
                for (Tree.StatementOrArgument na: results) {
                    builder.append(toString(na)).append(" ");
                }
                builder.append("};");
                tfc.addEdit(new InsertEdit(Nodes.getNodeStartOffset(results.get(0)), 
                        builder.toString()));
            }
        }
    }

    private void createNewClassDeclaration(final TextChange tfc,
            Tree.CompilationUnit root) {
        if (declaration.getUnit().equals(root.getUnit())) {
            String delim = getDefaultLineDelimiter(document);
            //TODO: for unshared declarations, we don't 
            //      need to make it toplevel, I guess
            int loc = findToplevelStatement(rootNode, node).getStartIndex();
            StringBuilder builder = new StringBuilder();
            if (declaration.isShared()) {
                builder.append("shared ");
            }
            builder.append("class ").append(newName).append("(");
            for (Tree.Parameter p: parameters) {
                boolean addShared = true;
                if (p instanceof Tree.ParameterDeclaration) {
                    Tree.TypedDeclaration ptd = 
                            ((Tree.ParameterDeclaration) p).getTypedDeclaration();
                    if (Util.hasAnnotation(ptd.getAnnotationList(), 
                            "shared", ptd.getUnit())) {
                        addShared = false;
                    }
                }
                if (addShared) {
                    builder.append("shared ");
                }
                builder.append(toString(p)).append(", ");
            }
            if (builder.toString().endsWith(", ")) {
                builder.setLength(builder.length()-2);
            }
            builder.append(") {}").append(delim).append(delim);
            tfc.addEdit(new InsertEdit(loc, builder.toString()));
        }
    }

    private void refactorArgument(TextChange tfc, String paramName,
            Tree.MethodArgument decNode) {
        refactorDec(tfc, paramName, 
                decNode.getParameterLists().get(parameterListIndex), 
                decNode.getBlock());
    }

    private void refactorDeclaration(TextChange tfc, String paramName, 
            Tree.StatementOrArgument decNode) {
        Tree.ParameterList pl;
        Node body;
        if (decNode instanceof Tree.MethodDefinition) {
            pl = ((Tree.AnyMethod) decNode).getParameterLists()
                    .get(parameterListIndex);
            body = ((Tree.MethodDefinition) decNode).getBlock();
        }
        else if (decNode instanceof Tree.MethodDeclaration) {
            pl = ((Tree.AnyMethod) decNode).getParameterLists()
                    .get(parameterListIndex);
            body = ((Tree.MethodDeclaration) decNode).getSpecifierExpression();
        }
        else if (decNode instanceof Tree.ClassDefinition) {
            pl = ((Tree.ClassDefinition) decNode).getParameterList();
            body = ((Tree.ClassDefinition) decNode).getClassBody();
        }
        else if (decNode instanceof Tree.SpecifierStatement) {
            Tree.Term bme = 
                    ((Tree.SpecifierStatement) decNode).getBaseMemberExpression();
            body = ((Tree.SpecifierStatement) decNode).getSpecifierExpression();
            if (bme instanceof Tree.ParameterizedExpression) {
                pl = ((Tree.ParameterizedExpression) bme)
                        .getParameterLists().get(parameterListIndex);
            }
            else {
                return;
            }
        }
        else {
            return;
        }
        refactorDec(tfc, paramName, pl, body);
    }
    
    private void refactorDec(final TextChange tfc, final String paramName,
            Tree.ParameterList pl, Node body) {
        List<Tree.Parameter> ps = pl.getParameters();
        final Set<MethodOrValue> params = new HashSet<MethodOrValue>();
        boolean allDefaulted = true;
        for (int i=firstParam; i<ps.size()&&i<=lastParam; i++) {
            Parameter p = ps.get(i).getParameterModel();
            params.add(p.getModel());
            if (!p.isDefaulted()) {
                allDefaulted = false;
            }
        }
        int startOffset = Nodes.getNodeStartOffset(ps.get(firstParam));
        int endOffset = Nodes.getNodeEndOffset(ps.get(lastParam));
        String text = newName + " " + paramName;
        if (allDefaulted) {
            text += " = " + newName + "()";
        }
        tfc.addEdit(new InsertEdit(startOffset, text));
        tfc.addEdit(new DeleteEdit(startOffset, endOffset-startOffset));
        for (int i=lastParam+1; i<ps.size(); i++) {
            refactorLocalRefs(tfc, paramName, params, ps.get(i));
        }
        refactorLocalRefs(tfc, paramName, params, body);
    }

    private void refactorLocalRefs(final TextChange tfc,
            final String paramName, final Set<MethodOrValue> params, Node node) {
        node.visit(new Visitor() {
            @Override
            public void visit(Tree.BaseMemberExpression that) {
                super.visit(that);
                if (params.contains(that.getDeclaration())) {
                    tfc.addEdit(new InsertEdit(that.getStartIndex(), 
                            paramName + "."));
                }
            }
        });
    }
    
}
