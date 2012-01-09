package com.redhat.ceylon.eclipse.imp.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class SyntaxPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {
    
    public SyntaxPreferencePage() {
        super(GRID);            
        setPreferenceStore(CeylonPlugin.getInstance().getPreferenceStore());
        setTitle("Ceylon Syntax");
        setDescription("Select colors used for highlighting Ceylon syntax:");
    }

    @Override
    public void init(IWorkbench wb) {}

    @Override
    protected void createFieldEditors() {
        addField(new ColorFieldEditor("color.keywords", "Keywords:", getFieldEditorParent()));
        addField(new ColorFieldEditor("color.types", "Type identifiers:", getFieldEditorParent()));
        addField(new ColorFieldEditor("color.identifiers", "Other identifiers:", getFieldEditorParent()));
        addField(new ColorFieldEditor("color.numbers", "Numeric literals:", getFieldEditorParent()));
        addField(new ColorFieldEditor("color.strings", "String literals:", getFieldEditorParent()));
        addField(new ColorFieldEditor("color.annotations", "Annotations:", getFieldEditorParent()));
        addField(new ColorFieldEditor("color.annotationstrings", "String literals in annotations:", getFieldEditorParent()));
        addField(new ColorFieldEditor("color.comments", "Comments:", getFieldEditorParent()));
        addField(new ColorFieldEditor("color.todos", "Todos and fixes:", getFieldEditorParent()));
    }

}
