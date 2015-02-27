package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDeclarationHover;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getHoverInfo;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getLinkedModel;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.GOTO;
import static org.eclipse.jdt.internal.ui.JavaPluginImages.setLocalImageDescriptors;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK_DISABLED;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD_DISABLED;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.part.ViewPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.eclipse.code.browser.BrowserInput;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.code.html.HTML;
import com.redhat.ceylon.eclipse.code.html.HTMLPrinter;
import com.redhat.ceylon.eclipse.code.search.FindAssignmentsAction;
import com.redhat.ceylon.eclipse.code.search.FindReferencesAction;
import com.redhat.ceylon.eclipse.code.search.FindRefinementsAction;
import com.redhat.ceylon.eclipse.code.search.FindSubtypesAction;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.DocBrowser;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class DocumentationView extends ViewPart {
    
    private static final Image GOTO_IMAGE = CeylonPlugin.getInstance()
            .getImageRegistry().get(GOTO);
    
    private static DocumentationView instance;
    
    public static DocumentationView getInstance() {
        return instance;
    }
    
    public DocumentationView() {
        instance = this;
    }
    
    private DocBrowser control;
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
        control = new DocBrowser(parent, SWT.NONE); 
        control.addLocationListener(new LocationListener() {
            @Override
            public void changing(LocationEvent event) {
                String location = event.location;
                
                //necessary for windows environment (fix for blank page)
                //somehow related to this: https://bugs.eclipse.org/bugs/show_bug.cgi?id=129236
                if (!"about:blank".equals(location) && !location.startsWith("http:")) {
                    event.doit = false;
                }
                
                handleLink(location);
            }
            @Override
            public void changed(LocationEvent event) {}
        });
        // Replace browser's built-in context menu with none
        Menu menu = new Menu(getSite().getShell(), SWT.NONE);
        MenuItem menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openDeclarationAction.run();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        menuItem.setImage(GOTO_IMAGE);
        menuItem.setText("Open Declaration");
        control.setMenu(menu);

        updateWithCurrentEditor();
        
        control.addVisibilityWindowListener(new VisibilityWindowListener() {
            @Override
            public void show(WindowEvent event) {
                updateWithCurrentEditor();
            }
            @Override
            public void hide(WindowEvent event) {}
        });
    }

    private void updateWithCurrentEditor() {
        IEditorPart part = EditorUtil.getCurrentEditor();
        if (part instanceof CeylonEditor) {
            CeylonEditor editor = (CeylonEditor) part;
            IRegion selection = editor.getSelection();
            editor.getSelectionText();
            update(editor, selection.getOffset(), selection.getLength());
        }
    }
    //TODO: big copy/paste from DocumentationHover.handleLink
    private void handleLink(String location) {
        if (location.startsWith("dec:")) {
            Referenceable target = getLinkedModel(editor, location);
            if (target!=null) {
                Navigation.gotoDeclaration(target);
            }
        }
        else if (location.startsWith("doc:")) {
            Referenceable target = getLinkedModel(editor, location);
            if (target!=null) {
                info = getHoverInfo(target, info, editor, null);
                if (info!=null) control.setText(info.getHtml());
                back.update();
                forward.update();
            }
        }
        else if (location.startsWith("ref:")) {
            Referenceable target = getLinkedModel(editor, location);
            new FindReferencesAction(editor, (Declaration) target).run();
        }
        else if (location.startsWith("sub:")) {
            Referenceable target = getLinkedModel(editor, location);
            new FindSubtypesAction(editor, (Declaration) target).run();
        }
        else if (location.startsWith("act:")) {
            Referenceable target = getLinkedModel(editor, location);
            new FindRefinementsAction(editor, (Declaration) target).run();
        }
        else if (location.startsWith("ass:")) {
            Referenceable target = getLinkedModel(editor, location);
            new FindAssignmentsAction(editor, (Declaration) target).run();
        }
        /*else if (location.startsWith("stp:")) {
            CompilationUnit rn = editor.getParseController().getRootNode();
            Node node = Nodes.findNode(rn, Integer.parseInt(location.substring(4)));
            for (SpecifyTypeProposal stp: SpecifyTypeProposal.createProposals(rn, node, editor)) {
                stp.apply(editor.getParseController().getDocument());
                break;
            }
        }
        else if (location.startsWith("exv:")) {
            new ExtractValueProposal(editor).apply(editor.getParseController().getDocument());
        }
        else if (location.startsWith("exf:")) {
            new ExtractFunctionProposal(editor).apply(editor.getParseController().getDocument());
        }*/
    }
    
    @Override
    public void setFocus() {}
    
    public void update(CeylonEditor editor, int offset, int length) { 
        if (editor==null) {
            clear();
        }
        else {
            CeylonBrowserInput newInfo = getDeclarationHover(editor, 
                    new Region(offset, length));
            if (newInfo!=null && newInfo.getHtml()!=null) {
                if (info==null || 
                        !info.getHtml().equals(newInfo.getHtml())) {
                    info = newInfo;
                    control.setText(info.getHtml());
                    back.update();
                    forward.update();
                    openDeclarationAction.setEnabled(true);
                }
            }
            else if (this.editor!=editor) {
                clear();
            }
        }
        this.editor = editor;
    }

    private void clear() {
        StringBuilder buffer = new StringBuilder();
        HTMLPrinter.insertPageProlog(buffer, 0, HTML.getStyleSheet());
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("information.gif").toExternalForm(), 
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
            gotoDeclaration(getLinkedModel(editor, info.getAddress()));
        }
    }
    
}
