package com.redhat.ceylon.eclipse.imp.refactoring;

import java.util.Iterator;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SequencedArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.ui.FindDeclarationVisitor;
import com.redhat.ceylon.compiler.typechecker.ui.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class InlineRefactoring extends Refactoring {
	private final IProject fSourceProject;
	//private final Node fNode;
	//private final ITextEditor fEditor;
	//private final CeylonParseController parseController;
	private final Declaration dec;
	private boolean delete = true;
	private int count;

	public InlineRefactoring(ITextEditor editor) {

		//fEditor = editor;

		IASTFindReplaceTarget frt = (IASTFindReplaceTarget) editor;
		IEditorInput input = editor.getEditorInput();
		//parseController = (CeylonParseController) frt.getParseController();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			fSourceProject = fileInput.getFile().getProject();
			Node node = findNode((CeylonParseController) frt.getParseController(), frt);
			dec = CeylonReferenceResolver.getReferencedDeclaration(node);
			FindReferenceVisitor frv = new FindReferenceVisitor(dec) {
				@Override
				public void visit(Tree.ExtendedTypeExpression that) {}
	            @Override
	            protected boolean equals(Declaration x, Declaration y) {
	                //TODO: surely there's got to be a more robust
	                //      way to do this:
	                try {
	                    return x.getQualifiedName().equals(y.getQualifiedName());
	                }
	                catch (UnsupportedOperationException uoe) {
	                    //a union or intersection type
	                    return false;
	                }
	            }
			};
			count = 0;
	        for (PhasedUnit pu: CeylonBuilder.getUnits(fSourceProject)) {
	            pu.getCompilationUnit().visit(frv);
	            count += frv.getNodes().size();
	            frv.getNodes().clear();
	        }
		} 
		else {
		    fSourceProject = null;
			dec = null;
			count = 0;
		}
	}
	
	public int getCount() {
		return count;
	}

	private static Node findNode(CeylonParseController cpc, IASTFindReplaceTarget frt) {
		return cpc.getSourcePositionLocator()
				.findNode(cpc.getRootNode(), frt.getSelection().x, 
						frt.getSelection().x+frt.getSelection().y);
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
        CompositeChange cc = new CompositeChange("Inline");
        Tree.Declaration declarationNode=null;
        PhasedUnit declarationUnit=null;
        Tree.Term t=null;
        
        if (dec!=null) {
            for (final PhasedUnit pu: CeylonBuilder.getUnits(fSourceProject)) {
    			FindDeclarationVisitor fdv = new FindDeclarationVisitor(dec) {
    			    @Override
    			    protected boolean equals(Declaration x, Declaration y) {
                        return x.getQualifiedName().equals(y.getQualifiedName());
    			    }
    			};
    			pu.getCompilationUnit().visit(fdv);
    			declarationNode = fdv.getDeclarationNode();
    			if (declarationNode!=null) {
                    declarationUnit = pu;
        			if (declarationNode instanceof Tree.AttributeDeclaration) {
        				Tree.AttributeDeclaration att = (Tree.AttributeDeclaration) declarationNode;
        				t = att.getSpecifierOrInitializerExpression().getExpression().getTerm();
        			}
        			else if (declarationNode instanceof Tree.MethodDefinition) {
        				Tree.MethodDefinition meth = (Tree.MethodDefinition) declarationNode;
        				if (meth.getBlock().getStatements().size()!=1) {
        					throw new RuntimeException("method has multiple statements");
        				}
        				if (meth.getType() instanceof Tree.VoidModifier) {
        					Tree.ExpressionStatement e = (Tree.ExpressionStatement) meth.getBlock()
        							.getStatements().get(0);
        					t = e.getExpression().getTerm();
        					
        				}
        				else {
        					Tree.Return r = (Tree.Return) meth.getBlock().getStatements().get(0);
        					t = r.getExpression().getTerm();
        				}
        			}
        			else if (declarationNode instanceof Tree.AttributeGetterDefinition) {
        				Tree.AttributeGetterDefinition att = (Tree.AttributeGetterDefinition) declarationNode;
        				if (att.getBlock().getStatements().size()!=1) {
        					throw new RuntimeException("getter has multiple statements");
        				}
        				Tree.Return r = (Tree.Return) att.getBlock().getStatements().get(0);
        				t = r.getExpression().getTerm();
        			}
        			else {
        				throw new RuntimeException("not a value or function");
        			}
                    break;
    			}
    		}
        }
        final Tree.Term term = t;
		
        if (declarationNode!=null) {
            for (final PhasedUnit pu: CeylonBuilder.getUnits(fSourceProject)) {
                final TextFileChange tfc = new TextFileChange("Inline", CeylonBuilder.getFile(pu));
                tfc.setEdit(new MultiTextEdit());
    			final String template = toString(term, declarationUnit.getTokenStream());
    			final int templateStart = term.getStartIndex();
    			if (declarationNode instanceof Tree.AnyAttribute) {
    				new Visitor() {
    					@Override
    					public void visit(Tree.BaseMemberExpression that) {
    						super.visit(that);
    						if (that.getDeclaration().getQualifiedName().equals(dec.getQualifiedName())) {
    							tfc.addEdit(new ReplaceEdit(that.getStartIndex(), 
    									that.getStopIndex()-that.getStartIndex()+1, 
    									template));	
    						}
    					}
    				}.visit(pu.getCompilationUnit());
    			}
    			else {
    				new Visitor() {
    					@Override
    					public void visit(final Tree.InvocationExpression that) {
    						super.visit(that);
    						if (that.getPrimary().getDeclaration().getQualifiedName()
    						        .equals(dec.getQualifiedName())) {
    							//TODO: breaks for invocations like f(f(x, y),z)
    							final StringBuilder result = new StringBuilder();
    							class InterpolateVisitor extends Visitor {
    								int start = 0;
    								@Override
    								public void visit(Tree.BaseMemberExpression it) {
    									super.visit(it);
    									if (it.getDeclaration() instanceof Parameter) {
    										Parameter param = (Parameter) it.getDeclaration();
    										if ( param.getDeclaration().getQualifiedName().equals(dec.getQualifiedName()) ) {
    											result.append(template.substring(start,it.getStartIndex()-templateStart));
    											start = it.getStopIndex()-templateStart+1;
    											boolean sequenced = param.isSequenced();
    											if (that.getPositionalArgumentList()!=null) {
    												interpolatePositionalArguments(result, that, it, sequenced);
    											}
    											if (that.getNamedArgumentList()!=null) {
    												interpolateNamedArguments(result, that, it, sequenced);
    											}
    										}
    									}
    								}
    								private void interpolatePositionalArguments(StringBuilder result, 
    										Tree.InvocationExpression that, Tree.BaseMemberExpression it,
    										boolean sequenced) {
    									boolean first = true;
    									boolean found = false;
    									if (sequenced) {
    										result.append("{");
    									}
    									for (Tree.PositionalArgument arg: that.getPositionalArgumentList()
    											.getPositionalArguments()) {
    										if (it.getDeclaration()==arg.getParameter()) {
    											if (arg.getParameter().isSequenced() && 
    													that.getPositionalArgumentList().getEllipsis()==null) {
    												if (first) result.append(" ");
    												if (!first) result.append(", ");
    												first = false;
    											}
    											result.append(InlineRefactoring.this.
    													toString(arg.getExpression().getTerm(), pu.getTokenStream()));
    											found = true;
    										}
    									}
    									if (sequenced) {
    										if (!first) result.append(" ");
    										result.append("}");
    									}
    									if (!found) {} //TODO: use default value!
    								}
    								private void interpolateNamedArguments(StringBuilder result,
    										Tree.InvocationExpression that, Tree.BaseMemberExpression it,
    										boolean sequenced) {
    									boolean found = false;
    									for (Tree.NamedArgument arg: that.getNamedArgumentList().getNamedArguments()) {
    										if (it.getDeclaration()==arg.getParameter()) {
    											result//.append(template.substring(start,it.getStartIndex()-templateStart))
    												.append(InlineRefactoring.this.
    														toString( ((Tree.SpecifiedArgument) arg).getSpecifierExpression()
    																.getExpression().getTerm(), pu.getTokenStream()) );
    											//start = it.getStopIndex()-templateStart+1;
    											found=true;
    										}
    									}
    									SequencedArgument seqArg = that.getNamedArgumentList().getSequencedArgument();
    									if (seqArg!=null && it.getDeclaration()==seqArg.getParameter()) {
    										result//.append(template.substring(start,it.getStartIndex()-templateStart))
    										    .append("{");
    										//start = it.getStopIndex()-templateStart+1;;
    										boolean first=true;
    										for (Tree.Expression e: seqArg.getExpressionList().getExpressions()) {
    											if (first) result.append(" ");
    											if (!first) result.append(", ");
    											first=false;
    											result.append(InlineRefactoring.this.toString(e.getTerm(), pu.getTokenStream()));
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
    								void finish() {
    									result.append(template.substring(start, template.length()));
    								}
    							}
    							InterpolateVisitor iv = new InterpolateVisitor();
    							iv.visit(term);
    							iv.finish();
    							tfc.addEdit(new ReplaceEdit(that.getStartIndex(), 
    									that.getStopIndex()-that.getStartIndex()+1, 
    									result.toString()));
    						}
    					}
    				}.visit(pu.getCompilationUnit());
    			}
                if (delete && pu==declarationUnit) {
                    CommonToken from = (CommonToken) declarationNode.getToken();
                    Tree.AnnotationList anns = declarationNode.getAnnotationList();
                    if (!anns.getAnnotations().isEmpty()) {
                        from = (CommonToken) anns.getAnnotations().get(0).getToken();
                    }
                    int prevIndex = from.getTokenIndex()-1;
                    if (prevIndex>=0) {
                        CommonToken tok = (CommonToken) pu.getTokenStream()
                                .get(prevIndex);
                        if (tok.getChannel()==Token.HIDDEN_CHANNEL) {
                            from=tok;
                        }
                    }
                    tfc.addEdit(new DeleteEdit(from.getStartIndex(), 
                            declarationNode.getStopIndex()-from.getStartIndex()+1));
                }
    			if (tfc.getEdit().hasChildren()) {
    			    cc.add(tfc);
    			}
    		}
        }
        return cc;
	}

	private String toString(final Tree.Term t, CommonTokenStream tokenStream) {
		Integer start = t.getStartIndex();
		int length = t.getStopIndex()-start+1;
		Region region = new Region(start, length);
		StringBuilder exp = new StringBuilder();
		for (Iterator<Token> ti = CeylonParseController.getTokenIterator(tokenStream, region); 
				ti.hasNext();) {
			exp.append(ti.next().getText());
		}
		return exp.toString();
	}

	public Declaration getDeclaration() {
		return dec;
	}
	
	public void setDelete() {
		this.delete = !delete;
	}
}
