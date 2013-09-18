package com.redhat.ceylon.eclipse.ui.test.swtbot;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardStrategy;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLink;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;
import com.redhat.ceylon.eclipse.ui.test.AbstractMultiProjectTest;
import com.redhat.ceylon.eclipse.ui.test.Utils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MainUITests extends AbstractMultiProjectTest {
    private static SWTWorkbenchBot  bot;
    
    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        bot= Utils.createBot();
        
        importAndBuild();
    }
    
    @Test
    public void gotoSelectedDeclarationFromLanguageModuleSource() {
        String fileName = "src/mainModule/run.ceylon";
        Utils.openInEditor(mainProject, fileName);
        
        SWTBotEditor editor = bot.editorByTitle("run.ceylon");
        Assert.assertNotNull(editor);
        SWTBotEclipseEditor runEditor = editor.toTextEditor();
        runEditor.show();
        runEditor.navigateTo(21, 1);
        bot.sleep(500);
        runEditor.pressShortcut(Keystrokes.F3);
        bot.sleep(500);
        editor = bot.editorByTitle("annotations.ceylon");
        Assert.assertNotNull(editor);
        SWTBotEclipseEditor annotationsEditor = editor.toTextEditor();
        annotationsEditor.show();
        annotationsEditor.navigateTo(4, 67);
        bot.sleep(500);
        annotationsEditor.pressShortcut(Keystrokes.F3);
        bot.sleep(500);
        editor = bot.editorByTitle("ClassDeclaration.ceylon");
        assertNotNull("Following links should have led to open the file 'ClassDeclaration.ceylon'", editor);
    }
}
