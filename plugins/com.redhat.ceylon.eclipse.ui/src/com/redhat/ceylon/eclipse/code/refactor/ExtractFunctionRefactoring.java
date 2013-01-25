package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;

public class ExtractFunctionRefactoring extends AbstractRefactoring {
    
    private final class FindOuterReferencesVisitor extends Visitor {
        final Declaration declaration;
        int refs = 0;
        FindOuterReferencesVisitor(Declaration declaration) {
            this.declaration = declaration;
        }
        @Override
        public void visit(Tree.MemberOrTypeExpression that) {
            super.visit(that);
            if (that.getDeclaration().equals(declaration)) {
                refs++;
            }
        }
        @Override
        public void visit(Tree.Declaration that) {
            super.visit(that);
            if (that.getDeclarationModel().equals(declaration)) {
                refs++;
            }
        }
        @Override
        public void visit(Tree.Type that) {
            super.visit(that);
            if (that.getTypeModel().getDeclaration().equals(declaration)) {
                refs++;
            }
        }
    }
    
    private final class CheckExpressionVisitor extends Visitor {
        String problem = null;
        @Override
        public void visit(Tree.Body that) {}
        @Override
        public void visit(Tree.AssignmentOp that) {
            super.visit(that);
            problem = "an assignment";
        }
    }

	private final class CheckStatementsVisitor extends Visitor {
	    final Tree.Body scope;
	    final Collection<Statement> statements;
	    CheckStatementsVisitor(Tree.Body scope, 
	            Collection<Statement> statements) {
	        this.scope = scope;
	        this.statements = statements;
	    }
	    String problem = null;
        @Override
        public void visit(Tree.Body that) {
            if (that.equals(scope)) {
                super.visit(that);
            }
        }
        @Override
        public void visit(Tree.Declaration that) {
            super.visit(that);
            if (result==null || !that.equals(result)) {
                Declaration d = that.getDeclarationModel();
                if (d.isShared()) {
                    problem = "a shared declaration";
                }
                else {
                    if (hasOuterRefs(d, scope, statements)) {
                        problem = "a declaration used elsewhere";
                    }
                }
            }
        }
        @Override
        public void visit(Tree.SpecifierStatement that) {
            super.visit(that);
            if (that.getBaseMemberExpression() instanceof Tree.MemberOrTypeExpression) {
                Declaration d = ((Tree.MemberOrTypeExpression) that.getBaseMemberExpression()).getDeclaration();
                if (notResultRef(d) && hasOuterRefs(d, scope, statements)) {
                    problem = "a specification statement for a declaration used or defined elsewhere";
                }
            }
        }
        @Override
        public void visit(Tree.AssignmentOp that) {
            super.visit(that);
            if (that.getLeftTerm() instanceof Tree.MemberOrTypeExpression) {
                Declaration d = ((Tree.MemberOrTypeExpression) that.getLeftTerm()).getDeclaration();
                if (notResultRef(d) && hasOuterRefs(d, scope, statements)) {
                    problem = "an assignment to a declaration used or defined elsewhere";
                }
            }
        }
        private boolean notResultRef(Declaration d) {
            return result==null || !result.getDeclarationModel().equals(d);
        }
        @Override
        public void visit(Tree.Directive that) {
            super.visit(that);
            problem = "a directive statement";
        }
    }

    private boolean hasOuterRefs(Declaration d, Tree.Body scope, 
            Collection<Statement> statements) {
        FindOuterReferencesVisitor v = 
                new FindOuterReferencesVisitor(d);
        for (Statement s: scope.getStatements()) {
            if (!statements.contains(s)) {
                s.visit(v);
            }
        }
        return v.refs>0;
    }
    
    private final class FindResultVisitor extends Visitor {
        Tree.AttributeDeclaration result = null;
        final Tree.Body scope;
        final Collection<Statement> statements;
        FindResultVisitor(Tree.Body scope, 
                Collection<Statement> statements) {
            this.scope = scope;
            this.statements = statements;
        }
        @Override
        public void visit(Tree.Body that) {}
        @Override
        public void visit(Tree.AttributeDeclaration that) {
            super.visit(that);
            if (hasOuterRefs(that.getDeclarationModel(), 
                    scope, statements)) {
                result = that;
            }
        }
    }

    public static final class FindLocalReferencesVisitor extends Visitor {
		List<Tree.BaseMemberExpression> localReferences = 
				new ArrayList<Tree.BaseMemberExpression>();
		Declaration declaration;
		FindLocalReferencesVisitor(Declaration declaration) {
			this.declaration = declaration;
		}
		public List<Tree.BaseMemberExpression> getLocalReferences() {
			return localReferences;
		}
		@Override
		public void visit(Tree.BaseMemberExpression that) {
			super.visit(that);
			//TODO: things nested inside control structures
			Scope scope = that.getDeclaration().getContainer();
			while (scope!=null) {
				if (scope==declaration) {
					for (Tree.BaseMemberExpression bme: localReferences) {
						if (bme.getDeclaration().equals(that.getDeclaration())) {
							return;
						}
					}
					localReferences.add(that);
				}
				scope = scope.getContainer();
			}
		}
	}

	private String newName;
	private boolean explicitType;
    private Tree.AttributeDeclaration result;
    private List<Statement> statements;

	public ExtractFunctionRefactoring(ITextEditor editor) {
	    super(editor);
	    if (editor instanceof CeylonEditor && 
	            editor.getSelectionProvider()!=null) {
	        init((ITextSelection) editor.getSelectionProvider()
	                .getSelection());
	    }
        if (result!=null) {
            newName = result.getDeclarationModel().getName();
        }
        else {
            newName = guessName(node);
        }
	}

    private void init(ITextSelection selection) {
        if (node instanceof Tree.Body) {
            Tree.Body body = (Tree.Body) node;
            statements = getStatements(body, selection);
	        for (Statement s: statements) {
	            FindResultVisitor v = new FindResultVisitor(body, statements);
	            s.visit(v);
	            if (v.result!=null) {
	                result = v.result;
	            }
	        }
	    }
    }

    @Override
    boolean isEnabled() {
        return node instanceof Tree.Term || 
                node instanceof Tree.Body && 
                    !statements.isEmpty();
    }

    /*public ExtractFunctionRefactoring(IQuickFixInvocationContext context) {
        super(context);
        newName = guessName();
    }*/

	public String getName() {
		return "Extract Function";
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
        if (node instanceof Tree.Body) {
            Tree.Body body = (Tree.Body) node;
            for (Statement s: statements) {
                CheckStatementsVisitor v = new CheckStatementsVisitor(body, statements);
                s.visit(v);
                if (v.problem!=null) {
                    return createWarningStatus("Selected statements contain "
                            + v.problem + " at  " + s.getLocation());
                }
            }
        }
        else if (node instanceof Tree.Term) {
            CheckExpressionVisitor v = new CheckExpressionVisitor();
            node.visit(v);
            if (v.problem!=null) {
                return createWarningStatus("Selected expression contains "
                        + v.problem);
            }
        }
		return new RefactoringStatus();
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
        Declaration existing = node.getScope()
                .getMemberOrParameter(node.getUnit(), newName, null, false);
        if (null!=existing) {
            return createWarningStatus("An existing declaration named '" +
                    newName + "' already exists in the same scope");
        }
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		TextChange tfc = newLocalChange();
		if (node instanceof Tree.Term) {
		    extractExpressionInFile(tfc);
		}
		else if (node instanceof Tree.Body) {
		    extractStatementsInFile(tfc);
		}
		return tfc;
	}

    private void extractExpressionInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
		IDocument doc = tfc.getCurrentDocument(null);
		
		Tree.Term term = (Tree.Term) node;
        Integer start = term.getStartIndex();
        int length = term.getStopIndex()-start+1;
		String exp = toString(unparenthesize(term));
		FindContainerVisitor fsv = new FindContainerVisitor(term);
		rootNode.visit(fsv);
		Tree.Declaration decNode = fsv.getDeclaration();
		/*if (decNode instanceof Tree.Declaration) {
			Tree.AnnotationList anns = ((Tree.Declaration) decNode).getAnnotationList();
			if (anns!=null && !anns.getAnnotations().isEmpty()) {
				decNode = anns.getAnnotations().get(0);
			}
		}*/
		Declaration dec = decNode.getDeclarationModel();
		FindLocalReferencesVisitor flrv = new FindLocalReferencesVisitor(dec);
		term.visit(flrv);
		List<TypeDeclaration> localTypes = new ArrayList<TypeDeclaration>();
		for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
			addLocalType(dec, node.getUnit().denotableType(bme.getTypeModel()), 
			        localTypes, new ArrayList<ProducedType>());
		}
		
		String params = "";
		String args = "";
		if (!flrv.getLocalReferences().isEmpty()) {
			for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
				params += node.getUnit().denotableType(bme.getTypeModel()).getProducedTypeName() + 
						" " + bme.getIdentifier().getText() + ", ";
				args += bme.getIdentifier().getText() + ", ";
			}
			params = params.substring(0, params.length()-2);
			args = args.substring(0, args.length()-2);
		}
		
		String indent = "\n" + getIndent(decNode, doc);
        String extraIndent = indent + getDefaultIndent();

		String typeParams = "";
		String constraints = "";
		if (!localTypes.isEmpty()) {
			for (TypeDeclaration t: localTypes) {
				typeParams += t.getName() + ", ";
				if (!t.getSatisfiedTypes().isEmpty()) {
                    constraints += extraIndent + getDefaultIndent() + 
                            "given " + t.getName() + " satisfies ";
					for (ProducedType pt: t.getSatisfiedTypes()) {
						constraints += pt.getProducedTypeName() + "&";
					}
					constraints = constraints.substring(0, constraints.length()-1);
				}
			}
			typeParams = "<" + typeParams.substring(0, typeParams.length()-2) + ">";
		}
		
		String type;
		String ending;
		ProducedType tt = term.getTypeModel();
		if (tt!=null) {
            boolean isVoid = (tt.getDeclaration() instanceof Class) && 
            		tt.getDeclaration().equals(term.getUnit().getAnythingDeclaration());
            if (isVoid) {
    	         type = "void";
    	         ending = "";
    		}
    		else {
                ProducedType returnType = node.getUnit().denotableType(tt);
                ending = "return ";
                if (explicitType || dec.isToplevel()) {
                	type = returnType.getProducedTypeName();
                	HashSet<Declaration> decs = new HashSet<Declaration>();
					importType(decs, returnType, rootNode);
					applyImports(tfc, decs, rootNode);
                }
                else {
                	type = "function";
                }
    		}    		
            
            tfc.addEdit(new InsertEdit(decNode.getStartIndex(),
    		        type + " " + newName + typeParams + "(" + params + ")" + 
                    constraints + 
    				" {" + extraIndent + ending + exp + ";" + indent + "}" 
    				+ indent + indent));
    		tfc.addEdit(new ReplaceEdit(start, length, newName + "(" + args + ")"));
		}
    }

    private void extractStatementsInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = tfc.getCurrentDocument(null);
        
        Tree.Body body = (Tree.Body) node;
        
        Integer start = statements.get(0).getStartIndex();
        int length = statements.get(statements.size()-1)
                .getStopIndex()-start+1;
        FindContainerVisitor fsv = new FindContainerVisitor(body);
        rootNode.visit(fsv);
        Tree.Declaration decNode = fsv.getDeclaration();
        /*if (decNode instanceof Tree.Declaration) {
            Tree.AnnotationList anns = ((Tree.Declaration) decNode).getAnnotationList();
            if (anns!=null && !anns.getAnnotations().isEmpty()) {
                decNode = anns.getAnnotations().get(0);
            }
        }*/
        Declaration dec = decNode.getDeclarationModel();
        FindLocalReferencesVisitor flrv = new FindLocalReferencesVisitor(dec);
        for (Statement s: statements) {
            s.visit(flrv);
        }
        List<TypeDeclaration> localTypes = new ArrayList<TypeDeclaration>();
        List<Tree.BaseMemberExpression> localRefs = new ArrayList<Tree.BaseMemberExpression>();
        for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
            if (result==null || !bme.getDeclaration().equals(result.getDeclarationModel())) {
                FindOuterReferencesVisitor v = new FindOuterReferencesVisitor(bme.getDeclaration());
                for (Statement s: body.getStatements()) {
                    if (!statements.contains(s)) {
                        s.visit(v);
                    }
                }
                if (v.refs>0) {
                    addLocalType(dec, node.getUnit().denotableType(bme.getTypeModel()), 
                            localTypes, new ArrayList<ProducedType>());
                    localRefs.add(bme);
                }
            }
        }
        
        String params = "";
        String args = "";
        HashSet<Declaration> decs = new HashSet<Declaration>();
		Set<Declaration> done = decs;
        boolean nonempty = false;
        for (Tree.BaseMemberExpression bme: localRefs) {
            if (done.add(bme.getDeclaration())) {
                params += node.getUnit().denotableType(bme.getTypeModel()).getProducedTypeName() + 
                        " " + bme.getIdentifier().getText() + ", ";
                args += bme.getIdentifier().getText() + ", ";
                nonempty = true;
            }
        }
        if (nonempty) {
            params = params.substring(0, params.length()-2);
            args = args.substring(0, args.length()-2);
        }
        
        String indent = "\n" + getIndent(decNode, doc);
        String extraIndent = indent + getDefaultIndent();

        String typeParams = "";
        String constraints = "";
        if (!localTypes.isEmpty()) {
            for (TypeDeclaration t: localTypes) {
                typeParams += t.getName() + ", ";
                if (!t.getSatisfiedTypes().isEmpty()) {
                    constraints += extraIndent + getDefaultIndent() + 
                            "given " + t.getName() + " satisfies ";
                    for (ProducedType pt: t.getSatisfiedTypes()) {
                        constraints += pt.getProducedTypeName() + "&";
                    }
                    constraints = constraints.substring(0, constraints.length()-1);
                }
            }
            typeParams = "<" + typeParams.substring(0, typeParams.length()-2) + ">";
        }
        
        ProducedType returnType = result==null ? null : node.getUnit()
        		.denotableType(result.getDeclarationModel().getType());
        String content;
        if (result!=null) {
        	if (explicitType||dec.isToplevel()) {
        		content = returnType.getProducedTypeName();
        		importType(decs, returnType, rootNode);
        		applyImports(tfc, decs, rootNode);
        	}
        	else {
        		content = "function";
        	}
        }
        else {
        	content = "void";
        }
        content += " " + newName + typeParams + "(" + params + ")" + 
                constraints + " {";
        for (Statement s: statements) {
            content += extraIndent + toString(s);
        }
        if (result!=null) {
            content += extraIndent + "return " + result.getDeclarationModel().getName() + ";";
        }
        content += indent + "}" + indent + indent;
        
        String invocation = newName + "(" + args + ");";
        if (result!=null) {
            String modifs;
            if (result.getDeclarationModel().isShared()) {
                modifs = "shared " + returnType.getProducedTypeName() + " ";
            }
            else {
                modifs = "value ";
            }
            invocation = modifs + result.getDeclarationModel().getName() + 
                    "=" + invocation;
        }
        
        tfc.addEdit(new InsertEdit(decNode.getStartIndex(), content));        
        tfc.addEdit(new ReplaceEdit(start, length, invocation));
    }

    private List<Statement> getStatements(Tree.Body body, ITextSelection selection) {
        List<Statement> statements = new ArrayList<Statement>();
        for (Tree.Statement s: body.getStatements()) {
            if (s.getStartIndex()>=selection.getOffset() &&
                    s.getStopIndex()<=selection.getOffset()+selection.getLength()) {
                statements.add(s);
            }
        }
        return statements;
    }

	private void addLocalType(Declaration dec, ProducedType type,
			List<TypeDeclaration> localTypes, List<ProducedType> visited) {
		if (visited.contains(type)) {
			return;
		}
		else {
			visited.add(type);
		}
		TypeDeclaration td = type.getDeclaration();
		if (td.getContainer()==dec) {
			boolean found=false;
			for (TypeDeclaration typeDeclaration: localTypes) {
				if (typeDeclaration==td) {
					found=true; 
					break;
				}
			}
			if (!found) {
				localTypes.add(td);
			}
		}
		for (ProducedType pt: td.getSatisfiedTypes()) {
			addLocalType(dec, pt, localTypes, visited);
		}
		for (ProducedType pt: type.getTypeArgumentList()) {
			addLocalType(dec, pt, localTypes, visited);
		}
	}


	public void setNewName(String text) {
		newName = text;
	}
	
	public String getNewName() {
		return newName;
	}
	
	public void setExplicitType() {
		this.explicitType = !explicitType;
	}
	
}
