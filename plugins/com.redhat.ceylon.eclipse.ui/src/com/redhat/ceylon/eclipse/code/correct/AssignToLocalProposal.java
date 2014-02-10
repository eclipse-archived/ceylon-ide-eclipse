package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring.guessName;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Annotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Primary;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindUtils;

class AssignToLocalProposal extends CorrectionProposal {
    
    final IFile file;
    final int offset;
    final int length;
    
    AssignToLocalProposal(int offset, int length, IFile file, 
            TextChange change) {
        super("Assign expression to new local", change);
        this.file=file;
        this.offset=offset;
        this.length=length;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        EditorUtil.gotoLocation(file, offset, length);
    }
    
    static void addAssignToLocalProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node, 
            int currentOffset) {
        //if (node instanceof Tree.Term) {
            Tree.Statement st = FindUtils.findStatement(cu, node);
            Node expression;
            Node expanse;
            if (st instanceof Tree.ExpressionStatement) {
                Tree.Expression e = ((Tree.ExpressionStatement) st).getExpression();
                expression = e;
                expanse = st;
                if (e.getTerm() instanceof Tree.InvocationExpression) {
                    Primary primary = ((Tree.InvocationExpression)e.getTerm()).getPrimary();
                    if (primary instanceof Tree.QualifiedMemberExpression) {
                        if (((Tree.QualifiedMemberExpression)primary).getMemberOperator().getToken()==null) {
                            //an expression followed by two annotations 
                            //can look like a named operator expression
                            //even though that is disallowed as an
                            //expression statement
                            expression = ((Tree.QualifiedMemberExpression) primary).getPrimary();
                            expanse = expression;
                        }
                    }
                }
            }
            else if (st instanceof Tree.Declaration) {
                Declaration d = ((Tree.Declaration) st).getDeclarationModel();
				if (d==null || d.isToplevel()) {
                    return;
                }
                //some expressions get interpreted as annotations
                List<Annotation> annotations = ((Tree.Declaration)st).getAnnotationList().getAnnotations();
                if (!annotations.isEmpty()) {
                    expression = annotations.get(0);
                    expanse = expression;
                }
                else if (st instanceof Tree.TypedDeclaration) {
                    //some expressions look like a type declaration
                    //when they appear right in front of an annotation
                    //or function invocations
                    Tree.Type type = ((Tree.TypedDeclaration) st).getType();
                    if (type instanceof Tree.SimpleType) {
                        expression = type;
                        expanse = expression;
                    }
                    else {
                        return;
                    }
                }
                else {
                    return;
                }
            }
            else {
                return;
            }
            if (currentOffset<expanse.getStartIndex() || 
                currentOffset>expanse.getStopIndex()+1) {
                return;
            }
            String name = guessName(expression);
            int offset = expanse.getStartIndex();
            TextChange change = new TextFileChange("Assign To Local", file);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new InsertEdit(offset, "value " + name + " = "));
            String terminal = expanse.getEndToken().getText();
            if (!terminal.equals(";")) {
                change.addEdit(new InsertEdit(expanse.getStopIndex()+1, ";"));
            }
            proposals.add(new AssignToLocalProposal(offset+6, name.length(), file, change));
        //}
    }
}