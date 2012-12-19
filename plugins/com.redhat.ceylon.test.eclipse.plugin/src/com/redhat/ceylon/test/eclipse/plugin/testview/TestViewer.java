package com.redhat.ceylon.test.eclipse.plugin.testview;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.RELAUNCH;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SCROLL_LOCK;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_FAILURES;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_NEXT;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_PREV;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STOP;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.relaunchLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.scrollLockLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showFailuresOnlyLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showNextFailureLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showPreviousFailureLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showTestsElapsedTime;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showTestsGroupedByPackages;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.stopLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_FAILURES_ONLY;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_TESTS_ELAPSED_TIME;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_TESTS_GROUPED_BY_PACKAGES;

import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRun;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunContainer;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunListenerAdapter;

public class TestViewer extends Composite {

    public static final NumberFormat ELAPSED_TIME_FORMAT;
    static {
        ELAPSED_TIME_FORMAT = NumberFormat.getNumberInstance();
        ELAPSED_TIME_FORMAT.setGroupingUsed(true);
        ELAPSED_TIME_FORMAT.setMinimumFractionDigits(3);
        ELAPSED_TIME_FORMAT.setMaximumFractionDigits(3);
        ELAPSED_TIME_FORMAT.setMinimumIntegerDigits(1);
    }
    
    private TestRun currentTestRun;
    private TestViewPart viewPart;
    private TreeViewer viewer;
    private ShowFailuresOnlyAction showFailuresOnlyAction;
    private ShowPreviousFailureAction showPreviousFailureAction;
    private ShowNextFailureAction showNextFailureAction;
    private ShowFailuresOnlyFilter showFailuresOnlyFilter;
    private ShowTestsElapsedTimeAction showTestsElapsedTimeAction;
    private ShowTestsGroupedByPackagesAction showTestsGroupedByPackagesAction;
    private ScrollLockAction scrollLockAction;
    private RelaunchAction relaunchAction;
    private StopAction stopAction;
    private TestRunListenerAdapter testRunListener;
    private TestElement lastStartedTestElement;
    private Set<String> lastFinishedPackages = new LinkedHashSet<String>();

    public TestViewer(TestViewPart viewPart, Composite parent) {
        super(parent, SWT.NONE);
        
        this.viewPart = viewPart;

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        createToolBar();
        createMenuBar();
        createViewer();
        createTestRunListener();
    }

    public void setCurrentTestRun(TestRun currentTestRun) {
        this.currentTestRun = currentTestRun;
    }

    private void createToolBar() {
        showNextFailureAction = new ShowNextFailureAction();
        showPreviousFailureAction = new ShowPreviousFailureAction();
        showFailuresOnlyAction = new ShowFailuresOnlyAction();
        showFailuresOnlyFilter = new ShowFailuresOnlyFilter();
        scrollLockAction = new ScrollLockAction();
        relaunchAction = new RelaunchAction();
        stopAction = new StopAction();
        
        IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(showNextFailureAction);
        toolBarManager.add(showPreviousFailureAction);
        toolBarManager.add(showFailuresOnlyAction);
        toolBarManager.add(scrollLockAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(relaunchAction);
        toolBarManager.add(stopAction);
        toolBarManager.update(true);
    }    

    private void createMenuBar() {
        showTestsElapsedTimeAction = new ShowTestsElapsedTimeAction();
        showTestsGroupedByPackagesAction = new ShowTestsGroupedByPackagesAction();
        
        IMenuManager menuManager = viewPart.getViewSite().getActionBars().getMenuManager();
        menuManager.add(showTestsElapsedTimeAction);
        menuManager.add(showTestsGroupedByPackagesAction);
        menuManager.update(true);
    }

    private void createViewer() {
        viewer = new TreeViewer(this, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setUseHashlookup(true);
        viewer.setContentProvider(new TestContentProvider());
        viewer.setLabelProvider(new TestLabelProvider());
        viewer.getControl().setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
    }
    
    private TreePath createTreePath(TestElement testElement) {
        if (showTestsGroupedByPackagesAction.isChecked()) {
            return new TreePath(new Object[] { testElement.getPackageName(), testElement });
        } else {
            return new TreePath(new Object[] { testElement });
        }
    }

    private void createTestRunListener() {
        testRunListener = new TestRunListenerAdapter() {
            @Override
            public void testStarted(TestRun testRun, TestElement testElement) {
                if (currentTestRun == testRun) {
                    lastStartedTestElement = testElement;
                }
            }
            @Override
            public void testFinished(TestRun testRun, TestElement testElement) {
                if (currentTestRun == testRun) {
                    lastFinishedPackages.add(testElement.getPackageName());
                }
            }
        };

        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
        testRunContainer.addTestRunListener(testRunListener);
    }
    
    public void updateView() {
        updateViewer();
        updateActionState();
        automaticRevealLastStarted();
        automaticCollapseLastSuccessPackages();
    }

    private void updateViewer() {
        if( viewer.getInput() != currentTestRun ) {
            viewer.setInput(currentTestRun);
        }
        viewer.refresh();
    }
    
    private void updateActionState() {
        boolean containsFailures = false;
        boolean canRelaunch = false;
        boolean canStop = false;
        
        if (currentTestRun != null) {
            containsFailures = !currentTestRun.isSuccess();
            canRelaunch = !currentTestRun.isRunning();
            canStop = currentTestRun.isRunning();
        }
    
        showNextFailureAction.setEnabled(containsFailures);
        showPreviousFailureAction.setEnabled(containsFailures);
        relaunchAction.setEnabled(canRelaunch);
        stopAction.setEnabled(canStop);
    }

    private void automaticRevealLastStarted() {
        if (currentTestRun != null &&
                lastStartedTestElement != null &&
                !scrollLockAction.isChecked()) {
            viewer.reveal(createTreePath(lastStartedTestElement));
        }
    }

    private void automaticCollapseLastSuccessPackages() {
        if (currentTestRun != null &&
                showTestsGroupedByPackagesAction.isChecked() &&
                !scrollLockAction.isChecked() &&
                !lastFinishedPackages.isEmpty()) {
            for (String packageName : lastFinishedPackages) {
                State packageState = currentTestRun.getPackageState(packageName);
                if (packageState == State.SUCCESS) {
                    viewer.collapseToLevel(packageName, TreeViewer.ALL_LEVELS);
                }
            }
            lastFinishedPackages.clear();
        }
    }

    public TreeViewer getViewer() {
        return viewer;
    }
    
    @Override
    public void dispose() {
        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
        testRunContainer.removeTestRunListener(testRunListener);
        super.dispose();
    }
    
    private class TestContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            boolean isGrouped = showTestsGroupedByPackagesAction.isChecked();
            if (inputElement instanceof TestRun) {
                TestRun testRun = (TestRun) inputElement;
                if (isGrouped) {
                    return testRun.getTestElementsByPackages().keySet().toArray();
                } else {
                    return testRun.getTestElements().toArray();
                }
            }
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            boolean isGrouped = showTestsGroupedByPackagesAction.isChecked();
            if (isGrouped && parentElement instanceof String) {
                String packageName = (String) parentElement;
                List<TestElement> testElementsInPackage = currentTestRun.getTestElementsByPackages().get(packageName);
                if (testElementsInPackage != null) {
                    return testElementsInPackage.toArray();
                }
            }
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            boolean isGrouped = showTestsGroupedByPackagesAction.isChecked();
            if (isGrouped && element instanceof String) {
                return true;
            }
            return false;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    private class TestLabelProvider extends StyledCellLabelProvider {

        @Override
        public void update(ViewerCell cell) {
            boolean isGrouped = showTestsGroupedByPackagesAction.isChecked();
            
            String text = null;
            Image image = null;
            long elapsedTimeInMilis = -1;
            
            if (cell.getElement() instanceof TestElement) {
                TestElement testElement = (TestElement) cell.getElement();
                text = isGrouped ? testElement.getName() : testElement.getQualifiedName();
                
                switch(testElement.getState()) {
                    case RUNNING: image = getImage(TEST_RUNNING); break;
                    case SUCCESS: image = getImage(TEST_SUCCESS); break;
                    case FAILURE: image = getImage(TEST_FAILED); break;
                    case ERROR: image = getImage(TEST_ERROR); break;
                    default: image = getImage(TEST); break;
                }
                
                if( testElement.getState().isFinished() ) {
                    elapsedTimeInMilis = testElement.getElapsedTimeInMilis();
                }
            }
            if (cell.getElement() instanceof String) {
                String packageName = (String) cell.getElement();
                text = packageName;
                
                State state = currentTestRun.getPackageState(packageName);
                switch(state) {
                    case RUNNING: image = getImage(TESTS_RUNNING); break;
                    case SUCCESS: image = getImage(TESTS_SUCCESS); break;
                    case FAILURE: image = getImage(TESTS_FAILED); break;
                    case ERROR: image = getImage(TESTS_ERROR); break;
                    default: image = getImage(TESTS); break;
                }
                
                if( state.isFinished() ) {
                    elapsedTimeInMilis = currentTestRun.getPackageElapsedTimeInMilis(packageName);
                }
            }
            
            StyledString styledText = new StyledString();
            styledText.append(text);
            if (showTestsElapsedTimeAction.isChecked() && elapsedTimeInMilis != -1) {
                String elapsedSeconds = ELAPSED_TIME_FORMAT.format(TimeUnit.MILLISECONDS.toSeconds(elapsedTimeInMilis));
                styledText.append(" (" + elapsedSeconds + " s)", StyledString.COUNTER_STYLER);
            }

            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
            cell.setImage(image);

            super.update(cell);
        }

    }

    private class ShowNextFailureAction extends Action {

        public ShowNextFailureAction() {
            super(showNextFailureLabel);
            setDescription(showNextFailureLabel);
            setToolTipText(showNextFailureLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(SHOW_NEXT));
            setEnabled(false);
        }

        @Override
        public void run() {
            if( currentTestRun == null ) {
                return;
            }
            
            Object currentElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
            Object nextElement = null;

            int fromIndex = 0;
            if (currentElement != null) {
                fromIndex = currentTestRun.getTestElements().indexOf(currentElement) + 1;
            }

            if (fromIndex < currentTestRun.getTestElements().size()) {
                for (int i = fromIndex; i < currentTestRun.getTestElements().size(); i++) {
                    TestElement testElement = currentTestRun.getTestElements().get(i);
                    if (testElement.getState() == State.FAILURE || testElement.getState() == State.ERROR) {
                        nextElement = testElement;
                        break;
                    }
                }
            }

            if (nextElement != null) {
                viewer.setSelection(new StructuredSelection(nextElement), true);
            }
        }
        
    }

    private class ShowPreviousFailureAction extends Action {

        public ShowPreviousFailureAction() {
            super(showPreviousFailureLabel);
            setDescription(showPreviousFailureLabel);
            setToolTipText(showPreviousFailureLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(SHOW_PREV));
            setEnabled(false);
        }

        @Override
        public void run() {
            if( currentTestRun == null ) {
                return;
            }
            
            Object currentElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
            Object prevElement = null;

            int fromIndex = currentTestRun.getTestElements().size() - 1;
            if (currentElement != null) {
                fromIndex = currentTestRun.getTestElements().indexOf(currentElement) - 1;
            }

            if (fromIndex >= 0) {
                for (int i = fromIndex; i >= 0; i--) {
                    TestElement testElement = currentTestRun.getTestElements().get(i);
                    if (testElement.getState() == State.FAILURE || testElement.getState() == State.ERROR) {
                        prevElement = testElement;
                        break;
                    }
                }
            }

            if (prevElement != null) {
                viewer.setSelection(new StructuredSelection(prevElement), true);
            }
        }
        
    }

    private class ShowFailuresOnlyAction extends Action {
        
        public ShowFailuresOnlyAction() {
            super(showFailuresOnlyLabel, AS_CHECK_BOX);
            setDescription(showFailuresOnlyLabel);
            setToolTipText(showFailuresOnlyLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(SHOW_FAILURES));
            
            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            setChecked(preferenceStore.getBoolean(PREF_SHOW_FAILURES_ONLY));
        }

        @Override
        public void run() {
            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            preferenceStore.setValue(PREF_SHOW_FAILURES_ONLY, isChecked());
            
            if( isChecked() ) {
                viewer.addFilter(showFailuresOnlyFilter);
            } else {
                viewer.removeFilter(showFailuresOnlyFilter);
            }
        }
        
    }
    
    private class ShowFailuresOnlyFilter extends ViewerFilter {

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof String) {
                return select(((String) element));
            } else if (element instanceof TestElement) {
                return select(((TestElement) element));
            }
            return false;
        }

        private boolean select(String packageName) {
            if (currentTestRun != null) {
                State packageState = currentTestRun.getPackageState(packageName);
                return packageState.isFailureOrError();
            }
            return false;
        }

        private boolean select(TestElement testElement) {
            State state = testElement.getState();
            return state.isFailureOrError();
        }

    }
    
    private class ShowTestsElapsedTimeAction extends Action {

        public ShowTestsElapsedTimeAction() {
            super(showTestsElapsedTime, AS_CHECK_BOX);
            setDescription(showTestsElapsedTime);
            setToolTipText(showTestsElapsedTime);

            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            setChecked(preferenceStore.getBoolean(PREF_SHOW_TESTS_ELAPSED_TIME));
        }

        @Override
        public void run() {
            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            preferenceStore.setValue(PREF_SHOW_TESTS_ELAPSED_TIME, isChecked());

            viewer.refresh();
        }

    }
    
    private class ShowTestsGroupedByPackagesAction extends Action {

        public ShowTestsGroupedByPackagesAction() {
            super(showTestsGroupedByPackages, AS_CHECK_BOX);
            setDescription(showTestsGroupedByPackages);
            setToolTipText(showTestsGroupedByPackages);

            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            setChecked(preferenceStore.getBoolean(PREF_SHOW_TESTS_GROUPED_BY_PACKAGES));
        }

        @Override
        public void run() {
            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            preferenceStore.setValue(PREF_SHOW_TESTS_GROUPED_BY_PACKAGES, isChecked());

            viewer.refresh();
        }

    }

    private class ScrollLockAction extends Action {
    
        public ScrollLockAction() {
            super(scrollLockLabel, AS_CHECK_BOX);
            setDescription(scrollLockLabel);
            setToolTipText(scrollLockLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(SCROLL_LOCK));
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