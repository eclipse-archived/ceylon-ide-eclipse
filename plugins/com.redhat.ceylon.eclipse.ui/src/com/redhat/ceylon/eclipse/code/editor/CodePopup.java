package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtility.getEditorInput;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.getNodePath;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoNode;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SOURCE;
import static com.redhat.ceylon.eclipse.util.Highlights.getCurrentThemeColor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Nodes;

final class CodePopup extends PopupDialog 
        implements IInformationControl, IInformationControlExtension2,
                   IInformationControlExtension3 {
    
    private final class GotoListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.character == 0x1B) // ESC
                dispose();
            if (e.character == 'p' && (e.stateMask&SWT.MOD1)!=0) {
                e.doit=false;
                dispose();
                gotoNode(referencedNode, 
                        editor.getParseController().getProject(), 
                        editor.getParseController().getTypeChecker());
            }
        }
    }

    static final String KEY = KeyStroke.getInstance(SWT.MOD1, 'P').format();

    ISourceViewer viewer;
    CeylonEditor editor;
    Node referencedNode;
    CeylonParseController pc = new CeylonParseController();
    
    CodePopup(Shell parent, int shellStyle, CeylonEditor editor) {
        super(parent, shellStyle, true, true, false, true,
                true, null, KEY + " to open editor");
        this.editor = editor;
        create();
        
        Color color = getCurrentThemeColor("code");
        getShell().setBackground(color);
        setBackgroundColor(color);

        //setBackgroundColor(getEditorWidget(editor).getBackground());
        setForegroundColor(getEditorWidget(editor).getForeground());
    }

    public StyledText getEditorWidget(CeylonEditor editor) {
        return editor.getCeylonSourceViewer().getTextWidget();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        int styles= SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
        viewer = new CeylonSourceViewer(editor, parent, null, null, false, styles);
        viewer.setEditable(false);
        StyledText textWidget = viewer.getTextWidget();
        textWidget.setFont(getEditorWidget(editor).getFont());
        textWidget.setBackground(getEditorWidget(editor).getBackground());
        textWidget.addKeyListener(new GotoListener());
        return textWidget;
    }
    
    private static GridLayoutFactory popupLayoutFactory;
    protected static GridLayoutFactory getPopupLayout() {
        if (popupLayoutFactory == null) {
            popupLayoutFactory = GridLayoutFactory.fillDefaults()
                    .margins(POPUP_MARGINWIDTH, POPUP_MARGINHEIGHT)
                    .spacing(POPUP_HORIZONTALSPACING, POPUP_VERTICALSPACING);
        }
        return popupLayoutFactory;
    }
    
    @Override
    protected Control createTitleControl(Composite parent) {
        getPopupLayout().copy().numColumns(3).applyTo(parent);
        Label iconLabel = new Label(parent, SWT.NONE);
        iconLabel.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CEYLON_SOURCE));
        getShell().addKeyListener(new GotoListener());
        return super.createTitleControl(parent);
    }
    
    /*@Override
    protected void adjustBounds() {
        Rectangle bounds = getShell().getBounds();
        int h = bounds.height;
        if (h>400) {
            bounds.height=400;
            bounds.y = bounds.y + (h-400)/3;
            getShell().setBounds(bounds);
        }
        int w = bounds.width;
        if (w<600) {
            bounds.width=600;
            getShell().setBounds(bounds);
        }
    }*/
    
    public void setInformation(String information) {
        // this method is ignored, see IInformationControlExtension2
    }

    public void setSize(int width, int height) {
        getShell().setSize(width, height);
    }

    public void addDisposeListener(DisposeListener listener) {
        getShell().addDisposeListener(listener);
    }

    public void removeDisposeListener(DisposeListener listener) {
        getShell().removeDisposeListener(listener);
    }

    public void setForegroundColor(Color foreground) {
        applyForegroundColor(foreground, getContents());
    }

    public void setBackgroundColor(Color background) {
        applyBackgroundColor(background, getContents());
    }

    public boolean isFocusControl() {
        return getShell().getDisplay().getActiveShell() == getShell();
    }

    public void setFocus() {
        getShell().forceFocus();
    }

    public void addFocusListener(FocusListener listener) {
        getShell().addFocusListener(listener);
    }

    public void removeFocusListener(FocusListener listener) {
        getShell().removeFocusListener(listener);
    }


    public void setSizeConstraints(int maxWidth, int maxHeight) {
        // ignore
    }

    public void setLocation(Point location) {
        /*
         * If the location is persisted, it gets managed by PopupDialog - fine. Otherwise, the location is
         * computed in Window#getInitialLocation, which will center it in the parent shell / main
         * monitor, which is wrong for two reasons:
         * - we want to center over the editor / subject control, not the parent shell
         * - the center is computed via the initalSize, which may be also wrong since the size may
         *   have been updated since via min/max sizing of AbstractInformationControlManager.
         * In that case, override the location with the one computed by the manager. Note that
         * the call to constrainShellSize in PopupDialog.open will still ensure that the shell is
         * entirely visible.
         */
        if (!getPersistLocation() || getDialogSettings() == null)
            getShell().setLocation(location);
    }

    public Point computeSizeHint() {
        // return the shell's size - note that it already has the persisted size if persisting
        // is enabled.
        return getShell().getSize();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            open();
        } else {
            saveDialogBounds(getShell());
            getShell().setVisible(false);
        }
    }

    public final void dispose() {
        docProvider.disconnect(ei);
        ei = null;
        close();
    }

    IDocumentProvider docProvider = new SourceArchiveDocumentProvider();
    IEditorInput ei;
    
    @Override
    public void setInput(Object input) {
        CeylonParseController epc = editor.getParseController();
        IRegion r = editor.getSelection();
        Node node = Nodes.findNode(epc.getRootNode(), r.getOffset(), 
                r.getOffset()+r.getLength());
        referencedNode = Nodes.getReferencedNode(node, epc);
        if (referencedNode==null) return;
        IPath path = getNodePath(referencedNode, epc.getProject(), epc.getTypeChecker());
        ei = getEditorInput(path);
        IDocument doc;
        try {
            docProvider.connect(ei);
            doc = docProvider.getDocument(ei);
        } 
        catch (CoreException e) {
            e.printStackTrace();
            return;
        }
        viewer.setDocument(doc);
        viewer.setVisibleRegion(referencedNode.getStartIndex(), 
                referencedNode.getStopIndex()-referencedNode.getStartIndex()+1);
        pc.initialize(path, epc.getProject(), null);
        pc.parse(doc, new NullProgressMonitor(), null);
        /*try {
            int lines = doc.getLineOfOffset(refDec.getStopIndex())-
                        doc.getLineOfOffset(refDec.getStartIndex())+1;
            setSize(getShell().getBounds().width, 
                    viewer.getTextWidget().getLineHeight()*lines);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }*/
        if (referencedNode instanceof Tree.Declaration) {
            Declaration model = ((Tree.Declaration) referencedNode).getDeclarationModel();
            setTitleText("Declaration of " + getDescriptionFor(model));
        }
    }

    @Override
    public boolean restoresLocation() {
        return false;
    }
    
    @Override
    public boolean restoresSize() {
        return true;
    }
    
    @Override
    public Rectangle getBounds() {
        return getShell().getBounds();
    }
    
    @Override
    public Rectangle computeTrim() {
        return getShell().computeTrim(0, 0, 0, 0);
    }

    public CeylonParseController getParseController() {
        return pc;
    }
}