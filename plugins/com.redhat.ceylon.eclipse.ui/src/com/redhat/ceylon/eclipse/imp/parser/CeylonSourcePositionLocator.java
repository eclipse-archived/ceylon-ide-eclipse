package com.redhat.ceylon.eclipse.imp.parser;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

/**
 * NOTE:  This version of the ISourcePositionLocator is for use when the Source
 * Position Locator and corresponding Parse Controller are generated separately from
 * a corresponding set of LPG grammar templates and possibly in the absence
 * of the lexer, parser, and AST-related types that would be generated from
 * those templates.  To enable compilation of the Locator and Controller,
 * dummy types have been defined as member types of the Controller in place
 * of possibly missing lexer, parser, and AST-related types.  This version
 * of the Node Locator refers to some of those types.  When those types
 * are replaced by real implementation types, the Locator must be modified
 * to refer to those.  Apart from statements to import needed types from
 * the Parse Controller, this SourcePositionLocator is the same as that used
 * with LPG.
 * @see the corresponding ParseController type
 * 
 * @author Stan Sutton (suttons@us.ibm.com)
 * @since May 15, 2007
 */
public class CeylonSourcePositionLocator implements ISourcePositionLocator {

  private CeylonParseController parseController;

  public CeylonSourcePositionLocator(IParseController parseController) {
    this.parseController= (CeylonParseController) parseController;
  }

  private final class NodeVisitor extends Visitor
      implements NaturalVisitor {
    
    private NodeVisitor(int fStartOffset, int fEndOffset) {
      this.fStartOffset = fStartOffset;
      this.fEndOffset = fEndOffset;
    }

  private Node node;
  private int fStartOffset;
  private int fEndOffset;
  
  public Node getNode() {
    return node;
  }
  
  @Override
  public void visitAny(Node that) {
	  super.visitAny(that);
	  if (node==null && inBounds(that)) {
		  node=that;
	  }
  }
  
  @Override
  public void visit(Tree.BinaryOperatorExpression that) {
	  super.visit(that);
	  if (node==null && inBounds(that.getLeftTerm(), that.getRightTerm())) {
		  node=that;
	  }
  }
  
  @Override
  public void visit(Tree.UnaryOperatorExpression that) {
	  super.visit(that);
	  if (node==null && (inBounds(that, that.getTerm())
			           ||inBounds(that.getTerm(),that))) {
		  node=that;
	  }
  }
  
  @Override
  public void visit(QualifiedMemberOrTypeExpression that) {
	if (inBounds(that.getMemberOperator(), that.getIdentifier())) {
		node=that;
	}
	else {
		super.visit(that);
	}
  }
  
  @Override
  public void visit(Tree.StaticMemberOrTypeExpression that) {
    if (inBounds(that.getIdentifier())) {
        node = that;
    }
    else {
        super.visit(that);
    }
  }
  
  @Override
  public void visit(Tree.SimpleType that) {
    if (inBounds(that.getIdentifier())) {
        node = that;
    }
    else {
        super.visit(that);
    }
  }
  
  @Override
  public void visit(Tree.ImportMemberOrType that) {
    if (inBounds(that.getIdentifier())) {
        node = that;
    }
    else {
        super.visit(that);
    }
  }
  
  @Override
  public void visit(Tree.Declaration that) {
    if (inBounds(that.getIdentifier())) {
        node = that;
    }
    else {
        super.visit(that);
    }
  }
  
  @Override
  public void visit(Tree.NamedArgument that) {
    if (inBounds(that.getIdentifier())) {
        node = that;
    }
    else {
        super.visit(that);
    }
  }
  
    private boolean inBounds(Node that) {
      return inBounds(that, that);
    }
    
    private boolean inBounds(Node left, Node right) {
      if (left==null) return false;
      if (right==null) left=right;
      Integer tokenStartIndex = left.getStartIndex();
      Integer tokenStopIndex = right.getStopIndex();
      return tokenStartIndex!=null && tokenStopIndex!=null &&
    		  tokenStartIndex <= fStartOffset && 
    		  tokenStopIndex+1 >= fEndOffset;
    }
    
  }

  public Node findNode(Object ast, int offset) {
    return findNode(ast, offset, offset+1);
  }

  public Node findNode(Object ast, int startOffset, int endOffset) {
    NodeVisitor visitor = new NodeVisitor(startOffset, endOffset);
    System.out.println("Looking for node spanning offsets " + startOffset + " => " + endOffset);    
    Tree.CompilationUnit cu = (Tree.CompilationUnit) ast;
    cu.visit(visitor);
    System.out.println("selected node: " + visitor.getNode());
    return visitor.getNode();
  }

  
  public int getStartOffset(Object node) {
	if (node instanceof CommonToken) {
		return ((CommonToken) node).getStartIndex();
	}
    Node in = toNode(node);
    if (in==null) {
    	return 0;
    }
    else {
    	Integer index = in.getStartIndex();
    	return index==null?0:index;
    }
  }

  public int getEndOffset(Object node) {
		if (node instanceof CommonToken) {
			return ((CommonToken) node).getStopIndex();
		}
		Node in = toNode(node);
	    if (in==null) {
	    	return 0;
	    }
	    else {
	    	Integer index = in.getStopIndex();
	    	return index==null?0:index;
	    }
	  }

  private Node toNode(Object node) {
	if (node instanceof ModelTreeNode) {
	    ModelTreeNode treeNode = (ModelTreeNode) node;
	    return (Node) treeNode.getASTNode();
	}
	else if (node instanceof Node) {
		return getIdentifyingNode((Node) node);
	}
	else {
	  return null;
	}
  }

  public int getLength(Object node) {
    return getEndOffset(node) - getStartOffset(node);
  }

  public static Node getIdentifyingNode(Node node) {
    if (node instanceof Tree.Declaration) {
      return ((Tree.Declaration) node).getIdentifier();
    }
    else if (node instanceof Tree.NamedArgument) {
      return ((Tree.NamedArgument) node).getIdentifier();
    }
    else if (node instanceof Tree.StaticMemberOrTypeExpression) {
      return ((Tree.StaticMemberOrTypeExpression) node).getIdentifier();
    }
    else if (node instanceof Tree.SimpleType) {
      return ((Tree.SimpleType) node).getIdentifier();
    }
    else if (node instanceof Tree.ImportMemberOrType) {
      return ((Tree.ImportMemberOrType) node).getIdentifier();
    }
    else {    
      return node;
    }
  }  

  public IPath getPath(Object entity) {
    if (entity instanceof Node) {
        Node node= (Node) entity;
        Unit unit = node.getUnit();
        String fileName = unit.getFilename();
        String packagePath = unit.getPackage().getQualifiedNameString().replace('.', '/');
        PhasedUnit phasedUnit = parseController.getPhasedUnits().getPhasedUnitFromRelativePath(packagePath + "/" + fileName);
        if (phasedUnit != null) {
            IFileVirtualFile file = (IFileVirtualFile) phasedUnit.getUnitFile();
            IFile fileResource = (IFile) file.getResource();
            return fileResource.getFullPath();
        }
    }
    if (entity instanceof ICompilationUnit) {
      ICompilationUnit cu= (ICompilationUnit) entity;
      return cu.getPath();
    }
    return new Path("");
  }
}
