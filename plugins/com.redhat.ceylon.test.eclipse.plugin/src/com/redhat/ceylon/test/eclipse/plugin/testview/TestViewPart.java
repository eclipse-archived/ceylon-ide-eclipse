package com.redhat.ceylon.test.eclipse.plugin.testview;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.msg;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunFinished;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunInterrupted;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.statusTestRunRunning;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getActivePage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getDisplay;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
    private UpdateViewJob updateViewJob = new UpdateViewJob();
    private TestRunListener testRunListener;
    private IPartListener2 viewPartListener;
    private TestRun currentTestRun;
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

    private void createTestRunListener() {
        testRunListener = new TestRunListenerAdapter() {
            @Override
            public void testRunAdded(TestRun testRun) {
                setCurrentTestRun(testRun);
            }
        };

        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
        testRunContainer.addTestRunListener(testRunListener);
    }
    
    private void setCurrentTestRun(TestRun testRun) {
        currentTestRun = testRun;
        testCounterPanel.setCurrentTestRun(testRun);
        testProgressBar.setCurrentTestRun(testRun);
        testViewer.setCurrentTestRun(testRun);
        
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                updateView();
            }
        });
        
        if( currentTestRun != null ) {
            updateViewJob.schedule(REFRESH_INTERVAL);
        }
    }
    
    private void updateView() {
        updateStatusMessage();
        testCounterPanel.updateView();
        testProgressBar.updateView();
        testViewer.updateView();
    }
    
    private void updateStatusMessage() {
        String msg = "";
    
        if (currentTestRun != null) {
            if (currentTestRun.isRunning()) {
                msg = msg(statusTestRunRunning, currentTestRun.getRunName());
            }
            else if (currentTestRun.isFinished()) {
                double seconds = currentTestRun.getRunElapsedTimeInMilis() / 1000.0;
                msg = msg(statusTestRunFinished, TestViewer.ELAPSED_TIME_FORMAT.format(seconds));
            }
            else if (currentTestRun.isInterrupted()) {
                msg = msg(statusTestRunInterrupted, currentTestRun.getRunName());
            }
        }
    
        setContentDescription(msg);
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

}