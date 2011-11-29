package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SequencedArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class InlineRefactoring extends AbstractRefactoring {
	private final Declaration declaration;
	private boolean delete = true;

	public InlineRefactoring(ITextEditor editor) {
	    super(editor);
		declaration = getReferencedDeclaration(node);
	}
	
	@Override
	boolean isEnabled() {
	    return declaration!=null && 
	            declaration instanceof MethodOrValue &&
	            !(declaration instanceof Setter) &&
	            !declaration.isDefault() &&
	            !declaration.isFormal() &&
	            !declaration.isShared(); //TODO temporary restriction!
	            //TODO && !declaration is a value with lazy init
	            
	}
	
	public int getCount() {
        if (declaration==null) {
            return 0; 
        }
        else {
            int count = 0;
            for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                FindReferenceVisitor frv = new FindReferenceVisitor(declaration);
                pu.getCompilationUnit().visit(frv);
                count += frv.getNodes().size();
            }
            return count;
        }
	}

	public String getName() {
		return "Inline";
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		// Check parameters retrieved from editor context
		return new RefactoringStatus();
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

        Tree.Declaration declarationNode=null;
        PhasedUnit declarationUnit=null;
        Tree.Term term = null;
        if (declaration!=null) {
            for (final PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                if (pu.getUnit().equals(declaration.getUnit())) {
                    declarationUnit = pu;
        			FindDeclarationVisitor fdv = new FindDeclarationVisitor(declaration);
        			pu.getCompilationUnit().visit(fdv);
        			declarationNode = fdv.getDeclarationNode();
        			term = getInlinedTerm(declarationNode);
                    break;
                }
    		}
        }
		
        CompositeChange cc = new CompositeChange("Inline");
        if (declarationNode!=null) {
            for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                TextFileChange tfc = new TextFileChange("Inline", CeylonBuilder.getFile(pu));
                tfc.setEdit(new MultiTextEdit());
                inlineReferences(declarationNode, declarationUnit, term, pu, tfc);
                deleteDeclaration(declarationNode, declarationUnit, pu, tfc);
    			if (tfc.getEdit().hasChildren()) {
    			    cc.add(tfc);
    			}
    		}
        }
        return cc;
        
	}

    private void deleteDeclaration(Tree.Declaration declarationNode, PhasedUnit declarationUnit,
            PhasedUnit pu, TextFileChange tfc) {
        if (delete && pu==declarationUnit) {
            CommonToken from = (CommonToken) declarationNode.getToken();
            Tree.AnnotationList anns = declarationNode.getAnnotationList();
            if (!anns.getAnnotations().isEmpty()) {
                from = (CommonToken) anns.getAnnotations().get(0).getToken();
            }
            int prevIndex = from.getTokenIndex()-1;
            if (prevIndex>=0) {
                CommonToken tok = (CommonToken) pu.getTokens()
                        .get(prevIndex);
                if (tok.getChannel()==Token.HIDDEN_CHANNEL) {
                    from=tok;
                }
            }
            tfc.addEdit(new DeleteEdit(from.getStartIndex(), 
                    declarationNode.getStopIndex()-from.getStartIndex()+1));
        }
    }

    private static Tree.Term getInlinedTerm(Tree.Declaration declarationNode) {
        if (declarationNode!=null) {
        	if (declarationNode instanceof Tree.AttributeDeclaration) {
        		Tree.AttributeDeclaration att = (Tree.AttributeDeclaration) declarationNode;
        		return att.getSpecifierOrInitializerExpression().getExpression().getTerm();
        	}
        	else if (declarationNode instanceof Tree.MethodDefinition) {
        		Tree.MethodDefinition meth = (Tree.MethodDefinition) declarationNode;
        		if (meth.getBlock().getStatements().size()!=1) {
        			throw new RuntimeException("method has multiple statements");
        		}
        		if (meth.getType() instanceof Tree.VoidModifier) {
        			Tree.ExpressionStatement e = (Tree.ExpressionStatement) meth.getBlock()
        					.getStatements().get(0);
        			return e.getExpression().getTerm();
        			
        		}
        		else {
        			Tree.Return r = (Tree.Return) meth.getBlock().getStatements().get(0);
        			return r.getExpression().getTerm();
        		}
        	}
        	else if (declarationNode instanceof Tree.AttributeGetterDefinition) {
        		Tree.AttributeGetterDefinition att = (Tree.AttributeGetterDefinition) declarationNode;
        		if (att.getBlock().getStatements().size()!=1) {
        			throw new RuntimeException("getter has multiple statements");
        		}
        		Tree.Return r = (Tree.Return) att.getBlock().getStatements().get(0);
        		return r.getExpression().getTerm();
        	}
        	else {
        		throw new RuntimeException("not a value or function");
        	}
        }
        else {
            return null;
        }
    }

    private void inlineReferences(Tree.Declaration declarationNode,
            PhasedUnit declarationUnit, final Tree.Term term, final PhasedUnit pu,
            final TextFileChange tfc) {
        String template = toString(term, declarationUnit.getTokens());
        int templateStart = term.getStartIndex();
        if (declarationNode instanceof Tree.AnyAttribute) {
        	inlineAttributeReferences(pu, template, tfc);
        }
        else {
        	inlineFunctionReferences(pu, term, template, templateStart, tfc);
        }
    }

    private void inlineFunctionReferences(final PhasedUnit pu, final Tree.Term term,
            final String template, final int templateStart, final TextFileChange tfc) {
        new Visitor() {
        	@Override
        	public void visit(final Tree.InvocationExpression that) {
        		super.visit(that);
        		if (that.getPrimary().getDeclaration().equals(declaration)) {
        			//TODO: breaks for invocations like f(f(x, y),z)
        			final StringBuilder result = new StringBuilder();
        			class InterpolateArgumentsVisitor extends Visitor {
        				int start = 0;
        				@Override
        				public void visit(Tree.BaseMemberExpression it) {
        					super.visit(it);
        					if (it.getDeclaration() instanceof Parameter) {
        						Parameter param = (Parameter) it.getDeclaration();
        						if ( param.getDeclaration().equals(declaration) ) {
        							result.append(template.substring(start,it.getStartIndex()-templateStart));
        							start = it.getStopIndex()-templateStart+1;
        							boolean sequenced = param.isSequenced();
        							if (that.getPositionalArgumentList()!=null) {
        								interpolatePositionalArguments(result, that, it, sequenced, pu);
        							}
        							if (that.getNamedArgumentList()!=null) {
        								interpolateNamedArguments(result, that, it, sequenced, pu);
        							}
        						}
        					}
        				}
        				void finish() {
        					result.append(template.substring(start, template.length()));
        				}
        			}
        			InterpolateArgumentsVisitor iv = new InterpolateArgumentsVisitor();
        			iv.visit(term);
        			iv.finish();
        			tfc.addEdit(new ReplaceEdit(that.getStartIndex(), 
        					that.getStopIndex()-that.getStartIndex()+1, 
        					result.toString()));
        		}
        	}
        }.visit(pu.getCompilationUnit());
    }

    private void inlineAttributeReferences(final PhasedUnit pu, final String template,
            final TextFileChange tfc) {
        new Visitor() {
            @Override
            public void visit(Tree.Variable that) {
                if (that.getType() instanceof Tree.SyntheticVariable) {
                    if (that.getDeclarationModel().getOriginalDeclaration()
                            .equals(declaration)) {
                        tfc.addEdit(new InsertEdit(that.getSpecifierExpression().getStartIndex(), 
                                that.getIdentifier().getText()+" = "));
                    }
                }
                super.visit(that);
            }
        	@Override
        	public void visit(Tree.BaseMemberExpression that) {
        		super.visit(that);
        		if (that.getDeclaration().equals(declaration)) {
        			tfc.addEdit(new ReplaceEdit(that.getStartIndex(), 
        					that.getStopIndex()-that.getStartIndex()+1, 
        					template));	
        		}
        	}
        }.visit(pu.getCompilationUnit());
    }

    private static void interpolatePositionalArguments(StringBuilder result, 
            Tree.InvocationExpression that, Tree.BaseMemberExpression it,
            boolean sequenced, PhasedUnit pu) {
        boolean first = true;
        boolean found = false;
        if (sequenced) {
            result.append("{");
        }
        for (Tree.PositionalArgument arg: that.getPositionalArgumentList()
                .getPositionalArguments()) {
            if (it.getDeclaration().equals(arg.getParameter())) {
                if (arg.getParameter().isSequenced() && 
                        that.getPositionalArgumentList().getEllipsis()==null) {
                    if (first) result.append(" ");
                    if (!first) result.append(", ");
                    first = false;
                }
                result.append(AbstractRefactoring.toString(arg.getExpression().getTerm(), 
                        pu.getTokens()));
                found = true;
            }
        }
        if (sequenced) {
            if (!first) result.append(" ");
            result.append("}");
        }
        if (!found) {} //TODO: use default value!
    }

    private static void interpolateNamedArguments(StringBuilder result,
            Tree.InvocationExpression that, Tree.BaseMemberExpression it,
            boolean sequenced, PhasedUnit pu) {
        boolean found = false;
        for (Tree.NamedArgument arg: that.getNamedArgumentList().getNamedArguments()) {
            if (it.getDeclaration().equals(arg.getParameter())) {
                Term argTerm = ((Tree.SpecifiedArgument) arg).getSpecifierExpression()
                                .getExpression().getTerm();
                result//.append(template.substring(start,it.getStartIndex()-templateStart))
                    .append(AbstractRefactoring.toString( argTerm, pu.getTokens()) );
                //start = it.getStopIndex()-templateStart+1;
                found=true;
            }
        }
        SequencedArgument seqArg = that.getNamedArgumentList().getSequencedArgument();
        if (seqArg!=null && it.getDeclaration().equals(seqArg.getParameter())) {
            result//.append(template.substring(start,it.getStartIndex()-templateStart))
                .append("{");
            //start = it.getStopIndex()-templateStart+1;;
            boolean first=true;
            for (Tree.Expression e: seqArg.getExpressionList().getExpressions()) {
                if (first) result.append(" ");
                if (!first) result.append(", ");
                first=false;
                result.append(AbstractRefactoring.toString(e.getTerm(), pu.getTokens()));
            }
            if (!first) result.append(" ");
            result.append("}");
            found=true;
        }
        if (!found) {
            if (sequenced) {
                result.append("{}");
            }
            else {} //TODO: use default value!
        }
    }
    
	public Declaration getDeclaration() {
		return declaration;
	}
	
	public void setDelete() {
		this.delete = !delete;
	}
}
