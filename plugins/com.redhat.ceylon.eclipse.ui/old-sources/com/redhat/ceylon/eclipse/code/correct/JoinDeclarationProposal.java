package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;

public class JoinDeclarationProposal {

    static void addJoinDeclarationProposal(final Collection<ICompletionProposal> proposals, 
            Tree.CompilationUnit rootNode, final Tree.Statement statement, final IFile file) {
        if (statement instanceof Tree.SpecifierStatement) {
            final Tree.SpecifierStatement spec = 
                    (Tree.SpecifierStatement) statement;
            Tree.Term term = spec.getBaseMemberExpression();
            while (term instanceof Tree.ParameterizedExpression) {
                term = ((Tree.ParameterizedExpression) term).getPrimary();
            }
            if (term instanceof Tree.BaseMemberExpression) {
                final Declaration dec = 
                        ((Tree.BaseMemberExpression) term).getDeclaration();
                if (dec instanceof FunctionOrValue) {
                    class FindBodyVisitor extends Visitor {
                        @Override
                        public void visit(Tree.Body that) {
                            super.visit(that);
                            if (that.getStatements().contains(statement)) {
                                for (Tree.Statement st: that.getStatements()) {
                                    if (st instanceof Tree.AttributeDeclaration) {
                                        Tree.AttributeDeclaration ad = 
                                                (Tree.AttributeDeclaration) st;
                                        if (ad.getDeclarationModel().equals(dec) &&
                                                ad.getSpecifierOrInitializerExpression()==null) {
                                            createJoinDeclarationProposal(proposals, 
                                                    spec, file, dec, that, 
                                                    that.getStatements().indexOf(st), 
                                                    ad);
                                            break;
                                        }
                                    }
                                    else if (st instanceof Tree.MethodDeclaration) {
                                        Tree.MethodDeclaration ad = 
                                                (Tree.MethodDeclaration) st;
                                        if (ad.getDeclarationModel().equals(dec) &&
                                                ad.getSpecifierExpression()==null) {
                                            createJoinDeclarationProposal(proposals, 
                                                    spec, file, dec, that, 
                                                    that.getStatements().indexOf(st), 
                                                    ad);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    new FindBodyVisitor().visit(rootNode);
                }
            }
        }
        if (statement instanceof Tree.AttributeDeclaration ||
            statement instanceof Tree.MethodDeclaration) {
            final Tree.TypedDeclaration ad = 
                    (Tree.TypedDeclaration) statement;
            Tree.SpecifierOrInitializerExpression sie = null;
            if (statement instanceof Tree.AttributeDeclaration) {
                sie = ((Tree.AttributeDeclaration) ad).getSpecifierOrInitializerExpression();
            }
            else if (statement instanceof Tree.MethodDeclaration) {
                sie = ((Tree.MethodDeclaration) ad).getSpecifierExpression();
            }
            if (sie==null) {
                final Declaration dec = ad.getDeclarationModel();
                class FindBodyVisitor extends Visitor {
                    @Override
                    public void visit(Tree.Body that) {
                        super.visit(that);
                        if (that.getStatements().contains(statement)) {
                            for (Tree.Statement st: that.getStatements()) {
                                if (st instanceof Tree.SpecifierStatement) {
                                    final Tree.SpecifierStatement spec = 
                                            (Tree.SpecifierStatement) st;
                                    Tree.Term term = spec.getBaseMemberExpression();
                                    while (term instanceof Tree.ParameterizedExpression) {
                                        term = ((Tree.ParameterizedExpression) term).getPrimary();
                                    }
                                    if (term instanceof Tree.BaseMemberExpression) {
                                        Declaration sd = 
                                                ((Tree.BaseMemberExpression) term).getDeclaration();
                                        if (sd!=null && sd.equals(dec)) {
                                            createJoinDeclarationProposal(proposals, 
                                                    spec, file, dec, that, 
                                                    that.getStatements().indexOf(statement), 
                                                    ad);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                new FindBodyVisitor().visit(rootNode);
            }
        }
    }

    private static void createJoinDeclarationProposal(
            Collection<ICompletionProposal> proposals,
            Tree.SpecifierStatement statement,
            IFile file, Declaration dec,
            Tree.Body that, int i, Tree.TypedDeclaration ad) {
        TextChange change = new TextFileChange("Join Declaration", file);
        change.setEdit(new MultiTextEdit());
        IDocument document = EditorUtil.getDocument(change);
        String text;
        int declarationStart = ad.getStartIndex();
        int declarationIdStart = ad.getIdentifier().getStartIndex();
        int declarationLength = ad.getDistance();
        if (that.getStatements().size()>i+1) {
            Tree.Statement next = that.getStatements().get(i+1);
            declarationLength=next.getStartIndex()-declarationStart;
        }
        try {
            text = document.get(declarationStart, 
                    declarationIdStart-declarationStart);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return;
        }
        change.addEdit(new DeleteEdit(declarationStart, declarationLength));
        int specifierStart = statement.getStartIndex();
        change.addEdit(new InsertEdit(specifierStart, text));
        String desc = "Join declaration of '" + dec.getName() + "' with specification";
        proposals.add(new CorrectionProposal(desc, change, 
                new Region(specifierStart-declarationLength, 0)));
    }
    
}
