package com.redhat.ceylon.eclipse.code.style;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.BufferedTokenStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.MarginPainter;
import org.eclipse.jface.text.WhitespaceCharacterPainter;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import ceylon.formatter.format_;
import ceylon.formatter.options.FormattingOptions;
import ceylon.formatter.options.SparseFormattingOptions;
import ceylon.formatter.options.combinedOptions_;
import ceylon.language.Singleton;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.StringBuilderWriter;

public class CeylonPreview {

    private final class CeylonSourcePreviewerUpdater {

        final IPropertyChangeListener fontListener = new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(
                        PreferenceConstants.EDITOR_TEXT_FONT)) {
                    final Font font = JFaceResources
                            .getFont(PreferenceConstants.EDITOR_TEXT_FONT);
                    fSourceViewer.getTextWidget().setFont(font);
                    if (fMarginPainter != null) {
                        fMarginPainter.initialize();
                    }
                }
            }
        };

        final IPropertyChangeListener propertyListener = new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (affectsTextPresentation(event)) {
                    handlePropertyChangeEvent(event);
                    fSourceViewer.invalidateTextPresentation();
                }
            }

            private void handlePropertyChangeEvent(PropertyChangeEvent event) {
                // TODO do something more

            }

            private boolean affectsTextPresentation(PropertyChangeEvent event) {
                return true; // Always
            }
        };

        public CeylonSourcePreviewerUpdater() {

            JFaceResources.getFontRegistry().addListener(fontListener);
            EditorsUI.getPreferenceStore().addPropertyChangeListener(
                    propertyListener);

            fSourceViewer.getTextWidget().addDisposeListener(
                    new DisposeListener() {
                        public void widgetDisposed(DisposeEvent e) {
                            JFaceResources.getFontRegistry().removeListener(
                                    fontListener);
                            EditorsUI.getPreferenceStore()
                                    .removePropertyChangeListener(
                                            propertyListener);
                        }
                    });
        }
    }

    protected final CeylonSourceViewerConfiguration viewerConfiguration;
    protected final Document fPreviewDocument;
    protected final SourceViewer fSourceViewer;

    protected final MarginPainter fMarginPainter;

    protected FormatterPreferences workingValues;

    private int fTabSize = 0;
    private WhitespaceCharacterPainter fWhitespaceCharacterPainter;

    private String fPreviewText;
    private CeylonLexer fPreviewLexer;
    private CompilationUnit fPreviewCu;

    public CeylonPreview(FormatterPreferences workingValues, Composite parent) {

        fPreviewDocument = new Document();
        this.workingValues = workingValues;

        fSourceViewer = new CeylonSourceViewer(parent, null, null, false,
                SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        fSourceViewer.setEditable(false);
        Cursor arrowCursor = fSourceViewer.getTextWidget().getDisplay()
                .getSystemCursor(SWT.CURSOR_ARROW);
        fSourceViewer.getTextWidget().setCursor(arrowCursor);

        viewerConfiguration = new CeylonSourceViewerConfiguration(null);
        fSourceViewer.configure(viewerConfiguration);
        fSourceViewer.getTextWidget().setFont(
                JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT));

        fMarginPainter = new MarginPainter(fSourceViewer);
        final RGB rgb = PreferenceConverter
                .getColor(
                        EditorsUI.getPreferenceStore(),
                        AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR);
        fMarginPainter.setMarginRulerColor(new Color(fSourceViewer
                .getTextWidget().getDisplay(), rgb));
        fSourceViewer.addPainter(fMarginPainter);

        new CeylonSourcePreviewerUpdater();
        fSourceViewer.setDocument(fPreviewDocument);
    }

    public Control getControl() {
        return fSourceViewer.getControl();
    }

    public void update() {
        if (workingValues == null) {
            fPreviewDocument.set("");
            return;
        }

        // update the print margin
        final String value = workingValues
                .get(CeylonFormatterConstants.FORMATTER_LINE_SPLIT);
        final int lineWidth = getPositiveIntValue(value, 0);
        fMarginPainter.setMarginRulerColumn(lineWidth);

        // update the tab size
        final int tabSize = getPositiveIntValue(
                workingValues.get(CeylonFormatterConstants.FORMATTER_TAB_SIZE),
                0);
        if (tabSize != fTabSize)
            fSourceViewer.getTextWidget().setTabs(tabSize);
        fTabSize = tabSize;

        final StyledText widget = (StyledText) fSourceViewer.getControl();
        final int height = widget.getClientArea().height;
        final int top0 = widget.getTopPixel();

        final int totalPixels0 = getHeightOfAllLines(widget);
        final int topPixelRange0 = totalPixels0 > height ? totalPixels0
                - height : 0;

        widget.setRedraw(false);
        doFormatPreview();
        fSourceViewer.setSelection(null);

        final int totalPixels1 = getHeightOfAllLines(widget);
        final int topPixelRange1 = totalPixels1 > height ? totalPixels1
                - height : 0;

        final int top1 = topPixelRange0 > 0 ? (int) (topPixelRange1 * top0 / (double) topPixelRange0)
                : 0;
        widget.setTopPixel(top1);
        widget.setRedraw(true);
    }

    private int getHeightOfAllLines(StyledText styledText) {
        int height = 0;
        int lineCount = styledText.getLineCount();
        for (int i = 0; i < lineCount; i++)
            height = height
                    + styledText.getLineHeight(styledText.getOffsetAtLine(i));
        return height;
    }

    protected void doFormatPreview() {
        if (fPreviewText == null) {
            fPreviewDocument.set("");
            return;
        }
        fPreviewDocument.set(fPreviewText);
        if (fPreviewCu == null) {
            return;
        }

        fSourceViewer.setRedraw(false);
        final IFormattingContext context = new CeylonFormattingContext();
        try {
            final StringBuilder builder = new StringBuilder(
                    fPreviewDocument.getLength());
            context.setProperty(
                    FormattingContextProperties.CONTEXT_PREFERENCES,
                    combinedOptions_.combinedOptions(
                            workingValues.getOptions(),
                            new Singleton<SparseFormattingOptions>
                                (SparseFormattingOptions.$TypeDescriptor$, 
                                        CeylonStyle.getEclipseWsOptions(null)))
                    );
            context.setProperty(
                    FormattingContextProperties.CONTEXT_DOCUMENT,
                    Boolean.valueOf(true));
            fPreviewLexer.reset();
            format_.format(
                fPreviewCu,
                (FormattingOptions) context.getProperty(FormattingContextProperties.CONTEXT_PREFERENCES),
                new StringBuilderWriter(builder),
                new BufferedTokenStream(fPreviewLexer));
            fPreviewDocument.set(builder.toString());
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
        fPreviewLexer = new CeylonLexer(new ANTLRStringStream(previewText));
        try {
            fPreviewCu = new CeylonParser(new CommonTokenStream(
                    fPreviewLexer)).compilationUnit();
        } catch (RecognitionException re) {
            CeylonPlugin.getInstance().getLog()
                    .log(new Status(IStatus.WARNING, CeylonPlugin.PLUGIN_ID,
                            "Error parsing preview code, should not have happened"));
            fPreviewCu = null;
        }
        update();
    }

    private static int getPositiveIntValue(String string, int defaultValue) {
        try {
            int i = Integer.parseInt(string);
            if (i >= 0) {
                return i;
            }
        } catch (NumberFormatException e) {
        }
        return defaultValue;
    }

    public FormatterPreferences getWorkingValues() {
        return workingValues;
    }

    public void setWorkingValues(FormatterPreferences options) {
        workingValues = options;
    }

    public void showInvisibleCharacters(boolean enable) {
        if (enable) {
            if (fWhitespaceCharacterPainter == null) {
                fWhitespaceCharacterPainter = new WhitespaceCharacterPainter(
                        fSourceViewer);
                fSourceViewer.addPainter(fWhitespaceCharacterPainter);
            }
        } else {
            fSourceViewer.removePainter(fWhitespaceCharacterPainter);
            fWhitespaceCharacterPainter = null;
        }
    }
}
