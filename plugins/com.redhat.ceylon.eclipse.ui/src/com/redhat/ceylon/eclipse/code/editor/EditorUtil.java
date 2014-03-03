package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.IUndoManager;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

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
            return ((IFileEditorInput) input).getFile();
        }
        else {
            return null;
        }
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

    public static void gotoLocation(final IResource file, final int offset) {
        gotoLocation(file, offset, 0);
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
            IDE.openEditor(getActivePage(), marker);
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
    
    public static IEditorPart getCurrentEditor() {
        IWorkbenchPage page = getActivePage();
        return page==null ? null : page.getActiveEditor();
    }

    public static IWorkbenchPage getActivePage() {
        try {
            IWorkbenchWindow window = getWorkbench()
                    .getActiveWorkbenchWindow();
            return window==null ? 
                    null : window.getActivePage();
        }
        catch (IllegalStateException ise) {
            return null;
        }
    }
    
    public static Shell getShell() {
        return getWorkbench()
                .getActiveWorkbenchWindow()
                .getShell();
    }

    public static IPreferenceStore getPreferences() {
        try {
            return EditorsUI.getPreferenceStore();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static ITextSelection getSelectionFromThread(final CeylonEditor editor) {
        final class GetSelection implements Runnable {
            ITextSelection selection;
            @Override
            public void run() {
                ISelectionProvider sp = editor.getSelectionProvider();
                selection = sp==null ? 
                        null : (ITextSelection) sp.getSelection();
            }
            ITextSelection getSelection() {
                Display.getDefault().syncExec(this);
                return selection;
            }
        }
        return new GetSelection().getSelection();
    }
    
    public static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor==null ? 
                null : editor.getParseController();
        if (cpc==null || cpc.getRootNode()==null) {
            return null;
        }
        ITextSelection selection = (ITextSelection) 
                editor.getSelectionProvider().getSelection();
        return findNode(cpc.getRootNode(), selection);
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

    public static void installLinkedMode(final CeylonEditor editor, 
            IDocument document, LinkedModeModel linkedModeModel, 
            Object linkedModeOwner,
            int exitSequenceNumber, int exitPosition)
                    throws BadLocationException {
        final IEditingSupport editingSupport = new FocusEditingSupport(editor);
        installLinkedMode(editor, linkedModeModel, linkedModeOwner,
                exitSequenceNumber, exitPosition, editingSupport,
                new DeleteBlockingExitPolicy(document), 
                new AbstractLinkedModeListener(editor, 
                        linkedModeOwner) {
                    @Override
                    public void left(LinkedModeModel model, int flags) {
                        editor.clearLinkedMode();
                        //linkedModeModel.exit(ILinkedModeListener.NONE);
                        unregisterEditingSupport(editor, editingSupport);
                        editor.getSite().getPage().activate(editor);
                        if ((flags&EXTERNAL_MODIFICATION)==0) {
                            CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
                            if (viewer!=null) {
                                viewer.invalidateTextPresentation();
                            }
                        }
                    }
                });
    }

    public static void installLinkedMode(final CeylonEditor editor,
            LinkedModeModel linkedModeModel, Object linkedModeOwner,
            int exitSequenceNumber, int exitPosition,
            IEditingSupport editingSupport, IExitPolicy exitPolicy,
            AbstractLinkedModeListener linkedModelListener) 
                    throws BadLocationException {
        linkedModeModel.forceInstall();
        linkedModeModel.addLinkingListener(linkedModelListener);
        registerEditingSupport(editor, editingSupport);
        editor.setLinkedMode(linkedModeModel, linkedModeOwner);
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        EditorLinkedModeUI ui = new EditorLinkedModeUI(linkedModeModel, viewer);
        ui.setExitPosition(viewer, exitPosition, 0, exitSequenceNumber);
        ui.setExitPolicy(exitPolicy);
        ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
        ui.setDoContextInfo(true);
        ui.enter();
    }

    public static void unregisterEditingSupport(CeylonEditor editor,
            IEditingSupport editingSupport) {
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        if (viewer!=null) {
            ((IEditingSupportRegistry) viewer).unregister(editingSupport);
        }
    }
    
    public static void registerEditingSupport(CeylonEditor editor,
            IEditingSupport editingSupport) {
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        if (viewer!=null) {
            ((IEditingSupportRegistry) viewer).register(editingSupport);
        }
    }
    
    public static void addLinkedPosition(final LinkedModeModel linkedModeModel,
            ProposalPosition linkedPosition) 
                    throws BadLocationException {
        LinkedPositionGroup linkedPositionGroup = new LinkedPositionGroup();
        linkedPositionGroup.addPosition(linkedPosition);
        linkedModeModel.addGroup(linkedPositionGroup);
    }

}
