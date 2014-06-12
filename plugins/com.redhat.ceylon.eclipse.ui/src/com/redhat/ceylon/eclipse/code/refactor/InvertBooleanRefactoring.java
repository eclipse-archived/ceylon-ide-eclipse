package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.Nodes.getNodeLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeStartOffset;
import static com.redhat.ceylon.eclipse.util.Nodes.getTokenLength;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AttributeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierStatement;
import com.redhat.ceylon.eclipse.util.Nodes;

public class InvertBooleanRefactoring extends AbstractRefactoring {

    private Value value = null;
    private Tree.SpecifierOrInitializerExpression specifierOrInitializerExpression = null;

    public InvertBooleanRefactoring(IEditorPart editor) {
        super(editor);

        Declaration declaration = Nodes.getReferencedDeclaration(node);
        if (declaration instanceof Value) {
            value = (Value) declaration;
        }

        if (node instanceof Tree.AttributeDeclaration) {
            Tree.AttributeDeclaration attributeDeclaration = (AttributeDeclaration) node;
            specifierOrInitializerExpression = attributeDeclaration.getSpecifierOrInitializerExpression();
        }
        else if (node instanceof Tree.BaseMemberExpression) {
            Tree.Statement statement = Nodes.findStatement(rootNode, node);
            if (statement instanceof Tree.SpecifierStatement) {
                Tree.SpecifierStatement specifierStatement = (SpecifierStatement) statement;
                if (specifierStatement.getBaseMemberExpression() == node) {
                    specifierOrInitializerExpression = specifierStatement.getSpecifierExpression();
                }
            }
        }
    }
    
    public Value getValue() {
        return value;
    }

    @Override
    public boolean isEnabled() {
        return specifierOrInitializerExpression != null
                && value != null
                && value.getTypeDeclaration() != null
                && value.getTypeDeclaration().equals(value.getUnit().getBooleanDeclaration());
    }

    @Override
    int countReferences(Tree.CompilationUnit cu) {
        return 0;
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
        TextChange textChange = newLocalChange();
        textChange.setEdit(new MultiTextEdit());
        CompositeChange compositeChange = new CompositeChange(getName());
        compositeChange.add(textChange);

        invertTerm(specifierOrInitializerExpression.getExpression().getTerm(), textChange);

        return compositeChange;
    }
    
    private void invertTerm(Tree.Term term, TextChange change) {
        CommonToken token = (CommonToken) term.getMainToken();
        if (term instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = (Tree.BaseMemberExpression) term;
            if (bme.getDeclaration() instanceof Value) {
                Value v = (Value) bme.getDeclaration();
                Value trueDeclaration = node.getUnit().getTrueValueDeclaration();
                Value falseDeclaration = node.getUnit().getFalseValueDeclaration();
                if (v.equals(trueDeclaration)) {
                    change.addEdit(new ReplaceEdit(getNodeStartOffset(bme), getNodeLength(bme), falseDeclaration.getName(node.getUnit())));
                }
                else if (v.equals(falseDeclaration)) {
                    change.addEdit(new ReplaceEdit(getNodeStartOffset(bme), getNodeLength(bme), trueDeclaration.getName(node.getUnit())));
                }
                else {
                    change.addEdit(new InsertEdit(getNodeStartOffset(term), "!"));  
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
            change.addEdit(new InsertEdit(getNodeStartOffset(term), "!"));
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
        else if( term instanceof Tree.Expression ) {
            invertTerm(((Tree.Expression) term).getTerm(), change);
        }
    }
    
}