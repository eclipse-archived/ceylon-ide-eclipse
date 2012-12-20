package com.redhat.ceylon.test.eclipse.plugin.testview.compare;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;

public class CompareDialogViewerConfiguration extends SourceViewerConfiguration {

    private static class SimpleDamagerRepairer implements IPresentationDamager, IPresentationRepairer {

        private IDocument document;
        private final int[] prefixSuffixOffsets2;

        public SimpleDamagerRepairer(int[] prefixSuffixOffsets) {
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

    private final int[] prefixSuffixOffsets;

    public CompareDialogViewerConfiguration(int[] prefixSuffixOffsets) {
        this.prefixSuffixOffsets = prefixSuffixOffsets;
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();
        CompareDialogViewerConfiguration.SimpleDamagerRepairer dr = new SimpleDamagerRepairer(prefixSuffixOffsets);
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        return reconciler;
    }

}