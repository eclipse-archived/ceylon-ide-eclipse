package com.redhat.ceylon.test.eclipse.plugin.ui;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.COLLAPSE_ALL;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.EXPAND_ALL;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SCROLL_LOCK;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_FAILURES;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_NEXT;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_PREV;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.collapseAllLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.expandAllLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.gotoLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.scrollLockLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showFailuresOnlyLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showNextFailureLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showPreviousFailureLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showTestsElapsedTime;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showTestsGroupedByPackages;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SCROLL_LOCK;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_FAILURES_ONLY;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_TESTS_ELAPSED_TIME;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_TESTS_GROUPED_BY_PACKAGES;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getElapsedTimeInSeconds;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getTestStateImage;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRun;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunContainer;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunListenerAdapter;
import com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil;

public class TestsPanel extends Composite {

    private TestRun currentTestRun;
    private TestRunViewPart viewPart;
    private TreeViewer viewer;
    private ShowFailuresOnlyAction showFailuresOnlyAction = new ShowFailuresOnlyAction();
    private ShowPreviousFailureAction showPreviousFailureAction = new ShowPreviousFailureAction();
    private ShowNextFailureAction showNextFailureAction = new ShowNextFailureAction();
    private ShowFailuresOnlyFilter showFailuresOnlyFilter = new ShowFailuresOnlyFilter();
    private ShowTestsElapsedTimeAction showTestsElapsedTimeAction = new ShowTestsElapsedTimeAction();
    private ShowTestsGroupedByPackagesAction showTestsGroupedByPackagesAction = new ShowTestsGroupedByPackagesAction();
    private ScrollLockAction scrollLockAction = new ScrollLockAction();
    private ExpandAllAction expandAllAction = new ExpandAllAction();
    private CollapseAllAction collapseAllAction = new CollapseAllAction();
    private GotoAction gotoAction = new GotoAction();
    private TestRunListenerAdapter testRunListener;
    private TestElement lastStartedTestElement;
    private Set<String> lastFinishedPackages = new LinkedHashSet<String>();

    public TestsPanel(TestRunViewPart viewPart, Composite parent) {
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
        createPopupMenu();
        createTestRunListener();
    }

    public void setCurrentTestRun(TestRun currentTestRun) {
        synchronized (TestRun.acquireLock(this.currentTestRun)) {
            this.currentTestRun = currentTestRun;
            this.lastStartedTestElement = null;
            this.lastFinishedPackages.clear();
        }
    }

    private void createToolBar() {
        IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(showNextFailureAction);
        toolBarManager.add(showPreviousFailureAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(expandAllAction);
        toolBarManager.add(collapseAllAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(showFailuresOnlyAction);
        toolBarManager.add(scrollLockAction);
        toolBarManager.update(true);
    }    

    private void createMenuBar() {
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
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                handleSelectionChange(((IStructuredSelection) event.getSelection()).getFirstElement());
            }
        });
        viewer.getTree().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                handleDoubleClick(((IStructuredSelection) viewer.getSelection()).getFirstElement());
            }
        });
    }
    
    private void createPopupMenu() {
        MenuManager popupMenu = new MenuManager();
        popupMenu.add(gotoAction);
        popupMenu.add(new Separator());
        popupMenu.add(expandAllAction);
        popupMenu.add(collapseAllAction);
        
        viewer.getTree().setMenu(popupMenu.createContextMenu(this));
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
        synchronized (TestRun.acquireLock(currentTestRun)) {
            updateViewer();
            updateActionState();
            automaticRevealLastStarted();
            automaticCollapseLastSuccessPackages();
        }
    }

    private void updateViewer() {
        if( viewer.getInput() != currentTestRun ) {
            viewer.setInput(currentTestRun);
        }
        viewer.refresh();
    }

    private void updateActionState() {
        boolean containsFailures = false;
        boolean canExpandCollapse = false;

        if (currentTestRun != null) {
            containsFailures = !currentTestRun.isSuccess();
            if (showTestsGroupedByPackagesAction.isChecked()) {
                canExpandCollapse = true;
            }
        }

        showNextFailureAction.setEnabled(containsFailures);
        showPreviousFailureAction.setEnabled(containsFailures);
        expandAllAction.setEnabled(canExpandCollapse);
        collapseAllAction.setEnabled(canExpandCollapse);
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

    private void handleSelectionChange(Object selectedItem) {
        if (selectedItem instanceof TestElement) {
            gotoAction.setEnabled(true);
        } else {
            gotoAction.setEnabled(false);
        }
    }

    private void handleDoubleClick(Object selectedItem) {
        if (selectedItem instanceof TestElement && gotoAction.isEnabled()) {
            gotoAction.run();
        } else if (selectedItem instanceof String) {
            boolean isExpanded = viewer.getExpandedState(selectedItem);
            if (isExpanded) {
                viewer.collapseToLevel(selectedItem, 1);
            } else {
                viewer.expandToLevel(selectedItem, 1);
            }
        }
    }

    private void moveTo(TestElement testElement) {
        if (testElement != null) {
            viewer.reveal(createTreePath(testElement));
            viewer.setSelection(new StructuredSelection(testElement), true);
        }
    }
    
    private void gotoTest(TestElement testElement) throws CoreException {
        ILaunch launch = currentTestRun.getLaunch();
        ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
        String projectName = launchConfiguration.getAttribute(ATTR_PROJECT_NAME, (String) null);

        IProject project = CeylonTestUtil.getProject(projectName);
        if (project != null) {
            List<Module> modules = CeylonBuilder.getModulesInProject(project);
            for (Module module : modules) {
                Package pkg = module.getDirectPackage(testElement.getPackageName());
                if (pkg != null) {
                    Declaration d = null;

                    int separatorIndex = testElement.getName().indexOf(".");
                    if (separatorIndex == -1) {
                        d = pkg.getMember(testElement.getName(), null, false);
                    } else {
                        String className = testElement.getName().substring(0, separatorIndex);
                        String methodName = testElement.getName().substring(separatorIndex + 1);
                        d = pkg.getMember(className, null, false);
                        if (d != null) {
                            d = d.getMember(methodName, null, false);
                        }
                    }

                    if (d != null) {
                        CeylonSourcePositionLocator.gotoDeclaration(d, project);
                        return;
                    }
                }
            }
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
                image = getTestStateImage(testElement);

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
                styledText.append(" (" + getElapsedTimeInSeconds(elapsedTimeInMilis) + " s)", StyledString.COUNTER_STYLER);
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
            synchronized (TestRun.acquireLock(currentTestRun)) {
                if( currentTestRun == null ) {
                    return;
                }

                Object currentElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();

                int fromIndex = 0;
                if (currentElement instanceof String ) {
                    List<TestElement> testElementsByPackage = currentTestRun.getTestElementsByPackages().get(currentElement);
                    if (testElementsByPackage != null && !testElementsByPackage.isEmpty()) {
                        fromIndex = currentTestRun.getTestElements().indexOf(testElementsByPackage.get(0));
                    }
                } else if (currentElement instanceof TestElement) {
                    fromIndex = currentTestRun.getTestElements().indexOf(currentElement) + 1;
                }

                TestElement nextElement = null;
                if (fromIndex < currentTestRun.getTestElements().size()) {
                    for (int i = fromIndex; i < currentTestRun.getTestElements().size(); i++) {
                        TestElement testElement = currentTestRun.getTestElements().get(i);
                        if (testElement.getState().isFailureOrError()) {
                            nextElement = testElement;
                            break;
                        }
                    }
                }

                moveTo(nextElement);
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
            synchronized (TestRun.acquireLock(currentTestRun)) {
                if( currentTestRun == null ) {
                    return;
                }

                Object currentElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();

                int fromIndex = -1;
                if (currentElement instanceof String) {
                    List<TestElement> testElementsByPackage = currentTestRun.getTestElementsByPackages().get(currentElement);
                    if (testElementsByPackage != null && !testElementsByPackage.isEmpty()) {
                        fromIndex = currentTestRun.getTestElements().indexOf(testElementsByPackage.get(0)) - 1;
                    }
                } else if (currentElement instanceof TestElement) {
                    fromIndex = currentTestRun.getTestElements().indexOf(currentElement) - 1;
                } else {
                    fromIndex = currentTestRun.getTestElements().size() - 1;
                }

                TestElement prevElement = null;
                if (fromIndex >= 0) {
                    for (int i = fromIndex; i >= 0; i--) {
                        TestElement testElement = currentTestRun.getTestElements().get(i);
                        if (testElement.getState().isFailureOrError()) {
                            prevElement = testElement;
                            break;
                        }
                    }
                }

                moveTo(prevElement);
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

            updateView();
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

            updateView();
        }

    }

    private class ScrollLockAction extends Action {

        public ScrollLockAction() {
            super(scrollLockLabel, AS_CHECK_BOX);
            setDescription(scrollLockLabel);
            setToolTipText(scrollLockLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(SCROLL_LOCK));

            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            setChecked(preferenceStore.getBoolean(PREF_SCROLL_LOCK));
        }

        @Override
        public void run() {
            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            preferenceStore.setValue(PREF_SCROLL_LOCK, isChecked());
        }

    }

    private class CollapseAllAction extends Action {

        public CollapseAllAction() {
            super(collapseAllLabel);
            setDescription(collapseAllLabel);
            setToolTipText(collapseAllLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(COLLAPSE_ALL));
            setEnabled(false);
        }

        @Override
        public void run() {
            viewer.collapseAll();
        }

    }

    private class ExpandAllAction extends Action {

        public ExpandAllAction() {
            super(expandAllLabel);
            setDescription(expandAllLabel);
            setToolTipText(expandAllLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(EXPAND_ALL));
            setEnabled(false);
        }

        @Override
        public void run() {
            viewer.expandAll();
        }

    }
    
    private class GotoAction extends Action {

        public GotoAction() {
            super(gotoLabel);
            setDescription(gotoLabel);
            setToolTipText(gotoLabel);
            setEnabled(false);
        }

        @Override
        public void run() {
            Object selectedElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
            if (selectedElement instanceof TestElement) {
                try {
                    gotoTest((TestElement) selectedElement);
                } catch (CoreException e) {
                    CeylonTestPlugin.logError("", e);
                }
            }
        }

    }

}