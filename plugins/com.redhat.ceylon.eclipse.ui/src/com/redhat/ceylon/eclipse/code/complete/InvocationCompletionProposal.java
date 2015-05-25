package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.model.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendParameterContextInfo;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getNamedInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getNamedInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getPositionalInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getPositionalInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getParameters;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getSortedProposedValues;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleClass;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleMethod;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleType;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleValue;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isInBounds;
import static com.redhat.ceylon.eclipse.code.complete.ParameterContextValidator.findCharCount;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importCallableParameterParamTypes;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importDeclaration;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.CHAIN_LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INEXACT_MATCHES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.LinkedMode.addLinkedPosition;
import static com.redhat.ceylon.eclipse.util.LinkedMode.installLinkedMode;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.CLASS_ALIAS;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.SATISFIES;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.TYPE_ALIAS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
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

import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Generic;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.Method;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.NothingType;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.ProducedReference;
import com.redhat.ceylon.model.typechecker.model.ProducedType;
import com.redhat.ceylon.model.typechecker.model.ProducedTypedReference;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.eclipse.util.OccurrenceLocation;

class InvocationCompletionProposal extends CompletionProposal {
    
    static void addProgramElementReferenceProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            Declaration dec, Scope scope, boolean isMember) {
        Unit unit = cpc.getRootNode().getUnit();
        result.add(new InvocationCompletionProposal(offset, prefix,
                dec.getName(unit), escapeName(dec, unit),
                dec, dec.getReference(), scope, cpc, 
                true, false, false, isMember, null));
    }
    
    static void addReferenceProposal(int offset, String prefix, 
            final CeylonParseController cpc, List<ICompletionProposal> result, 
            Declaration dec, Scope scope, boolean isMember, 
            ProducedReference pr, OccurrenceLocation ol) {
        Unit unit = cpc.getRootNode().getUnit();
        //proposal with type args
        if (dec instanceof Generic) {
            result.add(new InvocationCompletionProposal(offset, prefix,
                    getDescriptionFor(dec, unit), getTextFor(dec, unit), 
                    dec, pr, scope, cpc, true, false, false, isMember, null));
            if (((Generic) dec).getTypeParameters().isEmpty()) {
                //don't add another proposal below!
                return;
            }
        }
        //proposal without type args
        boolean isAbstract = 
                dec instanceof Class && ((Class) dec).isAbstract() ||
                dec instanceof Interface;
        if ((!isAbstract && 
                ol!=EXTENDS && ol!=SATISFIES && 
                ol!=CLASS_ALIAS && ol!=TYPE_ALIAS)) {
            result.add(new InvocationCompletionProposal(offset, prefix,
                    dec.getName(unit), escapeName(dec, unit), 
                    dec, pr, scope, cpc, true, false, false, isMember, null));
        }
    }
    
    static void addSecondLevelProposal(int offset, String prefix, 
            final CeylonParseController cpc, List<ICompletionProposal> result, 
            Declaration dec, Scope scope, boolean isMember, ProducedReference pr,
            ProducedType requiredType, OccurrenceLocation ol) {
        if (!(dec instanceof Functional) && 
            !(dec instanceof TypeDeclaration)) {
            //add qualified member proposals 
            Unit unit = cpc.getRootNode().getUnit();
            ProducedType type = pr.getType();
            if (isTypeUnknown(type)) return;
            Collection<DeclarationWithProximity> members = 
                    type.getDeclaration().getMatchingMemberDeclarations(unit, scope, "", 0).values();
            for (DeclarationWithProximity ndwp: members) {
                final Declaration m = ndwp.getDeclaration();
                if (m instanceof TypedDeclaration) { //TODO: member Class would also be useful! 
                    final ProducedTypedReference ptr = 
                            type.getTypedMember((TypedDeclaration) m, 
                                    Collections.<ProducedType>emptyList());
                    ProducedType mt = ptr.getType();
                    if (mt!=null && 
                            (requiredType==null || mt.isSubtypeOf(requiredType))) {
                        result.add(new InvocationCompletionProposal(offset, prefix,
                                dec.getName() + "." + getPositionalInvocationDescriptionFor(m, ol, ptr, unit, false, null), 
                                dec.getName() + "." + getPositionalInvocationTextFor(m, ol, ptr, unit, false, null), 
                                m, ptr, scope, cpc, true, true, false, true, dec));
                    }
                }
            }
        }
    }
    
    static void addInvocationProposals(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            Declaration dec, ProducedReference pr, Scope scope,
            OccurrenceLocation ol, String typeArgs, boolean isMember) {
        if (dec instanceof Functional) {
            Unit unit = cpc.getRootNode().getUnit();
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
                if (positional && 
                        parameterList.isPositionalParametersSupported() &&
                        (!isAbstract || 
                                ol==EXTENDS || ol==CLASS_ALIAS)) {
                    List<Parameter> parameters = 
                            getParameters(parameterList, false, false);
                    if (ps.size()!=parameters.size()) {
                        result.add(new InvocationCompletionProposal(offset, prefix, 
                                getPositionalInvocationDescriptionFor(dec, ol, pr, unit, false, typeArgs), 
                                getPositionalInvocationTextFor(dec, ol, pr, unit, false, typeArgs), dec,
                                pr, scope, cpc, false, true, false, isMember, null));
                    }
                    result.add(new InvocationCompletionProposal(offset, prefix, 
                            getPositionalInvocationDescriptionFor(dec, ol, pr, unit, true, typeArgs), 
                            getPositionalInvocationTextFor(dec, ol, pr, unit, true, typeArgs), dec,
                            pr, scope, cpc, true, true, false, isMember, null));
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
                        result.add(new InvocationCompletionProposal(offset, prefix, 
                                getNamedInvocationDescriptionFor(dec, pr, unit, false, typeArgs), 
                                getNamedInvocationTextFor(dec, pr, unit, false, typeArgs), dec,
                                pr, scope, cpc, false, false, true, isMember, null));
                    }
                    if (!ps.isEmpty()) {
                        result.add(new InvocationCompletionProposal(offset, prefix, 
                                getNamedInvocationDescriptionFor(dec, pr, unit, true, typeArgs), 
                                getNamedInvocationTextFor(dec, pr, unit, true, typeArgs), dec,
                                pr, scope, cpc, true, false, true, isMember, null));
                    }
                }
            }
        }
    }

    private static String prefixWithoutTypeArgs(String prefix, String typeArgs) {
        if (typeArgs==null) {
            return prefix;
        }
        else {
            return prefix.substring(0, 
                    prefix.length()-typeArgs.length());
        }
    }
    
    final class NestedCompletionProposal implements ICompletionProposal, 
            ICompletionProposalExtension2, ICompletionProposalExtension6 {
        private final String op;
        private final int loc;
        private final int index;
        private final boolean basic;
        private final Declaration dec;
        private Declaration qualifier;
        
        NestedCompletionProposal(Declaration dec, Declaration qualifier, 
                int loc, int index, boolean basic, String op) {
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
                IRegion li = 
                        document.getLineInformationOfOffset(loc);
                int endOfLine = li.getOffset() + li.getLength();
                int startOfArgs = getFirstPosition();
                int offset = findCharCount(index, document, 
                        loc+startOfArgs, endOfLine, 
                        ",;", "", true)+1;
                if (offset>0 && document.getChar(offset)==' ') {
                    offset++;
                }
                int nextOffset = findCharCount(index+1, document, 
                        loc+startOfArgs, endOfLine, 
                        ",;", "", true);
                int middleOffset = findCharCount(1, document, 
                        offset, nextOffset, 
                        "=", "", true)+1;
                if (middleOffset>0 &&
                        document.getChar(middleOffset)=='>') {
                    middleOffset++;
                }
                while (middleOffset>0 &&
                        document.getChar(middleOffset)==' ') {
                    middleOffset++;
                }
                if (middleOffset>offset &&
                        middleOffset<nextOffset) {
                    offset = middleOffset;
                }
                String str = getText(false);
                if (nextOffset==-1) {
                    nextOffset = offset;
                }
                if (document.getChar(nextOffset)=='}') {
                    str += " ";
                }
                document.replace(offset, nextOffset-offset, str);
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
                            MethodOrValue pm = p.getModel();
                            if (pm instanceof Method) {
                                for (ParameterList ppl: 
                                        ((Method) pm).getParameterLists()) {
                                    for (Parameter pp: ppl.getParameters()) {
                                        importSignatureTypes(pp.getModel(), cu, decs);
                                    }
                                }
                            }
                        }
                    }
                    
                }
                applyImports(tc, decs, cu, document);
                tc.perform(new NullProgressMonitor());
            }
            catch (Exception e) {
                e.printStackTrace();
            }*/
        }

        private String getText(boolean description) {
            StringBuilder sb = new StringBuilder().append(op);
            if (qualifier!=null) {
                sb.append(qualifier.getName(getUnit())).append('.');
            }
            sb.append(dec.getName(getUnit()));
            if (dec instanceof Functional && !basic) {
                appendPositionalArgs(dec, getUnit(), sb, 
                        false, description);
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
            Highlights.styleProposal(result, 
                    getDisplayString(), false);
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
        public boolean validate(IDocument document, int currentOffset,
                DocumentEvent event) {
            if (event==null) {
                return true;
            }
            else {
                try {
                    IRegion li = 
                            document.getLineInformationOfOffset(loc);
                    int endOfLine = li.getOffset() + li.getLength();
                    int startOfArgs = getFirstPosition();
                    int offset = 
                            findCharCount(index, document, 
                                    loc+startOfArgs, endOfLine, 
                                    ",;", "", true)+1;
                    String content = 
                            document.get(offset, 
                                    currentOffset-offset);
                    int fat = content.indexOf("=>");
                    if (fat>0) {
                        content = content.substring(fat+2);
                    }
                    int eq = content.indexOf("=");
                    if (eq>0) {
                        content = content.substring(eq+1);
                    }
                    String filter = content.trim().toLowerCase();
                    String decName = dec.getName(getUnit()).toLowerCase();
                    if ((op+decName).startsWith(filter) ||
                            decName.startsWith(filter)) {
                        return true;
                    }
                    if (qualifier!=null) {
                        String qualName = qualifier.getName(getUnit()).toLowerCase();
                        if ((op+qualName+'.'+decName).startsWith(filter) ||
                                (qualName+'.'+decName).startsWith(filter)) {
                            return true;
                        }
                    }
                }
                catch (BadLocationException e) {
                    // ignore concurrently modified document
                }
                return false;
            }
        }
    }

    final class NestedLiteralCompletionProposal 
            implements ICompletionProposal, 
                       ICompletionProposalExtension2 {
        
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
                IRegion li = 
                        document.getLineInformationOfOffset(loc);
                int endOfLine = li.getOffset() + li.getLength();
                int startOfArgs = getFirstPosition();
                int offset = 
                        findCharCount(index, document, 
                                loc+startOfArgs, endOfLine, 
                                ",;", "", true)+1;
                if (offset>0 && 
                        document.getChar(offset)==' ') {
                    offset++;
                }
                int nextOffset = 
                        findCharCount(index+1, document, 
                                loc+startOfArgs, endOfLine, 
                                ",;", "", true);
                int middleOffset = findCharCount(1, document, 
                        offset, nextOffset, 
                        "=", "", true)+1;
                if (middleOffset>0 &&
                        document.getChar(middleOffset)=='>') {
                    middleOffset++;
                }
                while (middleOffset>0 &&
                        document.getChar(middleOffset)==' ') {
                    middleOffset++;
                }
                if (middleOffset>offset &&
                        middleOffset<nextOffset) {
                    offset = middleOffset;
                }
                String str = value;
                if (nextOffset==-1) {
                    nextOffset = offset;
                }
                if (document.getChar(nextOffset)=='}') {
                    str += " ";
                }
                document.replace(offset, 
                        nextOffset-offset, str);
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
        public boolean validate(IDocument document, int currentOffset,
                DocumentEvent event) {
            if (event==null) {
                return true;
            }
            else {
                try {
                    IRegion li = 
                            document.getLineInformationOfOffset(loc);
                    int endOfLine = li.getOffset() + li.getLength();
                    int startOfArgs = getFirstPosition();
                    int offset = 
                            findCharCount(index, document, 
                                    loc+startOfArgs, endOfLine, 
                                    ",;", "", true)+1;
                    String content = 
                            document.get(offset, 
                                    currentOffset-offset);
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
    private final ProducedReference producedReference;
    private final Scope scope;
    private final boolean includeDefaulted;
    private final boolean namedInvocation;
    private final boolean positionalInvocation;
    private final boolean qualified;
    private Declaration qualifyingValue;
    
    private InvocationCompletionProposal(int offset, String prefix, 
            String desc, String text, Declaration dec,
            ProducedReference producedReference, Scope scope, 
            CeylonParseController cpc, boolean includeDefaulted,
            boolean positionalInvocation, boolean namedInvocation, 
            boolean qualified, Declaration qualifyingValue) {
        super(offset, prefix, getImageForDeclaration(dec), 
                desc, text);
        this.cpc = cpc;
        this.declaration = dec;
        this.producedReference = producedReference;
        this.scope = scope;
        this.includeDefaulted = includeDefaulted;
        this.namedInvocation = namedInvocation;
        this.positionalInvocation = positionalInvocation;
        this.qualified = qualified;
        this.qualifyingValue = qualifyingValue;
    }

    private Unit getUnit() {
        return cpc.getRootNode().getUnit();
    }

    private DocumentChange createChange(IDocument document)
            throws BadLocationException {
        DocumentChange change = 
                new DocumentChange("Complete Invocation", document);
        change.setEdit(new MultiTextEdit());
        HashSet<Declaration> decs = new HashSet<Declaration>();
        Tree.CompilationUnit cu = cpc.getRootNode();
        if (qualifyingValue!=null) {
            importDeclaration(decs, qualifyingValue, cu);
        }
        if (!qualified) {
            importDeclaration(decs, declaration, cu);
        }
        if (positionalInvocation||namedInvocation) {
            importCallableParameterParamTypes(declaration, decs, cu);
        }
        int il=applyImports(change, decs, cu, document);
        change.addEdit(createEdit(document));
        offset+=il;
        return change;
    }

    @Override
    public void apply(IDocument document) {
        try {
            createChange(document).perform(new NullProgressMonitor());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (getPreferences().getBoolean(LINKED_MODE_ARGUMENTS)) {
            activeLinkedMode(document);
        }
    }

    private void activeLinkedMode(IDocument document) {
        if (declaration instanceof Generic) {
            Generic generic = (Generic) declaration;
            ParameterList paramList = null;
            if (declaration instanceof Functional && 
                    (positionalInvocation||namedInvocation)) {
                List<ParameterList> pls = 
                        ((Functional) declaration).getParameterLists();
                if (!pls.isEmpty() && 
                        !pls.get(0).getParameters().isEmpty()) {
                    paramList = pls.get(0);
                }
            }
            if (paramList!=null) {
                List<Parameter> params = 
                        getParameters(paramList, 
                                includeDefaulted, namedInvocation);
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
            if (document.get(start, len).trim().equals("{}")) {
                start++;
                len=0;
            }
        } catch (BadLocationException e) {}
        return new Point(start, len);
    }
    
    protected int getCompletionPosition(int first, int next) {
        return text.substring(first, first+next-1).lastIndexOf(' ')+1;
    }

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
            comma = findCharCount(1, document, start, end, 
                    ",;", "", true) - start;
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
    
    public String getAdditionalProposalInfo() {
        return getDocumentationFor(cpc, declaration, 
                producedReference);    
    }
    
    public void enterLinkedMode(IDocument document, 
            List<Parameter> params, 
            List<TypeParameter> typeParams) {
        boolean proposeTypeArguments = params==null;
        int paramCount = proposeTypeArguments ? 
                typeParams.size() : params.size();
        if (paramCount==0) return;
        try {
            final int loc = offset-prefix.length();
            int first = getFirstPosition();
            if (first<=0) return; //no arg list
            int next = getNextPosition(document, first);
            if (next<=0) return; //empty arg list
            LinkedModeModel linkedModeModel = new LinkedModeModel();
            int seq=0, param=0;
            while (next>0 && param<paramCount) {
                boolean voidParam = !proposeTypeArguments &&
                        params.get(param).isDeclaredVoid();
                if (proposeTypeArguments || positionalInvocation ||
                        //don't create linked positions for
                        //void callable parameters in named
                        //argument lists
                        !voidParam) {
                    List<ICompletionProposal> props = 
                            new ArrayList<ICompletionProposal>();
                    if (proposeTypeArguments) {
                        addTypeArgumentProposals(typeParams.get(seq), 
                                loc, first, props, seq);
                    }
                    else if (!voidParam) {
                        addValueArgumentProposals(params.get(param), 
                                loc, first, props, seq, 
                                param==params.size()-1);
                    }
                    int middle = getCompletionPosition(first, next);
                    int start = loc+first+middle;
                    int len = next-middle;
                    if (voidParam) {
                        start++;
                        len=0;
                    }
                    ProposalPosition linkedPosition = 
                            new ProposalPosition(document, start, len, seq, 
                                    props.toArray(NO_COMPLETIONS));
                    addLinkedPosition(linkedModeModel, linkedPosition);
                    first = first+next+1;
                    next = getNextPosition(document, first);
                    seq++;
                }
                param++; 
            }
            if (seq>0) {
                installLinkedMode((CeylonEditor) getCurrentEditor(), 
                        document, linkedModeModel, this, 
                        new LinkedMode.NullExitPolicy(),
                        seq, loc+text.length());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addValueArgumentProposals(Parameter p, final int loc,
            int first, List<ICompletionProposal> props, int index,
            boolean last) {
        if (p.getModel().isDynamicallyTyped()) {
            return;
        }
        ProducedType type = 
                producedReference.getTypedParameter(p).getType();
        if (type==null) return;
        Unit unit = getUnit();
        List<DeclarationWithProximity> proposals = 
                getSortedProposedValues(scope, unit);
        for (DeclarationWithProximity dwp: proposals) {
            if (dwp.getProximity()<=1) {
                addValueArgumentProposal(p, loc, props, index, last,
                        type, unit, dwp, null);
            }
        }
        addLiteralProposals(loc, props, index, type, unit);
        for (DeclarationWithProximity dwp: proposals) {
            if (dwp.getProximity()>1) {
                addValueArgumentProposal(p, loc, props, index, last,
                        type, unit, dwp, null);
            }
        }
    }

    private void addValueArgumentProposal(Parameter p, final int loc,
            List<ICompletionProposal> props, int index, boolean last,
            ProducedType type, Unit unit, DeclarationWithProximity dwp,
            DeclarationWithProximity qualifier) {
        if (qualifier==null && dwp.isUnimported()) {
            return;
        }
        TypeDeclaration td = type.getDeclaration();
        Declaration d = dwp.getDeclaration();
        String pname = d.getUnit().getPackage().getNameAsString();
        boolean isInLanguageModule = qualifier==null &&
                pname.equals(Module.LANGUAGE_MODULE_NAME);
        Declaration qdec = qualifier==null ? 
                null : qualifier.getDeclaration();
        if (d instanceof Value) {
            Value value = (Value) d;
            if (isInLanguageModule) {
                if (isIgnoredLanguageModuleValue(value)) {
                    return;
                }
            }
            ProducedType vt = value.getType();
            if (vt!=null && !vt.isNothing()) {
                if (vt.isSubtypeOf(type) ||
                        (td instanceof TypeParameter) && 
                        isInBounds(((TypeParameter) td).getSatisfiedTypes(), vt)) {
                    boolean isIterArg = namedInvocation && last && 
                            unit.isIterableParameterType(type);
                    boolean isVarArg = p.isSequenced() && positionalInvocation;
                    props.add(new NestedCompletionProposal(d, qdec,
                            loc, index, false, isIterArg || isVarArg ? "*" : ""));
                }
                if (qualifier==null && 
                        getPreferences().getBoolean(CHAIN_LINKED_MODE_ARGUMENTS)) {
                    Collection<DeclarationWithProximity> members = 
                            ((Value) d).getTypeDeclaration()
                            .getMatchingMemberDeclarations(unit, scope, "", 0).values();
                    for (DeclarationWithProximity mwp: members) {
                        addValueArgumentProposal(p, loc, props, index, last, type, unit, mwp, dwp);
                    }
                }
            }
        }
        if (d instanceof Method) {
            if (!d.isAnnotation()) {
                Method method = (Method) d;
                if (isInLanguageModule) {
                    if (isIgnoredLanguageModuleMethod(method)) {
                        return;
                    }
                }
                ProducedType mt = method.getType();
                if (mt!=null && !mt.isNothing() &&
                        ((td instanceof TypeParameter) && 
                                isInBounds(((TypeParameter) td).getSatisfiedTypes(), mt) || 
                                mt.isSubtypeOf(type))) {
                    boolean isIterArg = namedInvocation && last && 
                            unit.isIterableParameterType(type);
                    boolean isVarArg = p.isSequenced() && positionalInvocation;
                    props.add(new NestedCompletionProposal(d, qdec,
                            loc, index, false, isIterArg || isVarArg ? "*" : ""));
                }
            }
        }
        if (d instanceof Class) {
            Class clazz = (Class) d;
            if (!clazz.isAbstract() && !d.isAnnotation()) {
                if (isInLanguageModule) {
                    if (isIgnoredLanguageModuleClass(clazz)) {
                        return;
                    }
                }
                ProducedType ct = clazz.getType();
                if (ct!=null && !ct.isNothing() &&
                        ((td instanceof TypeParameter) && 
                                isInBounds(((TypeParameter) td).getSatisfiedTypes(), ct) || 
                                ct.getDeclaration().equals(type.getDeclaration()) ||
                                ct.isSubtypeOf(type))) {
                    boolean isIterArg = namedInvocation && last && 
                            unit.isIterableParameterType(type);
                    boolean isVarArg = p.isSequenced() && positionalInvocation;
                    props.add(new NestedCompletionProposal(d, qdec, 
                            loc, index, false, isIterArg || isVarArg ? "*" : ""));
                }
            }
        }
    }

    private void addLiteralProposals(final int loc,
            List<ICompletionProposal> props, int index, ProducedType type,
            Unit unit) {
        TypeDeclaration dtd = unit.getDefiniteType(type).getDeclaration();
        if (dtd instanceof Class) {
            if (dtd.equals(unit.getIntegerDeclaration())) {
                props.add(new NestedLiteralCompletionProposal("0", loc, index));
                props.add(new NestedLiteralCompletionProposal("1", loc, index));
            }
            if (dtd.equals(unit.getFloatDeclaration())) {
                props.add(new NestedLiteralCompletionProposal("0.0", loc, index));
                props.add(new NestedLiteralCompletionProposal("1.0", loc, index));
            }
            if (dtd.equals(unit.getStringDeclaration())) {
                props.add(new NestedLiteralCompletionProposal("\"\"", loc, index));
            }
            if (dtd.equals(unit.getCharacterDeclaration())) {
                props.add(new NestedLiteralCompletionProposal("' '", loc, index));
                props.add(new NestedLiteralCompletionProposal("'\\n'", loc, index));
                props.add(new NestedLiteralCompletionProposal("'\\t'", loc, index));
            }
        }
        else if (dtd instanceof Interface) {
           if (dtd.equals(unit.getIterableDeclaration())) {
               props.add(new NestedLiteralCompletionProposal("{}", loc, index));
           }
           if (dtd.equals(unit.getSequentialDeclaration()) ||
               dtd.equals(unit.getEmptyDeclaration())) {
               props.add(new NestedLiteralCompletionProposal("[]", loc, index));
           }
        }
    }

    private void addTypeArgumentProposals(TypeParameter tp, 
            final int loc, int first, List<ICompletionProposal> props, 
            final int index) {
        for (DeclarationWithProximity dwp:
                getSortedProposedValues(scope, getUnit())) {
            Declaration d = dwp.getDeclaration();
            if (d instanceof TypeDeclaration && !dwp.isUnimported()) {
                TypeDeclaration td = (TypeDeclaration) d;
                ProducedType t = td.getType();
                if (td.getTypeParameters().isEmpty() && 
                        !td.isAnnotation() &&
                        !(td instanceof NothingType) &&
                        !td.inherits(td.getUnit().getExceptionDeclaration())) {
                    if (td.getUnit().getPackage().getNameAsString()
                            .equals(Module.LANGUAGE_MODULE_NAME)) {
                        if (isIgnoredLanguageModuleType(td)) {
                            continue;
                        }
                    }
                    if (isInBounds(tp.getSatisfiedTypes(), t)) {
                        props.add(new NestedCompletionProposal(d, null,
                                loc, index, true, ""));
                    }
                }
            }
        }
    }

    @Override
    public IContextInformation getContextInformation() {
        if (namedInvocation||positionalInvocation) { //TODO: context info for type arg lists!
            if (declaration instanceof Functional) {
                List<ParameterList> pls = 
                        ((Functional) declaration).getParameterLists();
                if (!pls.isEmpty()) {
                    int argListOffset = isParameterInfo() ? 
                            this.offset : 
                                offset-prefix.length() + 
                                text.indexOf(namedInvocation?'{':'(');
                    return new ParameterContextInformation(declaration, 
                            producedReference, getUnit(), 
                            pls.get(0), argListOffset, includeDefaulted, 
                            namedInvocation /*!isParameterInfo()*/);
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
        private ParameterInfo(int offset, Declaration dec, 
                ProducedReference producedReference,
                Scope scope, CeylonParseController cpc, 
                boolean namedInvocation) {
            super(offset, "", "show parameters", "", dec, 
                    producedReference, scope, cpc, true, 
                    true, namedInvocation, false, null);
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

    static List<IContextInformation> computeParameterContextInformation(final int offset,
            final Tree.CompilationUnit rootNode, final ITextViewer viewer) {
        final List<IContextInformation> infos = 
                new ArrayList<IContextInformation>();
        rootNode.visit(new Visitor() {
            @Override
            public void visit(Tree.InvocationExpression that) {
                Tree.ArgumentList al = 
                        that.getPositionalArgumentList();
                if (al==null) {
                    al = that.getNamedArgumentList();
                }
                if (al!=null) {
                    //TODO: should reuse logic for adjusting tokens
                    //      from CeylonContentProposer!!
                    Integer start = al.getStartIndex();
                    Integer stop = al.getStopIndex();
                    if (start!=null && stop!=null && offset>start) { 
                        String string = "";
                        if (offset>stop) {
                            try {
                                string = viewer.getDocument()
                                        .get(stop+1, offset-stop-1);
                            } 
                            catch (BadLocationException e) {}
                        }
                        if (string.trim().isEmpty()) {
                            Tree.MemberOrTypeExpression mte = 
                                    (Tree.MemberOrTypeExpression) that.getPrimary();
                            Declaration declaration = mte.getDeclaration();
                            if (declaration instanceof Functional) {
                                List<ParameterList> pls = 
                                        ((Functional) declaration).getParameterLists();
                                if (!pls.isEmpty()) {
                                    //Note: This line suppresses the little menu 
                                    //      that gives me a choice of context infos.
                                    //      Delete it to get a choice of all surrounding
                                    //      argument lists.
                                    infos.clear();
                                    infos.add(new ParameterContextInformation(declaration, 
                                            mte.getTarget(), rootNode.getUnit(), 
                                            pls.get(0), al.getStartIndex(), 
                                            true, al instanceof Tree.NamedArgumentList /*false*/));
                                }
                            }
                        }
                    }
                }
                super.visit(that);
            }
        });
        return infos;
    }
    
    static void addFakeShowParametersCompletion(final Node node, 
            final CeylonParseController cpc, 
            final List<ICompletionProposal> result) {
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
                                    (Tree.MemberOrTypeExpression) primary;
                            if (mte.getDeclaration()!=null && mte.getTarget()!=null) {
                                result.add(new ParameterInfo(al.getStartIndex(),
                                        mte.getDeclaration(), mte.getTarget(), 
                                        node.getScope(), cpc, 
                                        al instanceof Tree.NamedArgumentList));
                            }
                        }
                    }
                }
                super.visit(that);
            }
        }.visit(cpc.getRootNode());
    }
    
    static final class ParameterContextInformation 
            implements IContextInformation {
        
        private final Declaration declaration;
        private final ProducedReference producedReference;
        private final ParameterList parameterList;
        private final int argumentListOffset;
        private final Unit unit;
        private final boolean includeDefaulted;
//        private final boolean inLinkedMode;
        private final boolean namedInvocation;
        
        private ParameterContextInformation(Declaration declaration,
                ProducedReference producedReference, Unit unit,
                ParameterList parameterList, int argumentListOffset, 
                boolean includeDefaulted, boolean namedInvocation) {
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
            List<Parameter> ps = getParameters(parameterList, 
                    includeDefaulted, namedInvocation);
            if (ps.isEmpty()) {
                return "no parameters";
            }
            StringBuilder result = new StringBuilder();
            for (Parameter p: ps) {
                boolean isListedValues = namedInvocation && 
                        p==ps.get(ps.size()-1) &&
                        p.getModel() instanceof Value && 
                        p.getType()!=null &&
                        unit.isIterableParameterType(p.getType());
                if (includeDefaulted || !p.isDefaulted() ||
                        isListedValues) {
                    if (producedReference==null) {
                        result.append(p.getName());
                    }
                    else {
                        ProducedTypedReference pr = 
                                producedReference.getTypedParameter(p);
                        appendParameterContextInfo(result, pr, p, unit, 
                                namedInvocation, isListedValues);
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
        
        int getArgumentListOffset() {
            return argumentListOffset;
        }
        
    }
    
}