package com.redhat.ceylon.eclipse.code.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextEditorAction;

/**
 * A number of routines for mapping editor inputs to/from model elements.
 * 
 * Use 'isOpenInEditor' to test if an element is already open in a editor.
 * Use 'openInEditor' to force opening an element in a editor.
 * With 'getWorkingCopy' you get the working copy (element in the editor) of an element.
 */
public class EditorUtility {
	
    public static boolean isEditorInput(Object element, IEditorPart editor) {
        if (editor != null) {
            return editor.getEditorInput().equals(getEditorInput(element));
        }
        return false;
    }

    /**
     * Tests if a given input element is currently shown in an editor
     * 
     * @return the IEditorPart if shown, null if element is not open in an editor
     */
    public static IEditorPart isOpenInEditor(Object inputElement) {
        IEditorInput input= null;
        input= getEditorInput(inputElement);
        if (input != null) {
            IWorkbenchPage p= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (p != null) {
                return p.findEditor(input);
            }
        }
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

    /**
     * Opens an editor suitable for a model element, IFile, IStorage...
     * 
     * @return the IEditorPart or null if wrong element type or opening failed
     */
    public static IEditorPart openInEditor(Object inputElement, boolean activate) throws PartInitException {
        if (inputElement instanceof IFile)
            return openInEditor((IFile) inputElement, activate);
        if (inputElement instanceof ISourceEntity) {
            ICompilationUnit cu= (ICompilationUnit) ((ISourceEntity) inputElement).getAncestor(ICompilationUnit.class);

            if (cu != null /*&& !JavaModelUtil.isPrimary(cu) */) {
                /*
                 * Support for non-primary working copy. Try to reveal it in the active editor.
                 */
                IWorkbenchPage page= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                if (page != null) {
                    IEditorPart editor= page.getActiveEditor();
                    if (editor != null) {
                        ISourceEntity editorCU= EditorUtility.getEditorInputModelElement(editor, false);
                        if (editorCU == cu) {
                            EditorUtility.revealInEditor(editor, (ISourceEntity) inputElement);
                            return editor;
                        }
                    }
                }
            }
        }
        IEditorInput input= getEditorInput(inputElement);
        if (input != null)
            return openInEditor(input, getEditorID(input, inputElement), activate);
        return null;
    }

    /**
     * Selects a Java Element in an editor
     */
    public static void revealInEditor(IEditorPart part, ISourceEntity element) {
        if (element == null)
            return;
        if (part instanceof CeylonEditor) {
            // TODO If/when there exist model elements for things smaller than CUs (e.g. types), need to do something here
//            ((UniversalEditor) part).setSelection(element);
            return;
        }
    }

    /**
     * Selects and reveals the given region in the given editor part.
     */
    public static void revealInEditor(IEditorPart part, IRegion region) {
        if (part != null && region != null)
            revealInEditor(part, region.getOffset(), region.getLength());
    }

    /**
     * Selects and reveals the given offset and length in the given editor part.
     */
    public static void revealInEditor(IEditorPart editor, final int offset, final int length) {
        if (editor instanceof ITextEditor) {
            ((ITextEditor) editor).selectAndReveal(offset, length);
            return;
        }
        // Support for non-text editor - try IGotoMarker interface
        if (editor instanceof IGotoMarker) {
            final IEditorInput input= editor.getEditorInput();
            if (input instanceof IFileEditorInput) {
                final IGotoMarker gotoMarkerTarget= (IGotoMarker) editor;
                WorkspaceModifyOperation op= new WorkspaceModifyOperation() {
                    protected void execute(IProgressMonitor monitor) throws CoreException {
                        IMarker marker= null;
                        try {
                            marker = ((IFileEditorInput) input).getFile().createMarker(IMarker.TEXT);
                        	String [] attributeNames = new String[] {IMarker.CHAR_START, IMarker.CHAR_END};
                        	Object [] values = new Object[] {offset, offset + length};
                            marker.setAttributes(attributeNames, values);
                            
                            gotoMarkerTarget.gotoMarker(marker);
                        } finally {
                            if (marker != null)
                                marker.delete();
                        }
                    }
                };
                try {
                    op.run(null);
                } catch (InvocationTargetException ex) {
                    // reveal failed
                } catch (InterruptedException e) {
                    Assert.isTrue(false, "this operation can not be canceled"); //$NON-NLS-1$
                }
            }
            return;
        }
        /*
         * Workaround: send out a text selection XXX: Needs to be improved, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=32214
         */
        if (editor != null && editor.getEditorSite().getSelectionProvider() != null) {
            IEditorSite site= editor.getEditorSite();
            if (site == null)
                return;
            ISelectionProvider provider= editor.getEditorSite().getSelectionProvider();
            if (provider == null)
                return;
            provider.setSelection(new TextSelection(offset, length));
        }
    }

    private static IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
        if (file != null) {
            IWorkbenchPage p= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (p != null) {
                IEditorPart editorPart= IDE.openEditor(p, file, activate);
                initializeHighlightRange(editorPart);
                return editorPart;
            }
        }
        return null;
    }

    private static IEditorPart openInEditor(IEditorInput input, String editorID, boolean activate) throws PartInitException {
        if (input != null) {
            IWorkbenchPage p= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (p != null) {
                IEditorPart editorPart= p.openEditor(input, editorID, activate);
                initializeHighlightRange(editorPart);
                return editorPart;
            }
        }
        return null;
    }

    private static void initializeHighlightRange(IEditorPart editorPart) {
        if (editorPart instanceof ITextEditor) {
            IAction toggleAction= editorPart.getEditorSite().getActionBars().getGlobalActionHandler(
                    ITextEditorActionDefinitionIds.TOGGLE_SHOW_SELECTED_ELEMENT_ONLY);
            boolean enable= toggleAction != null;
            if (enable && editorPart instanceof CeylonEditor)
                // TODO Maybe support show segments?
                enable= false; // RuntimePlugin.getInstance().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS);
            else
                enable= enable && toggleAction.isEnabled() && toggleAction.isChecked();
            if (enable) {
                if (toggleAction instanceof TextEditorAction) {
                    // Reset the action
                    ((TextEditorAction) toggleAction).setEditor(null);
                    // Restore the action
                    ((TextEditorAction) toggleAction).setEditor((ITextEditor) editorPart);
                } else {
                    // Un-check
                    toggleAction.run();
                    // Check
                    toggleAction.run();
                }
            }
        }
    }

    /**
     * @deprecated Made it public again for java debugger UI.
     */
    public static String getEditorID(IEditorInput input, Object inputObject) {
        IEditorDescriptor editorDescriptor;
        try {
            if (input instanceof IFileEditorInput)
                editorDescriptor= IDE.getEditorDescriptor(((IFileEditorInput) input).getFile());
            else
                editorDescriptor= IDE.getEditorDescriptor(input.getName());
        } catch (PartInitException e) {
            return null;
        }
        if (editorDescriptor != null)
            return editorDescriptor.getId();
        return null;
    }

    /**
     * Returns the given editor's input as a model element.
     * 
     * @param editor
     *            the editor
     * @param primaryOnly
     *            if <code>true</code> only primary working copies will be returned
     * @return the given editor's input as model element or <code>null</code> if none
     * @since 3.2
     */
    public static ISourceEntity getEditorInputModelElement(IEditorPart editor, boolean primaryOnly) {
        Assert.isNotNull(editor);
        IEditorInput editorInput= editor.getEditorInput();
        if (editorInput == null)
            return null;
        ISourceEntity se= getEditorInputModelElement(editorInput);
        if (se != null || primaryOnly)
            return se;
        return null;
//      return RuntimePlugin.getInstance().getWorkingCopyManager().getWorkingCopy(editorInput, false);
    }

    private static IEditorInput getEditorInput(ISourceEntity element) {
        while (element != null) {
            if (element instanceof ICompilationUnit) {
                ICompilationUnit unit= (ICompilationUnit) element;
                IFile file= unit.getFile();

                return new FileEditorInput(file);
            }
//            if (element instanceof IClassFile)
//                return new InternalClassFileEditorInput((IClassFile) element);
            element= element.getParent();
        }
        return null;
    }

    public static IEditorInput getEditorInput(Object input) {
        if (input instanceof ISourceEntity)
            return getEditorInput((ISourceEntity) input);
        if (input instanceof IFile)
            return new FileEditorInput((IFile) input);
        if (input instanceof IPath) {
            IPath path= (IPath) input;
            IWorkspace ws= ResourcesPlugin.getWorkspace();
            IWorkspaceRoot wsRoot= ws.getRoot();

            // Only create an IFileStore directly from the path if the path is outside the workspace,
            // or points inside the workspace, but is still file-system-absolute.
            if (path.isAbsolute() && (wsRoot.getLocation().isPrefixOf(path) || !wsRoot.exists(path))) {
                try {
                    IFileSystem fileSystem= EFS.getFileSystem("file");
                    IFileStore fileStore= fileSystem.getStore((IPath) input);
                    return getEditorInput(fileStore);
                } 
                catch (CoreException e) {
                    e.printStackTrace();
                }
            } else {
                return new FileEditorInput(wsRoot.getFile(path));
            }
        }
        return null;
    }

	/**
	 * Create the Editor Input appropriate for the given <code>IFileStore</code>.
	 * The result is a normal file editor input if the file exists in the
	 * workspace and, if not, we create a wrapper capable of managing an
	 * 'external' file using its <code>IFileStore</code>.
	 * 
	 * @param fileStore
	 *            The file store to provide the editor input for
	 * @return The editor input associated with the given file store
	 */
	public static IEditorInput getEditorInput(IFileStore fileStore) {
		IFile workspaceFile = getWorkspaceFile(fileStore);
		if (workspaceFile != null)
			return new FileEditorInput(workspaceFile);
		return new FileStoreEditorInput(fileStore);
	}

	/**
	 * Determine whether or not the <code>IFileStore</code> represents a file
	 * currently in the workspace.
	 * 
	 * @param fileStore
	 *            The <code>IFileStore</code> to test
	 * @return The workspace's <code>IFile</code> if it exists or
	 *         <code>null</code> if not
	 */
	public static IFile getWorkspaceFile(IFileStore fileStore) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile[] files = root.findFilesForLocationURI(fileStore.toURI());
		files = filterNonExistentFiles(files);
		if (files == null || files.length == 0)
			return null;

		// for now only return the first file
		return files[0];
	}

	/**
	 * Filter the incoming array of <code>IFile</code> elements by removing
	 * any that do not currently exist in the workspace.
	 * 
	 * @param files
	 *            The array of <code>IFile</code> elements
	 * @return The filtered array
	 */
	private static IFile[] filterNonExistentFiles(IFile[] files) {
		if (files == null)
			return null;

		int length = files.length;
		ArrayList<IFile> existentFiles = new ArrayList<IFile>(length);
		for (int i = 0; i < length; i++) {
			if (files[i].exists())
				existentFiles.add(files[i]);
		}
		return (IFile[]) existentFiles.toArray(new IFile[existentFiles.size()]);
	}

    public static ISourceEntity getEditorInputModelElement(IEditorInput editorInput) {
        return (ISourceEntity) editorInput.getAdapter(ISourceEntity.class);
    }

    /**
     * If the current active editor edits a java element return it, else return null
     */
    public static ISourceEntity getActiveEditorModelInput() {
        IWorkbenchPage page= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            IEditorPart part= page.getActiveEditor();
            if (part != null) {
                IEditorInput editorInput= part.getEditorInput();
                if (editorInput != null) {
                    return getEditorInputModelElement(editorInput);
                }
            }
        }
        return null;
    }

    /**
     * Maps the localized modifier name to a code in the same manner as #findModifier.
     * 
     * @param modifierName
     *            the modifier name
     * @return the SWT modifier bit, or <code>0</code> if no match was found
     * @since 2.1.1
     */
    public static int findLocalizedModifier(String modifierName) {
        if (modifierName == null)
            return 0;
        if (modifierName.equalsIgnoreCase(Action.findModifierString(SWT.CTRL)))
            return SWT.CTRL;
        if (modifierName.equalsIgnoreCase(Action.findModifierString(SWT.SHIFT)))
            return SWT.SHIFT;
        if (modifierName.equalsIgnoreCase(Action.findModifierString(SWT.ALT)))
            return SWT.ALT;
        if (modifierName.equalsIgnoreCase(Action.findModifierString(SWT.COMMAND)))
            return SWT.COMMAND;
        return 0;
    }

//    /**
//     * Returns the modifier string for the given SWT modifier modifier bits.
//     * 
//     * @param stateMask
//     *            the SWT modifier bits
//     * @return the modifier string
//     * @since 2.1.1
//     */
//    public static String getModifierString(int stateMask) {
//        String modifierString= ""; //$NON-NLS-1$
//        if ((stateMask & SWT.CTRL) == SWT.CTRL)
//            modifierString= appendModifierString(modifierString, SWT.CTRL);
//        if ((stateMask & SWT.ALT) == SWT.ALT)
//            modifierString= appendModifierString(modifierString, SWT.ALT);
//        if ((stateMask & SWT.SHIFT) == SWT.SHIFT)
//            modifierString= appendModifierString(modifierString, SWT.SHIFT);
//        if ((stateMask & SWT.COMMAND) == SWT.COMMAND)
//            modifierString= appendModifierString(modifierString, SWT.COMMAND);
//        return modifierString;
//    }
//
//    /**
//     * Appends to modifier string of the given SWT modifier bit to the given modifierString.
//     * 
//     * @param modifierString
//     *            the modifier string
//     * @param modifier
//     *            an int with SWT modifier bit
//     * @return the concatenated modifier string
//     * @since 2.1.1
//     */
//    private static String appendModifierString(String modifierString, int modifier) {
//        if (modifierString == null)
//            modifierString= ""; //$NON-NLS-1$
//        String newModifierString= Action.findModifierString(modifier);
//        if (modifierString.length() == 0)
//            return newModifierString;
//        return IMPMessages.format(IMPMessages.EditorUtility_concatModifierStrings, new String[] { modifierString, newModifierString });
//    }

    /**
     * Returns the source project for a given editor input or <code>null</code> if no corresponding project exists.
     * 
     * @param input
     *            the editor input
     * @return the corresponding source project
     */
    public static ISourceProject getSourceProject(IEditorInput input) {
        ISourceProject srcProject= null;
        if (input instanceof IFileEditorInput) {
            IProject project= ((IFileEditorInput) input).getFile().getProject();
            if (project != null) {
                try {
                    srcProject= ModelFactory.open(project);
                } 
                catch (ModelException e) {
                    e.printStackTrace();
                }
            }
//        } else if (input instanceof IClassFileEditorInput) {
//            srcProject= ((IClassFileEditorInput) input).getClassFile().getJavaProject();
        }
        return srcProject;
    }

    /**
     * Returns an array of all editors that have an unsaved content. 
     * If the identical content is presented in more than one editor, 
     * only one of those editor parts is part of the result.
     * 
     * @return an array of all dirty editor parts.
     */
    public static IEditorPart[] getDirtyEditors() {
        Set<IEditorInput> inputs= new HashSet<IEditorInput>();
        List<IEditorPart> result= new ArrayList<IEditorPart>(0);
        IWorkbench workbench= PlatformUI.getWorkbench();
        IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();

        for(int i= 0; i < windows.length; i++) {
            IWorkbenchPage[] pages= windows[i].getPages();

            for(int x= 0; x < pages.length; x++) {
                IEditorPart[] editors= pages[x].getDirtyEditors();

                for(int z= 0; z < editors.length; z++) {
                    IEditorPart ep= editors[z];
                    IEditorInput input= ep.getEditorInput();

                    if (!inputs.contains(input)) {
                        inputs.add(input);
                        result.add(ep);
                    }
                }
            }
        }
        return result.toArray(new IEditorPart[result.size()]);
    }
    
	public static IDocument getDocument(Object input) {
		IEditorInput ei = getEditorInput(input);

		try {
			if (ei != null) {
				IDocumentProvider docProvider = DocumentProviderRegistry
						.getDefault().getDocumentProvider(ei);
				docProvider.connect(ei);
				return docProvider.getDocument(ei);
			}
		} catch (CoreException e) {
			// fall through
		}

		return null;
	}
}
