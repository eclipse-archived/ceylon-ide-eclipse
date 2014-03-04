package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getNameProposals;
import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getSupertypeProposals;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.styleProposal;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.MINOR_CHANGE;
import static com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring.guessName;
import static com.redhat.ceylon.eclipse.util.FindUtils.findStatement;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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

class AssignToLocalProposal implements ICompletionProposal, ICompletionProposalExtension6 {
    
    class AssignToLocalLinkedMode extends AbstractLinkedMode {
        
        private final IDocument document;
        
        private int offset;
        private int exitPos;
        private ProducedType type;
        private String name;
        
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
                performInitialChange(document);
                CeylonParseController cpc = editor.getParseController();
                Unit unit = cpc.getRootNode().getUnit();
                
                ProposalPosition typePosition = 
                        new ProposalPosition(document, offset, 5, 1, 
                                getSupertypeProposals(offset, unit, 
                                        type, true));
                
                ProposalPosition namePosition = 
                        new ProposalPosition(document, offset+6, name.length(), 0, 
                                getNameProposals(offset, 1, name));
                
                addLinkedPosition(linkedModeModel, typePosition);
                addLinkedPosition(linkedModeModel, namePosition);
                
                enterLinkedMode(document, 2, exitPos + name.length() + 9);
                openPopup();
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        private void performInitialChange(IDocument document) {
            
            Tree.Statement st = findStatement(rootNode, node);
            Node expression;
            Node expanse;
            ProducedType resultType;
            if (st instanceof Tree.ExpressionStatement) {
                Tree.Expression e = 
                        ((Tree.ExpressionStatement) st).getExpression();
                expression = e;
                expanse = st;
                resultType = e.getTypeModel();
                if (e.getTerm() instanceof Tree.InvocationExpression) {
                    Primary primary = 
                            ((Tree.InvocationExpression) e.getTerm()).getPrimary();
                    if (primary instanceof Tree.QualifiedMemberExpression) {
                        Tree.QualifiedMemberExpression prim = 
                                (Tree.QualifiedMemberExpression) primary;
                        if (prim.getMemberOperator().getToken()==null) {
                            //an expression followed by two annotations 
                            //can look like a named operator expression
                            //even though that is disallowed as an
                            //expression statement
                            Tree.Primary p = prim.getPrimary();
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
                List<Annotation> annotations = 
                        ((Tree.Declaration) st).getAnnotationList().getAnnotations();
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
            name = guessName(expression);
            offset = expanse.getStartIndex();
            type = resultType==null ? 
                    null : node.getUnit().denotableType(resultType);
            
            DocumentChange change = new DocumentChange("Assign To Local", document);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new InsertEdit(offset, "value " + name + " = "));
            
            String terminal = expanse.getEndToken().getText();
            if (!terminal.equals(";")) {
                change.addEdit(new InsertEdit(stopIndex+1, ";"));
                exitPos = stopIndex+2;
            }
            else {
                exitPos = stopIndex+1;
            }
            
            try {
                change.perform(new NullProgressMonitor());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    private final int currentOffset;
    private final Node node;
    private final Tree.CompilationUnit rootNode;
    
    public AssignToLocalProposal(Tree.CompilationUnit cu, 
            Node node, int currentOffset) {
        this.rootNode = cu;
        this.node = node;
        this.currentOffset = currentOffset;
    }
    
    @Override
    public void apply(IDocument document) {
        new AssignToLocalLinkedMode(document).start();
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        return styleProposal(getDisplayString());
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Assign expression to new local";
    }

    @Override
    public Image getImage() {
        return MINOR_CHANGE;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
    static void addAssignToLocalProposal(Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node, 
            int currentOffset) {
        proposals.add(new AssignToLocalProposal(cu, node, currentOffset));
    }

}