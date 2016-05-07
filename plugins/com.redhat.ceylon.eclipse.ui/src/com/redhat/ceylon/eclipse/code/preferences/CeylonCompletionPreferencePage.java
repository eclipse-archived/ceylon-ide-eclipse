package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_ACTIVATION;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_ACTIVATION_CHARS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_ACTIVATION_DELAY;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_INSERT;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_INSERT_PREFIX;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.CHAIN_LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.COMPLETION;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.COMPLETION_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.ENABLE_COMPLETION_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_COMPLETION_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INEXACT_MATCHES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMETER_TYPES_IN_COMPLETIONS;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.COLORS_AND_FONTS_PAGE_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.COMPLETION_FONT_PREFERENCE;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonCompletionPreferencePage 
        extends FiltersPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.completion";
    
    BooleanFieldEditor autoInsert;
    BooleanFieldEditor autoInsertPrefix;
    BoolFieldEditor autoActivation;
    RadioGroupFieldEditor completion;
    RadioGroupFieldEditor inexactMatches;
    BoolFieldEditor linkedMode;
    BooleanFieldEditor linkedModeChain;
    ScaleFieldEditor autoActivationDelay;
    RadioGroupFieldEditor autoActivationChars;
    BooleanFieldEditor displayParameterTypes;
    
    public CeylonCompletionPreferencePage() {
        setDescription("Preferences related to content completion in Ceylon source files.");
    }
    
    @Override
    public boolean performOk() {
        autoInsert.store();
        autoInsertPrefix.store();
        autoActivation.store();
        autoActivationDelay.store();
        autoActivationChars.store();
        completion.store();
        inexactMatches.store();
        linkedMode.store();
        linkedModeChain.store();
        displayParameterTypes.store();
        return super.performOk();
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        autoInsert.loadDefault();
        autoInsertPrefix.loadDefault();
        autoActivation.loadDefault();
        autoActivationDelay.loadDefault();
        autoActivationChars.loadDefault();
        completion.loadDefault();
        inexactMatches.loadDefault();
        linkedMode.loadDefault();
        linkedModeChain.loadDefault();
        displayParameterTypes.loadDefault();
    }
    
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(CeylonPlugin.getPreferences());
    }
    
    @Override
    protected String getLabelText() {
        return "Filtered packages and declarations are excluded from completion proposal lists.";
    }
    
    @Override
    protected void createFieldEditors() {
        
        final Composite group1 = createGroup(1, "General");
        displayParameterTypes = new BooleanFieldEditor(PARAMETER_TYPES_IN_COMPLETIONS, 
                "Display parameter types in completion proposals", 
                getFieldEditorParent(group1));
        displayParameterTypes.load();
        addField(displayParameterTypes);
        final Composite p4 = getFieldEditorParent(group1);
        inexactMatches = new RadioGroupFieldEditor(INEXACT_MATCHES, 
                "For inexact matches propose", 1, 
                new String[][] { new String[] { "no argument lists", "none" }, 
                                 new String[] { "positional argument lists", "positional" },
                                 new String[] { "both positional and named argument lists", "both" } }, p4);
        inexactMatches.load();
        addField(inexactMatches);
        final Composite p3 = getFieldEditorParent(group1);
        completion = new RadioGroupFieldEditor(COMPLETION, 
                "Completion with trailing identifier characters", 2, 
                new String[][] { new String[] { "inserts", "insert" }, 
                                 new String[] { "overwrites", "overwrite" } }, p3);
        completion.load();
        addField(completion);
        new Label(group1, SWT.NONE).setText("   Press 'Ctrl' when selecting a proposal to toggle");
        linkedMode = new BoolFieldEditor(LINKED_MODE_ARGUMENTS, 
                "Use linked mode to complete argument lists", 
                getFieldEditorParent(group1));
        linkedMode.load();
        addField(linkedMode);
        final Composite parent = getFieldEditorParent(group1);
        parent.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());
        linkedModeChain = new BooleanFieldEditor(CHAIN_LINKED_MODE_ARGUMENTS, 
                "Propose chain completions for arguments", parent);
        linkedModeChain.load();
        addField(linkedModeChain);
        linkedModeChain.setEnabled(
                getPreferenceStore().getBoolean(LINKED_MODE_ARGUMENTS), 
                parent);
        linkedMode.setListener(new BoolFieldEditor.Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                linkedModeChain.setEnabled(newValue, parent);
            }
        });
        
        final Composite group3 = createGroup(1, "Proposal auto-insertion");
        autoInsert = new BooleanFieldEditor(AUTO_INSERT, 
                "Auto-insert unique completion proposals", 
                getFieldEditorParent(group3));
        autoInsert.load();
        addField(autoInsert);
        autoInsertPrefix = new BooleanFieldEditor(AUTO_INSERT_PREFIX, 
                "Auto-insert prefix common to all completion proposals", 
                getFieldEditorParent(group3));
        autoInsertPrefix.load();
        addField(autoInsertPrefix);

        final Composite group4 = createGroup(1, "Proposal list auto-activation");
        autoActivation = new BoolFieldEditor(AUTO_ACTIVATION, 
                "Auto-activate completion proposal list", 
                getFieldEditorParent(group4));
        autoActivation.load();
        addField(autoActivation);
        final Composite p1 = getFieldEditorParent(group4);
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        autoActivationChars = new RadioGroupFieldEditor(AUTO_ACTIVATION_CHARS, 
                "Auto-activation characters", 3, 
                new String[][] { new String[] { "period", "." }, 
                                 new String[] { "letters", letters },
                                 new String[] { "both", "." + letters } }, p1);
        autoActivationChars.load();
        addField(autoActivationChars);
        final Composite p2 = getFieldEditorParent(group4);
        autoActivationDelay = new ScaleWithLabelFieldEditor(AUTO_ACTIVATION_DELAY, 
                "Auto-activation delay", p2);
        //autoActivationDelay.setValidRange(1, 9999);
        autoActivationDelay.setMinimum(1);
        autoActivationDelay.setMaximum(2000);
        autoActivationDelay.load();
        addField(autoActivationDelay);
        final IPreferenceStore store = CeylonPlugin.getPreferences();
        boolean enabled = store.getBoolean(AUTO_ACTIVATION);
        autoActivationChars.setEnabled(enabled, p1);
        autoActivationDelay.setEnabled(enabled, p2);        
        autoActivation.setListener(new BoolFieldEditor.Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                autoActivationChars.setEnabled(newValue, p1);
                autoActivationDelay.setEnabled(newValue, p2);
            }
        });
        
        super.createFieldEditors();
    }

    @Override
    protected String getInactiveFiltersPreference() {
        return INACTIVE_COMPLETION_FILTERS;
    }

    @Override
    protected String getActiveFiltersPreference() {
        return COMPLETION_FILTERS;
    }
    
    @Override
    protected String getEnabledPreference() {
        return ENABLE_COMPLETION_FILTERS;
    }
    
    @Override
    protected Control createContents(Composite parent) {
        Link colorsAndFontsLink = new Link(parent, 0);
        colorsAndFontsLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        colorsAndFontsLink.setText("See '<a>Colors and Fonts</a>' to customize font and proposal colors.");
        colorsAndFontsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        COLORS_AND_FONTS_PAGE_ID, null, 
                        "selectFont:" 
                                + COMPLETION_FONT_PREFERENCE);
            }
        });
        
        Link filtersLink = new Link(parent, 0);
        filtersLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        filtersLink.setText("See '<a>Filtering</a>' to set up global filters and match highlighting.");
        filtersLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonFiltersPreferencePage.ID, 
                        null, null);
            }
        });
        
        return super.createContents(parent);
    }

}
