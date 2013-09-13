package com.redhat.ceylon.eclipse.code.parse;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtility.getEditorInput;
import static com.redhat.ceylon.eclipse.code.editor.Util.getActivePage;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getCompilationUnit;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.util.FindStatementVisitor;

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
public class CeylonSourcePositionLocator {
        
    public static Node findNode(Tree.CompilationUnit cu, int offset) {
        return findNode(cu, offset, offset+1);
    }
    
    public static Node findNode(Tree.CompilationUnit cu, int startOffset, int endOffset) {
        FindNodeVisitor visitor = new FindNodeVisitor(startOffset, endOffset);
        cu.visit(visitor);
        return visitor.getNode();
    }

    public static Statement findStatement(Tree.CompilationUnit cu, Node node) {
        FindStatementVisitor visitor = new FindStatementVisitor(node, false);
        cu.visit(visitor);
        return visitor.getStatement();
    }

    
    public static Node findScope(Tree.CompilationUnit cu, int startOffset, int endOffset) {
        FindScopeVisitor visitor = new FindScopeVisitor(startOffset, endOffset);
        cu.visit(visitor);
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
    
    public static int getStartOffset(Object node) {
        return getNodeStartOffset(node);
    }
    
    public static int getEndOffset(Object node) {
        return getNodeEndOffset(node)+1;
    }
    
    public static int getLength(Object node) {
        return getEndOffset(node) - getStartOffset(node);
    }
    
    public static Node getIdentifyingNode(Node node) {
	    if (node instanceof Tree.Declaration) {
	        return ((Tree.Declaration) node).getIdentifier();
	    }
	    else if (node instanceof Tree.ModuleDescriptor) {
	        return ((Tree.ModuleDescriptor) node).getImportPath();
	    }
	    else if (node instanceof Tree.PackageDescriptor) {
	        return ((Tree.PackageDescriptor) node).getImportPath();
	    }
	    else if (node instanceof Tree.NamedArgument) {
	        Identifier id = ((Tree.NamedArgument) node).getIdentifier();
	        if (id==null || id.getToken()==null) {
	            return node;
	        }
	        else {
	            return id;
	        }
	    }
	    else if (node instanceof Tree.StaticMemberOrTypeExpression) {
	        return ((Tree.StaticMemberOrTypeExpression) node).getIdentifier();
	    }
	    else if (node instanceof Tree.ExtendedTypeExpression) {
	        //TODO: whoah! this is really ugly!
	        return ((Tree.SimpleType) ((Tree.ExtendedTypeExpression) node).getChildren().get(0))
	                .getIdentifier();
	    }
	    else if (node instanceof Tree.SimpleType) {
	        return ((Tree.SimpleType) node).getIdentifier();
	    }
	    else if (node instanceof Tree.ImportMemberOrType) {
	        return ((Tree.ImportMemberOrType) node).getIdentifier();
	    }
        else if (node instanceof Tree.InitializerParameter) {
            return ((Tree.InitializerParameter) node).getIdentifier();
        }
        else if (node instanceof Tree.MemberLiteral) {
            return ((Tree.MemberLiteral) node).getIdentifier();
        }
        else if (node instanceof Tree.TypeLiteral) {
            return getIdentifyingNode(((Tree.TypeLiteral) node).getType());
        }
	    else {    
	        return node;
	    }
	}
    
    public static void gotoDeclaration(Declaration d, IProject project) {
        gotoDeclaration(d, project, Util.getCurrentEditor());
    }
    
    public static void gotoDeclaration(Declaration d, IProject project, IEditorPart editor) {
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            IProject ep = ce.getParseController().getProject();
            if (ep != null && ep.equals(project)) {
                CeylonParseController cpc = ce.getParseController();
                Node node = getReferencedNode(d, getCompilationUnit(cpc, d));
                if (node != null) {
                    gotoNode(node, project, cpc.getTypeChecker());
                    return;
                }
            }
        }
        if (d.getUnit() instanceof CeylonUnit) {
            CeylonUnit ceylonUnit = (CeylonUnit) d.getUnit();
            Node node = getReferencedNode(d, ceylonUnit.getCompilationUnit());
            if (node != null) {
                gotoNode(node, project, getProjectTypeChecker(project));
            }
        }
    }
	
	public static void gotoNode(Node node, IProject project, TypeChecker tc) {
        gotoLocation(getNodePath(node, project, tc), 
                getNodeStartOffset(node));
    }

    public static void gotoLocation(IPath path, int offset) {
    	gotoLocation(path, offset, 0);
    }
    
    public static void gotoLocation(IPath path, int offset, int length) {
        if (path==null || path.isEmpty()) return;
        IEditorInput editorInput = getEditorInput(path);
        try {
            CeylonEditor editor = (CeylonEditor) getActivePage()
            		.openEditor(editorInput, EDITOR_ID);
            editor.selectAndReveal(offset, length);
        }
        catch (PartInitException pie) {
            pie.printStackTrace();
        }
    }
    
    private static Node toNode(Object node) {
        if (node instanceof CeylonOutlineNode) {
        	CeylonOutlineNode on = (CeylonOutlineNode) node;
            return (Node) on.getTreeNode();
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
    
    public static boolean belongsToProject(Unit unit, IProject project) {
        if (project == null) {
            return false;
        }
        
    	return (unit instanceof IResourceAware) &&
    	        project.equals(((IResourceAware)unit).getProjectResource());
    }

    public static IPath getNodePath(Node node, IProject project, TypeChecker tc) {
        Unit unit = node.getUnit();

        if (unit instanceof IResourceAware) {
    	    IFile fileResource = ((IResourceAware) unit).getFileResource();
    	    if (fileResource != null) {
                return fileResource.getLocation();
    	    }
    	}
    	
    	if ((unit instanceof ExternalSourceFile ) ||
	            (unit instanceof CeylonBinaryUnit )) {
	        IdePhasedUnit externalPhasedUnit = ((CeylonUnit) unit).getPhasedUnit();
	        return new Path(externalPhasedUnit.getUnitFile().getPath());
	    }
    	
    	return null;
    }

    public static Iterator<CommonToken> getTokenIterator(List<CommonToken> tokens, IRegion region) {
        int regionOffset = region.getOffset();
        int regionLength = region.getLength();
        if (regionLength<=0) {
            return Collections.<CommonToken>emptyList().iterator();
        }
        int regionEnd = regionOffset + regionLength - 1;
        if (tokens==null) {
            return null;
        }
        else {
            int firstTokIdx = getTokenIndexAtCharacter(tokens, regionOffset);
            // getTokenIndexAtCharacter() answers the negative of the index of the
            // preceding token if the given offset is not actually within a token.
            if (firstTokIdx < 0) {
                firstTokIdx= -firstTokIdx + 1;
            }
            int lastTokIdx = getTokenIndexAtCharacter(tokens, regionEnd);
            if (lastTokIdx < 0) {
                lastTokIdx= -lastTokIdx;
            }
            return tokens.subList(firstTokIdx, lastTokIdx+1).iterator();
        }
    }
    
    //
    // This function returns the index of the token element
    // containing the offset specified. If such a token does
    // not exist, it returns the negation of the index of the 
    // element immediately preceding the offset.
    //
    public static int getTokenIndexAtCharacter(List<CommonToken> tokens, int offset) {
        //search using bisection
        int low = 0,
                high = tokens.size();
        while (high > low)
        {
            int mid = (high + low) / 2;
            CommonToken midElement = (CommonToken) tokens.get(mid);
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
