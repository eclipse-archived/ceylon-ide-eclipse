/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.ui.test;

import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.asyncExec;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IBuildContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.Position;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.junit.Assert;

import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.ide.eclipse.code.editor.Navigation;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;

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
                	Navigation.gotoLocation(runFile, 0);
                } catch (Exception ex) {
                    System.err.println(ex);
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
                    @SuppressWarnings("rawtypes")
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

    /**
     * Create a mouse event
     *
     * @param x the x co-ordinate of the mouse event.
     * @param y the y co-ordinate of the mouse event.
     * @param button the mouse button that was clicked.
     * @param stateMask the state of the keyboard modifier keys.
     * @param count the number of times the mouse was clicked.
     * @return an event that encapsulates {@link #widget} and {@link #display}
     * @since 1.2
     */
    private static Event createMouseEvent(Widget widget, Display display, int x, int y, int button, int stateMask, int count) {
        Event event = new Event();
        event.time = (int) System.currentTimeMillis();
        event.widget = widget;
        event.display = display;
        event.x = x;
        event.y = y;
        event.button = button;
        event.stateMask = stateMask;
        event.count = count;
        return event;
    }

    /**
     * Sends a non-blocking notification of the specified type to the widget.
     *
     * @param eventType the type of event.
     * @param createEvent the event to be sent to the {@link #widget}.
     * @param widget the widget to send the event to.
     */
    public static void notify(final int eventType, final Event createEvent, final Widget widget, final Display display) {
        createEvent.type = eventType;
        UIThreadRunnable.asyncExec(display, new VoidResult() {
            public void run() {
                if ((widget == null) || widget.isDisposed()) {
                    return;
                }
                try {
                    if (! ((Boolean) SWTUtils.invokeMethod(widget, "isEnabled")).booleanValue()) {
                        return;
                    }
                } catch (Exception e) {
                }
                
                widget.notifyListeners(eventType, createEvent);
            }
        });

        UIThreadRunnable.syncExec(new VoidResult() {
            public void run() {
                // do nothing, just wait for sync.
            }
        });

        long playbackDelay = SWTBotPreferences.PLAYBACK_DELAY;
        if (playbackDelay > 0)
            SWTUtils.sleep(playbackDelay);
    }

    public static void resetWorkbench(SWTWorkbenchBot bot) {
        bot.closeAllEditors();
        bot.resetWorkbench();
    }

    public static Collection<String> getProjectErrorMarkers(IProject project) throws CoreException {
        project.refreshLocal(IResource.DEPTH_INFINITE, null);
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
    
    public static class PostBuildListener implements IResourceChangeListener {
        static private PostBuildListener _instance = null;
        
        public synchronized static PostBuildListener instance() {
            if (_instance == null) {
                _instance = new PostBuildListener();
                ResourcesPlugin.getWorkspace().addResourceChangeListener(
                        _instance, 
                        IResourceChangeEvent.POST_BUILD | 
                        IResourceChangeEvent.PRE_BUILD);
            }
            return _instance;
        }
        
        private CountDownLatch buildLatch = null;

        @Override
        public void resourceChanged(IResourceChangeEvent event) {
            if (event.getType() == IResourceChangeEvent.PRE_BUILD) {
                buildLatch = new CountDownLatch(1);
            }

            if (event.getType() == IResourceChangeEvent.POST_BUILD) {
                if (buildLatch != null) {
                    buildLatch.countDown();
                    buildLatch = null;
                }
            }
        }
        
        public void waitForEndOfCurrentBuild(long timeoutInSeconds) {
            CountDownLatch latch = buildLatch;
            if (latch != null) {
                try {
                    latch.await(timeoutInSeconds, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static class CeylonBuildSummary extends CeylonBuildHook {
        private CeylonBuildHook ceylonBuildHookToRestore;
        
        private boolean installed = false;
        private CountDownLatch firstBuildLatch = new CountDownLatch(1);
        private int kind;
        @SuppressWarnings("rawtypes")
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
        private List<String> errors = null;
        
        private CeylonBuildSummary summaryToFill = this;

        private IBuildConfiguration configuration;
        private IBuildConfiguration[] requestConfigurations = new IBuildConfiguration[0];
        private IBuildConfiguration[] referencedconfigurations = new IBuildConfiguration[0];
        private IBuildConfiguration[] referencingconfigurations = new IBuildConfiguration[0]; 

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
        protected void startBuild(int kind, @SuppressWarnings("rawtypes") Map args, IProject project, 
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
            errors = new ArrayList<>();
            if (project != null) {
                try {
                    IMarker[] allProblems = project.findMarkers(CeylonBuilder.PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
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
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }

            
            
            if (summaryToFill == this) {
                if (reentrantBuildSummary != null) {
                    reentrantBuildSummary.install();
                } else {
                    CeylonBuilder.replaceHook(ceylonBuildHookToRestore);
                }
                firstBuildLatch.countDown();
            }
        }
        
        @Override
        protected void afterReentrantBuild() {
            if (summaryToFill == this) {
                if (reentrantBuildSummary != null &&
                        ! reentrantBuildSummary.didFullBuild() &&
                        ! reentrantBuildSummary.didIncrementalBuild()) {
                    reentrantBuildSummary.endBuild();
                }
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

        
        @SuppressWarnings("rawtypes")
        public final Map getArgs() {
            return args;
        }

        public final boolean isReentrant() {
            return isReentrant;
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
        
        public List<String> getErrors() {
            return errors;
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
                        "  Error Markers : " + getErrors() + "\n" +
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
        PostBuildListener buildListener = PostBuildListener.instance();
        CeylonBuildSummary summary = new CeylonBuildSummary(project);
        summary.install();
        project.build(IncrementalProjectBuilder.FULL_BUILD, null);
        summary.waitForBuildEnd(60);
        buildListener.waitForEndOfCurrentBuild(5);
        return summary;
    }
    
    public static void ctrlClick(final SWTBotEclipseEditor editor) {
        Rectangle caretCoordinates = UIThreadRunnable.syncExec(editor.getStyledText().display, new Result<Rectangle>() {
            @Override
            public Rectangle run() {
                int offset = editor.getStyledText().widget.getCaretOffset();                
                return editor.getStyledText().widget.getTextBounds(offset, offset + 1);
            }
        });
        
        final Point pointToMoveAndClick = new Point(caretCoordinates.x + (caretCoordinates.width / 2), caretCoordinates.y + caretCoordinates.height / 2);
       
        editor.setFocus();
        asyncExec(editor.getStyledText().display, new VoidResult() {
            @Override
            public void run() {
                Point absoluteCoordinates = editor.getStyledText().widget.toDisplay(pointToMoveAndClick);
                editor.getStyledText().display.setCursorLocation(absoluteCoordinates);
            }
        });
        Event event = Utils.createMouseEvent(editor.getStyledText().widget, editor.getStyledText().display, pointToMoveAndClick.x, pointToMoveAndClick.y, 0, SWT.CTRL, 1);
        Utils.notify(SWT.MouseMove, event, editor.getStyledText().widget, editor.getStyledText().display);
        event = Utils.createMouseEvent(editor.getStyledText().widget, editor.getStyledText().display, pointToMoveAndClick.x, pointToMoveAndClick.y, 0, SWT.CTRL, 1);
        Utils.notify(SWT.MouseMove, event, editor.getStyledText().widget, editor.getStyledText().display);
        event = Utils.createMouseEvent(editor.getStyledText().widget, editor.getStyledText().display, pointToMoveAndClick.x, pointToMoveAndClick.y, 1, SWT.CTRL, 1);
        Utils.notify(SWT.MouseDown, event, editor.getStyledText().widget, editor.getStyledText().display);
        event = Utils.createMouseEvent(editor.getStyledText().widget, editor.getStyledText().display, pointToMoveAndClick.x, pointToMoveAndClick.y, 1, SWT.CTRL, 1);
        Utils.notify(SWT.MouseUp, event, editor.getStyledText().widget, editor.getStyledText().display);
    }
    
    public static SWTBotEclipseEditor showEditorByTitle(SWTWorkbenchBot bot, String title) {
        try {
            SWTBotEditor editor = bot.editorByTitle(title);
            Assert.assertNotNull("No opened editor found with title '" + title + "'", editor);
            SWTBotEclipseEditor textEditor = editor.toTextEditor();
            textEditor.show();
            return textEditor;
        } catch(WidgetNotFoundException e) {
            Assert.fail("No opened editor found with title '" + title + "'");
        }
        return null;
    }

    public static Position positionInTextEditor(SWTBotEclipseEditor editor, String match, int offset) {
        return positionInTextEditor(editor, Pattern.compile(match, Pattern.LITERAL), offset);
    }
    
    public static Position positionInTextEditor(SWTBotEclipseEditor editor, Pattern pattern, int offset) {
        for (int line=0; line < editor.getLineCount(); line++) {
            String lineText = editor.getTextOnLine(line);
            Matcher matcher = pattern.matcher(lineText);
            if (matcher.find()) {
                return new Position(line, matcher.start() + offset);
            }
        }
        Assert.fail("The editor of file '" + editor.getTitle() + "' doesn't contain any string matching '" + pattern + "'");
        return null;
    }
}
