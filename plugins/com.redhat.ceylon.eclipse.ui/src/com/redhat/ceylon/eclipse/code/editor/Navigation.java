package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getActivePage;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getEditorInput;
import static com.redhat.ceylon.eclipse.util.JavaSearch.toCeylonDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNodeInUnit;
import static org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.revealInEditor;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.TOGGLE_SHOW_SELECTED_ELEMENT_ONLY;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.IJavaModelAware;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.model.JavaUnit;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Nodes;


public class Navigation {
    
    public static void gotoDeclaration(Referenceable model) {
        if (model!=null) {
            Unit unit = model.getUnit();
            if (unit instanceof CeylonUnit) {
                CeylonUnit ceylonUnit = (CeylonUnit) unit;
                Node node = getReferencedNodeInUnit(model, 
                        ceylonUnit.getCompilationUnit());
                if (node != null) {
                    gotoNode(node, null);
                }
                else if (ceylonUnit instanceof CeylonBinaryUnit) {
                    //special case for Java source in ceylon.language!
                    CeylonBinaryUnit binaryUnit = 
                            (CeylonBinaryUnit) ceylonUnit;
                    String path = binaryUnit.getSourceRelativePath();
                    if (JavaCore.isJavaLikeFileName(path)) {
                        if (model instanceof Declaration) {
                            gotoJavaNode((Declaration) model);
                        }
                    }
                }
            }
            else if (unit instanceof JavaUnit) {
                gotoJavaNode((Declaration) model);
            }
        }
    }
    
    public static void gotoCeylonDeclarationFromJava(IProject project, 
            IJavaElement javaElement) {
        Declaration declaration = 
                toCeylonDeclaration(project, javaElement);
//        if (declaration != null) {
//            Unit u = declaration.getUnit();
//            if (u instanceof CeylonUnit) {
//                PhasedUnit pu = ((CeylonUnit) u).getPhasedUnit();
//                if (pu != null) {
//                    gotoDeclaration(pu, declaration);
//                    return;
//                }
//            }
            gotoDeclaration(declaration);
//        }
    }
    
//    private static void gotoDeclaration(PhasedUnit pu, Declaration declaration) {
//        IEditorInput editorInput = 
//                getEditorInput(pu.getUnit());
//        Node node = getReferencedNode(declaration, 
//                pu.getCompilationUnit());
//        try {
//            CeylonEditor editor = (CeylonEditor) 
//                    getActivePage().openEditor(editorInput, EDITOR_ID);
//            editor.selectAndReveal(getIdentifyingNode(node).getStartIndex(), 
//                    declaration.getName().length());
//        } 
//        catch (PartInitException e) {
//            e.printStackTrace();
//        }
//    }
    
    public static void gotoNode(Node node, CeylonEditor editor) {
        Unit unit = node.getUnit();
        int length = Nodes.getLength(node);
        int startOffset = Nodes.getStartOffset(node);
        Tree.CompilationUnit rootNode = editor==null ? null : 
            editor.getParseController().getRootNode();
        if (rootNode!=null && unit.equals(rootNode.getUnit())) {
            editor.selectAndReveal(startOffset, length);
        }
        else {
            if (unit instanceof IResourceAware) {
                IFile file = ((IResourceAware) unit).getFileResource();
                if (file != null) {
                    gotoFile(file, startOffset, length);
                    return;
                }
            }

            gotoLocation(getNodePath(node), startOffset, length);
        }
    }


    public static void gotoLocation(Unit unit, int startOffset, int length) {
        if (unit instanceof IResourceAware) {
            IFile file = ((IResourceAware) unit).getFileResource();
            if (file != null) {
                gotoFile(file, startOffset, length);
                return;
            }
        }
        gotoLocation(getUnitPath(unit), startOffset, length);
    }
    

    public static void gotoLocation(IPath path, int offset) {
        gotoLocation(path, offset, 0);
    }
    
    public static void gotoLocation(IPath path, int offset, int length) {
        if (path==null || path.isEmpty()) return;
        IEditorInput editorInput;
        try {
            editorInput = getEditorInput(path);
        }
        catch (IllegalArgumentException iae) {
            //this happens for source files that are not in a Ceylon source folder
            return;
        }
        try {
            IEditorPart editor = getActivePage()
                    .openEditor(editorInput, EDITOR_ID);
            if (editor instanceof CeylonEditor) {
                ((CeylonEditor) editor).selectAndReveal(offset, length);
            }
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
    
    public static IPath getNodePath(Node node) {
        return getUnitPath(node.getUnit());
    }

    public static IPath getUnitPath(Unit unit) {
        if (unit instanceof IResourceAware) {
            IFile fileResource = 
                    ((IResourceAware) unit).getFileResource();
            if (fileResource!=null) {
                return fileResource.getLocation();
            }
            else {
            	return new Path(unit.getFullPath());
            }
        }
        
        if ((unit instanceof ExternalSourceFile ) ||
                (unit instanceof CeylonBinaryUnit )) {
            IdePhasedUnit externalPhasedUnit = 
                    ((CeylonUnit) unit).getPhasedUnit();
            return new Path(externalPhasedUnit.getUnitFile().getPath());
        }
        
        return null;
    }
    
    private static IEditorPart openInEditor(IFile file, boolean activate) 
            throws PartInitException {
        if (file!=null) {
            IWorkbenchPage p = getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (p!=null) {
                IEditorPart editorPart = IDE.openEditor(p, file, activate);
                Navigation.initializeHighlightRange(editorPart);
                return editorPart;
            }
        }
        return null;
    }

    private static IEditorPart openInEditor(IEditorInput input, String editorID, boolean activate) 
            throws PartInitException {
        if (input!=null) {
            IWorkbenchPage p = getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (p!=null) {
                IEditorPart editorPart = p.openEditor(input, editorID, activate);
                Navigation.initializeHighlightRange(editorPart);
                return editorPart;
            }
        }
        return null;
    }

    /**
     * Opens an editor suitable for a model element, IFile, IStorage...
     * 
     * @return the IEditorPart or null if wrong element type or opening failed
     */
    public static IEditorPart openInEditor(Object inputElement, boolean activate) throws PartInitException {
        if (inputElement instanceof IFile)
            return openInEditor((IFile) inputElement, activate);
        IEditorInput input= EditorUtil.getEditorInput(inputElement);
        if (input!=null)
            return openInEditor(input, EditorUtil.getEditorID(input, inputElement), activate);
        return null;
    }

    /**
     * Opens an editor suitable for a model element, <code>IFile</code>, or <code>IStorage</code>.
     * The editor is activated by default.
     * 
     * @return the IEditorPart or null if wrong element type or opening failed
     */
    public static IEditorPart openInEditor(Object inputElement) throws PartInitException {
        return openInEditor(inputElement, true);
    }

    public static void gotoLocation(final IResource file, final int offset, int length) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(IMarker.CHAR_START, offset);
        map.put(IMarker.CHAR_END, offset+length);
        if (file instanceof IFile && CeylonBuilder.isCeylon((IFile) file)) {
            map.put(IDE.EDITOR_ID_ATTR, CeylonPlugin.EDITOR_ID);
        }
        try {
            IMarker marker = file.createMarker(IMarker.TEXT);
            marker.setAttributes(map);
            IDE.openEditor(EditorUtil.getActivePage(), marker);
            marker.delete();
        }
        catch (CoreException ce) {} //deliberately swallow it
        /*try {
            IEditorPart editor = EditorUtility.isOpenInEditor(path);
            if (editor == null) {
                editor = EditorUtility.openInEditor(path);
            }
            EditorUtility.revealInEditor(editor, targetOffset, 0);
        }
        catch (PartInitException e) {
            e.printStackTrace();
        }*/
    }

    public static void gotoLocation(final IResource file, final int offset) {
        gotoLocation(file, offset, 0);
    }

    private static void initializeHighlightRange(IEditorPart editorPart) {
        if (editorPart instanceof ITextEditor) {
            IAction toggleAction = 
                    editorPart.getEditorSite().getActionBars()
                        .getGlobalActionHandler(TOGGLE_SHOW_SELECTED_ELEMENT_ONLY);
            boolean enable = toggleAction!=null;
            if (enable && editorPart instanceof CeylonEditor) {
                // TODO Maybe support show segments?
                enable = false; // EditorUtil.getPreferences().getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS);
            }
            else {
                enable = enable && 
                        toggleAction.isEnabled() && 
                        toggleAction.isChecked();
            }
            if (enable) {
                if (toggleAction instanceof TextEditorAction) {
                    // Reset the action
                    ((TextEditorAction) toggleAction).setEditor(null);
                    // Restore the action
                    ((TextEditorAction) toggleAction).setEditor((ITextEditor) editorPart);
                } 
                else {
                    // Un-check
                    toggleAction.run();
                    // Check
                    toggleAction.run();
                }
            }
        }
    }

    public static void gotoJavaNode(Declaration declaration) {
        try {
            IJavaElement element = getJavaElement(declaration);
            if (element!=null) {
                IEditorPart part = openInEditor(element, true);
                if (part!=null) {
                    revealInEditor(part, element);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IJavaElement getJavaElement(Declaration declaration)
            throws JavaModelException {
        if (declaration instanceof Method && declaration.isAnnotation()) {
            declaration = ((Method) declaration).getTypeDeclaration();
        }
        if (declaration.getUnit() instanceof IJavaModelAware) {
            final IJavaModelAware javaModelAware = 
                    (IJavaModelAware) declaration.getUnit();
            return javaModelAware.toJavaElement(declaration);
        }
        return null;
    }

}
