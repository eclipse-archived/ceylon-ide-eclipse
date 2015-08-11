package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.util.Escaping.toInitialUppercase;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonInitializerAnnotation;
import com.redhat.ceylon.eclipse.code.editor.RefinementAnnotation;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Highlights;

/**
 * The annotation information control shows informations about a given
 * {@link AnnotationInfo}. It can also show a toolbar
 * and a list of {@link ICompletionProposal}s.
 *
 * @since 3.4
 */
class AnnotationInformationControl 
        extends AbstractInformationControl 
        implements IInformationControlExtension2 {

    private final DefaultMarkerAnnotationAccess fMarkerAnnotationAccess;
    private Control fFocusControl;
    private AnnotationInfo fInput;
    private Composite fParent;
    private final ISchedulingRule retrievingQuickFixesRule = new ISchedulingRule() {
        @Override
        public boolean isConflicting(ISchedulingRule rule) {
            return rule == this;
        }
        
        @Override
        public boolean contains(ISchedulingRule rule) {
            return rule == this;
        }
    };
    AtomicBoolean quickFixesRetrieved = new AtomicBoolean(false);

    AnnotationInformationControl(Shell parentShell, 
            String statusFieldText) {
        super(parentShell, statusFieldText);
        fMarkerAnnotationAccess = 
                new DefaultMarkerAnnotationAccess();
        create();
    }

    AnnotationInformationControl(Shell parentShell, 
            ToolBarManager toolBarManager) {
        super(parentShell, toolBarManager);
        fMarkerAnnotationAccess = 
                new DefaultMarkerAnnotationAccess();
        create();
    }

    @Override
    public void setInformation(String information) {
        //replaced by IInformationControlExtension2#setInput
    }

    public Tree.CompilationUnit getTypecheckedRootNode(AnnotationInfo info) {
        if (info == null) {
            return null;
        }
        
        CeylonEditor editor = info.getEditor();
        if (editor == null) {
            return null;
        }
        
        CeylonParseController cpc = editor.getParseController();
        if (cpc == null || ! Stage.TYPE_ANALYSIS.equals(cpc.getStage())) {
            return null;
        }
        return cpc.getRootNode();
    }
    
    @Override
    public void setInput(Object input) {
        Assert.isLegal(input instanceof AnnotationInfo);
        fInput = (AnnotationInfo) input;
        disposeDeferredCreatedContent();
        deferredCreateContent();
    }

    @Override
    public boolean hasContents() {
        return fInput != null;
    }

    AnnotationInfo getAnnotationInfo() {
        return fInput;
    }

    @Override
    public void setFocus() {
        if (!getShell().isDisposed()) {
            super.setFocus();
            if (fFocusControl != null) {
                fFocusControl.setFocus();
            }
        }
    }

    @Override
    public final void setVisible(boolean visible) {
        if (!visible) {
            disposeDeferredCreatedContent();
        }
        super.setVisible(visible);
    }

    protected void disposeDeferredCreatedContent() {
        Control[] children = fParent.getChildren();
        for (int i=0; i<children.length; i++) {
            children[i].dispose();
        }
        quickFixesRetrieved.set(false);
        ToolBarManager toolBarManager = getToolBarManager();
        if (toolBarManager != null)
            toolBarManager.removeAll();
    }

    @Override
    protected void createContent(Composite parent) {
        fParent = parent;
        quickFixesRetrieved.set(false);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        fParent.setLayout(layout);
    }

    @Override
    public Point computeSizeHint() {
        Point preferedSize = getShell()
                .computeSize(SWT.DEFAULT, SWT.DEFAULT, true);

        Point constrains = getSizeConstraints();
        if (constrains == null) {
            return preferedSize;
        }

        int trimWidth = 
                getShell().computeTrim(0, 0, 0, 0).width;
        Point constrainedSize = getShell()
                .computeSize(constrains.x - trimWidth, 
                        SWT.DEFAULT, true);

        int width = min(preferedSize.x, constrainedSize.x);
        int height = max(preferedSize.y, constrainedSize.y);

        return new Point(width, height);
    }

    private final static ICompletionProposal[] NO_PROPOSAL = new ICompletionProposal[0];
    
    /**
     * Create content of the hover. This is called after
     * the input has been set.
     */
    protected void deferredCreateContent() {
        createAnnotationInformation(fParent);
        quickFixesRetrieved.set(false);
        
        final ICompletionProposal[] existingProposals = fInput.getProposals();
        if (existingProposals == null) {
            final Composite quickFixProgressGroup = createQuickFixProgressBar();
            fParent.layout(true);
            final Composite currentParent = fParent;
            Job completionProposalsJob = createQuickFixesRetrievalJob(
                    quickFixProgressGroup, currentParent);
            completionProposalsJob.schedule();
        } else {
            setColorAndFont(fParent, 
                    fParent.getForeground(), 
                    fParent.getBackground(), 
                    CeylonPlugin.getHoverFont());
            if (existingProposals.length > 0) {
                createCompletionProposalsControl(fParent, 
                        existingProposals);
            }
            fParent.layout(true);
        }
    }

    private Job createQuickFixesRetrievalJob(
            final Composite quickFixProgressGroup, final Composite currentParent) {
        Job completionProposalsJob = new Job("Retrieving Fixes") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                AnnotationInfo info = fInput;
                final ICompletionProposal[] proposals;
                if (getTypecheckedRootNode(info) == null) {
                    proposals = NO_PROPOSAL;
                } else {
                    proposals = getAnnotationInfo().getCompletionProposals();
                    fInput.setProposals(proposals);
                }

                if (fParent == currentParent
                        && ! fParent.isDisposed()) {
                    final Display display= fParent.getDisplay();
                    if (display != null) {
                        display.asyncExec(new Runnable() {
                            public void run() {
                                if (fParent.isVisible()) {
                                    if (! quickFixProgressGroup.isDisposed()) {
                                        quickFixProgressGroup.dispose();
                                    }
                                    if (proposals.length > 0
                                            && quickFixesRetrieved.compareAndSet(false, true)) {
                                        createCompletionProposalsControl(fParent, 
                                                proposals);
                                    }
                                    fParent.layout(true);
                                    fParent.pack(true);
                                    Point sizeHint = computeSizeHint();
                                    setSize(sizeHint.x, sizeHint.y);
                                }
                            }
                        });
                    }
                }
                return Status.OK_STATUS;
            }
        };
        completionProposalsJob.setSystem(true);
        completionProposalsJob.setRule(retrievingQuickFixesRule);
        return completionProposalsJob;
    }

    private Composite createQuickFixProgressBar() {
        final Composite quickFixProgressGroup;
        quickFixProgressGroup = new Composite(fParent, SWT.FILL);
        GridData qk1 = 
                new GridData(SWT.FILL, SWT.FILL, 
                        true, true);
        qk1.horizontalSpan=2;
        quickFixProgressGroup.setLayoutData(qk1);
        GridLayout layout2 = new GridLayout(2, true);
        layout2.marginHeight = 0;
        layout2.marginWidth = 10;
        layout2.verticalSpacing = 2;
        quickFixProgressGroup.setLayout(layout2);
        Label progressLabel = new Label(quickFixProgressGroup, SWT.NONE);
        progressLabel.setText("Searching for fixes");
        new ProgressBar(quickFixProgressGroup, SWT.INDETERMINATE | SWT.FILL);
        setColorAndFont(fParent, 
                fParent.getForeground(), 
                fParent.getBackground(), 
                CeylonPlugin.getHoverFont());
        return quickFixProgressGroup;
    }

    private void setColorAndFont(Control control, 
            Color foreground, Color background, Font font) {
        control.setForeground(foreground);
        control.setBackground(background);
        control.setFont(font);

        if (control instanceof Composite) {
            Composite composite = (Composite) control;
            Control[] children = composite.getChildren();
            for (int i=0; i<children.length; i++) {
                setColorAndFont(children[i], 
                        foreground, background, 
                        font);
            }
        }
    }

    private void createAnnotationInformation(Composite parent) {
        Composite composite = 
                new Composite(parent, SWT.NONE);
        GridData gd1 = 
                new GridData(SWT.FILL, SWT.TOP, 
                        true, false);
        composite.setLayoutData(gd1);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        layout.horizontalSpacing = 4;
        layout.marginRight = 5;
        composite.setLayout(layout);

        Annotation[] annotations = 
                getAnnotationInfo()
                    .getAnnotationPositions()
                    .keySet()
                    .toArray(new Annotation[0]);
        Arrays.sort(annotations, 
                createAnnotationComparator());
        for (final Annotation annotation: annotations) {
            
            final Canvas canvas = 
                    new Canvas(composite, SWT.NO_FOCUS);
            GridData gd2 = 
                    new GridData(SWT.BEGINNING, SWT.BEGINNING, 
                            false, false);
            gd2.widthHint = 17;
            gd2.heightHint = 16;
            canvas.setLayoutData(gd2);
            
            canvas.addPaintListener(new PaintListener() {
                @Override
                public void paintControl(PaintEvent e) {
                    e.gc.setFont(null);
                    fMarkerAnnotationAccess.paint(annotation, 
                            e.gc, canvas, 
                            new Rectangle(0, 0, 16, 16));
                }
            });
            
            GridData gd3 = 
                    new GridData(SWT.FILL, SWT.FILL, 
                            true, true);
            if (annotation instanceof RefinementAnnotation) {
                Link link = new Link(composite, SWT.NONE);
                String text = 
                        annotation.getText()
                        .replaceFirst(" ", " <a>") + "</a>";
                link.setText(text);
                link.addSelectionListener(new SelectionListener() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        RefinementAnnotation ra = 
                                (RefinementAnnotation) 
                                    annotation;
                        CeylonEditor editor = 
                                getAnnotationInfo()
                                    .getEditor();
                        ra.gotoRefinedDeclaration(editor);
                    }
                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
            }
            /*else if (annotation instanceof 
                        CeylonRangeIndicator) {
                StyledText text = 
                        new StyledText(composite, 
                                SWT.MULTI | SWT.WRAP | 
                                SWT.READ_ONLY);
                text.setLayoutData(gd3);
                String message = annotation.getText();
                if (message!=null && !message.isEmpty()) {
                    CeylonRangeIndicator cia = 
                            (CeylonRangeIndicator) 
                                annotation;
                    StyledString styledString = 
                            cia.getStyledString();
                    text.setText(styledString.getString());
                    text.setStyleRanges(styledString.getStyleRanges());
                }
            }*/
            else if (annotation instanceof 
                        CeylonInitializerAnnotation) {
                StyledText text = 
                        new StyledText(composite, 
                                SWT.MULTI | SWT.WRAP | 
                                SWT.READ_ONLY);
                text.setLayoutData(gd3);
                String message = annotation.getText();
                if (message!=null && !message.isEmpty()) {
                    CeylonInitializerAnnotation cia = 
                            (CeylonInitializerAnnotation) 
                                annotation;
                    StyledString styledString = 
                            cia.getStyledString();
                    text.setText(styledString.getString());
                    text.setStyleRanges(styledString.getStyleRanges());
                }
            }
            else {
                StyledText text = 
                        new StyledText(composite, 
                                SWT.MULTI | SWT.WRAP | 
                                SWT.READ_ONLY);
                text.setLayoutData(gd3);
                String message = annotation.getText();
                if (message!=null && !message.isEmpty()) {
                    message = toInitialUppercase(message);
                    StyledString styled =
                            Highlights.styleProposal(
                                    message, true, true);
                    text.setText(styled.getString());
                    StyleRange[] styleRanges = 
                            styled.getStyleRanges();
                    Font editorFont = 
                            CeylonPlugin.getEditorFont();
                    Font hoverFont = 
                            CeylonPlugin.getHoverFont();
                    FontData monospaceFontData = 
                            editorFont.getFontData()[0];
                    Display display = Display.getDefault();
                    Shell activeShell = 
                            display.getActiveShell();
                    if (activeShell!=null) {
                        GC gc = new GC(activeShell);
                        Font font = gc.getFont();
                        gc.setFont(hoverFont);
                        int hoverFontHeight = 
                                gc.getFontMetrics()
                                    .getAscent();
                        gc.setFont(editorFont);
                        int monospaceFontHeight = 
                                gc.getFontMetrics()
                                    .getAscent();
                        gc.setFont(font);
                        int height = 
                                monospaceFontData.getHeight() * 
                                hoverFontHeight / monospaceFontHeight;
                        Font result = 
                                new Font(display, 
                                    monospaceFontData.getName(), 
                                    height, 
                                    monospaceFontData.getStyle());
                        for (StyleRange range: styleRanges) {
                            range.font = result;
                        }
                    }
                    text.setStyleRanges(styleRanges);
                }
            }
        }
    }

    Comparator<Annotation> createAnnotationComparator() {
        return new AnnotationComparator();
    }

    private void createCompletionProposalsControl(
            Composite parent, 
            ICompletionProposal[] proposals) {
        Composite composite = 
                new Composite(parent, SWT.NONE);
        GridData gd1 = 
                new GridData(SWT.FILL, SWT.FILL, 
                        true, true);
        composite.setLayoutData(gd1);
        GridLayout layout2 = new GridLayout(1, false);
        layout2.marginHeight = 0;
        layout2.marginWidth = 10;
        layout2.verticalSpacing = 2;
        composite.setLayout(layout2);

//        Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
//        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
//        separator.setLayoutData(gridData);

        Label quickFixLabel = 
                new Label(composite, SWT.NONE);
        GridData gd2 = 
                new GridData(SWT.BEGINNING, SWT.CENTER, 
                        false, false);
        gd2.horizontalIndent = 4;
        quickFixLabel.setLayoutData(gd2);
        String text;
        if (proposals.length == 1) {
            text = "1 quick fix available:";
        }
        else {
            text = proposals.length + 
                    " quick fixes available:";
        }
        quickFixLabel.setText(text);

        setColorAndFont(composite, 
                parent.getForeground(), 
                parent.getBackground(), 
                CeylonPlugin.getHoverFont());
        createCompletionProposalsList(composite, proposals);
    }

    private void createCompletionProposalsList(
            Composite parent, 
            ICompletionProposal[] proposals) {
        final ScrolledComposite scrolledComposite = 
                new ScrolledComposite(parent, 
                        SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gd1 = 
                new GridData(SWT.FILL, SWT.FILL, 
                        true, true);
        scrolledComposite.setLayoutData(gd1);
        scrolledComposite.setExpandVertical(false);
        scrolledComposite.setExpandHorizontal(false);

        Composite composite = 
                new Composite(scrolledComposite, SWT.NONE);
        GridData gd2 = 
                new GridData(SWT.FILL, SWT.FILL, 
                        true, true);
        composite.setLayoutData(gd2);
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 2;
        composite.setLayout(layout);

        final Link[] links = new Link[proposals.length];
        for (int i=0; i<proposals.length; i++) {
            Label indent = new Label(composite, SWT.NONE);
            GridData gridData1 = 
                    new GridData(SWT.BEGINNING, SWT.CENTER, 
                            false, false);
            gridData1.widthHint = 0;
            indent.setLayoutData(gridData1);

            links[i] = 
                    createCompletionProposalLink(composite, 
                            proposals[i]);
        }

        scrolledComposite.setContent(composite);
        setColorAndFont(scrolledComposite, 
                parent.getForeground(), 
                parent.getBackground(), 
                CeylonPlugin.getHoverFont());

        Point contentSize = 
                composite.computeSize(
                        SWT.DEFAULT, SWT.DEFAULT);
        composite.setSize(contentSize);

        Point constraints = getSizeConstraints();
        if (constraints != null && 
                contentSize.x < constraints.x) {
            ScrollBar horizontalBar = 
                    scrolledComposite.getHorizontalBar();

            int scrollBarHeight;
            if (horizontalBar == null) {
                Point scrollSize = 
                        scrolledComposite.computeSize(
                                SWT.DEFAULT, SWT.DEFAULT);
                scrollBarHeight = 
                        scrollSize.y - contentSize.y;
            }
            else {
                scrollBarHeight = 
                        horizontalBar.getSize().y;
            }
            gd1.heightHint = 
                    contentSize.y - scrollBarHeight;
        }

        fFocusControl = links[0];
        for (int i=0; i<links.length; i++) {
            final int index = i;
            final Link link = links[index];
            link.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.keyCode) {
                        case SWT.ARROW_DOWN:
                            if (index + 1 < links.length) {
                                links[index + 1].setFocus();
                            }
                            break;
                        case SWT.ARROW_UP:
                            if (index > 0) {
                                links[index - 1].setFocus();
                            }
                            break;
                        default:
                            break;
                    }
                }
                @Override
                public void keyReleased(KeyEvent e) {}
            });

            link.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    int currentPosition = 
                            scrolledComposite.getOrigin().y;
                    int height = 
                            scrolledComposite.getSize().y;
                    int linkPosition = link.getLocation().y;
                    if (linkPosition < currentPosition) {
                        if (linkPosition < 10) {
                            linkPosition= 0;
                        }
                        scrolledComposite.setOrigin(0, 
                                linkPosition);
                    } 
                    else if (linkPosition + 20 > 
                                currentPosition + height) {
                        scrolledComposite.setOrigin(0, 
                                linkPosition - height + 
                                        link.getSize().y);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {}
            });
        }
    }

    private Link createCompletionProposalLink(
            Composite parent,
            final ICompletionProposal proposal) {
        Label proposalImage = new Label(parent, SWT.NONE);
        GridData gd1 = 
                new GridData(SWT.BEGINNING, SWT.CENTER, 
                        false, false);
        proposalImage.setLayoutData(gd1);
        Image image = proposal.getImage();
        if (image != null) {
            proposalImage.setImage(image);
            proposalImage.addMouseListener(new MouseListener() {
                @Override
                public void mouseDoubleClick(MouseEvent e) {}
                @Override
                public void mouseDown(MouseEvent e) {}
                @Override
                public void mouseUp(MouseEvent e) {
                    if (e.button == 1) {
                        apply(proposal, fInput.getViewer());
                    }
                }
            });
        }

        Link proposalLink = new Link(parent, SWT.WRAP);
        GridData gd2 = 
                new GridData(SWT.BEGINNING, SWT.CENTER, 
                        false, false);
        proposalLink.setFont(CeylonPlugin.getHoverFont());
        proposalLink.setLayoutData(gd2);
        proposalLink.setText("<a>" + 
                proposal.getDisplayString()
                        .replace("&", "&&") + 
                "</a>");
        proposalLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                apply(proposal, fInput.getViewer());
            }
        });
        return proposalLink;
    }

    private void apply(ICompletionProposal p, ITextViewer viewer) {
        //Focus needs to be in the text viewer, 
        //otherwise linked mode does not work
        dispose();

        IRewriteTarget target = null;
        try {
            IDocument document = viewer.getDocument();

            if (viewer instanceof ITextViewerExtension) {
                ITextViewerExtension extension = 
                        (ITextViewerExtension) viewer;
                target = extension.getRewriteTarget();
            }

            if (target != null)
                target.beginCompoundChange();

//            if (p instanceof ICompletionProposalExtension2) {
//                ICompletionProposalExtension2 e= (ICompletionProposalExtension2) p;
//                e.apply(viewer, (char) 0, SWT.NONE, offset);
//            } else if (p instanceof ICompletionProposalExtension) {
//                ICompletionProposalExtension e= (ICompletionProposalExtension) p;
//                e.apply(document, (char) 0, offset);
//            } else {
                p.apply(document);
//            }

            Point selection = p.getSelection(document);
            if (selection != null) {
                int x = selection.x;
                int y = selection.y;
                viewer.setSelectedRange(x, y);
                viewer.revealRange(x, y);
            }
        }
        finally {
            if (target != null)
                target.endCompoundChange();
        }
    }
}