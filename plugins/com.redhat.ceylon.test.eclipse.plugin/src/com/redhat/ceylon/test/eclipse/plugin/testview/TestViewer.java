package com.redhat.ceylon.test.eclipse.plugin.testview;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.RELAUNCH;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_FAILURES;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_NEXT;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.SHOW_PREV;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STOP;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.relaunchLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showFailuresOnlyLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showNextFailureLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.showPreviousFailureLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.stopLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_FAILURES_ONLY;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRun;

public class TestViewer extends Composite {

    private TestRun currentTestRun;
    private TreeViewer viewer;
    private ToolBar toolBar;
    private ToolBarManager toolBarManager;
    private ShowFailuresOnlyAction showFailuresOnlyAction;
    private ShowPreviousFailureAction showPreviousFailureAction;
    private ShowNextFailureAction showNextFailureAction;
    private ShowFailuresOnlyFilter showFailuresOnlyFilter;
    private RelaunchAction relaunchAction;
    private StopAction stopAction;

    public TestViewer(Composite parent) {
        super(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        createToolBar();
        createViewer();
    }

    private void createToolBar() {
        showNextFailureAction = new ShowNextFailureAction();
        showPreviousFailureAction = new ShowPreviousFailureAction();
        showFailuresOnlyAction = new ShowFailuresOnlyAction();
        showFailuresOnlyFilter = new ShowFailuresOnlyFilter();
        relaunchAction = new RelaunchAction();
        stopAction = new StopAction();

        toolBar = new ToolBar(this, SWT.FLAT | SWT.WRAP);
        toolBar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).create());
        toolBarManager = new ToolBarManager(toolBar);
        toolBarManager.add(showNextFailureAction);
        toolBarManager.add(showPreviousFailureAction);
        toolBarManager.add(showFailuresOnlyAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(relaunchAction);
        toolBarManager.add(stopAction);
        toolBarManager.update(true);
    }    

    private void createViewer() {
        viewer = new TreeViewer(this, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new TestContentProvider());
        viewer.setLabelProvider(new TestLabelProvider());
        viewer.getControl().setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
    }
    
    public void setCurrentTestRun(TestRun currentTestRun) {
        this.currentTestRun = currentTestRun;
        viewer.setInput(currentTestRun);
        updateView();
    }

    public void updateView() {
        viewer.refresh();

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
    
    public TreeViewer getViewer() {
        return viewer;
    }

    private class TestContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof TestRun) {
                TestRun testRun = (TestRun) inputElement;
                return testRun.getTestElements().toArray();
            }
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            return null;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            return false;
        }

    }

    private static class TestLabelProvider extends StyledCellLabelProvider {

        @Override
        public void update(ViewerCell cell) {
            TestElement element = (TestElement) cell.getElement();
            cell.setText(element.getName());

            Image image = null;
            switch(element.getState()) {
            case RUNNING: image = getImage(TEST_RUNNING); break;
            case SUCCESS: image = getImage(TEST_SUCCESS); break;
            case FAILURE: image = getImage(TEST_FAILED); break;
            case ERROR: image = getImage(TEST_ERROR); break;
            default: image = getImage(TEST); break;
            }

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
            return select(((TestElement) element));
        }

        public boolean select(TestElement testElement) {
            State state = testElement.getState();
            if (state == State.FAILURE || state == State.ERROR) {
                return true;
            }
            return false;
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