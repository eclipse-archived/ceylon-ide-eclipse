package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getActivePage;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getEditorInput;
import static com.redhat.ceylon.eclipse.util.JavaSearch.toCeylonDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static com.redhat.ceylon.ide.common.util.toCeylonString_.toCeylonString;
import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;
import static org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.revealInEditor;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.TOGGLE_SHOW_SELECTED_ELEMENT_ONLY;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
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

import com.redhat.ceylon.common.Backends;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.model.CeylonBinaryUnit;
import com.redhat.ceylon.ide.common.model.CeylonUnit;
import com.redhat.ceylon.ide.common.model.ExternalSourceFile;
import com.redhat.ceylon.ide.common.model.IJavaModelAware;
import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.model.JavaUnit;
import com.redhat.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.ide.common.typechecker.IdePhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Unit;


public class Navigation {
    
    public static ITextEditor gotoDeclaration(Referenceable model) {
        if (model==null) {
            return null;
        }
        else {
            Unit unit = model.getUnit();
            if (unit instanceof CeylonUnit) {
                CeylonUnit ceylonUnit = (CeylonUnit) unit;
                Node node = 
                        getReferencedNode(model, 
                                ceylonUnit.getCompilationUnit());
                if (node != null) {
                    return gotoNode(node, null);
                }
                else if (ceylonUnit instanceof CeylonBinaryUnit) {
                    //special case for Java source in ceylon.language!
                    CeylonBinaryUnit binaryUnit = 
                            (CeylonBinaryUnit) ceylonUnit;
                    String path = toJavaString(binaryUnit.getSourceRelativePath());
                    if (JavaCore.isJavaLikeFileName(path) && 
                            model instanceof Declaration) {
                        return gotoJavaNode((Declaration) model);
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
            else if (unit instanceof JavaUnit) {
                return gotoJavaNode((Declaration) model);
            }
            else {
                return null;
            }
        }
    }
    
    public static ITextEditor gotoCeylonDeclarationFromJava(IProject project, 
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
            return gotoDeclaration(declaration);
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
    
    public static ITextEditor gotoNode(Node node, CeylonEditor editor) {
        Unit unit = node.getUnit();
        Node identifyingNode = getIdentifyingNode(node);
        int length = identifyingNode.getDistance();
        int startOffset = identifyingNode.getStartIndex();
        Tree.CompilationUnit rootNode = 
                editor==null ? null : 
                    editor.getParseController()
                        .getLastCompilationUnit();
        if (rootNode!=null && unit.equals(rootNode.getUnit())) {
            editor.selectAndReveal(startOffset, length);
            return editor;
        }
        else {
            if (unit instanceof IResourceAware) {
                IResourceAware<IProject,IFolder,IFile> 
                    ra = (IResourceAware) unit;
                IFile file = ra.getResourceFile();
                if (file != null) {
                    return gotoFile(file, startOffset, length);
                }
            }

            return gotoLocation(getNodePath(node), startOffset, length);
        }
    }


    public static ITextEditor gotoLocation(Unit unit, 
            int startOffset, int length) {
        if (unit instanceof IResourceAware) {
            IResourceAware<IProject,IFolder,IFile> 
                ra = (IResourceAware) unit;
            IFile file = ra.getResourceFile();
            if (file != null) {
                return gotoFile(file, startOffset, length);
            }
        }
        return gotoLocation(getUnitPath(unit), startOffset, length);
    }
    

    public static ITextEditor gotoLocation(IPath path, int offset) {
        return gotoLocation(path, offset, 0);
    }
    
    public static ITextEditor gotoLocation(IPath path, int offset, int length) {
        if (path==null || path.isEmpty()) {
            return null;
        }
        IEditorInput editorInput;
        try {
            editorInput = getEditorInput(path);
        }
        catch (IllegalArgumentException iae) {
            //this happens for source files that are not in a Ceylon source folder
            return null;
        }
        try {
            ITextEditor editor = 
                    (ITextEditor) 
                        getActivePage()
                            .openEditor(editorInput, 
                                    EDITOR_ID);
            editor.selectAndReveal(offset, length);
            return editor;
        }
        catch (PartInitException pie) {
            pie.printStackTrace();
            return null;
        }
    }
    
    public static ITextEditor gotoFile(IFile file, int offset, int length) {
        IWorkbenchPage page = getActivePage();
        IEditorInput input = new FileEditorInput(file);
        IEditorPart part = page.findEditor(input);
        ITextEditor editor = null;
        if (part instanceof ITextEditor) {
            editor = (ITextEditor) part;
        }
        else {
            try {
                editor = (ITextEditor) 
                        page.openEditor(input, 
                                EDITOR_ID);
            } 
            catch (PartInitException e) {
                e.printStackTrace();
                return null;
            }
        }
        if (offset>=0) {
            editor.selectAndReveal(offset, length);
        }
        page.activate(editor);
        return editor;
    }
    
    public static IPath getNodePath(Node node) {
        return getUnitPath(node.getUnit());
    }

    public static IPath getUnitPath(Unit unit) {
        if (unit instanceof IResourceAware) {
            IResourceAware<IProject,IFolder,IFile> 
                ra = (IResourceAware) unit;
            IFile fileResource = ra.getResourceFile();
            return fileResource!=null ? 
                    fileResource.getLocation() : 
                    new Path(unit.getFullPath());
        }
        
        if ((unit instanceof ExternalSourceFile) ||
                (unit instanceof CeylonBinaryUnit)) {
            CeylonUnit ceylonUnit = (CeylonUnit) unit;
            IdePhasedUnit externalPhasedUnit = 
                    ceylonUnit.getPhasedUnit();
            VirtualFile file = 
                    externalPhasedUnit.getUnitFile();
            return new Path(file.getPath());
        }
        
        return null;
    }
    
    private static IEditorPart openInEditor(IFile file, 
            boolean activate) 
                    throws PartInitException {
        if (file!=null) {
            IWorkbenchPage page = 
                    getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage();
            if (page!=null) {
                IEditorPart editorPart = 
                        IDE.openEditor(page, file, activate);
                Navigation.initializeHighlightRange(editorPart);
                return editorPart;
            }
        }
        return null;
    }

    private static IEditorPart openInEditor(IEditorInput input, 
            String editorID, boolean activate) 
                    throws PartInitException {
        if (input!=null) {
            IWorkbenchPage page = 
                    getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage();
            if (page!=null) {
                IEditorPart editorPart = 
                        page.openEditor(input, editorID, activate);
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
    public static IEditorPart openInEditor(Object inputElement, 
            boolean activate) 
                    throws PartInitException {
        if (inputElement instanceof IFile) {
            IFile file = (IFile) inputElement;
            return openInEditor(file, activate);
        }
        IEditorInput input = EditorUtil.getEditorInput(inputElement);
        if (input!=null) {
            String id = EditorUtil.getEditorID(input, inputElement);
            return openInEditor(input, id, activate);
        }
        return null;
    }

    /**
     * Opens an editor suitable for a model element, <code>IFile</code>, or <code>IStorage</code>.
     * The editor is activated by default.
     * 
     * @return the IEditorPart or null if wrong element type or opening failed
     */
    public static IEditorPart openInEditor(Object inputElement) 
            throws PartInitException {
        return openInEditor(inputElement, true);
    }

    public static void gotoLocation(final IResource file, final int offset, int length) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(IMarker.CHAR_START, offset);
        map.put(IMarker.CHAR_END, offset+length);
        if (file instanceof IFile && 
                CeylonBuilder.isCeylon((IFile) file)) {
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
                    editorPart.getEditorSite()
                        .getActionBars()
                        .getGlobalActionHandler(
                                TOGGLE_SHOW_SELECTED_ELEMENT_ONLY);
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
                    TextEditorAction textEditorAction = 
                            (TextEditorAction) toggleAction;
                    ITextEditor editor = (ITextEditor) editorPart;
                    // Reset the action
                    textEditorAction.setEditor(null);
                    // Restore the action
                    textEditorAction.setEditor(editor);
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

    public static ITextEditor gotoJavaNode(Declaration declaration) {
        try {
            IJavaElement element = getJavaElement(declaration);
            if (element==null) {
                return null;
            }
            else {
                IEditorPart part = openInEditor(element, true);
                if (part!=null) {
                    revealInEditor(part, element);
                }
                return (ITextEditor) part;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static IJavaElement getJavaElement(Declaration declaration)
            throws JavaModelException {
        return getJavaElement(declaration, null);
    }

    public static IJavaElement getJavaElement(Declaration declaration, IProgressMonitor monitor)
            throws JavaModelException {
        if (declaration instanceof Function && declaration.isAnnotation()) {
            Function fun = (Function) declaration;
            declaration = fun.getTypeDeclaration();
        }
        Unit unit = declaration.getUnit();
        if (unit instanceof IJavaModelAware) {
            IJavaModelAware<IProject, ITypeRoot, IJavaElement> 
                javaModelAware = (IJavaModelAware) unit;
            return javaModelAware.toJavaElement(declaration, 
                    utilJ2C().wrapProgressMonitor(monitor));
        }
        return null;
    }

    public static Referenceable resolveNative(
            Declaration dec, Backends backends) {
        if (backends.none()) {
            return null;
        }
        
        Unit unit = dec.getUnit();
        if (unit instanceof CeylonBinaryUnit) {
            CeylonBinaryUnit binaryUnit = 
                    (CeylonBinaryUnit) unit;
            ExternalPhasedUnit phasedUnit = 
                    binaryUnit.getPhasedUnit();
            if (phasedUnit != null) {
                Unit sourceFile = phasedUnit.getUnit();
                if (sourceFile != null) {
                    String sourceRelativePath = 
                            toJavaString(binaryUnit.getCeylonModule()
                                .toSourceUnitRelativePath(
                                        toCeylonString(unit.getRelativePath())));
                    boolean isCeylon = sourceRelativePath!=null && 
                            sourceRelativePath.endsWith(".ceylon");
                    for (Declaration sourceDecl: 
                            sourceFile.getDeclarations()) {
                        boolean thisOne = isCeylon ?
                                sourceDecl.equals(dec) :
                                sourceDecl.getQualifiedNameString()
                                .equals(dec.getQualifiedNameString());
                        if (thisOne) {
                            return ModelUtil.getNativeDeclaration(sourceDecl, backends);
                        }
                    }
                }
            }
        }
        
        return ModelUtil.getNativeDeclaration(dec, backends);
    }

}
