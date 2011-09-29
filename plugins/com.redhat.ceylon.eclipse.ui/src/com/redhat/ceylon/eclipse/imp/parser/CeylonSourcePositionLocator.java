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
import org.eclipse.imp.editor.EditorUtility;
import org.eclipse.imp.editor.IRegionSelectionService;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
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
    
    public static enum OccurrenceLocation { 
        EXTENDS, 
        SATISFIES, 
        UPPER_BOUND, 
        IMPORT, 
        EXPRESSION,
        PARAMETER_LIST,
        TYPE_PARAMETER_LIST,
        TYPE_ARGUMENT_LIST
    }
    
    public static OccurrenceLocation getOccurrenceLocation(Tree.CompilationUnit cu, Node node) {
        if (node.getToken()==null) return null;
        FindNodeVisitor visitor = new FindNodeVisitor(node.getStartIndex(), node.getStopIndex()+1);
        //System.out.println("Looking for node spanning offsets " + startOffset + " => " + endOffset);    
        cu.visit(visitor);
        //System.out.println("selected node: " + visitor.getNode());
        return visitor.getOccurrenceLocation();
    }
    
    public static Node findScope(Tree.CompilationUnit cu, int startOffset, int endOffset) {
        FindScopeVisitor visitor = new FindScopeVisitor(startOffset, endOffset);
        //System.out.println("Looking for node spanning offsets " + startOffset + " => " + endOffset);    
        cu.visit(visitor);
        //System.out.println("selected node: " + visitor.getNode());
        return visitor.getNode();
    }
    
    /*public static Node findNode(Tree.CompilationUnit cu, IRegion region) {
        return findNode(cu, region.getOffset(), 
                region.getOffset()+region.getLength());
    }*/
    
    public static Node findNode(Tree.CompilationUnit cu, ITextSelection s) {
        return findNode(cu, s.getOffset(), s.getOffset()+s.getLength());
    }
    
    public static Node findScope(Tree.CompilationUnit cu, ITextSelection s) {
        return findScope(cu, s.getOffset(), s.getOffset()+s.getLength());
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
        return getNodePath(entity, parseController.getTypeChecker());
    }
    
    public void gotoNode(Node node) {
        gotoNode(node, parseController.getTypeChecker());
    }
    
    public static void gotoNode(Node node, TypeChecker typeChecker) {
        IEditorInput editorInput = EditorUtility.getEditorInput(getNodePath(node, typeChecker));
        try {
            CeylonEditor editor = (CeylonEditor) Util.getActivePage().openEditor(editorInput, CeylonPlugin.EDITOR_ID);
            IRegionSelectionService rss = (IRegionSelectionService) editor.getAdapter(IRegionSelectionService.class);
            rss.selectAndReveal(getNodeStartOffset(node), 0);
        }
        catch (PartInitException pie) {
            pie.printStackTrace();
        }
        /*if (!project.getFullPath().lastSegment().equals(nodePath.segment(0))) {
            IFileStore fileLocation = EFS.getLocalFileSystem().getStore(nodePath);
            FileStoreEditorInput fileStoreEditorInput = new FileStoreEditorInput(
                                        fileLocation);
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                        .getActivePage();
            try {
                page.openEditor(fileStoreEditorInput, CeylonPlugin.EDITOR_ID);
            }
            catch (PartInitException e) {
                e.printStackTrace();
            }
        }
        else {
            IPath path = nodePath.removeFirstSegments(1);
            int targetOffset = getNodeStartOffset(node);
            IResource file = project.findMember(path);
            if (file!=null) {
                Util.gotoLocation(file, targetOffset);
            }
        }*/
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
    
    public static IPath getNodePath(Object entity, TypeChecker typeChecker) {
        if (entity instanceof Node) {
            Node node= (Node) entity;
            Unit unit = node.getUnit();
            String fileName = unit.getFilename();
            String packagePath = unit.getPackage().getQualifiedNameString().replace('.', '/');
            PhasedUnit phasedUnit = typeChecker.getPhasedUnitFromRelativePath(packagePath + "/" + fileName);
            if (phasedUnit != null) {
                VirtualFile unitFile = phasedUnit.getUnitFile();
                if (unitFile instanceof IFileVirtualFile) {
                    IFileVirtualFile file = (IFileVirtualFile) unitFile;
                    IFile fileResource = (IFile) file.getResource();
                    return fileResource.getFullPath();
                }
                else {
                    return Path.fromOSString(unitFile.getPath());
                }
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
    public static int getTokenIndexAtCharacter(CommonTokenStream stream, int offset) {
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
    
    /*public static String getIndent(CommonTokenStream tokens, Node node) {
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
    }*/
}
