package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.areAstAwareIncrementalBuildsEnabled;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJava;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJs;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getSuppressedWarnings;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getVerbose;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.ide.common.util.toCeylonBoolean_.toCeylonBoolean;
import static com.redhat.ceylon.ide.common.util.toCeylonStringIterable_.toCeylonStringIterable;
import static com.redhat.ceylon.ide.common.util.toCeylonString_.toCeylonString;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.modelJ2C;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;

public class CeylonRepoPropertiesPage extends PropertyPage {
    
    public static final String ID = 
            CeylonPlugin.PLUGIN_ID + ".preferences.repos";

    private CeylonRepoConfigBlock block;

    @Override
    public boolean performOk() {
        if (!isValid()) {
            return false;
        }

        IProject project = getSelectedProject();
        CeylonProject<IProject> ceylonProject = modelJ2C.ceylonModel().getProject(project);
        CeylonProjectConfig<IProject> projectConfig = 
                ceylonProject.getConfiguration();
        projectConfig.setOutputRepo(block.getOutputRepo());
        projectConfig.setProjectLocalRepos(
                toCeylonStringIterable(block.getProjectLocalRepos()));
        projectConfig.setProjectRemoteRepos(
                toCeylonStringIterable(block.getProjectRemoteRepos()));
        projectConfig.setProjectSuppressWarningsEnum(
                getSuppressedWarnings(project));
        projectConfig.setProjectOverrides(toCeylonString(block.getOverrides()));
        projectConfig.setProjectFlatClasspath(toCeylonBoolean(block.getFlatClasspath()));
        projectConfig.setProjectAutoExportMavenDependencies(
                toCeylonBoolean(block.getAutoExportMavenDependencies()));
        projectConfig.save();
        
        if (CeylonNature.isEnabled(project)) {
            boolean explodeModules = 
                    isExplodeModulesEnabled(project);
            boolean compileJs = compileToJs(project);
            boolean compileJava = compileToJava(project);
            boolean astAwareIncrementalBuildsEnabled = 
                    areAstAwareIncrementalBuildsEnabled(project);
            String verbose = getVerbose(project);
            new CeylonNature(block.getSystemRepo(), 
                    explodeModules, compileJava, compileJs, 
                    astAwareIncrementalBuildsEnabled, 
                    verbose).addToProject(project);      
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
        block = new CeylonRepoConfigBlock(
                new CeylonRepoConfigBlock.ValidationCallback() {
            @Override
            public void validationResultChange
                    (boolean isValid, String message) {
                setValid(isValid);
                setErrorMessage(message);
            }
        });
        block.initContents(composite);
        block.initState(project, 
                project.isOpen() && 
                CeylonNature.isEnabled(project));

        return composite;
    }

    private IProject getSelectedProject() {
        return (IProject) 
                getElement().getAdapter(IProject.class);
    }

}