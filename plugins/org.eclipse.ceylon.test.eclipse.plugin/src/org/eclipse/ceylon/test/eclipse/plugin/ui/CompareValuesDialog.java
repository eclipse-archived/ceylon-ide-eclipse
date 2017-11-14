/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.ui;

import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareValuesDlgActual;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareValuesDlgExpected;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareValuesDlgOk;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.compareValuesDlgTitle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import org.eclipse.ceylon.test.eclipse.plugin.model.TestElement;

public class CompareValuesDialog extends TrayDialog {

    public static final String PREFIX_SUFFIX_PROPERTY = "org.eclipse.ceylon.test.eclipse.plugin.testview.compare.CompareResultDialog.prefixSuffix";

    // Lengths of common prefix and suffix. Note: this array is passed to the DamagerRepairer and the lengths are updated on content change.
    private final int[] prefixSuffix = new int[2];

    private CompareViewerPane compareViewerPane;
    private TextMergeViewer viewer;
    private String testName;
    private String actualValue;
    private String expectedValue;

    public CompareValuesDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle((getShellStyle() & ~SWT.APPLICATION_MODAL) | SWT.TOOL);
        setBlockOnOpen(false);
        setHelpAvailable(false);
    }

    public void setTestElement(TestElement testElement) {
        testName = testElement.getQualifiedName();
        actualValue = testElement.getActualValue();
        expectedValue = testElement.getExpectedValue();

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
        compareConfiguration.setLeftLabel(compareValuesDlgActual);
        compareConfiguration.setLeftEditable(false);
        compareConfiguration.setRightLabel(compareValuesDlgExpected);
        compareConfiguration.setRightEditable(false);
        compareConfiguration.setProperty(CompareConfiguration.IGNORE_WHITESPACE, Boolean.FALSE);
        compareConfiguration.setProperty(PREFIX_SUFFIX_PROPERTY, prefixSuffix);

        viewer = new CompareValuesMergeViewer(compareViewerPane, SWT.NONE, compareConfiguration);

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
            viewer.setInput(new DiffNode(new CompareValueElement(actualValue), new CompareValueElement(expectedValue)));
            compareViewerPane.setText(testName);
        }
    }

    private void updatePrefixSuffix() {
        int end = Math.min(actualValue.length(), expectedValue.length());
        int i = 0;
        for (; i < end; i++)
            if (actualValue.charAt(i) != expectedValue.charAt(i))
                break;
        prefixSuffix[0] = i;

        int j = actualValue.length() - 1;
        int k = expectedValue.length() - 1;
        int l = 0;
        for (; k >= i && j >= i; k--, j--) {
            if (actualValue.charAt(j) != expectedValue.charAt(k))
                break;
            l++;
        }
        prefixSuffix[1] = l;
    }

    public class CompareValueElement implements ITypedElement, IEncodedStreamContentAccessor {

        private final String content;

        public CompareValueElement(String content) {
            this.content = content;
        }

        @Override
        public String getName() {
            return "<no name>";
        }

        @Override
        public Image getImage() {
            return null;
        }

        @Override
        public String getType() {
            return "txt";
        }

        @Override
        public String getCharset() throws CoreException {
            return "UTF-8";
        }

        @Override
        public InputStream getContents() {
            try {
                return new ByteArrayInputStream(content.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return new ByteArrayInputStream(content.getBytes());
            }
        }

    }

    private static class CompareValuesMergeViewer extends TextMergeViewer {

        public CompareValuesMergeViewer(Composite parent, int style, CompareConfiguration configuration) {
            super(parent, style, configuration);
        }

        @Override
        protected void configureTextViewer(TextViewer textViewer) {
            if (textViewer instanceof SourceViewer) {
                int[] prefixSuffixOffsets = (int[]) getCompareConfiguration().getProperty(CompareValuesDialog.PREFIX_SUFFIX_PROPERTY);
                ((SourceViewer)textViewer).configure(new CompareValuesViewerConfiguration(prefixSuffixOffsets));
            }
        }

    }

    private static class CompareValuesDamagerRepairer implements IPresentationDamager, IPresentationRepairer {

        private IDocument document;
        private final int[] prefixSuffixOffsets2;

        public CompareValuesDamagerRepairer(int[] prefixSuffixOffsets) {
            this.prefixSuffixOffsets2 = prefixSuffixOffsets;
        }

        @Override
        public void setDocument(IDocument document) {
            this.document = document;
        }

        @Override
        public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, boolean changed) {
            return new Region(0, document.getLength());
        }

        @Override
        public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
            presentation.setDefaultStyleRange(new StyleRange(0, document.getLength(), null, null));
            int prefix = prefixSuffixOffsets2[0];
            int suffix = prefixSuffixOffsets2[1];
            TextAttribute attr = new TextAttribute(Display.getDefault().getSystemColor(SWT.COLOR_RED), null, SWT.BOLD);
            presentation.addStyleRange(new StyleRange(prefix, document.getLength() - suffix - prefix, attr
                    .getForeground(), attr.getBackground(), attr.getStyle()));
        }

    }    

    private static class CompareValuesViewerConfiguration extends SourceViewerConfiguration {

        private final int[] prefixSuffixOffsets;

        public CompareValuesViewerConfiguration(int[] prefixSuffixOffsets) {
            this.prefixSuffixOffsets = prefixSuffixOffsets;
        }

        @Override
        public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
            PresentationReconciler reconciler = new PresentationReconciler();
            CompareValuesDamagerRepairer dr = new CompareValuesDamagerRepairer(prefixSuffixOffsets);
            reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
            reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
            return reconciler;
        }

    }    

}