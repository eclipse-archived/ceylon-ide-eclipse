package org.eclipse.ceylon.ide.eclipse.ui.test.buildlifecycle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

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
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import org.eclipse.ceylon.ide.eclipse.ui.test.AbstractMultiProjectTest;
import org.eclipse.ceylon.ide.eclipse.ui.test.Utils;
import org.eclipse.ceylon.ide.eclipse.ui.test.Utils.CeylonBuildSummary;

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
        try {
            mainProjectJDT.save(null, false);
            referencedCeylonProjectJDT.save(null, false);
            workspace.save(true, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void resetWorkbench() {
        // Don't clean things, since we will start another test that use the OSGI 
        // workspace data left by this one
    }
    
    
    @Test
    public void importShouldTriggerFullBuild() throws InterruptedException, CoreException {
        importAndBuild();
        
        assertThat("The referenced Ceylon project build should not have any error",
                Utils.getProjectErrorMarkers(referencedCeylonProject),
                Matchers.empty());
        assertThat("The main project build should not have any error",
                Utils.getProjectErrorMarkers(mainProject),
                Matchers.empty());
        
        // Now set autoBuild to false so that we can touch some files before closing
        IWorkspaceDescription description = workspace.getDescription();
        description.setAutoBuilding(false);
        workspace.setDescription(description);
        
        // Now touch on some files before start
        
        
        
        Utils.openInEditor(referencedCeylonProject, "src/referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon");
        Utils.openInEditor(mainProject, "src/mainModule/run.ceylon");
        
        SWTBotEditor editor = bot.editorByTitle("CeylonDeclarations_Referenced_Ceylon_Project.ceylon");
        Assert.assertNotNull(editor);
        SWTBotEclipseEditor fileEditor = editor.toTextEditor();
        fileEditor.show();
        fileEditor.insertText(0, 0, 
                "shared object ceylonAdditionalTopLevelObject_Referenced_Ceylon_Project {\n" +
                "}\n\n");
        fileEditor.saveAndClose();
        
        IFile otherFile = mainProject.getFile(new Path("src/mainModule/other.ceylon"));
        otherFile.create(new ByteArrayInputStream((
                "import referencedCeylonProject {\n"
                + "    ceylonAdditionalTopLevelObject_Referenced_Ceylon_Project\n"
                + "}\n"
                + "void other() {\n"
                + "    value v = ceylonAdditionalTopLevelObject_Referenced_Ceylon_Project;\n"
                + "}\n"
                ).getBytes()),
                true,
                null);
        
        for (IFile file : BuildLifecycleTestSecondStart.getFilesTouchedBeforeRestart()) {
            if (! file.getName().contains("CeylonDeclarations_Referenced_Ceylon_Project.ceylon") && 
                    ! file.getName().contains("other.ceylon") ) {
                file.touch(null);
            }
        }
    }
}
