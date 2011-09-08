package com.redhat.ceylon.eclipse.imp.refactoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class ExtractFunctionRefactoring extends Refactoring {
    
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

	private final IFile fSourceFile;
	private final Node fNode;
	private final ITextEditor fEditor;
	private final CeylonParseController parseController;
	private String newName;
	private boolean explicitType;

	public ExtractFunctionRefactoring(ITextEditor editor) {

		fEditor = editor;

		IASTFindReplaceTarget frt = (IASTFindReplaceTarget) fEditor;
		IEditorInput input = editor.getEditorInput();
		parseController = (CeylonParseController) frt.getParseController();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			fSourceFile = fileInput.getFile();
			fNode = findNode(frt);
			Node node = fNode;
			if (node instanceof Tree.Expression) {
				node = ((Tree.Expression) node).getTerm();
			}
			if (node instanceof Tree.InvocationExpression) {
				node = ((Tree.InvocationExpression) node).getPrimary();
			}
			if (node instanceof Tree.StaticMemberOrTypeExpression) {
				newName = ((Tree.StaticMemberOrTypeExpression) node).getIdentifier().getText();
				newName = Character.toLowerCase(newName.charAt(0)) + 
						newName.substring(1);
			}
			else {
				newName = "temp";
			}
		} 
		else {
			fSourceFile = null;
			fNode = null;
		}
	}

	private Node findNode(IASTFindReplaceTarget frt) {
		return parseController.getSourcePositionLocator()
				.findNode(parseController.getRootNode(), frt.getSelection().x, 
						frt.getSelection().x+frt.getSelection().y);
	}

	public String getName() {
		return "Extract function";
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
		TextFileChange tfc = new TextFileChange("Extract function", fSourceFile);
		tfc.setEdit(new MultiTextEdit());
		Tree.Term term = (Tree.Term) fNode;
		Integer start = fNode.getStartIndex();
		int length = fNode.getStopIndex()-start+1;
		Region region = new Region(start, length);
		String exp = "";
		for (Iterator<Token> ti = parseController.getTokenIterator(region); 
				ti.hasNext();) {
			exp+=ti.next().getText();
		}
		FindContainerVisitor fsv = new FindContainerVisitor(term);
		parseController.getRootNode().visit(fsv);
		Node node = fsv.getDeclaration();
		if (node instanceof Tree.Declaration) {
			Tree.AnnotationList anns = ((Tree.Declaration) node).getAnnotationList();
			if (!anns.getAnnotations().isEmpty()) {
				node = anns.getAnnotations().get(0);
			}
		}
		Declaration dec = fsv.getDeclaration().getDeclarationModel();
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
		
		String indent = getIndent(node);

		String typeParams = "";
		String constraints = "";
		if (!localTypes.isEmpty()) {
			for (TypeDeclaration t: localTypes) {
				typeParams += t.getName() + ", ";
				if (!t.getSatisfiedTypes().isEmpty()) {
					constraints += (indent.isEmpty() ? "\n" : indent) + 
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
		
		tfc.addEdit(new InsertEdit(node.getStartIndex(),
				( explicitType ? term.getTypeModel().getProducedTypeName() : (isVoid?"void":"function")) + 
				" " + newName + typeParams + "(" + params + ")" + constraints + 
				" { " + (isVoid?"":"return ") + exp + "; }" + indent));
		tfc.addEdit(new ReplaceEdit(start, length, newName + "(" + args + ")"));
		return tfc;
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

	private String getIndent(Node node) {
		int prevIndex = node.getToken().getTokenIndex()-1;
		if (prevIndex>=0) {
			Token prevToken = parseController.getTokenStream().get(prevIndex);
			if (prevToken.getChannel()==Token.HIDDEN_CHANNEL) {
				return prevToken.getText();
			}
		}
		return "";
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
