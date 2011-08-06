package ceylon.imp.parser;

import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class AnnotationVisitor extends Visitor {
    
	private final Set<Integer> annotatations;
	
	AnnotationVisitor(Set<Integer> annotatations) {
		this.annotatations = annotatations;

	}

	@Override
    public void visit(Tree.Annotation that) {
    	super.visit(that);
    	Identifier identifier = ((Tree.BaseMemberExpression) that.getPrimary() ).getIdentifier();
		annotatations.add(identifier.getAntlrTreeNode().getToken().getTokenIndex());
    }
    

}
