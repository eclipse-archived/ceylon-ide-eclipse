package com.redhat.ceylon.eclipse.imp.parser;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
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

  private final Node[] fNode = new Node[1];

  private int fStartOffset;
  private int fEndOffset;
  private CeylonParseController parseController;

  public CeylonSourcePositionLocator(IParseController parseController) {
    this.parseController= (CeylonParseController) parseController;
  }

  private final class NodeVisitor extends Visitor {

    public void visitAny(Node element) {
      CommonTree antlrTreeNode = element.getAntlrTreeNode();

      int tokenStartIndex = antlrTreeNode.getTokenStartIndex();
      CommonToken tokenStart = (CommonToken) getTokenStream().get(tokenStartIndex);
      int nodeStartOffset = tokenStart.getStartIndex();
      
      int tokenStopIndex = antlrTreeNode.getTokenStopIndex();
      CommonToken tokenStop = (CommonToken) getTokenStream().get(tokenStopIndex);
      int nodeEndOffset = tokenStop.getStopIndex();
      
      // If this node contains the span of interest then record it and continue visiting the subtrees
      if (nodeStartOffset <= fStartOffset && nodeEndOffset >= fEndOffset) {       
        fNode[0] = element;
        super.visitAny(element);
      }
    }
  }

  private NodeVisitor visitor = new NodeVisitor();

  private CommonTokenStream getTokenStream()
  {
    return (CommonTokenStream) parseController.getParser().getTokenStream();

  }
  
  public Object findNode(Object ast, int offset) {
    return findNode(ast, offset, offset);
  }

  public Object findNode(Object ast, int startOffset, int endOffset) {
    // System.out.println("Looking for node spanning offsets " + startOffset + " => " + endOffset);
    fStartOffset = startOffset;
    fEndOffset = endOffset;
    
    if (!(ast instanceof Tree.CompilationUnit))
      return ast;
  
    Tree.CompilationUnit cu = (Tree.CompilationUnit) ast;
    
    cu.visit(visitor);
    if (fNode[0] == null) {
      System.out.println("Selected node:  null");
    } else {
      System.out.println("Selected node: " + fNode[0]);
    }
    return fNode[0];
  }

  public int getStartOffset(Object node) {
    CommonToken tokenStart = ((CommonToken) node);
    return tokenStart.getStartIndex();
  }

  public int getEndOffset(Object node) {
    CommonToken tokenStop = ((CommonToken) node);
    return tokenStop.getStopIndex();
  }

  public int getLength(Object node) {
    return getEndOffset(node) - getStartOffset(node);
  }

  public IPath getPath(Object node) {
    if (parseController.getPath() != null) {
      return parseController.getPath();
    }
    return new Path("");
  }
}
