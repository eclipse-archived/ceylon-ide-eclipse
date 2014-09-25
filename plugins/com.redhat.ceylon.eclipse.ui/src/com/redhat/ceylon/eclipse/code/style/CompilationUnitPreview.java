package com.redhat.ceylon.eclipse.code.style;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatterExtension;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.swt.widgets.Composite;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CompilationUnitPreview extends CeylonPreview {

    private String fPreviewText;

    public CompilationUnitPreview(FormatterPreferences workingValues,
            Composite parent) {

        super(workingValues, parent);
    }

    @Override
    protected void doFormatPreview() {
        if (fPreviewText == null) {
            fPreviewDocument.set("");
            return;
        }
        fPreviewDocument.set(fPreviewText);

        fSourceViewer.setRedraw(false);
        final IFormattingContext context = new CeylonFormattingContext();
        try {
            final IContentFormatter formatter = this.viewerConfiguration
                    .getContentFormatter(fSourceViewer);
            if (formatter instanceof IContentFormatterExtension) {
                final IContentFormatterExtension extension = (IContentFormatterExtension) formatter;
                context.setProperty(
                        FormattingContextProperties.CONTEXT_PREFERENCES,
                        this.workingValues);
                context.setProperty(
                        FormattingContextProperties.CONTEXT_DOCUMENT,
                        Boolean.valueOf(true));
                extension.format(fPreviewDocument, context);
            } else
                formatter.format(fPreviewDocument, new Region(0,
                        fPreviewDocument.getLength()));
        } catch (Exception e) {
            final IStatus status = new Status(IStatus.ERROR,
                    CeylonPlugin.PLUGIN_ID, 10001,
                    "Internal Formatter Preview Exception", e);
            CeylonPlugin.getInstance().getLog().log(status);
        } finally {
            context.dispose();
            fSourceViewer.setRedraw(true);
        }
    }

    public void setPreviewText(String previewText) {
        if (previewText == null)
            throw new IllegalArgumentException();
        fPreviewText = previewText;
        update();
    }
}
