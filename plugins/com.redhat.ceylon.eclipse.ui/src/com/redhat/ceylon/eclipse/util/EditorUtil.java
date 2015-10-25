package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getRootFolderType;
import static com.redhat.ceylon.eclipse.core.vfs.vfsJ2C.instanceOfIFileVirtualFile;
import static org.eclipse.jdt.core.JavaCore.isJavaLikeFileName;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyLookupFactory;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.IUndoManager;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.themes.ITheme;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.code.editor.SourceArchiveEditorInput;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.RootFolderType;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.external.CeylonArchiveFileStore;
import com.redhat.ceylon.eclipse.core.external.CeylonArchiveFileSystem;
import com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.IJavaModelAware;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class EditorUtil {
    
    public static IProject getProject(IEditorInput input) {
        if (input instanceof IFileEditorInput) {
            return ((IFileEditorInput) input).getFile().getProject();
        }
        else {
            return null;
        }
    }
    
    public static IProject getProject(IEditorPart editor) {
        IProject project = getProject(editor.getEditorInput());
        /*if (project==null && editor instanceof CeylonEditor) {
            TypeChecker tc = ((CeylonEditor) editor).getParseController().getTypeChecker();
            for (IProject p: CeylonBuilder.getProjects()) {
                TypeChecker ptc = CeylonBuilder.getProjectTypeChecker(p);
                if (ptc==tc) {
                    return p;
                }
            }
        }*/
        return project;
    }

    public static IFile getFile(IEditorInput input) {
        if (input instanceof IFileEditorInput) {
            IFileEditorInput fei = (IFileEditorInput) input;
            return fei.getFile();
        }
        else {
            return null;
        }
    }

    public static IFile getFile(IEditorPart editor) {
        if (editor!=null) {
            IEditorInput editorInput = editor.getEditorInput();
            if (editorInput instanceof FileEditorInput) {
                FileEditorInput input = 
                        (FileEditorInput) editorInput;
                return input.getFile();
            }
        }
        return null;
    }
    
    public static ITextSelection getSelection(ITextEditor textEditor) {
        ISelectionProvider sp = textEditor.getSelectionProvider();
        return sp==null ? null : (ITextSelection) sp.getSelection();
    }
    
    public static String getSelectionText(ITextEditor textEditor) {
        ITextSelection sel = getSelection(textEditor);
        IDocument document = textEditor.getDocumentProvider()
                .getDocument(textEditor.getEditorInput());
        try {
            return document.get(sel.getOffset(), sel.getLength());
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static IEditorPart getCurrentEditor() {
        IWorkbenchPage page = getActivePage();
        return page==null ? null : 
            page.getActiveEditor();
    }

    public static IWorkbenchPage getActivePage() {
        try {
            IWorkbenchWindow window = 
                    getWorkbench()
                        .getActiveWorkbenchWindow();
            return window==null ? null : 
                window.getActivePage();
        }
        catch (IllegalStateException ise) {
            return null;
        }
    }
    
    public static ISearchResultPage getCurrentSearchResultPage() {
        IWorkbenchPage activePage = getActivePage();
        IWorkbenchPart part = 
                activePage==null ? null :
                    activePage.getActivePart();
        if (part instanceof SearchView) {
            SearchView searchView = (SearchView) part;
            return searchView.getActivePage();
        }
        else {
            return null;
        }
    }
    
    public static Shell getShell() {
        IWorkbenchWindow activeWindow = 
                getWorkbench()
                    .getActiveWorkbenchWindow();
        if (activeWindow != null) {
            return activeWindow.getShell();
        } else {
            return null;
        }
    }

    public static void performChange(IEditorPart activeEditor, 
            IDocument document, Change change, String name) 
                    throws CoreException {
        StyledText disabledStyledText= null;
        TraverseListener traverseBlocker= null;
        
        IRewriteTarget rewriteTarget= null;
        try {
            if (change != null) {
                if (document != null) {
                    LinkedModeModel.closeAllModels(document);
                }
                if (activeEditor != null) {
                    rewriteTarget= (IRewriteTarget) activeEditor.getAdapter(IRewriteTarget.class);
                    if (rewriteTarget != null) {
                        rewriteTarget.beginCompoundChange();
                    }
                    /*
                     * Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=195834#c7 :
                     * During change execution, an EventLoopProgressMonitor can process the event queue while the text
                     * widget has focus. When that happens and the user e.g. pressed a key, the event is prematurely
                     * delivered to the text widget and screws up the document. Change execution fails or performs
                     * wrong changes.
                     * 
                     * The fix is to temporarily disable the text widget.
                     */
                    Object control= activeEditor.getAdapter(Control.class);
                    if (control instanceof StyledText) {
                        disabledStyledText= (StyledText) control;
                        if (disabledStyledText.getEditable()) {
                            disabledStyledText.setEditable(false);
                            traverseBlocker= new TraverseListener() {
                                public void keyTraversed(TraverseEvent e) {
                                    e.doit= true;
                                    e.detail= SWT.TRAVERSE_NONE;
                                }
                            };
                            disabledStyledText.addTraverseListener(traverseBlocker);
                        } else {
                            disabledStyledText= null;
                        }
                    }
                }
    
                change.initializeValidationData(new NullProgressMonitor());
                RefactoringStatus valid= change.isValid(new NullProgressMonitor());
                if (valid.hasFatalError()) {
                    IStatus status= new Status(IStatus.ERROR, JavaPlugin.getPluginId(), IStatus.ERROR,
                        valid.getMessageMatchingSeverity(RefactoringStatus.FATAL), null);
                    throw new CoreException(status);
                } else {
                    IUndoManager manager= RefactoringCore.getUndoManager();
                    Change undoChange;
                    boolean successful= false;
                    try {
                        manager.aboutToPerformChange(change);
                        undoChange= change.perform(new NullProgressMonitor());
                        successful= true;
                    } finally {
                        manager.changePerformed(change, successful);
                    }
                    if (undoChange != null) {
                        undoChange.initializeValidationData(new NullProgressMonitor());
                        manager.addUndo(name, undoChange);
                    }
                }
            }
        } finally {
            if (disabledStyledText != null) {
                disabledStyledText.setEditable(true);
                disabledStyledText.removeTraverseListener(traverseBlocker);
            }
            if (rewriteTarget != null) {
                rewriteTarget.endCompoundChange();
            }
    
            if (change != null) {
                change.dispose();
            }
        }
    }

    /**
     * WARNING: only works in workbench window context!
     */
    public static TriggerSequence getCommandBinding(String actionName) {
        if (actionName==null) {
            return null;
        }
        else {
            IBindingService bindingService = 
                    (IBindingService) getWorkbench()
                        .getAdapter(IBindingService.class);
            if (bindingService == null) {
                return null;
            }
            else {
                return bindingService.getBestActiveBindingFor(actionName);
            }
        }
    }

    public static boolean triggersBinding(KeyEvent e, TriggerSequence commandBinding) {
        if (commandBinding==null) return false;
        char character = e.character;
        boolean ctrlDown = (e.stateMask & SWT.CTRL) != 0;
        if (ctrlDown && e.character != e.keyCode && e.character < 0x20
                && (e.keyCode & SWT.KEYCODE_BIT) == 0) {
            character += 0x40;
        }
        // do not process modifier keys
        if ((e.keyCode & (~SWT.MODIFIER_MASK)) == 0) {
            return false;
        }
        // if there is a character, use it. if no character available,
        // try with key code
        KeyStroke ks = KeyStroke.getInstance(e.stateMask,
                character != 0 ? Character.toUpperCase(character) : e.keyCode);
        return commandBinding.startsWith(KeySequence.getInstance(ks), true);
    }
    
    public static String getEnterBinding() {
        return KeyStroke.getInstance(KeyLookupFactory.getDefault().formalKeyLookup(IKeyLookup.CR_NAME)).format();
    }

    //  private static boolean isEditorInput(Object element, IEditorPart editor) {
    //  if (editor!=null) {
    //      return editor.getEditorInput().equals(getEditorInput(element));
    //  }
    //  return false;
    //}

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
        if (editorDescriptor!=null)
            return editorDescriptor.getId();
        return null;
    }

    public static IEditorInput getEditorInput(Object input) {
        if (input instanceof IFile) {
            return new FileEditorInput((IFile) input);
        }
        if (input instanceof IPath) {
            IPath path= (IPath) input;
            return getEditorInput(path);
        }

        if (input instanceof IJavaElement) {
            IClassFile classFile = null;
            if (input instanceof IClassFile) {
                classFile = (IClassFile) input;
            }
            if (input instanceof IMember) {
                classFile = ((IMember) input).getClassFile();
            }
            if (classFile != null) {
                IJavaModelAware unit = CeylonBuilder.getUnit(classFile);
                if (unit instanceof CeylonBinaryUnit) {                
                    CeylonBinaryUnit ceylonUnit = (CeylonBinaryUnit) unit;
                    if (! isJavaLikeFileName(ceylonUnit.getSourceRelativePath())) {
                        return getEditorInput(Path.fromOSString(ceylonUnit.getSourceFullPath()));
                    }
                }
            }

            return EditorUtility.getEditorInput((IJavaElement) input);
        }

        if (JavaModelUtil.isOpenableStorage(input)) {
            if (input instanceof JarEntryFile) {
                JarEntryFile entry = (JarEntryFile) input;
                JarPackageFragmentRoot root = (JarPackageFragmentRoot) entry.getPackageFragmentRoot();
                try {
                    IPath archiveFullPath = Path.fromOSString(root.getJar().getName());
                    IPath entryRelativePath = entry.getFullPath();
                    if (archiveFullPath.getFileExtension().equalsIgnoreCase("SRC") && 
                            entryRelativePath.getFileExtension().equalsIgnoreCase("ceylon")) {
                        IPath finalPath = Path.fromOSString(archiveFullPath.toOSString() + "!").append(entryRelativePath);
                        return getEditorInput(finalPath);
                    }
                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            return new JarEntryEditorInput((IStorage)input);
        }

        if (input instanceof CeylonArchiveFileStore) {
            return getEditorInput(((CeylonArchiveFileStore) input).getFullPath());
        }

        if (input instanceof IFileStore) {
            return getEditorInput((IFileStore)input);
        }
        return null;
    }

    public static IEditorInput getEditorInput(IPath path) {
        IWorkspace ws= ResourcesPlugin.getWorkspace();
        IWorkspaceRoot wsRoot= ws.getRoot();

        IResource sourceArchiveResource = ExternalSourceArchiveManager.toResource(path);
        if (sourceArchiveResource instanceof IFile) {
            return new SourceArchiveEditorInput((IFile)sourceArchiveResource);
        }

        // Only create an IFileStore directly from the path if the path is outside the workspace,
        // or points inside the workspace, but is still file-system-absolute.
        if (path.isAbsolute() && (wsRoot.getLocation().isPrefixOf(path) || !wsRoot.exists(path))) {
            try {
                IFileSystem fileSystem= EFS.getFileSystem("file");
                IFileStore fileStore= fileSystem.getStore(path);
                return getEditorInput(fileStore);
            } 
            catch (CoreException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return new FileEditorInput(wsRoot.getFile(path));
        }
    }

    public static IEditorInput getEditorInput(Unit unit) {
        if (unit == null) {
            return null;
        }
        if (unit instanceof IResourceAware) {
            IResourceAware ra = (IResourceAware) unit;
            IFile file = ra.getResourceFile();
            if (file != null) {
                return getEditorInput(file);
            }
        }
        
        return getEditorInput(Navigation.getUnitPath(unit));
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
        if (workspaceFile!=null)
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
    private static IFile getWorkspaceFile(IFileStore fileStore) {
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

    /**
     * Maps the localized modifier name to a code in the same manner as #findModifier.
     * 
     * @param modifierName
     *            the modifier name
     * @return the SWT modifier bit, or <code>0</code> if no match was found
     * @since 2.1.1
     */
    //private static int findLocalizedModifier(String modifierName) {
    //  if (modifierName == null)
    //      return 0;
    //  if (modifierName.equalsIgnoreCase(Action.findModifierString(SWT.CTRL)))
    //      return SWT.CTRL;
    //  if (modifierName.equalsIgnoreCase(Action.findModifierString(SWT.SHIFT)))
    //      return SWT.SHIFT;
    //  if (modifierName.equalsIgnoreCase(Action.findModifierString(SWT.ALT)))
    //      return SWT.ALT;
    //  if (modifierName.equalsIgnoreCase(Action.findModifierString(SWT.COMMAND)))
    //      return SWT.COMMAND;
    //  return 0;
    //}

    ///**
    //* Returns the modifier string for the given SWT modifier modifier bits.
    //* 
    //* @param stateMask
    //*            the SWT modifier bits
    //* @return the modifier string
    //* @since 2.1.1
    //*/
    //public static String getModifierString(int stateMask) {
    //  String modifierString= ""; //$NON-NLS-1$
    //  if ((stateMask & SWT.CTRL) == SWT.CTRL)
    //      modifierString= appendModifierString(modifierString, SWT.CTRL);
    //  if ((stateMask & SWT.ALT) == SWT.ALT)
    //      modifierString= appendModifierString(modifierString, SWT.ALT);
    //  if ((stateMask & SWT.SHIFT) == SWT.SHIFT)
    //      modifierString= appendModifierString(modifierString, SWT.SHIFT);
    //  if ((stateMask & SWT.COMMAND) == SWT.COMMAND)
    //      modifierString= appendModifierString(modifierString, SWT.COMMAND);
    //  return modifierString;
    //}
    //
    ///**
    //* Appends to modifier string of the given SWT modifier bit to the given modifierString.
    //* 
    //* @param modifierString
    //*            the modifier string
    //* @param modifier
    //*            an int with SWT modifier bit
    //* @return the concatenated modifier string
    //* @since 2.1.1
    //*/
    //private static String appendModifierString(String modifierString, int modifier) {
    //  if (modifierString == null)
    //      modifierString= ""; //$NON-NLS-1$
    //  String newModifierString= Action.findModifierString(modifier);
    //  if (modifierString.length() == 0)
    //      return newModifierString;
    //  return IMPMessages.format(IMPMessages.EditorUtility_concatModifierStrings, new String[] { modifierString, newModifierString });
    //}

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

    public static IDocument getDocument(IEditorInput ei) {
        try {
            if (ei!=null) {
                IDocumentProvider docProvider = 
                        DocumentProviderRegistry.getDefault()
                            .getDocumentProvider(ei);
                docProvider.connect(ei);
                return docProvider.getDocument(ei);
            }
        }
        catch (CoreException e) {
            // fall through
        }
        return null;
    }

    public static IDocument getDocument(TextChange change) {
        try {
            return change.getCurrentDocument(null);
        }
        catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean performChange(TextChange textFileChange) {
        try {
            textFileChange.perform(new NullProgressMonitor());
            return true;
        }
        catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ITheme getCurrentTheme() {
        return PlatformUI.getWorkbench()
                .getThemeManager()
                .getCurrentTheme();
    }

    public static SourceArchiveEditorInput fixSourceArchiveInput(
            FileStoreEditorInput input) {
        URI uri = input.getURI();
        System.out.println("FileStoreEditorInput URI : " + uri);
        if (uri != null) {
            String path = uri.getPath();
            if (path.contains(CeylonArchiveFileSystem.JAR_SUFFIX)) {
                IPath fullPath = new Path(path);
                System.out.println("FileStoreEditorInput full path : " + 
                        fullPath);
                IEditorInput newInput = 
                        getEditorInput(fullPath);
                System.out.println("Changed EditorInput : " + 
                        newInput + " / " + newInput.getToolTipText());
                if (newInput instanceof SourceArchiveEditorInput) {
                    return (SourceArchiveEditorInput) newInput;
                }
            }
        }
        return null;
    }

    public static IEditorInput adjustEditorInput(IEditorInput input) {
        if (input instanceof FileStoreEditorInput) {
            FileStoreEditorInput fsei = 
                    (FileStoreEditorInput) input;
            IEditorInput fixedInput = 
                    fixSourceArchiveInput(fsei);
            if (fixedInput != null) {
                input = fixedInput;
            }
        }
    
        if (input instanceof IFileEditorInput) {
            boolean replacedByTheSourceFile = false;
            IFileEditorInput fei = (IFileEditorInput) input;
            IFile file = fei.getFile();
            if (file != null) {
                if (!CeylonNature.isEnabled(file.getProject()) ||
                        getRootFolderType(file) 
                                != RootFolderType.SOURCE) {
                    // search if those files are also in the source directory of
                    // a Ceylon project existing in this project
                    IWorkspaceRoot root = 
                            file.getWorkspace().getRoot();
                    if (input instanceof SourceArchiveEditorInput) {
                        IPath fileFullPath = 
                                ExternalSourceArchiveManager.toFullPath(file);
                        IPath relativePath = ExternalSourceArchiveManager.getSourceArchiveEntryPath(file);
    
                        if (fileFullPath!=null && relativePath!=null) {
                            for (IProject project: root.getProjects()) {
                                if (project.isAccessible() && 
                                        CeylonNature.isEnabled(project)) {
                                    IPath projectModuleDirFullPath = 
                                            getCeylonModulesOutputFolder(project)
                                                .getLocation();
                                    if (projectModuleDirFullPath!=null &&
                                            projectModuleDirFullPath.isPrefixOf(fileFullPath)) {
                                        TypeChecker typeChecker = 
                                                getProjectTypeChecker(project);
                                        if (typeChecker != null) {
                                            PhasedUnits sourcePhasedUnits = 
                                                    typeChecker.getPhasedUnits();
                                            PhasedUnit unit = 
                                                    sourcePhasedUnits.getPhasedUnitFromRelativePath(
                                                            relativePath.toString());
                                            if (unit instanceof ProjectPhasedUnit) {
                                                if (instanceOfIFileVirtualFile(unit.getUnitFile())) {
                                                    ProjectPhasedUnit ppu = 
                                                            (ProjectPhasedUnit) unit;
                                                    IFile newFile = ppu.getResourceFile();
                                                    if (newFile.exists() &&
                                                            getRootFolderType(newFile) 
                                                                    == RootFolderType.SOURCE) {
                                                        file = newFile;
                                                        input = getEditorInput(newFile);
                                                        replacedByTheSourceFile = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        IPath location = file.getLocation();
                        if (location != null) {
                            for (IProject project: root.getProjects()) {
                                IPath projectLocation = 
                                        project.getLocation();
                                if (project.isAccessible() && 
                                        projectLocation != null && 
                                        CeylonNature.isEnabled(project) && 
                                        projectLocation.isPrefixOf(location)) {
                                    IPath relative = 
                                            location.makeRelativeTo(
                                                    projectLocation);
                                    IFile newFile = 
                                            project.getFile(relative);
                                    if (newFile.exists() && 
                                            getRootFolderType(newFile) 
                                                == RootFolderType.SOURCE) {
                                        file = newFile;
                                        input = getEditorInput(newFile);
                                        replacedByTheSourceFile = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (!replacedByTheSourceFile &&
                        !(input instanceof SourceArchiveEditorInput)) {
                    if (ExternalSourceArchiveManager.isInSourceArchive(file)) {
                        IPath fullPath = 
                                ExternalSourceArchiveManager.toFullPath(file);
                        if (fullPath != null) {
                            input = getEditorInput(fullPath);
                        }
                        else {
                            fullPath = file.getFullPath();
                            if (fullPath.segmentCount() > 1) {
                                fullPath = fullPath.removeFirstSegments(1);
                                fullPath = fullPath.makeAbsolute();
                            }
                            input = getEditorInput(fullPath);
                        }
                    }
                }
            }
        }
        return input;
    }
}
