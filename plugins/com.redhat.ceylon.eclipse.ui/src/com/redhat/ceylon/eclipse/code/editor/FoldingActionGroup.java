package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.FoldingMessages.getResourceBundle;
import static org.eclipse.jdt.ui.PreferenceConstants.EDITOR_FOLDING_ENABLED;
import static org.eclipse.jface.text.source.projection.ProjectionViewer.COLLAPSE;
import static org.eclipse.jface.text.source.projection.ProjectionViewer.COLLAPSE_ALL;
import static org.eclipse.jface.text.source.projection.ProjectionViewer.EXPAND;
import static org.eclipse.jface.text.source.projection.ProjectionViewer.EXPAND_ALL;
import static org.eclipse.jface.text.source.projection.ProjectionViewer.TOGGLE;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_COLLAPSE;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_COLLAPSE_ALL;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_EXPAND;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_EXPAND_ALL;
import static org.eclipse.ui.editors.text.IFoldingCommandIds.FOLDING_TOGGLE;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.ResourceAction;
import org.eclipse.ui.texteditor.TextOperationAction;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class FoldingActionGroup extends ActionGroup {
    
    private static abstract class PreferenceAction 
            extends ResourceAction implements IUpdate {
        PreferenceAction(ResourceBundle bundle, String prefix, int style) {
            super(bundle, prefix, style);
        }
    }
    
    private class FoldingAction extends PreferenceAction {
        FoldingAction(ResourceBundle bundle, String prefix) {
            super(bundle, prefix, IAction.AS_PUSH_BUTTON);
        }
        public void update() {
            setEnabled(FoldingActionGroup.this.isEnabled() && fViewer.isProjectionMode());
        }
    }
    
    private ProjectionViewer fViewer;
    
    private final PreferenceAction fToggle;
    private final TextOperationAction fExpand;
    private final TextOperationAction fCollapse;
    private final TextOperationAction fExpandAll;
    private final IProjectionListener fProjectionListener;    
    private final FoldingAction fCollapseComments;
    private final FoldingAction fCollapseImports;
    private final TextOperationAction fCollapseAll;
    //private final PreferenceAction fRestoreDefaults;
    //private final FoldingAction fCollapseMembers;


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
            fToggle= null;
            fExpand= null;
            fCollapse= null;
            fExpandAll= null;
            fCollapseAll= null;
            //fRestoreDefaults= null;
            //fCollapseMembers= null;
            fCollapseComments= null;
            fCollapseImports= null;
            fProjectionListener= null;
            return;
        }
        
        fViewer = (ProjectionViewer) viewer;
        
        fProjectionListener = new IProjectionListener() {
            public void projectionEnabled() {
                update();
            }
            public void projectionDisabled() {
                update();
            }
        };
        
        fViewer.addProjectionListener(fProjectionListener);
        
        final IPreferenceStore store= CeylonPlugin.getPreferences();
        fToggle= new PreferenceAction(FoldingMessages.getResourceBundle(), "Projection.Toggle.", IAction.AS_CHECK_BOX) {
            public void run() {
                store.setValue(EDITOR_FOLDING_ENABLED, !store.getBoolean(EDITOR_FOLDING_ENABLED));
            }
            public void update() {
                ITextOperationTarget target= (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
                setEnabled(target!=null && target.canDoOperation(TOGGLE));
            }
        };
        fToggle.setChecked(store.getBoolean(EDITOR_FOLDING_ENABLED));
        fToggle.setActionDefinitionId(FOLDING_TOGGLE);
        editor.setAction("FoldingToggle", fToggle);
        
        fExpandAll= new TextOperationAction(getResourceBundle(), "Projection.ExpandAll.", editor, EXPAND_ALL, true);
        fExpandAll.setActionDefinitionId(FOLDING_EXPAND_ALL);
        editor.setAction("FoldingExpandAll", fExpandAll);
        
        fCollapseAll= new TextOperationAction(getResourceBundle(), "Projection.CollapseAll.", editor, COLLAPSE_ALL, true);
        fCollapseAll.setActionDefinitionId(FOLDING_COLLAPSE_ALL);
        editor.setAction("FoldingCollapseAll", fCollapseAll);
        
        fExpand= new TextOperationAction(getResourceBundle(), "Projection.Expand.", editor, EXPAND, true);
        fExpand.setActionDefinitionId(FOLDING_EXPAND);
        editor.setAction("FoldingExpand", fExpand);
        
        fCollapse= new TextOperationAction(getResourceBundle(), "Projection.Collapse.", editor, COLLAPSE, true);
        fCollapse.setActionDefinitionId(FOLDING_COLLAPSE);
        editor.setAction("FoldingCollapse", fCollapse);
        
        /*fRestoreDefaults= new FoldingAction(getResourceBundle(), "Projection.Restore.") { //$NON-NLS-1$
            public void run() {
                if (editor instanceof CeylonEditor) {
                    CeylonEditor univEditor= (CeylonEditor) editor;
//                  javaEditor.resetProjection();
                }
            }
        };
        fRestoreDefaults.setActionDefinitionId(FOLDING_RESTORE);
        editor.setAction("FoldingRestore", fRestoreDefaults); //$NON-NLS-1$*/
                
        fCollapseComments= new FoldingAction(getResourceBundle(), "Projection.CollapseComments.") {
            public void run() {
                if (editor instanceof CeylonEditor) {
                    ProjectionAnnotationModel pam = ((CeylonEditor) editor).getCeylonSourceViewer()
                            .getProjectionAnnotationModel();
                    for (Iterator<Annotation> iter =
                                pam.getAnnotationIterator(); 
                            iter.hasNext();) {
                        Annotation pa = iter.next();
                        if (pa instanceof CeylonProjectionAnnotation) {
                            int tt = ((CeylonProjectionAnnotation) pa).getTokenType();
                            if (tt==CeylonLexer.MULTI_COMMENT || 
                                tt==CeylonLexer.LINE_COMMENT) {
                                pam.collapse(pa);
                            }
                        }
                    }
                }
            }
        };
        fCollapseComments.setActionDefinitionId(CeylonPlugin.PLUGIN_ID + ".editor.folding.collapseComments");
        editor.setAction("FoldingCollapseComments", fCollapseComments);
        
        fCollapseImports= new FoldingAction(getResourceBundle(), "Projection.CollapseImports.") {
            public void run() {
                if (editor instanceof CeylonEditor) {
                    ProjectionAnnotationModel pam = ((CeylonEditor) editor).getCeylonSourceViewer()
                            .getProjectionAnnotationModel();
                    for (Iterator<Annotation> iter =
                                pam.getAnnotationIterator(); 
                            iter.hasNext();) {
                        Annotation pa = iter.next();
                        if (pa instanceof CeylonProjectionAnnotation) {
                            int tt = ((CeylonProjectionAnnotation) pa).getTokenType();
                            if (tt==CeylonLexer.IMPORT) {
                                pam.collapse(pa);
                            }
                        }
                    }
                }
            }
        };
        fCollapseImports.setActionDefinitionId(CeylonPlugin.PLUGIN_ID + ".editor.folding.collapseImports");
        editor.setAction("FoldingCollapseImports", fCollapseImports);
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
            fToggle.update();
            fToggle.setChecked(fViewer.isProjectionMode());
            fExpand.update();
            fExpandAll.update();
            fCollapse.update();
            fCollapseAll.update();
            //fRestoreDefaults.update();
            //fCollapseMembers.update();
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
            manager.add(fToggle);
            manager.add(fExpandAll);
            manager.add(fExpand);
            manager.add(fCollapse);
            manager.add(fCollapseAll);
            //manager.add(fRestoreDefaults);
            //manager.add(fCollapseMembers);
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
