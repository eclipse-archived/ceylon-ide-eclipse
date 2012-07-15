package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

/**
 * This interface houses action definition IDs, which match command IDs, so that they
 * can be bound to keystrokes in the UI.
 * @author rfuhrer@watson.ibm.com
 */
public interface IEditorActionDefinitionIds {
    /**
     * Action definition ID of the Edit -> Correct Indentation action
     * (value <code>"org.eclipse.imp.runtime.editor.correctIndentation"</code>).
     */
    public static final String CORRECT_INDENTATION= PLUGIN_ID + ".editor.correctIndentation"; //$NON-NLS-1$

    /**
     * Action definition id of the collapse members action
     * (value: <code>"org.eclipse.imp.runtime.editor.folding.collapseMembers"</code>).
     * @since 0.1
     */
    public static final String FOLDING_COLLAPSE_MEMBERS= PLUGIN_ID + ".editor.folding.collapseMembers"; //$NON-NLS-1$

    /**
     * Action definition id of the collapse comments action
     * (value: <code>"org.eclipse.imp.runtime.editor.folding.collapseComments"</code>).
     * @since 0.1
     */
    public static final String FOLDING_COLLAPSE_COMMENTS= PLUGIN_ID + ".editor.folding.collapseComments"; //$NON-NLS-1$

    /**
     * Source menu: id of standard Format global action
     * (value <code>"org.eclipse.imp.runtime.editor.formatSource"</code>).
     */
    public static final String FORMAT= PLUGIN_ID + ".editor.formatSource"; //$NON-NLS-1$

    /**
     * Action definition ID of the edit -> Go to Matching Fence action
     * (value <code>"org.eclipse.imp.runtime.gotoMatchingFence"</code>).
     */
    public static final String GOTO_MATCHING_FENCE= PLUGIN_ID + ".editor.gotoMatchingFence"; //$NON-NLS-1$

    /**
     * Action definition ID of the edit -> Go to Previous Navigation Target action
     * (value <code>"org.eclipse.imp.runtime.editor.gotoPreviousTarget"</code>).
     */
    public static final String GOTO_PREVIOUS_TARGET= PLUGIN_ID + ".editor.gotoPreviousTarget"; //$NON-NLS-1$

    /**
     * Action definition ID of the edit -> Go to Next Navigation Target action
     * (value <code>"org.eclipse.imp.runtime.editor.gotoNextTarget"</code>).
     */
    public static final String GOTO_NEXT_TARGET= PLUGIN_ID + ".editor.gotoNextTarget"; //$NON-NLS-1$

    /**
     * Action definition ID of the Edit -> Open Declaration action
     * (value <code>"org.eclipse.imp.runtime.editor.openDeclaration"</code>).
     */
    public static final String OPEN_EDITOR= PLUGIN_ID + ".editor.openDeclaration"; //$NON-NLS-1$

    /**
     * Action definition ID of the Edit -> Select Enclosing action
     * (value <code>"org.eclipse.imp.runtime.editor.selectEnclosing"</code>).
     */
    public static final String SELECT_ENCLOSING= PLUGIN_ID + ".editor.selectEnclosing";

    /**
     * Action definition ID of the Edit -> Shift Right action
     * (value <code>"org.eclipse.imp.runtime.editor.shiftRight"</code>).
     */
    public static final String SHIFT_RIGHT= PLUGIN_ID + ".editor.shiftRight"; //$NON-NLS-1$

    /**
     * Action definition ID of the Edit -> Shift Left action
     * (value <code>"org.eclipse.imp.runtime.editor.shiftLeft"</code>).
     */
    public static final String SHIFT_LEFT= PLUGIN_ID + ".editor.shiftLeft"; //$NON-NLS-1$

    /**
     * Action definition ID of the navigate -> Show Outline action
     * (value <code>"org.eclipse.imp.runtime.editor.showOutline"</code>).
     * 
     * @since 0.1
     */
    public static final String SHOW_OUTLINE= PLUGIN_ID + ".editor.showOutline"; //$NON-NLS-1$
    
    /**
     * Action definition ID of the Edit -> Toggle Comment action
     * (value <code>"org.eclipse.imp.runtime.editor.toggleComment"</code>).
     */
    public static final String TOGGLE_COMMENT= PLUGIN_ID + ".editor.toggleComment"; //$NON-NLS-1$
}

