package com.redhat.ceylon.eclipse.imp.parser;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getIdentifyingNode;

import java.util.Collections;
import java.util.Iterator;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.jface.text.IRegion;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnnotationList;
import com.redhat.ceylon.eclipse.vfs.IFileVirtualFile;

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
    
    public Node findNode(Object ast, int offset) {
        return findNode(ast, offset, offset+1);
    }
    
    public Node findNode(Object ast, int startOffset, int endOffset) {   
        Tree.CompilationUnit cu = (Tree.CompilationUnit) ast;
        return findNode(cu, startOffset, endOffset);
    }
    
    public static Node findNode(Tree.CompilationUnit cu, int offset) {
        return findNode(cu, offset, offset+1);
    }
    
    public static Node findNode(Tree.CompilationUnit cu, int startOffset, int endOffset) {
        FindNodeVisitor visitor = new FindNodeVisitor(startOffset, endOffset);
        //System.out.println("Looking for node spanning offsets " + startOffset + " => " + endOffset);    
        cu.visit(visitor);
        //System.out.println("selected node: " + visitor.getNode());
        return visitor.getNode();
    }
    
    public Node findNode(IASTFindReplaceTarget frt) {
        return findNode(parseController.getRootNode(), frt);
    }
    
    public static Node findNode(Tree.CompilationUnit cu, IASTFindReplaceTarget frt) {
        return findNode(cu, frt.getSelection().x, 
                frt.getSelection().x+frt.getSelection().y);
    }
    
    public int getStartOffset(Object node) {
        return getNodeStartOffset(node);
    }
    
    public int getEndOffset(Object node) {
        return getNodeEndOffset(node);
    }
    
    public int getLength(Object node) {
        return getEndOffset(node) - getStartOffset(node);
    }
    
    public IPath getPath(Object entity) {
        return getNodePath(entity, parseController.getPhasedUnits());
    }
    
    private static Node toNode(Object node) {
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
    
    public static int getNodeStartOffset(Object node) {
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
    
    public static int getNodeEndOffset(Object node) {
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
    
    public static IPath getNodePath(Object entity, PhasedUnits units) {
        if (entity instanceof Node) {
            Node node= (Node) entity;
            Unit unit = node.getUnit();
            String fileName = unit.getFilename();
            String packagePath = unit.getPackage().getQualifiedNameString().replace('.', '/');
            PhasedUnit phasedUnit = units.getPhasedUnitFromRelativePath(packagePath + "/" + fileName);
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
    
    public static Iterator<Token> getTokenIterator(CommonTokenStream stream, IRegion region) {
        int regionOffset = region.getOffset();
        int regionLength = region.getLength();
        if (regionLength<=0) {
            return Collections.<Token>emptyList().iterator();
        }
        int regionEnd = regionOffset + regionLength - 1;
        if (stream==null) {
            return null;
        }
        else {
            int firstTokIdx = getTokenIndexAtCharacter(stream, regionOffset);
            // getTokenIndexAtCharacter() answers the negative of the index of the
            // preceding token if the given offset is not actually within a token.
            if (firstTokIdx < 0) {
                firstTokIdx= -firstTokIdx + 1;
            }
            int lastTokIdx = getTokenIndexAtCharacter(stream, regionEnd);
            if (lastTokIdx < 0) {
                lastTokIdx= -lastTokIdx;
            }
            return stream.getTokens().subList(firstTokIdx, lastTokIdx+1).iterator();
        }
    }
    
    //
    // This function returns the index of the token element
    // containing the offset specified. If such a token does
    // not exist, it returns the negation of the index of the 
    // element immediately preceding the offset.
    //
    private static int getTokenIndexAtCharacter(CommonTokenStream stream, int offset) {
      //search using bisection
      int low = 0,
          high = stream.getTokens().size();
      while (high > low)
      {
          int mid = (high + low) / 2;
          CommonToken midElement = (CommonToken) stream.getTokens().get(mid);
          if (offset >= midElement.getStartIndex() &&
              offset <= midElement.getStopIndex())
               return mid;
          else if (offset < midElement.getStartIndex())
               high = mid;
          else low = mid + 1;
      }

      return -(low - 1);
    }

    public static String getIndent(CommonTokenStream tokens, Node node) {
        int prevIndex = node.getToken().getTokenIndex()-1;
        if (node instanceof Tree.Declaration) {
            AnnotationList anl = ((Tree.Declaration) node).getAnnotationList();
            if (anl!=null && !anl.getAnnotations().isEmpty()) { 
                prevIndex = anl.getAnnotations().get(0).getToken().getTokenIndex()-1;
            }
        }
        if (prevIndex>=0) {
            Token prevToken = tokens.get(prevIndex);
            if (prevToken.getChannel()==Token.HIDDEN_CHANNEL) {
                return prevToken.getText();
            }
        }
        return "";
    }
}
