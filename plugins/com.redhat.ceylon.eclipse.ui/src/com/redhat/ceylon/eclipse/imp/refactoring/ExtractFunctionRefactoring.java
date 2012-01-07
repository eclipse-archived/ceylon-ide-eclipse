package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.imp.quickfix.CeylonQuickFixAssistant.getIndent;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.ArrayList;
import java.util.List;

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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;

public class ExtractFunctionRefactoring extends AbstractRefactoring {
    
	private final class CheckStatementsVisitor extends Visitor {
	    String problem = null;
        @Override
        public void visit(Tree.Declaration that) {
            super.visit(that);
            //TODO: search to see if it is referenced outside
            //      the selected list of statements
            problem = "a declaration";
        }
        @Override
        public void visit(Tree.Directive that) {
            super.visit(that);
            problem = "a directive statement";
        }
        @Override
        public void visit(Tree.SpecifierStatement that) {
            super.visit(that);
            //TODO: search to see if the specified value is 
            //      defined of referenced outside the selected 
            //      list of statements
            problem = "a specification statement";
        }
        @Override
        public void visit(Tree.AssignmentOp that) {
            super.visit(that);
            //TODO: search to see if the specified value is 
            //      defined of referenced outside the selected 
            //      list of statements
            problem = "an assignment";
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
    private ITextSelection selection;

	public ExtractFunctionRefactoring(ITextEditor editor) {
	    super(editor);
		newName = guessName();
        selection = (ITextSelection) editor.getSelectionProvider().getSelection();
	}

    @Override
    boolean isEnabled() {
        return node instanceof Tree.Term || 
                node instanceof Tree.Body;
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
            for (Statement s: getStatements((Tree.Body) node)) {
                CheckStatementsVisitor v = new CheckStatementsVisitor();
                s.visit(v);
                if (v.problem!=null) {
                    return createWarningStatus("Selected statements contain "
                            + v.problem + " at  " + s.getLocation());
                }
            }
        }
		return new RefactoringStatus();
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
        Declaration existing = node.getScope()
                .getMemberOrParameter(node.getUnit(), newName);
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
		String exp = toString(term);
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
			addLocalType(dec, bme.getTypeModel(), localTypes, 
					new ArrayList<ProducedType>());
		}
		
		String params = "";
		String args = "";
		if (!flrv.getLocalReferences().isEmpty()) {
			for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
				params += bme.getTypeModel().getProducedTypeName() + 
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
		
		boolean isVoid = "Void".equals(term.getTypeModel().getProducedTypeName());
		
		tfc.addEdit(new InsertEdit(decNode.getStartIndex(),
				(explicitType || dec.isToplevel() ? 
				        term.getTypeModel().getProducedTypeName() : 
				        (isVoid ? "void":"function")) + 
				" " + newName + typeParams + "(" + params + ")" + constraints + 
				" {" + extraIndent + (isVoid?"":"return ") + exp + ";" + indent + "}" 
				+ indent + indent));
		tfc.addEdit(new ReplaceEdit(start, length, newName + "(" + args + ")"));
    }

    private void extractStatementsInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = tfc.getCurrentDocument(null);
        
        Tree.Body body = (Tree.Body) node;
        List<Statement> statements = getStatements(body);
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
        body.visit(flrv);
        List<TypeDeclaration> localTypes = new ArrayList<TypeDeclaration>();
        for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
            addLocalType(dec, bme.getTypeModel(), localTypes, 
                    new ArrayList<ProducedType>());
        }
        
        String params = "";
        String args = "";
        if (!flrv.getLocalReferences().isEmpty()) {
            for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
                params += bme.getTypeModel().getProducedTypeName() + 
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
        
        //boolean isVoid = "Void".equals(term.getTypeModel().getProducedTypeName());
        
        String content = "void" + " " + newName + typeParams + "(" + params + ")" + 
                constraints + " {";
        for (Statement s: statements) {
            content+=extraIndent + toString(s);
        }
        content += indent + "}" + indent + indent;
        
        tfc.addEdit(new InsertEdit(decNode.getStartIndex(), content));
        tfc.addEdit(new ReplaceEdit(start, length, newName + "(" + args + ");"));
    }

    private List<Statement> getStatements(Tree.Body body) {
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
