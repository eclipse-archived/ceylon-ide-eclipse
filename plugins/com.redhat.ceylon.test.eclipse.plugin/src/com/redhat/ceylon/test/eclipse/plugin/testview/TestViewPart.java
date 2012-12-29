package com.redhat.ceylon.test.eclipse.plugin.testview;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.HISTORY;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.RELAUNCH;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STOP;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.historyLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.msg;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.relaunchLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunFinished;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunInterrupted;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunRunning;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.stopLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getActivePage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getDisplay;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getElapsedTimeInSeconds;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getShell;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.Action;
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
import org.eclipse.swt.widgets.Display;
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

public class TestViewPart extends ViewPart {

    private static final String NAME = "com.redhat.ceylon.test.eclipse.plugin.testview";
    private static final int REFRESH_INTERVAL= 200;

    private TestCounterPanel testCounterPanel;
    private TestProgressBar testProgressBar;
    private SashForm sashForm;
    private TestViewer testViewer;
    private TestStackTracePanel testStackTracePanel;
    private IPartListener2 viewPartListener;
    private TestRunListener testRunListener;
    private TestRun currentTestRun;

    private UpdateViewJob updateViewJob = new UpdateViewJob();
    private ShowHistoryAction showHistoryAction;
    private RelaunchAction relaunchAction;
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
                TestViewPart view = (TestViewPart) page.findView(NAME);
                if (view == null) {
                    page.showView(NAME, null, IWorkbenchPage.VIEW_VISIBLE);
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
        createTestViewer();
        createStackTracePanel();
        createToolBar();
        createViewPartListener();
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
                    isVisible= true;
                }
            }

            public void partHidden(IWorkbenchPartReference ref) {
                if (getSite().getId().equals(ref.getId())) {
                    isVisible= false;
                }
            }
        };

        getViewSite().getPage().addPartListener(viewPartListener);
    }

    private void createCounterPanel(Composite composite) {
        testCounterPanel = new TestCounterPanel(composite);
        testCounterPanel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
    }

    private void createProgressBar(Composite composite) {
        testProgressBar = new TestProgressBar(composite);
        testProgressBar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
    }

    private void createSashForm(Composite composite) {
        sashForm = new SashForm(composite, SWT.VERTICAL);
        sashForm.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
    }

    private void createTestViewer() {
        testViewer = new TestViewer(this, sashForm);
        testViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                TestElement selectedTestElement = null;
                Object selectedItem = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (selectedItem instanceof TestElement) {
                    selectedTestElement = (TestElement) selectedItem;
                }
                testStackTracePanel.setSelectedTestElement(selectedTestElement);
            }
        });
    }

    private void createStackTracePanel() {
        testStackTracePanel = new TestStackTracePanel(sashForm);
    }

    private void createToolBar() {
        relaunchAction = new RelaunchAction();
        stopAction = new StopAction();
        showHistoryAction = new ShowHistoryAction();
    
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(relaunchAction);
        toolBarManager.add(stopAction);
        toolBarManager.add(showHistoryAction);
        toolBarManager.update(true);        
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
                    testViewer.setCurrentTestRun(testRun);

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
            testCounterPanel.updateView(currentTestRun);
            testProgressBar.updateView(currentTestRun);
            testViewer.updateView();
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
        }

        setContentDescription(msg);
    }
    
    private void updateActionState() {
        boolean canRelaunch = false;
        boolean canStop = false;
        
        if (currentTestRun != null) {
            canRelaunch = !currentTestRun.isRunning();
            canStop = currentTestRun.isRunning();
        }
        
        relaunchAction.setEnabled(canRelaunch);
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

    private class RelaunchAction extends Action {

        public RelaunchAction() {
            super(relaunchLabel);
            setDescription(relaunchLabel);
            setToolTipText(relaunchLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(RELAUNCH));
            setEnabled(false);
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
            TestHistoryDialog dlg = new TestHistoryDialog(getShell());
            if (dlg.open() == Dialog.OK) {
                TestRun selectedTestRun = dlg.getSelectedTestRun();
                if( selectedTestRun != currentTestRun ) {
                    setCurrentTestRun(selectedTestRun);
                }
            }
        }
        
    }

}