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
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.addPageEpilog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.convertToHTMLContent;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.insertPageProlog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.toHex;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.ALTERNATE_ICONS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getTypeCheckers;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.getFrame;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.getJdiProducedType;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.producedTypeFromTypeDescriptor;
import static com.redhat.ceylon.eclipse.core.debug.DebugUtils.toModelProducedType;
import static com.redhat.ceylon.eclipse.core.debug.hover.CeylonDebugHover.jdiVariableForTypeParameter;
import static com.redhat.ceylon.eclipse.util.Highlights.ANNOTATIONS;
import static com.redhat.ceylon.eclipse.util.Highlights.ANNOTATION_STRINGS;
import static com.redhat.ceylon.eclipse.util.Highlights.CHARS;
import static com.redhat.ceylon.eclipse.util.Highlights.NUMBERS;
import static com.redhat.ceylon.eclipse.util.Highlights.STRINGS;
import static com.redhat.ceylon.eclipse.util.Highlights.getCurrentThemeColor;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isResolvable;
import static java.lang.Character.codePointCount;
import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
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
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.ISharedImages;

import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Configuration.Builder;
import com.github.rjeschke.txtmark.Processor;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.common.Backends;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnonymousAnnotation;
import com.redhat.ceylon.eclipse.code.browser.BrowserInformationControl;
import com.redhat.ceylon.eclipse.code.browser.BrowserInput;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.html.HTML;
import com.redhat.ceylon.eclipse.code.html.HTMLPrinter;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.UnlinkedSpanEmitter;
import com.redhat.ceylon.model.cmr.JDKUtils;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Constructor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Generic;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.NothingType;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeAlias;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedReference;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;
import com.redhat.ceylon.model.typechecker.util.TypePrinter;


public class DocumentationHover extends SourceInfoHover {
    
    public static final String smallerSize = "90%";
    public static final String annotationSize = "85%";
    public static final String largerSize = "103%";
    
    public DocumentationHover(CeylonEditor editor) {
        super(editor);
    }

    /**
     * Action to go back to the previous input in the hover control.
     */
    static final class BackAction extends Action {
        private final BrowserInformationControl fInfoControl;

        public BackAction(BrowserInformationControl infoControl) {
            fInfoControl = infoControl;
            setText("Back");
            ISharedImages images = getWorkbench().getSharedImages();
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
            ISharedImages images = getWorkbench().getSharedImages();
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
    
    static void close(BrowserInformationControl control) {
        control.notifyDelayedInputChange(null);
        control.dispose();
    }
    
    @Override
    public IInformationControlCreator getHoverControlCreator() {
        return new CeylonInformationControlCreator(editor, 
                "F2 for focus");
    }

    public static Referenceable getLinkedModel(String location, 
            CeylonEditor editor) {
        CeylonParseController controller = 
                editor.getParseController();
        if (location==null) {
            return null;
        }
        else if (location.matches("doc:ceylon.language/.*:ceylon.language:Nothing")) {
            Unit unit = controller.getLastCompilationUnit().getUnit();
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
    public CeylonBrowserInput getHoverInfo2(
            ITextViewer textViewer, IRegion hoverRegion) {
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

    static String getExpressionHoverText(
            CeylonEditor editor, IRegion hoverRegion) {
        CeylonParseController parseController = 
                editor.getParseController();
        if (parseController==null) {
            return null;
        }
        Tree.CompilationUnit rootNode = 
                parseController.getTypecheckedRootNode();
        if (rootNode!=null) {
            int hoffset = hoverRegion.getOffset();
            int hlength = hoverRegion.getLength();
            ITextSelection selection = 
                    editor.getSelectionFromThread();
            if (selection!=null) {
                int offset = selection.getOffset();
                int length = selection.getLength();
                if (offset<=hoffset && 
                    offset+length>=hoffset+hlength) {
                    Node node = 
                            findNode(rootNode, 
                                parseController.getTokens(), 
                                selection);
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
                        Tree.Expression expression = 
                                (Tree.Expression) node;
                        node = expression.getTerm();
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
        Node node = 
                getHoverNode(hoverRegion, 
                        editor.getParseController());
        return node==null ? null :
            getReferencedDeclaration(node);
    }
    
    static String getHoverText(CeylonEditor editor,
            IRegion hoverRegion) {
        CeylonParseController parseController = 
                editor.getParseController();

        Node node = 
                getHoverNode(hoverRegion, parseController);
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
                        document, project);
            }
            else {
                Referenceable model = 
                        getReferencedDeclaration(node);
                return getDocumentationHoverText(model, 
                        editor, node, null);
            }
        }
        else {
            return null;
        }
    }

    private static String getInferredTypeHoverText(Node node, 
            IProject project) {
        Tree.LocalModifier local = (Tree.LocalModifier) node;
        Type t = local.getTypeModel();
        if (t==null) return null;
        StringBuilder buffer = new StringBuilder();
        HTMLPrinter.insertPageProlog(buffer, 0, 
                HTML.getStyleSheet());
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("types.png").toExternalForm(), 
                16, 16, 
                "Inferred type&nbsp;<tt>" + 
                producedTypeLink(t, node.getUnit()) + "</tt>", 
                20, 4);
        buffer.append("<br/>");
        if (!t.containsUnknowns()) {
            buffer.append("One quick assist available:<br/>");
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("correction_change.png")
                        .toExternalForm(), 
                    16, 16, 
                    "<a href=\"stp:" + node.getStartIndex() + 
                    "\">Specify explicit type</a>", 
                    20, 4);
        }
        //buffer.append(getDocumentationFor(editor.getParseController(), t.getDeclaration()));
        HTMLPrinter.addPageEpilog(buffer);
        return buffer.toString();
    }
    
    private static String getTypeHoverText(
            Node node, String selectedText, 
            IDocument doc, IProject project) {
        Tree.Type type = (Tree.Type) node;
        Type t = type.getTypeModel();
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
                PRINTER.print(t,unit);
        String unabbreviated = 
                VERBOSE_PRINTER.print(t,unit);
        StringBuilder buffer = new StringBuilder();
        HTMLPrinter.insertPageProlog(buffer, 0, 
                HTML.getStyleSheet());
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("types.png").toExternalForm(), 
                16, 16, 
                "<tt>" + producedTypeLink(t,unit) + "</tt> ", 
                20, 4);
        if (!abbreviated.equals(unabbreviated)) {
            buffer.append("<br/>")
                  .append("Abbreviation&nbsp;of:&nbsp;")
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
    
    private static String getTermTypeHoverText(
            Node node, String selectedText, 
            IDocument doc, IProject project) {
        Tree.Term term = (Tree.Term) node;
        Type type = term.getTypeModel();
        if (type==null) return null;
        StringBuilder buffer = new StringBuilder();
        HTMLPrinter.insertPageProlog(buffer, 0, 
                HTML.getStyleSheet());
        String desc = 
                node instanceof Tree.Literal ? 
                        "Literal of type" : 
                        "Expression of type";
        Unit unit = node.getUnit();
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("types.png")
                    .toExternalForm(), 
                16, 16, 
                desc + 
                "&nbsp;<tt>" + 
                producedTypeLink(type, unit) + 
                "</tt> ",
                20, 4);
        String text = node.getText();
        if (node instanceof Tree.StringLiteral) {
            appendStringHoverInfo(buffer, text);
            // If a single char selection, then append info 
            // on that character too
            if (selectedText!=null) {
                int count = 
                        codePointCount(selectedText, 
                                0, selectedText.length());
                if (count == 1) {
                    appendCharacterHoverInfo(buffer, 
                            selectedText);
                }
            }
        }
        else if (node instanceof Tree.CharLiteral) {
            if (text.length()>2) {
                appendCharacterHoverInfo(buffer, 
                        text.substring(1, text.length()-1));
            }
        }
        else if (node instanceof Tree.NaturalLiteral) {
            appendIntegerHoverInfo(buffer, text);
        }
        else if (node instanceof Tree.FloatLiteral) {
            appendFloatHoverInfo(buffer, text);
        }
        HTMLPrinter.addPageEpilog(buffer);
        return buffer.toString();
    }

    private static void appendStringHoverInfo(
            StringBuilder buffer, String text) {
        String escaped = escape(text);
        if (escaped.length()>250) {
            escaped = escaped.substring(0,250) + "...";
        }
        String html = 
                convertToHTMLContent(escaped)
                    .replace("\\n", "<br/>");
        buffer.append( "<br/>")
            .append("<code style='color:")
            .append(toHex(getCurrentThemeColor(STRINGS)))
            .append("'><pre>")
            .append('\"')
            .append(html)
            .append('\"')
            .append("</pre></code>");
    }

    private static void appendFloatHoverInfo(
            StringBuilder buffer, String text) {
        text = text.replace("_", "");
        try {
            buffer.append("<br/>")
                .append("<code style='color:")
                .append(toHex(getCurrentThemeColor(NUMBERS)))
                .append("'>")
                .append(parseDouble(text))
                .append("</code>");
        }
        catch (NumberFormatException nfe) {}
    }

    private static void appendIntegerHoverInfo(
            StringBuilder buffer, String text) {
        text = text.replace("_", "");
        try {
            buffer.append("<br/>")
                .append("<code style='color:")
                .append(toHex(getCurrentThemeColor(NUMBERS)))
                .append("'>");
            switch (text.charAt(0)) {
            case '#':
                long hex = parseLong(text.substring(1), 16);
                buffer.append(hex);
                break;
            case '$':
                long bin = parseLong(text.substring(1), 2);
                buffer.append(bin);
                break;
            default:
                buffer.append(parseLong(text));
            }
            buffer.append("</code>");
        }
        catch (NumberFormatException nfe) {}
    }

    private static void appendCharacterHoverInfo(
            StringBuilder buffer, String character) {
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
        String hex = 
                Integer.toHexString(codepoint)
                    .toUpperCase();
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        String category = 
                getCodepointGeneralCategoryName(codepoint);
        String script = 
                Character.UnicodeScript.of(codepoint)
                    .name();
        String block = 
                Character.UnicodeBlock.of(codepoint)
                    .toString();
        buffer.append("<br/>Unicode Name: <code>")
            .append(name)
            .append("</code>");
        buffer.append("<br/>Codepoint: <code>")
            .append("U+")
            .append(hex)
            .append("</code>");
        buffer.append("<br/>General Category: <code>")
            .append(category)
            .append("</code>");
        buffer.append("<br/>Script: <code>")
            .append(script)
            .append("</code>");
        buffer.append("<br/>Block: <code>")
            .append(block)
            .append("</code><br/>");
    }

    private static String getCodepointGeneralCategoryName(
            int codepoint) {
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
            return "package_obj.png";
        }
        else if (obj instanceof Declaration) {
            Declaration dec = (Declaration) obj;
            if (CeylonPlugin.getPreferences().getBoolean(ALTERNATE_ICONS)) {
                if (dec instanceof Class) {
                    if (dec.isAnonymous()) {
                        return "anonymousClass.png";
                    }
                    return "class.png";
                }
                else if (dec instanceof Interface) {
                    return "interface.png"; 
                }
                else if (dec instanceof Constructor) {
                    return "classInitializer.png";
                }
                else if (dec.isParameter()) {
                    return "parameter.png";
                }
                else if (dec instanceof Value) {
                    return "field.png";
                }
                else if (dec instanceof Function) {
                    return dec.isShared() ?
                            "method.png" :
                            "function.png";
                }
                else if (dec instanceof TypeParameter) {
                    return "variable.png";
                }
            }
            if (dec instanceof Class) {
                String icon = dec.isShared() ? 
                        "class_obj.png" : 
                        "innerclass_private_obj.png";
                return decorateTypeIcon(dec, icon);
            }
            else if (dec instanceof Interface) {
                String icon = dec.isShared() ? 
                        "int_obj.png" : 
                        "innerinterface_private_obj.png";
                return decorateTypeIcon(dec, icon);
            }
            else if (dec instanceof Constructor) {
                String icon = dec.isShared() ? 
                        "constructor.png" : 
                        "constructor.png"; //TODO!!!!!!
                return icon;
//                return decorateTypeIcon(dec, icon);
            }
            else if (dec instanceof TypeAlias||
                    dec instanceof NothingType) {
                return "type_alias.gif";
            }
            else if (dec.isParameter()) {
                if (dec instanceof Function) {
                    return "methpro_obj.png";
                }
                else {
                    return "field_protected_obj.png";
                }
            }
            else if (dec instanceof Function) {
                String icon = dec.isShared() ?
                        "methpub_obj.png" : 
                        "methpri_obj.png";
                return decorateFunctionIcon(dec, icon);
            }
            else if (dec instanceof FunctionOrValue) {
                return dec.isShared() ?
                        "field_public_obj.png" : 
                        "field_private_obj.png";
            }
            else if (dec instanceof TypeParameter) {
                return "typevariable_obj.png";
            }
        }
        return null;
    }

    private static String decorateFunctionIcon(
            Declaration dec, String icon) {
        if (dec.isAnnotation()) {
            return icon.replace("obj", "ann")
                    .replace("png", "gif");
        }
        else {
            return icon;
        }
    }

    private static String decorateTypeIcon(
            Declaration dec, String icon) {
        TypeDeclaration td = (TypeDeclaration) dec;
        if (td.getCaseTypes()!=null) {
            return icon.replace("obj", "enum")
                    .replace("png", "gif");
        }
        else if (dec.isAnnotation()) {
            return icon.replace("obj", "ann")
                    .replace("png", "gif");
        }
        else if (td.isAlias()) {
            return icon.replace("obj", "alias")
                    .replace("png", "gif");
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
    public static String getDocumentationHoverText(
            Referenceable model, CeylonEditor editor, 
            Node node, IProgressMonitor monitor) {
        CeylonParseController parseController = 
                editor.getParseController();
        if (model instanceof Declaration) {
            Declaration dec = (Declaration) model;
            return getDocumentationFor(parseController, dec, 
                    node, null, null);
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

    private static void appendJavadoc(IJavaElement elem, 
            StringBuilder sb) {
        if (elem instanceof IMember) {
            try {
                //TODO: Javadoc @ icon?
                IMember mem = (IMember) elem;
//                String jd = JavadocContentAccess2.getHTMLContent(mem, true);
                String javadocText = getHtmlContent(mem);
                if (javadocText!=null) {
                    sb.append("<br/>").append(javadocText);
                    String base = 
                            getBaseURL(mem, mem.isBinary());
                    int endHeadIdx= sb.indexOf("</head>");
                    sb.insert(endHeadIdx, 
                            "\n<base href='" + base + "'>\n");
                }
            } 
            catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getHtmlContent(IMember mem) {
        for (java.lang.reflect.Method m: 
                JavadocContentAccess2.class
                    .getDeclaredMethods()) {
        	if (m.getName().equals("getHTMLContent")) {
        		try {
            		Object[] args = { mem, true };
                    return (String) m.invoke(null, args);
        		}
        		catch (Exception e) {}
        	}
        }
        return null;
    }

    private static String getBaseURL(
            IJavaElement element, boolean isBinary) 
                    throws JavaModelException {
        if (isBinary) {
            // Source attachment usually does not include Javadoc resources
            // => Always use the Javadoc location as base:
            URL baseURL = 
                    JavaUI.getJavadocLocation(element, false);
            if (baseURL != null) {
                String urlString = baseURL.toExternalForm();
                if (baseURL.getProtocol().equals("jar")) {
                    // It's a JarURLConnection, which is not known to the browser widget.
                    // Let's start the help web server:
                    URL baseURL2 = 
                            getWorkbench()
                                .getHelpSystem()
                                .resolve(urlString, true);
                    if (baseURL2 != null) { // can be null if org.eclipse.help.ui is not available
                        baseURL = baseURL2;
                    }
                }
                return urlString;
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

    public static String getDocumentationFor(
            CeylonParseController controller, Package pack) {
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

    private static void addPackageMembers(
            StringBuilder buffer, Package pack) {
        boolean first = true;
        for (Declaration dec: pack.getMembers()) {
            if (dec.getName()==null) {
                continue;
            }
            if (dec instanceof Class) {
                Class c = (Class) dec;
                if (c.isOverloaded()) {
                    continue;
                }
            }
            if (dec.isShared() && !dec.isAnonymous()) {
                if (first) {
                    buffer.append("<p>Contains:&nbsp;");
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

    private static void appendLink(
            StringBuilder buffer, Referenceable model) {
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

    private static void addAdditionalPackageInfo(
            StringBuilder buffer, Package pack) {
        Module mod = pack.getModule();
        if (mod.isJava()) {
            buffer.append("<p>This package is implemented in Java.</p>");
        }
        if (JDKUtils.isJDKModule(mod.getNameAsString())) {
            buffer.append("<p>This package forms part of the Java SDK.</p>");            
        }
    }

    private static void addMainPackageDescription(
            Package pack, StringBuilder buffer) {
        if (pack.isShared()) {
            String ann = toHex(getCurrentThemeColor(ANNOTATIONS));
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("annotation_obj.gif")
                        .toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:" + annotationSize + 
                    ";color:" + ann + "'>shared</span></tt>"
                    , 20, 4);
        }
        HTML.addImageAndLabel(buffer, pack, 
                HTML.fileUrl(getIcon(pack))
                    .toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                HTML.highlightLine(description(pack)) +
                "</span></tt>", 
                20, 4);
    }

    private static void addPackageModuleInfo(
            Package pack, StringBuilder buffer) {
        Module mod = pack.getModule();
        String label;
        boolean defaultPackage =
                mod.getNameAsString().isEmpty() || 
                mod.getNameAsString().equals("default");
        if (defaultPackage) {
            label = "<span>Belongs to default module.</span>";
        }
        else {
            label = "<span>Belongs to&nbsp;" + 
                    link(mod) + 
                    "&nbsp;<tt><span style='color:" + 
                    toHex(getCurrentThemeColor(STRINGS)) + 
                    "'>\"" + 
                    mod.getVersion() + 
                    "\"</span></tt>" + ".</span>";
        }
        HTML.addImageAndLabel(buffer, mod, 
                HTML.fileUrl(getIcon(mod))
                    .toExternalForm(), 
                16, 16, label, 20, 2);
    }
    
    private static String description(Package pack) {
        return "package " + pack.getNameAsString();
    }
    
    public static String getDocumentationFor(ModuleDetails mod, 
            String version, Scope scope, Unit unit) {
        return getDocumentationForModule(mod.getName(), 
                version, mod.getDoc(), scope, unit);
    }
    
    public static String getDocumentationFor(ModuleDetails mod, 
            String version, String packageName, Scope scope, 
            Unit unit) {
        StringBuilder buffer = new StringBuilder();
        String ann = toHex(getCurrentThemeColor(ANNOTATIONS));
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("annotation_obj.gif")
                    .toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + annotationSize + 
                ";color:" + ann + "'>shared</span></tt>", 
                20, 4);
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("package_obj.png")
                    .toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                HTML.highlightLine("package " + packageName) +
                "</span></tt>", 
                20, 4);
        
        buffer.append("<p>This package belongs to the unimported module <code> ")
            .append(mod.getName()) 
            .append("</code>, which will be automatically added to the descriptor of the current module.<p>");

        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("jar_l_obj.gif")
                    .toExternalForm(), 
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
                HTML.fileUrl("jar_l_obj.gif")
                    .toExternalForm(), 
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

    public static String getDocumentationFor(
            CeylonParseController controller, Module mod) {
        StringBuilder buffer = new StringBuilder();
        addMainModuleDescription(mod, buffer);
        addAdditionalModuleInfo(buffer, mod);
        addModuleDocumentation(controller, mod, buffer);
        addModuleMembers(buffer, mod);
        insertPageProlog(buffer, 0, HTML.getStyleSheet());
        addPageEpilog(buffer);
        return buffer.toString();
    }

    private static void addAdditionalModuleInfo(
            StringBuilder buffer, Module mod) {
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
                HTML.fileUrl(getIcon(mod))
                    .toExternalForm(), 
                16, 16, 
                "<tt><span style='font-size:" + largerSize + "'>" + 
                HTML.highlightLine(description(mod)) + 
                "</span></tt>", 
                20, 4);
    }

    private static void addModuleDocumentation(
            CeylonParseController cpc, Module mod, 
            StringBuilder buffer) {
        Unit unit = mod.getUnit();
        PhasedUnit pu = null;
        if (unit instanceof CeylonUnit) {
            pu = ((CeylonUnit) unit).getPhasedUnit();
        }
        if (pu!=null) {
            List<Tree.ModuleDescriptor> moduleDescriptors = 
                    pu.getCompilationUnit()
                        .getModuleDescriptors();
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

    private static void addPackageDocumentation(
            CeylonParseController cpc, Package pack, 
            StringBuilder buffer) {
        Unit unit = pack.getUnit();
        PhasedUnit pu = null;
        if (unit instanceof CeylonUnit) {
            pu = ((CeylonUnit) unit).getPhasedUnit();
        }
        if (pu!=null) {
            List<Tree.PackageDescriptor> packageDescriptors = 
                    pu.getCompilationUnit()
                        .getPackageDescriptors();
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

    private static void addModuleMembers(
            StringBuilder buffer, Module mod) {
        boolean first = true;
        for (Package pack: mod.getPackages()) {
            if (pack.isShared()) {
                if (first) {
                    buffer.append("<p>Contains:&nbsp;");
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

    public static String getDocumentationFor(
            CeylonParseController controller, 
            Declaration dec,
            IProgressMonitor monitor) {
        return getDocumentationFor(controller, dec, null, null, monitor);
    }
    
    public static String getDocumentationFor(
            CeylonParseController controller, 
            Declaration dec, Reference pr, IProgressMonitor monitor) {
        return getDocumentationFor(controller, dec, null, pr, monitor);
    }
    
    private static String getDocumentationFor(
            CeylonParseController controller, 
            Declaration dec, Node node, Reference pr, IProgressMonitor monitor) {
        if (dec==null) return null;
        if (dec instanceof FunctionOrValue) {
            FunctionOrValue value = (FunctionOrValue) dec;
            TypeDeclaration valueType = 
                    value.getTypeDeclaration();
            if (valueType!=null && 
                    valueType.isAnonymous() &&
                    !value.getType().isTypeConstructor()) {
                dec = valueType;
            }
        }
        Unit unit = controller==null ? null : 
            controller.getLastCompilationUnit().getUnit();
        StringBuilder buffer = new StringBuilder();
        insertPageProlog(buffer, 0, HTML.getStyleSheet());
        addMainDescription(buffer, dec, node, pr, controller, unit);
        boolean obj = addInheritanceInfo(dec, node, pr, buffer, unit);
        addContainerInfo(dec, node, buffer); //TODO: use the pr to get the qualifying type??
        if (!(dec instanceof NothingType)) {
            addPackageInfo(dec, buffer);
        }
        boolean hasDoc = addDoc(dec, node, buffer, monitor);
        addRefinementInfo(dec, node, buffer, hasDoc, unit); //TODO: use the pr to get the qualifying type??
        addReturnType(dec, buffer, node, pr, obj, unit);
        addParameters(controller, dec, node, pr, buffer, unit);
        addClassMembersInfo(dec, buffer);
        if (dec instanceof NothingType) {
            addNothingTypeInfo(buffer);
        }
        else {
            addUnitInfo(dec, buffer);
        }
        addPageEpilog(buffer);
        return buffer.toString();
    }

    private static void addMainDescription(StringBuilder buffer,
            Declaration dec, Node node, Reference pr, 
            CeylonParseController cpc, Unit unit) {
        StringBuilder buf = new StringBuilder();
        if (dec.isShared()) buf.append("shared&nbsp;");
        if (dec.isActual()) buf.append("actual&nbsp;");
        if (dec.isDefault()) buf.append("default&nbsp;");
        if (dec.isFormal()) buf.append("formal&nbsp;");
        if (dec instanceof Value && ((Value) dec).isLate()) {
            buf.append("late&nbsp;");
        }
        if (isVariable(dec)) buf.append("variable&nbsp;");
        if (dec.isNative()) buf.append("native");
        Backends nativeBackends = dec.getNativeBackends();
        if (!nativeBackends.none() && !Backends.HEADER.equals(nativeBackends)) {
            String color = 
                    toHex(getCurrentThemeColor(ANNOTATION_STRINGS));
            buf.append("(<span style='color:")
                .append(color)
                .append("'>\"")
                .append(nativeBackends.toString())
                .append("\"</span>)");
        }
        if (dec.isNative()) buf.append("&nbsp;");
        if (dec instanceof TypeDeclaration) {
            TypeDeclaration td = (TypeDeclaration) dec;
            if (td.isSealed()) buf.append("sealed&nbsp;");
            if (td.isFinal() && !(td instanceof Constructor)) {
                buf.append("final&nbsp;");
            }
            if (td instanceof Class) {
                Class c = (Class) td;
                if (c.isAbstract()) {
                    buf.append("abstract&nbsp;");
                }
            }
        }
        if (dec.isAnnotation()) buf.append("annotation&nbsp;");
        if (buf.length()!=0) {
            String color = 
                    toHex(getCurrentThemeColor(ANNOTATIONS));
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("annotation_obj.gif")
                        .toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:" + 
                    annotationSize + ";color:" + color + "'>" + 
                    buf + "</span></tt>", 
                    20, 4);
        }
        HTML.addImageAndLabel(buffer, dec, 
                HTML.fileUrl(getIcon(dec))
                    .toExternalForm(), 
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
            boolean first = true;
            for (Declaration mem: dec.getMembers()) {
                if (isResolvable(mem) && mem.isShared() &&
                        (!mem.isOverloaded() ||
                          mem.isAbstraction())) {
                    if (first) {
                        buffer.append("<p>Members:&nbsp;");
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
            Node node, Reference pr, StringBuilder buffer,
            Unit unit) {
        buffer.append("<p><div style='padding-left:20px'>");
        boolean obj=false;
        if (dec instanceof TypedDeclaration) {
            TypedDeclaration d = (TypedDeclaration) dec;
            TypeDeclaration td = d.getTypeDeclaration();
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
            Type sup = 
                    getQualifyingType(node, outer)
                        .getSupertype(superclass);
            String icon = rd.isFormal() ? 
                    "implm_co.png" : "over_co.png";
            HTML.addImageAndLabel(buffer, rd, 
                    HTML.fileUrl(icon).toExternalForm(),
                    16, 16,
                    "Refines&nbsp;" + link(rd) + 
                    "&nbsp;declared by&nbsp;<tt>" +
                    producedTypeLink(sup, unit) + "</tt>.", 
                    20, 2);
            buffer.append("</p>");
            if (!hasDoc) {
                Tree.Declaration decNode = 
                        (Tree.Declaration) 
                            getReferencedNode(rd);
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
            Reference pr, Unit unit, 
            StringBuilder result/*, CeylonParseController cpc*/) {
        if (dec instanceof Functional) {
            Functional fun = (Functional) dec;
            List<ParameterList> plists = 
                    fun.getParameterLists();
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
            Reference pr, Parameter p, Unit unit) {
        result.append("<tt>");
        if (p.getModel() == null) {
            result.append(p.getName());
            result.append("</tt>");
        }
        else {
            TypedReference ppr = pr==null ? null : 
                pr.getTypedParameter(p);
            if (p.isDeclaredVoid()) {
                result.append(HTML.keyword("void"));
            }
            else {
                if (ppr!=null) {
                    Type pt = ppr.getType();
                    if (p.isSequenced() && pt!=null) {
                        pt = p.getDeclaration().getUnit()
                                .getSequentialElementType(pt);
                    }
                    result.append(producedTypeLink(pt, unit));
                    if (p.isSequenced()) {
                        result.append(p.isAtLeastOne()?'+':'*');
                    }
                }
                else if (p.getModel() instanceof Function) {
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
            Declaration dec, Node node, Reference pr, 
            StringBuilder buffer, Unit unit) {
        if (dec instanceof Functional) {
            if (pr==null) {
                pr = appliedReference(dec, node);
            }
            if (pr==null) return;
            Functional fun = (Functional) dec;
            List<ParameterList> pls = 
                    fun.getParameterLists();
            for (ParameterList pl: pls) {
                if (!pl.getParameters().isEmpty()) {
                    buffer.append("<p>");
                    for (Parameter p: pl.getParameters()) {
                        FunctionOrValue model = p.getModel();
                        if (model!=null) {
                            StringBuilder param = 
                                    new StringBuilder();
                            param.append("Accepts&nbsp;");
//                            param.append("<span style='font-size:" + smallerSize + "'>accepts&nbsp;");
                            appendParameter(param, pr, p, unit);
                            String init = 
                                    getInitialValueDescription(
                                            model, cpc);
                            param.append("<tt>")
                                 .append(HTML.highlightLine(init))
                                 .append("</tt>")
                                 .append(".");
                            Tree.Declaration refNode = 
                                    (Tree.Declaration) 
                                        getReferencedNode(model);
                            if (refNode!=null) {
                                Tree.AnnotationList annotationList = 
                                        refNode.getAnnotationList();
                                appendDocAnnotationContent(annotationList, 
                                        param, resolveScope(dec));
                            }
//                            param.append("</span>");
                            HTML.addImageAndLabel(buffer, model, 
                                    HTML.fileUrl("methpro_obj.png")
                                        .toExternalForm(),
                                    16, 16, param.toString(), 20, 2);
                        }
                    }
                    buffer.append("</p>");
                }
            }
        }
    }

    private static void addReturnType(
            Declaration dec, StringBuilder buffer,
            Node node, Reference pr, boolean obj, 
            Unit unit) {
        if (dec instanceof TypedDeclaration && !obj) {
            if (pr==null) {
                pr = appliedReference(dec, node);
            }
            if (pr==null) return;
            Type ret = pr.getType();
            if (ret!=null) {
                buffer.append("<p>");
                StringBuilder buf = 
                        new StringBuilder("Returns&nbsp;<tt>");
                buf.append(producedTypeLink(ret, unit))
                    .append("|");
                buf.setLength(buf.length()-1);
                buf.append("</tt>.");
                HTML.addImageAndLabel(buffer, 
                        ret.getDeclaration(), 
                        HTML.fileUrl("stepreturn_co.png")
                            .toExternalForm(), 
                        16, 16, buf.toString(), 20, 2);
                buffer.append("</p>");
            }
        }
    }

    private static TypePrinter printer(boolean abbreviate) { 
        return new TypePrinter(abbreviate, true, false, true, false) {
            @Override
            protected String getSimpleDeclarationName(
                    Declaration declaration, Unit unit) {
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
    
    private static TypePrinter PRINTER = printer(true);
    private static TypePrinter VERBOSE_PRINTER = printer(false);
    
    private static String producedTypeLink(Type pt, Unit unit) {
        return PRINTER.print(pt, unit);
    }

    private static List<Type> getTypeParameters(Declaration dec) {
        if (dec instanceof Generic) {
            List<TypeParameter> typeParameters = 
                    ((Generic) dec).getTypeParameters();
            if (typeParameters.isEmpty()) {
                return Collections.<Type>emptyList();
            }
            else {
                List<Type> list = 
                        new ArrayList<Type>();
                for (TypeParameter p: typeParameters) {
                    list.add(p.getType());
                }
                return list;
            }
        }
        else {
            return Collections.<Type>emptyList();
        }
    }

    private static Reference appliedReference(Declaration dec,
            Node node) {
        if (node instanceof Tree.TypeDeclaration) {
            TypeDeclaration td = (TypeDeclaration) dec;
            return td.getType();
        }
        else if (node instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression) node;
            return mte.getTarget();
        }
        else if (node instanceof Tree.Type) {
            Tree.Type t = (Tree.Type) node;
            return t.getTypeModel();
        }
        else {
            //a member declaration - unfortunately there is 
            //nothing matching TypeDeclaration.getType() for
            //TypedDeclarations!
            Type qt;
            if (dec.isClassOrInterfaceMember()) {
                ClassOrInterface ci = 
                        (ClassOrInterface) 
                            dec.getContainer();
                qt = ci.getType();
            }
            else {
                qt = null;
            }
            return dec.appliedReference(qt,
                    getTypeParameters(dec));
        }
    }

    private static boolean addDoc(Declaration dec, 
            Node node, StringBuilder buffer, IProgressMonitor monitor) {
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
            appendJavadoc(dec, buffer, monitor);
        }
        return hasDoc;
    }

    private static void addContainerInfo(
            Declaration dec, Node node,
            StringBuilder buffer) {
        Unit unit = node==null ? null : node.getUnit();
        buffer.append("<p>");
        if (dec.isParameter()) {
            FunctionOrValue mv = (FunctionOrValue) dec;
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
//                    "<span style='font-size:" + smallerSize + "'>parameter of&nbsp;<tt><a " + HTML.link(pd) + ">" + 
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
//                    "<span style='font-size:" + smallerSize + "'>type parameter of&nbsp;<tt><a " + HTML.link(pd) + ">" + 
//                            pd.getName() +"</a></tt></span>", 
//                    20, 2);
        }
        else {
            if (dec.isClassOrInterfaceMember()) {
                ClassOrInterface outer = 
                        (ClassOrInterface) 
                            dec.getContainer();
                Type qt = getQualifyingType(node, outer);
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
                    else if (dec instanceof Function) {
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
                    if (qt.getDeclaration().getName()
                            .startsWith("anonymous#")) {
                        typeDesc = " anonymous class";
                    }
                    else {
                        typeDesc = "&nbsp;" + "<tt>" + 
                                producedTypeLink(qt, unit) + 
                                "</tt>";
                    }
                    buffer.append(desc + typeDesc + ".");
//                    HTML.addImageAndLabel(buffer, outer, 
//                            HTML.fileUrl(getIcon(outer)).toExternalForm(), 
//                            16, 16, 
//                            "<span style='font-size:" + smallerSize + "'>member of&nbsp;<tt>" + 
//                                producedTypeLink(qt, unit) + "</tt></span>", 
//                            20, 2);
                }
            }
            
        }
        buffer.append("</p>");
    }

    private static void appendParameterLink(
            StringBuilder buffer, Declaration pd) {
        if (pd instanceof Class) {
            buffer.append(" class");
        }
        else if (pd instanceof Interface) {
            buffer.append(" interface");
        }
        else if (pd instanceof Function) {
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
        buffer.append("&nbsp;");
        if (pd.isClassOrInterfaceMember()) {
            appendLink(buffer, 
                    (Referenceable) pd.getContainer());
            buffer.append(".");
        }
        appendLink(buffer, pd);
    }
    
    private static void addPackageInfo(Declaration dec,
            StringBuilder buffer) {
        Package pack = dec.getUnit().getPackage();
        if ((/*dec.isShared() ||*/ dec.isToplevel()) &&
                !(dec instanceof NothingType)) {
            String label;
            if (pack.getNameAsString().isEmpty()) {
                label = "<span>Member of default package.</span>";
            }
            else {
                label = "<span>Member of package&nbsp;" + 
                        link(pack) + ".</span>";
            }
            HTML.addImageAndLabel(buffer, pack, 
                    HTML.fileUrl(getIcon(pack))
                        .toExternalForm(), 
                    16, 16, label, 20, 2);
        }
    }
    
    private static Type getQualifyingType(
            Node node, ClassOrInterface outer) {
        if (outer == null) {
            return null;
        }
        if (node instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression) node;
            Reference pr = mte.getTarget();
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

    private static void addUnitInfo(
            Declaration dec, StringBuilder buffer) {
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
                "<span>Declared in&nbsp;<tt><a href='dec:" + 
                HTML.declink(dec) + "'>" + 
                unitName + "</a></tt>.</span>", 
                20, 2);
        addPackageModuleInfo(dec.getUnit().getPackage(), buffer);
        //}
        buffer.append("</p>");
    }
    
    private static void documentInheritance(TypeDeclaration dec, 
            Node node, Reference pr, StringBuilder buffer,
            Unit unit) {
        if (pr==null) {
            pr = appliedReference(dec, node);
        }
        Type type;
        if (pr instanceof Type) {
            type = (Type) pr;
        }
        else {
            type = dec.getType();
        }
        List<Type> cts = type.getCaseTypes();
        if (cts!=null) {
            StringBuilder cases = new StringBuilder();
            for (Type ct: cts) {
                if (cases.length()>0) {
                    cases.append(" | ");
                }
                cases.append(producedTypeLink(ct, unit));
            }
            if (dec.getSelfType()!=null) {
                cases.append(" (self type)");
            }
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("sub.png").toExternalForm(), 
                    16, 16,
                    " <tt><span style='font-size:" + 
                    smallerSize + "'>of&nbsp;" + 
                    cases +"</span></tt>", 
                    20, 2);
        }
        if (dec instanceof Class) {
            Type sup = type.getExtendedType();
            if (sup!=null) {
                HTML.addImageAndLabel(buffer, sup.getDeclaration(), 
                        HTML.fileUrl("superclass.png").toExternalForm(), 
                        16, 16, 
                        "<tt><span style='font-size:" + 
                        smallerSize + "'>extends&nbsp;" + 
                        producedTypeLink(sup, unit) +"</span></tt>", 
                        20, 2);
            }
        }
        List<Type> sts = type.getSatisfiedTypes();
        if (!sts.isEmpty()) {
            StringBuilder satisfies = new StringBuilder();
            for (Type st: sts) {
                if (satisfies.length()>0) {
                    satisfies.append(" &amp; ");
                }
                satisfies.append(producedTypeLink(st, unit));
            }
            HTML.addImageAndLabel(buffer, null, 
                    HTML.fileUrl("super.png").toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:" + 
                    smallerSize + "'>satisfies&nbsp;" + 
                    satisfies + "</span></tt>",
                    20, 2);
        }
    }
    
    private static void documentTypeParameters(Declaration dec, 
            Node node, Reference pr, StringBuilder buffer,
            Unit unit) {
        if (pr==null) {
            pr = appliedReference(dec, node);
        }
        List<TypeParameter> typeParameters;
        if (dec instanceof Generic) {
            Generic g = (Generic) dec;
            typeParameters = g.getTypeParameters();
        }
        else {
            typeParameters = Collections.emptyList();
        }
        for (TypeParameter tp: typeParameters) {
            StringBuilder bounds = new StringBuilder();
            for (Type st: tp.getSatisfiedTypes()) {
                if (bounds.length() == 0) {
                    bounds.append(" satisfies ");
                }
                else {
                    bounds.append(" &amp; ");
                }
                Unit du = dec.getUnit();
                bounds.append(producedTypeLink(st, du));
            }
            String arg= "";
            String liveValue = getLiveValue(tp, unit);
            if (liveValue!=null) {
                arg = liveValue;
            }
            else {
                Type typeArg = pr==null ? null : 
                    pr.getTypeArguments().get(tp);
                if (typeArg!=null && 
                        !tp.getType().isExactly(typeArg)) {
                    arg = "&nbsp;=&nbsp;" + 
                        producedTypeLink(typeArg, unit);
                }
            }
            HTML.addImageAndLabel(buffer, tp, 
                    HTML.fileUrl(getIcon(tp))
                        .toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:" + 
                    smallerSize + "'>given&nbsp;<a " + 
                    HTML.link(tp) + ">" + 
                    tp.getName() + "</a>" + bounds + arg + 
                    "</span></tt>", 
                    20, 4);
        }
    }

    private static String description(Declaration dec, 
            Node node,  Reference pr, 
            CeylonParseController cpc, Unit unit) {
        if (pr==null) {
            pr = appliedReference(dec, node);
        }
        String doc = getDocDescriptionFor(dec, pr, unit);
        StringBuffer description = new StringBuffer(doc);
        if (dec instanceof TypeDeclaration) {
            TypeDeclaration td = (TypeDeclaration) dec;
            if (td.isAlias()) {
                Type et = td.getExtendedType();
                if (et!=null) {
                    description.append(" => ")
                        .append(et.asString());
                }
            }
        }
        if (dec instanceof Value && !isVariable(dec) ||
                dec instanceof Function) {
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
                            jdiVariableForTypeParameter(
                                    stackFrame.getJavaDebugTarget(), 
                                    stackFrame, typeParameter);
                    if (typeDescriptor!=null) {
                        IJavaObject jdiProducedType = 
                                getJdiProducedType(typeDescriptor.getValue(), 
                                        producedTypeFromTypeDescriptor);
                        Type producedType = 
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
            StringBuilder buffer, IProgressMonitor monitor) {
        try {
            appendJavadoc(getJavaElement(model, monitor), buffer);
        }
        catch (JavaModelException jme) {
            jme.printStackTrace();
        }
    }

    private static void appendDocAnnotationContent(
            Tree.AnnotationList annotationList,
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
            for (Tree.Annotation annotation: 
                    annotationList.getAnnotations()) {
                Tree.Primary annotPrim = 
                        annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) 
                                annotPrim;
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
    
    private static void appendDeprecatedAnnotationContent(
            Tree.AnnotationList annotationList,
            StringBuilder documentation, Scope linkScope) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation: 
                    annotationList.getAnnotations()) {
                Tree.Primary annotPrim = 
                        annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) 
                                annotPrim;
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
    
    private static void appendSeeAnnotationContent(
            Tree.AnnotationList annotationList,
            StringBuilder documentation) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation: 
                    annotationList.getAnnotations()) {
                Tree.Primary annotPrim = 
                        annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) 
                                annotPrim;
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
                                                        (ClassOrInterface) 
                                                            dec.getContainer();
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
                                        HTML.fileUrl("link_obj.gif"/*getIcon(dec)*/)
                                            .toExternalForm(), 
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
    
    private static void appendThrowAnnotationContent(
            Tree.AnnotationList annotationList,
            StringBuilder documentation, Scope linkScope) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation: 
                    annotationList.getAnnotations()) {
                Tree.Primary annotPrim = 
                        annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) 
                                annotPrim;
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
                                                HTML.fileUrl("ihigh_obj.gif"/*getIcon(dec)*/)
                                                    .toExternalForm(), 
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
    
    private static String markdown(String text, 
            final Scope linkScope, final Unit unit) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        Builder builder = 
                Configuration.builder()
                    .forceExtentedProfile();
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
    
    public IInformationControlCreator getInformationPresenterControlCreator() {
        return new CeylonEnrichedInformationControlCreator(editor);
    }
    
}
