package com.redhat.ceylon.eclipse.imp.proposals;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MEMBER_OP;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.UIDENTIFIER;
import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getCompilationUnit;
import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.imp.hover.CeylonDocumentationProvider.getDocumentation;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.getTokenIndexAtCharacter;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonTokenColorer.keywords;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.imp.editor.SourceProposal;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IContentProposer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.ImportList;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SimpleType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.ICeylonResources;

public class CeylonContentProposer implements IContentProposer {
    
    private static Image DEFAULT_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(ICeylonResources.CEYLON_DEFAULT_REFINEMENT);
    private static Image FORMAL_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(ICeylonResources.CEYLON_FORMAL_REFINEMENT);
    
    
    /**
     * Returns an array of content proposals applicable relative to the AST of the given
     * parse controller at the given position.
     * 
     * (The provided ITextViewer is not used in the default implementation provided here
     * but but is stipulated by the IContentProposer interface for purposes such as accessing
     * the IDocument for which content proposals are sought.)
     * 
     * @param controller  A parse controller from which the AST of the document being edited
     *             can be obtained
     * @param int      The offset for which content proposals are sought
     * @param viewer    The viewer in which the document represented by the AST in the given
     *             parse controller is being displayed (may be null for some implementations)
     * @return        An array of completion proposals applicable relative to the AST of the given
     *             parse controller at the given position
     */
    public ICompletionProposal[] getContentProposals(IParseController controller,
            final int offset, ITextViewer viewer) {
        CeylonParseController cpc = (CeylonParseController) controller;
        
        //BEGIN HUGE BUG WORKAROUND
        //What is going on here is that when I have a list of proposals open
        //and then I type a character, IMP sends us the old syntax tree and
        //doesn't bother to even send us the character I just typed, except
        //in the ITextViewer. So we need to do some guessing to figure out
        //that there is a missing character in the token stream and take
        //corrective action. This should be fixed in IMP!
        CommonToken token;
        CommonToken previousToken;
        int index = getTokenIndexAtCharacter(cpc.getTokenStream(), offset-1);
        if (index<0) {
            index = -index;
            previousToken = (CommonToken) cpc.getTokenStream().get(index);
            token = index==cpc.getTokenStream().size()-1 ? 
                    null : (CommonToken) cpc.getTokenStream().get(index+1);
        }
        else {
            previousToken = index==0 ? 
                    null : (CommonToken) cpc.getTokenStream().get(index-1);
            token = (CommonToken) cpc.getTokenStream().get(index);
        }
        char charAtOffset = viewer.getDocument().get().charAt(offset-1);
        Character charInTokenAtOffset = token==null ? 
                null : token.getText().charAt(offset-token.getStartIndex()-1);
        String prefix = "";
        int start = offset;
        int end = offset;
        if (charInTokenAtOffset!=null && 
                charAtOffset==charInTokenAtOffset) {
            if (isIdentifier(token)) {
                prefix = token.getText().substring(0, offset-token.getStartIndex());
                start = token.getStartIndex();
                end = token.getStopIndex();
            }
        } 
        else {
            boolean isIdentifierChar = isJavaIdentifierPart(charAtOffset);
            if (previousToken!=null) {
                if (isIdentifierChar) {
                    if (previousToken.getType()==MEMBER_OP) {
                        prefix = Character.toString(charAtOffset);
                        start = previousToken.getStartIndex();
                        end = previousToken.getStopIndex();
                    }
                    else if (isIdentifier(previousToken)) {
                        prefix = previousToken.getText()+charAtOffset;
                        start = previousToken.getStartIndex();
                        end = previousToken.getStopIndex();
                    }
                    else {
                        prefix = Character.toString(charAtOffset);
                    }
                }
            }
            else if (isIdentifierChar) {
                prefix = Character.toString(charAtOffset);
            }
        }
        //END BUG WORKAROUND
        
        //adjust the token to account for unclosed blocks
        int tokenIndex = getTokenIndexAtCharacter(cpc.getTokenStream(), start);
        if (tokenIndex<0) tokenIndex = -tokenIndex;
        int adjustedStart = start;
        int adjustedEnd = end;
        Token adjustedToken = cpc.getTokenStream().get(tokenIndex); 
        while (--tokenIndex>=0 && adjustedToken.getChannel()==CommonToken.HIDDEN_CHANNEL) {
            adjustedToken = cpc.getTokenStream().get(tokenIndex);
            if (/*adjustedToken.getType()!=CeylonLexer.SEMICOLON &&*/ 
                    adjustedToken.getType()!=CeylonLexer.RBRACE) {
                adjustedStart = ((CommonToken) adjustedToken).getStartIndex();
                adjustedEnd = ((CommonToken) adjustedToken).getStopIndex();
                break;
            }
        }
                
        if (cpc.getRootNode() != null) {
            Node node = findNode(cpc.getRootNode(), adjustedStart, adjustedEnd);
            if (node!=null) {
                Token t = node.getEndToken();
                if (node.getStopIndex()<offset && 
                        (t.getType()==CeylonLexer.RBRACE /*|| t.getType()==CeylonLexer.SEMICOLON*/)) {
                    node = findNode(cpc.getRootNode(), adjustedStart, adjustedEnd+1);
                }
            }
            if (node==null) node = cpc.getRootNode(); //we're in whitespace at the end of the file
            return constructCompletions(offset, prefix, 
                        sortProposals(prefix, getProposals(node, prefix, cpc.getContext())),
                        cpc, node);
        } 
        /*result.add(new ErrorProposal("No proposals available due to syntax errors", 
                 offset));*/
        return null;
        
    }
    
    private static void addPackageCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result) {
        StringBuilder fullPath = new StringBuilder();
        if (path!=null) {
            int ids = path.getIdentifiers().size();
            if (!prefix.isEmpty()) ids--; //when the path does not end in a .
            for (int i=0; i<ids; i++) {
                fullPath.append(path.getIdentifiers().get(i).getText()).append('.');
            }
        }
        int len = fullPath.length();
        fullPath.append(prefix);
        //TODO: someday it would be nice to propose from all packages 
        //      and auto-add the module dependency!
        /*TypeChecker tc = CeylonBuilder.getProjectTypeChecker(cpc.getProject().getRawProject());
      if (tc!=null) {
        for (Module m: tc.getContext().getModules().getListOfModules()) {*/
        for (Module m: node.getUnit().getPackage().getModule().getDependencies()) {
            for (Package p: m.getAllPackages()) {
                if (p.getQualifiedNameString().startsWith(fullPath.toString())) {
                    boolean already = false;
                    for (ImportList il: node.getUnit().getImportLists()) {
                        if (il.getImportedPackage()==p) {
                            already = true;
                            break;
                        }
                    }
                    if (!already) {
                        result.add(sourceProposal(offset, prefix, CeylonLabelProvider.PACKAGE, 
                                "[" + p.getQualifiedNameString() + "]", p.getQualifiedNameString(), 
                                p.getQualifiedNameString().substring(len), false));
                    }
                }
            }
        }
    }
    
    private static boolean isIdentifier(Token token) {
        return token.getType()==LIDENTIFIER || 
                token.getType()==UIDENTIFIER;
    }
    
    private static ICompletionProposal[] constructCompletions(int offset, String prefix, 
            Set<DeclarationWithProximity> set, CeylonParseController cpc, Node node) {
        List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
        if (node instanceof Tree.Import) {
            addPackageCompletions(cpc, offset, prefix, null, node, result);
        }
        else if (node instanceof Tree.ImportPath) {
            addPackageCompletions(cpc, offset, prefix, (Tree.ImportPath) node, node, result);
        }
        else if (node instanceof Tree.TypedDeclaration) {
            addMemberNameProposal(offset, prefix, node, result);
        }
        else {
            boolean inImport = node.getScope() instanceof ImportList;
            boolean isQualified = node instanceof Tree.QualifiedMemberOrTypeExpression;
            if (!inImport && !isQualified) {
                addKeywordProposals(offset, prefix, result);
            }
            for (final DeclarationWithProximity dwp: set) {
                Declaration d = dwp.getDeclaration();
                addBasicProposal(offset, prefix, cpc, result, inImport, dwp, d);
                if (!inImport) {
                    if (d instanceof Functional) {
                        addInvocationProposals(offset, prefix, cpc, result, dwp, d);
                    }
                    if (d instanceof MethodOrValue || d instanceof Class) {
                        addRefinementProposal(offset, prefix, cpc, node, result, d);
                    }
                }
            }
        }
        return result.toArray(new ICompletionProposal[result.size()]);
    }

    private static void addRefinementProposal(int offset, String prefix, CeylonParseController cpc,
            Node node, List<ICompletionProposal> result, Declaration d) {
        if (node.getScope() instanceof ClassOrInterface &&
                ((ClassOrInterface) node.getScope()).isInheritedFromSupertype(d)) {
            result.add(sourceProposal(offset, prefix, 
                    d.isFormal() ? FORMAL_REFINEMENT : DEFAULT_REFINEMENT, 
                            getDocumentationFor(cpc, d), 
                            getRefinementDescriptionFor(d), 
                            getRefinementTextFor(d), false));
        }
    }

    private static void addBasicProposal(int offset, String prefix, CeylonParseController cpc,
            List<ICompletionProposal> result, boolean inImport, final DeclarationWithProximity dwp,
            Declaration d) {
        result.add(sourceProposal(offset, prefix, 
                CeylonLabelProvider.getImage(d),
                getDocumentationFor(cpc, d), 
                getDescriptionFor(dwp, !inImport), 
                getTextFor(dwp, !inImport), true));
    }

    private static void addInvocationProposals(int offset, String prefix,
            CeylonParseController cpc, List<ICompletionProposal> result,
            final DeclarationWithProximity dwp, Declaration d) {
        boolean isAbstractClass = d instanceof Class && ((Class) d).isAbstract();
        if (!isAbstractClass) {
            result.add(sourceProposal(offset, prefix, 
                    CeylonLabelProvider.getImage(d),
                    getDocumentationFor(cpc, d), 
                    getPositionalInvocationDescriptionFor(dwp), 
                    getPositionalInvocationTextFor(dwp), true));
            List<ParameterList> pls = ((Functional) d).getParameterLists();
            if ( !pls.isEmpty() && pls.get(0).getParameters().size()>1) {
                //if there is more than one parameter, 
                //suggest a named argument invocation 
                result.add(sourceProposal(offset, prefix, 
                        CeylonLabelProvider.getImage(d),
                        getDocumentationFor(cpc, d), 
                        getNamedInvocationDescriptionFor(dwp), 
                        getNamedInvocationTextFor(dwp), true));
            }
        }
    }

    protected static void addMemberNameProposal(int offset, String prefix, Node node,
            List<ICompletionProposal> result) {
        Type type = ((Tree.TypedDeclaration) node).getType();
        if (type instanceof SimpleType) {
            String suggestedName = ((SimpleType) type).getIdentifier().getText();
            if (suggestedName!=null) {
                suggestedName = Character.toLowerCase(suggestedName.charAt(0)) + 
                        suggestedName.substring(1);
                if (suggestedName.startsWith(prefix) && 
                        !suggestedName.equals(prefix) && 
                        !keywords.contains(suggestedName)) {
                    result.add(sourceProposal(offset, prefix, null, 
                            "proposed name for new declaration", 
                            suggestedName, suggestedName, false));
                }
            }
        }
    }
    
    private static void addKeywordProposals(int offset, String prefix, List<ICompletionProposal> result) {
        for (String keyword: keywords) {
            if (!prefix.isEmpty() && keyword.startsWith(prefix) 
                    && !keyword.equals(prefix)) {
                result.add(sourceProposal(offset, prefix, null, 
                        keyword + " keyword", keyword, keyword + " ", 
                        true));
            }
        }
    }
    
    private static String getDocumentationFor(CeylonParseController cpc, Declaration d) {
        return getDocumentation(getReferencedNode(d, getCompilationUnit(cpc, d)));
    }
    
    private static Set<DeclarationWithProximity> sortProposals(final String prefix,
            Map<String, DeclarationWithProximity> proposals) {
        Set<DeclarationWithProximity> set = new TreeSet<DeclarationWithProximity>(
                new Comparator<DeclarationWithProximity>() {
                    public int compare(DeclarationWithProximity x, DeclarationWithProximity y) {
                        String xName = x.getName();
                        String yName = y.getName();
                        if (prefix.length()!=0) {
                            int lowers =  isLowerCase(prefix.charAt(0)) ? -1 : 1;
                            if (isLowerCase(xName.charAt(0)) && 
                                    isUpperCase(yName.charAt(0))) {
                                return lowers;
                            }
                            else if (isUpperCase(xName.charAt(0)) && 
                                    isLowerCase(yName.charAt(0))) {
                                return -lowers;
                            }
                        }
                        if (x.getProximity()!=y.getProximity()) {
                            return new Integer(x.getProximity()).compareTo(y.getProximity());
                        }
                        return xName.compareTo(yName);
                    }
                });
        set.addAll(proposals.values());
        return set;
    }
    
    private static SourceProposal sourceProposal(final int offset, final String prefix,
            final Image image, String doc, String desc, final String text, 
            final boolean selectParams) {
        return new SourceProposal(desc, text, "", 
                new Region(offset - prefix.length(), prefix.length()), 
                offset + text.length(), doc) { 
            @Override
            public Image getImage() {
                return image;
            }
            @Override
            public Point getSelection(IDocument document) {
                if (selectParams) {
                    int locOfTypeArgs = text.indexOf('<');
                    int loc = locOfTypeArgs;
                    if (loc<0) loc = text.indexOf('(');
                    if (loc<0) loc = text.indexOf('=')+1;
                    int start;
                    int length;
                    if (loc<=0 || locOfTypeArgs<0 &&
                            (text.contains("()") || text.contains("{}"))) {
                        start = text.length();
                        length = 0;
                    }
                    else {
                        int endOfTypeArgs = text.indexOf('>'); 
                        int end = text.indexOf(',');
                        if (end<0) end = text.indexOf(';');
                        if (end<0) end = text.length()-1;
                        if (endOfTypeArgs>0) end = end < endOfTypeArgs ? end : endOfTypeArgs;
                        start = loc+1;
                        length = end-loc-1;
                    }
                    return new Point(offset-prefix.length() + start, length);
                }
                else {
                    return new Point(offset + text.length()-prefix.length(), 0);
                }
            }
        };
    }
    
    public static Map<String, DeclarationWithProximity> getProposals(Node node, String prefix,
            Context context) {
        //TODO: substitute type arguments to receiving type
        if (node instanceof Tree.QualifiedMemberExpression) {
            ProducedType type = ((Tree.QualifiedMemberExpression) node).getPrimary().getTypeModel();
            if (type!=null) {
                return type.getDeclaration().getMatchingMemberDeclarations(prefix, 0);
            }
            else {
                return Collections.emptyMap();
            }
        }
        else if (node instanceof Tree.QualifiedTypeExpression) {
            ProducedType type = ((Tree.QualifiedTypeExpression) node).getPrimary().getTypeModel();
            if (type!=null) {
                return type.getDeclaration().getMatchingMemberDeclarations(prefix, 0);
            }
            else {
                return Collections.emptyMap();
            }
        }
        else {
            Map<String, DeclarationWithProximity> result = getLanguageModuleProposals(node, prefix, context);
            result.putAll(node.getScope().getMatchingDeclarations(node.getUnit(), prefix, 0));
            return result;
        }
    }
    
    //TODO: move this method to the model (perhaps make a LanguageModulePackage subclass)
    private static Map<String, DeclarationWithProximity> getLanguageModuleProposals(Node node, 
            String prefix, Context context) {
        Map<String, DeclarationWithProximity> result = new TreeMap<String, DeclarationWithProximity>();
        Module languageModule = context.getModules().getLanguageModule();
        if (languageModule!=null && !(node.getScope() instanceof ImportList)) {
            for (Package languageScope: languageModule.getPackages() ) {
                for (Map.Entry<String, DeclarationWithProximity> entry: 
                    languageScope.getMatchingDeclarations(null, prefix, 1000).entrySet()) {
                    if (entry.getValue().getDeclaration().isShared()) {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return result;
    }
    
    private static boolean forceExplicitTypeArgs(Declaration d) {
        //TODO: this is a pretty limited implementation 
        //      for now, but eventually we could do 
        //      something much more sophisticated to
        //      guess is explicit type args will be
        //      necessary (variance, etc)
        if (d instanceof Functional) {
            List<ParameterList> pls = ((Functional) d).getParameterLists();
            return pls.isEmpty() || pls.get(0).getParameters().isEmpty();
        }
        else {
            return false;
        }
    }
    
    private static String getTextFor(DeclarationWithProximity d, 
            boolean includeTypeArgs) {
        StringBuilder result = new StringBuilder(d.getName());
        if (includeTypeArgs) appendTypeParameters(d.getDeclaration(), result);
        return result.toString();
    }
    
    private static String getPositionalInvocationTextFor(DeclarationWithProximity d) {
        StringBuilder result = new StringBuilder(d.getName());
        if (forceExplicitTypeArgs(d.getDeclaration()))
            appendTypeParameters(d.getDeclaration(), result);
        appendPositionalArgs(d.getDeclaration(), result);
        return result.toString();
    }
    
    private static String getNamedInvocationTextFor(DeclarationWithProximity d) {
        StringBuilder result = new StringBuilder(d.getName());
        if (forceExplicitTypeArgs(d.getDeclaration()))
            appendTypeParameters(d.getDeclaration(), result);
        appendNamedArgs(d.getDeclaration(), result);
        return result.toString();
    }
    
    private static String getDescriptionFor(DeclarationWithProximity d, 
            boolean includeTypeArgs) {
        StringBuilder result = new StringBuilder(d.getName());
        if (includeTypeArgs) appendTypeParameters(d.getDeclaration(), result);
        return result.toString();
    }
    
    private static String getPositionalInvocationDescriptionFor(DeclarationWithProximity d) {
        StringBuilder result = new StringBuilder(d.getName());
        if (forceExplicitTypeArgs(d.getDeclaration()))
            appendTypeParameters(d.getDeclaration(), result);
        appendPositionalArgs(d.getDeclaration(), result);
        return result/*.append(" - invoke with positional arguments")*/.toString();
    }
    
    private static String getNamedInvocationDescriptionFor(DeclarationWithProximity d) {
        StringBuilder result = new StringBuilder(d.getName());
        if (forceExplicitTypeArgs(d.getDeclaration()))
            appendTypeParameters(d.getDeclaration(), result);
        appendNamedArgs(d.getDeclaration(), result);
        return result/*.append(" - invoke with named arguments")*/.toString();
    }
    
    private static String getRefinementTextFor(Declaration d) {
        StringBuilder result = new StringBuilder("shared actual ");
        appendDeclarationText(d, result);
        appendTypeParameters(d, result);
        appendParameters(d, result);
        return result.toString();
    }
    
    private static String getRefinementDescriptionFor(Declaration d) {
        StringBuilder result = new StringBuilder("shared actual ");
        appendDeclarationText(d, result);
        appendTypeParameters(d, result);
        appendParameters(d, result);
        /*result.append(" - refine declaration in ") 
            .append(((Declaration) d.getContainer()).getName());*/
        return result.toString();
    }
    
    public static String getDescriptionFor(Declaration d) {
        StringBuilder result = new StringBuilder();
        if (d!=null) {
            if (d.isFormal()) result.append("formal ");
            if (d.isDefault()) result.append("default ");
            appendDeclarationText(d, result);
            appendTypeParameters(d, result);
            appendParameters(d, result);
            /*result.append(" - refine declaration in ") 
                .append(((Declaration) d.getContainer()).getName());*/
        }
        return result.toString();
    }
    
    private static void appendPositionalArgs(Declaration d, StringBuilder result) {
        if (d instanceof Functional) {
            List<ParameterList> plists = ((Functional) d).getParameterLists();
            if (plists!=null && !plists.isEmpty()) {
                ParameterList params = plists.get(0);
                if (params.getParameters().isEmpty()) {
                    result.append("()");
                }
                else {
                    result.append("(");
                    for (Parameter p: params.getParameters()) {
                        result.append(p.getName()).append(", ");
                    }
                    result.setLength(result.length()-2);
                    result.append(")");
                }
            }
        }
    }
    
    private static void appendNamedArgs(Declaration d, StringBuilder result) {
        if (d instanceof Functional) {
            List<ParameterList> plists = ((Functional) d).getParameterLists();
            if (plists!=null && !plists.isEmpty()) {
                ParameterList params = plists.get(0);
                if (params.getParameters().isEmpty()) {
                    result.append(" {}");
                }
                else {
                    result.append(" { ");
                    for (Parameter p: params.getParameters()) {
                        if (!p.isSequenced()) {
                            result.append(p.getName()).append(" = ")
                            .append(p.getName()).append("; ");
                        }
                    }
                    result.append("}");
                }
            }
        }
    }
    
    private static void appendTypeParameters(Declaration d, StringBuilder result) {
        if (d instanceof Generic) {
            List<TypeParameter> types = ((Generic) d).getTypeParameters();
            if (!types.isEmpty()) {
                result.append("<");
                for (TypeParameter p: types) {
                    result.append(p.getName()).append(", ");
                }
                result.setLength(result.length()-2);
                result.append(">");
            }
        }
    }
    
    private static void appendDeclarationText(Declaration d, StringBuilder result) {
        if (d instanceof Class) {
            result.append("class");
        }
        else if (d instanceof TypedDeclaration) {
            TypedDeclaration td = (TypedDeclaration) d;
            if (td.getType()!=null) {
                String typeName = td.getType().getProducedTypeName();
                if (d instanceof Method) {
                    if (typeName.equals("Void")) { //TODO: fix this!
                        result.append("void");
                    }
                    else {
                        result.append(typeName);
                    }
                }
                else {
                    result.append(typeName);
                }
            }
        }
        result.append(" ").append(d.getName());
    }
    
    /*private static void appendPackage(Declaration d, StringBuilder result) {
    if (d.isToplevel()) {
        result.append(" [").append(getPackageLabel(d)).append("]");
    }
    if (d.isClassOrInterfaceMember()) {
        result.append(" - ");
        ClassOrInterface td = (ClassOrInterface) d.getContainer();
        result.append( td.getName() );
        appendPackage(td, result);
    }
  }*/
    
    private static void appendParameters(Declaration d, StringBuilder result) {
        if (d instanceof Functional) {
            List<ParameterList> plists = ((Functional) d).getParameterLists();
            if (plists!=null && !plists.isEmpty()) {
                ParameterList params = plists.get(0);
                if (params.getParameters().isEmpty()) {
                    result.append("()");
                }
                else {
                    result.append("(");
                    for (Parameter p: params.getParameters()) {
                        result.append(p.getType().getProducedTypeName()).append(" ")
                        .append(p.getName()).append(", ");
                    }
                    result.setLength(result.length()-2);
                    result.append(")");
                }
            }
        }
    }
    
}
