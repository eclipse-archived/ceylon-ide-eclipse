package com.redhat.ceylon.eclipse.code.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class CeylonEditorPreferencesPage 
        extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {
    
    BooleanFieldEditor enclosingBrackets;
    BooleanFieldEditor matchingBracket;
    BooleanFieldEditor currentBracket;
    
    public CeylonEditorPreferencesPage() {
    }
    
    @Override
    public boolean performOk() {
        enclosingBrackets.store();
        matchingBracket.store();
        currentBracket.store();
    	return true;
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        enclosingBrackets.loadDefault();
        matchingBracket.loadDefault();
        currentBracket.loadDefault();
    }
    

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(EditorsPlugin.getDefault().getPreferenceStore());
//        setDescription("Preferences for the Ceylon editor");
    }

    @Override
    protected void createFieldEditors() {
        matchingBracket = new BooleanFieldEditor(CeylonEditor.MATCHING_BRACKET, 
                "Highlight matching bracket", 
                getFieldEditorParent());
        matchingBracket.loadDefault();
        addField(matchingBracket);
        currentBracket = new BooleanFieldEditor(CeylonEditor.SELECTED_BRACKET, 
                "Highlight selected bracket", 
                getFieldEditorParent());
        currentBracket.loadDefault();
        addField(currentBracket);
        enclosingBrackets = new BooleanFieldEditor(CeylonEditor.ENCLOSING_BRACKETS, 
                "Highlight enclosing brackets", 
                getFieldEditorParent());
        enclosingBrackets.loadDefault();
        addField(enclosingBrackets);
    }

}