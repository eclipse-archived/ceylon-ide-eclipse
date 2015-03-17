package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.areAstAwareIncrementalBuildsEnabled;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJava;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJs;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getSuppressedWarnings;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getVerbose;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonRepoPropertiesPage extends PropertyPage {
    
    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.repos";

    private CeylonRepoConfigBlock block;

    @Override
    public boolean performOk() {
        if (!isValid()) {
            return false;
        }

        IProject project = getSelectedProject();
        CeylonProjectConfig projectConfig = CeylonProjectConfig.get(project);
        projectConfig.setOutputRepo(block.getOutputRepo());
        projectConfig.setProjectLocalRepos(block.getProjectLocalRepos());
        projectConfig.setProjectRemoteRepos(block.getProjectRemoteRepos());
        projectConfig.setProjectSuppressWarningsEnum(getSuppressedWarnings(project));
        projectConfig.setProjectOverrides(block.getOverrides());
        projectConfig.setProjectFlatClasspath(block.getFlatClasspath());
        projectConfig.setProjectAutoExportMavenDependencies(block.getAutoExportMavenDependencies());
        projectConfig.save();
        
        if (CeylonNature.isEnabled(project)) {
            boolean explodeModules = isExplodeModulesEnabled(project);
            boolean compileJs = compileToJs(project);
            boolean compileJava = compileToJava(project);
            boolean astAwareIncrementalBuildsEnabled = areAstAwareIncrementalBuildsEnabled(project);
            String verbose = getVerbose(project);
            new CeylonNature(block.getSystemRepo(), explodeModules,
                    compileJava, compileJs, astAwareIncrementalBuildsEnabled, verbose
                    ).addToProject(project);      
        }
        return true;
    }

    @Override
    protected void performDefaults() {
        block.performDefaults();
        super.performDefaults();
    }

    @Override
    protected Control createContents(Composite composite) {
        IProject project = getSelectedProject();
        boolean isCeylonNatureEnabled = project.isOpen() && CeylonNature.isEnabled(project);
        
        block = new CeylonRepoConfigBlock(new CeylonRepoConfigBlock.ValidationCallback() {
            @Override
            public void validationResultChange(boolean isValid, String message) {
                setValid(isValid);
                setErrorMessage(message);
            }
        });
        block.initContents(composite);
        block.initState(project, isCeylonNatureEnabled);

        return composite;
    }

    private IProject getSelectedProject() {
        return (IProject) getElement().getAdapter(IProject.class);
    }

}