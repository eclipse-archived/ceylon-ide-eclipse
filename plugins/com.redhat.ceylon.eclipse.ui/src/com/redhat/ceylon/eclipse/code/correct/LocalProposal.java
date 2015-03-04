package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.refactor.AbstractLinkedMode;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.Nodes;

public abstract class LocalProposal extends AbstractLinkedMode
        implements ICompletionProposal, ICompletionProposalExtension6 {

    protected int offset;
    protected int exitPos;
    protected ProducedType type;
    protected String initialName;
    protected String[] nameProposals;
    protected final int currentOffset;

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
                Tree.Primary primary = 
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
            List<Tree.Annotation> annotations = 
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
                else if (type instanceof Tree.FunctionType) {
                    expression = type;
                    expanse = expression;
                    resultType = node.getUnit()
                            .getCallableReturnType(type.getTypeModel());
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
        nameProposals = computeNameProposals(expression);
        initialName = nameProposals[0];
        offset = expanse.getStartIndex();
        type = resultType==null ? 
                null : node.getUnit().denotableType(resultType);
    
        DocumentChange change = 
                createChange(document, expanse, stopIndex);
        try {
            change.perform(new NullProgressMonitor());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String[] computeNameProposals(Node expression) {
        return Nodes.nameProposals(expression);
    }
    
    protected abstract DocumentChange createChange(IDocument document, 
            Node expanse, Integer stopIndex);

    protected final Node node;
    protected final Tree.CompilationUnit rootNode;

    @Override
    public void apply(IDocument document) {
        try {
            performInitialChange(document);
            CeylonParseController cpc = 
                    editor.getParseController();
            Unit unit = cpc.getRootNode().getUnit();
            addLinkedPositions(document, unit);
            enterLinkedMode(document, 
                    getExitSequenceNumber(), 
                    getExitPosition());
            openPopup();
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    int getExitSequenceNumber() {
        return 2;
    }

    protected int getExitPosition() {
        return exitPos + initialName.length() + 9;
    }
    
    protected abstract void addLinkedPositions(IDocument document, Unit unit)
            throws BadLocationException;

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), false, true);
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
    public Image getImage() {
        return MINOR_CHANGE;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    public LocalProposal(Tree.CompilationUnit cu, Node node, 
            int currentOffset) {
        super((CeylonEditor) EditorUtil.getCurrentEditor());
        this.rootNode = cu;
        this.node = node;
        this.currentOffset = currentOffset;
    }
    
    boolean isEnabled(ProducedType resultType) {
        return true;
    }

    boolean isEnabled() {
        Tree.Statement st = findStatement(rootNode, node);
        if (st instanceof Tree.ExpressionStatement) {
            Tree.Expression e = 
                    ((Tree.ExpressionStatement) st).getExpression();
            ProducedType resultType = e.getTypeModel();
            if (e.getTerm() instanceof Tree.InvocationExpression) {
                Tree.Primary primary = 
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
                        resultType = p.getTypeModel();
                    }
                }
            }
            return isEnabled(resultType);
        }
        else if (st instanceof Tree.Declaration) {
            Tree.Declaration dec = (Tree.Declaration) st;
            Tree.Identifier id = dec.getIdentifier();
            if (id==null) {
                return false;
            }
            int line = id.getToken().getLine();
    		Declaration d = dec.getDeclarationModel();
            if (d==null || d.isToplevel()) {
                return false;
            }
            //some expressions get interpreted as annotations
            List<Tree.Annotation> annotations = 
                    dec.getAnnotationList().getAnnotations();
            Tree.AnonymousAnnotation aa = 
                    dec.getAnnotationList().getAnonymousAnnotation();
            ProducedType resultType;
            if (aa!=null && currentOffset<=aa.getStopIndex()+1) {
                if (aa.getEndToken().getLine()==line) {
                    return false;
                }
                resultType = aa.getUnit().getStringDeclaration().getType();
            }
            else if (!annotations.isEmpty() && 
                    currentOffset<=dec.getAnnotationList().getStopIndex()+1) {
                Tree.Annotation a = annotations.get(0);
                if (a.getEndToken().getLine()==line) {
                    return false;
                }
                resultType = a.getTypeModel();
            }
            else if (st instanceof Tree.TypedDeclaration) {
                //some expressions look like a type declaration
                //when they appear right in front of an annotation
                //or function invocations
                Tree.Type type = ((Tree.TypedDeclaration) st).getType();
                if (currentOffset<=type.getStopIndex()+1 &&
                    currentOffset>=type.getStartIndex() &&
                    type.getEndToken().getLine()!=line) {
                    resultType = type.getTypeModel();
                    if (type instanceof Tree.SimpleType) {
                        //just use that type
                    }
                    else if (type instanceof Tree.FunctionType) {
                        //instantiation expressions look like a
                        //function type declaration
                        resultType = node.getUnit()
                                .getCallableReturnType(resultType);
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
            return isEnabled(resultType);
        }
        return false;
    }

}