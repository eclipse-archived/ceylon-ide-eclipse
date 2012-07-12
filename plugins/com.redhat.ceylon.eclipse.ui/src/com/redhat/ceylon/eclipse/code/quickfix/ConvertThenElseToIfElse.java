package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CHANGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AssignOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.InvocationExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Return;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ThenOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ValueModifier;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.code.editor.Util;

class ConvertThenElseToIfElse extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    ConvertThenElseToIfElse(int offset, IFile file, TextChange change) {
        super("Convert to if-else", change, 10, CHANGE);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }
    
    static void addConvertToGetterProposal(IDocument doc,
    			Collection<ICompletionProposal> proposals, IFile file,
    			Statement statement) {
    	try {
    		String action;
    		String declaration = null;
    		Node operation;
    		if (statement instanceof Tree.Return) {
    			Tree.Return returnOp = (Return) statement;
    			action = "return ";
    			if (returnOp.getExpression() == null || returnOp.getExpression().getTerm() == null) {
    				return;
    			}
    			operation = returnOp.getExpression().getTerm();
    			
    		} else if (statement instanceof Tree.ExpressionStatement) {
    			Tree.ExpressionStatement expressionStmt = (Tree.ExpressionStatement) statement;
    			if (expressionStmt.getExpression() == null) {
    				return;
    			}
    			Tree.Expression expression = expressionStmt.getExpression();
    			if (expression.getChildren().isEmpty()) {
    				return;
    			}
    			if (! (expression.getChildren().get(0) instanceof Tree.AssignOp)) {
    				return;
    			}
				Tree.AssignOp assignOp = (Tree.AssignOp) expression.getChildren().get(0);
    			action = getTerm(doc, assignOp.getLeftTerm()) + " := ";
    			operation = assignOp.getRightTerm();
    		} else if (statement instanceof CustomTree.AttributeDeclaration) {
    			CustomTree.AttributeDeclaration attrDecl = (CustomTree.AttributeDeclaration) statement;
    			String identifier = getTerm(doc, attrDecl.getIdentifier());
				String annotations = "";
				if (!attrDecl.getAnnotationList().getAnnotations().isEmpty()) {
					annotations = getTerm(doc, attrDecl.getAnnotationList()) + " ";
				}
				String type;
				if (attrDecl.getType() instanceof ValueModifier) {
					ValueModifier valueModifier = (ValueModifier) attrDecl.getType();
					ProducedType typeModel = valueModifier.getTypeModel();
					type = typeModel.getProducedTypeName();
					
				} else {
					type = getTerm(doc, attrDecl.getType());
				}
				
				declaration = annotations + type + " " + identifier + ";";
    			SpecifierOrInitializerExpression sie = attrDecl.getSpecifierOrInitializerExpression();
    			if (sie==null) return;
				action = identifier + " " + getToken(sie) + " ";
    			operation = sie.getExpression().getTerm();
    		} else {
    			return;
    		}
    		
    		String test;
    		String elseTerm;
    		String thenTerm;
    		
    		while (operation instanceof Expression) {
    			//If Operation is enclosed in parenthesis we need to get down through them: return (test then x else y);
    			operation = ((Expression) operation).getTerm();
     		}
			
    		if (operation instanceof Tree.DefaultOp) {
    			Tree.DefaultOp defaultOp = (Tree.DefaultOp) operation;
    			if (defaultOp.getLeftTerm() instanceof Tree.ThenOp) {
    				Tree.ThenOp thenOp = (ThenOp) defaultOp.getLeftTerm();
    				thenTerm = getTerm(doc, thenOp.getRightTerm());
    				test = getTerm(doc, thenOp.getLeftTerm());
    			} else {
    				Term leftTerm = defaultOp.getLeftTerm();
					String leftTermStr = getTerm(doc, leftTerm);
					if (leftTerm instanceof BaseMemberExpression) {
    					thenTerm = leftTermStr;
    					test = "exists " + leftTermStr;			
    				} else  {
    					if (leftTerm instanceof InvocationExpression) {
    						InvocationExpression expr = (InvocationExpression) leftTerm;
    						if (expr.getPrimary() instanceof QualifiedMemberExpression) {
    							leftTerm = expr.getPrimary();
    						}
    					}
    					if (leftTerm instanceof QualifiedMemberExpression) {
    						QualifiedMemberExpression qMemberExp = (QualifiedMemberExpression) leftTerm;
    						String id = getTerm(doc, qMemberExp.getIdentifier());
    						test = "exists " + id + " = " + leftTermStr;
    						thenTerm = id;
    					} else {
    						test = "exists tmp_var = " + leftTermStr;
    						thenTerm = "tmp_var";
    					}
    				}	
    			}
    			elseTerm = getTerm(doc, defaultOp.getRightTerm());
    		} else if (operation instanceof Tree.ThenOp) {
    			Tree.ThenOp thenOp = (ThenOp) operation;
    			thenTerm = getTerm(doc, thenOp.getRightTerm());
   			 	test = getTerm(doc, thenOp.getLeftTerm());
   			 	elseTerm = "null";
    		} else {
    			return;
    		}

			String baseIndent = CeylonQuickFixAssistant.getIndent(statement, doc);
			String indent = CeylonAutoEditStrategy.getDefaultIndent();
			
			test = removeEnclosingParentesis(test);
			
			StringBuilder replace = new StringBuilder();
			if (declaration != null) {
				replace.append(declaration).append("\n").append(baseIndent);
			}
			replace.append("if (").append(test).append(") {\n")
					.append(baseIndent).append(indent).append(action).append(thenTerm).append(";\n")
					.append(baseIndent).append("}\n")
					.append(baseIndent).append("else {\n")
					.append(baseIndent).append(indent).append(action).append(elseTerm).append(";\n")
					.append(baseIndent).append("}");

			TextChange change = new DocumentChange("Convert To if-else", doc);
			change.setEdit(new ReplaceEdit(statement.getStartIndex(), statement.getStopIndex() - statement.getStartIndex() + 1, replace.toString()));
			proposals.add(new ConvertThenElseToIfElse(statement.getStartIndex(), file, change));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    }
    
	private static String getToken(
			SpecifierOrInitializerExpression token) {
		if (token instanceof SpecifierExpression) {
			return "=";
		} else {
			return ":=";
		}
	}

	private static String removeEnclosingParentesis(String s) {
    	if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
    		return s.substring(1, s.length() - 1);
    	}
    	return s;
    }
    
    private static String getTerm(IDocument doc, Node node) throws BadLocationException {
    	return doc.get(node.getStartIndex(), node.getStopIndex() - node.getStartIndex() + 1);
    }
}