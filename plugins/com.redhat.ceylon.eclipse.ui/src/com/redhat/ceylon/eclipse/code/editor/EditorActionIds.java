package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

/**
 * This interface houses action definition IDs, which match command IDs, so that they
 * can be bound to keystrokes in the UI.
 * @author rfuhrer@watson.ibm.com
 */
public interface EditorActionIds {
    /**
     * Action definition ID of the Edit -> Correct Indentation action
     */
    public static final String CORRECT_INDENTATION= PLUGIN_ID + ".editor.correctIndentation"; 

    /**
     * Action definition id of the collapse members action
     */
    public static final String FOLDING_COLLAPSE_MEMBERS= PLUGIN_ID + ".editor.folding.collapseMembers"; 

    /**
     * Action definition id of the collapse comments action
     */
    public static final String FOLDING_COLLAPSE_COMMENTS= PLUGIN_ID + ".editor.folding.collapseComments"; 

    /**
     * Source menu: id of standard Format global action
     */
    public static final String FORMAT= PLUGIN_ID + ".editor.formatSource"; 

    /**
     * Action definition ID of the edit -> Go to Matching Fence action
     */
    public static final String GOTO_MATCHING_FENCE= PLUGIN_ID + ".editor.gotoMatchingFence"; 

    /**
     * Action definition ID of the edit -> Go to Previous Navigation Target action
     */
    public static final String GOTO_PREVIOUS_TARGET= PLUGIN_ID + ".editor.gotoPreviousTarget"; 

    /**
     * Action definition ID of the edit -> Go to Next Navigation Target action
     */
    public static final String GOTO_NEXT_TARGET= PLUGIN_ID + ".editor.gotoNextTarget"; 

    /**
     * Action definition ID of the Edit -> Open Declaration action
     */
    public static final String OPEN_EDITOR= PLUGIN_ID + ".editor.openDeclaration"; 

    /**
     * Action definition ID of the Edit -> Select Enclosing action
     */
    public static final String SELECT_ENCLOSING= PLUGIN_ID + ".editor.selectEnclosing";

    /**
     * Action definition ID of the Edit -> Restore Previous Selection action
     */
    public static final String RESTORE_PREVIOUS= PLUGIN_ID + ".editor.restorePrevious";

    /**
     * Action definition ID of the Edit -> Shift Right action
     */
    public static final String SHIFT_RIGHT= PLUGIN_ID + ".editor.shiftRight"; 

    /**
     * Action definition ID of the Edit -> Shift Left action
     */
    public static final String SHIFT_LEFT= PLUGIN_ID + ".editor.shiftLeft"; 

    /**
     * Action definition ID of the navigate -> Show Outline action
     * 
     * @since 0.1
     */
    public static final String SHOW_OUTLINE= PLUGIN_ID + ".editor.showOutline"; 
    
    /**
     * Action definition ID of the Edit -> Toggle Comment action
     */
    public static final String TOGGLE_COMMENT= PLUGIN_ID + ".editor.toggleComment"; 
}

