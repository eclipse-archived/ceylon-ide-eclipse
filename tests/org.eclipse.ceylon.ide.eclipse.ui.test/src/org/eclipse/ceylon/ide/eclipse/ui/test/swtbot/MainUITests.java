/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.ui.test.swtbot;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.eclipse.ceylon.ide.eclipse.ui.test.AbstractMultiProjectTest;
import org.eclipse.ceylon.ide.eclipse.ui.test.Utils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MainUITests extends AbstractMultiProjectTest {
    
    private static SWTWorkbenchBot  bot;
    
    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        bot= Utils.createBot();
        
        importAndBuild();
    }
    
    @After
    public void after() {
        bot.closeAllEditors();
    }
    
    static protected abstract class NavigationStep {
        Pattern pattern;
        int offset;
        String titleOfOpenedEditor;

        public NavigationStep(String match, int offset, String titleOfOpenedEditor) {
            this(Pattern.compile(match, Pattern.LITERAL), offset, titleOfOpenedEditor);
        }
        public NavigationStep(Pattern pattern, int offset, String titleOfOpenedEditor) {
            this.pattern = pattern;
            this.offset = offset;
            this.titleOfOpenedEditor = titleOfOpenedEditor;
        }
        
        abstract void navigateToCurrentDeclaration(SWTBotEclipseEditor editor);
    }

    static protected class CtrlClick extends NavigationStep {
        public CtrlClick(String match, int offset, String titleOfOpenedEditor) {
            super(match, offset, titleOfOpenedEditor);
        }
        public CtrlClick(Pattern pattern, int offset, String titleOfOpenedEditor) {
            super(pattern, offset, titleOfOpenedEditor);
        }
        void navigateToCurrentDeclaration(SWTBotEclipseEditor editor) {
            Utils.ctrlClick(editor);
        }
    }
    
    static protected class GotoDeclaration extends NavigationStep {
        public GotoDeclaration(String match, int offset, String titleOfOpenedEditor) {
            super(match, offset, titleOfOpenedEditor);
        }
        public GotoDeclaration(Pattern pattern, int offset, String titleOfOpenedEditor) {
            super(pattern, offset, titleOfOpenedEditor);
        }
        void navigateToCurrentDeclaration(SWTBotEclipseEditor editor) {
            editor.pressShortcut(Keystrokes.F3);
        }
    }

    public void navigationTest(IFile initialFile, NavigationStep... navigationSteps) {
        Utils.openInEditor(initialFile);
        SWTBotEclipseEditor editor = Utils.showEditorByTitle(bot, initialFile.getName());
        for (NavigationStep step : navigationSteps) {
            bot.sleep(500);
            editor.navigateTo(Utils.positionInTextEditor(editor, step.pattern, step.offset));
            bot.sleep(500);
            step.navigateToCurrentDeclaration(editor);
            bot.sleep(500);
            editor = Utils.showEditorByTitle(bot, step.titleOfOpenedEditor);
        }
    }

    @Test
    public void gotoDeclarationFromLanguageModuleSource() {
        navigationTest(mainProject.getFile("src/mainModule/run.ceylon"),
                new GotoDeclaration("doc ", 1, "annotations.ceylon"),
                new GotoDeclaration(Pattern.compile("\\bClassDeclaration\\b"), 5, "ClassDeclaration.ceylon")
        );
    }

    
    @Test
    public void ctrlClickDeclarationFromLanguageModuleSource() {
        navigationTest(mainProject.getFile("src/mainModule/run.ceylon"),
                new CtrlClick("doc ", 1, "annotations.ceylon"),
                new CtrlClick(Pattern.compile("\\bClassDeclaration\\b"), 5, "ClassDeclaration.ceylon")
        );
    }
    
    @Test
    public void gotoDeclarationOnJavaLangAdditions() throws CoreException {
        IFile initialFile = copyFileFromResources("navigateToSource", "mainModule/navigateToJavaLangAdditions.ceylon", mainProject, "src");
        try {
            navigationTest(initialFile,
                    new GotoDeclaration("ObjectArray", 3, "ObjectArray.class")
            );
            navigationTest(initialFile,
                    new GotoDeclaration("ByteArray", 3, "ByteArray.class")
            );
            navigationTest(initialFile,
                    new GotoDeclaration("IntArray", 3, "IntArray.class")
            );
            navigationTest(initialFile,
                    new GotoDeclaration("ShortArray", 3, "ShortArray.class")
            );
            navigationTest(initialFile,
                    new GotoDeclaration("FloatArray", 3, "FloatArray.class")
            );
            navigationTest(initialFile,
                    new GotoDeclaration("DoubleArray", 3, "DoubleArray.class")
            );
        } finally {
            initialFile.delete(true, null);
        }
    }

    
    @Test
    public void ctrlClickOnJavaLangAdditions() throws CoreException {
        IFile initialFile = copyFileFromResources("navigateToSource", "mainModule/navigateToJavaLangAdditions.ceylon", mainProject, "src");
        try {
            navigationTest(initialFile,
                    new CtrlClick("ObjectArray", 3, "ObjectArray.class")
            );
            navigationTest(initialFile,
                    new CtrlClick("ByteArray", 3, "ByteArray.class")
            );
            navigationTest(initialFile,
                    new CtrlClick("IntArray", 3, "IntArray.class")
            );
            navigationTest(initialFile,
                    new CtrlClick("ShortArray", 3, "ShortArray.class")
            );
            navigationTest(initialFile,
                    new CtrlClick("FloatArray", 3, "FloatArray.class")
            );
            navigationTest(initialFile,
                    new CtrlClick("DoubleArray", 3, "DoubleArray.class")
            );
        } finally {
            initialFile.delete(true, null);
        }
    }
}
