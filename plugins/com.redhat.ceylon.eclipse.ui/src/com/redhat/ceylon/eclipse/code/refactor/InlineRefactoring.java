package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;
import static com.redhat.ceylon.eclipse.code.quickfix.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.ImportProposals.importDeclaration;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createFatalErrorStatus;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;
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
	    return  declaration!=null &&
	            project != null &&
	            inSameProject(declaration) &&
	            declaration instanceof MethodOrValue &&
	            !(declaration instanceof Setter) &&
	            !declaration.isDefault() &&
	            !declaration.isFormal() &&
	            (((MethodOrValue)declaration).getTypeDeclaration()!=null) &&
	            (!((MethodOrValue)declaration).getTypeDeclaration().isAnonymous()) &&
	            (declaration.isToplevel() || !declaration.isShared()); //TODO temporary restriction!
	            //TODO: && !declaration is a control structure variable 
	            //TODO: && !declaration is a value with lazy init
	}
	
	public int getCount() {
        return declaration==null ? 0 : countDeclarationOccurrences();
	}
	
	@Override
	int countReferences(Tree.CompilationUnit cu) {
        FindReferenceVisitor frv = new FindReferenceVisitor(declaration);
        cu.visit(frv);
        return frv.getNodes().size();
	}

	public String getName() {
		return "Inline";
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		final RefactoringStatus result = new RefactoringStatus();
        Tree.Declaration declarationNode=null;
        Tree.CompilationUnit declarationUnit=null;
        if (searchInEditor()) {
        	Tree.CompilationUnit cu = editor.getParseController().getRootNode();
            if (cu.getUnit().equals(declaration.getUnit())) {
                declarationUnit = cu;
            }
        }
        if (declarationUnit==null) {
            for (final PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                if (pu.getUnit().equals(declaration.getUnit())) {
                    declarationUnit = pu.getCompilationUnit();
                    break;
                }
            }
        }
        FindDeclarationNodeVisitor fdv = new FindDeclarationNodeVisitor(declaration);
        declarationUnit.visit(fdv);
        declarationNode = fdv.getDeclarationNode();
        if (declarationNode instanceof Tree.AttributeDeclaration &&
                ((Tree.AttributeDeclaration) declarationNode).getSpecifierOrInitializerExpression()==null ||
            declarationNode instanceof Tree.MethodDeclaration &&
                ((Tree.MethodDeclaration) declarationNode).getSpecifierExpression()==null) {
            return createFatalErrorStatus("Cannot inline forward declaration: " + 
                declaration.getName());
        }
        if (declarationNode instanceof Tree.MethodDefinition &&
        		((Tree.MethodDefinition) declarationNode).getBlock().getStatements().size()!=1 ||
        	declarationNode instanceof Tree.AttributeGetterDefinition &&
        		((Tree.AttributeGetterDefinition) declarationNode).getBlock().getStatements().size()!=1) {
        	return createFatalErrorStatus("Cannot inline declaration with multiple statements: " + 
                    declaration.getName());
        }
		if (declarationNode instanceof Tree.AnyAttribute &&
				((Tree.AnyAttribute) declarationNode).getDeclarationModel().isVariable()) {
			result.merge(createWarningStatus("Inlined value is variable"));
		}
        declarationNode.visit(new Visitor() {
        	@Override
        	public void visit(Tree.BaseMemberOrTypeExpression that) {
        		super.visit(that);
        		if (that.getDeclaration()==null) {
        			result.merge(createWarningStatus("Definition contains unresolved reference"));
        		}
        		else if (declaration.isShared() &&
        				!that.getDeclaration().isShared()) {
        			result.merge(createWarningStatus("Definition contains reference to unshared declaration: " +
        					that.getDeclaration().getName()));
        		}
        	}
		});
		return result;
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

        Tree.Declaration declarationNode=null;
        Tree.CompilationUnit declarationUnit=null;
        Tree.Term term = null;
        List<CommonToken> declarationTokens = null;
        Tree.CompilationUnit editorRootNode = editor.getParseController().getRootNode();
		List<CommonToken> editorTokens = editor.getParseController().getTokens();
		if (declaration!=null) {
            if (searchInEditor()) {
            	Tree.CompilationUnit cu = editorRootNode;
                if (cu.getUnit().equals(declaration.getUnit())) {
                    declarationUnit = cu;
                    declarationTokens = editorTokens;
                }
            }
            if (declarationUnit==null) {
                for (final PhasedUnit pu: getAllUnits()) {
                    if (pu.getUnit().equals(declaration.getUnit())) {
                        declarationUnit = pu.getCompilationUnit();
                        declarationTokens = pu.getTokens();
                        break;
                    }
                }
            }
            FindDeclarationNodeVisitor fdv = new FindDeclarationNodeVisitor(declaration);
            declarationUnit.visit(fdv);
            declarationNode = fdv.getDeclarationNode();
            term = getInlinedTerm(declarationNode);
        }
		
        CompositeChange cc = new CompositeChange(getName());
        if (declarationNode!=null) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu)) {
                    TextFileChange tfc = newTextFileChange(pu);
                    Tree.CompilationUnit cu = pu.getCompilationUnit();
					inlineInFile(tfc, cc, declarationNode, 
							declarationUnit, term, declarationTokens, cu, 
							pu.getTokens());
                }
    		}
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            inlineInFile(dc, cc, declarationNode, declarationUnit, 
                    term, declarationTokens,
                    editorRootNode, editorTokens);
        }
        return cc;
        
	}
	
	private boolean addImports(final TextChange change, 
			final Tree.Declaration declarationNode, 
			final Tree.CompilationUnit cu) {
		
		final Package decPack = declarationNode.getUnit().getPackage();
		final Package filePack = cu.getUnit().getPackage();
		
		final class AddImportsVisitor extends Visitor {
			private final Set<Declaration> already;
			boolean importedFromDeclarationPackage;

			private AddImportsVisitor(Set<Declaration> already) {
				this.already = already;
			}

			@Override
			public void visit(Tree.BaseMemberOrTypeExpression that) {
				super.visit(that);
				if (that.getDeclaration()!=null) {
					importDeclaration(already, that.getDeclaration(), cu);
					Package refPack = that.getDeclaration().getUnit().getPackage();
					importedFromDeclarationPackage = importedFromDeclarationPackage ||
							//result!=0 &&
							refPack.equals(decPack) && 
							!decPack.equals(filePack); //unnecessary
				}
			}
		}

		final Set<Declaration> already = new HashSet<Declaration>();
		AddImportsVisitor aiv = new AddImportsVisitor(already);
		declarationNode.visit(aiv);
		applyImports(change, already, 
				declarationNode.getDeclarationModel(), cu, document);
		return aiv.importedFromDeclarationPackage;
	}

    private void inlineInFile(TextChange tfc, CompositeChange cc, 
            Tree.Declaration declarationNode, Tree.CompilationUnit declarationUnit, 
            Tree.Term term, List<CommonToken> declarationTokens,
            Tree.CompilationUnit cu, List<CommonToken> tokens) {
        tfc.setEdit(new MultiTextEdit());
        inlineReferences(declarationNode, declarationUnit, term, declarationTokens, 
                cu, tokens, tfc);
        boolean inlined = tfc.getEdit().hasChildren();
        deleteDeclaration(declarationNode, declarationUnit, cu, tokens, tfc);
        boolean importsAddedToDeclarationPackage = false;
		if (inlined) {
			importsAddedToDeclarationPackage = addImports(tfc, declarationNode, cu);
		}
        deleteImports(tfc, declarationNode, cu, tokens, importsAddedToDeclarationPackage);
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

	private void deleteImports(TextChange tfc, Tree.Declaration declarationNode, 
			Tree.CompilationUnit cu, List<CommonToken> tokens, 
			boolean importsAddedToDeclarationPackage) {
		Tree.ImportList il = cu.getImportList();
		if (il!=null) {
			for (Tree.Import i: il.getImports()) {
				List<Tree.ImportMemberOrType> list = i.getImportMemberOrTypeList()
						.getImportMemberOrTypes();
				for (Tree.ImportMemberOrType imt: list) {
					Declaration d = imt.getDeclarationModel();
					if (d!=null && d.equals(declarationNode.getDeclarationModel())) {
						if (list.size()==1 && !importsAddedToDeclarationPackage) {
							//delete the whole import statement
							tfc.addEdit(new DeleteEdit(i.getStartIndex(), 
									i.getStopIndex()-i.getStartIndex()+1));
						}
						else {
							//delete just the item in the import statement...
							tfc.addEdit(new DeleteEdit(imt.getStartIndex(), 
									imt.getStopIndex()-imt.getStartIndex()+1));
							//...along with a comma before or after
							int ti = getTokenIndexAtCharacter(tokens, imt.getStartIndex());
							CommonToken prev = tokens.get(ti-1);
							if (prev.getChannel()==CommonToken.HIDDEN_CHANNEL) {
								prev = tokens.get(ti-2);
							}
							CommonToken next = tokens.get(ti+1);
							if (next.getChannel()==CommonToken.HIDDEN_CHANNEL) {
								next = tokens.get(ti+2);
							}
							if (prev.getType()==CeylonLexer.COMMA) {
								tfc.addEdit(new DeleteEdit(prev.getStartIndex(), 
										imt.getStartIndex()-prev.getStartIndex()));
							}
							else if (next.getType()==CeylonLexer.COMMA) {
								tfc.addEdit(new DeleteEdit(imt.getStopIndex()+1, 
										next.getStopIndex()-imt.getStopIndex()));
							}
						}
					}
				}
        	}
        }
	}

    private void deleteDeclaration(Tree.Declaration declarationNode, 
    		Tree.CompilationUnit declarationUnit, Tree.CompilationUnit cu, 
            List<CommonToken> tokens, TextChange tfc) {
        if (delete && cu.getUnit().equals(declarationUnit.getUnit())) {
            CommonToken from = (CommonToken) declarationNode.getToken();
            Tree.AnnotationList anns = declarationNode.getAnnotationList();
            if (!anns.getAnnotations().isEmpty()) {
                from = (CommonToken) anns.getAnnotations().get(0).getToken();
            }
            int prevIndex = from.getTokenIndex()-1;
            if (prevIndex>=0) {
                CommonToken tok = tokens.get(prevIndex);
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
        			//TODO: in the case of a void method, tolerate 
        			//      multiple statements 
        			Tree.ExpressionStatement e = (Tree.ExpressionStatement) meth.getBlock()
        					.getStatements().get(0);
        			return e.getExpression().getTerm();
        			
        		}
        		else {
        			Tree.Return r = (Tree.Return) meth.getBlock().getStatements().get(0);
        			return r.getExpression().getTerm();
        		}
        	}
            else if (declarationNode instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration meth = (Tree.MethodDeclaration) declarationNode;
                return meth.getSpecifierExpression().getExpression().getTerm();
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
    		Tree.CompilationUnit declarationUnit, Tree.Term term, 
            List<CommonToken> declarationTokens, Tree.CompilationUnit pu, 
            List<CommonToken> tokens, TextChange tfc) {
        String template = toString(term, declarationTokens);
        int templateStart = term.getStartIndex();
        if (declarationNode instanceof Tree.AnyAttribute) {
        	inlineAttributeReferences(pu, template, tfc);
        }
        else if (declarationNode instanceof Tree.AnyMethod) {
        	inlineFunctionReferences(pu, tokens, term, template, templateStart, tfc);
        }
    }

    private void inlineFunctionReferences(final Tree.CompilationUnit pu, final List<CommonToken> tokens,
            final Tree.Term term, final String template, final int templateStart, 
            final TextChange tfc) {
        new Visitor() {
        	@Override
        	public void visit(final Tree.InvocationExpression that) {
        		super.visit(that);
        		if (that.getPrimary() instanceof Tree.MemberOrTypeExpression) {
        		    Tree.MemberOrTypeExpression mte = (Tree.MemberOrTypeExpression) that.getPrimary();
        		    if (mte.getDeclaration().equals(declaration)) {
        		        //TODO: breaks for invocations like f(f(x, y),z)
        		        final StringBuilder result = new StringBuilder();
        		        class InterpolateArgumentsVisitor extends Visitor {
        		            int start = 0;
        		            @Override
        		            public void visit(Tree.BaseMemberExpression it) {
        		                super.visit(it);
        		                if (it.getDeclaration().isParameter()) {
        		                    Parameter param = ((MethodOrValue) it.getDeclaration()).getInitializerParameter();
        		                    if ( param.getDeclaration().equals(declaration) ) {
        		                        result.append(template.substring(start,it.getStartIndex()-templateStart));
        		                        start = it.getStopIndex()-templateStart+1;
        		                        boolean sequenced = param.isSequenced();
        		                        if (that.getPositionalArgumentList()!=null) {
        		                            interpolatePositionalArguments(result, that, it, sequenced, tokens);
        		                        }
        		                        if (that.getNamedArgumentList()!=null) {
        		                            interpolateNamedArguments(result, that, it, sequenced, tokens);
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
        	}
        }.visit(pu);
    }

    private void inlineAttributeReferences(final Tree.CompilationUnit pu, final String template,
            final TextChange tfc) {
        new Visitor() {
            @Override
            public void visit(Tree.Variable that) {
                if (that.getType() instanceof Tree.SyntheticVariable) {
                    TypedDeclaration od = that.getDeclarationModel().getOriginalDeclaration();
					if (od!=null && od.equals(declaration)) {
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
        }.visit(pu);
    }

    private static void interpolatePositionalArguments(StringBuilder result, 
            Tree.InvocationExpression that, Tree.BaseMemberExpression it,
            boolean sequenced, List<CommonToken> tokens) {
        boolean first = true;
        boolean found = false;
        if (sequenced) {
            result.append("{");
        }
        for (Tree.PositionalArgument arg: that.getPositionalArgumentList()
                .getPositionalArguments()) {
            if (it.getDeclaration().equals(arg.getParameter().getModel())) {
                if (arg.getParameter().isSequenced() &&
                		(arg instanceof Tree.ListedArgument)) {
                    if (first) result.append(" ");
                    if (!first) result.append(", ");
                    first = false;
                }
                result.append(AbstractRefactoring.toString(arg, 
                		tokens));
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
            boolean sequenced, List<CommonToken> tokens) {
        boolean found = false;
        for (Tree.NamedArgument arg: that.getNamedArgumentList().getNamedArguments()) {
            if (it.getDeclaration().equals(arg.getParameter())) {
            	Tree.Term argTerm = ((Tree.SpecifiedArgument) arg).getSpecifierExpression()
                                .getExpression().getTerm();
                result//.append(template.substring(start,it.getStartIndex()-templateStart))
                    .append(AbstractRefactoring.toString(argTerm, tokens) );
                //start = it.getStopIndex()-templateStart+1;
                found=true;
            }
        }
        Tree.SequencedArgument seqArg = that.getNamedArgumentList().getSequencedArgument();
        if (seqArg!=null && it.getDeclaration().equals(seqArg.getParameter())) {
            result//.append(template.substring(start,it.getStartIndex()-templateStart))
                .append("{");
            //start = it.getStopIndex()-templateStart+1;;
            boolean first=true;
            for (Tree.PositionalArgument pa: seqArg.getPositionalArguments()) {
                if (first) result.append(" ");
                if (!first) result.append(", ");
                first=false;
                result.append(AbstractRefactoring.toString(pa, tokens));
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
