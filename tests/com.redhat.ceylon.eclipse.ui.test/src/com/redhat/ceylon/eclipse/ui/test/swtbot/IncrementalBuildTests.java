package com.redhat.ceylon.eclipse.ui.test.swtbot;


import static com.redhat.ceylon.eclipse.ui.test.Utils.openInEditor;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.Position;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.redhat.ceylon.eclipse.ui.test.AbstractMultiProjectTest;
import com.redhat.ceylon.eclipse.ui.test.Utils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class IncrementalBuildTests extends AbstractMultiProjectTest {
    private static SWTWorkbenchBot  bot;
    
    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        bot = Utils.createBot();
        importAndBuild();
    }
    
    @After
    public void resetWorkbench() {
        Utils.resetWorkbench(bot);
    }
    
    @Test
    public void bug589_AddedJavaMethodNotSeen() throws InterruptedException, CoreException {
        openInEditor(mainProject, "src/mainModule/run.ceylon");
        openInEditor(mainProject, "javaSrc/mainModule/JavaClassInCeylonModule_Main_Ceylon_Project.java");
        
        SWTBotEclipseEditor javaFileEditor = Utils.showEditorByTitle(bot, "JavaClassInCeylonModule_Main_Ceylon_Project.java");
        Position javaClassDeclarationPosition = Utils.positionInTextEditor(javaFileEditor, "public class JavaClassInCeylonModule_Main_Ceylon_Project", 0);
        String javaEditorText = javaFileEditor.getText();
        javaFileEditor.insertText(javaClassDeclarationPosition.line + 1, 0, "public void newMethodToTest() {}\n");
        
        Utils.CeylonBuildSummary buildSummary = new Utils.CeylonBuildSummary(mainProject);
        buildSummary.install();
        javaFileEditor.save();
        try {
            buildSummary.waitForBuildEnd(30);
            
            SWTBotEclipseEditor ceylonFileEditor = Utils.showEditorByTitle(bot, "run.ceylon");
            Position javaClassUsePosition = Utils.positionInTextEditor(ceylonFileEditor, "value v5 = JavaClassInCeylonModule_Main_Ceylon_Project();", 0);
            String ceylonEditorText = ceylonFileEditor.getText();
            ceylonFileEditor.insertText(javaClassUsePosition.line + 1, 0,"v5.newMethodToTest();\n");
            
            /*
            ceylonFileEditor.navigateTo(18, 3);
            List<String> proposals = javaFileEditor.getAutoCompleteProposals("");
            assertThat("The new method of the Java class should be proposed",
                   proposals,
                   new IsCollectionContaining(new IsEqual("test()")));
                   */
            
            buildSummary = new Utils.CeylonBuildSummary(mainProject);
            buildSummary.install();
            ceylonFileEditor.save();
            try {
                buildSummary.waitForBuildEnd(30);
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

    @Test
    public void bug821_AddedJavaClassNotSeen() throws InterruptedException, CoreException {

        Utils.CeylonBuildSummary buildSummary = new Utils.CeylonBuildSummary(mainProject);
        buildSummary.install();
        IFile useFile = copyFileFromResources("bug821", "mainModule/Use.ceylon", mainProject, "src");        
        try {
            buildSummary.waitForBuildEnd(30);
            assertThat("The build should have an error",
                    Utils.getProjectErrorMarkers(mainProject),
                    Matchers.hasItem(stringContainsInOrder(Arrays.asList("src/mainModule/Use.ceylon", "l.2","type declaration does not exist"))));
            
            buildSummary = new Utils.CeylonBuildSummary(mainProject);
            buildSummary.install();
            IFile declarationFile = copyFileFromResources("bug821", "mainModule/UsedDeclaration.java", mainProject, "javaSrc");
            try {
                buildSummary.waitForBuildEnd(30);
                assertThat("The build should not have any error",
                        Utils.getProjectErrorMarkers(mainProject),
                        Matchers.empty());
            }
            finally {
                declarationFile.delete(true, null);
            }
        }
        finally {
            useFile.delete(true, null);
        }
    }
    
   public void changeAndRestoreDeclaration(IProject declarationProject, String path, String declarationMatch, String prefixBeforeDeclaration, Matcher<? super String> expectedErrorMatcher) throws InterruptedException, CoreException {
       String fileName = new Path(path).lastSegment();
       openInEditor(declarationProject, path);
       SWTBotEclipseEditor editor = Utils.showEditorByTitle(bot, fileName);
       editor.setFocus();
       String originalText = editor.getText();
       Position position = Utils.positionInTextEditor(editor, declarationMatch, 0);
       editor.insertText(position.line, position.column, prefixBeforeDeclaration);
       Utils.CeylonBuildSummary buildSummary = new Utils.CeylonBuildSummary(mainProject);
       buildSummary.install();
       editor.save();
       try {
           buildSummary.waitForBuildEnd(30);
           assertThat("The build should have an error",
                   Utils.getProjectErrorMarkers(mainProject),
                   Matchers.hasItem(expectedErrorMatcher));
           editor.setText("");
           editor.insertText(0, 0, originalText);
           buildSummary = new Utils.CeylonBuildSummary(mainProject);
           buildSummary.install();
           editor.save();
           editor = null;
           buildSummary.waitForBuildEnd(30);
           assertThat("The build should not have any error",
                   Utils.getProjectErrorMarkers(mainProject),
                   Matchers.empty());
       }
       catch(Throwable e) {
           declarationProject.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
           throw e;
       }
       finally { 
           if (editor != null) {
               editor.setText(originalText);
               editor.save();
           }
       }
   }

   @Test
   public void removeAndRestore_CeylonClass_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "src/usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
               "CeylonTopLevelClass_Main_Ceylon_Project", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'CeylonTopLevelClass_Main_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonToplevelObject_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "src/usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
               "ceylonTopLevelObject_Main_Ceylon_Project", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "function or value does not exist: 'ceylonTopLevelObject_Main_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonToplevelMethod_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "src/usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
               "ceylonTopLevelMethod_Main_Ceylon_Project", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "function or value does not exist: 'ceylonTopLevelMethod_Main_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonJavaClass_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "javaSrc/mainModule/JavaCeylonTopLevelClass_Main_Ceylon_Project.java", 
               "JavaCeylonTopLevelClass_Main_Ceylon_Project", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'JavaCeylonTopLevelClass_Main_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonJavaToplevelObject_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "javaSrc/mainModule/javaCeylonTopLevelObject_Main_Ceylon_Project_.java", 
               "javaCeylonTopLevelObject_Main_Ceylon_Project_", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "function or value does not exist: 'javaCeylonTopLevelObject_Main_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonJavaToplevelMethod_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "javaSrc/mainModule/javaCeylonTopLevelMethod_Main_Ceylon_Project_.java", 
               "javaCeylonTopLevelMethod_Main_Ceylon_Project_", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "function or value does not exist: 'javaCeylonTopLevelMethod_Main_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_PureJavaClass_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "javaSrc/mainModule/JavaClassInCeylonModule_Main_Ceylon_Project.java", 
               "JavaClassInCeylonModule_Main_Ceylon_Project", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'JavaClassInCeylonModule_Main_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_PureJavaSecondaryClass_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "javaSrc/mainModule/JavaClassInCeylonModule_Main_Ceylon_Project.java", 
               "JavaSecondaryClassInCeylonModule_Main_Ceylon_Project", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'JavaSecondaryClassInCeylonModule_Main_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonClass_Method_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "src/usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
               "method", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "method or attribute does not exist: 'method'")));
   }

   @Test
   public void removeAndRestore_CeylonClass_Attribute_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "src/usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
               "attribute", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "method or attribute does not exist: 'attribute'")));
   }

   @Test
   public void removeAndRestore_CeylonClass_InnerClass_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "src/usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
               "InnerClass", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'InnerClass'")));
   }

   @Test
   public void removeAndRestore_CeylonClass_Object_InSameProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(mainProject, 
               "src/usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
               "obj {}", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "method or attribute does not exist: 'obj'")));
   }
   
   
   @Test
   public void removeAndRestore_CeylonClass_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "src/referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
               "CeylonTopLevelClass_Referenced_Ceylon_Project", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'CeylonTopLevelClass_Referenced_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonToplevelObject_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "src/referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
               "ceylonTopLevelObject_Referenced_Ceylon_Project", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "function or value does not exist: 'ceylonTopLevelObject_Referenced_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonToplevelMethod_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "src/referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
               "ceylonTopLevelMethod_Referenced_Ceylon_Project", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "function or value does not exist: 'ceylonTopLevelMethod_Referenced_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonJavaClass_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "javaSrc/referencedCeylonProject/JavaCeylonTopLevelClass_Referenced_Ceylon_Project.java", 
               "JavaCeylonTopLevelClass_Referenced_Ceylon_Project", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'JavaCeylonTopLevelClass_Referenced_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonJavaToplevelObject_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "javaSrc/referencedCeylonProject/javaCeylonTopLevelObject_Referenced_Ceylon_Project_.java", 
               "javaCeylonTopLevelObject_Referenced_Ceylon_Project_", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "function or value does not exist: 'javaCeylonTopLevelObject_Referenced_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonJavaToplevelMethod_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "javaSrc/referencedCeylonProject/javaCeylonTopLevelMethod_Referenced_Ceylon_Project_.java", 
               "javaCeylonTopLevelMethod_Referenced_Ceylon_Project_", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "function or value does not exist: 'javaCeylonTopLevelMethod_Referenced_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_PureJavaClass_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "javaSrc/referencedCeylonProject/JavaClassInCeylonModule_Referenced_Ceylon_Project.java", 
               "JavaClassInCeylonModule_Referenced_Ceylon_Project", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'JavaClassInCeylonModule_Referenced_Ceylon_Project'")));
   }

   @Test
   public void removeAndRestore_CeylonClass_Method_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "src/referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
               "method", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "method or attribute does not exist: 'method'")));
   }

   @Test
   public void removeAndRestore_CeylonClass_Attribute_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "src/referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
               "attribute", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "method or attribute does not exist: 'attribute'")));
   }

   @Test
   public void removeAndRestore_CeylonClass_InnerClass_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "src/referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
               "InnerClass", "Z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "type does not exist: 'InnerClass'")));
   }

   @Test
   public void removeAndRestore_CeylonClass_Object_InReferencedProject() throws InterruptedException, CoreException {
       changeAndRestoreDeclaration(referencedCeylonProject, 
               "src/referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
               "obj {}", "z_", 
               stringContainsInOrder(Arrays.asList("src/mainModule/run.ceylon", "method or attribute does not exist: 'obj'")));
   }
}
