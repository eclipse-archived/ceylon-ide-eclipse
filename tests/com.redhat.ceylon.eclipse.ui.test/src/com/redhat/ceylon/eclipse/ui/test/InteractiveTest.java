package com.redhat.ceylon.eclipse.ui.test;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IFile;
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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.redhat.ceylon.eclipse.code.editor.Util;

@RunWith(SWTBotJunit4ClassRunner.class)
public class InteractiveTest extends ExistingMultiProjectTest {
    private static SWTWorkbenchBot  bot;
    
    @BeforeClass
    public static void createBot() {
        bot = new SWTWorkbenchBot();
        bot.viewByTitle("Welcome").close();
    }
    
    @Test
    public void gotoSelectedDeclarationFromLanguageModuleSource() {
        String fileName = "src/mainModule/run.ceylon";
        openInEditor(fileName);
        
        SWTBotEditor editor = bot.editorByTitle("run.ceylon");
        Assert.assertNotNull(editor);
        SWTBotEclipseEditor runEditor = editor.toTextEditor();
        runEditor.navigateTo(5, 1);
        runEditor.pressShortcut(Keystrokes.F3);
        editor = bot.editorByTitle("annotations.ceylon");
        Assert.assertNotNull(editor);
        SWTBotEclipseEditor annotationsEditor = editor.toTextEditor();
        annotationsEditor.navigateTo(3, 9);
        runEditor.pressShortcut(Keystrokes.F3);
        editor = bot.editorByTitle("Null.ceylon");
        assertNotNull(editor);
    }

    public void openInEditor(String fileName) {
        final IFile runFile = mainProject.getFile(fileName);
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                try {
                    Util.gotoLocation(runFile, 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
