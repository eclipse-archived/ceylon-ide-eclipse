package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCommandBinding;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Annotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Primary;

class PrintProposal implements ICompletionProposal, ICompletionProposalExtension6 {
    
    private final Node node;
    private final Tree.CompilationUnit rootNode;
    private final int currentOffset;
    
    public PrintProposal(Tree.CompilationUnit cu, 
    		Node node, int currentOffset) {
        this.rootNode = cu;
        this.node = node;
		this.currentOffset = currentOffset;
    }
    
    @Override
    public void apply(IDocument document) {
    	
        Tree.Statement st = findStatement(rootNode, node);
        Node expression;
        Node expanse;
        if (st instanceof Tree.ExpressionStatement) {
            Tree.Expression e = 
                    ((Tree.ExpressionStatement) st).getExpression();
            expression = e;
            expanse = st;
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
            }
            else if (!annotations.isEmpty() && 
            		currentOffset<=dec.getAnnotationList().getStopIndex()+1) {
                Tree.Annotation a = annotations.get(0);
                expression = a;
                expanse = expression;
            }
            else if (st instanceof Tree.TypedDeclaration) {
                //some expressions look like a type declaration
                //when they appear right in front of an annotation
                //or function invocations
                Tree.Type type = ((Tree.TypedDeclaration) st).getType();
                if (type instanceof Tree.SimpleType || 
                    type instanceof Tree.FunctionType) {
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
//        
        Integer stopIndex = expanse.getStopIndex();
//        if (currentOffset<expanse.getStartIndex() || 
//            currentOffset>stopIndex+1) {
//            return;
//        }
        int offset = expanse.getStartIndex();
        
        DocumentChange change = 
        		new DocumentChange("Print Expression", document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(offset, "print("));
        
        String terminal = expanse.getEndToken().getText();
        String close = ")";
        if (!terminal.equals(";")) {
        	stopIndex++;
        	close = ");";
        }
        change.addEdit(new InsertEdit(stopIndex, close));
        
        try {
	        change.perform(new NullProgressMonitor());
        }
        catch (CoreException e) {
	        e.printStackTrace();
        }
        
    }
    

    @Override
    public Point getSelection(IDocument document) {
        return new Point(currentOffset+6,0);
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Print expression";
    }

    @Override
    public StyledString getStyledDisplayString() {
        TriggerSequence binding = 
                getCommandBinding("com.redhat.ceylon.eclipse.ui.action.print");
        String hint = binding==null ? "" : " (" + binding.format() + ")";
        return new StyledString(getDisplayString())
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

    @Override
    public Image getImage() {
        return MINOR_CHANGE;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
    static void addPrintProposal(Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, 
            Node node, int currentOffset) {
        PrintProposal prop = 
        		new PrintProposal(cu, node, currentOffset);
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
            Identifier id = dec.getIdentifier();
            if (id==null) {
                return false;
            }
            int line = id.getToken().getLine();
			Declaration d = dec.getDeclarationModel();
            if (d==null || d.isToplevel()) {
                return false;
            }
            //some expressions get interpreted as annotations
            Tree.AnnotationList al = dec.getAnnotationList();
            List<Annotation> annotations = 
                    al.getAnnotations();
            Tree.AnonymousAnnotation aa = 
            		al.getAnonymousAnnotation();
            if (aa!=null &&
            		currentOffset<=aa.getStopIndex()+1) {
                return aa.getEndToken().getLine()!=line;
            }
            else if (!annotations.isEmpty() &&
                    currentOffset<=dec.getAnnotationList().getStopIndex()+1) {
                return al.getEndToken().getLine()!=line;
            }
            else if (st instanceof Tree.TypedDeclaration) {
                //some expressions look like a type declaration
                //when they appear right in front of an annotation
                //or function invocations
                Tree.Type type = ((Tree.TypedDeclaration) st).getType();
                if (currentOffset<=type.getStopIndex()+1) {
                	return (type instanceof Tree.SimpleType || 
                	        type instanceof Tree.FunctionType) && 
                            currentOffset<=type.getStopIndex()+1 &&
                            currentOffset>=type.getStartIndex() &&
                	        type.getEndToken().getLine()!=line;
                }
            }
        }
        return false;
    }

}