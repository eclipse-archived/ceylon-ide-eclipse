package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendParameterContextInfo;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getNamedInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getNamedInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getPositionalInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getPositionalInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getAssignableLiterals;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getCurrentArgumentRegion;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getParameters;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getProposedName;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getSortedProposedValues;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleClass;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleMethod;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleType;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleValue;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isInBounds;
import static com.redhat.ceylon.eclipse.code.complete.ParameterContextValidator.findCharCount;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importProposals;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.CHAIN_LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INEXACT_MATCHES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.getCompletionFont;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.getPreferences;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.performChange;
import static com.redhat.ceylon.eclipse.util.LinkedMode.addLinkedPosition;
import static com.redhat.ceylon.eclipse.util.LinkedMode.installLinkedMode;
import static com.redhat.ceylon.ide.common.util.Escaping.escapeName;
import static com.redhat.ceylon.ide.common.util.OccurrenceLocation.CLASS_ALIAS;
import static com.redhat.ceylon.ide.common.util.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.ide.common.util.OccurrenceLocation.SATISFIES;
import static com.redhat.ceylon.ide.common.util.OccurrenceLocation.TYPE_ALIAS;
import static com.redhat.ceylon.ide.common.util.OccurrenceLocation.UPPER_BOUND;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getContainingClassOrInterface;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.ide.common.util.OccurrenceLocation;
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
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.NothingType;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedReference;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;

class InvocationCompletionProposal extends CompletionProposal {
    
    private static final List<Type> NO_TYPES = Collections.<Type>emptyList();

    @Deprecated
    static void addProgramElementReferenceProposal(
            int offset, String prefix,
            CeylonParseController controller,
            List<ICompletionProposal> result,
            Declaration dec, Scope scope, boolean isMember) {
        Unit unit = controller.getLastCompilationUnit().getUnit();
        result.add(new InvocationCompletionProposal(
                offset, prefix,
                dec.getName(unit), escapeName(dec, unit),
                dec, dec.getReference(), scope, controller,
                true, false, false, false, isMember, null));
    }
    
    @Deprecated
    static void addReferenceProposal(
            int offset, String prefix,
            final CeylonParseController controller,
            List<ICompletionProposal> result, 
            DeclarationWithProximity dwp,
            Scope scope, boolean isMember,
            Reference pr, OccurrenceLocation ol) {
        Unit unit = controller.getLastCompilationUnit().getUnit();
        Declaration dec = dwp.getDeclaration();
        //proposal with type args
        if (dec instanceof Generic) {
            result.add(new InvocationCompletionProposal(
                    offset, prefix,
                    getDescriptionFor(dwp, unit, true),
                    getTextFor(dec, unit), 
                    dec, pr, scope, controller,
                    true, false, false,
                    ol==UPPER_BOUND ||
                    ol==EXTENDS ||
                    ol==SATISFIES,
                    isMember, null));
            Generic g = (Generic) dec;
            if (g.getTypeParameters().isEmpty()) {
                //don't add another proposal below!
                return;
            }
        }
        //proposal without type args
        boolean isAbstract = 
                dec instanceof Interface ||
                dec instanceof Class && 
                    ((Class) dec).isAbstract();
        if ((!isAbstract && 
                ol!=EXTENDS && ol!=SATISFIES && ol!=UPPER_BOUND ||
                ol!=CLASS_ALIAS && ol!=TYPE_ALIAS)) {
            result.add(new InvocationCompletionProposal(
                    offset, prefix,
                    getDescriptionFor(dwp, unit, false),
                    escapeName(dec, unit), 
                    dec, pr, scope, controller,
                    true, false, false, false,
                    isMember, null));
        }
    }
    
    @Deprecated
    static void addSecondLevelProposal(
            int offset, String prefix, 
            CeylonParseController controller, 
            List<ICompletionProposal> result, 
            Declaration dec, Scope scope, 
            boolean isMember, Reference pr,
            Type requiredType, OccurrenceLocation ol) {
        Unit unit =
                controller.getLastCompilationUnit()
                    .getUnit();
        Type type = pr.getType();
        if (type!=null) {
            if (!(dec instanceof Functional) &&
                !(dec instanceof TypeDeclaration)) {
                //add qualified member proposals
                Collection<DeclarationWithProximity> members =
                        type.getDeclaration()
                            .getMatchingMemberDeclarations(
                                    unit, scope, "", 0)
                            .values();
                for (DeclarationWithProximity ndwp: members) {
                    Declaration m = ndwp.getDeclaration();
                    if ((m instanceof FunctionOrValue ||
                            m instanceof Class) &&
                            !isConstructor(m)) {
                        if (m.isAbstraction()) {
                            for (Declaration o:
                                    m.getOverloads()) {
                                addSecondLevelProposal(
                                        offset, prefix,
                                        controller, result, dec,
                                        scope, requiredType, ol,
                                        unit, type, ndwp, o);
                            }
                        }
                        else {
                            addSecondLevelProposal(
                                    offset, prefix,
                                    controller, result, dec,
                                    scope, requiredType, ol,
                                    unit, type, ndwp, m);
                        }
                    }
                }
            }
            if (dec instanceof Class) {
                //add constructor proposals
                List<Declaration> members =
                        type.getDeclaration()
                            .getMembers();
                for (Declaration m: members) {
                    if (m instanceof Constructor &&
                            m.isShared() &&
                            m.getName()!=null) {
                        addSecondLevelProposal(
                                offset, prefix,
                                controller, result, dec,
                                scope, requiredType, ol,
                                unit, type, null, m);
                    }
                }
            }
        }
    }

    @Deprecated
    private static void addSecondLevelProposal(
            int offset, String prefix,
            CeylonParseController controller, 
            List<ICompletionProposal> result,
            Declaration dec, Scope scope,
            Type requiredType, OccurrenceLocation ol,
            Unit unit, Type type,
            DeclarationWithProximity mwp,
            // sometimes we have no mwp so we also need the m
            Declaration m) {
        Reference ptr = type.getTypedReference(m, NO_TYPES);
        Type mt = ptr.getType();
        if (mt!=null && 
                (requiredType==null ||
                 withinBounds(requiredType.getDeclaration(), mt) ||
                 dec instanceof Class &&
                     dec.equals(requiredType.getDeclaration()) ||
                 mt.isSubtypeOf(requiredType))) {
            String qualifier = dec.getName() + ".";
            String desc = 
                    qualifier + 
                    getPositionalInvocationDescriptionFor(
                            mwp, m, ol, ptr, unit, false, null);
            String text = 
                    qualifier + 
                    getPositionalInvocationTextFor(
                            m, ol, ptr, unit, false, null);
            result.add(new InvocationCompletionProposal(
                    offset, prefix, desc, text, m, ptr, scope, 
                    controller, true, true, false,
                    ol==UPPER_BOUND ||
                    ol==EXTENDS ||
                    ol==SATISFIES,
                    true, dec));
        }
    }
    
    @Deprecated
    static void addInvocationProposals(
            int offset, String prefix, 
            CeylonParseController controller,
            List<ICompletionProposal> result, 
            DeclarationWithProximity dwp,
            // sometimes we have no dwp, just a dec, so we have to handle that too
            Declaration dec,
            Reference pr,
            Scope scope, OccurrenceLocation ol,
            String typeArgs, boolean isMember) {
        if (dec instanceof Functional) {
            Unit unit =
                    controller.getLastCompilationUnit()
                        .getUnit();
            boolean isAbstract = 
                    dec instanceof TypeDeclaration && 
                    ((TypeDeclaration) dec).isAbstract();
            Functional fd = (Functional) dec;
            List<ParameterList> pls = fd.getParameterLists();
            if (!pls.isEmpty()) {
                ParameterList parameterList = pls.get(0);
                List<Parameter> ps = 
                        parameterList.getParameters();
                String inexactMatches = 
                        getPreferences()
                            .getString(INEXACT_MATCHES);
                boolean exact = 
                        prefixWithoutTypeArgs(prefix, typeArgs)
                            .equalsIgnoreCase(dec.getName(unit));
                boolean positional = 
                        exact ||
                        "both".equals(inexactMatches) || 
                        "positional".equals(inexactMatches);
                boolean named = 
                        exact ||
                        "both".equals(inexactMatches);
                boolean inheritance =
                        ol==UPPER_BOUND ||
                        ol==EXTENDS ||
                        ol==SATISFIES;
                if (positional &&
                        parameterList.isPositionalParametersSupported() &&
                        (!isAbstract || 
                                ol==EXTENDS || ol==CLASS_ALIAS)) {
                    List<Parameter> parameters = 
                            getParameters(parameterList, 
                                    false, false);
                    if (ps.size()!=parameters.size()) {
                        String desc = 
                                getPositionalInvocationDescriptionFor(
                                        dwp, dec, ol, pr, unit, false,
                                        typeArgs);
                        String text = 
                                getPositionalInvocationTextFor(
                                        dec, ol, pr, unit, false, 
                                        typeArgs);
                        result.add(new InvocationCompletionProposal(
                                offset, prefix, desc, text, dec, pr, scope, 
                                controller, false, true, false,
                                inheritance, isMember, null));
                    }
                    String desc = 
                            getPositionalInvocationDescriptionFor(
                                    dwp, dec, ol, pr, unit, true,
                                    typeArgs);
                    String text = 
                            getPositionalInvocationTextFor(
                                    dec, ol, pr, unit, true, 
                                    typeArgs);
                    result.add(new InvocationCompletionProposal(
                            offset, prefix, desc, text, dec, pr, scope, 
                            controller, true, true, false,
                            inheritance, isMember, null));
                }
                if (named && 
                        parameterList.isNamedParametersSupported() &&
                        (!isAbstract && 
                                ol!=EXTENDS && ol!=CLASS_ALIAS &&
                                !dec.isOverloaded())) {
                    //if there is at least one parameter, 
                    //suggest a named argument invocation
                    List<Parameter> parameters = 
                            getParameters(parameterList, false, true);
                    if (ps.size()!=parameters.size()) {
                        String desc = 
                                getNamedInvocationDescriptionFor(
                                        dec, pr, unit, false, 
                                        typeArgs);
                        String text = 
                                getNamedInvocationTextFor(
                                        dec, pr, unit, false, 
                                        typeArgs);
                        result.add(new InvocationCompletionProposal(
                                offset, prefix, desc, text, dec, pr, scope, 
                                controller, false, false, true,
                                inheritance, isMember, null));
                    }
                    if (!ps.isEmpty()) {
                        String desc = 
                                getNamedInvocationDescriptionFor(
                                        dec, pr, unit, true, 
                                        typeArgs);
                        String text = 
                                getNamedInvocationTextFor(
                                        dec, pr, unit, true, 
                                        typeArgs);
                        result.add(new InvocationCompletionProposal(
                                offset, prefix, desc, text, dec, pr, scope, 
                                controller, true, false, true,
                                inheritance, isMember, null));
                    }
                }
            }
        }
    }

    private static String prefixWithoutTypeArgs(
            String prefix, String typeArgs) {
        if (typeArgs==null) {
            return prefix;
        }
        else {
            return prefix.substring(0, 
                    prefix.length()-typeArgs.length());
        }
    }
    
    @Deprecated
    final class NestedCompletionProposal
            implements ICompletionProposal, 
                       ICompletionProposalExtension2,
                       ICompletionProposalExtension6 {
        private final String op;
        private final int loc;
        private final int index;
        private final boolean basic;
        private final Declaration dec;
        private Declaration qualifier;
        
        NestedCompletionProposal(
                Declaration dec, Declaration qualifier, 
                int loc, int index, boolean basic, 
                String op) {
            this.qualifier = qualifier;
            this.op = op;
            this.loc = loc;
            this.index = index;
            this.basic = basic;
            this.dec = dec;
        }

        public String getAdditionalProposalInfo() {
            return null;
        }

        @Override
        public void apply(IDocument document) {
            //the following awfulness is necessary because the
            //insertion point may have changed (and even its
            //text may have changed, since the proposal was
            //instantiated).
            try {
                IRegion region =
                        getCurrentArgumentRegion(document,
                                loc, index,
                                getFirstPosition());
                String str = getText(false);
                int start = region.getOffset();
                int len = region.getLength();
                int end = start + len;
                if (document.getChar(end)=='}') {
                    str += " ";
                }
                document.replace(start, len, str);
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
            //adding imports drops us out of linked mode :(
            //not needed anyway because we never propose
            //unimported stuff, so no big deal
            /*try {
                DocumentChange tc = 
                        new DocumentChange("imports", document);
                tc.setEdit(new MultiTextEdit());
                HashSet<Declaration> decs = 
                        new HashSet<Declaration>();
                Tree.CompilationUnit cu = cpc.getRootNode();
                importDeclaration(decs, dec, cu);
                if (dec instanceof Functional) {
                    List<ParameterList> pls = 
                            ((Functional) dec).getParameterLists();
                    if (!pls.isEmpty()) {
                        for (Parameter p: pls.get(0).getParameters()) {
                            FunctionOrValue pm = p.getModel();
                            if (pm instanceof Function) {
                                for (ParameterList ppl: 
                                        ((Function) pm).getParameterLists()) {
                                    for (Parameter pp: ppl.getParameters()) {
                                        importSignatureTypes(pp.getModel(), cu, decs);
                                    }
                                }
                            }
                        }
                    }
                    
                }
                applyImports(tc, decs, cu, document);
                EditorUtil.performChange(tc);
            }
            catch (Exception e) {
                e.printStackTrace();
            }*/
        }

        private String getText(boolean description) {
            StringBuilder sb = new StringBuilder(op);
            Unit unit = getUnit();
            sb.append(getProposedName(qualifier, dec, unit));
            if (dec instanceof Functional && !basic) {
                appendPositionalArgs(dec, unit, sb, false,
                        description);
            }
            return sb.toString();
        }

        @Override
        public Point getSelection(IDocument document) {
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
                    getCompletionFont());
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
                    IRegion region =
                            getCurrentArgumentRegion(document,
                                    loc, index,
                                    getFirstPosition());
                    String content = 
                            document.get(region.getOffset(),
                                    currentOffset-region.getOffset());
                    return isContentValid(content);
                }
                catch (BadLocationException e) {
                    // ignore concurrently modified document
                    return false;
                }
            }
        }

        private boolean isContentValid(String content) {
            int fat = content.indexOf("=>");
            if (fat>0) {
                content = content.substring(fat+2);
            }
            int eq = content.indexOf("=");
            if (eq>0) {
                content = content.substring(eq+1);
            }
            if (content.startsWith(op)) {
                content = content.substring(op.length());
            }
            String filter = content.trim().toLowerCase();
            return ModelUtil.isNameMatching(content, dec) ||
                    getProposedName(qualifier, dec, getUnit())
                        .toLowerCase()
                        .startsWith(filter);
        }
    }

    @Deprecated
    final class NestedLiteralCompletionProposal
            implements ICompletionProposal, 
                       ICompletionProposalExtension2,
                       ICompletionProposalExtension6 {
        
        private final int loc;
        private final int index;
        private final String value;
        
        NestedLiteralCompletionProposal(String value, int loc, 
                int index) {
            this.value = value;
            this.loc = loc;
            this.index = index;
        }
        
        public String getAdditionalProposalInfo() {
            return null;
        }
        
        @Override
        public void apply(IDocument document) {
            //the following awfulness is necessary because the
            //insertion point may have changed (and even its
            //text may have changed, since the proposal was
            //instantiated).
            try {
                IRegion region =
                        getCurrentArgumentRegion(document,
                                loc, index,
                                getFirstPosition());
                String str = value;
                int start = region.getOffset();
                int len = region.getLength();
                int end = start + len;
                if (document.getChar(end)=='}') {
                    str += " ";
                }
                document.replace(start, len, str);
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
        public String getDisplayString() {
            return value;
        }
        

        @Override
        public StyledString getStyledDisplayString() {
            StyledString result = new StyledString();
            Highlights.styleFragment(result,
                    getDisplayString(), false, null,
                    getCompletionFont());
            return result;
        }

        @Override
        public Image getImage() {
            return getDecoratedImage(CEYLON_LITERAL, 0, false);
        }
        
        @Override
        public IContextInformation getContextInformation() {
            return null;
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
                    IRegion region =
                            getCurrentArgumentRegion(document,
                                    loc, index,
                                    getFirstPosition());
                    String content = 
                            document.get(region.getOffset(),
                                    currentOffset-region.getOffset());
                    int eq = content.indexOf("=");
                    if (eq>0) {
                        content = content.substring(eq+1);
                    }
                    String filter = content.trim().toLowerCase();
                    if (value.toLowerCase().startsWith(filter)) {
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
    
    private final CeylonParseController cpc;
    private final Declaration declaration;
    private final Reference producedReference;
    private final Scope scope;
    private final boolean includeDefaulted;
    private final boolean namedInvocation;
    private final boolean positionalInvocation;
    private final boolean qualified;
    private Declaration qualifyingValue;
    private boolean inheritance;

    @Deprecated
    InvocationCompletionProposal(
            int offset, String prefix,
            String desc, String text,
            Declaration dec,
            Reference producedReference,
            Scope scope,
            CeylonParseController controller,
            boolean includeDefaulted,
            boolean positionalInvocation,
            boolean namedInvocation,
            boolean inheritance,
            boolean qualified,
            Declaration qualifyingValue) {
        super(offset, prefix, getImageForDeclaration(dec), 
                desc, text);
        this.cpc = controller;
        this.declaration = dec;
        this.producedReference = producedReference;
        this.scope = scope;
        this.includeDefaulted = includeDefaulted;
        this.namedInvocation = namedInvocation;
        this.positionalInvocation = positionalInvocation;
        this.inheritance = inheritance;
        this.qualified = qualified;
        this.qualifyingValue = qualifyingValue;
    }

    @Deprecated
    protected boolean isProposalMatching(String currentPrefix, String text){
        if(super.isProposalMatching(currentPrefix, text))
            return true;
        for(String alias : declaration.getAliases()){
            if(ModelUtil.isNameMatching(currentPrefix, alias))
                return true;
        }
        return false;
    }


    private Unit getUnit() {
        return cpc.getLastCompilationUnit().getUnit();
    }

    @Deprecated
    private DocumentChange createChange(IDocument document)
            throws BadLocationException {
        DocumentChange change = 
                new DocumentChange("Complete Invocation", 
                        document);
        change.setEdit(new MultiTextEdit());
        HashSet<Declaration> decs =
                new HashSet<Declaration>();
        Tree.CompilationUnit cu = cpc.getLastCompilationUnit();
        if (qualifyingValue!=null) {
            importProposals().importDeclaration(decs, qualifyingValue, cu);
        }
        if (!qualified) {
            importProposals().importDeclaration(decs, declaration, cu);
        }
        if (positionalInvocation||namedInvocation) {
            importProposals().importCallableParameterParamTypes(declaration,
                    decs, cu);
        }
        int il= (int) importProposals().applyImports(change, decs, cu, document);
        change.addEdit(createEdit(document));
        offset+=il;
        return change;
    }

    @Override
    public void apply(IDocument document) {
        try {
            performChange(createChange(document));
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (getPreferences()
                .getBoolean(LINKED_MODE_ARGUMENTS)) {
            activeLinkedMode(document);
        }
    }

    @Deprecated
    private void activeLinkedMode(IDocument document) {
        if (declaration instanceof Generic) {
            Generic generic = (Generic) declaration;
            ParameterList paramList = null;
            if (declaration instanceof Functional && 
                    (positionalInvocation || namedInvocation)) {
                Functional fd = (Functional) declaration;
                List<ParameterList> pls =
                        fd.getParameterLists();
                if (!pls.isEmpty() && 
                        !pls.get(0).getParameters()
                            .isEmpty()) {
                    paramList = pls.get(0);
                }
            }
            if (paramList!=null) {
                List<Parameter> params = 
                        getParameters(paramList, 
                                includeDefaulted, 
                                namedInvocation);
                if (!params.isEmpty()) {
                    enterLinkedMode(document, params, null);
                    return; //NOTE: early exit!
                }
            }
            List<TypeParameter> typeParams = 
                    generic.getTypeParameters();
            if (!typeParams.isEmpty()) {
                enterLinkedMode(document, null, typeParams);
            }
        }
    }
    
    @Override
    public Point getSelection(IDocument document) {
        int first = getFirstPosition();
        if (first<=0) {
            //no arg list
            return super.getSelection(document);
        }
        int next = getNextPosition(document, first);
        if (next<=0) {
            //an empty arg list
            return super.getSelection(document);
        }
        int middle = getCompletionPosition(first, next);
        int start = offset-prefix.length()+first+middle;
        int len = next-middle;
        try {
            if (document.get(start, len).trim()
                    .equals("{}")) {
                start++;
                len=0;
            }
        } catch (BadLocationException e) {}
        return new Point(start, len);
    }
    
    @Deprecated
    protected int getCompletionPosition(int first, int next) {
        return text.substring(first, first+next-1)
                .lastIndexOf(' ') + 1;
    }

    @Deprecated
    protected int getFirstPosition() {
        int index;
        if (namedInvocation) {
            index = text.indexOf('{');
        }
        else if (positionalInvocation) {
            index = text.indexOf('(');
        }
        else {
            index = text.indexOf('<');
        }
        return index+1;
    }
    
    @Deprecated
    public int getNextPosition(IDocument document,
            int lastOffset) {
        int loc = offset-prefix.length();
        int comma = -1;
        try {
            int start = loc+lastOffset;
            int end = loc+text.length()-1;
            if (text.endsWith(";")) {
                end--;
            }
            comma =
                    findCharCount(1, document,
                            start, end,
                            ",;", "", true)
                        - start;
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (comma<0) {
            int index;
            if (namedInvocation) {
                index = text.lastIndexOf('}');
            }
            else if (positionalInvocation) {
                index = text.lastIndexOf(')');
            }
            else {
                index = text.lastIndexOf('>');
            }
            return index - lastOffset;
        }
        return comma;
    }

    @Deprecated
    public String getAdditionalProposalInfo() {
        return getAdditionalProposalInfo(null);
    }

    public String getAdditionalProposalInfo(IProgressMonitor monitor) {
        return getDocumentationFor(cpc, declaration,
                producedReference, monitor);
    }
    
    @Deprecated
    public void enterLinkedMode(IDocument document,
            List<Parameter> params, 
            List<TypeParameter> typeParams) {
        boolean proposeTypeArguments = params==null;
        int paramCount = 
                proposeTypeArguments ? 
                        typeParams.size() : 
                        params.size();
        if (paramCount==0) return;
        try {
            final int loc = offset-prefix.length();
            int first = getFirstPosition();
            if (first<=0) return; //no arg list
            int next = getNextPosition(document, first);
            if (next<=0) return; //empty arg list
            LinkedModeModel linkedModeModel = 
                    new LinkedModeModel();
            int seq=0, param=0;
            while (next>0 && param<paramCount) {
                boolean voidParam = 
                        !proposeTypeArguments &&
                        params.get(param).isDeclaredVoid();
                if (proposeTypeArguments ||
                        positionalInvocation ||
                        //don't create linked positions for
                        //void callable parameters in named
                        //argument lists
                        !voidParam) {
                    List<ICompletionProposal> props = 
                            new ArrayList<ICompletionProposal>();
                    if (proposeTypeArguments) {
                        addTypeArgumentProposals(
                                typeParams.get(seq), 
                                loc, first, props, seq);
                    }
                    else if (!voidParam) {
                        addValueArgumentProposals(
                                params.get(param), 
                                loc, first, props, seq, 
                                param==params.size()-1);
                    }
                    int middle =
                            getCompletionPosition(first, next);
                    int start = loc+first+middle;
                    int len = next-middle;
                    if (voidParam) {
                        start++;
                        len=0;
                    }
                    ProposalPosition linkedPosition = 
                            new ProposalPosition(
                                    document, start, len, seq, 
                                    props.toArray(NO_COMPLETIONS));
                    addLinkedPosition(linkedModeModel, linkedPosition);
                    first = first+next+1;
                    next = getNextPosition(document, first);
                    seq++;
                }
                param++; 
            }
            if (seq>0) {
                CeylonEditor editor = 
                        (CeylonEditor)
                            getCurrentEditor();
                installLinkedMode(editor, 
                        document, linkedModeModel, this, 
                        new LinkedMode.NullExitPolicy(),
                        seq, loc+text.length());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    private void addValueArgumentProposals(
            Parameter param, int loc, int first,
            List<ICompletionProposal> props, 
            int index, boolean last) {
        if (param.getModel().isDynamicallyTyped()) {
            return;
        }
        Type type = 
                producedReference.getTypedParameter(param)
                    .getType();
        if (type==null) {
            return;
        }
        Unit unit = getUnit();
        String exactName = param.getName();
        List<DeclarationWithProximity> proposals =
                getSortedProposedValues(scope, unit,
                        exactName);
        //very special case for print()
        String dname = declaration.getQualifiedNameString();
        boolean print = "ceylon.language::print".equals(dname);
        if (print) {
            for (String value: getAssignableLiterals(
                    unit.getStringType(), unit)) {
                props.add(new NestedLiteralCompletionProposal(
                            value, loc, index));
            }
        }
        //stuff defined in the same block, along with
        //stuff with fuzzily-matching name
        for (DeclarationWithProximity dwp: proposals) {
            if (dwp.getProximity()<=1) {
                addValueArgumentProposal(param, loc, props,
                        index, last, type, unit, dwp, null);
            }
        }
        //this
        ClassOrInterface ci =
                getContainingClassOrInterface(scope);
        if (ci!=null) {
            if (ci.getType().isSubtypeOf(type)) {
                props.add(new NestedLiteralCompletionProposal(
                        "this", loc, index));
            }
        }
        //literals
        if (!print) {
            for (String value: getAssignableLiterals(type, unit)) {
                props.add(new NestedLiteralCompletionProposal(
                            value, loc, index));
            }
        }
        //stuff with lower proximity
        for (DeclarationWithProximity dwp: proposals) {
            if (dwp.getProximity()>1) {
                addValueArgumentProposal(param, loc, props,
                        index, last, type, unit, dwp, null);
            }
        }
    }

    @Deprecated
    private void addValueArgumentProposal(
            Parameter p, int loc,
            List<ICompletionProposal> props, 
            int index, boolean last,
            Type type, Unit unit, 
            DeclarationWithProximity dwp,
            DeclarationWithProximity qualifier) {
        if (qualifier==null && dwp.isUnimported()) {
            return;
        }
        TypeDeclaration td = type.getDeclaration();
        Declaration d = dwp.getDeclaration();
        if (d instanceof NothingType) {
            return;
        }
        String pname =
                d.getUnit().getPackage()
                    .getNameAsString();
        boolean isInLanguageModule =
                qualifier==null &&
                pname.equals(Module.LANGUAGE_MODULE_NAME);
        Declaration qdec =
                qualifier==null ? null :
                    qualifier.getDeclaration();
        if (d instanceof Value) {
            Value value = (Value) d;
            if (isInLanguageModule &&
                    isIgnoredLanguageModuleValue(value)) {
                return;
            }
            Type vt = value.getType();
            if (vt!=null && !vt.isNothing()) {
                if (vt.isSubtypeOf(type) ||
                        withinBounds(td, vt)) {
                    boolean isIterArg = 
                            namedInvocation && last && 
                            unit.isIterableParameterType(type);
                    boolean isVarArg = 
                            p.isSequenced() && 
                            positionalInvocation;
                    String op =
                            isIterArg || isVarArg ?
                                    "*" : "";
                    props.add(new NestedCompletionProposal(
                            d, qdec, loc, index, false, op));
                }
                if (qualifier==null && 
                        getPreferences()
                            .getBoolean(CHAIN_LINKED_MODE_ARGUMENTS)) {
                    Collection<DeclarationWithProximity> members = 
                            value.getTypeDeclaration()
                                .getMatchingMemberDeclarations(
                                        unit, scope, "", 0)
                                .values();
                    for (DeclarationWithProximity mwp: members) {
                        addValueArgumentProposal(p, loc, props, 
                                index, last, type, unit, mwp, dwp);
                    }
                }
            }
        }
        if (d instanceof Function) {
            if (!d.isAnnotation()) {
                Function method = (Function) d;
                if (isInLanguageModule &&
                        isIgnoredLanguageModuleMethod(method)) {
                    return;
                }
                Type mt = method.getType();
                if (mt!=null && !mt.isNothing()) {
                    if (mt.isSubtypeOf(type) ||
                            withinBounds(td, mt)) {
                        boolean isIterArg = 
                                namedInvocation && last && 
                                unit.isIterableParameterType(type);
                        boolean isVarArg = 
                                p.isSequenced() && 
                                positionalInvocation;
                        String op =
                                isIterArg || isVarArg ?
                                        "*" : "";
                        props.add(new NestedCompletionProposal(
                                d, qdec, loc, index, false, op));
                    }
                }
            }
        }
        if (d instanceof Class) {
            Class clazz = (Class) d;
            if (!clazz.isAbstract() && !d.isAnnotation()) {
                if (isInLanguageModule &&
                        isIgnoredLanguageModuleClass(clazz)) {
                    return;
                }
                Type ct = clazz.getType();
                if (ct!=null &&
                        (withinBounds(td, ct) || 
                         clazz.equals(type.getDeclaration()) ||
                         ct.isSubtypeOf(type))) {
                    boolean isIterArg = 
                            namedInvocation && last && 
                            unit.isIterableParameterType(type);
                    boolean isVarArg = 
                            p.isSequenced() && 
                            positionalInvocation;
                    String op =
                            isIterArg || isVarArg ?
                                    "*" : "";
                    if (clazz.getParameterList()!=null) {
                        props.add(new NestedCompletionProposal(
                                d, qdec, loc, index, false, op));
                    }
                    for (Declaration m: clazz.getMembers()) {
                        if (m instanceof Constructor &&
                                m.isShared() &&
                                m.getName()!=null) {
                            props.add(new NestedCompletionProposal(
                                    m, qdec, loc, index, false, op));
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    protected static boolean withinBounds(TypeDeclaration td, Type t) {
        if (td instanceof TypeParameter) { 
            TypeParameter tp = (TypeParameter) td;
            return isInBounds(tp.getSatisfiedTypes(), t);
        }
        else {
            return false;
        }
    }

    @Deprecated
    private void addTypeArgumentProposals(
            TypeParameter tp,
            final int loc, int first,
            List<ICompletionProposal> props,
            final int index) {
        Unit unit = getUnit();
        Class ed = unit.getExceptionDeclaration();
        for (DeclarationWithProximity dwp:
                getSortedProposedValues(scope, unit)) {
            Declaration dec = dwp.getDeclaration();
            if (dec instanceof TypeDeclaration &&
                    !dwp.isUnimported()) {
                TypeDeclaration td = (TypeDeclaration) dec;
                Type t = td.getType();
                if (!t.isNothing() &&
                        td.getTypeParameters().isEmpty() && 
                        !td.isAnnotation() &&
                        !td.inherits(ed)) {
                    String pname =
                            td.getUnit()
                                .getPackage()
                                .getNameAsString();
                    if (pname.equals(Module.LANGUAGE_MODULE_NAME)) {
                        if (isIgnoredLanguageModuleType(td)) {
                            continue;
                        }
                    }
                    if (inheritance && tp.isSelfType() ?
                            scope.equals(td) :
                            isInBounds(tp.getSatisfiedTypes(), t)) {
                        props.add(new NestedCompletionProposal(
                                dec, null, loc, index, true, ""));
                    }
                }
            }
        }
    }

    @Override
    public IContextInformation getContextInformation() {
        if (namedInvocation || positionalInvocation) { //TODO: context info for type arg lists!
            if (declaration instanceof Functional) {
                Functional fd = (Functional) declaration;
                List<ParameterList> pls = 
                        fd.getParameterLists();
                if (!pls.isEmpty()) {
                    int argListOffset = 
                            isParameterInfo() ?
                                this.offset :
                                offset-prefix.length() + 
                                text.indexOf(namedInvocation?'{':'(');
                    return new ParameterContextInformation(
                            declaration, producedReference, 
                            getUnit(), pls.get(0), 
                            argListOffset, 
                            includeDefaulted, 
                            namedInvocation);
                }
            }
        }
        return null;
    }
    
    boolean isParameterInfo() {
        return false;
    }
    
    static final class ParameterInfo 
            extends InvocationCompletionProposal {
        ParameterInfo(int offset, Declaration dec, 
                Reference producedReference,
                Scope scope, CeylonParseController cpc, 
                boolean namedInvocation) {
            super(offset, "", "show parameters", "", dec, 
                    producedReference, scope, cpc, true, 
                    true, namedInvocation, false, false,
                    null);
        }
        @Override
        boolean isParameterInfo() {
            return true;
        }
        @Override
        public Point getSelection(IDocument document) {
            return null;
        }
        @Override
        public void apply(IDocument document) {}
    }

    static void addFakeShowParametersCompletion(final Node node, 
            final CeylonParseController cpc, 
            final List<ICompletionProposal> result) {
        Tree.CompilationUnit upToDateAndTypeChecked =
                cpc.getTypecheckedRootNode();
        if (upToDateAndTypeChecked == null) {
            return;
        }
        new Visitor() {
            @Override
            public void visit(Tree.InvocationExpression that) {
                Tree.ArgumentList al = 
                        that.getPositionalArgumentList();
                if (al==null) {
                    al = that.getNamedArgumentList();
                }
                if (al!=null) {
                    Integer startIndex = al.getStartIndex();
                    Integer startIndex2 = node.getStartIndex();
                    if (startIndex!=null && startIndex2!=null &&
                            startIndex.intValue()==startIndex2.intValue()) {
                        Tree.Primary primary = that.getPrimary();
                        if (primary instanceof Tree.MemberOrTypeExpression) {
                            Tree.MemberOrTypeExpression mte = 
                                    (Tree.MemberOrTypeExpression) 
                                        primary;
                            if (mte.getDeclaration()!=null && 
                                    mte.getTarget()!=null) {
                                result.add(new ParameterInfo(
                                        al.getStartIndex(),
                                        mte.getDeclaration(), 
                                        mte.getTarget(), 
                                        node.getScope(), cpc,
                                        al instanceof Tree.NamedArgumentList));
                            }
                        }
                    }
                }
                super.visit(that);
            }
        }.visit(upToDateAndTypeChecked);
    }
    
    static final class ParameterContextInformation 
            implements IContextInformation {
        
        private final Declaration declaration;
        private final Reference producedReference;
        private final ParameterList parameterList;
        private final int argumentListOffset;
        private final Unit unit;
        private final boolean includeDefaulted;
//        private final boolean inLinkedMode;
        private final boolean namedInvocation;
        
        ParameterContextInformation(
                Declaration declaration,
                Reference producedReference, Unit unit,
                ParameterList parameterList, 
                int argumentListOffset, 
                boolean includeDefaulted, 
                boolean namedInvocation) {
//                boolean inLinkedMode
            this.declaration = declaration;
            this.producedReference = producedReference;
            this.unit = unit;
            this.parameterList = parameterList;
            this.argumentListOffset = argumentListOffset;
            this.includeDefaulted = includeDefaulted;
//            this.inLinkedMode = inLinkedMode;
            this.namedInvocation = namedInvocation;
        }
        
        @Override
        public String getContextDisplayString() {
            return "Parameters of '" + declaration.getName() + "'";
        }
        
        @Override
        public Image getImage() {
            return getImageForDeclaration(declaration);
        }
        
        @Override
        public String getInformationDisplayString() {
            List<Parameter> ps = 
                    getParameters(parameterList, 
                            includeDefaulted, 
                            namedInvocation);
            if (ps.isEmpty()) {
                return "no parameters";
            }
            StringBuilder result = new StringBuilder();
            for (Parameter p: ps) {
                boolean isListedValues = 
                        namedInvocation && 
                        p==ps.get(ps.size()-1) &&
                        p.getModel() instanceof Value && 
                        p.getType()!=null &&
                        unit.isIterableParameterType(
                                p.getType());
                if (includeDefaulted || !p.isDefaulted() ||
                        isListedValues) {
                    if (producedReference==null) {
                        result.append(p.getName());
                    }
                    else {
                        TypedReference pr = 
                                producedReference.getTypedParameter(p);
                        appendParameterContextInfo(
                                result, pr, p, unit, 
                                namedInvocation, 
                                isListedValues);
                    }
                    if (!isListedValues) {
                        result.append(namedInvocation ? "; " : ", ");
                    }
                }
            }
            if (!namedInvocation && result.length()>0) {
                result.setLength(result.length()-2);
            }
            return result.toString();
        }
        
        @Override
        public boolean equals(Object that) {
            if (that instanceof ParameterContextInformation) {
                ParameterContextInformation pci = 
                        (ParameterContextInformation) that;
                return pci.declaration.equals(declaration);
            }
            else {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
        	return declaration.hashCode();
        }
        
        int getArgumentListOffset() {
            return argumentListOffset;
        }
        
    }
    
}
