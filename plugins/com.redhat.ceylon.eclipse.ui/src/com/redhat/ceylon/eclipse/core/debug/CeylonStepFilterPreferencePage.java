package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonStepFilterPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    public static final String PAGE_ID = "org.eclipse.jdt.debug.ui.JavaStepFilterPreferencePage"; //$NON-NLS-1$
    
    private Button filterLanguageModuleButton;
    private Button filterJBossModulesButton;

    public CeylonStepFilterPreferencePage() {
        this("Ceylon-specific Debug settings");
    }

    public CeylonStepFilterPreferencePage(String title) {
        super(title);
        setPreferenceStore(CeylonPlugin.getInstance().getPreferenceStore());
        setDescription("Additional options when debugging Ceylon programs");
    }

    public CeylonStepFilterPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
        setPreferenceStore(CeylonPlugin.getInstance().getPreferenceStore());
        setDescription("Additional options when debugging Ceylon programs");
    }

    @Override
    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        //The main composite
        Composite composite = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH, 0, 0);
        createCeylonDebugPreferences(composite);
        return composite;
    }

    private void createCeylonDebugPreferences(Composite parent) {
        Composite container = SWTFactory.createComposite(parent, parent.getFont(), 2, 1, GridData.FILL_BOTH, 0, 0);
        createStepFilterCheckboxes(container);
    }

    /**
     * create the checked preferences for the page
     * @param container the parent container
     */
    private void createStepFilterCheckboxes(Composite container) {
        filterLanguageModuleButton = SWTFactory.createCheckButton(container, 
                "Filter language module declarations (ceylon.language*, com.redhat.ceylon.*)", 
                null, getPreferenceStore().getBoolean(CeylonDebugOptionsManager.PREF_FILTER_LANGUAGE_MODULE), 2);
        filterJBossModulesButton = SWTFactory.createCheckButton(container, 
                "Filter Module Runtime declarations (JBoss Modules, Ceylon Runtime, Ceylon Bootstrap)", 
                null, getPreferenceStore().getBoolean(CeylonDebugOptionsManager.PREF_FILTER_MODULE_RUNTIME), 2);
    }

    
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        filterLanguageModuleButton.setSelection(getPreferenceStore().getDefaultBoolean(CeylonDebugOptionsManager.PREF_FILTER_LANGUAGE_MODULE));
        filterJBossModulesButton.setSelection(getPreferenceStore().getDefaultBoolean(CeylonDebugOptionsManager.PREF_FILTER_MODULE_RUNTIME));
        super.performDefaults();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(CeylonDebugOptionsManager.PREF_FILTER_LANGUAGE_MODULE, filterLanguageModuleButton.getSelection());
        store.setValue(CeylonDebugOptionsManager.PREF_FILTER_MODULE_RUNTIME, filterJBossModulesButton.getSelection());
        return super.performOk();
    }
    
}
