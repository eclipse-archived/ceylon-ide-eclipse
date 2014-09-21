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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Nodes;

public class JoinDeclarationProposal {

    static void addJoinDeclarationProposal(final Collection<ICompletionProposal> proposals, 
            Tree.CompilationUnit rootNode, final Tree.Statement statement, final IFile file) {
        if (statement instanceof Tree.SpecifierStatement) {
            Tree.Term term = ((Tree.SpecifierStatement) statement).getBaseMemberExpression();
            if (term instanceof Tree.BaseMemberExpression) {
                final Declaration dec = ((Tree.BaseMemberExpression) term).getDeclaration();
                if (dec instanceof Value) {
                    class FindBodyVisitor extends Visitor {
                        @Override
                        public void visit(Tree.Body that) {
                            super.visit(that);
                            if (that.getStatements().contains(statement)) {
                                int i=0;
                                for (Tree.Statement st: that.getStatements()) {
                                    if (st instanceof Tree.AttributeDeclaration) {
                                        Tree.AttributeDeclaration ad = (Tree.AttributeDeclaration) st;
                                        if (ad.getDeclarationModel().equals(dec) &&
                                                ad.getSpecifierOrInitializerExpression()==null) {
                                            TextChange change = new TextFileChange("Join Declaration", file);
                                            change.setEdit(new MultiTextEdit());
                                            IDocument document = EditorUtil.getDocument(change);
                                            String text;
                                            int declarationStart = Nodes.getNodeStartOffset(ad);
                                            int declarationIdStart = Nodes.getNodeStartOffset(ad.getIdentifier());
                                            int declarationLength = Nodes.getNodeLength(ad);
                                            if (that.getStatements().size()>i+1) {
                                                Tree.Statement next = that.getStatements().get(i+1);
                                                declarationLength=Nodes.getNodeStartOffset(next)-declarationStart;
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
                                            int specifierStart = Nodes.getNodeStartOffset(statement);
                                            change.addEdit(new InsertEdit(specifierStart, text));
                                            proposals.add(new CorrectionProposal("Join declaration of '" + dec.getName() + "' with specification", 
                                                    change, new Region(specifierStart-declarationLength, 0)));
                                            break;
                                        }
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                    new FindBodyVisitor().visit(rootNode);
                }
            }
        }
    }

}
