package ceylon.imp.parser;

import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class AnnotationVisitor extends Visitor {
    
	private final Set<Integer> annotatations;
	private boolean inAnnotation = false;
	
	AnnotationVisitor(Set<Integer> annotatations) {
		this.annotatations = annotatations;
	}

	@Override
    public void visit(Tree.Annotation that) {
		inAnnotation = true;
    	super.visit(that);
		inAnnotation = false;
    }
    
	@Override
    public void visit(Tree.Identifier that) {
		if (inAnnotation) {
			annotatations.add(that.getAntlrTreeNode().getToken().getTokenIndex());
		}
	}
	
}
