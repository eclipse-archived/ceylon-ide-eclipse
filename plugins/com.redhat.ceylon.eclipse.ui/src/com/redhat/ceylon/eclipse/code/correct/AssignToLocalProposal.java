package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getNameProposals;
import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getSupertypeProposals;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;

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
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.refactor.AbstractLinkedMode;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.eclipse.util.Nodes;

class AssignToLocalProposal implements ICompletionProposal, ICompletionProposalExtension6 {
    
    class AssignToLocalLinkedMode extends AbstractLinkedMode {
        
        private final IDocument document;
        
        private int offset;
        private int exitPos;
        private ProducedType type;
        private String initialName;
        private String[] nameProposals;
        
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
                						type, true, "value"));
                
                ProposalPosition namePosition = 
                		new ProposalPosition(document, offset+6, initialName.length(), 0, 
                				getNameProposals(offset, 1, nameProposals));
                
                LinkedMode.addLinkedPosition(linkedModeModel, typePosition);
                LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
                
                enterLinkedMode(document, 2, exitPos + initialName.length() + 9);
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
                Tree.Declaration dec = (Tree.Declaration) st;
				Declaration d = dec.getDeclarationModel();
                if (d==null || d.isToplevel()) {
                    return;
                }
                //some expressions get interpreted as annotations
                List<Annotation> annotations = 
                        dec.getAnnotationList().getAnnotations();
                Tree.AnonymousAnnotation aa = 
                		dec.getAnnotationList().getAnonymousAnnotation();
                if (aa!=null && currentOffset<=aa.getStopIndex()+1) {
                	expression = aa;
                	expanse = expression;
                	resultType = aa.getUnit().getStringDeclaration().getType();
                }
                else if (!annotations.isEmpty() && 
                		currentOffset<=dec.getAnnotationList().getStopIndex()+1) {
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
            nameProposals = Nodes.nameProposals(expression);
            initialName = nameProposals[0];
            offset = expanse.getStartIndex();
            type = resultType==null ? 
                    null : node.getUnit().denotableType(resultType);
            
            DocumentChange change = 
            		new DocumentChange("Assign to Local", document);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new InsertEdit(offset, "value " + initialName + " = "));
            
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
        return Highlights.styleProposal(getDisplayString(), false);
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
        AssignToLocalProposal prop = 
                new AssignToLocalProposal(cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }
    
    private boolean isEnabled() {
        Tree.Statement st = findStatement(rootNode, node);
        if (st instanceof Tree.ExpressionStatement) {
            return true;
        }
        else if (st instanceof Tree.Declaration) {
            Tree.Declaration dec = (Tree.Declaration) st;
			Declaration d = dec.getDeclarationModel();
            if (d==null || d.isToplevel()) {
                return false;
            }
            //some expressions get interpreted as annotations
            List<Annotation> annotations = 
                    dec.getAnnotationList().getAnnotations();
            Tree.AnonymousAnnotation aa = 
            		dec.getAnnotationList().getAnonymousAnnotation();
            if ((aa!=null || !annotations.isEmpty()) &&
            		currentOffset<=dec.getAnnotationList().getStopIndex()+1) {
            	return true;
            }
            else if (st instanceof Tree.TypedDeclaration) {
                //some expressions look like a type declaration
                //when they appear right in front of an annotation
                //or function invocations
                Tree.Type type = ((Tree.TypedDeclaration) st).getType();
                if (type!=null && currentOffset<=type.getStopIndex()+1) {
                	return type instanceof Tree.SimpleType;
                }
            }
        }
        return false;
    }

}