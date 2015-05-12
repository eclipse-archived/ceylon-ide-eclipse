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
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_IGNORED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.collapseAllLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.debugLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.errorCanNotFindSelectedTest;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.errorDialogTitle;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.expandAllLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.gotoLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.runLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.scrollLockLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showFailuresOnlyLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showNextFailureLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showPreviousFailureLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showTestsElapsedTime;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showTestsInHierarchy;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SCROLL_LOCK;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_FAILURES_ONLY;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_TESTS_ELAPSED_TIME;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_TESTS_IN_HIERARCHY;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.CLASS;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.CLASS_LOCAL;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.METHOD;
import static com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type.METHOD_LOCAL;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getElapsedTimeInSeconds;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getTestStateImage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Method;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry;
import com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchConfigEntry.Type;
import com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchShortcut;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRun;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRun.TestVisitor;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunContainer;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunListenerAdapter;
import com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil;
import com.redhat.ceylon.test.eclipse.plugin.util.MethodWithContainer;

public class TestsPanel extends Composite {

    private TestRun currentTestRun;
    private TestRunViewPart viewPart;
    private TreeViewer viewer;
    private ShowFailuresOnlyAction showFailuresOnlyAction = new ShowFailuresOnlyAction();
    private ShowPreviousFailureAction showPreviousFailureAction = new ShowPreviousFailureAction();
    private ShowNextFailureAction showNextFailureAction = new ShowNextFailureAction();
    private ShowFailuresOnlyFilter showFailuresOnlyFilter = new ShowFailuresOnlyFilter();
    private ShowTestsElapsedTimeAction showTestsElapsedTimeAction = new ShowTestsElapsedTimeAction();
    private ShowTestsInHierarchyAction showTestsInHierarchyAction = new ShowTestsInHierarchyAction();
    private ScrollLockAction scrollLockAction = new ScrollLockAction();
    private ExpandAllAction expandAllAction = new ExpandAllAction();
    private CollapseAllAction collapseAllAction = new CollapseAllAction();
    private GotoAction gotoAction = new GotoAction();
    private RunAction runAction = new RunAction();
    private DebugAction debugAction = new DebugAction();
    private TestRunListenerAdapter testRunListener;
    private TestElement lastStartedTestElement;
    private Set<String> lastFinishedPackages = new LinkedHashSet<String>();
    private Set<TestElement> lastFinishedTestElements = new LinkedHashSet<TestElement>();

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
            this.lastFinishedTestElements.clear();
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
        menuManager.add(showTestsInHierarchyAction);
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
        popupMenu.add(runAction);
        popupMenu.add(debugAction);
        popupMenu.add(new Separator());
        popupMenu.add(expandAllAction);
        popupMenu.add(collapseAllAction);
        
        viewer.getTree().setMenu(popupMenu.createContextMenu(this));
    }

    private TreePath createTreePath(TestElement testElement) {
        if (showTestsInHierarchyAction.isChecked()) {
            List<Object> path = new ArrayList<Object>();
            findPath(testElement, currentTestRun.getRoot(), path);
            if (!path.isEmpty()) {
                path.add(0, ((TestElement) path.get(0)).getPackageName());
            }
            return new TreePath(path.toArray());
        } else {
            return new TreePath(new Object[] { testElement });
        }
    }
    
    private boolean findPath(TestElement e, TestElement parent, List<Object> path) {
        if (e.equals(parent)) {
            path.add(e);
            return true;
        } else if (parent.getChildren() != null) {
            for (TestElement child : parent.getChildren()) {
                if (findPath(e, child, path)) {
                    if (!parent.equals(currentTestRun.getRoot())) {
                        path.add(0, parent);
                    }
                    return true;
                }
            }
        }
        return false;
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
                    lastFinishedTestElements.add(testElement);
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
            viewer.setSelection(null);
        }
        viewer.refresh();
    }

    private void updateActionState() {
        boolean containsFailures = false;
        boolean canExpandCollapse = false;

        if (currentTestRun != null) {
            containsFailures = !currentTestRun.isSuccess();
            if (showTestsInHierarchyAction.isChecked()) {
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
                currentTestRun.isRunning() &&
                lastStartedTestElement != null &&
                !scrollLockAction.isChecked()) {
            viewer.reveal(createTreePath(lastStartedTestElement));
        }
    }

    private void automaticCollapseLastSuccessPackages() {
        if (currentTestRun != null &&
                showTestsInHierarchyAction.isChecked() &&
                !scrollLockAction.isChecked() ) {
            for (TestElement lastFinishedTestElement : lastFinishedTestElements) {
                if (!currentTestRun.getAtomicTests().contains(lastFinishedTestElement)
                        && lastFinishedTestElement.getState() == State.SUCCESS) {
                    viewer.collapseToLevel(lastFinishedTestElement, TreeViewer.ALL_LEVELS);
                }
            }
            for (String packageName : lastFinishedPackages) {
                State packageState = currentTestRun.getPackageState(packageName);
                if (packageState == State.SUCCESS) {
                    viewer.collapseToLevel(packageName, TreeViewer.ALL_LEVELS);
                }
            }
            lastFinishedTestElements.clear();
            lastFinishedPackages.clear();
        }
    }

	private void handleSelectionChange(Object selectedItem) {
		if (selectedItem == null) {
			gotoAction.setEnabled(false);
			runAction.setEnabled(false);
			debugAction.setEnabled(false);
		} else {
			if (selectedItem instanceof TestElement) {
				gotoAction.setEnabled(true);
			} else {
				gotoAction.setEnabled(false);
			}
			runAction.setEnabled(true);
			debugAction.setEnabled(true);
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
    
    public void moveToFirstFailure() {
        synchronized (TestRun.acquireLock(currentTestRun)) {
            if (currentTestRun == null) {
                return;
            }
            NextFailureVisitor nfv = new NextFailureVisitor(null);
            nfv.visitElements(currentTestRun.getRoot());
            moveTo(nfv.next);
        }
    }
    
    private void gotoTest(TestElement testElement) throws CoreException {
        IProject project = CeylonTestUtil.getProject(currentTestRun.getLaunch());
        if (project != null) {
            Object result = CeylonTestUtil.getPackageOrDeclaration(project, testElement.getQualifiedName());
            if (result instanceof Declaration) {
                Navigation.gotoDeclaration((Declaration) result);
            }
            else if (result instanceof MethodWithContainer) {
                Navigation.gotoDeclaration(((MethodWithContainer) result).getMethod());
            }
        }
    }

    private void runTest(String launchMode) throws CoreException {
    	String launchName = null;
    	List<CeylonTestLaunchConfigEntry> entries = new ArrayList<CeylonTestLaunchConfigEntry>();
		
    	IProject project = CeylonTestUtil.getProject(currentTestRun.getLaunch());
    	if( project != null ) {
		
    		String qualifiedName = null;
    		Object selectedElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
    		if (selectedElement instanceof TestElement) {
    			qualifiedName = ((TestElement) selectedElement).getQualifiedName();
    		} else {
    			qualifiedName = (String) selectedElement;
    		}

    		Object result = CeylonTestUtil.getPackageOrDeclaration(project, qualifiedName);
    		if( result instanceof Package ) {
    			Package pkg = (Package) result;
    			launchName = pkg.getNameAsString();
    			entries.add(CeylonTestLaunchConfigEntry.build(project, Type.PACKAGE, pkg.getNameAsString()));
    		} else if (result instanceof Class) {
    			Class clazz = (Class) result;
    			launchName = clazz.getName();
    			entries.add(CeylonTestLaunchConfigEntry.build(project, clazz.isShared() ? CLASS : CLASS_LOCAL, clazz.getQualifiedNameString()));
    		}
    		else if (result instanceof Method) {
    			Method method = (Method) result;
    			launchName = (method.isMember() ? ((Declaration) method.getContainer()).getName() + "." : "") + method.getName();
    			entries.add(CeylonTestLaunchConfigEntry.build(project, method.isShared() ? METHOD : METHOD_LOCAL, method.getQualifiedNameString()));
    		}
    		else if( result instanceof MethodWithContainer ) {
    		    MethodWithContainer methodWithContainer = (MethodWithContainer) result;
                launchName = methodWithContainer.getContainer().getName() + "." + methodWithContainer.getMethod().getName();
                entries.add(CeylonTestLaunchConfigEntry.build(project, methodWithContainer.getMethod().isShared() ? METHOD : METHOD_LOCAL, methodWithContainer.getContainer().getQualifiedNameString() + "." + methodWithContainer.getMethod().getName()));
    		}
    	}
		
		if (entries.isEmpty()) {
			MessageDialog.openInformation(getShell(), errorDialogTitle, errorCanNotFindSelectedTest);
		} else {
			CeylonTestLaunchShortcut.launch(launchName, entries, launchMode, currentTestRun.getLaunch().getLaunchConfiguration().getType().getIdentifier());
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
            if (inputElement instanceof TestRun) {
                TestRun testRun = (TestRun) inputElement;
                if (showTestsInHierarchyAction.isChecked()) {
                    return testRun.getTestsByPackages().keySet().toArray();
                } else {
                    return testRun.getAtomicTests().toArray();
                }
            }
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (showTestsInHierarchyAction.isChecked()) {
                if( parentElement instanceof String ) {
                    String packageName = (String) parentElement;
                    List<TestElement> testElementsInPackage = currentTestRun.getTestsByPackages().get(packageName);
                    if (testElementsInPackage != null) {
                        return testElementsInPackage.toArray();
                    }
                } else {
                    return ((TestElement)parentElement).getChildren(); 
                }
            }
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (showTestsInHierarchyAction.isChecked()) {
                if (element instanceof String) {
                    return true;
                } else {
                    TestElement e = (TestElement) element;
                    if (e.getChildren() != null && e.getChildren().length != 0) {
                        return true;
                    }
                }
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
            String text = null;
            Image image = null;
            long elapsedTimeInMilis = -1;

            if (cell.getElement() instanceof TestElement) {
                TestElement testElement = (TestElement) cell.getElement();
                text = showTestsInHierarchyAction.isChecked() ? testElement.getShortName() : testElement.getQualifiedName();
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
                case IGNORED: image = getImage(TESTS_IGNORED); break;
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
                NextFailureVisitor nfv = new NextFailureVisitor(((IStructuredSelection) viewer.getSelection()).getFirstElement());
                nfv.visitElements(currentTestRun.getRoot());
                moveTo(nfv.next);
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
                PreviousFailureVisitor pfv = new PreviousFailureVisitor(((IStructuredSelection) viewer.getSelection()).getFirstElement());
                pfv.visitElements(currentTestRun.getRoot());
                moveTo(pfv.previous);
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

    private class ShowTestsInHierarchyAction extends Action {

        public ShowTestsInHierarchyAction() {
            super(showTestsInHierarchy, AS_CHECK_BOX);
            setDescription(showTestsInHierarchy);
            setToolTipText(showTestsInHierarchy);

            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            setChecked(preferenceStore.getBoolean(PREF_SHOW_TESTS_IN_HIERARCHY));
        }

        @Override
        public void run() {
            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            preferenceStore.setValue(PREF_SHOW_TESTS_IN_HIERARCHY, isChecked());

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
    
	private class RunAction extends Action {

		public RunAction() {
			super(runLabel);
			setDescription(runLabel);
			setEnabled(false);
		}

		@Override
		public void run() {
			try {
				runTest(ILaunchManager.RUN_MODE);
			} catch (CoreException e) {
				CeylonTestPlugin.logError("", e);
			}
		}

	}

	private class DebugAction extends Action {

		public DebugAction() {
			super(debugLabel);
			setDescription(debugLabel);
			setEnabled(false);
		}

		@Override
		public void run() {
			try {
				runTest(ILaunchManager.DEBUG_MODE);
			} catch (CoreException e) {
				CeylonTestPlugin.logError("", e);
			}
		}

	}
    
    private static class NextFailureVisitor extends TestVisitor {

        private boolean isBehindCurrentSelection = false;
        private Object currentSelection;
        private TestElement next = null;

        public NextFailureVisitor(Object currentSelection) {
            this.currentSelection = currentSelection;
        }

        @Override
        public void visitElement(TestElement e) {
            if (!isBehindCurrentSelection) {
                if( currentSelection == null ) {
                  isBehindCurrentSelection = true;  
                } else if (currentSelection instanceof String && currentSelection.equals(e.getPackageName())) {
                    isBehindCurrentSelection = true;
                } else if (e.equals(currentSelection)) {
                    isBehindCurrentSelection = true;
                    return;
                }
            }
            if (isBehindCurrentSelection && next == null && e.getChildren() == null && e.getState().isFailureOrError()) {
                next = e;
            }
        }

    }
    
    private static class PreviousFailureVisitor extends TestVisitor {

        private boolean isBeforeCurrentSelection = true;
        private Object currentSelection;
        private TestElement previous = null;

        public PreviousFailureVisitor(Object currentSelection) {
            this.currentSelection = currentSelection;
        }

        @Override
        public void visitElement(TestElement e) {
            if (isBeforeCurrentSelection) {
                if (currentSelection instanceof String && currentSelection.equals(e.getPackageName())) {
                    isBeforeCurrentSelection = false;
                } else if (e.equals(currentSelection)) {
                    isBeforeCurrentSelection = false;
                }

                if (isBeforeCurrentSelection && e.getChildren() == null && e.getState().isFailureOrError()) {
                    previous = e;
                }
            }
        }

    }

}