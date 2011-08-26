package ceylon.imp.foldingUpdater;

import java.util.HashMap;
import java.util.List;

import org.eclipse.imp.services.base.FolderBase;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

/**
 * This file provides a skeletal implementation of the language-dependent aspects
 * of a source-text folder.  This implementation is generated from a template that
 * is parameterized with respect to the name of the language, the package containing
 * the language-specific types for AST nodes and AbstractVisitors, and the name of
 * the folder package and class.
 */
public class CeylonFoldingUpdater extends FolderBase {

	// When instantiated will provide a concrete implementation of an abstract method
	// defined in FolderBase
	@Override
	public void sendVisitorToAST(HashMap<Annotation,Position> newAnnotations, List<Annotation> annotations,
			Object ast) {
		Tree.CompilationUnit cu = (Tree.CompilationUnit) ast;
		new Visitor() {
			@Override 
			public void visit(Tree.Body body) {
				makeAnnotation(body);
			}
		}.visit(cu);
	}
}
