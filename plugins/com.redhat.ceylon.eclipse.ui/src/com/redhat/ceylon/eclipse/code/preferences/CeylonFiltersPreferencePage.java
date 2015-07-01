package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.MATCH_HIGHLIGHTING;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.redhat.ceylon.eclipse.core.debug.preferences.CeylonStepFilterPreferencePage;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonFiltersPreferencePage 
        extends FiltersPreferencePage 
        implements IWorkbenchPreferencePage {

    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.filters";
    
    private RadioGroupFieldEditor matchHighlighting;
    
    public CeylonFiltersPreferencePage() {
        setDescription("Configure global filters and match highlighting for Ceylon development.");
    }

    @Override
    public boolean performOk() {
        matchHighlighting.store();
        return super.performOk();
    }
    
    
    @Override
    protected void performDefaults() {
        matchHighlighting.loadDefault();
        super.performDefaults();
    }
    
    @Override
    protected Control createContents(Composite parent) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        composite.setLayout(new GridLayout(1, true));
        
        Control contents = super.createContents(composite);
        
        Link completionLink = new Link(parent, 0);
        completionLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        completionLink.setText("See '<a>Completion</a>' to add additional filters for completion proposals.");
        completionLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonCompletionPreferencePage.ID, null, null);
            }
        });
        
        Link outlineLink = new Link(parent, 0);
        outlineLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        outlineLink.setText("See '<a>Outlines and Hierarchies</a>' to add additional filters for hierarchy views.");
        outlineLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonOutlinesPreferencePage.ID, null, null);
            }
        });
        
        Link openLink = new Link(parent, 0);
        openLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        openLink.setText("See '<a>Open Dialogs</a>' to add additional filters for 'Open ...' dialogs.");
        openLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonOpenDialogsPreferencePage.ID, null, null);
            }
        });
        
        Link debugLink = new Link(parent, 0);
        debugLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        debugLink.setText("See '<a>Debugging</a>' to set up step filtering.");
        debugLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonStepFilterPreferencePage.ID, null, null);
            }
        });
        
        return contents;
    }
    

    @Override
    protected void createFieldEditors() {
        Group highlighting = createGroup(1, "Match highlighting");
        matchHighlighting = new RadioGroupFieldEditor(MATCH_HIGHLIGHTING, 
                "Emphasize matching text in proposal lists, 'Open' dialogs, and \npopup outline and hierarchy:", 4, 
                new String[][] { new String[] { "Bold", "bold" }, 
                        new String[] { "Underline", "underline" },
                        new String[] { "Text color", "color" },
//                        new String[] { "Background color", "background" },
                        new String[] { "None", "none" } }, 
                        getFieldEditorParent(highlighting));
        matchHighlighting.load();
        addField(matchHighlighting);
    }
    
    @Override
    protected String getLabelText() {
        return "Filtered packages and declarations are excluded from completion\n" +
               "proposal lists, 'Open' dialogs, and type hierarchy views.";
    }
    
    @Override
    protected String getInactiveFiltersPreference() {
        return INACTIVE_FILTERS;
    }

    @Override
    protected String getActiveFiltersPreference() {
        return FILTERS;
    }

}
