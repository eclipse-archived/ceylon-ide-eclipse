package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJava;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJs;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.showWarnings;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class CeylonPreferencesPage extends PropertyPage {

    private boolean explodeModules;
    private boolean showCompilerWarnings=true;
    private boolean builderEnabled = false;
    private boolean backendJs = false;
    private boolean backendJava = false;

    private Button showWarnings;
    private Button compileToJs;
    private Button compileToJava;
    private Button enableExplodeModules;

    @Override
    public boolean performOk() {
    	store();
    	return true;
    }
    
    @Override
    protected void performDefaults() {
        explodeModules=false;
        enableExplodeModules.setSelection(false);
        showCompilerWarnings=true;
        showWarnings.setSelection(true);
        backendJs = false;
        backendJava = true;
        compileToJs.setSelection(false);
        compileToJava.setSelection(true);
        super.performDefaults();
    }
    
    private void store() {
        IProject project = getSelectedProject();
        String systemRepo = CeylonBuilder.getCeylonSystemRepo(project);
        new CeylonNature(systemRepo, explodeModules, !showCompilerWarnings, 
                backendJava, backendJs).addToProject(project);
    }

    private IProject getSelectedProject() {
        return (IProject) getElement().getAdapter(IProject.class);
    }
    
    //TODO: fix copy/paste!
    void addControls(Composite parent) {
        Label desc = new Label(parent, SWT.LEFT | SWT.WRAP);
        desc.setText("The Ceylon builder compiles Ceylon source contained in the project");

        final Button enableBuilder = new Button(parent, SWT.PUSH);
        enableBuilder.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        enableBuilder.setText("Enable Ceylon Builder");
        enableBuilder.setEnabled(!builderEnabled && getSelectedProject().isOpen());
        enableBuilder.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.ELE32));
        //enableBuilder.setSize(40, 40);

        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sep.setLayoutData(sgd);

        //Label misc = new Label(parent, SWT.LEFT | SWT.WRAP);
        //misc.setText("Ceylon compiler settings:");

        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        composite.setText("Ceylon compiler settings");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout); 
        
        enableExplodeModules = new Button(composite, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        enableExplodeModules.setText("Enable Java classes calling Ceylon (may affect performance)");
        enableExplodeModules.setSelection(explodeModules);
        enableExplodeModules.setEnabled(builderEnabled);

        showWarnings = new Button(composite, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        showWarnings.setText("Show compiler warnings (for unused declarations)");
        showWarnings.setSelection(showCompilerWarnings);
        showWarnings.setEnabled(builderEnabled);

        compileToJava = new Button(composite, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        compileToJava.setText("Compile project for JVM");
        compileToJava.setSelection(backendJava);
        compileToJava.setEnabled(builderEnabled);

        compileToJs = new Button(composite, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        compileToJs.setText("Compile project to JavaScript");
        compileToJs.setSelection(backendJs);
        compileToJs.setEnabled(builderEnabled);

        enableBuilder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new CeylonNature().addToProject(getSelectedProject());
                enableBuilder.setEnabled(false);
                enableExplodeModules.setEnabled(true);
                showWarnings.setEnabled(true);
                compileToJs.setEnabled(true);
                compileToJava.setEnabled(true);
                builderEnabled=true;
            }
        });
    
        enableExplodeModules.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	explodeModules = !explodeModules;
            }
        });
        
        showWarnings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	showCompilerWarnings = !showCompilerWarnings;
            }
        });

        compileToJava.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                backendJava = !backendJava;
            }
        });
        
        compileToJs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                backendJs = !backendJs;
            }
        });
    }
	
    @Override
    protected Control createContents(Composite composite) {
        IProject project = getSelectedProject();
        if (project.isOpen()) {
            builderEnabled = CeylonNature.isEnabled(project);
            if (builderEnabled) {
                explodeModules = isExplodeModulesEnabled(project);
                showCompilerWarnings = showWarnings(project);
                backendJs = compileToJs(project);
                backendJava = compileToJava(project);
            }
        }

        addControls(composite);
        return composite;
    }

}