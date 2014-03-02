package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.gotoLocation;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.installLinkedMode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring.guessName;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Annotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Primary;
import com.redhat.ceylon.eclipse.code.complete.CompletionProposal;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindUtils;

class AssignToLocalProposal extends CorrectionProposal {
    
    private final IFile file;
    private final int offset;
    private final int length;
    private final int exitPos;
    private final ProducedType resultType;
    
    AssignToLocalProposal(int offset, int length, int exitPos, 
            ProducedType resultType, IFile file, TextChange change) {
        super("Assign expression to new local", change);
        this.exitPos = exitPos;
        this.resultType = resultType;
        this.file=file;
        this.offset=offset;
        this.length=length;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        gotoLocation(file, offset, length);
        LinkedModeModel linkedModeModel = new LinkedModeModel();
        if (!isTypeUnknown(resultType)) {
            CeylonEditor editor = (CeylonEditor) EditorUtil.getCurrentEditor();
            Unit unit = editor.getParseController().getRootNode().getUnit();
            List<ProducedType> supertypes = resultType.getSupertypes();
            ICompletionProposal[] proposals = 
                    new ICompletionProposal[supertypes.size()];
            for (int i=0; i<supertypes.size(); i++) {
                ProducedType type = supertypes.get(i);
                String typeName = type.getProducedTypeName(unit);
                proposals[i] = new CompletionProposal(offset-1, "value", 
                        getImageForDeclaration(type.getDeclaration()),
                        typeName, typeName);
            }
            ProposalPosition linkedPosition = 
                    new ProposalPosition(document, offset-6, 5, 0, proposals);
            try {
                addLinkedPosition(linkedModeModel, linkedPosition);
                installLinkedMode(editor, document, linkedModeModel, 
                        this, 1, exitPos + length + 9);
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
    
    static void addAssignToLocalProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node, 
            int currentOffset) {
        //if (node instanceof Tree.Term) {
            Tree.Statement st = FindUtils.findStatement(cu, node);
            Node expression;
            Node expanse;
            ProducedType resultType;
            if (st instanceof Tree.ExpressionStatement) {
                Tree.Expression e = ((Tree.ExpressionStatement) st).getExpression();
                expression = e;
                expanse = st;
                resultType = e.getTypeModel();
                if (e.getTerm() instanceof Tree.InvocationExpression) {
                    Primary primary = ((Tree.InvocationExpression)e.getTerm()).getPrimary();
                    if (primary instanceof Tree.QualifiedMemberExpression) {
                        if (((Tree.QualifiedMemberExpression) primary).getMemberOperator().getToken()==null) {
                            //an expression followed by two annotations 
                            //can look like a named operator expression
                            //even though that is disallowed as an
                            //expression statement
                            Tree.Primary p = ((Tree.QualifiedMemberExpression) primary).getPrimary();
                            expression = p;
                            expanse = expression;
                            resultType = p.getTypeModel();
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
                    Tree.Annotation a = annotations.get(0);
                    expression = a;
                    expanse = expression;
                    resultType = a.getTypeModel();
                }
                else if (st instanceof Tree.TypedDeclaration) {
                    //some expressions look like a type declaration
                    //when they appear right in front of an annotation
                    //or function invocations
                    Tree.Type type = ((Tree.TypedDeclaration) st).getType();
                    if (type instanceof Tree.SimpleType) {
                        expression = type;
                        expanse = expression;
                        resultType = type.getTypeModel();
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
            int exitPos;
            if (!terminal.equals(";")) {
                change.addEdit(new InsertEdit(expanse.getStopIndex()+1, ";"));
                exitPos = expanse.getStopIndex()+2;
            }
            else {
                exitPos = expanse.getStopIndex()+1;
            }
            proposals.add(new AssignToLocalProposal(offset+6, name.length(), exitPos, 
                    resultType, file, change));
        //}
    }
}