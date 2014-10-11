package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getActivePage;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getEditorInput;
import static com.redhat.ceylon.eclipse.util.Nodes.getCompilationUnit;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static org.eclipse.jdt.core.JavaCore.isJavaLikeFileName;
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
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.IProjectAware;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Nodes;


public class Navigation {
        
    public static void gotoDeclaration(Declaration d, IProject project) {
        gotoDeclaration(d, project, getCurrentEditor());
    }
    
    public static void gotoDeclaration(Declaration d, IProject project, 
            IEditorPart editor) {
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
    
    public static void gotoDeclaration(IProject project, PhasedUnit pu,
            Declaration declaration) {
        IEditorInput editorInput = getEditorInput(pu.getUnit());
        Node node = getReferencedNode(declaration, 
                pu.getCompilationUnit());
        try {
            CeylonEditor editor = (CeylonEditor) 
                    getActivePage().openEditor(editorInput, EDITOR_ID);
            editor.selectAndReveal(getIdentifyingNode(node).getStartIndex(), 
                    declaration.getName().length());
        } 
        catch (PartInitException e) {
            e.printStackTrace();
        }
    }
    
    public static void gotoNode(Node node, IProject project) {
        Unit unit = node.getUnit();
        int length = Nodes.getLength(node);
        int startOffset = Nodes.getStartOffset(node);
        if (unit instanceof IResourceAware) {
            IFile file = ((IResourceAware) unit).getFileResource();
            if (file != null) {
                gotoFile(file, startOffset, length);
                return;
            }
        }
        
        gotoLocation(getNodePath(node, project), 
                startOffset, 
                length);
    }


    public static void gotoLocation(Unit unit, int startOffset, int length) {
        if (unit instanceof IResourceAware) {
            IFile file = ((IResourceAware) unit).getFileResource();
            if (file != null) {
                gotoFile(file, startOffset, length);
                return;
            }
        }

        IPath path;
        if (unit instanceof IProjectAware) {
            path = getUnitPath(((IProjectAware) unit).getProject(), unit);
        } else {
            path = getUnitPath(null, unit);
        }
        gotoLocation(path, startOffset, length);
    }
    

    public static void gotoLocation(IPath path, int offset) {
        gotoLocation(path, offset, 0);
    }
    
    public static void gotoLocation(IPath path, int offset, int length) {
        if (path==null || path.isEmpty()) return;
        IEditorInput editorInput = getEditorInput(path);
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
    
//    public static boolean belongsToProject(Unit unit, IProject project) {
//        if (project == null) {
//            return false;
//        }
//        return (unit instanceof IResourceAware) &&
//                project.equals(((IResourceAware)unit).getProjectResource());
//    }

    public static IPath getNodePath(Node node, IProject project) {
        Unit unit = node.getUnit();
        
        return getUnitPath(project, unit);
    }

    public static IPath getUnitPath(IProject project, Unit unit) {
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

    /**
     * Selects and reveals the given offset and length in the given editor part.
     */
    //private static void revealInEditor(IEditorPart editor, final int offset, final int length) {
    //  if (editor instanceof ITextEditor) {
    //      ((ITextEditor) editor).selectAndReveal(offset, length);
    //      return;
    //  }
    //  // Support for non-text editor - try IGotoMarker interface
    //  if (editor instanceof IGotoMarker) {
    //      final IEditorInput input= editor.getEditorInput();
    //      if (input instanceof IFileEditorInput) {
    //          final IGotoMarker gotoMarkerTarget= (IGotoMarker) editor;
    //          WorkspaceModifyOperation op= new WorkspaceModifyOperation() {
    //              protected void execute(IProgressMonitor monitor) throws CoreException {
    //                  IMarker marker= null;
    //                  try {
    //                      marker = ((IFileEditorInput) input).getFile().createMarker(IMarker.TEXT);
    //                      String[] attributeNames = new String[] {IMarker.CHAR_START, IMarker.CHAR_END};
    //                      Object[] values = new Object[] {offset, offset + length};
    //                      marker.setAttributes(attributeNames, values);
    //                      gotoMarkerTarget.gotoMarker(marker);
    //                  } finally {
    //                      if (marker!=null)
    //                          marker.delete();
    //                  }
    //              }
    //          };
    //          try {
    //              op.run(null);
    //          } catch (InvocationTargetException ex) {
    //              // reveal failed
    //          } catch (InterruptedException e) {
    //              Assert.isTrue(false, "this operation can not be canceled"); //$NON-NLS-1$
    //          }
    //      }
    //      return;
    //  }
    //  /*
    //   * Workaround: send out a text selection XXX: Needs to be improved, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=32214
    //   */
    //  if (editor != null && editor.getEditorSite().getSelectionProvider() != null) {
    //      IEditorSite site= editor.getEditorSite();
    //      if (site==null)
    //          return;
    //      ISelectionProvider provider= editor.getEditorSite().getSelectionProvider();
    //      if (provider==null)
    //          return;
    //      provider.setSelection(new TextSelection(offset, length));
    //  }
    //}
    
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

    /**
     * Selects and reveals the given region in the given editor part.
     */
    //private static void revealInEditor(IEditorPart part, IRegion region) {
    //  if (part!=null && region!=null)
    //      revealInEditor(part, region.getOffset(), region.getLength());
    //}
    
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

    /**
     * Tests if a given input element is currently shown in an editor
     * 
     * @return the IEditorPart if shown, null if element is not open in an editor
     */
    //private static IEditorPart isOpenInEditor(Object inputElement) {
    //  IEditorInput input= null;
    //  input= getEditorInput(inputElement);
    //  if (input!=null) {
    //      IWorkbenchPage p= getWorkbench().getActiveWorkbenchWindow().getActivePage();
    //      if (p!=null) {
    //          return p.findEditor(input);
    //      }
    //  }
    //  return null;
    //}
    
    private static void initializeHighlightRange(IEditorPart editorPart) {
        if (editorPart instanceof ITextEditor) {
            IAction toggleAction = 
                    editorPart.getEditorSite().getActionBars()
                        .getGlobalActionHandler(TOGGLE_SHOW_SELECTED_ELEMENT_ONLY);
            boolean enable = toggleAction!=null;
            if (enable && editorPart instanceof CeylonEditor) {
                // TODO Maybe support show segments?
                enable = false; // CeylonPlugin.getInstance().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS);
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
    
}
