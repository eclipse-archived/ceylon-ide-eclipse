package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.LARGE_CORRECTION_IMAGE;
import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.anonFunctionHeader;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getAssignableLiterals;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getCurrentArgumentRegion;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getProposedName;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getSortedProposedValues;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleClass;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleMethod;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleValue;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isInBounds;
import static com.redhat.ceylon.eclipse.code.complete.ParameterContextValidator.findCharCount;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.CHAIN_LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_ARGUMENTS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMETER_TYPES_IN_COMPLETIONS;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static com.redhat.ceylon.eclipse.util.LinkedMode.addLinkedPosition;
import static com.redhat.ceylon.eclipse.util.LinkedMode.installLinkedMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;

class ParametersCompletionProposal extends CompletionProposal {
    
    //TODO: this is a big copy/paste from 
    //      InvocationCompletionProposal.NestedCompletionProposal
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
                tc.perform(new NullProgressMonitor());
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
                }
                return false;
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
                    CeylonPlugin.getCompletionFont());
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
        public boolean validate(IDocument document, int currentOffset,
                DocumentEvent event) {
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
    
    private final Unit unit;
    private final List<Type> argTypes;
    private final Scope scope;
    
    ParametersCompletionProposal(int offset, 
            String desc, String text, 
            List<Type> argTypes, 
            Scope scope, 
            Unit unit) {
        super(offset, "", LARGE_CORRECTION_IMAGE, 
                desc, text);
        this.unit = unit;
        this.scope = scope;
        this.argTypes = argTypes;
    }

    private Unit getUnit() {
        return unit;
    }

    private DocumentChange createChange(IDocument document)
            throws BadLocationException {
        DocumentChange change = 
                new DocumentChange("Complete Invocation", document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(createEdit(document));
        return change;
    }

    @Override
    public void apply(IDocument document) {
        try {
            EditorUtil.performChange(createChange(document));
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (getPreferences()
                .getBoolean(LINKED_MODE_ARGUMENTS)) {
            enterLinkedMode(document);
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
        return 1;
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
            int index = text.lastIndexOf(')');
            return index - lastOffset;
        }
        return comma;
    }
    
    public String getAdditionalProposalInfo() {
        return null;    
    }
    
    public void enterLinkedMode(IDocument document) {
        int paramCount = argTypes.size();
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
                    List<ICompletionProposal> props = 
                            new ArrayList<ICompletionProposal>();
                    addValueArgumentProposals(
                            argTypes.get(seq), 
                            loc, first, props, seq);
                    int middle = 
                            getCompletionPosition(first, next);
                    int start = loc+first+middle;
                    int len = next-middle;
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

    private void addValueArgumentProposals(
            Type type, final int loc, int first, 
            List<ICompletionProposal> props, 
            int index) {
        if (type==null) {
            return;
        }
        Unit unit = getUnit();
        List<DeclarationWithProximity> proposals = 
                getSortedProposedValues(scope, unit);
        for (DeclarationWithProximity dwp: proposals) {
            if (dwp.getProximity()<=1) {
                addValueArgumentProposal(loc, props, index, 
                        type, unit, dwp, null);
            }
        }
        for (String value: 
                getAssignableLiterals(type, unit)) {
            props.add(new NestedLiteralCompletionProposal(
                        value, loc, index));
        }
        for (DeclarationWithProximity dwp: proposals) {
            if (dwp.getProximity()>1) {
                addValueArgumentProposal(loc, props, index,
                        type, unit, dwp, null);
            }
        }
    }

    private void addValueArgumentProposal(final int loc,
            List<ICompletionProposal> props, 
            int index, Type type, Unit unit, 
            DeclarationWithProximity dwp,
            DeclarationWithProximity qualifier) {
        if (qualifier==null && dwp.isUnimported()) {
            return;
        }
        TypeDeclaration td = type.getDeclaration();
        Declaration d = dwp.getDeclaration();
        String pname = 
                d.getUnit()
                    .getPackage()
                    .getNameAsString();
        boolean isInLanguageModule = 
                qualifier==null &&
                    pname.equals(Module.LANGUAGE_MODULE_NAME);
        Declaration qdec = 
                qualifier==null ? null : 
                    qualifier.getDeclaration();
        if (d instanceof Value) {
            Value value = (Value) d;
            if (isInLanguageModule) {
                if (isIgnoredLanguageModuleValue(value)) {
                    return;
                }
            }
            Type vt = value.getType();
            if (vt!=null && !vt.isNothing()) {
                if (vt.isSubtypeOf(type) ||
                        withinBounds(td, vt)) {
//                    boolean isIterArg = namedInvocation && last && 
//                            unit.isIterableParameterType(type);
//                    boolean isVarArg = p.isSequenced() && positionalInvocation;
                    props.add(new NestedCompletionProposal(
                            d, qdec, loc, index, false, 
                            /*isIterArg || isVarArg ? "*" :*/ 
                            ""));
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
                        addValueArgumentProposal(loc, props, 
                                index, type, unit, mwp, dwp);
                    }
                }
            }
        }
        if (d instanceof Function) {
            if (!d.isAnnotation()) {
                Function method = (Function) d;
                if (isInLanguageModule) {
                    if (isIgnoredLanguageModuleMethod(method)) {
                        return;
                    }
                }
                Type mt = method.getType();
                if (mt!=null && !mt.isNothing() &&
                        (withinBounds(td, mt) || 
                                mt.isSubtypeOf(type))) {
//                    boolean isIterArg = namedInvocation && last && 
//                            unit.isIterableParameterType(type);
//                    boolean isVarArg = p.isSequenced() && positionalInvocation;
                    props.add(new NestedCompletionProposal(
                            d, qdec, loc, index, false, 
                            /*isIterArg || isVarArg ? "*" :*/ 
                            ""));
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
                Type ct = clazz.getType();
                if (ct!=null && !ct.isNothing() &&
                        (withinBounds(td, ct) || 
                                ct.getDeclaration()
                                    .equals(type.getDeclaration()) ||
                                ct.isSubtypeOf(type))) {
//                    boolean isIterArg = namedInvocation && last && 
//                            unit.isIterableParameterType(type);
//                    boolean isVarArg = p.isSequenced() && positionalInvocation;
                    props.add(new NestedCompletionProposal(
                            d, qdec, loc, index, false, 
                            /*isIterArg || isVarArg ? "*" :*/ 
                            ""));
                }
            }
        }
    }

    public boolean withinBounds(TypeDeclaration td, Type t) {
        if (td instanceof TypeParameter) {
            TypeParameter tp = (TypeParameter) td;
            return isInBounds(tp.getSatisfiedTypes(), t);
        }
        else {
            return false;
        }
    }
    
    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
    boolean isParameterInfo() {
        return false;
    }

    static void addParametersProposal(
            final int offset, Node node,
            final List<ICompletionProposal> result) {
        if (!(node instanceof Tree.StaticMemberOrTypeExpression) || 
                !(((Tree.StaticMemberOrTypeExpression) node)
                        .getDeclaration() 
                            instanceof Functional)) {
            Tree.Term term = (Tree.Term) node;
            Type type = term.getTypeModel();
            Unit unit = node.getUnit();
            if (type!=null) {
                TypeDeclaration td = type.getDeclaration();
                Interface cd = unit.getCallableDeclaration();
                if (type.isClassOrInterface() &&
                        td.equals(cd)) {
                    final List<Type> argTypes = 
                            unit.getCallableArgumentTypes(type);
                    boolean paramTypes = 
                            getPreferences()
                                .getBoolean(PARAMETER_TYPES_IN_COMPLETIONS);
                    final StringBuilder desc = new StringBuilder();
                    final StringBuilder text = new StringBuilder();
                    desc.append('(');
                    text.append('(');
                    for (int i = 0; i < argTypes.size(); i++) {
                        Type argType = argTypes.get(i);
                        if (desc.length()>1) desc.append(", ");
                        if (text.length()>1) text.append(", ");
                        if (argType.isClassOrInterface() &&
                                argType.getDeclaration()
                                    .equals(cd)) {
                            String anon = 
                                    anonFunctionHeader(argType, unit);
                            text.append(anon)
                                .append(" => ");
                            desc.append(anon)
                                .append(" => ");
                            argType = unit.getCallableReturnType(argType);
                            argTypes.set(i, argType);
                        }
                        else if (paramTypes) {
                            desc.append(argType.asString(unit))
                                .append(' ');
                        }
                        String name;
                        if (argType.isClassOrInterface() ||
                            argType.isTypeParameter()) {
                            String n = argType.getDeclaration()
                                    .getName(unit);
                            name = Escaping.toInitialLowercase(n);
                        }
                        else {
                            name = "it";
                        }
                        text.append(name);
                        desc.append(name);
                    }
                    text.append(')');
                    desc.append(')');
                    result.add(new ParametersCompletionProposal(offset, 
                            desc.toString(), text.toString(), 
                            argTypes, node.getScope(), unit));
                }
            }
        }
    }
    
}