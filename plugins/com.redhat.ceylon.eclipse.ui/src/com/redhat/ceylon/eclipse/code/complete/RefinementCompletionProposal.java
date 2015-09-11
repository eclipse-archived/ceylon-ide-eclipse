package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getInlineFunctionDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getInlineFunctionTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getSortedProposedValues;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleClass;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleMethod;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleValue;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isInBounds;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importParameterTypes;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getRefinementIcon;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_DEFAULT_REFINEMENT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_FORMAL_REFINEMENT;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isNameMatching;
import static java.lang.Character.isJavaIdentifierPart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Constructor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Generic;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;

public final class RefinementCompletionProposal extends CompletionProposal {
    
    final class ReturnValueContextInfo implements IContextInformation {
        @Override
        public String getInformationDisplayString() {
            if (declaration instanceof TypedDeclaration) {
                return getType().asString(getUnit());
            }
            else {
                return null;
            }
        }

        @Override
        public Image getImage() {
            return getImageForDeclaration(declaration);
        }

        @Override
        public String getContextDisplayString() {
            return "Return value of '" + declaration.getName() + "'";
        }
    }

    public static Image DEFAULT_REFINEMENT = 
            CeylonPlugin.imageRegistry()
                .get(CEYLON_DEFAULT_REFINEMENT);

    public static Image FORMAL_REFINEMENT = 
            CeylonPlugin.imageRegistry()
                .get(CEYLON_FORMAL_REFINEMENT);
    
    static void addRefinementProposal(int offset, 
            Declaration dec, ClassOrInterface ci, 
            Node node, Scope scope, String prefix, 
            CeylonParseController cpc, IDocument doc, 
            List<ICompletionProposal> result, 
            boolean preamble) {
        boolean isInterface = scope instanceof Interface;
        Reference pr = getRefinedProducedReference(scope, dec);
        Unit unit = node.getUnit();
        result.add(new RefinementCompletionProposal(offset, 
                prefix, pr,
                getRefinementDescriptionFor(dec, pr, unit), 
                getRefinementTextFor(dec, pr, unit, 
                        isInterface, ci, 
                        getDefaultLineDelimiter(doc) 
                            + getIndent(node, doc), 
                        true, preamble), 
                cpc, dec, scope, false, true));
    }
    
    static void addNamedArgumentProposal(int offset, 
            String prefix, 
            CeylonParseController cpc, 
            List<ICompletionProposal> result, 
            Declaration dec, Scope scope) {
        //TODO: type argument substitution using the 
        //     Reference of the primary node
        Unit unit = cpc.getRootNode().getUnit();
        result.add(new RefinementCompletionProposal(offset, 
                prefix, 
                dec.getReference(), //TODO: this needs to do type arg substitution
                getDescriptionFor(dec, unit), 
                getTextFor(dec, unit) + " = nothing;", 
                cpc, dec, scope, true, false));
    }

    static void addInlineFunctionProposal(int offset, 
            Declaration dec, Scope scope, Node node, 
            String prefix, CeylonParseController cpc, 
            IDocument doc, 
            List<ICompletionProposal> result) {
        //TODO: type argument substitution using the 
        //      Reference of the primary node
        if (dec.isParameter()) {
            FunctionOrValue fov = (FunctionOrValue) dec;
            Parameter p = fov.getInitializerParameter();
            Unit unit = node.getUnit();
            result.add(new RefinementCompletionProposal(
                    offset, prefix, 
                    dec.getReference(), //TODO: this needs to do type arg substitution
                    getInlineFunctionDescriptionFor(p, null, unit),
                    getInlineFunctionTextFor(p, null, unit, 
                            getDefaultLineDelimiter(doc) + 
                            getIndent(node, doc)),
                    cpc, dec, scope, false, false));
        }
    }

    public static Reference getRefinedProducedReference(
            Scope scope, Declaration d) {
        return refinedProducedReference(
                scope.getDeclaringType(d), d);
    }

    public static Reference getRefinedProducedReference(
            Type superType, Declaration d) {
        if (superType.isIntersection()) {
            for (Type pt: superType.getSatisfiedTypes()) {
                Reference result = 
                        getRefinedProducedReference(pt, d);
                if (result!=null) return result;
            }
            return null; //never happens?
        }
        else {
            Type declaringType = 
                    superType.getDeclaration()
                        .getDeclaringType(d);
            if (declaringType==null) return null;
            Type outerType = 
                    superType.getSupertype(
                            declaringType.getDeclaration());
            return refinedProducedReference(outerType, d);
        }
    }
    
    private static Reference refinedProducedReference(
            Type outerType, Declaration d) {
        List<Type> params = new ArrayList<Type>();
        if (d instanceof Generic) {
            Generic g = (Generic) d;
            for (TypeParameter tp: g.getTypeParameters()) {
                params.add(tp.getType());
            }
        }
        return d.appliedReference(outerType, params);
    }
    
    private final CeylonParseController cpc;
    private final Declaration declaration;
    private final Reference pr;
    private final boolean fullType;
    private final Scope scope;
    private boolean explicitReturnType;

    private RefinementCompletionProposal(int offset, 
            String prefix, Reference pr, 
            String desc, String text, 
            CeylonParseController cpc, 
            Declaration dec, Scope scope,
            boolean fullType, boolean explicitReturnType) {
        super(offset, prefix, getRefinementIcon(dec), 
                desc, text);
        this.cpc = cpc;
        this.declaration = dec;
        this.pr = pr;
        this.fullType = fullType;
        this.scope = scope;
        this.explicitReturnType = explicitReturnType;
    }

    private Unit getUnit() {
        return cpc.getRootNode().getUnit();
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        StyledString result = new StyledString();
        String string = getDisplayString();
        Font font = CeylonPlugin.getCompletionFont();
        if (string.startsWith("shared actual ")) {
            result.append(string.substring(0,14), 
            		new Highlights.FontStyler(font,
            		        Highlights.ANN_STYLER));
            string = string.substring(14);
        }
        int loc = string.indexOf(' ');
        if (loc>=0) {
            Highlights.styleFragment(result, 
            		string.substring(0, loc), 
            		false, null, font);
            Highlights.styleFragment(result, 
            		string.substring(loc), 
            		false, currentPrefix, font);
        }
        else {
            Highlights.styleFragment(result, 
                    string, false, null, font);
        }
        return result;
    }
    
    private Type getType() {
        return fullType ?
                pr.getFullType() :
                pr.getType();
    }
    
    @Override
    public Point getSelection(IDocument document) {
        int loc = text.indexOf("nothing;");
        int length;
        int start;
        if (loc<0) {
            start = offset + text.length()-prefix.length();
            if (text.endsWith("{}")) start--;
            length = 0;
        }
        else {
            start = offset + loc-prefix.length();
            length = 7;
        }
        return new Point(start, length);
    }

    @Override
    public void apply(IDocument document) {
        try {
            createChange(document)
                .perform(new NullProgressMonitor());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (getPreferences().getBoolean(LINKED_MODE_ARGUMENTS)) {
            enterLinkedMode(document);
        }
    }

    private DocumentChange createChange(IDocument document)
            throws BadLocationException {
        DocumentChange change = 
                new DocumentChange("Complete Refinement", 
                        document);
        change.setEdit(new MultiTextEdit());
        HashSet<Declaration> decs = 
                new HashSet<Declaration>();
        Tree.CompilationUnit cu = cpc.getRootNode();
        if (explicitReturnType) {
            importSignatureTypes(declaration, cu, decs);
        }
        else {
            importParameterTypes(declaration, cu, decs);
        }
        int il = applyImports(change, decs, cu, document);
        change.addEdit(createEdit(document));
        offset+=il;
        return change;
    }

    public String getAdditionalProposalInfo() {
        return getDocumentationFor(cpc, declaration);    
    }
    
    public void enterLinkedMode(IDocument document) {
        try {
            final int loc = offset-prefix.length();
            int pos = text.indexOf("nothing");
            if (pos>0) {
                final LinkedModeModel linkedModeModel = 
                        new LinkedModeModel();
                List<ICompletionProposal> props = 
                        new ArrayList<ICompletionProposal>();
                addProposals(loc+pos, prefix, props);
                ProposalPosition linkedPosition = 
                        new ProposalPosition(document, 
                                loc+pos, 7, 0, 
                                props.toArray(NO_COMPLETIONS));
                LinkedMode.addLinkedPosition(linkedModeModel, 
                        linkedPosition);
                CeylonEditor editor = 
                		(CeylonEditor) 
                		    getCurrentEditor();
				LinkedMode.installLinkedMode(editor,
                        document, linkedModeModel, this, 
                        new LinkedMode.NullExitPolicy(), 
                        1, loc+text.length());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public IContextInformation getContextInformation() {
        return new ReturnValueContextInfo();
    }
    @Override
    public boolean validate(IDocument document, int offset, 
            DocumentEvent event) {
        if (offset<this.offset) {
            return false;
        }
        currentPrefix = getCurrentPrefix(document, offset);
        return currentPrefix==null ? false :
        	isNameMatching(currentPrefix, 
        	        declaration.getName());
    }
    
    private void addProposals(int loc, String prefix, 
            List<ICompletionProposal> props) {
        Type type = getType();
        if (type==null) return;
        TypeDeclaration td = type.getDeclaration();
        for (DeclarationWithProximity dwp: 
                getSortedProposedValues(scope, getUnit())) {
            if (dwp.isUnimported()) {
                //don't propose unimported stuff b/c adding
                //imports drops us out of linked mode and
                //because it results in a pause
                continue;
            }
            Declaration d = dwp.getDeclaration();
            final String name = d.getName();
            String[] split = prefix.split("\\s+");
            if (split.length>0 && 
                    name.equals(split[split.length-1])) {
                continue;
            }
            Package pack = d.getUnit().getPackage();
            boolean inLanguageModule = 
                    pack.getNameAsString()
                        .equals(Module.LANGUAGE_MODULE_NAME);
            if (d instanceof Value && 
                    !d.equals(declaration)) {
                Value value = (Value) d;
                if (inLanguageModule) {
                    if (isIgnoredLanguageModuleValue(value)) {
                        continue;
                    }
                }
                Type vt = value.getType();
                if (vt!=null && !vt.isNothing() &&
                    (isTypeParamInBounds(td, vt) || 
                            vt.isSubtypeOf(type))) {
                    props.add(new NestedCompletionProposal(d, loc));
                }
            }
            if (d instanceof Function && 
                    !d.equals(declaration) && 
                    !d.isAnnotation()) {
                Function method = (Function) d;
                if (inLanguageModule &&
                        isIgnoredLanguageModuleMethod(method)) {
                        continue;
                }
                Type mt = method.getType();
                if (mt!=null && !mt.isNothing() &&
                    (isTypeParamInBounds(td, mt) || 
                            mt.isSubtypeOf(type))) {
                    props.add(new NestedCompletionProposal(d, loc));
                }
            }
            if (d instanceof Class) {
                Class clazz = (Class) d;
                if (!clazz.isAbstract() && 
                        !d.isAnnotation()) {
                    if (inLanguageModule 
                            && isIgnoredLanguageModuleClass(clazz)) {
                            continue;
                    }
                    Type ct = clazz.getType();
                    if (ct!=null && !ct.isNothing() &&
                            (isTypeParamInBounds(td, ct) || 
                                    ct.getDeclaration()
                                        .equals(type.getDeclaration()) ||
                                    ct.isSubtypeOf(type))) {
                        if (clazz.getParameterList()!=null) {
                            props.add(new NestedCompletionProposal(d, loc));
                        }
                        for (Declaration m: clazz.getMembers()) {
                            if (m instanceof Constructor && 
                                    m.isShared() &&
                                    m.getName()!=null) {
                                props.add(new NestedCompletionProposal(m, loc));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isTypeParamInBounds(TypeDeclaration td, Type t) {
        if (td instanceof TypeParameter) {
            TypeParameter tp = (TypeParameter) td;
            return isInBounds(tp.getSatisfiedTypes(), t);
        }
        else {
            return false;
        }
    }
    
    final class NestedCompletionProposal 
            implements ICompletionProposal, 
                       ICompletionProposalExtension2,
                       ICompletionProposalExtension6 {
        
        private final Declaration dec;
        private final int offset;
        
        public NestedCompletionProposal(Declaration dec, int offset) {
            super();
            this.dec = dec;
            this.offset = offset;
        }

        @Override
        public void apply(IDocument document) {
            try {
                int len = 0;
                while (isJavaIdentifierPart(
                        document.getChar(offset+len))) {
                    len++;
                }
                document.replace(offset, len, getText(false));
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Point getSelection(IDocument document) {
            return null;
        }

        @Override
        public String getAdditionalProposalInfo() {
            return null;
        }

        @Override
        public String getDisplayString() {
            return getText(true);
        }

        @Override
        public StyledString getStyledDisplayString() {
            StyledString result = new StyledString();
            Highlights.styleFragment(result, 
                    getDisplayString(), false, null, 
                    CeylonPlugin.getCompletionFont());
            return result;
        }

        @Override
        public Image getImage() {
            return getImageForDeclaration(dec);
        }

        @Override
        public IContextInformation getContextInformation() {
           return null;
        }
        
        private String getText(boolean description) {
            StringBuilder sb = new StringBuilder();
            if (dec instanceof Constructor) {
                Constructor constructor = (Constructor) dec;
                TypeDeclaration clazz = 
                        constructor.getExtendedType()
                            .getDeclaration();
                sb.append(escapeName(clazz, getUnit()))
                    .append('.');
            }
            sb.append(escapeName(dec, getUnit()));
            if (dec instanceof Functional) {
                appendPositionalArgs(dec, getUnit(), 
                        sb, false, description);
            }
            return sb.toString();
        }

        @Override
        public void apply(ITextViewer viewer, char trigger, 
                int stateMask, int offset) {
            apply(viewer.getDocument());
        }

        @Override
        public void selected(ITextViewer viewer, boolean smartToggle) {}

        @Override
        public void unselected(ITextViewer viewer) {}

        @Override
        public boolean validate(IDocument document, 
                int currentOffset, DocumentEvent event) {
            if (event==null) {
                return true;
            }
            else {
                try {
                    String content = 
                            document.get(offset, 
                                    currentOffset-offset);
                    String filter = 
                            content.trim().toLowerCase();
                    String decName = 
                            dec.getName(getUnit())
                                .toLowerCase();
                    if (dec instanceof Constructor) {
                        Constructor constructor = 
                                (Constructor) dec;
                        TypeDeclaration clazz = 
                                constructor.getExtendedType()
                                    .getDeclaration();
                        decName = 
                                clazz.getName(getUnit())
                                    .toLowerCase() +
                                '.' + decName;
                    }
                    if (decName.startsWith(filter)) {
                        return true;
                    }
                }
                catch (BadLocationException e) {
                    // ignore concurrently modified document
                }
                return false;
            }
        }

    }
    
}