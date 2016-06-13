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
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Unit;
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
    public boolean isAffectingOtherFiles() {
        if (value==null) {
            return false;
        }
        if (value.isToplevel() ||
                value.isShared()) {
            return true;
        }
        if (value.isParameter()) {
            FunctionOrValue fov = 
                    (FunctionOrValue) 
                        value;
            Declaration container = 
                    fov.getInitializerParameter()
                        .getDeclaration();

            if (container.isToplevel() || 
                container.isShared()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }
    
    @Override
    protected void refactorInFile(TextChange tc, 
            CompositeChange cc, Tree.CompilationUnit root, 
            List<CommonToken> tokens) {
        tc.setEdit(new MultiTextEdit());

        InternalVisitor visitor = new InternalVisitor(value);
        visitor.visit(root);

        for (Tree.AttributeDeclaration attributeDeclaration : 
                visitor.attributeDeclarations) {
            Tree.SpecifierOrInitializerExpression sie = 
                    attributeDeclaration.getSpecifierOrInitializerExpression();
            invertSpecifierExpression(sie, tc);
        }
        for (Tree.SpecifierStatement specifierStatement : 
                visitor.specifierStatements) {
            Tree.SpecifierExpression se = 
                    specifierStatement.getSpecifierExpression();
            invertSpecifierExpression(se, tc);
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

    private void invertSpecifierExpression(
            Tree.SpecifierOrInitializerExpression specifierExpression, 
            TextChange textChange) {
        if (specifierExpression != null) {
            Tree.Expression e = 
                    specifierExpression.getExpression();
            if (e != null && e.getTerm() != null) {
                invertTerm(e.getTerm(), textChange);
            }
        }
    }

    public static void invertTerm(Tree.Term term, TextChange change) {
        CommonToken token = (CommonToken) term.getMainToken();
        if (term instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = 
                    (Tree.BaseMemberExpression) term;
            if (bme.getDeclaration() instanceof Value) {
                Value v = (Value) bme.getDeclaration();
                Unit unit = term.getUnit();
                Value trueDeclaration = 
                        unit.getTrueValueDeclaration();
                Value falseDeclaration = 
                        unit.getFalseValueDeclaration();
                if (v.equals(trueDeclaration)) {
                    change.addEdit(new ReplaceEdit(bme.getStartIndex(), 
                            bme.getDistance(), 
                            falseDeclaration.getName(unit)));
                }
                else if (v.equals(falseDeclaration)) {
                    change.addEdit(new ReplaceEdit(bme.getStartIndex(), 
                            bme.getDistance(), 
                            trueDeclaration.getName(unit)));
                }
                else {
                    change.addEdit(new InsertEdit(term.getStartIndex(), "!"));
                }
            }
        }
        else if (term instanceof Tree.NotOp) {
            change.addEdit(new DeleteEdit(token.getStartIndex(), 
                    getTokenLength(token)));
        }
        else if (term instanceof Tree.EqualOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), 
                    getTokenLength(token), "!="));
        }
        else if (term instanceof Tree.NotEqualOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), 
                    getTokenLength(token), "=="));
        }
        else if (term instanceof Tree.LargerOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), 
                    getTokenLength(token), "<="));
        }
        else if (term instanceof Tree.LargeAsOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), 
                    getTokenLength(token), "<"));
        }
        else if (term instanceof Tree.SmallerOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), 
                    getTokenLength(token), ">="));
        }
        else if (term instanceof Tree.SmallAsOp) {
            change.addEdit(new ReplaceEdit(token.getStartIndex(), 
                    getTokenLength(token), ">"));
        }
        else if (term instanceof Tree.AndOp) {
            Tree.AndOp andOp = (Tree.AndOp) term;
            change.addEdit(new ReplaceEdit(token.getStartIndex(), 
                    getTokenLength(token), "||"));
            invertTerm(andOp.getLeftTerm(), change);
            invertTerm(andOp.getRightTerm(), change);
        }
        else if (term instanceof Tree.OrOp) {
            Tree.OrOp orOp = (Tree.OrOp) term;
            change.addEdit(new ReplaceEdit(token.getStartIndex(), 
                    getTokenLength(token), "&&"));
            //if either operand is an && then add parens
            Tree.Term lt = orOp.getLeftTerm();
            Tree.Term rt = orOp.getRightTerm();
            if (lt instanceof Tree.AndOp) {
                change.addEdit(new InsertEdit(lt.getStartIndex(), "("));
            }
            invertTerm(lt, change);
            if (lt instanceof Tree.AndOp) {
                change.addEdit(new InsertEdit(lt.getEndIndex(), ")"));
            }
            if (rt instanceof Tree.AndOp) {
                change.addEdit(new InsertEdit(rt.getStartIndex(), "("));
            }
            invertTerm(rt, change);
            if (rt instanceof Tree.AndOp) {
                change.addEdit(new InsertEdit(rt.getEndIndex(), ")"));
            }
        }
        else if (term instanceof Tree.Expression && term.getToken()==null) {
            invertTerm(((Tree.Expression) term).getTerm(), change);
        }
        else if (term instanceof Tree.ThenOp
               || term instanceof Tree.DefaultOp
               || term instanceof Tree.LetExpression
               || term instanceof Tree.SwitchExpression
               || term instanceof Tree.IfExpression
               || term instanceof Tree.AssignmentOp) {
            change.addEdit(new InsertEdit(term.getStartIndex(), "!("));
            change.addEdit(new InsertEdit(term.getEndIndex(), ")"));
        }
        else {
            change.addEdit(new InsertEdit(term.getStartIndex(), "!"));
        }
    }

    private static class InternalVisitor extends FindReferencesVisitor {

        Set<Tree.AttributeDeclaration> attributeDeclarations = 
                new HashSet<Tree.AttributeDeclaration>();
        Set<Tree.SpecifierStatement> specifierStatements = 
                new HashSet<Tree.SpecifierStatement>();
        Set<Tree.AssignOp> assignOps = 
                new HashSet<Tree.AssignOp>();
        Map<Node, Tree.NotOp> notOpMap = 
                new HashMap<Node, Tree.NotOp>();

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
            if (that.getLeftTerm() 
                    instanceof Tree.MemberOrTypeExpression) {
                Tree.MemberOrTypeExpression mote = 
                        (Tree.MemberOrTypeExpression) 
                            that.getLeftTerm();
                if (getDeclaration().equals(mote.getDeclaration())) {
                    assignOps.add(that);
                    return;
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.SpecifierStatement that) {
            if (that.getBaseMemberExpression() 
                    instanceof Tree.MemberOrTypeExpression) {
                Tree.MemberOrTypeExpression mote = 
                        (Tree.MemberOrTypeExpression) 
                            that.getBaseMemberExpression();
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
