package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findStatement;

import java.util.List;

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

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.InvocationExpression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.TypedDeclaration;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.code.refactor.AbstractLinkedMode;
import org.eclipse.ceylon.ide.eclipse.code.refactor.RefactorInformationPopup;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Unit;

public abstract class LocalProposal extends AbstractLinkedMode
        implements ICompletionProposal, ICompletionProposalExtension6 {

    protected int offset;
    protected int exitPos;
    protected Type type;
    protected String initialName;
    protected String[] nameProposals;
    protected final int currentOffset;

    @Override
    protected String getHintTemplate() {
        return "Enter type and name for new local {0}";
    }

    @Override
    protected final void updatePopupLocation() {
        LinkedPosition currentLinkedPosition = 
                getCurrentLinkedPosition();
        RefactorInformationPopup popup = getInfoPopup();
        if (currentLinkedPosition==null) {
            popup.setHintTemplate(getHintTemplate());
        }
        else if (currentLinkedPosition.getSequenceNumber()==1) {
            popup.setHintTemplate("Enter type for new local {0}");
        }
        else {
            popup.setHintTemplate("Enter name for new local {0}");
        }
    }

    private void performInitialChange(IDocument document) {
    
        Tree.Statement st = findStatement(rootNode, node);
        Node expression;
        Node expanse;
        Type resultType;
        Unit unit = node.getUnit();
        if (st instanceof Tree.ExpressionStatement) {
            Tree.ExpressionStatement es = 
                    (Tree.ExpressionStatement) st;
            Tree.Expression e = es.getExpression();
            expression = e;
            expanse = st;
            resultType = e.getTypeModel();
            Tree.Term term = e.getTerm();
            if (term instanceof Tree.InvocationExpression) {
                Tree.InvocationExpression ie = 
                        (Tree.InvocationExpression) term;
                Tree.Primary primary = ie.getPrimary();
                if (primary instanceof Tree.QualifiedMemberExpression) {
                    Tree.QualifiedMemberExpression prim = 
                            (Tree.QualifiedMemberExpression) 
                                primary;
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
                    dec.getAnnotationList()
                        .getAnnotations();
            Tree.AnonymousAnnotation aa = 
                    dec.getAnnotationList()
                        .getAnonymousAnnotation();
            if (aa!=null && currentOffset<=aa.getEndIndex()) {
                expression = aa;
                expanse = expression;
                resultType = unit.getStringType();
            }
            else if (!annotations.isEmpty() && 
                    currentOffset<=dec.getAnnotationList().getEndIndex()) {
                Tree.Annotation a = annotations.get(0);
                expression = a;
                expanse = expression;
                resultType = a.getTypeModel();
            }
            else if (st instanceof Tree.TypedDeclaration) {
                //some expressions look like a type declaration
                //when they appear right in front of an annotation
                //or function invocations
                TypedDeclaration td = 
                        (Tree.TypedDeclaration) st;
                Tree.Type type = td.getType();
                Type t = type.getTypeModel();
                if (type instanceof Tree.SimpleType) {
                    expression = type;
                    expanse = expression;
                    resultType = t;
                }
                else if (type instanceof Tree.FunctionType) {
                    expression = type;
                    expanse = expression;
                    resultType = unit.getCallableReturnType(t);
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
    
        int startIndex = expanse.getStartIndex();
        int endIndex = expanse.getEndIndex();
        if (currentOffset<startIndex || 
                currentOffset>endIndex) {
            return;
        }
        nameProposals = computeNameProposals(expression);
        initialName = nameProposals[0];
        offset = startIndex;
        type = resultType==null ? null : 
            unit.denotableType(resultType);
    
        DocumentChange change = 
                createChange(document, expanse, endIndex);
        EditorUtil.performChange(change);
    }

    String[] computeNameProposals(Node expression) {
        return Nodes.nameProposals(expression);
    }
    
    protected abstract DocumentChange createChange(
            IDocument document, Node expanse, int endIndex);

    protected final Node node;
    protected final Tree.CompilationUnit rootNode;

    @Override
    public void apply(IDocument document) {
        try {
            performInitialChange(document);
            CeylonParseController cpc = 
                    editor.getParseController();
            Unit unit = cpc.getLastCompilationUnit().getUnit();
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
    
    protected abstract void addLinkedPositions(
            IDocument document, Unit unit)
                    throws BadLocationException;

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), 
                false, true);
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

    public LocalProposal(CeylonEditor ceylonEditor, 
            Tree.CompilationUnit cu, Node node, 
            int currentOffset) {
        super(ceylonEditor);
        this.rootNode = cu;
        this.node = node;
        this.currentOffset = currentOffset;
    }
    
    boolean isEnabled(Type resultType) {
        return true;
    }

    boolean isEnabled() {
        Tree.Statement st = findStatement(rootNode, node);
        if (st instanceof Tree.ExpressionStatement) {
            Tree.ExpressionStatement es = 
                    (Tree.ExpressionStatement) st;
            Tree.Expression e = es.getExpression();
            Type resultType = e.getTypeModel();
            Tree.Term term = e.getTerm();
            if (term instanceof Tree.InvocationExpression) {
                InvocationExpression ie = 
                        (Tree.InvocationExpression) term;
                Tree.Primary primary = ie.getPrimary();
                if (primary instanceof Tree.QualifiedMemberExpression) {
                    Tree.QualifiedMemberExpression prim = 
                            (Tree.QualifiedMemberExpression) 
                                primary;
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
            Unit unit = node.getUnit();
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
                    dec.getAnnotationList()
                        .getAnnotations();
            Tree.AnonymousAnnotation aa = 
                    dec.getAnnotationList()
                        .getAnonymousAnnotation();
            Type resultType;
            if (aa!=null && currentOffset<=aa.getEndIndex()) {
                if (aa.getEndToken().getLine()==line) {
                    return false;
                }
                resultType = unit.getStringType();
            }
            else if (!annotations.isEmpty() && 
                    currentOffset<=dec.getAnnotationList().getEndIndex()) {
                Tree.Annotation a = annotations.get(0);
                if (a.getEndToken().getLine()==line) {
                    return false;
                }
                resultType = a.getTypeModel();
            }
            else if (st instanceof Tree.TypedDeclaration &&
                    !(st instanceof Tree.ObjectDefinition)) {
                //some expressions look like a type declaration
                //when they appear right in front of an annotation
                //or function invocations
                TypedDeclaration td = 
                        (Tree.TypedDeclaration) st;
                Tree.Type type = td.getType();
                if (currentOffset<=type.getEndIndex() &&
                    currentOffset>=type.getStartIndex() &&
                    type.getEndToken().getLine()!=line) {
                    resultType = type.getTypeModel();
                    if (type instanceof Tree.SimpleType) {
                        //just use that type
                    }
                    else if (type instanceof Tree.FunctionType) {
                        //instantiation expressions look like a
                        //function type declaration
                        resultType = 
                                unit.getCallableReturnType(
                                        resultType);
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