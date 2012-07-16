package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.FOLDING_COLLAPSE_COMMENTS;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.FOLDING_COLLAPSE_MEMBERS;
import static org.eclipse.imp.actions.FoldingMessages.getResourceBundle;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_COLLAPSE;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_COLLAPSE_ALL;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_EXPAND;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_EXPAND_ALL;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_RESTORE;

import java.util.ResourceBundle;

import org.eclipse.imp.actions.FoldingMessages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.editors.text.IFoldingCommandIds;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.ResourceAction;
import org.eclipse.ui.texteditor.TextOperationAction;

public class FoldingActionGroup extends ActionGroup {
    private static abstract class PreferenceAction extends ResourceAction implements IUpdate {
        PreferenceAction(ResourceBundle bundle, String prefix, int style) {
            super(bundle, prefix, style);
        }
    }
    
    /**
     * @since 3.2
     */
    private class FoldingAction extends PreferenceAction {

        FoldingAction(ResourceBundle bundle, String prefix) {
            super(bundle, prefix, IAction.AS_PUSH_BUTTON);
        }

        public void update() {
            setEnabled(FoldingActionGroup.this.isEnabled() && fViewer.isProjectionMode());
        }
        
    }
    
    private ProjectionViewer fViewer;
    
//  private final PreferenceAction fToggle;
    private final TextOperationAction fExpand;
    private final TextOperationAction fCollapse;
    private final TextOperationAction fExpandAll;
    private final IProjectionListener fProjectionListener;
    
    /* since 3.2 */
    private final PreferenceAction fRestoreDefaults;
    private final FoldingAction fCollapseMembers;
    private final FoldingAction fCollapseComments;
    private final TextOperationAction fCollapseAll;


    /**
     * Creates a new projection action group for <code>editor</code>. If the
     * supplied viewer is not an instance of <code>ProjectionViewer</code>, the
     * action group is disabled.
     * 
     * @param editor the text editor to operate on
     * @param viewer the viewer of the editor
     */
    public FoldingActionGroup(final ITextEditor editor, ITextViewer viewer) {
        if (!(viewer instanceof ProjectionViewer)) {
//          fToggle= null;
            fExpand= null;
            fCollapse= null;
            fExpandAll= null;
            fCollapseAll= null;
            fRestoreDefaults= null;
            fCollapseMembers= null;
            fCollapseComments= null;
            fProjectionListener= null;
            return;
        }
        
        fViewer= (ProjectionViewer) viewer;
        
        fProjectionListener= new IProjectionListener() {

            public void projectionEnabled() {
                update();
            }

            public void projectionDisabled() {
                update();
            }
        };
        
        fViewer.addProjectionListener(fProjectionListener);
        
//        fToggle= new PreferenceAction(FoldingMessages.getResourceBundle(), "Projection.Toggle.", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
//            public void run() {
//                IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
//                boolean current= store.getBoolean(PreferenceConstants.EDITOR_FOLDING_ENABLED);
//                store.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, !current);
//            }
//
//            public void update() {
//                ITextOperationTarget target= (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
//                    
//                boolean isEnabled= (target != null && target.canDoOperation(ProjectionViewer.TOGGLE));
//                setEnabled(isEnabled);
//            }
//        };
//        fToggle.setChecked(true);
//        fToggle.setActionDefinitionId(IFoldingCommandIds.FOLDING_TOGGLE);
//        editor.setAction("FoldingToggle", fToggle); //$NON-NLS-1$
        
        fExpandAll= new TextOperationAction(getResourceBundle(), "Projection.ExpandAll.", editor, ProjectionViewer.EXPAND_ALL, true); //$NON-NLS-1$
        fExpandAll.setActionDefinitionId(FOLDING_EXPAND_ALL);
        editor.setAction("FoldingExpandAll", fExpandAll); //$NON-NLS-1$
        
        fCollapseAll= new TextOperationAction(getResourceBundle(), "Projection.CollapseAll.", editor, ProjectionViewer.COLLAPSE_ALL, true); //$NON-NLS-1$
        fCollapseAll.setActionDefinitionId(FOLDING_COLLAPSE_ALL);
        editor.setAction("FoldingCollapseAll", fCollapseAll); //$NON-NLS-1$
        
        fExpand= new TextOperationAction(getResourceBundle(), "Projection.Expand.", editor, ProjectionViewer.EXPAND, true); //$NON-NLS-1$
        fExpand.setActionDefinitionId(FOLDING_EXPAND);
        editor.setAction("FoldingExpand", fExpand); //$NON-NLS-1$
        
        fCollapse= new TextOperationAction(getResourceBundle(), "Projection.Collapse.", editor, ProjectionViewer.COLLAPSE, true); //$NON-NLS-1$
        fCollapse.setActionDefinitionId(FOLDING_COLLAPSE);
        editor.setAction("FoldingCollapse", fCollapse); //$NON-NLS-1$
        
        fRestoreDefaults= new FoldingAction(getResourceBundle(), "Projection.Restore.") { //$NON-NLS-1$
            public void run() {
                if (editor instanceof CeylonEditor) {
                	CeylonEditor univEditor= (CeylonEditor) editor;
//                  javaEditor.resetProjection();
                }
            }
        };
        fRestoreDefaults.setActionDefinitionId(FOLDING_RESTORE);
        editor.setAction("FoldingRestore", fRestoreDefaults); //$NON-NLS-1$
        
        fCollapseMembers= new FoldingAction(getResourceBundle(), "Projection.CollapseMembers.") { //$NON-NLS-1$
            public void run() {
                if (editor instanceof CeylonEditor) {
                	CeylonEditor univEditor= (CeylonEditor) editor;
                    // TODO Need more API on UniversalEditor in order to enable the following
//                  univEditor.collapseMembers();
                }
            }
        };
        fCollapseMembers.setActionDefinitionId(FOLDING_COLLAPSE_MEMBERS);
        editor.setAction("FoldingCollapseMembers", fCollapseMembers); //$NON-NLS-1$
        
        fCollapseComments= new FoldingAction(getResourceBundle(), "Projection.CollapseComments.") { //$NON-NLS-1$
            public void run() {
                if (editor instanceof CeylonEditor) {
                	CeylonEditor univEditor= (CeylonEditor) editor;
                    // TODO Need more API on UniversalEditor in order to enable the following
//                  javaEditor.collapseComments();
                }
            }
        };
        fCollapseComments.setActionDefinitionId(FOLDING_COLLAPSE_COMMENTS);
        editor.setAction("FoldingCollapseComments", fCollapseComments); //$NON-NLS-1$
    }
    
    /**
     * Returns <code>true</code> if the group is enabled. 
     * <pre>
     * Invariant: isEnabled() <=> fViewer and all actions are != null.
     * </pre>
     * 
     * @return <code>true</code> if the group is enabled
     */
    protected boolean isEnabled() {
        return fViewer != null;
    }
    
    /*
     * @see org.eclipse.ui.actions.ActionGroup#dispose()
     */
    public void dispose() {
        if (isEnabled()) {
            fViewer.removeProjectionListener(fProjectionListener);
            fViewer= null;
        }
        super.dispose();
    }
    
    /**
     * Updates the actions.
     */
    protected void update() {
        if (isEnabled()) {
//          fToggle.update();
//          fToggle.setChecked(fViewer.isProjectionMode());
            fExpand.update();
            fExpandAll.update();
            fCollapse.update();
            fCollapseAll.update();
            fRestoreDefaults.update();
            fCollapseMembers.update();
            fCollapseComments.update();
        }
    }
    
    /**
     * Fills the menu with all folding actions.
     * 
     * @param manager the menu manager for the folding submenu
     */
    public void fillMenu(IMenuManager manager) {
        if (isEnabled()) {
            update();
//          manager.add(fToggle);
            manager.add(fExpandAll);
            manager.add(fExpand);
            manager.add(fCollapse);
            manager.add(fCollapseAll);
            manager.add(fRestoreDefaults);
            manager.add(fCollapseMembers);
            manager.add(fCollapseComments);
        }
    }
    
    /*
     * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
     */
    public void updateActionBars() {
        update();
    }
}
