package com.redhat.ceylon.eclipse.core.debug.hover;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationHoverText;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getLinkedModel;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static java.lang.Integer.parseInt;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.correct.correctJ2C;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.debug.hover.ExpressionInformationControlCreator.ExpressionInformationControl;
import com.redhat.ceylon.ide.common.correct.specifyTypeQuickFix_;
import com.redhat.ceylon.model.typechecker.model.Referenceable;

final class CeylonLocationListener implements LocationListener {
    
    //TODO: this class is a big copy/paste of the other CeylonLocationListener
    
    private final CeylonEditor editor;
    private final ExpressionInformationControl control;
    
    CeylonLocationListener(CeylonEditor editor, 
            ExpressionInformationControl control) {
        this.editor = editor;
        this.control = control;
    }
    
    @Override
    public void changing(LocationEvent event) {
        String location = event.location;
        
        //necessary for windows environment (fix for blank page)
        //somehow related to this: https://bugs.eclipse.org/bugs/show_bug.cgi?id=129236
        if (!"about:blank".equals(location) && 
                !location.startsWith("http:")) {
            event.doit = false;
            handleLink(location);
        }
        
        /*else if (location.startsWith("javadoc:")) {
            final DocBrowserInformationControlInput input = (DocBrowserInformationControlInput) control.getInput();
            int beginIndex = input.getHtml().indexOf("javadoc:")+8;
            final String handle = input.getHtml().substring(beginIndex, input.getHtml().indexOf("\"",beginIndex));
            new Job("Fetching Javadoc") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    final IJavaElement elem = JavaCore.create(handle);
                    try {
                        final String javadoc = JavadocContentAccess2.getHTMLContent((IMember) elem, true);
                        if (javadoc!=null) {
                            PlatformUI.getWorkbench().getProgressService()
                                    .runInUI(editor.getSite().getWorkbenchWindow(), new IRunnableWithProgress() {
                                @Override
                                public void run(IProgressMonitor monitor) 
                                        throws InvocationTargetException, InterruptedException {
                                    StringBuilder sb = new StringBuilder();
                                    HTMLPrinter.insertPageProlog(sb, 0, getStyleSheet());
                                    appendJavadoc(elem, javadoc, sb);
                                    HTMLPrinter.addPageEpilog(sb);
                                    control.setInput(new DocBrowserInformationControlInput(input, null, sb.toString(), 0));
                                }
                            }, null);
                        }
                    } 
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Status.OK_STATUS;
                }
            }.schedule();
        }*/
    }
    
    private void close(ExpressionInformationControl control) {
        control.dispose();
    }
    
    private void handleLink(String location) {
        if (location.startsWith("dec:")) {
            Referenceable target = 
                    getLinkedModel(location, editor);
            if (target!=null) {
                close(control); //FIXME: should have protocol to hide, rather than dispose
                gotoDeclaration(target);
            }
        }
        else if (location.startsWith("doc:")) {
            Referenceable target = 
                    getLinkedModel(location, editor);
            if (target!=null) {
                String text = 
                        getDocumentationHoverText(target, 
                                editor, null, null);
                DebugHoverInput input = 
                        new DebugHoverInput(
                                control.getVariable(), 
                                text);
                control.setInput(input);
            }
        }
        /*else if (location.startsWith("ref:")) {
            Declaration target = (Declaration)
                    getLinkedModel(location, editor);
            close(control);
            new FindReferencesAction(editor,target).run();
        }
        else if (location.startsWith("sub:")) {
            Declaration target = (Declaration)
                    getLinkedModel(location, editor);
            close(control);
            new FindSubtypesAction(editor,target).run();
        }
        else if (location.startsWith("act:")) {
            Declaration target = (Declaration)
                    getLinkedModel(location, editor);
            close(control);
            new FindRefinementsAction(editor,target).run();
        }
        else if (location.startsWith("ass:")) {
            Declaration target = (Declaration)
                    getLinkedModel(location, editor);
            close(control);
            new FindAssignmentsAction(editor,target).run();
        }*/
        else {
            CeylonParseController controller = 
                    editor.getParseController();
            IDocument document = 
                    controller.getDocument();
            if (location.startsWith("stp:")) {
                close(control);
                Tree.CompilationUnit rn = 
                        controller.getLastCompilationUnit();
                int offset = 
                        parseInt(location.substring(4));
                Node node = findNode(rn, offset);
                if (node instanceof Tree.Type) {
                    Tree.Type type = (Tree.Type) node;
                    specifyTypeQuickFix_.get_().specifyType(
                        rn,
                        new correctJ2C().newDocument(document),
                        type,
                        true,
                        type.getTypeModel()
                    );
                }
            }
            /*else if (location.startsWith("exv:")) {
                close(control);
                new ExtractValueProposal(editor)
                    .apply(document);
            }
            else if (location.startsWith("exf:")) {
                close(control);
                new ExtractFunctionProposal(editor)
                    .apply(document);
            }*/
        }
    }
    
    @Override
    public void changed(LocationEvent event) {}
}