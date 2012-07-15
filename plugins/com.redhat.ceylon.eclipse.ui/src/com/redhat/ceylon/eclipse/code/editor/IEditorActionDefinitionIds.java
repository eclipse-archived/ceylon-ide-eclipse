package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.imp.runtime.RuntimePlugin;

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
    public static final String CORRECT_INDENTATION= RuntimePlugin.IMP_RUNTIME + ".editor.correctIndentation"; //$NON-NLS-1$

    /**
     * Action definition id of the collapse members action
     * (value: <code>"org.eclipse.imp.runtime.editor.folding.collapseMembers"</code>).
     * @since 0.1
     */
    public static final String FOLDING_COLLAPSE_MEMBERS= RuntimePlugin.IMP_RUNTIME + ".editor.folding.collapseMembers"; //$NON-NLS-1$

    /**
     * Action definition id of the collapse comments action
     * (value: <code>"org.eclipse.imp.runtime.editor.folding.collapseComments"</code>).
     * @since 0.1
     */
    public static final String FOLDING_COLLAPSE_COMMENTS= RuntimePlugin.IMP_RUNTIME + ".editor.folding.collapseComments"; //$NON-NLS-1$

    /**
     * Source menu: id of standard Format global action
     * (value <code>"org.eclipse.imp.runtime.editor.formatSource"</code>).
     */
    public static final String FORMAT= RuntimePlugin.IMP_RUNTIME + ".editor.formatSource"; //$NON-NLS-1$

    /**
     * Action definition ID of the edit -> Go to Matching Fence action
     * (value <code>"org.eclipse.imp.runtime.gotoMatchingFence"</code>).
     */
    public static final String GOTO_MATCHING_FENCE= RuntimePlugin.IMP_RUNTIME + ".editor.gotoMatchingFence"; //$NON-NLS-1$

    /**
     * Action definition ID of the edit -> Go to Previous Navigation Target action
     * (value <code>"org.eclipse.imp.runtime.editor.gotoPreviousTarget"</code>).
     */
    public static final String GOTO_PREVIOUS_TARGET= RuntimePlugin.IMP_RUNTIME + ".editor.gotoPreviousTarget"; //$NON-NLS-1$

    /**
     * Action definition ID of the edit -> Go to Next Navigation Target action
     * (value <code>"org.eclipse.imp.runtime.editor.gotoNextTarget"</code>).
     */
    public static final String GOTO_NEXT_TARGET= RuntimePlugin.IMP_RUNTIME + ".editor.gotoNextTarget"; //$NON-NLS-1$

    /**
     * Action definition ID of the Edit -> Open Declaration action
     * (value <code>"org.eclipse.imp.runtime.editor.openDeclaration"</code>).
     */
    public static final String OPEN_EDITOR= RuntimePlugin.IMP_RUNTIME + ".editor.openDeclaration"; //$NON-NLS-1$

    /**
     * Action definition ID of the Edit -> Select Enclosing action
     * (value <code>"org.eclipse.imp.runtime.editor.selectEnclosing"</code>).
     */
    public static final String SELECT_ENCLOSING= RuntimePlugin.IMP_RUNTIME + ".editor.selectEnclosing";

    /**
     * Action definition ID of the Edit -> Shift Right action
     * (value <code>"org.eclipse.imp.runtime.editor.shiftRight"</code>).
     */
    public static final String SHIFT_RIGHT= RuntimePlugin.IMP_RUNTIME + ".editor.shiftRight"; //$NON-NLS-1$

    /**
     * Action definition ID of the Edit -> Shift Left action
     * (value <code>"org.eclipse.imp.runtime.editor.shiftLeft"</code>).
     */
    public static final String SHIFT_LEFT= RuntimePlugin.IMP_RUNTIME + ".editor.shiftLeft"; //$NON-NLS-1$

    /**
     * Action definition ID of the navigate -> Show Outline action
     * (value <code>"org.eclipse.imp.runtime.editor.showOutline"</code>).
     * 
     * @since 0.1
     */
    public static final String SHOW_OUTLINE= RuntimePlugin.IMP_RUNTIME + ".editor.showOutline"; //$NON-NLS-1$
    
    /**
     * Action definition ID of the Edit -> Toggle Comment action
     * (value <code>"org.eclipse.imp.runtime.editor.toggleComment"</code>).
     */
    public static final String TOGGLE_COMMENT= RuntimePlugin.IMP_RUNTIME + ".editor.toggleComment"; //$NON-NLS-1$
}

