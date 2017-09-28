package org.eclipse.ceylon.test.eclipse.plugin.ui;

import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STATE_ADDED;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STATE_CHANGED;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STATE_FIXED;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STATE_REGRESSED_ERROR;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STATE_REGRESSED_FAILURE;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STATE_REMOVED;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STATE_UNCHANGED;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_ERROR;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_FAILED;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_SKIPPED;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_SUCCESS;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgAdded;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgChanged;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgElapsedTime;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgErrors;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgFailures;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgFixed;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgSkipped;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgPlatform;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgRegressedError;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgRegressedFailure;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgRemoved;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgRunName;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgShowOnly;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgStartDate;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgSuccess;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgTotal;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareRunsDlgUnchanged;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.platformJs;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.platformJvm;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE_JS;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getDisplay;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getElapsedTimeInSeconds;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getTestStateImage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry;
import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import org.eclipse.ceylon.test.eclipse.plugin.model.TestElement;
import org.eclipse.ceylon.test.eclipse.plugin.model.TestElement.State;
import org.eclipse.ceylon.test.eclipse.plugin.model.TestElementComparatorByName;
import org.eclipse.ceylon.test.eclipse.plugin.model.TestRun;

public class CompareRunsDialog extends TrayDialog {

    private final DateFormat startDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    private final TestRun testRun1;
    private final TestRun testRun2;

    private final List<ComparedElement> comparedElements = new ArrayList<ComparedElement>();
    private final Map<ComparedState, Integer> comparedStateCounts = new HashMap<ComparedState, Integer>();

    private ShowOnlyFixedAction showOnlyFixedAction;
    private ShowOnlyRegressedErrorAction showOnlyRegressedErrorAction;
    private ShowOnlyRegressedFailureAction showOnlyRegressedFailureAction;
    private ShowOnlyChangedAction showonlyChangedAction;
    private ShowOnlyUnchangedAction showOnlyUnchangedAction;
    private ShowOnlyAddedAction showOnlyAddedAction;
    private ShowOnlyRemovedAction showOnlyRemovedAction;

    private Composite panel;
    private SashForm sashForm;
    private TableViewer testsViewer;
    private StyledText exceptionText1;
    private StyledText exceptionText2;

    public CompareRunsDialog(Shell shell, TestRun testRun1, TestRun testRun2) {
        super(shell);
        this.testRun1 = testRun1;
        this.testRun2 = testRun2;
        initComparedElements();
        initComparedStateCounts();
    }

    private void initComparedElements() {
        TestElement[] testElements1 = testRun1.getAtomicTests().toArray(new TestElement[] {});
        TestElement[] testElements2 = testRun2.getAtomicTests().toArray(new TestElement[] {});

        Arrays.sort(testElements1, TestElementComparatorByName.INSTANCE);
        Arrays.sort(testElements2, TestElementComparatorByName.INSTANCE);

        int index1 = 0;
        int index2 = 0;
        TestElement testElement1 = null;
        TestElement testElement2 = null;
        ComparedElement comparedElement = null;

        while (index1 < testElements1.length || index2 < testElements2.length) {
            testElement1 = (index1 < testElements1.length) ? testElements1[index1] : null;
            testElement2 = (index2 < testElements2.length) ? testElements2[index2] : null;

            int compare = TestElementComparatorByName.INSTANCE.compare(testElement1, testElement2);
            if (compare == 0) {
                index1++;
                index2++;
                comparedElement = new ComparedElement(testElement1, testElement2);
            } else if (compare < 0) {
                index1++;
                comparedElement = new ComparedElement(testElement1, null);
            } else if (compare > 0) {
                index2++;
                comparedElement = new ComparedElement(null, testElement2);
            }

            comparedElements.add(comparedElement);
        }
    }

    private void initComparedStateCounts() {
        for (ComparedState comparedState : ComparedState.values()) {
            comparedStateCounts.put(comparedState, 0);
        }
        for (ComparedElement comparedElement : comparedElements) {
            ComparedState state = comparedElement.getState();
            Integer count = comparedStateCounts.get(state);
            comparedStateCounts.put(state, ++count);
        }
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void constrainShellSize() {
        getShell().setMaximized(true);
        super.constrainShellSize();
    };

    @Override
    protected void configureShell(Shell shell) {
        shell.setText("Compare Test Runs");
        setBlockOnOpen(true);
        setHelpAvailable(false);
        super.configureShell(shell);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 0;

        panel = new Composite(parent, SWT.NONE);
        panel.setLayout(layout);
        panel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
        parent.setLayout(new GridLayout(1, false));

        createTestRunSummary(testRun1);
        createTestRunSummary(testRun2);
        createToolBar();

        sashForm = new SashForm(panel, SWT.VERTICAL);
        sashForm.setSashWidth(5);
        sashForm.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).create());

        createTestsViewer();
        createExceptionsViewer();

        sashForm.setWeights(new int[] { 66, 33 });

        return parent;
    }

    private void createTestRunSummary(TestRun testRun) {
        Color backgroundColor = getDisplay().getSystemColor(SWT.COLOR_WHITE);
        Color labelForegroundColor = JFaceResources.getColorRegistry().get(JFacePreferences.QUALIFIER_COLOR);

        Group g = new Group(panel, SWT.SHADOW_IN);
        g.setLayout(new GridLayout(2, false));
        g.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());
        g.setBackground(backgroundColor);

        Label runNameLabel = new Label(g, SWT.NONE);
        runNameLabel.setText(compareRunsDlgRunName);
        runNameLabel.setBackground(backgroundColor);
        runNameLabel.setForeground(labelForegroundColor);

        Text runNameText = new Text(g, SWT.READ_ONLY);
        runNameText.setText(testRun.getRunName());
        runNameText.setBackground(backgroundColor);
        runNameText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());
        
        Label platformLabel = new Label(g, SWT.NONE);
        platformLabel.setText(compareRunsDlgPlatform);
        platformLabel.setBackground(backgroundColor);
        platformLabel.setForeground(labelForegroundColor);

        Text platformText = new Text(g, SWT.READ_ONLY);
        platformText.setBackground(backgroundColor);
        platformText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());
        try {
            if (LAUNCH_CONFIG_TYPE_JS.equals(testRun.getLaunch().getLaunchConfiguration().getType().getIdentifier())) {
                platformText.setText(platformJs);
            } else {
                platformText.setText(platformJvm);
            }
        } catch (CoreException e) {
            CeylonTestPlugin.logError("", e);
        }

        Label startDateLabel = new Label(g, SWT.NONE);
        startDateLabel.setText(compareRunsDlgStartDate);
        startDateLabel.setBackground(backgroundColor);
        startDateLabel.setForeground(labelForegroundColor);

        Text startDateText = new Text(g, SWT.READ_ONLY);
        startDateText.setText(startDateFormat.format(testRun.getRunStartDate()));
        startDateText.setBackground(backgroundColor);
        startDateText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());

        Label elapsedTimeLabel = new Label(g, SWT.NONE);
        elapsedTimeLabel.setText(compareRunsDlgElapsedTime);
        elapsedTimeLabel.setBackground(backgroundColor);
        elapsedTimeLabel.setForeground(labelForegroundColor);

        Text elapsedTimeText = new Text(g, SWT.READ_ONLY);
        elapsedTimeText.setText(getElapsedTimeInSeconds(testRun.getRunElapsedTimeInMilis()) + " s");
        elapsedTimeText.setBackground(backgroundColor);
        elapsedTimeText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());

        Composite c = new Composite(g, SWT.NONE);
        c.setLayout(new GridLayout(9, false));
        c.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(2, 1).create());
        c.setBackground(backgroundColor);

        Label totalIcon = new Label(c, SWT.NONE);
        totalIcon.setImage(CeylonTestImageRegistry.getImage(TEST));
        totalIcon.setBackground(backgroundColor);

        Label totalLabel = new Label(c, SWT.NONE);
        totalLabel.setText(compareRunsDlgTotal);
        totalLabel.setBackground(backgroundColor);
        totalLabel.setForeground(labelForegroundColor);

        Text totalText = new Text(c, SWT.READ_ONLY);
        totalText.setText(Integer.toString(testRun.getTotalCount()));
        totalText.setBackground(backgroundColor);

        Label successIcon = new Label(c, SWT.NONE);
        successIcon.setImage(CeylonTestImageRegistry.getImage(TEST_SUCCESS));
        successIcon.setBackground(backgroundColor);
        successIcon.setLayoutData(GridDataFactory.swtDefaults().indent(50, 0).create());

        Label successLabel = new Label(c, SWT.NONE);
        successLabel.setText(compareRunsDlgSuccess);
        successLabel.setBackground(backgroundColor);
        successLabel.setForeground(labelForegroundColor);

        Text successText = new Text(c, SWT.READ_ONLY);
        successText.setText(Integer.toString(testRun.getSuccessCount()));
        successText.setBackground(backgroundColor);

        Label failureIcon = new Label(c, SWT.NONE);
        failureIcon.setImage(CeylonTestImageRegistry.getImage(TEST_FAILED));
        failureIcon.setBackground(backgroundColor);
        failureIcon.setLayoutData(GridDataFactory.swtDefaults().indent(50, 0).create());

        Label failureLabel = new Label(c, SWT.NONE);
        failureLabel.setText(compareRunsDlgFailures);
        failureLabel.setBackground(backgroundColor);
        failureLabel.setForeground(labelForegroundColor);

        Text failureText = new Text(c, SWT.READ_ONLY);
        failureText.setText(Integer.toString(testRun.getFailureCount()));
        failureText.setBackground(backgroundColor);
        
        Label span1 = new Label(c, SWT.NONE);
        span1.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());

        Label skippedIcon = new Label(c, SWT.NONE);
        skippedIcon.setImage(CeylonTestImageRegistry.getImage(TEST_SKIPPED));
        skippedIcon.setBackground(backgroundColor);
        skippedIcon.setLayoutData(GridDataFactory.swtDefaults().indent(50, 0).create());

        Label skippedLabel = new Label(c, SWT.NONE);
        skippedLabel.setText(compareRunsDlgSkipped);
        skippedLabel.setBackground(backgroundColor);
        skippedLabel.setForeground(labelForegroundColor);

        Text skippedText = new Text(c, SWT.READ_ONLY);
        skippedText.setText(Integer.toString(testRun.getSkippedOrAbortedCount()));
        skippedText.setBackground(backgroundColor);
        
        Label errorIcon = new Label(c, SWT.NONE);
        errorIcon.setImage(CeylonTestImageRegistry.getImage(TEST_ERROR));
        errorIcon.setBackground(backgroundColor);
        errorIcon.setLayoutData(GridDataFactory.swtDefaults().indent(50, 0).create());

        Label errorLabel = new Label(c, SWT.NONE);
        errorLabel.setText(compareRunsDlgErrors);
        errorLabel.setBackground(backgroundColor);
        errorLabel.setForeground(labelForegroundColor);

        Text errorText = new Text(c, SWT.READ_ONLY);
        errorText.setText(Integer.toString(testRun.getErrorCount()));
        errorText.setBackground(backgroundColor);
    }

    private void createToolBar() {
        showOnlyFixedAction = new ShowOnlyFixedAction();
        showOnlyRegressedFailureAction = new ShowOnlyRegressedFailureAction();
        showOnlyRegressedErrorAction = new ShowOnlyRegressedErrorAction();
        showonlyChangedAction = new ShowOnlyChangedAction();
        showOnlyUnchangedAction = new ShowOnlyUnchangedAction();
        showOnlyAddedAction = new ShowOnlyAddedAction();
        showOnlyRemovedAction = new ShowOnlyRemovedAction();

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;

        Composite toolBarPanel = new Composite(panel, SWT.NONE);
        toolBarPanel.setLayout(layout);
        toolBarPanel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(2, 1).indent(0, 20).create());

        Label showOnlyLabel = new Label(toolBarPanel, SWT.NONE);
        showOnlyLabel.setText(compareRunsDlgShowOnly);

        ToolBar toolBar = new ToolBar(toolBarPanel, SWT.FLAT | SWT.RIGHT);
        ToolBarManager toolBarManager = new ToolBarManager(toolBar);
        toolBarManager.add(createActionContributionItem(showOnlyFixedAction));
        toolBarManager.add(createActionContributionItem(showOnlyRegressedFailureAction));
        toolBarManager.add(createActionContributionItem(showOnlyRegressedErrorAction));
        toolBarManager.add(createActionContributionItem(showonlyChangedAction));
        toolBarManager.add(createActionContributionItem(showOnlyUnchangedAction));
        toolBarManager.add(createActionContributionItem(showOnlyAddedAction));
        toolBarManager.add(createActionContributionItem(showOnlyRemovedAction));
        toolBarManager.update(true);
    }

    private void createExceptionsViewer() {
        Color backgroundColor = getDisplay().getSystemColor(SWT.COLOR_WHITE);

        SashForm exceptionSashForm = new SashForm(sashForm, SWT.HORIZONTAL);
        exceptionSashForm.setSashWidth(5);

        Group exceptionGroup1 = new Group(exceptionSashForm, SWT.SHADOW_IN);
        exceptionGroup1.setLayout(new GridLayout());
        exceptionGroup1.setBackground(backgroundColor);

        exceptionText1 = new StyledText(exceptionGroup1, SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
        exceptionText1.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());

        Group exceptionGroup2 = new Group(exceptionSashForm, SWT.SHADOW_IN);
        exceptionGroup2.setLayout(new GridLayout());
        exceptionGroup2.setBackground(backgroundColor);

        exceptionText2 = new StyledText(exceptionGroup2, SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
        exceptionText2.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
    }

    private void createTestsViewer() {
        testsViewer = new TableViewer(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        createColumnName1();
        createColumnCompareState();
        createColumnName2();

        testsViewer.setContentProvider(ArrayContentProvider.getInstance());
        testsViewer.setInput(comparedElements);
        testsViewer.addFilter(new ShowOnlyViewerFilter());
        testsViewer.getTable().setHeaderVisible(true);
        testsViewer.getTable().setLinesVisible(true);
        testsViewer.getTable().setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).create());
        testsViewer.getTable().addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                int witdh = (testsViewer.getTable().getSize().x / 2) - testsViewer.getTable().getColumn(1).getWidth() - 1;
                testsViewer.getTable().getColumn(0).setWidth(witdh);
                testsViewer.getTable().getColumn(2).setWidth(witdh);
            }
        });
        testsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ComparedElement comparedElement = null;

                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (!selection.isEmpty()) {
                    comparedElement = (ComparedElement) selection.getFirstElement();
                }

                String exception1 = "";
                String exception2 = "";
                if (comparedElement != null) {
                    if (comparedElement.getTestElement1() != null && 
                            comparedElement.getTestElement1().getException() != null && 
                            comparedElement.getTestElement1().getState() != State.SKIPPED_OR_ABORTED) {
                        exception1 = comparedElement.getTestElement1().getException();
                    }
                    if (comparedElement.getTestElement2() != null && 
                            comparedElement.getTestElement2().getException() != null && 
                            comparedElement.getTestElement2().getState() != State.SKIPPED_OR_ABORTED) {
                        exception2 = comparedElement.getTestElement2().getException();
                    }
                }

                exceptionText1.setText(exception1);
                exceptionText2.setText(exception2);
            }
        });
    }

    private void createColumnName1() {
        TableViewerColumn colName1 = new TableViewerColumn(testsViewer, SWT.NONE);
        colName1.getColumn().setWidth(100);
        colName1.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                super.update(cell);
                TestElement testElement = ((ComparedElement) cell.getElement()).getTestElement1();
                updateViewerCell(cell, testElement);
            }
        });
    }

    private void createColumnName2() {
        TableViewerColumn colName2 = new TableViewerColumn(testsViewer, SWT.NONE);
        colName2.getColumn().setWidth(100);
        colName2.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                super.update(cell);
                TestElement testElement = ((ComparedElement) cell.getElement()).getTestElement2();
                updateViewerCell(cell, testElement);
            }
        });
    }

    private void createColumnCompareState() {
        TableViewerColumn colCompareState = new TableViewerColumn(testsViewer, SWT.NONE);
        colCompareState.getColumn().setWidth(50);
        colCompareState.getColumn().setResizable(false);
        colCompareState.getColumn().setAlignment(SWT.CENTER);
        colCompareState.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return "";
            }
        });

        // workaround: image alignment doesn't work
        testsViewer.getTable().addListener(SWT.PaintItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.index == 1) {
                    TableItem tableItem = (TableItem) event.item;
                    Image image = ((ComparedElement) tableItem.getData()).getStateIcon();
                    if (image != null) {
                        int w = testsViewer.getTable().getColumn(event.index).getWidth();
                        int h = tableItem.getBounds().height;

                        int x = (w / 2 - image.getBounds().width / 2);
                        if (x <= 0)
                            x = event.x;
                        else
                            x += event.x;

                        int y = (h / 2 - image.getBounds().height / 2);
                        if (y <= 0)
                            y = event.y;
                        else
                            y += event.y;

                        event.gc.drawImage(image, x, y);
                    }
                }
            }
        });
    }

    private ActionContributionItem createActionContributionItem(Action action) {
        ActionContributionItem actionContributionItem = new ActionContributionItem(action);
        actionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
        return actionContributionItem;
    }

    private void updateViewerCell(ViewerCell cell, TestElement testElement) {
        if( testElement != null ) {
            StyledString styledText = new StyledString();
            styledText.append(testElement.getQualifiedName());
            if( testElement.getVariant() != null && testElement.getVariantIndex() != null ) {
                styledText.append(" #"+testElement.getVariantIndex()+" "+testElement.getVariant(), StyledString.QUALIFIER_STYLER);
            }
            styledText.append(" [" + getElapsedTimeInSeconds(testElement.getElapsedTimeInMilis()) + " s]", StyledString.COUNTER_STYLER);

            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
            cell.setImage(getTestStateImage(testElement));
        }
    }

    private class ShowOnlyFixedAction extends Action {

        public ShowOnlyFixedAction() {
            super(compareRunsDlgFixed, AS_CHECK_BOX);
            setText(compareRunsDlgFixed + " (" + comparedStateCounts.get(ComparedState.FIXED) + ")");
            setEnabled(comparedStateCounts.get(ComparedState.FIXED) != 0);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STATE_FIXED));
        }

        @Override
        public void run() {
            testsViewer.refresh();
        }

    }

    private class ShowOnlyRegressedErrorAction extends Action {

        public ShowOnlyRegressedErrorAction() {
            super(compareRunsDlgRegressedError, AS_CHECK_BOX);
            setText(compareRunsDlgRegressedError + " (" + comparedStateCounts.get(ComparedState.REGRESSED_ERROR) + ")");
            setEnabled(comparedStateCounts.get(ComparedState.REGRESSED_ERROR) != 0);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STATE_REGRESSED_ERROR));
        }

        @Override
        public void run() {
            testsViewer.refresh();
        }

    }

    private class ShowOnlyRegressedFailureAction extends Action {

        public ShowOnlyRegressedFailureAction() {
            super(compareRunsDlgRegressedFailure, AS_CHECK_BOX);
            setText(compareRunsDlgRegressedFailure + " (" + comparedStateCounts.get(ComparedState.REGRESSED_FAILURE) + ")");
            setEnabled(comparedStateCounts.get(ComparedState.REGRESSED_FAILURE) != 0);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STATE_REGRESSED_FAILURE));
        }

        @Override
        public void run() {
            testsViewer.refresh();
        }

    }

    private class ShowOnlyChangedAction extends Action {

        public ShowOnlyChangedAction() {
            super(compareRunsDlgChanged, AS_CHECK_BOX);
            setText(compareRunsDlgChanged + " (" + comparedStateCounts.get(ComparedState.CHANGED) + ")");
            setEnabled(comparedStateCounts.get(ComparedState.CHANGED) != 0);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STATE_CHANGED));
        }

        @Override
        public void run() {
            testsViewer.refresh();
        }

    }

    private class ShowOnlyUnchangedAction extends Action {

        public ShowOnlyUnchangedAction() {
            super(compareRunsDlgUnchanged, AS_CHECK_BOX);
            setText(compareRunsDlgUnchanged + " (" + comparedStateCounts.get(ComparedState.UNCHANGED) + ")");
            setEnabled(comparedStateCounts.get(ComparedState.UNCHANGED) != 0);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STATE_UNCHANGED));
        }

        @Override
        public void run() {
            testsViewer.refresh();
        }

    }

    private class ShowOnlyAddedAction extends Action {

        public ShowOnlyAddedAction() {
            super(compareRunsDlgAdded, AS_CHECK_BOX);
            setText(compareRunsDlgAdded + " (" + comparedStateCounts.get(ComparedState.ADDED) + ")");
            setEnabled(comparedStateCounts.get(ComparedState.ADDED) != 0);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STATE_ADDED));
        }

        @Override
        public void run() {
            testsViewer.refresh();
        }

    }

    private class ShowOnlyRemovedAction extends Action {

        public ShowOnlyRemovedAction() {
            super(compareRunsDlgRemoved, AS_CHECK_BOX);
            setText(compareRunsDlgRemoved + " (" + comparedStateCounts.get(ComparedState.REMOVED) + ")");
            setEnabled(comparedStateCounts.get(ComparedState.REMOVED) != 0);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STATE_REMOVED));
        }

        @Override
        public void run() {
            testsViewer.refresh();
        }

    }

    private class ShowOnlyViewerFilter extends ViewerFilter {

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            boolean added = showOnlyAddedAction.isChecked();
            boolean removed = showOnlyRemovedAction.isChecked();
            boolean changed = showonlyChangedAction.isChecked();
            boolean unchanged = showOnlyUnchangedAction.isChecked();
            boolean fixed = showOnlyFixedAction.isChecked();
            boolean regressedError = showOnlyRegressedErrorAction.isChecked();
            boolean regressedFailure = showOnlyRegressedFailureAction.isChecked();

            if (added || removed || changed || unchanged || fixed || regressedError || regressedFailure) {
                ComparedState state = ((ComparedElement) element).getState();
                switch (state) {
                case ADDED:
                    return added;
                case REMOVED:
                    return removed;
                case CHANGED:
                    return changed;
                case UNCHANGED:
                    return unchanged;
                case FIXED:
                    return fixed;
                case REGRESSED_ERROR:
                    return regressedError;
                case REGRESSED_FAILURE:
                    return regressedFailure;
                }
            }

            return true;
        }
    }

    private static enum ComparedState {

        FIXED,
        REGRESSED_ERROR,
        REGRESSED_FAILURE,
        CHANGED,
        UNCHANGED,
        ADDED,
        REMOVED

    }

    private static class ComparedElement {

        private final ComparedState state;
        private final TestElement testElement1;
        private final TestElement testElement2;

        public ComparedElement(TestElement testElement1, TestElement testElement2) {
            this.testElement1 = testElement1;
            this.testElement2 = testElement2;
            this.state = initState();
        }

        public TestElement getTestElement1() {
            return testElement1;
        }

        public TestElement getTestElement2() {
            return testElement2;
        }

        public ComparedState getState() {
            return state;
        }

        private ComparedState initState() {
            ComparedState state = null;

            if (testElement1 == null && testElement2 != null) {
                state = ComparedState.ADDED;
            } else if (testElement1 != null && testElement2 == null) {
                state = ComparedState.REMOVED;
            } else if (testElement1.getState() == State.SUCCESS && testElement2.getState() == State.SUCCESS) {
                state = ComparedState.UNCHANGED;
            } else if (testElement1.getState() == State.SKIPPED_OR_ABORTED && testElement2.getState() == State.SKIPPED_OR_ABORTED) {
                state = ComparedState.UNCHANGED;
            } else if (testElement1.getState() == State.ERROR && testElement2.getState() == State.ERROR) {
                state = isUnchangedException() ? ComparedState.UNCHANGED : ComparedState.CHANGED;
            } else if (testElement1.getState() == State.FAILURE && testElement2.getState() == State.FAILURE) {
                state = isUnchangedException() ? ComparedState.UNCHANGED : ComparedState.CHANGED;
            } else if (testElement1.getState() != State.ERROR && testElement2.getState() == State.ERROR) {
                state = ComparedState.REGRESSED_ERROR;
            } else if (testElement1.getState() != State.FAILURE && testElement2.getState() == State.FAILURE) {
                state = ComparedState.REGRESSED_FAILURE;
            } else if (testElement1.getState() != State.SUCCESS && testElement1.getState() != State.SKIPPED_OR_ABORTED && testElement2.getState() == State.SUCCESS) {
                state = ComparedState.FIXED;
            } else {
                state = ComparedState.CHANGED;
            }
            
            return state;
        }

        public Image getStateIcon() {
            Image image = null;
            switch (state) {
            case ADDED:
                image = CeylonTestImageRegistry.getImage(STATE_ADDED);
                break;
            case REMOVED:
                image = CeylonTestImageRegistry.getImage(STATE_REMOVED);
                break;
            case CHANGED:
                image = CeylonTestImageRegistry.getImage(STATE_CHANGED);
                break;
            case UNCHANGED:
                image = CeylonTestImageRegistry.getImage(STATE_UNCHANGED);
                break;
            case FIXED:
                image = CeylonTestImageRegistry.getImage(STATE_FIXED);
                break;
            case REGRESSED_ERROR:
                image = CeylonTestImageRegistry.getImage(STATE_REGRESSED_ERROR);
                break;
            case REGRESSED_FAILURE:
                image = CeylonTestImageRegistry.getImage(STATE_REGRESSED_FAILURE);
                break;
            }
            return image;
        }

        private boolean isUnchangedException() {
            return testElement1.getException().equals(testElement2.getException());
        }

    }

}