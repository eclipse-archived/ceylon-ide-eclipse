package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.cmr.api.ArtifactContext.allSuffixes;
import static com.redhat.ceylon.eclipse.code.wizard.ExportModuleWizardPage.CLEAN_BUILD_BEFORE_EXPORT;
import static com.redhat.ceylon.eclipse.code.wizard.ExportModuleWizardPage.RECURSIVE_EXPORT;
import static com.redhat.ceylon.eclipse.code.wizard.WizardUtil.getSelectedJavaElement;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputDirectory;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getInterpolatedCeylonSystemRepo;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getReferencedProjectsOutputRepositories;
import static com.redhat.ceylon.eclipse.core.model.modelJ2C.ceylonModel;
import static org.eclipse.core.resources.IncrementalProjectBuilder.AUTO_BUILD;
import static org.eclipse.core.resources.IncrementalProjectBuilder.CLEAN_BUILD;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.ceylon.CeylonUtils;
import com.redhat.ceylon.cmr.ceylon.CeylonUtils.CeylonRepoManagerBuilder;
import com.redhat.ceylon.cmr.ceylon.ModuleCopycat;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.redhat.ceylon.ide.common.model.BaseCeylonProject;

public class ExportModuleWizard extends Wizard implements IExportWizard {

    private IStructuredSelection selection;
    private ExportModuleWizardPage page;
    
    public ExportModuleWizard() {
        setDialogSettings(CeylonPlugin.getInstance().getDialogSettings());
        setWindowTitle("Export Ceylon Module");
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
            IProject project=null;
            if (selectedElement!=null) {
                project = selectedElement.getJavaProject().getProject();
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
            page = new ExportModuleWizardPage(repoPath, project, selectedElement);
            //page.init(selection);
        }
        addPage(page);
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
        final String repositoryPath = page.getRepositoryPath();
        final IProject project = page.getProject();
        if (project==null) {
            MessageDialog.openError(getShell(), "Export Module Error", 
                    "No Java project selected.");
            return false;
        }

        final BaseCeylonProject ceylonProject = ceylonModel().getProject(project);
        if (ceylonProject == null) {
            MessageDialog.openError(getShell(), "Export Module Error", 
                    "The selected project is not a Ceylon project.");
            return false;
        }
        
        ex = null;
        List<TableItem> selectedItems = page.getModules();
        final String[] selectedModules = new String[selectedItems.size()];
        final String[] selectedVersions = new String[selectedItems.size()];
        for (int i=0; i<selectedItems.size(); i++) {
            selectedModules[i] = selectedItems.get(i).getText();
            selectedVersions[i] = selectedItems.get(i).getText(1);
        }
        try {
            Job job = new Job("Exporting modules") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    monitor.setTaskName("Exporting modules to repository");
                    getDialogSettings().put(CLEAN_BUILD_BEFORE_EXPORT, page.isClean());
                    getDialogSettings().put(RECURSIVE_EXPORT, page.isRecursive());
                    if (page.isClean()) {
                        try {
                            project.build(CLEAN_BUILD, monitor);
                            project.build(AUTO_BUILD, monitor);
                        }
                        catch (CoreException e) {
                            ex = e;
                            return Status.CANCEL_STATUS;
                        }
                        yieldRule(monitor);
                    }
                    try {
                        File cwd = project.getLocation().toFile();
                        String systemRepo = getInterpolatedCeylonSystemRepo(project);
                        boolean offline = ceylonProject.getConfiguration().getOffline();
                        String output = getCeylonModulesOutputDirectory(project).getAbsolutePath();
                        List<String> outputRepositories = 
                                getReferencedProjectsOutputRepositories(project);
                        outputRepositories.add(output);
                        CeylonUtils.CeylonRepoManagerBuilder rmb = 
                                CeylonUtils.repoManager()
                                .cwd(cwd)
                                .systemRepo(systemRepo)
                                .extraUserRepos(outputRepositories)
                                .offline(offline)
                                .logger(new EclipseLogger());
                        RepositoryManager repo = rmb.buildManager();
                        CeylonRepoManagerBuilder builder = 
                                CeylonUtils.repoManager()
                                .noDefaultRepos(true)
                                .systemRepo(systemRepo)
                                .outRepo(repositoryPath)
                                .offline(offline)
                                .logger(new EclipseLogger());
                        if (page.isRemote()) {
                            builder.user(page.getUser());
                            builder.password(page.getPass());
                        }
                        RepositoryManager outRepo = builder.buildOutputManager();
                        ModuleCopycat copycat = new ModuleCopycat(repo,outRepo);
                        for (int i=0; i<selectedModules.length; i++) {
                            String name = selectedModules[i];
                            String version = selectedVersions[i];
                            ArtifactContext artifactContext = 
                                    new ArtifactContext(name, version, allSuffixes());
                            artifactContext.setIgnoreDependencies(!page.isRecursive());
                            copycat.copyModule(artifactContext);
                        }
                    }
                    catch (Exception e) {
                        ex = e;
//                            e.printStackTrace();
                        return Status.CANCEL_STATUS;
                    }
                    /*File outputDir = getCeylonModulesOutputDirectory(project);
                    Path outputPath = Paths.get(outputDir.getAbsolutePath());
                    Path repoPath = Paths.get(repositoryPath);
                    if (!Files.exists(repoPath)) {
                        MessageDialog.openError(getShell(), "Export Module Error", 
                                "No repository at location: " + repositoryPath);
                        return Status.CANCEL_STATUS;
                    }
                    for (int i=0; i<selectedModules.length; i++) {
                        String name = selectedModules[i];
                        String version = selectedVersions[i];
                        String glob = name + '-' + version + "*";
                        String dir = name.replace('.', File.separatorChar) + File.separatorChar + version;
                        Path repoOutputPath = outputPath.resolve(dir);
                        Path repoModulePath = repoPath.resolve(dir);
                        try {
                            Files.createDirectories(repoModulePath);
                            DirectoryStream<Path> ds = Files.newDirectoryStream(repoOutputPath, glob);
                            try {
                                for (Path path: ds) {
                                    Files.copy(path, repoModulePath.resolve(path.getFileName()), 
                                            REPLACE_EXISTING);
                                }
                            }
                            finally {
                                ds.close();
                            }
                        }
                        catch (Exception e) {
                            ex = e;
                            return Status.CANCEL_STATUS;
                        }
                    }*/
                    return Status.OK_STATUS;
                }
            };
            job.setRule(getWorkspace().getRuleFactory().buildRule());
            getWorkbench().getProgressService().showInDialog(getShell(), job);
            job.setUser(true);
            job.schedule();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        if (ex!=null) {
            ex.printStackTrace();
            MessageDialog.openError(getShell(), "Export Module Error", 
                    "Error occurred exporting module: " + ex.getMessage());
        }
        persistDefaultRepositoryPath(repositoryPath);
        return true;
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
