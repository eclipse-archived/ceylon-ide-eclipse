package com.redhat.ceylon.eclipse.code.parse;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtility.getEditorInput;
import static com.redhat.ceylon.eclipse.code.editor.Util.getActivePage;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode;
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
        return getNodeEndOffset(node);
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
	        return ((Tree.NamedArgument) node).getIdentifier();
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
	    else {    
	        return node;
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
    	return getPath(unit, project)!=null;
    }

	private static IPath getPath(Unit unit, IProject project) {
		try {
			for (IPackageFragmentRoot srcDir: JavaCore.create(project)
					.getPackageFragmentRoots()) {
				IPackageFragment pkg = srcDir.getPackageFragment(unit.getPackage()
						.getQualifiedNameString());
				IResource rsrc = pkg.getResource();
				if (rsrc instanceof IFolder && rsrc.exists()) {
					IFile file = ((IFolder) rsrc).getFile(unit.getFilename());
					if (file!=null && file.exists()) {
						return file.getLocation();
					}
				}
			}
		} 
    	catch (JavaModelException e) {
			e.printStackTrace();
		}
    	return null;
	}
    
    public static IPath getNodePath(Node node, IProject project, TypeChecker tc) {
    	Unit unit = node.getUnit();
    	
    	//look for a source file in a project
    	if (project!=null) {
    		//first look for it in the current project
    		IPath path = getPath(unit, project);
    		if (path!=null) return path;
    		try {
    			//now look for it in projects that the current
    			//project depends on
    			for (IProject p: project.getReferencedProjects()) {
    				path = getPath(unit, p);
    				if (path!=null) return path;
    			}
    		} 
    		catch (CoreException e) {
    			e.printStackTrace();
    		}
    	}
    	else for (IProject p: getProjects()) {
    		//if we weren't given a project,
    		//iterate over all of them (note
    		//that this case happens in the 
    		//hierarchy popup for an archive
    		//unit that is extended by a 
    		//project source file)
    		IPath path = getPath(unit, p);
    		if (path!=null) return path;
    	}

    	//finally look for it in a module archive 
    	PhasedUnit pu = tc.getPhasedUnitFromRelativePath(getRelativePath(unit));
    	if (pu!=null) {
    		return new Path(pu.getUnitFile().getPath());
    		/*VirtualFile unitFile = pu.getUnitFile();
                if (unitFile instanceof IFileVirtualFile) {
                    return ((IFileVirtualFile) unitFile).getFile().getFullPath();
                }
                else {
                    return new Path(unitFile.getPath());
                }*/
    	}

    	return null;

    }

	private static String getRelativePath(Unit unit) {
		String fileName = unit.getFilename();
		String packagePath = unit.getPackage().getQualifiedNameString().replace('.', '/');
		String fileRelativePath = packagePath + "/" + fileName;
		return fileRelativePath;
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
