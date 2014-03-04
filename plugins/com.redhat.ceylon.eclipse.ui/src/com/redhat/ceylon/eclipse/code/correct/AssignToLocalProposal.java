package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getNameProposals;
import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getSupertypeProposals;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring.guessName;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedPosition;
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
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.refactor.AbstractLinkedMode;
import com.redhat.ceylon.eclipse.util.FindUtils;

//TODO: refactor this to directly extend ICompletionProposal, ICompletionProposalExtension6
class AssignToLocalProposal extends CorrectionProposal {
    
    class AssignToLocalLinkedMode extends AbstractLinkedMode {
        final IDocument document;
        
        private AssignToLocalLinkedMode(IDocument document) {
            super((CeylonEditor) EditorUtil.getCurrentEditor());
            this.document = document;
        }

        @Override
        protected String getHintTemplate() {
            return "Enter type and name for new local {0}";
        }
        
        @Override
        protected final void updatePopupLocation() {
            LinkedPosition currentLinkedPosition = getCurrentLinkedPosition();
            if (currentLinkedPosition==null) {
                getInfoPopup().setHintTemplate(getHintTemplate());
            }
            else if (currentLinkedPosition.getSequenceNumber()==1) {
                getInfoPopup().setHintTemplate("Enter type for new local {0}");
            }
            else {
                getInfoPopup().setHintTemplate("Enter name for new local {0}");
            }
        }

        void start() {
            try {
                AssignToLocalProposal.super.apply(document);
                CeylonParseController cpc = editor.getParseController();
                Unit unit = cpc.getRootNode().getUnit();
                
                ProposalPosition typePosition = 
                        new ProposalPosition(document, offset-6, 5, 1, 
                                getSupertypeProposals(offset-6, unit, resultType));
                
                ProposalPosition namePosition = 
                        new ProposalPosition(document, offset, name.length(), 0, 
                                getNameProposals(offset-6, 1, name));
                
                addLinkedPosition(linkedModeModel, typePosition);
                addLinkedPosition(linkedModeModel, namePosition);
                
                enterLinkedMode(document, 2, exitPos + length + 9);
                openPopup();
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

    }
    
//    private final IFile file;
    private final int offset;
    private final int length;
    private final int exitPos;
    private final ProducedType resultType;
    private final String name;
    
    AssignToLocalProposal(int offset, int length, int exitPos, 
            ProducedType resultType, String name, //IFile file, 
            TextChange change) {
        super("Assign expression to new local", change);
        this.exitPos = exitPos;
        this.resultType = resultType;
//        this.file=file;
        this.offset=offset;
        this.length=length;
        this.name = name;
    }
    
    @Override
    public void apply(IDocument document) {
        new AssignToLocalLinkedMode(document).start();
//        gotoLocation(file, offset, length);
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
            Integer stopIndex = expanse.getStopIndex();
            if (currentOffset<expanse.getStartIndex() || 
                currentOffset>stopIndex+1) {
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
                change.addEdit(new InsertEdit(stopIndex+1, ";"));
                exitPos = stopIndex+2;
            }
            else {
                exitPos = stopIndex+1;
            }
            ProducedType type = resultType==null ? 
                    null : node.getUnit().denotableType(resultType);
            proposals.add(new AssignToLocalProposal(offset+6, name.length(), 
                    exitPos, type, name, /*file,*/ change));
        //}
    }
}