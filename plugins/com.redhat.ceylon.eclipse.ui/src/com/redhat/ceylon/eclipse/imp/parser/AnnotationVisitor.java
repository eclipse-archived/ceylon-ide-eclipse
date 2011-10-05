package com.redhat.ceylon.eclipse.imp.parser;

import java.util.List;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class AnnotationVisitor extends Visitor 
        implements NaturalVisitor {
    
    static class Span {
        int start;
        int end;
        Span(Tree.AnnotationList that) {
            start=that.getStartIndex();
            end=that.getStopIndex();
        }
        @Override public String toString() {
            return start + "-" + end;
        }
    }
    
	private final List<Span> spans;
	
	AnnotationVisitor(List<Span> spans) {
		this.spans = spans;
	}

	@Override
    public void visit(Tree.AnnotationList that) {
	    if (that.getStartIndex()!=null) {
    		spans.add(new Span(that));
	    }
    }
    
	/*@Override
    public void visit(Tree.CompilerAnnotation that) {
		inAnnotation = true;
    	super.visit(that);
		inAnnotation = false;
    }*/
    
}
