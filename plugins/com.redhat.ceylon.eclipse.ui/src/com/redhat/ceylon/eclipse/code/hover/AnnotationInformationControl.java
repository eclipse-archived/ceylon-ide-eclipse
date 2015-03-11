package com.redhat.ceylon.eclipse.code.hover;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonInitializerAnnotation;
import com.redhat.ceylon.eclipse.code.editor.RefinementAnnotation;
import com.redhat.ceylon.eclipse.util.Highlights;

/**
 * The annotation information control shows informations about a given
 * {@link AnnotationInfo}. It can also show a toolbar
 * and a list of {@link ICompletionProposal}s.
 *
 * @since 3.4
 */
class AnnotationInformationControl extends AbstractInformationControl 
        implements IInformationControlExtension2 {

    private final DefaultMarkerAnnotationAccess fMarkerAnnotationAccess;
    private Control fFocusControl;
    private AnnotationInfo fInput;
    private Composite fParent;

    AnnotationInformationControl(Shell parentShell, 
            String statusFieldText) {
        super(parentShell, statusFieldText);
        fMarkerAnnotationAccess = new DefaultMarkerAnnotationAccess();
        create();
    }

    AnnotationInformationControl(Shell parentShell, 
            ToolBarManager toolBarManager) {
        super(parentShell, toolBarManager);
        fMarkerAnnotationAccess = new DefaultMarkerAnnotationAccess();
        create();
    }

    @Override
    public void setInformation(String information) {
        //replaced by IInformationControlExtension2#setInput
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
        ToolBarManager toolBarManager= getToolBarManager();
        if (toolBarManager != null)
            toolBarManager.removeAll();
    }

    @Override
    protected void createContent(Composite parent) {
        fParent = parent;
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

        int trimWidth = getShell().computeTrim(0, 0, 0, 0).width;
        Point constrainedSize = getShell()
                .computeSize(constrains.x - trimWidth, SWT.DEFAULT, true);

        int width = Math.min(preferedSize.x, constrainedSize.x);
        int height = Math.max(preferedSize.y, constrainedSize.y);

        return new Point(width, height);
    }

    /**
     * Create content of the hover. This is called after
     * the input has been set.
     */
    protected void deferredCreateContent() {
        createAnnotationInformation(fParent);
        setColorAndFont(fParent, fParent.getForeground(), 
                fParent.getBackground(), 
                CeylonEditor.getHoverFont());

        ICompletionProposal[] proposals = 
                getAnnotationInfo().getCompletionProposals();
        if (proposals.length > 0) {
            createCompletionProposalsControl(fParent, proposals);
        }

        fParent.layout(true);
    }

    private void setColorAndFont(Control control, Color foreground, 
            Color background, Font font) {
        control.setForeground(foreground);
        control.setBackground(background);
        control.setFont(font);

        if (control instanceof Composite) {
            Control[] children = ((Composite) control).getChildren();
            for (int i=0; i<children.length; i++) {
                setColorAndFont(children[i], foreground, background, font);
            }
        }
    }

    private void createAnnotationInformation(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        layout.horizontalSpacing = 4;
        layout.marginRight = 5;
        composite.setLayout(layout);

        Annotation[] annotations = getAnnotationInfo()
                .getAnnotationPositions().keySet()
                .toArray(new Annotation[0]);
        Arrays.sort(annotations, createAnnotationComparator());
        for (final Annotation annotation: annotations) {
            
            final Canvas canvas = 
                    new Canvas(composite, SWT.NO_FOCUS);
            GridData gridData = 
                    new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
            gridData.widthHint = 17;
            gridData.heightHint = 16;
            canvas.setLayoutData(gridData);
            
            canvas.addPaintListener(new PaintListener() {
                @Override
                public void paintControl(PaintEvent e) {
                    e.gc.setFont(null);
                    fMarkerAnnotationAccess.paint(annotation, e.gc, canvas, 
                            new Rectangle(0, 0, 16, 16));
                }
            });
            
            GridData data = 
                    new GridData(SWT.FILL, SWT.FILL, true, true);
            if (annotation instanceof RefinementAnnotation) {
                Link link = new Link(composite, SWT.NONE);
                String text = annotation.getText().replaceFirst(" ", " <a>") + "</a>";
                link.setText(text);
                link.addSelectionListener(new SelectionListener() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        RefinementAnnotation ra = (RefinementAnnotation) annotation;
                        ra.gotoRefinedDeclaration(getAnnotationInfo().getEditor());
                    }
                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
            }
            else if (annotation instanceof CeylonInitializerAnnotation) {
                StyledText text = 
                        new StyledText(composite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
                text.setLayoutData(data);
                String annotationText = annotation.getText();
                if (annotationText!=null && !annotationText.isEmpty()) {
                    StyledString styledString = ((CeylonInitializerAnnotation) annotation).getStyledString();
                    text.setText(styledString.getString());
                    text.setStyleRanges(styledString.getStyleRanges());
                }
            } else {
                StyledText text = 
                        new StyledText(composite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
                text.setLayoutData(data);
                String annotationText = annotation.getText();
                if (annotationText!=null && !annotationText.isEmpty()) {
                    annotationText = Character.toUpperCase(annotationText.charAt(0)) +
                            annotationText.substring(1);
                    StyledString styled =
                            Highlights.styleProposal(annotationText, true, true);
                    text.setText(styled.getString());
                    text.setStyleRanges(styled.getStyleRanges());
                }
            }
        }
    }

    Comparator<Annotation> createAnnotationComparator() {
        return new AnnotationComparator();
    }

    private void createCompletionProposalsControl(Composite parent, 
            ICompletionProposal[] proposals) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout2 = new GridLayout(1, false);
        layout2.marginHeight = 0;
        layout2.marginWidth = 10;
        layout2.verticalSpacing = 2;
        composite.setLayout(layout2);

//        Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
//        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
//        separator.setLayoutData(gridData);

        Label quickFixLabel = new Label(composite, SWT.NONE);
        GridData layoutData = 
                new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        layoutData.horizontalIndent = 4;
        quickFixLabel.setLayoutData(layoutData);
        String text;
        if (proposals.length == 1) {
            text = "1 quick fix available:";
        }
        else {
            text = proposals.length + " quick fixes available:";
        }
        quickFixLabel.setText(text);

        setColorAndFont(composite, parent.getForeground(), 
                parent.getBackground(), 
                CeylonEditor.getHoverFont());
        createCompletionProposalsList(composite, proposals);
    }

    private void createCompletionProposalsList(Composite parent, 
            ICompletionProposal[] proposals) {
        final ScrolledComposite scrolledComposite = 
                new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        scrolledComposite.setLayoutData(gridData);
        scrolledComposite.setExpandVertical(false);
        scrolledComposite.setExpandHorizontal(false);

        Composite composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 2;
        composite.setLayout(layout);

        final Link[] links = new Link[proposals.length];
        for (int i=0; i<proposals.length; i++) {
            Label indent = new Label(composite, SWT.NONE);
            GridData gridData1 = 
                    new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
            gridData1.widthHint = 0;
            indent.setLayoutData(gridData1);

            links[i] = createCompletionProposalLink(composite, proposals[i]);
        }

        scrolledComposite.setContent(composite);
        setColorAndFont(scrolledComposite, parent.getForeground(), 
                parent.getBackground(), 
                JFaceResources.getDialogFont());

        Point contentSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        composite.setSize(contentSize);

        Point constraints = getSizeConstraints();
        if (constraints != null && contentSize.x < constraints.x) {
            ScrollBar horizontalBar = scrolledComposite.getHorizontalBar();

            int scrollBarHeight;
            if (horizontalBar == null) {
                Point scrollSize = 
                        scrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                scrollBarHeight = scrollSize.y - contentSize.y;
            }
            else {
                scrollBarHeight = horizontalBar.getSize().y;
            }
            gridData.heightHint = contentSize.y - scrollBarHeight;
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
                    int currentPosition = scrolledComposite.getOrigin().y;
                    int hight = scrolledComposite.getSize().y;
                    int linkPosition = link.getLocation().y;
                    if (linkPosition < currentPosition) {
                        if (linkPosition < 10) {
                            linkPosition= 0;
                        }
                        scrolledComposite.setOrigin(0, linkPosition);
                    } 
                    else if (linkPosition + 20 > currentPosition + hight) {
                        scrolledComposite.setOrigin(0, 
                                linkPosition - hight + link.getSize().y);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {}
            });
        }
    }

    private Link createCompletionProposalLink(Composite parent,
            final ICompletionProposal proposal) {
        Label proposalImage = new Label(parent, SWT.NONE);
        proposalImage.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
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
        proposalLink.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        proposalLink.setText("<a>" + proposal.getDisplayString().replace("&", "&&") + "</a>");
        proposalLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                apply(proposal, fInput.getViewer());
            }
        });
        return proposalLink;
    }

    private void apply(ICompletionProposal p, ITextViewer viewer/*, int offset*/) {
        //Focus needs to be in the text viewer, otherwise linked mode does not work
        dispose();

        IRewriteTarget target = null;
        try {
            IDocument document = viewer.getDocument();

            if (viewer instanceof ITextViewerExtension) {
                ITextViewerExtension extension = (ITextViewerExtension) viewer;
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
                viewer.setSelectedRange(selection.x, selection.y);
                viewer.revealRange(selection.x, selection.y);
            }
        }
        finally {
            if (target != null)
                target.endCompoundChange();
        }
    }
}