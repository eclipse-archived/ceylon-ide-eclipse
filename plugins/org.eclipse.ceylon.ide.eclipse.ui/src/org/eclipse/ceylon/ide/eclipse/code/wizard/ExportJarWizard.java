/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.wizard;

import static org.eclipse.ceylon.cmr.ceylon.CeylonUtils.repoManager;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.ModuleImportSelectionDialog.selectModules;
import static org.eclipse.ceylon.ide.eclipse.code.wizard.WizardUtil.getSelectedJavaElement;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import org.eclipse.ceylon.cmr.api.ModuleQuery;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult;
import org.eclipse.ceylon.cmr.impl.ShaSigner;
import org.eclipse.ceylon.common.Versions;
import org.eclipse.ceylon.common.log.Logger;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.ide.eclipse.code.preferences.ModuleImportContentProvider;
import org.eclipse.ceylon.ide.eclipse.code.preferences.ModuleImportSelectionDialog;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.EclipseLogger;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleVersionNode;

public class ExportJarWizard extends Wizard implements IExportWizard {

    private IStructuredSelection selection;
    private ExportJarWizardPage page;
    private ImportModulesWizardPage importsPage;
    
    public ExportJarWizard() {
        setDialogSettings(CeylonPlugin.getInstance().getDialogSettings());
        setWindowTitle("Export Java Archive");
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            IJavaElement selectedElement = getSelectedJavaElement(selection);
            String repoPath=null;
            IJavaProject project=null;
            if (selectedElement!=null) {
                project = selectedElement.getJavaProject();
                /*List<String> paths = getCeylonRepositories(project.getProject());
                if (paths!=null) {
                    for (int i=paths.size()-1; i>=0; i--) {
                        String path = paths.get(i);
                        if (!path.startsWith("http://")) {
                            repoPath = path;
                            break;
                        }
                    }
                }*/
            }
            if (repoPath==null) repoPath = getDefaultRepositoryPath();
            page = new ExportJarWizardPage(repoPath, project, selectedElement);
            //page.init(selection);
        }
        if (importsPage == null) {
            importsPage = new ImportModulesWizardPage() {
                IProject getProject() {
                    return page.getProject()==null ? 
                            null : page.getProject().getProject();
                }
                @Override
                Map<String, ModuleVersionNode> getModules() {
                    return selectModules(new ModuleImportSelectionDialog(getShell(), 
                            new ModuleImportContentProvider(null, getProject()) {
                        @Override
                        public ModuleSearchResult getModules(String prefix) {
                            ModuleQuery query = new ModuleQuery(prefix, ModuleQuery.Type.JVM);
                            query.setJvmBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
                            query.setJvmBinaryMinor(Versions.JVM_BINARY_MINOR_VERSION);
                            query.setJsBinaryMajor(Versions.JS_BINARY_MAJOR_VERSION);
                            query.setJsBinaryMinor(Versions.JS_BINARY_MINOR_VERSION);
                            return repoManager().logger(new EclipseLogger()).isJDKIncluded(true)
                                    .buildManager().completeModules(query);
                        }
                    }), getProject());
                }
            };
        }
        addPage(page);
        addPage(importsPage);
    }
    
    public static String getDefaultRepositoryPath() {
        String repositoryPath = CeylonPlugin.getInstance().getDialogSettings()
                .get("repositoryPath");
        if (repositoryPath==null || repositoryPath.startsWith("http://")) {
            repositoryPath = System.getProperty("user.home") + "/.ceylon/repo";
        }
        return repositoryPath;
    }
    
    private Exception ex;
    
    @Override
    public boolean performFinish() {
        final String name = page.getModuleName();
        final String version = page.getVersion();
        final String dir = name.replace('.', File.separatorChar) + File.separatorChar + version;
        final String fileName = name + "-" + version + ".jar";
        final String xml = renderModuleDescriptor(name, version, fileName);
        final String repositoryPath = page.getRepositoryPath();
//        final IJavaProject project = page.getProject();
//        if (project==null) {
//            MessageDialog.openError(getShell(), "Export Java Archive Error", 
//                    "No Java project selected.");
//            return false;
//        }
//        else {
            /*IProject project = javaProject.getProject();
            List<PhasedUnit> list = CeylonBuilder.getProjectTypeChecker(project)
                .getPhasedUnits().getPhasedUnits();
            Set<String> moduleNames = new HashSet<String>();
            for (PhasedUnit phasedUnit: list) {
                Module module = phasedUnit.getUnit().getPackage().getModule();
                moduleNames.add(module.getNameAsString());
            }*/
            ex = null;
//            TableItem[] selectedItems = page.getModules().getSelection();
//            final String[] selectedModules = new String[selectedItems.length];
//            for (int i=0; i<selectedItems.length; i++) {
//                selectedModules[i] = selectedItems[i].getText();
//            }
            try {
                Job job = new Job("Exporting Java archive") {
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        Path jarPath = Paths.get(page.getJarPath());
                        Path repoPath = Paths.get(repositoryPath);
                        if (!Files.exists(repoPath)) {
                            try {
                                Files.createDirectories(repoPath);
                            } catch (IOException e) {
                                MessageDialog.openError(getShell(), "Export Java Archive Error", 
                                        "No repository at location: " + repositoryPath);
                                return Status.CANCEL_STATUS;
                            }
                        }
                        Path repoModulePath = repoPath.resolve(dir);
                        try {
                            Files.createDirectories(repoModulePath);
                            Path targetPath = repoModulePath.resolve(fileName);
                            Files.copy(jarPath, targetPath, REPLACE_EXISTING);
                            Logger log = new Logger() {
                                @Override
                                public void warning(String str) {
                                    // TODO Auto-generated method stub
                                }
                                @Override
                                public void info(String str) {
                                    // TODO Auto-generated method stub
                                }
                                @Override
                                public void error(String str) {
                                    // TODO Auto-generated method stub
                                }
                                @Override
                                public void debug(String str) {
                                    // TODO Auto-generated method stub
                                }
                            };
                            ShaSigner.sign(targetPath.toFile(), log, false);
                            Path descriptorPath = repoModulePath.resolve("module.xml");
                            OutputStream stream = Files.newOutputStream(descriptorPath);
                            try {
                                OutputStreamWriter writer = new OutputStreamWriter(stream);
                                writer.append(xml);
                                writer.flush();
                            }
                            finally {
                                stream.close();
                            }
                            
                        }
                        catch (Exception e) {
                            ex = e;
                            return Status.CANCEL_STATUS;
                        }
                        return Status.OK_STATUS;
                    }
                };
                PlatformUI.getWorkbench().getProgressService().showInDialog(getShell(), job);
                job.setUser(true);
                job.schedule();
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
            if (ex!=null) {
                ex.printStackTrace();
                MessageDialog.openError(getShell(), "Export Java Archive Error", 
                        "Error occurred exporting Java archive: " + ex.getMessage());
            }
            persistDefaultRepositoryPath(repositoryPath);
//        }
        return true;
    }

    public String renderModuleDescriptor(final String name,
            final String version, final String fileName) {
        StringBuilder builder = new StringBuilder();
        String newline = System.lineSeparator();
        builder.append("<module xmlns=\"urn:jboss:module:1.1\" name=\"")
                .append(name)
                .append("\" slot=\"")
                .append(version)
                .append("\">")
                .append(newline)
                .append("    ")
                .append("<resources>")
                .append(newline)
                .append("        ")
                .append("<resource-root path=\"")
                .append(fileName)
                .append("\"/>")
                .append(newline)
                .append("    ")
                .append("</resources>")
                .append(newline)
                .append("    ")
                .append("<dependencies>")
                .append(newline);
        for (Map.Entry<String, String> entry: 
            importsPage.getImports().entrySet()) {
            if (name.equals(Module.LANGUAGE_MODULE_NAME)) continue;
            builder.append("        ")
                    .append("<module name=\"")
                    .append(entry.getKey())
                    .append("\" slot=\"")
                    .append(entry.getValue())
                    .append("\"");
            if (importsPage.getSharedImports().contains(entry.getKey())) {
                builder.append(" export=\"true\"");
            }
            builder.append("/>")
                    .append(newline);
        }
        builder.append("    ")
                .append("</dependencies>")
                .append(newline)
                .append("</module>");
        return builder.toString();
    }
    
    public static void persistDefaultRepositoryPath(String repositoryPath) {
        if (repositoryPath!=null && !repositoryPath.isEmpty()) {
            CeylonPlugin.getInstance().getDialogSettings()
                    .put("repositoryPath", repositoryPath);
        }
    }
    
    /*public static void copyFolder(File src, File dest)
            throws IOException{
        if (src.isDirectory()) {
            if ( !dest.exists() ) dest.mkdir();
            for (String file: src.list()) {
               File srcFile = new File(src, file);
               File destFile = new File(dest, file);
               copyFolder(srcFile, destFile);
            }
        }
        else if (src.getName().endsWith(".car") ||
                src.getName().endsWith(".src") ||
                src.getName().endsWith(".sha1")) {
            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(src).getChannel();
                destination = new FileOutputStream(dest).getChannel();
                destination.transferFrom(source, 0, source.size());
            }
            finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
            System.out.println("Archive exported from " + src + " to " + dest);
        }
    }*/
    
    @Override
    public boolean canFinish() {
        return page.isPageComplete();
    }
    
}
