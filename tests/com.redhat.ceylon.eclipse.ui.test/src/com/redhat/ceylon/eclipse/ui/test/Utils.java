package com.redhat.ceylon.eclipse.ui.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IBuildContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;

public class Utils {

    public static IProject importProject(final IWorkspace workspace,
            final String destinationRootPath,
            final IPath projectDescriptionPath) throws CoreException,
            InvocationTargetException, InterruptedException {

        final IProjectDescription originalProjectDescription =
                workspace.loadProjectDescription(projectDescriptionPath);
        return importProject(workspace, destinationRootPath,
                originalProjectDescription);
    }

    public static void openInEditor(IProject project, String fileName) {
        final IFile runFile = project.getFile(fileName);
        openInEditor(runFile);
    }

    public static void openInEditor(final IFile runFile) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                try {
                	EditorUtil.gotoLocation(runFile, 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static IProject importProject(final IWorkspace workspace,
            final String destinationRootPath,
            final IProjectDescription originalProjectDescription)
            throws InvocationTargetException, InterruptedException {
        final String projectName = originalProjectDescription.getName();

        WorkspaceModifyOperation importOperation = new WorkspaceModifyOperation() {
            @Override
            protected void execute(IProgressMonitor monitor) throws CoreException,
                    InvocationTargetException, InterruptedException {

                final IProject project = workspace.getRoot().getProject(projectName);
            
                // import from file system
                File importSource = null;
            
                // import project from location copying files - use default project
                // location for this workspace
                URI locationURI = originalProjectDescription.getLocationURI();
                // if location is null, project already exists in this location or
                // some error condition occured.
                IProjectDescription newProjectDescription = originalProjectDescription;
                if (locationURI != null) {
                    importSource = new File(locationURI);
                    newProjectDescription = workspace
                            .newProjectDescription(projectName);
                    newProjectDescription.setBuildSpec(originalProjectDescription.getBuildSpec());
                    newProjectDescription.setComment(originalProjectDescription.getComment());
                    newProjectDescription.setDynamicReferences(originalProjectDescription
                            .getDynamicReferences());
                    newProjectDescription.setNatureIds(originalProjectDescription.getNatureIds());
                    newProjectDescription.setReferencedProjects(originalProjectDescription
                            .getReferencedProjects());
                    newProjectDescription.setLocation(workspace.getRoot().getLocation().append(destinationRootPath + "/" + projectName + "/"));
                }
            
                project.create(newProjectDescription, null);
                project.open(IResource.NONE, null);

                // import operation to import project files if copy checkbox is selected
                if (importSource != null) {
                    List filesToImport = FileSystemStructureProvider.INSTANCE
                            .getChildren(importSource);
                    ImportOperation operation = new ImportOperation(project
                            .getFullPath(), importSource,
                            FileSystemStructureProvider.INSTANCE, new IOverwriteQuery() {
                                @Override
                                public String queryOverwrite(String pathString) {
                                    return IOverwriteQuery.ALL;
                                }
                    }, filesToImport);
                    operation.setContext(null);
                    operation.setOverwriteResources(true); // need to overwrite
                    // .project, .classpath
                    // files
                    operation.setCreateContainerStructure(false);
                    operation.run(null);
                }
                
            }
        };
 
        importOperation.run(null);
        return workspace.getRoot().getProject(projectName);
    }

    public static SWTWorkbenchBot createBot() {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        try {
            bot.viewByTitle("Welcome").close();
        }
        catch(WidgetNotFoundException e) {}
        return bot;
    }

    public static void resetWorkbench(SWTWorkbenchBot bot) {
        bot.closeAllEditors();
        bot.resetWorkbench();
    }

    public static Collection<String> getProjectErrorMarkers(IProject project) throws CoreException {
        IMarker[] allProblems = project.findMarkers(CeylonBuilder.PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
        List<String> errors = new ArrayList<String>();
        for (IMarker marker : allProblems) {
            if (marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO) >= IMarker.SEVERITY_ERROR) {
                String errorToAdd = marker.getResource().getProjectRelativePath() +
                        " (l."+
                        marker.getAttribute(IMarker.LINE_NUMBER, 0) +
                        ") : " +
                        marker.getAttribute(IMarker.MESSAGE, "");

                errors.add(errorToAdd);
            }
        }
        return errors;
    }
    
    public static class CeylonBuildSummary extends CeylonBuildHook {
        private CeylonBuildHook ceylonBuildHookToRestore;
        
        private boolean installed = false;
        private CountDownLatch firstBuildLatch = new CountDownLatch(1);
        private int kind;
        private Map args;
        private IProject project;
        private Collection<IClasspathContainer> resolvedCpContainers;
        private boolean cpContainersSetAndRefreshed = false;
        private boolean fullBuild = false;
        private boolean ceylonModelParsedDuringFullBuild = false;
        private boolean incrementalBuild = false;
        private boolean fullTypeCheckDuringIncrementalBuild = false;
        private Collection<IFile> incrementalBuildChangedSources;
        private Collection<IFile> incrementalBuildfilesToRemove;
        private Collection<IFile> incrementalBuildSourcesToCompile;
        private Collection<PhasedUnit> incrementalBuildResultPhasedUnits;
        private CeylonBuildSummary reentrantBuildSummary;
        private boolean scheduledReentrantBuild = false;
        private List<CeylonBuildSummary> previousBuilds =new ArrayList<>();
        private boolean isReentrant = false;
        
        private CeylonBuildSummary summaryToFill = this;

        private IBuildConfiguration configuration;
        private IBuildConfiguration[] requestConfigurations;
        private IBuildConfiguration[] referencedconfigurations;
        private IBuildConfiguration[] referencingconfigurations; 

        public CeylonBuildSummary(IProject project) {
            this.project = project;
        }

        public void install() {
            CeylonBuildHook previousHook = CeylonBuilder.replaceHook(this);
            installed = true;
            if (ceylonBuildHookToRestore == null) {
                ceylonBuildHookToRestore = previousHook;
            }
        }
        
        @Override
        protected void startBuild(int kind, Map args, IProject project, 
                IBuildConfiguration config, IBuildContext context, IProgressMonitor monitor) {
            if (! this.project.equals(project)) {
                summaryToFill = new CeylonBuildSummary(project);
                previousBuilds.add(summaryToFill);
            } else {
                summaryToFill = this;
            }
            summaryToFill.kind = kind;
            summaryToFill.args = args;
            summaryToFill.configuration = config;
            summaryToFill.requestConfigurations = context.getRequestedConfigs();
            summaryToFill.referencedconfigurations = context.getAllReferencedBuildConfigs();
            summaryToFill.referencingconfigurations = context.getAllReferencingBuildConfigs();
        }
        
        protected void resolvingClasspathContainer(
                List<IClasspathContainer> cpContainers) {
            summaryToFill.resolvedCpContainers = cpContainers;
        }
        protected void setAndRefreshClasspathContainer() {
            summaryToFill.cpContainersSetAndRefreshed = true;
        }
        protected void doFullBuild() {
            summaryToFill.fullBuild = true;
        }
        protected void parseCeylonModel() {
            summaryToFill.ceylonModelParsedDuringFullBuild = true;
        }
        protected void doIncrementalBuild() {
            summaryToFill.incrementalBuild = true;
        }
        protected void fullTypeCheckDuringIncrementalBuild() {
            summaryToFill.fullTypeCheckDuringIncrementalBuild = true;
        }
        protected void incrementalBuildSources(Set<IFile> changedSources,
                List<IFile> filesToRemove, Collection<IFile> sourcesToCompile) {
            summaryToFill.incrementalBuildChangedSources = changedSources;
            summaryToFill.incrementalBuildfilesToRemove = filesToRemove;
            summaryToFill.incrementalBuildSourcesToCompile = sourcesToCompile;
        }
        protected void incrementalBuildResult(List<PhasedUnit> builtPhasedUnits) {
            summaryToFill.incrementalBuildResultPhasedUnits = builtPhasedUnits;
        }
        
        protected CeylonBuildSummary createReentrantBuildSummary(IProject project) {
            return new CeylonBuildSummary(project);
        }
        
        @Override
        protected void scheduleReentrantBuild() {
            summaryToFill.scheduledReentrantBuild = true;
            if (summaryToFill == this) {
                reentrantBuildSummary = createReentrantBuildSummary(project);
                reentrantBuildSummary.isReentrant = true;
                reentrantBuildSummary.ceylonBuildHookToRestore = ceylonBuildHookToRestore;
            }
        }
        
        @Override
        protected void endBuild() {
            if (summaryToFill == this) {
                if (reentrantBuildSummary != null) {
                    reentrantBuildSummary.install();
                }
                firstBuildLatch.countDown();
                CeylonBuilder.replaceHook(ceylonBuildHookToRestore);
            }
        }
        
        public boolean waitForBuildEnd(long timeoutInSeconds) throws InterruptedException {
            if (!installed) {
                throw new RuntimeException("Cannot wait for a build with a non-installed hook !");
            }
            try {
                if (firstBuildLatch.await(timeoutInSeconds, TimeUnit.SECONDS)) {
                    if (reentrantBuildSummary != null) {
                        reentrantBuildSummary.waitForBuildEnd(timeoutInSeconds);
                    }
                    return true;
                } else {
                    if (reentrantBuildSummary != null) {
                        reentrantBuildSummary.endBuild();
                    }
                    return false;
                }
            }
            finally {
                System.out.println(toString());
            }
        }

        public final int getKind() {
            return kind;
        }

        public final Map getArgs() {
            return args;
        }

        public final IProject getProject() {
            return project;
        }

        public final boolean didTriggerReentrantBuild() {
            return scheduledReentrantBuild;
        }
        
        public final CeylonBuildSummary getReentrantBuildSummary() {
            return reentrantBuildSummary;
        }

        public final boolean didResolveCpContainers() {
            return resolvedCpContainers != null;
        }

        public final Collection<IClasspathContainer> getResolvedCpContainers() {
            return resolvedCpContainers;
        }

        public final boolean didSetAndRefreshedCpContainers() {
            return cpContainersSetAndRefreshed;
        }

        public final boolean didFullBuild() {
            return fullBuild;
        }

        public final boolean didParseCeylonModelDuringFullBuild() {
            return ceylonModelParsedDuringFullBuild;
        }

        public final boolean didIncrementalBuild() {
            return incrementalBuild;
        }

        public final boolean didFullTypeCheckDuringIncrementalBuild() {
            return fullTypeCheckDuringIncrementalBuild;
        }

        public final Collection<IFile> getIncrementalBuildChangedSources() {
            return incrementalBuildChangedSources;
        }

        public final Collection<IFile> getIncrementalBuildfilesToRemove() {
            return incrementalBuildfilesToRemove;
        }

        public final Collection<IFile> getIncrementalBuildSourcesToCompile() { 
            return incrementalBuildSourcesToCompile;
        }

        public final Collection<PhasedUnit> getIncrementalBuildResultPhasedUnits() {
            return incrementalBuildResultPhasedUnits;
        }

        public List<CeylonBuildSummary> getPreviousBuilds() {
            return previousBuilds;
        }

        public IBuildConfiguration getConfiguration() {
            return configuration;
        }

        public IBuildConfiguration[] getRequestConfigurations() {
            return requestConfigurations;
        }

        public IBuildConfiguration[] getReferencedconfigurations() {
            return referencedconfigurations;
        }

        public IBuildConfiguration[] getReferencingconfigurations() {
            return referencingconfigurations;
        }

        public void setPreviousBuilds(List<CeylonBuildSummary> previousBuilds) {
            this.previousBuilds = previousBuilds;
        }
        
        @Override
        public String toString() {
            String result = "";
            for (CeylonBuildSummary previousSummary : getPreviousBuilds()) {
                result += previousSummary + "\n";
            }            
            result += 
                "Project : " + project + "\n" +
                (   ! didFullBuild() && ! didIncrementalBuild() ? 
                        "  No Build performed !\n"
                    :
                        "  Kind : " + kind + "\n" +
                        "  Args : " + args + "\n" +
                        "  Current Build Configuration : " + configuration + "\n" +                        
                        "  Requested Build Configurations : " + Arrays.asList(requestConfigurations) + "\n" +                        
                        "  Referenced Build Configurations : " + Arrays.asList(referencedconfigurations) + "\n" +                        
                        "  Referencing Build Configurations : " + Arrays.asList(referencingconfigurations) + "\n" +                        
                        "  Build Type : " + ( didFullBuild() ? "Full" : "Incremental" ) + "\n" +
                        "  Resolved Classpath Containers during Build : " + didResolveCpContainers() + "\n" +
                        "  Set and Refreshed Classpath Containers during Build : " + didSetAndRefreshedCpContainers() + "\n" +
                        (   didFullBuild() ?
                                "  Parsed Ceylon Model during Full Build : " + didParseCeylonModelDuringFullBuild() + "\n"
                            :  
                                "  Did a Full TypeCheck during Incremental Build : " + didFullTypeCheckDuringIncrementalBuild() + "\n" +
                                "  Changed Sources : " + getIncrementalBuildChangedSources() + "\n" +
                                "  Files To Remove : " + getIncrementalBuildfilesToRemove() + "\n" +
                                "  Sources To Compile : " + getIncrementalBuildSourcesToCompile() + "\n" +
                                "  Result Phased Units : " + getIncrementalBuildResultPhasedUnits() + "\n"
                        ) +
                        "  Triggered a reentrant Build : " + didTriggerReentrantBuild() + "\n"
                );
            if (didTriggerReentrantBuild() && installed) {
                result += "\nReentrant Builds :\n";
                for (CeylonBuildSummary previousSummary : getPreviousBuilds()) {
                    if (previousSummary.didTriggerReentrantBuild()) {
                        result += previousSummary.getReentrantBuildSummary() + "\n";
                    }
                }
                result += getReentrantBuildSummary() + "\n";
            }
            
            return result;
        }
    }

    public static CeylonBuildSummary buildProject(IProject project) throws CoreException,
    InterruptedException {
        CeylonBuildSummary summary = new CeylonBuildSummary(project);
        summary.install();
        project.build(IncrementalProjectBuilder.FULL_BUILD, null);
        summary.waitForBuildEnd(60);
        return summary;
    }
    

}
