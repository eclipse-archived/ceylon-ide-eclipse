package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getActivePage;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtility.getEditorInput;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;
import static com.redhat.ceylon.eclipse.util.Nodes.getCompilationUnit;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static org.eclipse.jdt.core.JavaCore.isJavaLikeFileName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.util.Nodes;


public class Navigation {
        
    public static void gotoDeclaration(Declaration d, IProject project) {
        gotoDeclaration(d, project, EditorUtil.getCurrentEditor());
    }
    
    public static void gotoDeclaration(Declaration d, IProject project, IEditorPart editor) {
//        if (project!=null) {
            if (project!=null && editor instanceof CeylonEditor) {
                CeylonEditor ce = (CeylonEditor) editor;
                CeylonParseController cpc = ce.getParseController();
                IProject ep = cpc.getProject();
                if (ep != null && ep.equals(project)) {
                    Node node = getReferencedNode(d, getCompilationUnit(d, cpc));
                    if (node != null) {
                        gotoNode(node, project);
                        return;
                    }
                }
            }
            if (d.getUnit() instanceof CeylonUnit) {
                CeylonUnit ceylonUnit = (CeylonUnit) d.getUnit();
                Node node = getReferencedNode(d, ceylonUnit.getCompilationUnit());
                if (node != null) {
                    gotoNode(node, project);
                }
                else if (ceylonUnit instanceof CeylonBinaryUnit) {
                    CeylonBinaryUnit binaryUnit = (CeylonBinaryUnit) ceylonUnit;
                    if (isJavaLikeFileName(binaryUnit.getSourceRelativePath())) {
                        gotoJavaNode(d);
                    }
                }
            }
            else {
                gotoJavaNode(d);
            }
//        }
//        else {
//            //it's coming from the "unversioned" JDK module, which
//            //we don't display multiple choices for, so just pick
//            //the first available project
//            gotoJavaNode(d);
//        }
    }
    
    public static void gotoNode(Node node, IProject project) {
        gotoLocation(getNodePath(node, project), 
                Nodes.getStartOffset(node), 
                Nodes.getLength(node));
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
    
    public static void gotoFile(IFile file, int offset, int length) {
        IWorkbenchPage page = getActivePage();
        IEditorInput input = new FileEditorInput(file);
        if (input!=null) {
            IEditorPart part = page.findEditor(input);
            ITextEditor editor = null;
            if (part instanceof ITextEditor) {
                editor = (ITextEditor) part;
            }
            else {
                try {
                    editor = (ITextEditor) 
                            page.openEditor(input, EDITOR_ID);
                } 
                catch (PartInitException e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (offset>=0) {
                editor.selectAndReveal(offset, length);
            }
            page.activate(editor);
        }
    }
    
//    public static boolean belongsToProject(Unit unit, IProject project) {
//        if (project == null) {
//            return false;
//        }
//        return (unit instanceof IResourceAware) &&
//                project.equals(((IResourceAware)unit).getProjectResource());
//    }

    public static IPath getNodePath(Node node, IProject project) {
        Unit unit = node.getUnit();
        
        if (unit instanceof IResourceAware) {
            IFile fileResource = ((IResourceAware) unit).getFileResource();
            if (fileResource!=null) {
                return fileResource.getLocation();
            }
            else if (project!=null) {
            	return project.getLocation().append(unit.getRelativePath());
            }
        }
        
        if ((unit instanceof ExternalSourceFile ) ||
                (unit instanceof CeylonBinaryUnit )) {
            IdePhasedUnit externalPhasedUnit = ((CeylonUnit) unit).getPhasedUnit();
            return new Path(externalPhasedUnit.getUnitFile().getPath());
        }
        
        return null;
    }

    public static void gotoDeclaration(Referenceable model, 
            CeylonEditor editor) {
        gotoDeclaration(model, editor.getParseController());
    }

    public static void gotoDeclaration(Referenceable model,
            CeylonParseController controller) {
        if (model!=null) {
            Node refNode = getReferencedNode(model, controller);
            if (refNode!=null) {
                gotoNode(refNode, controller.getProject());
            }
            else if (model instanceof Declaration) {
                gotoJavaNode((Declaration) model);
            }
        }
    }
    
}
