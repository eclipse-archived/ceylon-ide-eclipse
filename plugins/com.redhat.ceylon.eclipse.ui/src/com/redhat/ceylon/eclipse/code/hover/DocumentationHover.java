package com.redhat.ceylon.eclipse.code.hover;

/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Genady Beryozkin <eclipse@genady.org> - [hovering] tooltip for constant string does not show constant value - https://bugs.eclipse.org/bugs/show_bug.cgi?id=85382
 *******************************************************************************/

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDocDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.isVariable;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getInitialValueDescription;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.getJavaElement;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.addPageEpilog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.convertToHTMLContent;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.insertPageProlog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.toHex;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getTypeCheckers;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.getFrame;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.getJdiProducedType;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.toModelProducedType;
import static com.redhat.ceylon.eclipse.core.debug.hover.CeylonDebugHover.jdiVariableForTypeParameter;
import static com.redhat.ceylon.eclipse.util.Highlights.ANNOTATIONS;
import static com.redhat.ceylon.eclipse.util.Highlights.CHARS;
import static com.redhat.ceylon.eclipse.util.Highlights.NUMBERS;
import static com.redhat.ceylon.eclipse.util.Highlights.STRINGS;
import static com.redhat.ceylon.eclipse.util.Highlights.getCurrentThemeColor;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static java.lang.Character.codePointCount;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static org.eclipse.jdt.internal.ui.JavaPluginImages.setLocalImageDescriptors;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK_DISABLED;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD_DISABLED;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;

import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Configuration.Builder;
import com.github.rjeschke.txtmark.Processor;
import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Constructor;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedTypedReference;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnonymousAnnotation;
import com.redhat.ceylon.compiler.typechecker.util.ProducedTypeNamePrinter;
import com.redhat.ceylon.eclipse.code.browser.BrowserInformationControl;
import com.redhat.ceylon.eclipse.code.browser.BrowserInput;
import com.redhat.ceylon.eclipse.code.correct.ExtractFunctionProposal;
import com.redhat.ceylon.eclipse.code.correct.ExtractValueProposal;
import com.redhat.ceylon.eclipse.code.correct.SpecifyTypeProposal;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.html.HTML;
import com.redhat.ceylon.eclipse.code.html.HTMLPrinter;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.search.FindAssignmentsAction;
import com.redhat.ceylon.eclipse.code.search.FindReferencesAction;
import com.redhat.ceylon.eclipse.code.search.FindRefinementsAction;
import com.redhat.ceylon.eclipse.code.search.FindSubtypesAction;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.util.UnlinkedSpanEmitter;


public class DocumentationHover extends SourceInfoHover {
    
    public static final String smallerSize = "90%";
    public static final String annotationSize = "85%";
    public static final String largerSize = "103%";
    
    public DocumentationHover(CeylonEditor editor) {
        super(editor);
    }

    final class CeylonLocationListener implements LocationListener {
        
        private final BrowserInformationControl control;
        
        CeylonLocationListener(BrowserInformationControl control) {
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
        
        private void handleLink(String location) {
            if (location.startsWith("dec:")) {
                Referenceable target = 
                        getLinkedModel(location,editor);
                if (target!=null) {
                    close(control); //FIXME: should have protocol to hide, rather than dispose
                    gotoDeclaration(target);
                }
            }
            else if (location.startsWith("doc:")) {
                Referenceable target = 
                        getLinkedModel(location,editor);
                if (target!=null) {
                    String text = 
                            getDocumentationHoverText(target, 
                                    editor, null);
                    CeylonBrowserInput input = 
                            new CeylonBrowserInput(control.getInput(), 
                                    target, text);
                    control.setInput(input);
                }
            }
            else if (location.startsWith("ref:")) {
                Declaration target = (Declaration)
                        getLinkedModel(location,editor);
                close(control);
                new FindReferencesAction(editor,target).run();
            }
            else if (location.startsWith("sub:")) {
                Declaration target = (Declaration)
                        getLinkedModel(location,editor);
                close(control);
                new FindSubtypesAction(editor,target).run();
            }
            else if (location.startsWith("act:")) {
                Declaration target = (Declaration)
                        getLinkedModel(location,editor);
                close(control);
                new FindRefinementsAction(editor,target).run();
            }
            else if (location.startsWith("ass:")) {
                Declaration target = (Declaration)
                        getLinkedModel(location,editor);
                close(control);
                new FindAssignmentsAction(editor,target).run();
            }
            else {
                CeylonParseController controller = 
                        editor.getParseController();
                IDocument document = 
                        controller.getDocument();
                if (location.startsWith("stp:")) {
                    close(control);
                    Tree.CompilationUnit rootNode = 
                            controller.getRootNode();
                    int offset = parseInt(location.substring(4));
                    Node node = findNode(rootNode, offset);
                    SpecifyTypeProposal
                        .createProposal(rootNode, node, editor)
                        .apply(document);
                }
                else if (location.startsWith("exv:")) {
                    close(control);
                    new ExtractValueProposal(editor)
                        .apply(document);
                }
                else if (location.startsWith("exf:")) {
                    close(control);
                    new ExtractFunctionProposal(editor)
                        .apply(document);
                }
            }
        }
        
        @Override
        public void changed(LocationEvent event) {}
    }
    
    /**
     * Action to go back to the previous input in the hover control.
     */
    static final class BackAction extends Action {
        private final BrowserInformationControl fInfoControl;

        public BackAction(BrowserInformationControl infoControl) {
            fInfoControl= infoControl;
            setText("Back");
            ISharedImages images= getWorkbench().getSharedImages();
            setImageDescriptor(images.getImageDescriptor(IMG_TOOL_BACK));
            setDisabledImageDescriptor(images.getImageDescriptor(IMG_TOOL_BACK_DISABLED));

            update();
        }

        @Override
        public void run() {
            BrowserInput previous= (BrowserInput) 
                    fInfoControl.getInput().getPrevious();
            if (previous != null) {
                fInfoControl.setInput(previous);
            }
        }

        public void update() {
            BrowserInput current = 
                    fInfoControl.getInput();
            if (current!=null && 
                    current.getPrevious()!=null) {
                BrowserInput previous = 
                        current.getPrevious();
                setToolTipText("Back to " + 
                        previous.getInputName());
                setEnabled(true);
            }
            else {
                setToolTipText("Back");
                setEnabled(false);
            }
        }
    }

    /**
     * Action to go forward to the next input in the hover control.
     */
    static final class ForwardAction extends Action {
        private final BrowserInformationControl fInfoControl;

        public ForwardAction(BrowserInformationControl infoControl) {
            fInfoControl = infoControl;
            setText("Forward");
            ISharedImages images= getWorkbench().getSharedImages();
            setImageDescriptor(images.getImageDescriptor(IMG_TOOL_FORWARD));
            setDisabledImageDescriptor(images.getImageDescriptor(IMG_TOOL_FORWARD_DISABLED));

            update();
        }

        @Override
        public void run() {
            BrowserInput next = (BrowserInput) 
                    fInfoControl.getInput().getNext();
            if (next != null) {
                fInfoControl.setInput(next);
            }
        }

        public void update() {
            BrowserInput current = 
                    fInfoControl.getInput();
            if (current!=null && 
                    current.getNext()!=null) {
                setToolTipText("Forward to " + 
                        current.getNext().getInputName());
                setEnabled(true);
            }
            else {
                setToolTipText("Forward");
                setEnabled(false);
            }
        }
    }
    
    /**
     * Action that opens the current hover input element.
     */
    final class OpenDeclarationAction extends Action {
        
        private final BrowserInformationControl fInfoControl;
        
        public OpenDeclarationAction(BrowserInformationControl infoControl) {
            fInfoControl = infoControl;
            setText("Open Declaration");
            setToolTipText("Open Declaration");
            setLocalImageDescriptors(this, "goto_input.gif");
        }
        @Override
        public void run() {
            close(fInfoControl); //FIXME: should have protocol to hide, rather than dispose
            CeylonBrowserInput input = (CeylonBrowserInput) 
                    fInfoControl.getInput();
            gotoDeclaration(getLinkedModel(input.getAddress(), 
                    editor));
        }
    }

    private static void close(BrowserInformationControl control) {
        control.notifyDelayedInputChange(null);
        control.dispose();
    }
    
    @Override
    public IInformationControlCreator getHoverControlCreator() {
        return getHoverControlCreator("F2 for focus");
    }

    public IInformationControlCreator getHoverControlCreator(
            final String statusLineMessage) {
        return new AbstractReusableInformationControlCreator() {
            @Override
            public IInformationControl doCreateInformationControl(Shell parent) {
                BrowserInformationControl control = 
                        new BrowserInformationControl(parent, 
                                APPEARANCE_JAVADOC_FONT, 
                                statusLineMessage) {
                    /**
                     * Create the "enriched" control when 
                     * the hover receives focus
                     */
                    @Override
                    public IInformationControlCreator getInformationPresenterControlCreator() {
                        return new AbstractReusableInformationControlCreator() {
                            @Override
                            public IInformationControl doCreateInformationControl(Shell parent) {
                                ToolBarManager tbm = new ToolBarManager(SWT.FLAT);
                                BrowserInformationControl control = 
                                        new BrowserInformationControl(parent, 
                                                APPEARANCE_JAVADOC_FONT, tbm);

                                final BackAction backAction = 
                                        new BackAction(control);
                                backAction.setEnabled(false);
                                tbm.add(backAction);
                                final ForwardAction forwardAction = 
                                        new ForwardAction(control);
                                tbm.add(forwardAction);
                                forwardAction.setEnabled(false);

                                final OpenDeclarationAction openDeclarationAction = 
                                        new OpenDeclarationAction(control);
                                tbm.add(openDeclarationAction);

                                IInputChangedListener inputChangeListener = 
                                        new IInputChangedListener() {
                                    public void inputChanged(Object newInput) {
                                        backAction.update();
                                        forwardAction.update();
                                        boolean isDeclaration = false;
                                        if (newInput instanceof CeylonBrowserInput) {
                                            CeylonBrowserInput input = 
                                                    (CeylonBrowserInput) newInput;
                                            isDeclaration = input.getAddress()!=null;
                                        }
                                        openDeclarationAction.setEnabled(isDeclaration);
                                    }
                                };
                                control.addInputChangeListener(inputChangeListener);

                                tbm.update(true);

                                control.addLocationListener(new CeylonLocationListener(control));
                                return control;
                            }
                        };
                    }
                };
                control.addLocationListener(new CeylonLocationListener(control));
                return control;
            }
        };
    }
    
    public static Referenceable getLinkedModel(String location, 
            CeylonEditor editor) {
        CeylonParseController controller = 
                editor.getParseController();
        if (location==null) {
            return null;
        }
        else if (location.matches("doc:ceylon.language/.*:ceylon.language:Nothing")) {
            Unit unit = controller.getRootNode().getUnit();
            return unit.getNothingDeclaration();
        }
        return getLinkedModel(location, 
                controller.getTypeChecker());
    }
    
    public static Referenceable getLinkedModel(String location) {
        if (location==null) {
            return null;
        }
        for (TypeChecker typeChecker: getTypeCheckers()) {
            Referenceable linkedModel = 
                    getLinkedModel(location, typeChecker);
            if (linkedModel!=null) {
                return linkedModel;
            }
        }
        return null;
    }

    public static Referenceable getLinkedModel(String location, 
            TypeChecker typeChecker) {
        String[] bits = location.split(":");
        JDTModelLoader modelLoader = 
                getModelLoader(typeChecker);
        String moduleNameAndVersion = bits[1];
        int loc = moduleNameAndVersion.indexOf('/');
        String moduleName = 
                moduleNameAndVersion.substring(0,loc);
        String moduleVersion = 
                moduleNameAndVersion.substring(loc+1);
        Module module = 
                modelLoader.getLoadedModule(moduleName, 
                        moduleVersion);
        if (module==null || bits.length==2) {
            return module;
        }
        Referenceable target = 
                module.getPackage(bits[2]);
        for (int i=3; i<bits.length; i++) {
            Scope scope;
            if (target instanceof Scope) {
                scope = (Scope) target;
            }
            else if (target instanceof TypedDeclaration) {
                TypedDeclaration td = 
                        (TypedDeclaration) target;
                scope = td.getType().getDeclaration();
            }
            else {
                return null;
            }
            if (scope instanceof Value) {
                Value v = (Value) scope;
                TypeDeclaration val = 
                        v.getTypeDeclaration();
                if (val.isAnonymous()) {
                    scope = val;
                }
            }
            target = scope.getDirectMember(bits[i], null, false);
        }
        return target;
    }
    
    public String getHoverInfo(ITextViewer textViewer, 
            IRegion hoverRegion) {
        if (editor==null || 
                editor.getSelectionProvider()==null) {
            return null;
        }
        String result = 
                getExpressionHoverText(editor, hoverRegion);
        if (result==null) {
            result = getHoverText(editor, hoverRegion);
        }
        return result;
    }

    @Override
    public CeylonBrowserInput getHoverInfo2(ITextViewer textViewer, 
            IRegion hoverRegion) {
        if (editor==null || 
                editor.getSelectionProvider()==null) {
            return null;
        }
        String result = 
                getExpressionHoverText(editor, hoverRegion);
        if (result!=null) {
            return new CeylonBrowserInput(null, null, result);
        }
        else {
            result = getHoverText(editor, hoverRegion);
            if (result!=null) {
                return new CeylonBrowserInput(null, 
                        getModel(editor, hoverRegion), 
                        result);
            }
        }
        return null;
    }

    static String getExpressionHoverText(CeylonEditor editor, 
            IRegion hoverRegion) {
        CeylonParseController parseController = 
                editor.getParseController();
        if (parseController==null) {
            return null;
        }
        Tree.CompilationUnit rootNode = 
                parseController.getRootNode();
        if (rootNode!=null) {
            int hoffset = hoverRegion.getOffset();
            ITextSelection selection = 
                    editor.getSelectionFromThread();
            if (selection!=null) {
                int offset = selection.getOffset();
                int length = selection.getLength();
                if (offset<=hoffset && offset+length>=hoffset) {
                    Node node = findNode(rootNode, 
                            offset, offset+length-1);
                    IDocument document = 
                            editor.getCeylonSourceViewer()
                                    .getDocument();
                    IProject project = 
                            editor.getParseController()
                                    .getProject();
                    if (node instanceof Tree.Type) {
                        return getTypeHoverText(node, 
                                selection.getText(), 
                                document,
                                project);
                    }
                    if (node instanceof Tree.Expression) {
                        node = ((Tree.Expression) node).getTerm();
                    }
                    if (node instanceof Tree.Term) {
                        return getTermTypeHoverText(node, 
                                selection.getText(), 
                                document,
                                project);
                    }
                }
            }
        }
        return null;
    }
    
    static Referenceable getModel(CeylonEditor editor,
            IRegion hoverRegion) {
        Node node = getHoverNode(hoverRegion, 
                editor.getParseController());
        return node!=null ? 
                getReferencedDeclaration(node) : null;
    }
    
    static String getHoverText(CeylonEditor editor,
            IRegion hoverRegion) {
        CeylonParseController parseController = 
                editor.getParseController();

        Node node = getHoverNode(hoverRegion, 
                parseController);
        if (node!=null) {
            IProject project = 
                    parseController.getProject();
            if (node instanceof Tree.LocalModifier) {
                return getInferredTypeHoverText(node,
                        project);
            }
            else if (node instanceof Tree.Literal) {
                IDocument document = 
                        editor.getCeylonSourceViewer()
                            .getDocument();
                return getTermTypeHoverText(node, null, 
                        document,
                        project);
            }
            else {
                Referenceable model = 
                        getReferencedDeclaration(node);
                return getDocumentationHoverText(model, 
                        editor, node);
            }
        }
        else {
            return null;
        }
    }

    private static String getInferredTypeHoverText(Node node, 
            IProject project) {
        Tree.LocalModifier local = (Tree.LocalModifier) node;
        ProducedType t = local.getTypeModel();
        if (t==null) return null;
        StringBuilder buffer = new StringBuilder();
        HTMLPrinter.insertPageProlog(buffer, 0, HTML.getStyleSheet());
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("types.gif").toExternalForm(), 
                16, 16, 
                "Inferred type&nbsp;&nbsp;<tt>" + 
                producedTypeLink(t, node.getUnit()) + "</tt>", 
                20, 4);
        buffer.append("<br/>");
        if (!t.containsUnknowns()) {
            buffer.append("One quick assist available:<br/>");
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("correction_change.gif").toExternalForm(), 
                    16, 16, 
                    "<a href=\"stp:" + node.getStartIndex() + 
                    "\">Specify explicit type</a>", 
                    20, 4);
        }
        //buffer.append(getDocumentationFor(editor.getParseController(), t.getDeclaration()));
        HTMLPrinter.addPageEpilog(buffer);
        return buffer.toString();
    }
    
    private static String getTypeHoverText(Node node, String selectedText, 
            IDocument doc, IProject project) {
        Tree.Type type = (Tree.Type) node;
        ProducedType t = type.getTypeModel();
        if (t==null) return null;
//        String expr = "";
//        try {
//            expr = doc.get(node.getStartIndex(), node.getStopIndex()-node.getStartIndex()+1);
//        } 
//        catch (BadLocationException e) {
//            e.printStackTrace();
//        }
        Unit unit = node.getUnit();
        String abbreviated = 
                PRINTER.getProducedTypeName(t,unit);
        String unabbreviated = 
                VERBOSE_PRINTER.getProducedTypeName(t,unit);
        StringBuilder buffer = new StringBuilder();
        HTMLPrinter.insertPageProlog(buffer, 0, HTML.getStyleSheet());
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("types.gif").toExternalForm(), 
                16, 16, 
                "<tt>" + producedTypeLink(t,unit) + "</tt> ", 
                20, 4);
        if (!abbreviated.equals(unabbreviated)) {
            buffer.append("<br/>")
                  .append("Abbreviation&nbsp;of:&nbsp;&nbsp;")
                  .append(unabbreviated);
        }
        HTMLPrinter.addPageEpilog(buffer);
        return buffer.toString();

    }
    
    private static String escape(String string) {
        return string
                .replace("\0", "\\0")
                .replace("\b", "\\b")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\u001b", "\\e");
    }
    
    private static String getTermTypeHoverText(Node node, String selectedText, 
            IDocument doc, IProject project) {
        Tree.Term term = (Tree.Term) node;
        ProducedType t = term.getTypeModel();
        if (t==null) return null;
//        String expr = "";
//        try {
//            expr = doc.get(node.getStartIndex(), node.getStopIndex()-node.getStartIndex()+1);
//        } 
//        catch (BadLocationException e) {
//            e.printStackTrace();
//        }
        StringBuilder buffer = new StringBuilder();
        HTMLPrinter.insertPageProlog(buffer, 0, HTML.getStyleSheet());
        String desc = node instanceof Tree.Literal ? 
                "Literal of type" : "Expression of type";
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("types.gif").toExternalForm(), 
                16, 16, 
                desc + "&nbsp;&nbsp;<tt>" + 
                producedTypeLink(t, node.getUnit()) + "</tt> ", 
                20, 4);
        if (node instanceof Tree.StringLiteral) {
            String escaped = escape(node.getText());
            if (escaped.length()>250) {
                escaped = escaped.substring(0,250) + "...";
            }
            buffer.append( "<br/>")
                .append("<code style='color:")
                .append(toHex(getCurrentThemeColor(STRINGS)))
                .append("'><pre>")
                .append('\"')
                .append(convertToHTMLContent(escaped).replace("\\n", "<br/>"))
                .append('\"')
                .append("</pre></code>");
            // If a single char selection, then append info on that character too
            if (selectedText != null
                    && codePointCount(selectedText, 0, selectedText.length()) == 1) {
                appendCharacterHoverInfo(buffer, selectedText);
            }
        }
        else if (node instanceof Tree.CharLiteral) {
            String character = node.getText();
            if (character.length()>2) {
                appendCharacterHoverInfo(buffer, 
                        character.substring(1, character.length()-1));
            }
        }
        else if (node instanceof Tree.NaturalLiteral) {
            try {
                buffer.append("<br/>")
                    .append("<code style='color:")
                    .append(toHex(getCurrentThemeColor(NUMBERS)))
                    .append("'>");
                String text = node.getText().replace("_", "");
                switch (text.charAt(0)) {
                case '#':
                    buffer.append(parseLong(text.substring(1),16));
                    break;
                case '$':
                    buffer.append(parseLong(text.substring(1),2));
                    break;
                default:
                    buffer.append(parseLong(text));
                }
                buffer.append("</code>");
            }
            catch (NumberFormatException nfe) {}
        }
        else if (node instanceof Tree.FloatLiteral) {
            try {
                buffer.append("<br/>")
                    .append("<code style='color:")
                    .append(toHex(getCurrentThemeColor(NUMBERS)))
                    .append("'>")
                    .append(parseDouble(node.getText().replace("_", "")))
                    .append("</code>");
            }
            catch (NumberFormatException nfe) {}
        }
        if (selectedText!=null) {
            buffer.append("<br/>")
                .append("Two quick assists available:<br/>");
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("change.png").toExternalForm(), 
                    16, 16, 
                    "<a href=\"exv:\">Extract value</a>", 
                    20, 4);
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("change.png").toExternalForm(), 
                    16, 16,
                    "<a href=\"exf:\">Extract function</a>", 
                    20, 4);
            buffer.append("<br/>");
        }
        HTMLPrinter.addPageEpilog(buffer);
        return buffer.toString();
    }

    private static void appendCharacterHoverInfo(StringBuilder buffer, 
            String character) {
        buffer.append( "<br/>")
            .append("<code style='color:")
            .append(toHex(getCurrentThemeColor(CHARS)))
            .append("'>")
            .append('\'')
            .append(convertToHTMLContent(escape(character)))
            .append('\'')
            .append("</code>");
        int codepoint = Character.codePointAt(character, 0);
        String name = Character.getName(codepoint);
        buffer.append("<br/>Unicode Name: <code>").append(name).append("</code>");
        String hex = Integer.toHexString(codepoint).toUpperCase();
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        buffer.append("<br/>Codepoint: <code>").append("U+").append(hex).append("</code>");
        buffer.append("<br/>General Category: <code>")
            .append(getCodepointGeneralCategoryName(codepoint)).append("</code>");
        Character.UnicodeScript script = Character.UnicodeScript.of(codepoint);
        buffer.append("<br/>Script: <code>").append(script.name()).append("</code>");
        Character.UnicodeBlock block = Character.UnicodeBlock.of(codepoint);
        buffer.append("<br/>Block: <code>").append(block).append("</code><br/>");
    }

    private static String getCodepointGeneralCategoryName(int codepoint) {
        String gc;
        switch (Character.getType(codepoint)) {
        case Character.COMBINING_SPACING_MARK:
            gc = "Mark, combining spacing"; break;
        case Character.CONNECTOR_PUNCTUATION:
            gc = "Punctuation, connector"; break;
        case Character.CONTROL:
            gc = "Other, control"; break;
        case Character.CURRENCY_SYMBOL:
            gc = "Symbol, currency"; break;
        case Character.DASH_PUNCTUATION:
            gc = "Punctuation, dash"; break;
        case Character.DECIMAL_DIGIT_NUMBER:
            gc = "Number, decimal digit"; break;
        case Character.ENCLOSING_MARK:
            gc = "Mark, enclosing"; break;
        case Character.END_PUNCTUATION:
            gc = "Punctuation, close"; break;
        case Character.FINAL_QUOTE_PUNCTUATION:
            gc = "Punctuation, final quote"; break;
        case Character.FORMAT:
            gc = "Other, format"; break;
        case Character.INITIAL_QUOTE_PUNCTUATION:
            gc = "Punctuation, initial quote"; break;
        case Character.LETTER_NUMBER:
            gc = "Number, letter"; break;
        case Character.LINE_SEPARATOR:
            gc = "Separator, line"; break;
        case Character.LOWERCASE_LETTER:
            gc = "Letter, lowercase"; break;
        case Character.MATH_SYMBOL:
            gc = "Symbol, math"; break;
        case Character.MODIFIER_LETTER:
            gc = "Letter, modifier"; break;
        case Character.MODIFIER_SYMBOL:
            gc = "Symbol, modifier"; break;
        case Character.NON_SPACING_MARK:
            gc = "Mark, nonspacing"; break;
        case Character.OTHER_LETTER:
            gc = "Letter, other"; break;
        case Character.OTHER_NUMBER:
            gc = "Number, other"; break;
        case Character.OTHER_PUNCTUATION:
            gc = "Punctuation, other"; break;
        case Character.OTHER_SYMBOL:
            gc = "Symbol, other"; break;
        case Character.PARAGRAPH_SEPARATOR:
            gc = "Separator, paragraph"; break;
        case Character.PRIVATE_USE:
            gc = "Other, private use"; break;
        case Character.SPACE_SEPARATOR:
            gc = "Separator, space"; break;
        case Character.START_PUNCTUATION:
            gc = "Punctuation, open"; break;
        case Character.SURROGATE:
            gc = "Other, surrogate"; break;
        case Character.TITLECASE_LETTER:
            gc = "Letter, titlecase"; break;
        case Character.UNASSIGNED:
            gc = "Other, unassigned"; break;
        case Character.UPPERCASE_LETTER:
            gc = "Letter, uppercase"; break;
        default:
            gc = "&lt;Unknown&gt;";
        }
        return gc;
    }
    
    private static String getIcon(Object obj) {
        if (obj instanceof Module) {
            return "jar_l_obj.gif";
        }
        else if (obj instanceof Package) {
            return "package_obj.gif";
        }
        else if (obj instanceof Declaration) {
            Declaration dec = (Declaration) obj;
            if (dec instanceof Class) {
                String icon = dec.isShared() ? 
                        "class_obj.gif" : 
                        "innerclass_private_obj.gif";
                return decorateTypeIcon(dec, icon);
            }
            else if (dec instanceof Interface) {
                String icon = dec.isShared() ? 
                        "int_obj.gif" : 
                        "innerinterface_private_obj.gif";
                return decorateTypeIcon(dec, icon);
            }
            else if (dec instanceof Constructor) {
                String icon = dec.isShared() ? 
                        "constructor.gif" : 
                        "constructor.gif"; //TODO!!!!!!
                return decorateTypeIcon(dec, icon);
            }
            else if (dec instanceof TypeAlias||
                    dec instanceof NothingType) {
                return "type_alias.gif";
            }
            else if (dec.isParameter()) {
                if (dec instanceof Method) {
                    return "methpro_obj.gif";
                }
                else {
                    return "field_protected_obj.gif";
                }
            }
            else if (dec instanceof Method) {
                String icon = dec.isShared() ?
                        "public_co.gif" : 
                        "private_co.gif";
                return decorateFunctionIcon(dec, icon);
            }
            else if (dec instanceof MethodOrValue) {
                return dec.isShared() ?
                        "field_public_obj.gif" : 
                        "field_private_obj.gif";
            }
            else if (dec instanceof TypeParameter) {
                return "typevariable_obj.gif";
            }
        }
        return null;
    }

    private static String decorateFunctionIcon(Declaration dec, String icon) {
        if (dec.isAnnotation()) {
            return icon.replace("co", "ann");
        }
        else {
            return icon;
        }
    }

    private static String decorateTypeIcon(Declaration dec, String icon) {
        TypeDeclaration td = (TypeDeclaration) dec;
        if (td.getCaseTypes()!=null) {
            return icon.replace("obj", "enum");
        }
        else if (dec.isAnnotation()) {
            return icon.replace("obj", "ann");
        }
        else if (td.isAlias()) {
            return icon.replace("obj", "alias");
        }
        else {
            return icon;
        }
    }

    /**
     * Computes the hover info.
     * @param node 
     * @param elements the resolved elements
     * @param editorInputElement the editor input, or <code>null</code>
     * @return the HTML hover info for the given element(s) or <code>null</code> 
     *         if no information is available
     * @since 3.4
     */
    public static String getDocumentationHoverText(Referenceable model, 
            CeylonEditor editor, Node node) {
        CeylonParseController parseController = 
                editor.getParseController();
        if (model instanceof Declaration) {
            Declaration dec = (Declaration) model;
            return getDocumentationFor(parseController, dec, 
                    node, null);
        }
        else if (model instanceof Package) {
            Package dec = (Package) model;
            return getDocumentationFor(parseController, dec);
        }
        else if (model instanceof Module) {
            Module dec = (Module) model;
            return getDocumentationFor(parseController, dec);
        }
        else {
            return null;
        }
    }

    private static void appendJavadoc(IJavaElement elem, StringBuilder sb) {
        if (elem instanceof IMember) {
            try {
                //TODO: Javadoc @ icon?
                IMember mem = (IMember) elem;
                String jd = JavadocContentAccess2.getHTMLContent(mem, true);
                if (jd!=null) {
                    sb.append("<br/>").append(jd);
                    String base = getBaseURL(mem, mem.isBinary());
                    int endHeadIdx= sb.indexOf("</head>");
                    sb.insert(endHeadIdx, "\n<base href='" + base + "'>\n");
                }
            } 
            catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getBaseURL(IJavaElement element, boolean isBinary) 
            throws JavaModelException {
        if (isBinary) {
            // Source attachment usually does not include Javadoc resources
            // => Always use the Javadoc location as base:
            URL baseURL = JavaUI.getJavadocLocation(element, false);
            if (baseURL != null) {
                if (baseURL.getProtocol().equals("jar")) {
                    // It's a JarURLConnection, which is not known to the browser widget.
                    // Let's start the help web server:
                    URL baseURL2 = 
                            getWorkbench().getHelpSystem()
                                .resolve(baseURL.toExternalForm(), true);
                    if (baseURL2 != null) { // can be null if org.eclipse.help.ui is not available
                        baseURL = baseURL2;
                    }
                }
                return baseURL.toExternalForm();
            }
        }
        else {
            IResource resource = element.getResource();
            if (resource != null) {
                /*
                 * Too bad: Browser widget knows nothing about EFS and custom URL handlers,
                 * so IResource#getLocationURI() does not work in all cases.
                 * We only support the local file system for now.
                 * A solution could be https://bugs.eclipse.org/bugs/show_bug.cgi?id=149022 .
                 */
                IPath location = resource.getLocation();
                if (location != null) {
                    return location.toFile().toURI().toString();
                }
            }
        }
        return null;
    }

    public static String getDocumentationFor(CeylonParseController controller, 
            Package pack) {
        StringBuilder buffer= new StringBuilder();
        addMainPackageDescription(pack, buffer);
        addPackageDocumentation(controller, pack, buffer);
        addAdditionalPackageInfo(buffer, pack);
        addPackageMembers(buffer, pack);
        addPackageModuleInfo(pack, buffer);
        insertPageProlog(buffer, 0, HTML.getStyleSheet());
        addPageEpilog(buffer);
        return buffer.toString();
        
    }

    private static void addPackageMembers(StringBuilder buffer, 
            Package pack) {
        boolean first = true;
        for (Declaration dec: pack.getMembers()) {
            if (dec.getName()==null) {
                continue;
            }
            if (dec instanceof Class && 
                    ((Class) dec).isOverloaded()) {
                continue;
            }
            if (dec.isShared() && !dec.isAnonymous()) {
                if (first) {
                    buffer.append("<p>Contains:&nbsp;&nbsp;");
                    first = false;
                }
                else {
                    buffer.append(", ");
                }

                /*addImageAndLabel(buffer, null, fileUrl(getIcon(dec)).toExternalForm(), 
                    16, 16, "<tt><a " + link(dec) + ">" + 
                    dec.getName() + "</a></tt>", 20, 2);*/
                appendLink(buffer, dec);
            }
        }
        if (!first) {
            buffer.append(".</p>");
        }
    }

    private static void appendLink(StringBuilder buffer, 
            Referenceable model) {
        buffer.append("<tt><a ")
            .append(HTML.link(model))
            .append(">")
            .append(model.getNameAsString())
            .append("</a></tt>");
    }
    
    private static String link(Referenceable dec) {
        StringBuilder builder = new StringBuilder();
        appendLink(builder, dec);
        return builder.toString(); 
    }

    private static void addAdditionalPackageInfo(StringBuilder buffer,
            Package pack) {
        Module mod = pack.getModule();
        if (mod.isJava()) {
            buffer.append("<p>This package is implemented in Java.</p>");
        }
        if (JDKUtils.isJDKModule(mod.getNameAsString())) {
            buffer.append("<p>This package forms part of the Java SDK.</p>");            
        }
    }

    private static void addMainPackageDescription(Package pack,
            StringBuilder buffer) {
        if (pack.isShared()) {
            String ann = toHex(getCurrentThemeColor(ANNOTATIONS));
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("annotation_obj.gif").toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:" + annotationSize + 
                    ";color:" + ann + "'>shared</span></tt>"
                    , 20, 4);
        }
        HTML.addImageAndLabel(buffer, pack, 
                HTML.fileUrl(getIcon(pack)).toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                HTML.highlightLine(description(pack)) +
                "</span></tt>", 
                20, 4);
    }

    private static void addPackageModuleInfo(Package pack,
            StringBuilder buffer) {
        Module mod = pack.getModule();
        String label;
        if (mod.getNameAsString().isEmpty() || 
                mod.getNameAsString().equals("default")) {
            label = "<span style='font-size:" + smallerSize + 
                    "'>in default module</span>";
        }
        else {
            label = "<span style='font-size:" + smallerSize + 
                    "'>in module&nbsp;&nbsp;" + link(mod) + 
                    " \"" + mod.getVersion() + "\"" + "</span>";
        }
        HTML.addImageAndLabel(buffer, mod, 
                HTML.fileUrl(getIcon(mod)).toExternalForm(), 
                16, 16, label, 20, 2);
    }
    
    private static String description(Package pack) {
        return "package " + pack.getNameAsString();
    }
    
    public static String getDocumentationFor(ModuleDetails mod, 
            String version, Scope scope, Unit unit) {
        return getDocumentationForModule(mod.getName(), version, 
                mod.getDoc(), scope, unit);
    }
    
    public static String getDocumentationFor(ModuleDetails mod, 
            String version, String packageName, Scope scope, 
            Unit unit) {
        StringBuilder buffer = new StringBuilder();
        String ann = toHex(getCurrentThemeColor(ANNOTATIONS));
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("annotation_obj.gif").toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + annotationSize + 
                ";color:" + ann + "'>shared</span></tt>", 
                20, 4);
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("package_obj.gif").toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                HTML.highlightLine("package " + packageName) +
                "</span></tt>", 
                20, 4);
        
        buffer.append("<p>This package belongs to the unimported module <code> " + 
                mod.getName() + 
                "</code>, which will be automatically added to the descriptor of the current module.<p>");
        
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("jar_l_obj.gif").toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                HTML.highlightLine("import " + mod.getName() + 
                        " \"" + version + "\"") + 
                "</span></tt></b>",
                20, 4);
        
        if (mod.getDoc()!=null) {
            buffer.append(markdown(mod.getDoc(), scope, unit));
        }
                
        insertPageProlog(buffer, 0, HTML.getStyleSheet());
        addPageEpilog(buffer);
        return buffer.toString();
    }
    
    public static String getDocumentationForModule(String name, 
            String version, String doc, Scope scope, Unit unit) {
        StringBuilder buffer = new StringBuilder();
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("jar_l_obj.gif").toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                HTML.highlightLine(description(name, version)) + 
                "</span></tt></b>",
                20, 4);
        
        if (doc!=null) {
            buffer.append(markdown(doc, scope, unit));
        }
                
        insertPageProlog(buffer, 0, HTML.getStyleSheet());
        addPageEpilog(buffer);
        return buffer.toString();        
    }

    private static String description(String name, String version) {
        return "module " + name + " \"" + version + "\"";
    }

    public static String getDocumentationFor(CeylonParseController controller, 
            Module mod) {
        StringBuilder buffer = new StringBuilder();
        addMainModuleDescription(mod, buffer);
        addAdditionalModuleInfo(buffer, mod);
        addModuleDocumentation(controller, mod, buffer);
        addModuleMembers(buffer, mod);
        insertPageProlog(buffer, 0, HTML.getStyleSheet());
        addPageEpilog(buffer);
        return buffer.toString();
    }

    private static void addAdditionalModuleInfo(StringBuilder buffer, 
            Module mod) {
        if (mod.isJava()) {
            buffer.append("<p>This module is implemented in Java.</p>");
        }
        if (mod.isDefault()) {
            buffer.append("<p>The default module for packages which do not belong to explicit module.</p>");
        }
        if (JDKUtils.isJDKModule(mod.getNameAsString())) {
            buffer.append("<p>This module forms part of the Java SDK.</p>");            
        }
    }

    private static void addMainModuleDescription(Module mod,
            StringBuilder buffer) {
        HTML.addImageAndLabel(buffer, mod, 
                HTML.fileUrl(getIcon(mod)).toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                HTML.highlightLine(description(mod)) + 
                "</span></tt>", 
                20, 4);
    }

    private static void addModuleDocumentation(CeylonParseController cpc,
            Module mod, StringBuilder buffer) {
        Unit unit = mod.getUnit();
        PhasedUnit pu = null;
        if (unit instanceof CeylonUnit) {
            pu = ((CeylonUnit) unit).getPhasedUnit();
        }
        if (pu!=null) {
            List<Tree.ModuleDescriptor> moduleDescriptors = 
                    pu.getCompilationUnit().getModuleDescriptors();
            if (!moduleDescriptors.isEmpty()) {
                Tree.ModuleDescriptor refnode = 
                        moduleDescriptors.get(0);
                if (refnode!=null) {
                    Scope linkScope = 
                            mod.getPackage(mod.getNameAsString());
                    Tree.AnnotationList annotationList = 
                            refnode.getAnnotationList();
                    appendDocAnnotationContent(annotationList, 
                            buffer, linkScope);
                    appendThrowAnnotationContent(annotationList, 
                            buffer, linkScope);
                    appendSeeAnnotationContent(annotationList, 
                            buffer);
                }
            }
        }
    }

    private static void addPackageDocumentation(CeylonParseController cpc,
            Package pack, StringBuilder buffer) {
        Unit unit = pack.getUnit();
        PhasedUnit pu = null;
        if (unit instanceof CeylonUnit) {
            pu = ((CeylonUnit) unit).getPhasedUnit();
        }
        if (pu!=null) {
            List<Tree.PackageDescriptor> packageDescriptors = 
                    pu.getCompilationUnit().getPackageDescriptors();
            if (!packageDescriptors.isEmpty()) {
                Tree.PackageDescriptor refnode = 
                        packageDescriptors.get(0);
                if (refnode!=null) {
                    Tree.AnnotationList annotationList = 
                            refnode.getAnnotationList();
                    appendDocAnnotationContent(annotationList, 
                            buffer, pack);
                    appendThrowAnnotationContent(annotationList, 
                            buffer, pack);
                    appendSeeAnnotationContent(annotationList, 
                            buffer);
                }
            }
        }
    }

    private static void addModuleMembers(StringBuilder buffer, 
            Module mod) {
        boolean first = true;
        for (Package pack: mod.getPackages()) {
            if (pack.isShared()) {
                if (first) {
                    buffer.append("<p>Contains:&nbsp;&nbsp;");
                    first = false;
                }
                else {
                    buffer.append(", ");
                }

                /*addImageAndLabel(buffer, null, fileUrl(getIcon(dec)).toExternalForm(), 
                    16, 16, "<tt><a " + link(dec) + ">" + 
                    dec.getName() + "</a></tt>", 20, 2);*/
                appendLink(buffer, pack);
            }
        }
        if (!first) {
            buffer.append(".</p>");
        }
    }

    private static String description(Module mod) {
        return "module " + 
                mod.getNameAsString() + 
                " \"" + mod.getVersion() + "\"";
    }

    public static String getDocumentationFor(CeylonParseController controller, 
            Declaration dec) {
        return getDocumentationFor(controller, dec, null, null);
    }
    
    public static String getDocumentationFor(CeylonParseController controller, 
            Declaration dec, ProducedReference pr) {
        return getDocumentationFor(controller, dec, null, pr);
    }
    
    private static String getDocumentationFor(CeylonParseController controller, 
            Declaration dec, Node node, ProducedReference pr) {
        if (dec==null) return null;
        if (dec instanceof Value) {
            TypeDeclaration val = 
                    ((Value) dec).getTypeDeclaration();
            if (val!=null && val.isAnonymous()) {
                dec = val;
            }
        }
        Unit unit = controller==null ? 
                null : controller.getRootNode().getUnit();
        StringBuilder buffer = new StringBuilder();
        insertPageProlog(buffer, 0, HTML.getStyleSheet());
        addMainDescription(buffer, dec, node, pr, controller, unit);
        boolean obj = addInheritanceInfo(dec, node, pr, buffer, unit);
        addContainerInfo(dec, node, buffer); //TODO: use the pr to get the qualifying type??
        boolean hasDoc = addDoc(dec, node, buffer);
        addRefinementInfo(dec, node, buffer, hasDoc, unit); //TODO: use the pr to get the qualifying type??
        addReturnType(dec, buffer, node, pr, obj, unit);
        addParameters(controller, dec, node, pr, buffer, unit);
        addClassMembersInfo(dec, buffer);
        if (dec instanceof NothingType) {
            addNothingTypeInfo(buffer);
        }
        else {
            addUnitInfo(dec, buffer);
            addPackageInfo(dec, buffer);
            appendExtraActions(dec, buffer);
        }
        addPageEpilog(buffer);
        return buffer.toString();
    }

    private static void addMainDescription(StringBuilder buffer,
            Declaration dec, Node node, ProducedReference pr, 
            CeylonParseController cpc, Unit unit) {
        StringBuilder buf = new StringBuilder();
        if (dec.isShared()) buf.append("shared&nbsp;");
        if (dec.isActual()) buf.append("actual&nbsp;");
        if (dec.isDefault()) buf.append("default&nbsp;");
        if (dec.isFormal()) buf.append("formal&nbsp;");
        if (dec instanceof Value && ((Value) dec).isLate()) 
            buf.append("late&nbsp;");
        if (isVariable(dec)) buf.append("variable&nbsp;");
        if (dec.isNative()) buf.append("native&nbsp;");
        if (dec instanceof TypeDeclaration) {
            TypeDeclaration td = (TypeDeclaration) dec;
            if (td.isSealed()) buf.append("sealed&nbsp;");
            if (td.isFinal()) buf.append("final&nbsp;");
            if (td instanceof Class && 
                    ((Class) td).isAbstract()) 
                buf.append("abstract&nbsp;");
        }
        if (dec.isAnnotation()) buf.append("annotation&nbsp;");
        if (buf.length()!=0) {
            String ann = toHex(getCurrentThemeColor(ANNOTATIONS));
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("annotation_obj.gif").toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:" + annotationSize + ";color:" + ann + "'>" + 
                    buf + "</span></tt>", 
                    20, 4);
        }
        HTML.addImageAndLabel(buffer, dec, 
                HTML.fileUrl(getIcon(dec)).toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                (dec.isDeprecated() ? "<s>":"") + 
                description(dec, node, pr, cpc, unit) + 
                (dec.isDeprecated() ? "</s>":"") + 
                "</span></tt>", 
                20, 4);
    }

    private static void addClassMembersInfo(Declaration dec,
            StringBuilder buffer) {
        if (dec instanceof ClassOrInterface) {
            if (!dec.getMembers().isEmpty()) {
                boolean first = true;
                for (Declaration mem: dec.getMembers()) {
                    if (mem.getName()==null) {
                        continue;
                    }
                    if (mem instanceof Method && 
                            ((Method) mem).isOverloaded()) {
                        continue;
                    }
                    if (mem.isShared()) {
                        if (first) {
                            buffer.append("<p>Members:&nbsp;&nbsp;");
                            first = false;
                        }
                        else {
                            buffer.append(", ");
                        }

                        appendLink(buffer, mem);
                    }
                }
                if (!first) {
                    buffer.append(".</p>");
                    //extraBreak = true;
                }
            }
        }
    }

    private static void addNothingTypeInfo(StringBuilder buffer) {
        buffer.append("Special bottom type defined by the language. "
                + "<code>Nothing</code> is assignable to all types, but has no value. "
                + "A function or value of type <code>Nothing</code> either throws "
                + "an exception, or never returns.");
    }

    private static boolean addInheritanceInfo(Declaration dec,
            Node node, ProducedReference pr, StringBuilder buffer,
            Unit unit) {
        buffer.append("<p><div style='padding-left:20px'>");
        boolean obj=false;
        if (dec instanceof TypedDeclaration) {
            TypeDeclaration td = 
                    ((TypedDeclaration) dec).getTypeDeclaration();
            if (td!=null && td.isAnonymous()) {
                obj=true;
                documentInheritance(td, node, pr, buffer, unit);    
            }
        }
        else if (dec instanceof TypeDeclaration) {
            documentInheritance((TypeDeclaration) dec, node, 
                    pr, buffer, unit);    
        }
        buffer.append("</div></p>");
        documentTypeParameters(dec, node, pr, buffer, unit);
        buffer.append("</p>");
        return obj;
    }

    private static void addRefinementInfo(Declaration dec, 
            Node node, StringBuilder buffer, 
            boolean hasDoc, Unit unit) {
        Declaration rd = dec.getRefinedDeclaration();
        if (dec!=rd && rd!=null) {
            buffer.append("<p>");
            TypeDeclaration superclass = 
                    (TypeDeclaration) rd.getContainer();
            ClassOrInterface outer = 
                    (ClassOrInterface) dec.getContainer();
            ProducedType sup = 
                    getQualifyingType(node, outer)
                        .getSupertype(superclass);
            String icon = rd.isFormal() ? 
                    "implm_co.gif" : "over_co.gif";
            HTML.addImageAndLabel(buffer, rd, 
                    HTML.fileUrl(icon).toExternalForm(),
                    16, 16,
                    "refines&nbsp;&nbsp;" + link(rd) + 
                    "&nbsp;&nbsp;declared by&nbsp;&nbsp;<tt>" +
                    producedTypeLink(sup, unit) + "</tt>", 
                    20, 2);
            buffer.append("</p>");
            if (!hasDoc) {
                Tree.Declaration decNode = 
                        (Tree.Declaration) getReferencedNode(rd);
                if (decNode!=null) {
                    Tree.AnnotationList annotationList = 
                            decNode.getAnnotationList();
                    appendDocAnnotationContent(annotationList, 
                            buffer, resolveScope(rd));
                }
            }
        }
    }

    private static void appendParameters(Declaration dec, 
            ProducedReference pr, Unit unit, 
            StringBuilder result/*, CeylonParseController cpc*/) {
        if (dec instanceof Functional) {
            List<ParameterList> plists = 
                    ((Functional) dec).getParameterLists();
            if (plists!=null) {
                for (ParameterList params: plists) {
                    if (params.getParameters().isEmpty()) {
                        result.append("()");
                    }
                    else {
                        result.append("(");
                        for (Parameter p: params.getParameters()) {
                            appendParameter(result, pr, p, unit);
//                            if (cpc!=null) {
//                                result.append(getDefaultValueDescription(p, cpc));
//                            }
                            result.append(", ");
                        }
                        result.setLength(result.length()-2);
                        result.append(")");
                    }
                }
            }
        }
    }
    
    private static void appendParameter(StringBuilder result,
            ProducedReference pr, Parameter p, Unit unit) {
        result.append("<tt>");
        if (p.getModel() == null) {
            result.append(p.getName());
            result.append("</tt>");
        }
        else {
            ProducedTypedReference ppr = pr==null ? 
                    null : pr.getTypedParameter(p);
            if (p.isDeclaredVoid()) {
                result.append(HTML.keyword("void"));
            }
            else {
                if (ppr!=null) {
                    ProducedType pt = ppr.getType();
                    if (p.isSequenced() && pt!=null) {
                        pt = p.getDeclaration().getUnit()
                                .getSequentialElementType(pt);
                    }
                    result.append(producedTypeLink(pt, unit));
                    if (p.isSequenced()) {
                        result.append(p.isAtLeastOne()?'+':'*');
                    }
                }
                else if (p.getModel() instanceof Method) {
                    result.append(HTML.keyword("function"));
                }
                else {
                    result.append(HTML.keyword("value"));
                }
            }
            result.append("&nbsp;");
            result.append("</tt>");
            appendLink(result, p.getModel());
            appendParameters(p.getModel(), ppr, unit, result);
        }
    }
    
    private static void addParameters(CeylonParseController cpc,
            Declaration dec, Node node, ProducedReference pr, 
            StringBuilder buffer, Unit unit) {
        if (dec instanceof Functional) {
            if (pr==null) {
                pr = getProducedReference(dec, node);
            }
            if (pr==null) return;
            List<ParameterList> pls = 
                    ((Functional) dec).getParameterLists();
            for (ParameterList pl: pls) {
                if (!pl.getParameters().isEmpty()) {
                    buffer.append("<p>");
                    for (Parameter p: pl.getParameters()) {
                        MethodOrValue model = p.getModel();
                        if (model!=null) {
                            StringBuilder param = new StringBuilder();
                            param.append("accepts&nbsp;&nbsp;");
//                            param.append("<span style='font-size:" + smallerSize + "'>accepts&nbsp;&nbsp;");
                            appendParameter(param, pr, p, unit);
                            param.append("<tt>")
                                 .append(HTML.highlightLine(getInitialValueDescription(model, cpc)))
                                 .append("</tt>");
                            Tree.Declaration refNode = 
                                    (Tree.Declaration) getReferencedNode(model);
                            if (refNode!=null) {
                                Tree.AnnotationList annotationList = 
                                        refNode.getAnnotationList();
                                appendDocAnnotationContent(annotationList, 
                                        param, resolveScope(dec));
                            }
//                            param.append("</span>");
                            HTML.addImageAndLabel(buffer, model, 
                                    HTML.fileUrl("methpro_obj.gif").toExternalForm(),
                                    16, 16, param.toString(), 20, 2);
                        }
                    }
                    buffer.append("</p>");
                }
            }
        }
    }

    private static void addReturnType(Declaration dec, StringBuilder buffer,
            Node node, ProducedReference pr, boolean obj, Unit unit) {
        if (dec instanceof TypedDeclaration && !obj) {
            if (pr==null) {
                pr = getProducedReference(dec, node);
            }
            if (pr==null) return;
            ProducedType ret = pr.getType();
            if (ret!=null) {
                buffer.append("<p>");
                StringBuilder buf = 
                        new StringBuilder("returns&nbsp;&nbsp;<tt>");
                buf.append(producedTypeLink(ret, unit)).append("|");
                buf.setLength(buf.length()-1);
                buf.append("</tt>");
                HTML.addImageAndLabel(buffer, 
                        ret.getDeclaration(), 
                        HTML.fileUrl("stepreturn_co.gif").toExternalForm(), 
                        16, 16, buf.toString(), 20, 2);
                buffer.append("</p>");
            }
        }
    }

    private static ProducedTypeNamePrinter printer(boolean abbreviate) { 
        return new ProducedTypeNamePrinter(abbreviate, true, false, true, false) {
            @Override
            protected String getSimpleDeclarationName(Declaration declaration, Unit unit) {
                return "<a " + HTML.link(declaration) + ">" + 
                        super.getSimpleDeclarationName(declaration, unit) + 
                        "</a>";
            }
            @Override
            protected String amp() {
                return "&amp;";
            }
            @Override
            protected String lt() {
                return "&lt;";
            }
            @Override
            protected String gt() {
                return "&gt;";
            }
        };
    }
    
    private static ProducedTypeNamePrinter PRINTER = printer(true);
    private static ProducedTypeNamePrinter VERBOSE_PRINTER = printer(false);
    
    private static String producedTypeLink(ProducedType pt, Unit unit) {
        return PRINTER.getProducedTypeName(pt, unit);
    }

    private static List<ProducedType> getTypeParameters(Declaration dec) {
        if (dec instanceof Functional) {
            List<TypeParameter> typeParameters = 
                    ((Functional) dec).getTypeParameters();
            if (typeParameters.isEmpty()) {
                return Collections.<ProducedType>emptyList();
            }
            else {
                List<ProducedType> list = 
                        new ArrayList<ProducedType>();
                for (TypeParameter p: typeParameters) {
                    list.add(p.getType());
                }
                return list;
            }
        }
        else {
            return Collections.<ProducedType>emptyList();
        }
    }

    private static ProducedReference getProducedReference(Declaration dec,
            Node node) {
        if (node instanceof Tree.TypeDeclaration) {
            return ((TypeDeclaration) dec).getType();
        }
        else if (node instanceof Tree.MemberOrTypeExpression) {
            return ((Tree.MemberOrTypeExpression) node).getTarget();
        }
        else if (node instanceof Tree.Type) {
            return ((Tree.Type) node).getTypeModel();
        }
        else {
            //a member declaration - unfortunately there is 
            //nothing matching TypeDeclaration.getType() for
            //TypedDeclarations!
            ProducedType qt;
            if (dec.isClassOrInterfaceMember()) {
                ClassOrInterface ci = (ClassOrInterface) 
                        dec.getContainer();
                qt = ci.getType();
            }
            else {
                qt = null;
            }
            return dec.getProducedReference(qt,
                    getTypeParameters(dec));
        }
    }

    private static boolean addDoc(Declaration dec, 
            Node node, StringBuilder buffer) {
        boolean hasDoc = false;
        Node rn = getReferencedNode(dec);
        if (rn instanceof Tree.Declaration) {
            Tree.Declaration refnode = 
                    (Tree.Declaration) rn;
            Tree.AnnotationList annotationList = 
                    refnode.getAnnotationList();
            Scope scope = resolveScope(dec);
            appendDeprecatedAnnotationContent(annotationList, 
                    buffer, scope);
            int len = buffer.length();
            appendDocAnnotationContent(annotationList, 
                    buffer, scope);
            hasDoc = buffer.length()!=len;
            appendThrowAnnotationContent(annotationList, 
                    buffer, scope);
            appendSeeAnnotationContent(annotationList, 
                    buffer);
        }
        else {
            appendJavadoc(dec, buffer);
        }
        return hasDoc;
    }

    private static void addContainerInfo(Declaration dec, Node node,
            StringBuilder buffer) {
        Unit unit = node==null ? null : node.getUnit();
        buffer.append("<p>");
        if (dec.isParameter()) {
            MethodOrValue mv = (MethodOrValue) dec;
            Parameter ip = mv.getInitializerParameter();
            Declaration pd = ip.getDeclaration();
            if (pd.getName()==null) {
                if (pd instanceof Constructor) {
                    buffer.append("Parameter of default constructor of");
                    appendParameterLink(buffer, 
                            (Declaration) pd.getContainer());
                    buffer.append(".");
                }
            }
            else if (pd.getName().startsWith("anonymous#")) {
                buffer.append("Parameter of anonymous function.");
            }
            else {
                buffer.append("Parameter of");
                appendParameterLink(buffer, pd);
                buffer.append(".");
            }
//            HTML.addImageAndLabel(buffer, pd, 
//                    HTML.fileUrl(getIcon(pd)).toExternalForm(),
//                    16, 16, 
//                    "<span style='font-size:" + smallerSize + "'>parameter of&nbsp;&nbsp;<tt><a " + HTML.link(pd) + ">" + 
//                            pd.getName() +"</a></tt><span>", 20, 2);
        }
        else if (dec instanceof TypeParameter) {
            TypeParameter tp = (TypeParameter) dec;
            Declaration pd = tp.getDeclaration();
            buffer.append("Type parameter of");
            appendParameterLink(buffer, pd);
            buffer.append(".");
//            HTML.addImageAndLabel(buffer, pd, 
//                    HTML.fileUrl(getIcon(pd)).toExternalForm(),
//                    16, 16, 
//                    "<span style='font-size:" + smallerSize + "'>type parameter of&nbsp;&nbsp;<tt><a " + HTML.link(pd) + ">" + 
//                            pd.getName() +"</a></tt></span>", 
//                    20, 2);
        }
        else {
            if (dec.isClassOrInterfaceMember()) {
                ClassOrInterface outer = 
                        (ClassOrInterface) dec.getContainer();
                ProducedType qt = 
                        getQualifyingType(node, outer);
                if (qt!=null) {
                    String desc;
                    if (dec instanceof Constructor) {
                        if (dec.getName()==null) {
                            desc = "Default constructor of";
                        }
                        else {
                            desc = "Constructor of";
                        }
                    }
                    else if (dec instanceof Value) {
                        if (dec.isStaticallyImportable()) {
                            desc = "Static attribute of";
                        }
                        else {
                            desc = "Attribute of";
                        }
                    }
                    else if (dec instanceof Method) {
                        if (dec.isStaticallyImportable()) {
                            desc = "Static method of";
                        }
                        else {
                            desc = "Method of";
                        }
                    }
                    else {
                        if (dec.isStaticallyImportable()) {
                            desc = "Static member of";
                        }
                        else {
                            desc = "Member of";
                        }
                    }
                    String typeDesc;
                    if (qt.getDeclaration().getName().startsWith("anonymous#")) {
                        typeDesc = " anonymous class";
                    }
                    else {
                        typeDesc = "&nbsp;&nbsp;" + "<tt>" + 
                                producedTypeLink(qt, unit) + 
                                "</tt>";
                    }
                    buffer.append(desc + typeDesc + ".");
//                    HTML.addImageAndLabel(buffer, outer, 
//                            HTML.fileUrl(getIcon(outer)).toExternalForm(), 
//                            16, 16, 
//                            "<span style='font-size:" + smallerSize + "'>member of&nbsp;&nbsp;<tt>" + 
//                                producedTypeLink(qt, unit) + "</tt></span>", 
//                            20, 2);
                }
            }
            
        }
        buffer.append("</p>");
    }

    private static void appendParameterLink(StringBuilder buffer, 
            Declaration pd) {
        if (pd instanceof Class) {
            buffer.append(" class");
        }
        else if (pd instanceof Interface) {
            buffer.append(" interface");
        }
        else if (pd instanceof Method) {
            if (pd.isClassOrInterfaceMember()) {
                buffer.append(" method");
            }
            else {
                buffer.append(" function");
            }
        }
        else if (pd instanceof Constructor) {
            buffer.append(" constructor");
        }
        buffer.append("&nbsp;&nbsp;");
        if (pd.isClassOrInterfaceMember()) {
            appendLink(buffer, 
                    (Referenceable) pd.getContainer());
            buffer.append(".");
        }
        appendLink(buffer, pd);
    }
    
    private static void addPackageInfo(Declaration dec,
            StringBuilder buffer) {
        buffer.append("<p>");
        Package pack = dec.getUnit().getPackage();
        if ((dec.isShared() || dec.isToplevel()) &&
                !(dec instanceof NothingType)) {
            String label;
            if (pack.getNameAsString().isEmpty()) {
                label = "<span style='font-size:" + smallerSize + 
                        "'>in default package</span>";
            }
            else {
                label = "<span style='font-size:" + smallerSize + 
                        "'>in package&nbsp;&nbsp;" + 
                        link(pack) + "</span>";
            }
            HTML.addImageAndLabel(buffer, pack, 
                    HTML.fileUrl(getIcon(pack)).toExternalForm(), 
                    16, 16, label, 20, 2);
            addPackageModuleInfo(pack, buffer);
        }
        buffer.append("</p>");
    }
    
    private static ProducedType getQualifyingType(Node node,
            ClassOrInterface outer) {
        if (outer == null) {
            return null;
        }
        if (node instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression) node;
            ProducedReference pr = mte.getTarget();
            if (pr!=null) {
                return pr.getQualifyingType();
            }
        }
        if (node instanceof Tree.QualifiedType) {
            Tree.QualifiedType qt = 
                    (Tree.QualifiedType) node;
            return qt.getOuterType().getTypeModel();
        }
        return outer.getType();
    }

    private static void addUnitInfo(Declaration dec, 
            StringBuilder buffer) {
        buffer.append("<p>");
        String unitName = null;
        if (dec.getUnit() instanceof CeylonUnit) {
            // Manage the case of CeylonBinaryUnit : getFileName() would return the class file name.
            // but getCeylonFileName() will return the ceylon source file name if any.
            CeylonUnit ceylonUnit = 
                    (CeylonUnit) dec.getUnit();
            unitName = ceylonUnit.getCeylonFileName();
        }
        if (unitName == null) {
            unitName = dec.getUnit().getFilename();
        }
                
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("unit.gif").toExternalForm(), 
                16, 16, 
                "<span style='font-size:" + smallerSize + 
                "'>declared in&nbsp;&nbsp;<tt><a href='dec:" + 
                HTML.declink(dec) + "'>" + 
                unitName + "</a></tt></span>", 
                20, 2);
        //}
        buffer.append("</p>");
    }
    
    private static void appendExtraActions(Declaration dec, 
            StringBuilder buffer) {
        buffer.append("<p>");
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("search_ref_obj.png").toExternalForm(), 
                16, 16, 
                "<span style='font-size:" + 
                smallerSize + "'><a href='ref:" + 
                HTML.declink(dec) + 
                "'>find references</a> to&nbsp;&nbsp;<tt>" +
                dec.getName() + "</tt></span>",
                20, 2);
        if (dec instanceof ClassOrInterface) {
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("search_decl_obj.png").toExternalForm(), 
                    16, 16, 
                    "<span style='font-size:" + 
                    smallerSize + "'><a href='sub:" + 
                    HTML.declink(dec) + 
                    "'>find subtypes</a> of&nbsp;&nbsp;<tt>" +
                    dec.getName() + "</tt></span>",
                    20, 2);
        }
        if (dec instanceof MethodOrValue ||
            dec instanceof TypeParameter) {
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("search_ref_obj.png").toExternalForm(), 
                    16, 16, 
                    "<span style='font-size:" + 
                    smallerSize + "'><a href='ass:" + 
                    HTML.declink(dec) + 
                    "'>find assignments</a> to&nbsp;&nbsp;<tt>" +
                    dec.getName() + "</tt></span>", 
                    20, 2);
        }
        if (dec.isFormal() || dec.isDefault()) {
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("search_decl_obj.png").toExternalForm(), 
                    16, 16, 
                    "<span style='font-size:" + 
                    smallerSize + "'><a href='act:" + 
                    HTML.declink(dec) + 
                    "'>find refinements</a> of&nbsp;&nbsp;<tt>" +
                    dec.getName() + "</tt></span>", 
                    20, 2);
        }
        buffer.append("</p>");
    }

    private static void documentInheritance(TypeDeclaration dec, 
            Node node, ProducedReference pr, StringBuilder buffer,
            Unit unit) {
        if (pr==null) {
            pr = getProducedReference(dec, node);
        }
        ProducedType type;
        if (pr instanceof ProducedType) {
            type = (ProducedType) pr;
        }
        else {
            type = dec.getType();
        }
        List<ProducedType> cts = type.getCaseTypes();
        if (cts!=null) {
            StringBuilder cases = new StringBuilder();
            for (ProducedType ct: cts) {
                if (cases.length()>0) {
                    cases.append(" | ");
                }
                cases.append(producedTypeLink(ct, unit));
            }
            if (dec.getSelfType()!=null) {
                cases.append(" (self type)");
            }
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("sub.gif").toExternalForm(), 
                    16, 16,
                    " <tt><span style='font-size:" + 
                    smallerSize + "'>of&nbsp;" + 
                    cases +"</span></tt>", 
                    20, 2);
        }
        if (dec instanceof Class) {
            ProducedType sup = type.getExtendedType();
            if (sup!=null) {
                HTML.addImageAndLabel(buffer, sup.getDeclaration(), 
                        HTML.fileUrl("superclass.gif").toExternalForm(), 
                        16, 16, 
                        "<tt><span style='font-size:" + 
                        smallerSize + "'>extends&nbsp;" + 
                        producedTypeLink(sup, unit) +"</span></tt>", 
                        20, 2);
            }
        }
        List<ProducedType> sts = type.getSatisfiedTypes();
        if (!sts.isEmpty()) {
            StringBuilder satisfies = new StringBuilder();
            for (ProducedType st: sts) {
                if (satisfies.length()>0) {
                    satisfies.append(" &amp; ");
                }
                satisfies.append(producedTypeLink(st, unit));
            }
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("super.gif").toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:" + 
                    smallerSize + "'>satisfies&nbsp;" + 
                    satisfies + "</span></tt>",
                    20, 2);
        }
    }
    
    private static void documentTypeParameters(Declaration dec, 
            Node node, ProducedReference pr, StringBuilder buffer,
            Unit unit) {
        if (pr==null) {
            pr = getProducedReference(dec, node);
        }
        List<TypeParameter> typeParameters;
        if (dec instanceof Functional) {
            typeParameters = ((Functional) dec).getTypeParameters();
        }
        else if (dec instanceof Interface) {
            typeParameters = ((Interface) dec).getTypeParameters();
        }
        else {
            typeParameters = Collections.emptyList();
        }
        for (TypeParameter tp: typeParameters) {
            StringBuilder bounds = new StringBuilder();
            for (ProducedType st: tp.getSatisfiedTypes()) {
                if (bounds.length() == 0) {
                    bounds.append(" satisfies ");
                }
                else {
                    bounds.append(" &amp; ");
                }
                bounds.append(producedTypeLink(st, dec.getUnit()));
            }
            String arg= "";
            String liveValue = getLiveValue(tp, unit);
            if (liveValue!=null) {
                arg = liveValue;
            }
            else {
                ProducedType typeArg = pr==null ? 
                        null : pr.getTypeArguments().get(tp);
                if (typeArg!=null && 
                        !tp.getType().isExactly(typeArg)) {
                    arg = "&nbsp;=&nbsp;" + 
                        producedTypeLink(typeArg, unit);
                }
            }
            HTML.addImageAndLabel(buffer, tp, 
                    HTML.fileUrl(getIcon(tp)).toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:" + 
                    smallerSize + "'>given&nbsp;<a " + 
                    HTML.link(tp) + ">" + 
                    tp.getName() + "</a>" + bounds + arg + "</span></tt>", 
                    20, 4);
        }
    }

    private static String description(Declaration dec, Node node, 
            ProducedReference pr, CeylonParseController cpc, Unit unit) {
        if (pr==null) {
            pr = getProducedReference(dec, node);
        }
        StringBuffer description = 
                new StringBuffer(getDocDescriptionFor(dec, pr, unit));
        if (dec instanceof TypeDeclaration) {
            TypeDeclaration td = (TypeDeclaration) dec;
            if (td.isAlias() && td.getExtendedType()!=null) {
                description.append(" => ")
                    .append(td.getExtendedType().getProducedTypeName());
            }
        }
        if (dec instanceof Value && !isVariable(dec) ||
                dec instanceof Method) {
            description.append(getInitialValueDescription(dec, cpc));
        }
        
        String result = HTML.highlightLine(description.toString());
        String liveValue = getLiveValue(dec, unit);
        return liveValue==null ? result : result+liveValue;
    }

    private static String getLiveValue(Declaration dec, Unit unit) {
        if (dec instanceof TypeParameter && unit!=null) {
            TypeParameter typeParameter = (TypeParameter) dec;
            JDIStackFrame stackFrame = getFrame();
            if (stackFrame!=null) {
                try {
                    IJavaVariable typeDescriptor = 
                            jdiVariableForTypeParameter(stackFrame.getJavaDebugTarget(), 
                                    stackFrame, typeParameter);
                    if (typeDescriptor!=null) {
                        IJavaObject jdiProducedType = 
                                getJdiProducedType(typeDescriptor.getValue());
                        ProducedType producedType = 
                                toModelProducedType(jdiProducedType);
                        if (producedType != null) {
                            return new StringBuilder()
                                    .append(" <i>= ")
                                    .append(producedTypeLink(producedType, unit))
                                    .append("</i>").toString();
                        }
                    }
                }
                catch (DebugException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static void appendJavadoc(Declaration model,
            StringBuilder buffer) {
        try {
            appendJavadoc(getJavaElement(model), buffer);
        }
        catch (JavaModelException jme) {
            jme.printStackTrace();
        }
    }

    private static void appendDocAnnotationContent(Tree.AnnotationList annotationList,
            StringBuilder documentation, Scope linkScope) {
        if (annotationList!=null) {
            AnonymousAnnotation aa = 
                    annotationList.getAnonymousAnnotation();
            Unit unit = annotationList.getUnit();
            if (aa!=null) {
                String text = aa.getStringLiteral().getText();
                documentation.append(markdown(text, linkScope, unit));
//                HTML.addImageAndLabel(documentation, null, 
//                        HTML.fileUrl("toc_obj.gif").toExternalForm(), 
//                        16, 16,
//                        markdown(aa.getStringLiteral().getText(), 
//                                linkScope, annotationList.getUnit()), 
//                        20, 0);
            }
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) annotPrim;
                    String name = bme.getIdentifier().getText();
                    if ("doc".equals(name)) {
                        Tree.PositionalArgumentList argList = 
                                annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = 
                                    argList.getPositionalArguments();
                            if (!args.isEmpty()) {
                                Tree.PositionalArgument a = args.get(0);
                                if (a instanceof Tree.ListedArgument) {
                                    Tree.ListedArgument la = 
                                            (Tree.ListedArgument) a;
                                    String text = 
                                            la.getExpression()
                                                .getTerm().getText();
                                    if (text!=null) {
                                        documentation.append(markdown(text, 
                                                linkScope, unit));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void appendDeprecatedAnnotationContent(Tree.AnnotationList annotationList,
            StringBuilder documentation, Scope linkScope) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) annotPrim;
                    String name = bme.getIdentifier().getText();
                    if ("deprecated".equals(name)) {
                        Tree.PositionalArgumentList argList = 
                                annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = 
                                    argList.getPositionalArguments();
                            if (!args.isEmpty()) {
                                Tree.PositionalArgument a = args.get(0);
                                if (a instanceof Tree.ListedArgument) {
                                    Tree.ListedArgument la = 
                                            (Tree.ListedArgument) a;
                                    String text = 
                                            la.getExpression()
                                                .getTerm().getText();
                                    if (text!=null) {
                                        documentation.append(markdown(
                                                "_(This is a deprecated program element.)_\n\n" + text, 
                                                linkScope, annotationList.getUnit()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void appendSeeAnnotationContent(Tree.AnnotationList annotationList,
            StringBuilder documentation) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) annotPrim;
                    String name = bme.getIdentifier().getText();
                    if ("see".equals(name)) {
                        Tree.PositionalArgumentList argList = 
                                annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            StringBuilder sb = new StringBuilder();
                            List<Tree.PositionalArgument> args = 
                                    argList.getPositionalArguments();
                            for (Tree.PositionalArgument arg: args) {
                                if (arg instanceof Tree.ListedArgument) {
                                    Tree.ListedArgument la = 
                                            (Tree.ListedArgument) arg;
                                    Tree.Term term = la.getExpression().getTerm();
                                    if (term instanceof Tree.MetaLiteral) {
                                        Tree.MetaLiteral ml = 
                                                (Tree.MetaLiteral) term;
                                        Declaration dec = ml.getDeclaration();
                                        if (dec!=null) {
                                            String dn = dec.getName();
                                            if (dec.isClassOrInterfaceMember()) {
                                                ClassOrInterface container = 
                                                        (ClassOrInterface) dec.getContainer();
                                                dn = container.getName() + "." + dn;
                                            }
                                            if (sb.length()!=0) sb.append(", ");
                                            sb.append("<tt><a "+HTML.link(dec)+">"+dn+"</a></tt>");
                                        }
                                    }
                                }
                            }
                            if (sb.length()!=0) {
                                HTML.addImageAndLabel(documentation, null, 
                                        HTML.fileUrl("link_obj.gif"/*getIcon(dec)*/).toExternalForm(), 
                                        16, 16, 
                                        "see " + sb + ".", 
                                        20, 2);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void appendThrowAnnotationContent(Tree.AnnotationList annotationList,
            StringBuilder documentation, Scope linkScope) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) annotPrim;
                    String name = bme.getIdentifier().getText();
                    if ("throws".equals(name)) {
                        Tree.PositionalArgumentList argList = 
                                annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = 
                                    argList.getPositionalArguments();
                            if (args.isEmpty()) continue;
                            Tree.PositionalArgument typeArg = 
                                    args.get(0);
                            Tree.PositionalArgument textArg = 
                                    args.size()>1 ? args.get(1) : null;
                            if (typeArg instanceof Tree.ListedArgument && 
                                    (textArg==null || textArg instanceof Tree.ListedArgument)) {
                                Tree.ListedArgument typeListedArg = 
                                        (Tree.ListedArgument) typeArg;
                                Tree.ListedArgument textListedArg = 
                                        (Tree.ListedArgument) textArg;
                                Tree.Term typeArgTerm = 
                                        typeListedArg.getExpression().getTerm();
                                Tree.Term textArgTerm = textArg==null ? 
                                        null : textListedArg.getExpression().getTerm();
                                String text = 
                                        textArgTerm instanceof Tree.StringLiteral ?
                                                textArgTerm.getText() : "";
                                if (typeArgTerm instanceof Tree.MetaLiteral) {
                                    Tree.MetaLiteral ml = 
                                            (Tree.MetaLiteral) typeArgTerm;
                                    Declaration dec = ml.getDeclaration();
                                    if (dec!=null) {
                                        String dn = dec.getName();
                                        if (typeArgTerm instanceof Tree.QualifiedMemberOrTypeExpression) {
                                            Tree.QualifiedMemberOrTypeExpression qmte = 
                                                    (Tree.QualifiedMemberOrTypeExpression) typeArgTerm;
                                            Tree.Primary p = qmte.getPrimary();
                                            if (p instanceof Tree.MemberOrTypeExpression) {
                                                Tree.MemberOrTypeExpression mte = 
                                                        (Tree.MemberOrTypeExpression) p;
                                                dn = mte.getDeclaration().getName()
                                                        + "." + dn;
                                            }
                                        }
                                        HTML.addImageAndLabel(documentation, dec, 
                                                HTML.fileUrl("ihigh_obj.gif"/*getIcon(dec)*/).toExternalForm(), 
                                                16, 16, 
                                                "throws <tt><a "+HTML.link(dec)+">"+dn+"</a></tt>" + 
                                                        markdown(text, linkScope, 
                                                                annotationList.getUnit()), 
                                                20, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static String markdown(String text, final Scope linkScope, final Unit unit) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        Builder builder = Configuration.builder().forceExtentedProfile();
        builder.setCodeBlockEmitter(new CeylonBlockEmitter());
        if (linkScope!=null && unit!=null) {
            builder.setSpecialLinkEmitter(new CeylonSpanEmitter(linkScope, unit));
        }
        else {
            builder.setSpecialLinkEmitter(new UnlinkedSpanEmitter());
        }
        return Processor.process(text, builder.build());
    }
    
    private static Scope resolveScope(Declaration decl) {
        if (decl == null) {
            return null;
        }
        else if (decl instanceof Scope) {
            return (Scope) decl;
        }
        else {
            return decl.getContainer();
        }
    }
    
    static Module resolveModule(Scope scope) {
        if (scope == null) {
            return null;
        }
        else if (scope instanceof Package) {
            return ((Package) scope).getModule();
        }
        else {
            return resolveModule(scope.getContainer());
        }
    }
    
}
