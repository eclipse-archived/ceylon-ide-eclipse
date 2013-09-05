package com.redhat.ceylon.eclipse.ui.test.swtbot;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardStrategy;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLink;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;
import com.redhat.ceylon.eclipse.ui.test.AbstractMultiProjectTest;
import com.redhat.ceylon.eclipse.ui.test.Utils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MainBuildTests extends AbstractMultiProjectTest {
    private static SWTWorkbenchBot  bot;
    
    @BeforeClass
    public static void beforeClass() {
        bot = Utils.createBot();
        importAndBuild();
    }
    
    @After
    public void resetWorkbench() {
        Utils.resetWorkbench(bot);
    }
    
    protected void openInEditor(String fileName) {
        final IFile runFile = mainProject.getFile(fileName);
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                try {
                    new SWTWorkbenchBot();
                    Util.gotoLocation(runFile, 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    
    
    @Test
    public void bug589_AddedJavaMethodNotSeen() throws InterruptedException, CoreException {
        openInEditor("src/mainModule/run.ceylon");
        openInEditor("javaSrc/mainModule/JavaClassInCeylonModule_Main_Ceylon_Project.java");

        SWTBotEditor editor = bot.editorByTitle("JavaClassInCeylonModule_Main_Ceylon_Project.java");
        Assert.assertNotNull(editor);
        SWTBotEclipseEditor javaFileEditor = editor.toTextEditor();
        javaFileEditor.show();
        assertEquals("Wrong line 4 in file JavaClassInCeylonModule_Main_Ceylon_Project.java : ", javaFileEditor.getLines().get(3).trim(), "public class JavaClassInCeylonModule_Main_Ceylon_Project {");        
        String javaEditorText = javaFileEditor.getText();
        javaFileEditor.insertText(4, 0, "public void newMethodTotest() {}\n");
        
        Utils.CeylonBuildSummary buildSummary = new Utils.CeylonBuildSummary(mainProjectJDT);
        buildSummary.install();
        javaFileEditor.save();
        try {
            buildSummary.waitForBuildEnd(10);
            
            editor = bot.editorByTitle("run.ceylon");
            Assert.assertNotNull(editor);
            SWTBotEclipseEditor ceylonFileEditor = editor.toTextEditor();
            ceylonFileEditor.show();
            assertEquals("Bad line 18 in run.ceylon : ", ceylonFileEditor.getLines().get(17).trim(), "value v5 = JavaClassInCeylonModule_Main_Ceylon_Project();");
            String ceylonEditorText = ceylonFileEditor.getText();
            ceylonFileEditor.insertText(18, 0,"v5.newMethodToTest();\n");
            
            /*
            ceylonFileEditor.navigateTo(18, 3);
            List<String> proposals = javaFileEditor.getAutoCompleteProposals("");
            assertThat("The new method of the Java class should be proposed",
                   proposals,
                   new IsCollectionContaining(new IsEqual("test()")));
                   */
            
            buildSummary = new Utils.CeylonBuildSummary(mainProjectJDT);
            ceylonFileEditor.save();
            try {
                buildSummary.waitForBuildEnd(10);
                assertThat("The build should not have any error",
                        Utils.getProjectErrorMarkers(mainProject),
                        Matchers.empty());
            }
            finally {
                ceylonFileEditor.setText(ceylonEditorText);
                ceylonFileEditor.saveAndClose();
            }
        }
        finally {
            javaFileEditor.setText(javaEditorText);
            javaFileEditor.saveAndClose();
        }
    }
    
}
