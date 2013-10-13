package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.addImageAndLabel;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.fileUrl;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getHoverInfo;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getLinkedModel;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getStyleSheet;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.internalGetHoverInfo;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static org.eclipse.jdt.internal.ui.JavaPluginImages.setLocalImageDescriptors;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK_DISABLED;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD_DISABLED;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.part.ViewPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.browser.BrowserInput;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.html.HTMLPrinter;
import com.redhat.ceylon.eclipse.code.quickfix.ExtractFunctionProposal;
import com.redhat.ceylon.eclipse.code.quickfix.ExtractValueProposal;
import com.redhat.ceylon.eclipse.code.quickfix.SpecifyTypeProposal;
import com.redhat.ceylon.eclipse.code.search.FindAssignmentsAction;
import com.redhat.ceylon.eclipse.code.search.FindReferencesAction;
import com.redhat.ceylon.eclipse.code.search.FindRefinementsAction;
import com.redhat.ceylon.eclipse.code.search.FindSubtypesAction;

public class DocumentationView extends ViewPart {
    
    private static DocumentationView instance;
    
    public static DocumentationView getInstance() {
        return instance;
    }
    
    public DocumentationView() {
        instance = this;
    }
    
    private Browser control;
    private CeylonEditor editor;
    private CeylonBrowserInput info;
    private BackAction back;
    private ForwardAction forward;
    private OpenDeclarationAction openDeclarationAction;
    
    @Override
    public void createPartControl(Composite parent) {
        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
        back = new BackAction();
        back.setEnabled(false);
        tbm.add(back);
        forward = new ForwardAction();
        forward.setEnabled(false);
        tbm.add(forward);
        openDeclarationAction = new OpenDeclarationAction();
        tbm.add(openDeclarationAction);
        openDeclarationAction.setEnabled(false);
        control = new Browser(parent, SWT.NONE); 
        control.setJavascriptEnabled(false);
        Display display = getSite().getShell().getDisplay();
        Color fg = display.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        Color bg = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        control.setForeground(fg);
        control.setBackground(bg);
        parent.setForeground(fg);
        parent.setBackground(bg);
        FontData fontData = JFaceResources.getFontRegistry()
                .getFontData(APPEARANCE_JAVADOC_FONT)[0];
        control.setFont(new Font(Display.getDefault(), fontData));
        control.addLocationListener(new LocationListener() {
            @Override
            public void changing(LocationEvent event) {
                String location = event.location;
                
                //necessary for windows environment (fix for blank page)
                //somehow related to this: https://bugs.eclipse.org/bugs/show_bug.cgi?id=129236
                if (!"about:blank".equals(location)) {
                    event.doit = false;
                }
                
                handleLink(location);
            }
            @Override
            public void changed(LocationEvent event) {}
        });
        update(null, -1, -1);
    }

    //TODO: big copy/paste from DocumentationHover.handleLink
    private void handleLink(String location) {
        if (location.startsWith("dec:")) {
            Referenceable target = getLinkedModel(info, editor, location);
            if (target!=null) {
                gotoDeclaration(editor, target);
            }
        }
        else if (location.startsWith("doc:")) {
            Referenceable target = getLinkedModel(info, editor, location);
            if (target!=null) {
                info = getHoverInfo(target, info, editor, null);
                if (info!=null) control.setText(info.getHtml());
                back.update();
                forward.update();
            }
        }
        else if (location.startsWith("ref:")) {
            Referenceable target = getLinkedModel(info, editor, location);
            new FindReferencesAction(editor, (Declaration) target).run();
        }
        else if (location.startsWith("sub:")) {
            Referenceable target = getLinkedModel(info, editor, location);
            new FindSubtypesAction(editor, (Declaration) target).run();
        }
        else if (location.startsWith("act:")) {
            Referenceable target = getLinkedModel(info, editor, location);
            new FindRefinementsAction(editor, (Declaration) target).run();
        }
        else if (location.startsWith("ass:")) {
            Referenceable target = getLinkedModel(info, editor, location);
            new FindAssignmentsAction(editor, (Declaration) target).run();
        }
        else if (location.startsWith("stp:")) {
            CompilationUnit rn = editor.getParseController().getRootNode();
            Node node = findNode(rn, Integer.parseInt(location.substring(4)));
            SpecifyTypeProposal.create(rn, node, Util.getFile(editor.getEditorInput()))
                    .apply(editor.getParseController().getDocument());
        }
        else if (location.startsWith("exv:")) {
            new ExtractValueProposal(editor).apply(editor.getParseController().getDocument());
        }
        else if (location.startsWith("exf:")) {
            new ExtractFunctionProposal(editor).apply(editor.getParseController().getDocument());
        }
    }
    
    @Override
    public void setFocus() {}
    
    public void update(CeylonEditor editor, int offset, int length) { 
        if (editor==null) {
            clear();
        }
        else {
            info = internalGetHoverInfo(editor, 
                    new Region(offset, length));
            if (info!=null && info.getAddress()!=null) {
                control.setText(info.getHtml());
                back.update();
                forward.update();
                openDeclarationAction.setEnabled(true);
            }
            else if (this.editor!=editor) {
                clear();
            }
        }
        this.editor = editor;
    }

    private void clear() {
        StringBuffer buffer = new StringBuffer();
        HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
        addImageAndLabel(buffer, null, 
                fileUrl("information.gif").toExternalForm(), 
                16, 16, "<i>Nothing selected in Ceylon editor.</i>", 20, 2);
//            buffer.append("<p>Nothing selected.</p>");
        HTMLPrinter.addPageProlog(buffer);
        control.setText(buffer.toString());
        info=null;
        back.update();
        forward.update();
        openDeclarationAction.setEnabled(false);
    }
    
    @Override
    public void dispose() {
        instance = null;
        super.dispose();
    }

    class BackAction extends Action {
        
        public BackAction() {
            setText("Back");
            ISharedImages images = getWorkbench().getSharedImages();
            setImageDescriptor(images.getImageDescriptor(IMG_TOOL_BACK));
            setDisabledImageDescriptor(images.getImageDescriptor(IMG_TOOL_BACK_DISABLED));

            update();
        }
        
        @Override
        public void run() {
            BrowserInput previous = info.getPrevious();
            if (previous != null) {
                control.setText(previous.getHtml());
                info = (CeylonBrowserInput) previous;
                update();
                forward.update();
            }
        }
        
        public void update() {
            if (info != null && info.getPrevious() != null) {
                BrowserInput previous = info.getPrevious();
                setToolTipText("Back to " + previous.getInputName());
                setEnabled(true);
            }
            else {
                setToolTipText("Back");
                setEnabled(false);
            }
        }
        
    }
    
    class ForwardAction extends Action {
        
        public ForwardAction() {
            setText("Forward");
            ISharedImages images = getWorkbench().getSharedImages();
            setImageDescriptor(images.getImageDescriptor(IMG_TOOL_FORWARD));
            setDisabledImageDescriptor(images.getImageDescriptor(IMG_TOOL_FORWARD_DISABLED));

            update();
        }
        
        @Override
        public void run() {
            BrowserInput next = info.getNext();
            if (next != null) {
                control.setText(next.getHtml());
                info = (CeylonBrowserInput) next;
                update();
                forward.update();
            }
        }
        
        public void update() {
            if (info != null && info.getNext() != null) {
                BrowserInput next = info.getNext();
                setToolTipText("Forward to " + next.getInputName());
                setEnabled(true);
            }
            else {
                setToolTipText("Forward");
                setEnabled(false);
            }
        }
        
    }
    
    final class OpenDeclarationAction extends Action {
        public OpenDeclarationAction() {
            setText("Open Declaration");
            setLocalImageDescriptors(this, "goto_input.gif");
        }
        @Override
        public void run() {
            gotoDeclaration(editor, getLinkedModel(info, editor, info.getAddress()));
        }
    }
    
}
