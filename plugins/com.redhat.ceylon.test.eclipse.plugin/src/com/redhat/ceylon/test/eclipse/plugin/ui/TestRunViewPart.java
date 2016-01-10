package com.redhat.ceylon.test.eclipse.plugin.ui;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.HISTORY;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.RELAUNCH;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.RELAUNCH_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STOP;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.historyLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.msg;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.relaunchLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.relaunchFailedLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.relaunchFailedNamePostfix;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestPlatformJs;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestPlatformJvm;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunFinished;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunInterrupted;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunRunning;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.stopLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE_JS;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getActivePage;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getDisplay;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getElapsedTimeInSeconds;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getShell;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchShortcut.relaunch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRun;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunContainer;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunListener;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunListenerAdapter;

public class TestRunViewPart extends ViewPart {

    private static final String NAME = "com.redhat.ceylon.test.eclipse.plugin.testview";
    private static final int REFRESH_INTERVAL = 200;

    private CounterPanel counterPanel;
    private ProgressBar progressBar;
    private SashForm sashForm;
    private TestsPanel testsPanel;
    private StackTracePanel stackTracePanel;
    private IPartListener2 viewPartListener;
    private TestRunListener testRunListener;
    private TestRun currentTestRun;
    private UpdateViewJob updateViewJob = new UpdateViewJob();
    private ShowHistoryAction showHistoryAction;
    private RelaunchAction relaunchAction;
    private RelaunchFailedAction relaunchFailedAction;
    private StopAction stopAction;
    private boolean isDisposed;
    private boolean isVisible;

    public static void showPageAsync() {
        getDisplay().asyncExec(new Runnable() {
            public void run() {
                showPage();
            }
        });
    }

    public static void showPage() {
        try {
            IWorkbenchPage page = getActivePage();
            if (page != null) {
                TestRunViewPart view = (TestRunViewPart) page.findView(NAME);
                if (view == null) {
                    page.showView(NAME, null, IWorkbenchPage.VIEW_VISIBLE);
                } else {
                	page.bringToTop(view);
                }
            }
        } catch (PartInitException e) {
            CeylonTestPlugin.logError("", e);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout= new GridLayout(1, false);
        layout.marginLeft = 0;
        layout.marginRight = 0;

        Composite composite= new Composite(parent, SWT.NONE);
        composite.setLayout(layout);

        createCounterPanel(composite);
        createProgressBar(composite);
        createSashForm(composite);
        createTestsPanel();
        createStackTracePanel();
        createToolBar();
        createViewPartListener();
        initCurrentTestRun();
        createTestRunListener();
    }

    private void createViewPartListener() {
        viewPartListener = new IPartListener2() {
            public void partActivated(IWorkbenchPartReference ref) {}
            public void partBroughtToTop(IWorkbenchPartReference ref) {}
            public void partInputChanged(IWorkbenchPartReference ref) {}
            public void partClosed(IWorkbenchPartReference ref) {}
            public void partDeactivated(IWorkbenchPartReference ref) {}
            public void partOpened(IWorkbenchPartReference ref) {}

            public void partVisible(IWorkbenchPartReference ref) {
                if (getSite().getId().equals(ref.getId())) {
                    isVisible = true;
                    updateView();
                }
            }

            public void partHidden(IWorkbenchPartReference ref) {
                if (getSite().getId().equals(ref.getId())) {
                    isVisible = false;
                }
            }
        };

        getViewSite().getPage().addPartListener(viewPartListener);
    }

    private void createCounterPanel(Composite composite) {
        counterPanel = new CounterPanel(composite);
        counterPanel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
    }

    private void createProgressBar(Composite composite) {
        progressBar = new ProgressBar(composite);
        progressBar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
    }

    private void createSashForm(Composite composite) {
        sashForm = new SashForm(composite, SWT.VERTICAL);
        sashForm.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
    }

    private void createTestsPanel() {
        testsPanel = new TestsPanel(this, sashForm);
        testsPanel.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                TestElement selectedTestElement = null;
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (selection.size() == 1) {
                    Object selectedItem = selection.getFirstElement();
                    if (selectedItem instanceof TestElement) {
                        selectedTestElement = (TestElement) selectedItem;
                    }
                }
                stackTracePanel.setSelectedTestElement(selectedTestElement);
            }
        });
    }

    private void createStackTracePanel() {
        stackTracePanel = new StackTracePanel(sashForm);
    }

    private void createToolBar() {
        relaunchAction = new RelaunchAction();
        relaunchFailedAction = new RelaunchFailedAction();
        stopAction = new StopAction();
        showHistoryAction = new ShowHistoryAction();

        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(relaunchAction);
        toolBarManager.add(relaunchFailedAction);
        toolBarManager.add(stopAction);
        toolBarManager.add(showHistoryAction);
        toolBarManager.update(true);        
    }
    
    private void initCurrentTestRun() {
        if( currentTestRun == null ) {
            List<TestRun> testRuns = CeylonTestPlugin.getDefault().getModel().getTestRuns();
            if( !testRuns.isEmpty() ) {
                setCurrentTestRun(testRuns.get(0));                
            }
        }
    }

    private void createTestRunListener() {
        testRunListener = new TestRunListenerAdapter() {
            @Override
            public void testRunAdded(TestRun testRun) {
                setCurrentTestRun(testRun);
            }
            @Override
            public void testRunRemoved(TestRun testRun) {
                if (testRun == currentTestRun) {
                    setCurrentTestRun(null);
                }
            }
            @Override
            public void testRunFinished(TestRun testRun) {
                if (testRun == currentTestRun) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            testsPanel.moveToFirstFailure();
                        }
                    });
                }
            }
        };

        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
        testRunContainer.addTestRunListener(testRunListener);
    }

    private void setCurrentTestRun(final TestRun testRun) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                synchronized (TestRun.acquireLock(testRun)) {
                    currentTestRun = testRun;
                    testsPanel.setCurrentTestRun(testRun);

                    updateView();

                    if( testRun != null ) {
                        updateViewJob.schedule(REFRESH_INTERVAL);
                    }
                }
            }
        });
    }

    private void updateView() {
        synchronized (TestRun.acquireLock(currentTestRun)) {
            updateStatusMessage();
            updateActionState();
            counterPanel.updateView(currentTestRun);
            progressBar.updateView(currentTestRun);
            testsPanel.updateView();
        }
    }

    private void updateStatusMessage() {
        String msg = "";

        if (currentTestRun != null) {
            if (currentTestRun.isRunning()) {
                msg = msg(statusTestRunRunning, currentTestRun.getRunName());
            }
            else if (currentTestRun.isFinished()) {
                msg = msg(statusTestRunFinished, getElapsedTimeInSeconds(currentTestRun.getRunElapsedTimeInMilis()));
            }
            else if (currentTestRun.isInterrupted()) {
                msg = msg(statusTestRunInterrupted, currentTestRun.getRunName());
            }
            
            try {
                msg += " ";
                if (LAUNCH_CONFIG_TYPE_JS.equals(currentTestRun.getLaunch().getLaunchConfiguration().getType().getIdentifier())) {
                    msg += statusTestPlatformJs;
                } else {
                    msg += statusTestPlatformJvm;
                }
            } catch (CoreException e) {
                CeylonTestPlugin.logError("", e);
            }
        }

        setContentDescription(msg);
    }

    private void updateActionState() {
        boolean canRelaunch = false;
        boolean canRelaunchFailed = false;
        boolean canStop = false;

        if (currentTestRun != null) {
            canRelaunch = !currentTestRun.isRunning();
            canRelaunchFailed = !currentTestRun.isRunning() && currentTestRun.isFailureOrError();
            canStop = currentTestRun.isRunning();
        }

        relaunchAction.setEnabled(canRelaunch);
        relaunchFailedAction.setEnabled(canRelaunchFailed);
        stopAction.setEnabled(canStop);
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void dispose() {
        isDisposed = true;
        isVisible = false;

        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
        testRunContainer.removeTestRunListener(testRunListener);

        getViewSite().getPage().removePartListener(viewPartListener);

        super.dispose();
    }

    private class UpdateViewJob extends UIJob {

        public UpdateViewJob() {
            super("UpdateViewJob");
            setSystem(true);
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {
            if (!isDisposed) {
                if (isVisible) {
                    updateView();
                }
                if (currentTestRun != null && currentTestRun.isRunning()) {
                    schedule(REFRESH_INTERVAL);
                }
            }
            return Status.OK_STATUS;
        }

    }

    private class RelaunchAction extends Action implements IMenuCreator {
        
        private Menu dropdownMenu;

        public RelaunchAction() {
            super(relaunchLabel, AS_DROP_DOWN_MENU);
            setDescription(relaunchLabel);
            setToolTipText(relaunchLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(RELAUNCH));
            setEnabled(false);
            setMenuCreator(this);
        }

        @Override
        public void run() {
            synchronized (TestRun.acquireLock(currentTestRun)) {
                if( currentTestRun == null || currentTestRun.isRunning() )
                    return;

                ILaunch launch = currentTestRun.getLaunch();
                if( launch == null )
                    return;

                ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
                if( launchConfiguration == null )
                    return;

                DebugUITools.launch(launchConfiguration, launch.getLaunchMode());
            }
        }

        @Override
        public Menu getMenu(Control parent) {
            if( dropdownMenu != null ) {
                dropdownMenu.dispose();
            }
            
            dropdownMenu = new Menu(parent.getShell(), SWT.POP_UP|SWT.NONE);
            
            TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
            List<TestRun> testRuns = testRunContainer.getTestRuns();
            Set<String> testRunNames = new HashSet<String>();
            for(TestRun testRun : testRuns) {
                if( !testRunNames.contains(testRun.getRunName()) ) {
                    testRunNames.add(testRun.getRunName());
                    addActionToMenu(dropdownMenu, new RelaunchMenuAction(testRun));
                }
            }
            
            return dropdownMenu;
        }
        
        private void addActionToMenu(Menu parent, Action action) {
            ActionContributionItem item = new ActionContributionItem(action);
            item.fill(parent, -1);
        }

        @Override
        public Menu getMenu(Menu parent) {
            return null;
        }
        
        @Override
        public void dispose() {
            if( dropdownMenu != null ) {
                dropdownMenu.dispose();
                dropdownMenu = null;
            }
        }

    }
    
    private class RelaunchMenuAction extends Action {

        private final TestRun testRun;

        public RelaunchMenuAction(TestRun testRun) {
            super(testRun.getRunName());
            setDescription(testRun.getRunName());
            setToolTipText(testRun.getRunName());
            this.testRun = testRun;
        }
        
        @Override
        public void run() {
            ILaunch launch = testRun.getLaunch();
            if( launch == null )
                return;

            ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
            if( launchConfiguration == null )
                return;

            DebugUITools.launch(launchConfiguration, launch.getLaunchMode());
        }
        
    }
    
    private class RelaunchFailedAction extends Action {

        public RelaunchFailedAction() {
            super(relaunchFailedLabel);
            setDescription(relaunchFailedLabel);
            setToolTipText(relaunchFailedLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(RELAUNCH_FAILED));
            setEnabled(false);
        }

        @Override
        public void run() {
            synchronized (TestRun.acquireLock(currentTestRun)) {
                if (currentTestRun == null)
                    return;

                ILaunch launch = currentTestRun.getLaunch();
                if (launch == null)
                    return;

                String launchName = launch.getLaunchConfiguration().getName() + " " + relaunchFailedNamePostfix;
                String launchMode = launch.getLaunchMode();

                List<String> qualifiedNames = new ArrayList<String>();
                for (TestElement testElement : currentTestRun.getAtomicTests()) {
                    if (testElement.getState().isFailureOrError()
                            && !qualifiedNames.contains(testElement.getQualifiedName())) {
                        qualifiedNames.add(testElement.getQualifiedName());
                    }
                }

                try {
                    relaunch(launch, launchName, launchMode, qualifiedNames);
                } catch (CoreException e) {
                    CeylonTestPlugin.logError("", e);
                }
            }
        }

    }

    private class StopAction extends Action {

        public StopAction() {
            super(stopLabel);
            setDescription(stopLabel);
            setToolTipText(stopLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STOP));
            setEnabled(false);
        }

        @Override
        public void run() {
            synchronized (TestRun.acquireLock(currentTestRun)) {
                if( currentTestRun == null || !currentTestRun.isRunning() )
                    return;

                ILaunch launch = currentTestRun.getLaunch();
                if( launch == null || !launch.canTerminate() )
                    return;

                try {
                    launch.terminate();
                } catch (DebugException e) {
                    CeylonTestPlugin.logError("", e);
                }
            }
        }

    }    

    private class ShowHistoryAction extends Action {

        public ShowHistoryAction() {
            super(historyLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(HISTORY));
        }

        @Override
        public void run() {
            HistoryDialog dlg = new HistoryDialog(getShell());
            if (dlg.open() == Dialog.OK) {
                TestRun selectedTestRun = dlg.getSelectedTestRun();
                if( selectedTestRun != currentTestRun ) {
                    setCurrentTestRun(selectedTestRun);
                }
            }
        }

    }

}