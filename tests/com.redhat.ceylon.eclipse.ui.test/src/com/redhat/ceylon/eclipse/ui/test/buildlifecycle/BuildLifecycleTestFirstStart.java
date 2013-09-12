package com.redhat.ceylon.eclipse.ui.test.buildlifecycle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.eclipse.core.internal.events.BuildManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.redhat.ceylon.eclipse.ui.test.AbstractMultiProjectTest;
import com.redhat.ceylon.eclipse.ui.test.Utils;
import com.redhat.ceylon.eclipse.ui.test.Utils.CeylonBuildSummary;

@RunWith(SWTBotJunit4ClassRunner.class)
public class BuildLifecycleTestFirstStart extends AbstractMultiProjectTest {

    private static SWTWorkbenchBot  bot;

    @BeforeClass
    public static void beforeClass() {
        bot = Utils.createBot();
    }
    
    @AfterClass
    public static void afterClass() throws CoreException {
        // Don't delete projects since we will start another test that use the OSGI 
        // workspace data left by this one        
        // Instead save the projects
        workspace.save(true, null);
    }
    
    @After
    public void resetWorkbench() {
        // Don't clean things, since we will start another test that use the OSGI 
        // workspace data left by this one
    }
    
    
    @Test
    public void importShouldTriggerFullBuild() throws InterruptedException, CoreException {
        
        IPath projectDescriptionPath = null;
        IPath userDirPath = new Path(System.getProperty("user.dir"));

        IProject project = workspace.getRoot().getProject(mainProjectName);
        CeylonBuildSummary buildSummary = new CeylonBuildSummary(project);
        buildSummary.install();
        
        IPath projectPathPrefix = userDirPath.append("resources/" + projectGroup + "/");
        
        try {
            projectDescriptionPath = projectPathPrefix.append(referencedJavaProjectName + "/.project");
            referencedJavaProject = Utils.importProject(workspace, projectGroup, projectDescriptionPath);
            referencedJavaProjectJDT = JavaCore.create(referencedJavaProject);
        }
        catch(Exception e) {
            Assert.fail("Import of the referenced java project failed with the exception : \n" + e.toString());
        }

        try {
            projectDescriptionPath = projectPathPrefix.append("referenced-ceylon-project/.project");
            referencedCeylonProject = Utils.importProject(workspace, projectGroup, projectDescriptionPath);
            referencedCeylonProjectJDT = JavaCore.create(referencedCeylonProject);
        }
        catch(Exception e) {
            Assert.fail("Import of the referenced ceylon project failed with the exception : \n" + e.toString());
        }

        try {
            projectDescriptionPath = projectPathPrefix.append("main-ceylon-project/.project");
            mainProject = Utils.importProject(workspace, projectGroup,
                    projectDescriptionPath);
            mainProjectJDT = JavaCore.create(mainProject);
        }
        catch(Exception e) {
            Assert.fail("Build of the main project failed with the exception : \n" + e.toString());
        }
        
        if (!buildSummary.waitForBuildEnd(120)) {
            fail("No build has been automatically started after the projects import");
        }
        
        assertTrue("It should have done a full build after an import", buildSummary.didFullBuild());
        assertEquals("It should have build the referenced projects first", 1, buildSummary.getPreviousBuilds().size());
        assertThat("The referenced Java project build should not have any error",
                Utils.getProjectErrorMarkers(referencedJavaProject),
                Matchers.empty());
        assertThat("The referenced Ceylon project build should not have any error",
                Utils.getProjectErrorMarkers(referencedJavaProject),
                Matchers.empty());
        assertThat("The main project build should not have any error",
                Utils.getProjectErrorMarkers(mainProject),
                Matchers.empty());
        
        // Now set autoBuild to false so that we can touch some files before closing
        IWorkspaceDescription description = workspace.getDescription();
        description.setAutoBuilding(false);
        workspace.setDescription(description);
        
        // Now touch on some files before start
        
        for (IFile file : BuildLifecycleTestSecondStart.getFilesTouchedBeforeRestart()) {
            file.touch(null);
        }
    }
}
