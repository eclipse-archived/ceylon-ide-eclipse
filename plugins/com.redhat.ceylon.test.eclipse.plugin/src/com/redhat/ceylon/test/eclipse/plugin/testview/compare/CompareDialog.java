package com.redhat.ceylon.test.eclipse.plugin.testview.compare;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.compareValuesDlgActual;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.compareValuesDlgExpected;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.compareValuesDlgOk;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.compareValuesDlgTitle;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;

public class CompareDialog extends TrayDialog {

    public static final String PREFIX_SUFFIX_PROPERTY = "com.redhat.ceylon.test.eclipse.plugin.testview.compare.CompareResultDialog.prefixSuffix";

    // Lengths of common prefix and suffix. Note: this array is passed to the DamagerRepairer and the lengths are updated on content change.
    private final int[] prefixSuffix = new int[2];
    
    private CompareViewerPane compareViewerPane;
    private TextMergeViewer viewer;
    private String testName;
    private String expectedValue;
    private String actualValue;

    public CompareDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle((getShellStyle() & ~SWT.APPLICATION_MODAL) | SWT.TOOL);
        setBlockOnOpen(false);
        setHelpAvailable(false);
    }

    public void setTestElement(TestElement testElement) {
        testName = testElement.getQualifiedName();
        expectedValue = testElement.getExpectedValue();
        actualValue = testElement.getActualValue();
    
        updateView();
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        String name = getClass().getName();
        IDialogSettings dialogSettings = CeylonTestPlugin.getDefault().getDialogSettings();
        IDialogSettings dialogSettingsSection = dialogSettings.getSection(name);
        if (dialogSettingsSection == null) {
            dialogSettingsSection = dialogSettings.addNewSection(name);
        }
        return dialogSettingsSection;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(compareValuesDlgTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(1, false));

        createCompareViewerPane(composite);
        createPreviewer();
        applyDialogFont(parent);
        
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, compareValuesDlgOk, true);
    }

    private void createCompareViewerPane(Composite composite) {
        compareViewerPane = new CompareViewerPane(composite, SWT.BORDER | SWT.FLAT);
        compareViewerPane.setLayoutData(GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.FILL).grab(true, true)
                .hint(convertWidthInCharsToPixels(120), convertHeightInCharsToPixels(13)).create());
    }

    private void createPreviewer() {
        final CompareConfiguration compareConfiguration = new CompareConfiguration();
        compareConfiguration.setLeftLabel(compareValuesDlgExpected);
        compareConfiguration.setLeftEditable(false);
        compareConfiguration.setRightLabel(compareValuesDlgActual);
        compareConfiguration.setRightEditable(false);
        compareConfiguration.setProperty(CompareConfiguration.IGNORE_WHITESPACE, Boolean.FALSE);
        compareConfiguration.setProperty(PREFIX_SUFFIX_PROPERTY, prefixSuffix);

        viewer = new CompareDialogMergeViewer(compareViewerPane, SWT.NONE, compareConfiguration);

        Control control = viewer.getControl();
        control.setLayoutData(new GridData(GridData.FILL_BOTH));
        control.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                compareConfiguration.dispose();
            }
        });

        compareViewerPane.setContent(control);
    }

    private void updateView() {
        updatePrefixSuffix();
        
        if (!viewer.getControl().isDisposed()) {
            viewer.setInput(new DiffNode(new CompareElement(expectedValue), new CompareElement(actualValue)));
            compareViewerPane.setText(testName);
        }
    }

    private void updatePrefixSuffix() {
        int end = Math.min(expectedValue.length(), actualValue.length());
        int i = 0;
        for (; i < end; i++)
            if (expectedValue.charAt(i) != actualValue.charAt(i))
                break;
        prefixSuffix[0] = i;
    
        int j = expectedValue.length() - 1;
        int k = actualValue.length() - 1;
        int l = 0;
        for (; k >= i && j >= i; k--, j--) {
            if (expectedValue.charAt(j) != actualValue.charAt(k))
                break;
            l++;
        }
        prefixSuffix[1] = l;
    }

}
