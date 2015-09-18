package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.getTokenLength;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Value;

public class InvertBooleanRefactoring extends AbstractRefactoring {

    private Value value = null;

    public InvertBooleanRefactoring(IEditorPart editor) {
        super(editor);
        Referenceable declaration =  
                getReferencedDeclaration(node);
        if (declaration instanceof Value) {
            value = (Value) declaration;
        }
    }

    public Value getValue() {
        return value;
    }

    @Override
    public boolean getEnabled() {
        return value != null
                && value.getTypeDeclaration() instanceof Class
                && value.getTypeDeclaration()
                       .equals(value.getUnit().getBooleanDeclaration());
    }

    @Override
    public String getName() {
        return "Invert Boolean";
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        final RefactoringStatus result = new RefactoringStatus();
        return result;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange cc = new CompositeChange(getName());

        List<PhasedUnit> units = getAllUnits();
        for (PhasedUnit unit : units) {
            if (searchInFile(unit)) {
                TextFileChange tfc = newTextFileChange((ProjectPhasedUnit)unit);
                invertBoolean(unit.getCompilationUnit(), tfc, cc);

            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            invertBoolean(editor.getParseController().getLastCompilationUnit(), dc, cc);
        }

        return cc;
    }

    private void invertBoolean(Tree.CompilationUnit unit, TextChange tc, CompositeChange cc) {
        tc.setEdit(new MultiTextEdit());

        InternalVisitor visitor = new InternalVisitor(value);
        visitor.visit(unit);

        for (Tree.AttributeDeclaration attributeDeclaration : visitor.attributeDeclarations) {
            invertSpecifierExpression(attributeDeclaration.getSpecifierOrInitializerExpression(), tc);
        }
        for (Tree.SpecifierStatement specifierStatement : visitor.specifierStatements) {
            invertSpecifierExpression(specifierStatement.getSpecifierExpression(), tc);
        }
        for (Tree.AssignOp assignOp : visitor.assignOps) {
            invertTerm(assignOp.getRightTerm(), tc);
        }
        for (Node node : visitor.getNodes()) {
            if (visitor.notOpMap.containsKey(node)) {
                Tree.NotOp notOp = visitor.notOpMap.get(node);
                invertTerm(notOp, tc);
            } else {
                invertTerm((Tree.Term) node, tc);
            }
        }

        if (tc.getEdit().hasChildren()) {
            cc.add(tc);
        }
    }

    private void invertSpecifierExpression(Tree.SpecifierOrInitializerExpression specifierExpression, TextChange textChange) {
        if (specifierExpression != null
                && specifierExpression.getExpression() != null
                && specifierExpression.getExpression().getTerm() != null) {
            invertTerm(specifierExpression.getExpression().getTerm(), textChange);
        }
    }

    public static void invertTerm(Tree.Term term, TextChange change) {
        CommonToken token = (CommonToken) term.getMainToken();
        if (term instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = (Tree.BaseMemberExpression) term;
            if (bme.getDeclaration() instanceof Value) {
                Value v = (Value) bme.getDeclaration();
                Value trueDeclaration = term.getUnit().getTrueValueDeclaration();
                Value falseDeclaration = term.getUnit().getFalseValueDeclaration();
                if (v.equals(trueDeclaration)) {
                    change.addEdit(new ReplaceEdit(bme.getStartIndex(), bme.getDistance(), falseDeclaration.getName(term.getUnit())));
                }
                else if (v.equals(falseDeclaration)) {
                    change.addEdit(new ReplaceEdit(bme.getStartIndex(), bme.getDistance(), trueDeclaration.getName(term.getUnit())));
                }
                else {
                    change.addEdit(new InsertEdit(term.getStartIndex(), "!"));
                }
            }
        }
        else if (term instanceof Tree.Exists ||
                term instanceof Tree.Nonempty ||
                term instanceof Tree.IdenticalOp ||
                term instanceof Tree.InvocationExpression ||
                term instanceof Tree.MemberOrTypeExpression ||
                term instanceof Tree.TypeOperatorExpression ||
                term instanceof Tree.InOp) {
            change.addEdit(new InsertEdit(term.getStartIndex(), "!"));
        }
        else if (term instanceof Tree.NotOp) {
            change.addEdit(new DeleteEdit(token.getStartIndex(), getTokenLength(token)));
        }
        else if (term instanceof Tree.EqualOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), getTokenLength(token), "!="));
        }
        else if (term instanceof Tree.NotEqualOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), getTokenLength(token), "=="));
        }
        else if (term instanceof Tree.LargerOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), getTokenLength(token), "<="));
        }
        else if (term instanceof Tree.LargeAsOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), getTokenLength(token), "<"));
        }
        else if (term instanceof Tree.SmallerOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), getTokenLength(token), ">="));
        }
        else if (term instanceof Tree.SmallAsOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), getTokenLength(token), ">"));
        }
        else if (term instanceof Tree.AndOp) {
            Tree.AndOp andOp = (Tree.AndOp) term;
            change.addEdit(new ReplaceEdit(token.getStartIndex(), getTokenLength(token), "||"));
            invertTerm(andOp.getLeftTerm(), change);
            invertTerm(andOp.getRightTerm(), change);
        }
        else if (term instanceof Tree.OrOp) {
            Tree.OrOp orOp = (Tree.OrOp) term;
            change.addEdit(new ReplaceEdit(token.getStartIndex(), getTokenLength(token), "&&"));
            invertTerm(orOp.getLeftTerm(), change);
            invertTerm(orOp.getRightTerm(), change);
        }
        else if (term instanceof Tree.Expression) {
            invertTerm(((Tree.Expression) term).getTerm(), change);
        }
    }

    private static class InternalVisitor extends FindReferencesVisitor {

        Set<Tree.AttributeDeclaration> attributeDeclarations = new HashSet<Tree.AttributeDeclaration>();
        Set<Tree.SpecifierStatement> specifierStatements = new HashSet<Tree.SpecifierStatement>();
        Set<Tree.AssignOp> assignOps = new HashSet<Tree.AssignOp>();
        Map<Node, Tree.NotOp> notOpMap = new HashMap<Node, Tree.NotOp>();

        public InternalVisitor(Declaration declaration) {
            super(declaration);
        }

        @Override
        public void visit(Tree.AttributeDeclaration that) {
            if (getDeclaration().equals(that.getDeclarationModel())) {
                attributeDeclarations.add(that);
                return;
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.AssignOp that) {
            if (that.getLeftTerm() instanceof Tree.MemberOrTypeExpression) {
                Tree.MemberOrTypeExpression mote = (Tree.MemberOrTypeExpression) that.getLeftTerm();
                if (getDeclaration().equals(mote.getDeclaration())) {
                    assignOps.add(that);
                    return;
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.SpecifierStatement that) {
            if (that.getBaseMemberExpression() instanceof Tree.MemberOrTypeExpression) {
                Tree.MemberOrTypeExpression mote = (Tree.MemberOrTypeExpression) that.getBaseMemberExpression();
                if (getDeclaration().equals(mote.getDeclaration())) {
                    specifierStatements.add(that);
                    return;
                }

            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.NotOp that) {
            super.visit(that);
            if (getNodes().contains(that.getTerm())) {
                notOpMap.put(that.getTerm(), that);
            }
        }

    }

}
