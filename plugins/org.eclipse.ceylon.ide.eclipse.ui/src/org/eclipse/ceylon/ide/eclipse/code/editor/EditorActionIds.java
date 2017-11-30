/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

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
     * Action definition ID of the Edit -> Terminate Statement action
     */
    public static final String TERMINATE_STATEMENT= PLUGIN_ID + ".editor.terminateStatement";

    /**
     * Action definition ID of the Edit -> Format Block action
     */
    public static final String FORMAT_BLOCK = PLUGIN_ID + ".editor.formatBlock";

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
     * Action definition ID of the Edit -> Select Enclosing action
     */
    public static final String SELECT_ENCLOSING= PLUGIN_ID + ".editor.selectEnclosing";

    /**
     * Action definition ID of the Edit -> Restore Previous Selection action
     */
    public static final String RESTORE_PREVIOUS= PLUGIN_ID + ".editor.restorePrevious";

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

    public static final String ADD_BLOCK_COMMENT= PLUGIN_ID + ".editor.addBlockComment";
    
    public static final String REMOVE_BLOCK_COMMENT= PLUGIN_ID + ".editor.removeBlockComment";
    
    public static final String SHOW_CEYLON_CODE = PLUGIN_ID + ".editor.code";

    public static final String SHOW_CEYLON_HIERARCHY = PLUGIN_ID + ".editor.hierarchy";

    public static final String SHOW_CEYLON_REFERENCES = PLUGIN_ID + ".editor.findReferences";

    public static final String SHOW_IN_CEYLON_HIERARCHY_VIEW = PLUGIN_ID + ".action.showInHierarchyView";

}

