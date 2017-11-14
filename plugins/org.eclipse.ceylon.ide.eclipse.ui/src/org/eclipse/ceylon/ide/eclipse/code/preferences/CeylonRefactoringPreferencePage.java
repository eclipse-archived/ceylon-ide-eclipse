/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_EXTRACT;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_RENAME;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_RENAME_SELECT;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class CeylonRefactoringPreferencePage 
        extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.refactoring";
    
    BoolFieldEditor linkedModeRename;
    BooleanFieldEditor linkedModeRenameSelect;
    BooleanFieldEditor linkedModeExtract;
    
    public CeylonRefactoringPreferencePage() {
        super(GRID);
        setDescription("Preferences related to Ceylon refactorings.");
    }
    
    @Override
    public boolean performOk() {
        linkedModeRename.store();
        linkedModeRenameSelect.store();
        linkedModeExtract.store();
        return true;
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        linkedModeRename.loadDefault();
        linkedModeRenameSelect.loadDefault();
        linkedModeExtract.loadDefault();
    }
    
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(CeylonPlugin.getPreferences());
        CeylonEditor.initializeBrackMatcherPreferences();
    }
    
    
    @Override
    protected Control createContents(Composite parent) {
        
//        Composite composite = new Composite(parent, SWT.NONE);
//        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
//        composite.setLayout(new GridLayout(1, true));
        
        Control contents = super.createContents(parent);
            
        return contents;
    }

    @Override
    protected void createFieldEditors() {
        Composite group = createGroup(1, "Linked mode");
        linkedModeExtract = new BooleanFieldEditor(LINKED_MODE_EXTRACT, 
                "Use linked mode for extract refactorings", 
                getFieldEditorParent(group));
        linkedModeExtract.load();
        addField(linkedModeExtract);
        linkedModeRename = new BoolFieldEditor(LINKED_MODE_RENAME, 
                "Use linked mode for rename", 
                getFieldEditorParent(group));
        linkedModeRename.load();
        addField(linkedModeRename);
        final Composite parent = getFieldEditorParent(group);
        parent.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());
        linkedModeRenameSelect = new BooleanFieldEditor(LINKED_MODE_RENAME_SELECT, 
                "Fully select renamed identifier", 
                parent);
        linkedModeRenameSelect.load();
        addField(linkedModeRenameSelect);
        linkedModeRenameSelect.setEnabled(
                getPreferenceStore().getBoolean(LINKED_MODE_RENAME), 
                parent);
        linkedModeRename.setListener(new Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                linkedModeRenameSelect.setEnabled(newValue, parent);
            }
        });
    }

    private Group createGroup(int cols, String text) {
        Composite parent = getFieldEditorParent();
        Group group = new Group(parent, SWT.NONE);
        group.setText(text);
        group.setLayout(GridLayoutFactory.swtDefaults().equalWidth(true).numColumns(cols).create());
        group.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).grab(true, false).create());
        return group;
    }
    
    interface Listener {
        void valueChanged(boolean oldValue, boolean newValue);
    }
    
    static class BoolFieldEditor extends BooleanFieldEditor {
        private Listener listener;
        public BoolFieldEditor(String name, String label, Composite parent) {
            super(name, label, parent);
        }
        public BoolFieldEditor(String name, String labelText, int style,
                Composite parent) {
            super(name, labelText, style, parent);
        }
        public void setListener(Listener listener) {
            this.listener = listener;
        }
        @Override
        protected void valueChanged(boolean oldValue, boolean newValue) {
            super.valueChanged(oldValue, newValue);
            if (listener!=null) {
                listener.valueChanged(oldValue, newValue);
            }
        }
        @Override
        protected void doLoadDefault() {
            boolean oldValue = getBooleanValue();
            super.doLoadDefault();
            boolean newValue = getBooleanValue();
            if (listener!=null) {
                listener.valueChanged(oldValue, newValue);
            }
        }
    }
    
    protected Composite getFieldEditorParent(Composite group) {
        Composite parent = new Composite(group, SWT.NULL);
        parent.setLayoutData(GridDataFactory.fillDefaults().create());
        return parent;
    }

    private IPropertyChangeListener listener;
    
    @Override
    public void dispose() {
        super.dispose();
        if (listener!=null) {
            CeylonPlugin.getPreferences().removePropertyChangeListener(listener);
        }
    }

}
