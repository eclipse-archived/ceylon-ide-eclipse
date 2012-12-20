package com.redhat.ceylon.test.eclipse.plugin.testview.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

public class CompareDialogMergeViewer extends TextMergeViewer {

    public CompareDialogMergeViewer(Composite parent, int style, CompareConfiguration configuration) {
        super(parent, style, configuration);
    }

    @Override
    protected void configureTextViewer(TextViewer textViewer) {
        if (textViewer instanceof SourceViewer) {
            int[] prefixSuffixOffsets = (int[]) getCompareConfiguration().getProperty(CompareDialog.PREFIX_SUFFIX_PROPERTY);
            ((SourceViewer)textViewer).configure(new CompareDialogViewerConfiguration(prefixSuffixOffsets));
        }
    }
    
}