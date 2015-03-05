package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.getDefaultArgSpecifier;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeStartOffset;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedExplicitDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Constructor;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PositionalArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;

public class ChangeParametersRefactoring extends AbstractRefactoring {

    private static class FindInvocationsVisitor extends Visitor {

        private Declaration declaration;

        private final Set<Tree.PositionalArgumentList> posResults = 
                new HashSet<Tree.PositionalArgumentList>();
        private final Set<Tree.NamedArgumentList> namedResults = 
                new HashSet<Tree.NamedArgumentList>();

        Set<Tree.PositionalArgumentList> getPositionalArgLists() {
            return posResults;
        }

        Set<Tree.NamedArgumentList> getNamedArgLists() {
            return namedResults;
        }

        private FindInvocationsVisitor(Declaration declaration) {
            this.declaration = declaration;
        }

        @Override
        public void visit(Tree.InvocationExpression that) {
            super.visit(that);
            Tree.Primary primary = that.getPrimary();
            if (primary instanceof Tree.MemberOrTypeExpression) {
                MemberOrTypeExpression mte = 
                        (Tree.MemberOrTypeExpression) primary;
                Declaration dec = mte.getDeclaration();
                if (isReference(dec)) {
                    Tree.PositionalArgumentList pal = 
                            that.getPositionalArgumentList();
                    if (pal != null) {
                        posResults.add(pal);
                    }
                    Tree.NamedArgumentList nal = 
                            that.getNamedArgumentList();
                    if (nal != null) {
                        namedResults.add(nal);
                    }
                }
            }
        }

        private boolean isReference(Declaration dec) {
            return dec.refines(declaration) ||
                    dec instanceof Class && 
                    declaration instanceof Constructor &&
                    declaration.getContainer().equals(dec) && 
                    declaration.getName().equals(dec.getName());
        }
    }

    private static class FindArgumentsVisitor extends Visitor {

        private Declaration declaration;

        private final Set<Tree.MethodArgument> results = 
                new HashSet<Tree.MethodArgument>();

        Set<Tree.MethodArgument> getResults() {
            return results;
        }

        private FindArgumentsVisitor(Declaration declaration) {
            this.declaration = declaration;
        }

        @Override
        public void visit(Tree.MethodArgument that) {
            super.visit(that);
            Parameter p = that.getParameter();
            if (p != null && p.getModel().equals(declaration)) {
                results.add(that);
            }
        }
    }

    private List<Integer> order = new ArrayList<Integer>();
    private List<Boolean> defaulted = new ArrayList<Boolean>();
    private List<String> names = new ArrayList<String>();

    private final Declaration declaration;

    private final List<Parameter> parameters;

    private Map<MethodOrValue, String> arguments = 
            new HashMap<MethodOrValue, String>();
    private final Map<MethodOrValue, String> defaultArgs = 
            new HashMap<MethodOrValue, String>();
    private final Map<MethodOrValue, String> originalDefaultArgs = 
            new HashMap<MethodOrValue, String>();
    private final Map<MethodOrValue, String> paramLists = 
            new HashMap<MethodOrValue, String>();

    public Map<MethodOrValue, String> getDefaultArgs() {
        return defaultArgs;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Node getNode() {
        return node;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public List<Boolean> getDefaulted() {
        return defaulted;
    }

    public List<String> getNames() {
        return names;
    }

    public Map<MethodOrValue, String> getArguments() {
        return arguments;
    }

    public ChangeParametersRefactoring(IEditorPart textEditor) {
        super(textEditor);
        if (rootNode != null) {
            Referenceable refDec = 
                    getReferencedExplicitDeclaration(node,
                            rootNode);
            if (refDec instanceof Functional && 
                refDec instanceof Declaration) {
                refDec = ((Declaration) refDec)
                        .getRefinedDeclaration();
                List<ParameterList> pls = 
                        ((Functional) refDec)
                                .getParameterLists();
                if (pls.isEmpty()) {
                    declaration = null;
                    parameters = null;
                }
                else {
                    Declaration dec = (Declaration) refDec;
                    if (dec instanceof Class) {
                        if (((Class) dec).hasConstructors()) {
                            Declaration d = 
                                    dec.getMember(dec.getName(), 
                                            null, false);
                            if (d instanceof Constructor) {
                                dec = d;
                            }
                        }
                    }
                    declaration = dec;
                    List<Parameter> paramList = 
                            pls.get(0).getParameters();
                    parameters = new ArrayList<Parameter>(paramList);
                    for (int i=0; i<parameters.size(); i++) {
                        order.add(i);
                        Parameter parameter = parameters.get(i);
                        defaulted.add(parameter.isDefaulted());
                        names.add(parameter.getName());
                    }
                    Node decNode = getReferencedNode(refDec);
                    Tree.ParameterList pl = null;
                    if (decNode instanceof Tree.AnyClass) {
                        Tree.AnyClass ac = 
                                (Tree.AnyClass) decNode;
                        pl = ac.getParameterList();
                    }
                    if (decNode instanceof Tree.Constructor) {
                        Tree.Constructor c = 
                                (Tree.Constructor) decNode;
                        pl = c.getParameterList();
                    }
                    else if (decNode instanceof Tree.AnyMethod) {
                        Tree.AnyMethod am = 
                                (Tree.AnyMethod) decNode;
                        pl = am.getParameterLists().get(0);
                    }
                    if (pl!=null) {
                        for (Tree.Parameter p: pl.getParameters()) {
                            Tree.SpecifierOrInitializerExpression sie = 
                                    getDefaultArgSpecifier(p);
                            MethodOrValue pm = 
                                    p.getParameterModel().getModel();
                            if (sie != null) {
                                defaultArgs.put(pm,
                                        toString(sie.getExpression()));
                            }
                            if (p instanceof Tree.FunctionalParameterDeclaration) {
                                Tree.FunctionalParameterDeclaration fp = 
                                        (Tree.FunctionalParameterDeclaration) p;
                                Tree.MethodDeclaration pd = 
                                        (Tree.MethodDeclaration) fp
                                                .getTypedDeclaration();
                                Tree.ParameterList first = 
                                        pd.getParameterLists().get(0);
                                paramLists.put(pm, toString(first));
                            }
                        }
                        originalDefaultArgs.putAll(defaultArgs);
                    }
                }
            } else {
                declaration = null;
                parameters = null;
            }
        } else {
            declaration = null;
            parameters = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return declaration instanceof Functional && 
                project != null && 
                inSameProject(declaration);
    }

    public int getCount() {
        return declaration == null ? 
                0 : countDeclarationOccurrences();
    }

    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindInvocationsVisitor frv = 
                new FindInvocationsVisitor(declaration);
        FindRefinementsVisitor fdv = 
                new FindRefinementsVisitor(declaration);
        FindArgumentsVisitor fav = 
                new FindArgumentsVisitor(declaration);
        cu.visit(frv);
        cu.visit(fdv);
        cu.visit(fav);
        return frv.getPositionalArgLists().size() + 
                fdv.getDeclarationNodes().size() + 
                fav.getResults().size();
    }

    public String getName() {
        return "Change Parameter List";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        
        RefactoringStatus result = new RefactoringStatus();
        
        boolean foundDefaulted = false;
        for (int index=0; index<defaulted.size(); index++) {
            if (defaulted.get(index)) {
                foundDefaulted = true;
            }
            else {
                if (foundDefaulted) {
                    result.addWarning("defaulted parameters occur before required parameters");
                    break;
                }
            }
        }
        
        for (int index=0; index<defaulted.size(); index++) {
            Parameter p = parameters.get(order.get(index));
            if (defaulted.get(index)) {
                String arg = defaultArgs.get(p.getModel());
                if (arg == null || arg.isEmpty()) {
                    result.addWarning("missing default argument for "
                            + p.getName());
                }
            }
        }
        
        return result;
    }

    public CompositeChange createChange(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        List<PhasedUnit> units = getAllUnits();
        pm.beginTask(getName(), units.size());
        CompositeChange cc = 
                new CompositeChange(getName());
        int i = 0;
        for (PhasedUnit pu : units) {
            if (searchInFile(pu)) {
                TextFileChange tfc = 
                        newTextFileChange(pu);
                refactorInFile(tfc, cc, 
                        pu.getCompilationUnit(),
                        pu.getTokens());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            CeylonParseController pc = 
                    editor.getParseController();
            refactorInFile(dc, cc, 
                    pc.getRootNode(), 
                    pc.getTokens());
            pm.worked(i++);
        }
        pm.done();
        return cc;
    }

    private void refactorInFile(TextChange tfc, CompositeChange cc,
            Tree.CompilationUnit root, List<CommonToken> tokens) {
        tfc.setEdit(new MultiTextEdit());
        if (declaration != null) {
            refactorArgumentLists(tfc, root);
            refactorDeclarations(tfc, root, tokens);
            refactorReferences(tfc, root);
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

    private void refactorReferences(TextChange tfc, Tree.CompilationUnit root) {
        for (int i=0; i<names.size(); i++) {
            Parameter p = parameters.get(order.get(i));
            String newName = names.get(i);
            if (!p.getName().equals(newName)) {
                FindReferencesVisitor fprv = 
                        new FindReferencesVisitor(p.getModel());
                root.visit(fprv);
                for (Node ref: fprv.getNodes()) {
                    Node idn = getIdentifyingNode(ref);
                    if (idn instanceof Tree.Identifier) {
                        Tree.Identifier id = (Tree.Identifier) idn;
                        tfc.addEdit(new ReplaceEdit(id.getStartIndex(), 
                              id.getStopIndex()-id.getStartIndex()+1, 
                              newName));
                    }
                }
            }
        }
    }

    private void refactorDeclarations(TextChange tfc,
            Tree.CompilationUnit root, List<CommonToken> tokens) {
        
        FindRefinementsVisitor frv = 
                new FindRefinementsVisitor(declaration);
        root.visit(frv);
        
        for (Tree.StatementOrArgument decNode: 
                frv.getDeclarationNodes()) {
            boolean actual;
            Tree.ParameterList pl;
            if (decNode instanceof Tree.AnyMethod) {
                Tree.AnyMethod m = 
                        (Tree.AnyMethod) decNode;
                pl = m.getParameterLists().get(0);
                actual = m.getDeclarationModel().isActual();
            }
            else if (decNode instanceof Tree.AnyClass) {
                Tree.AnyClass c = 
                        (Tree.AnyClass) decNode;
                pl = c.getParameterList();
                actual = c.getDeclarationModel().isActual();
            }
            else if (decNode instanceof Tree.Constructor) {
                Tree.Constructor c = 
                        (Tree.Constructor) decNode;
                pl = c.getParameterList();
                actual = c.getDeclarationModel().isActual();
            }
            else if (decNode instanceof Tree.SpecifierStatement) {
                Tree.SpecifierStatement ss = 
                        (Tree.SpecifierStatement) decNode;
                Tree.Term bme = 
                        ss.getBaseMemberExpression();
                if (bme instanceof Tree.ParameterizedExpression) {
                    Tree.ParameterizedExpression pe = 
                            (Tree.ParameterizedExpression) bme;
                    pl = pe.getParameterLists().get(0);
                    actual = true;
                }
                else {
                    continue;
                }
            }
            else {
                continue;
            }
            
            tfc.addEdit(reorderParamsEdit(pl, 
                    reorderedParameters(pl.getParameters()), 
                    actual, tokens));
        }
    }

    private void refactorArgumentLists(TextChange tfc,
            Tree.CompilationUnit root) {
                
        FindInvocationsVisitor fiv = 
                new FindInvocationsVisitor(declaration);
        root.visit(fiv);
        
        int requiredParams = countRequiredParameters();
        for (Tree.PositionalArgumentList pal: fiv.getPositionalArgLists()) {
            tfc.addEdit(reorderArgsEdit(pal, 
                    reorderedArguments(requiredParams, 
                            pal.getPositionalArguments()), 
                    tokens));
        }

        for (Tree.NamedArgumentList nal: fiv.getNamedArgLists()) {
            List<Tree.NamedArgument> nas = 
                    nal.getNamedArguments();
            Tree.NamedArgument last = null;
            for (Tree.NamedArgument na: nas) {
                Parameter nap = na.getParameter();
                if (nap != null) {
                    boolean found = false;
                    for (int i=0; i<defaulted.size(); i++) {
                        Parameter p = parameters.get(order.get(i));
                        if (nap.getModel()
                                .equals(p.getModel())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        int start = 
                                last == null ? 
                                nal.getStartIndex() + 1 : 
                                last.getStopIndex() + 1;
                        tfc.addEdit(new DeleteEdit(start, 
                                na.getStopIndex() - start + 1));
                    }
                }
                last = na;
            }
            for (int i=0; i<defaulted.size(); i++) {
                int index = order.get(i);
                Parameter p = parameters.get(index);
                if (!defaulted.get(i) || 
                        defaultHasChanged(p)) {
                    boolean found = false;
                    for (Tree.NamedArgument na : nas) {
                        Parameter nap = na.getParameter();
                        if (nap!=null
                                && nap.getModel()
                                        .equals(p.getModel())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        String arg = arguments.get(p.getModel());
                        String argString = 
                                getInlinedNamedArg(p, arg);
                        int startOffset = nal.getStartIndex();
                        int stopOffset = nal.getStopIndex();
                        try {
                            IDocument doc = getDocument(tfc);
                            if (doc.getLineOfOffset(stopOffset) > 
                                doc.getLineOfOffset(startOffset)) {
                                argString = 
                                        getDefaultIndent() + 
                                        argString + ';' +
                                        getDefaultLineDelimiter(doc) + 
                                        getIndent(nal, doc);
                            }
                            else if (startOffset==stopOffset-1) {
                                argString = ' ' + argString + ' ';
                            }
                            else {
                                argString = argString + ' ';
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tfc.addEdit(new InsertEdit(stopOffset, 
                                argString));
                    }
                }
            }
        }
        
        FindArgumentsVisitor fav = 
                new FindArgumentsVisitor(declaration);
        root.visit(fav);
        for (Tree.MethodArgument decNode: 
                fav.getResults()) {
            Tree.ParameterList pl = 
                    decNode.getParameterLists().get(0);
            tfc.addEdit(reorderParamsEdit(pl, 
                    reorderedParameters(pl.getParameters()), 
                    tokens));
        }
        
    }

    private int countRequiredParameters() {
        int requiredParams = -1;
        for (int i=0; i<defaulted.size(); i++) {
            int index = order.get(i);
            Parameter p = parameters.get(index);
            if (!defaulted.get(i) || defaultHasChanged(p)) {
                if (i > requiredParams) {
                    requiredParams = i;
                }
            }
        }
        return requiredParams;
    }

    private int countExistingArgs(List<Tree.PositionalArgument> pas) {
        int existingArgs = 0;
        for (int i=0; i<pas.size(); i++) {
            Parameter p = pas.get(i).getParameter();
            if (p != null) {
                int newLoc = order.indexOf(i);
                if (newLoc > existingArgs) {
                    existingArgs = newLoc;
                }
            }
        }
        return existingArgs;
    }

    private Tree.PositionalArgument[] reorderedArguments(int requiredParams,
            List<Tree.PositionalArgument> pas) {
        int existingArgs = countExistingArgs(pas);
        int len = Math.max(requiredParams + 1, existingArgs + 1);
        Tree.PositionalArgument[] args = 
                new Tree.PositionalArgument[len];
        for (int i=0; i<pas.size(); i++) {
            PositionalArgument argument = pas.get(i);
            int index = order.indexOf(i);
            if (index >= 0) {
                args[index] = argument;
            }
        }
        return args;
    }

    Tree.Parameter[] reorderedParameters(List<Tree.Parameter> ps) {
        Tree.Parameter[] params = 
                new Tree.Parameter[defaulted.size()];
        for (int i=0; i<ps.size(); i++) {
            int index = order.indexOf(i);
            if (index >= 0) {
                params[index] = ps.get(i);
            }
        }
        return params;
    }

    boolean defaultHasChanged(Parameter p) {
        String original = originalDefaultArgs.get(p.getModel());
        String current = defaultArgs.get(p.getModel());
        return p.isDefaulted() && original != null &&
        // the default arg has been modified
                (current == null || !current.equals(original));
    }

    public ReplaceEdit reorderArgsEdit(Node list,
            Tree.PositionalArgument[] arguments,
            List<CommonToken> tokens) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < arguments.length; i++) {
            Tree.PositionalArgument elem = arguments[i];
            String argString;
            if (elem == null) {
                int index = order.get(i);
                Parameter p = parameters.get(index);
                String arg = this.arguments.get(p.getModel());
                argString = getInlinedArg(p, arg);
            } else {
                argString = Nodes.toString(elem, tokens);
            }
            sb.append(argString).append(", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")");
        return new ReplaceEdit(getNodeStartOffset(list), 
                getNodeLength(list), sb.toString());
    }

    public ReplaceEdit reorderParamsEdit(Node list, 
            Tree.Parameter[] parameters,
            List<CommonToken> tokens) {
        StringBuilder sb = new StringBuilder("(");
        for (int i=0; i<parameters.length; i++) {
            String paramString = 
                    paramString(parameters[i], names.get(i), 
                            tokens);
            sb.append(paramString)
              .append(", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")");
        return new ReplaceEdit(getNodeStartOffset(list), 
                getNodeLength(list), sb.toString());
    }

    public ReplaceEdit reorderParamsEdit(Node list, 
            Tree.Parameter[] parameters,
            boolean actual, 
            List<CommonToken> tokens) {
        StringBuilder sb = new StringBuilder("(");
        for (int i=0; i<parameters.length; i++) {
            Tree.Parameter parameter = parameters[i];
            String paramString;
            String newName = names.get(i);
            if (parameter == null) {
                int index = order.get(i);
                Parameter addedParameter = 
                        this.parameters.get(index);
                Unit unit = list.getUnit();
                paramString = 
                        addedParameter.getType()
                            .getProducedTypeName(unit) + 
                        ' ' + newName;
                if (defaulted.get(i) && !actual) {
                    MethodOrValue model = 
                            addedParameter.getModel();
                    paramString += " = " + defaultArgs.get(model);
                }
            }
            else {
                paramString = 
                        paramStringWithoutDefaultArg(parameter, 
                                newName, tokens);
                if (defaulted.get(i) && !actual) {
                    // now add the new default arg
                    // TODO: this results in incorrectly-typed
                    // code for void functional parameters
                    Parameter p = parameter.getParameterModel();
                    paramString = 
                            paramString + 
                            getSpecifier(parameter) + 
                            getNewDefaultArg(p);
                }
            }
            sb.append(paramString).append(", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")");
        return new ReplaceEdit(getNodeStartOffset(list), 
                getNodeLength(list), sb.toString());
    }

    private String paramString(Tree.Parameter parameter, 
            String newName, List<CommonToken> tokens) {
        String paramString = 
                Nodes.toString(parameter, tokens);
        int loc = parameter.getStartIndex();
        Tree.Identifier id = getIdentifier(parameter);
        int start = id.getStartIndex() - loc;
        int end = id.getStopIndex()+1 - loc;
        paramString = paramString.substring(0, start) + 
                newName + paramString.substring(end);
        return paramString;
    }

    private String paramStringWithoutDefaultArg(Tree.Parameter parameter,
            String newName, List<CommonToken> tokens) {
        String paramString = Nodes.toString(parameter, tokens);
        // first remove the default arg
        Node sie = getDefaultArgSpecifier(parameter);
        int loc = parameter.getStartIndex();
        if (sie!=null) {
            int start = sie.getStartIndex() - loc;
            paramString = paramString.substring(0,start).trim();
        }
        Tree.Identifier id = getIdentifier(parameter);
        int start = id.getStartIndex() - loc;
        int end = id.getStopIndex()+1 - loc;
        paramString = paramString.substring(0, start) + 
                newName + paramString.substring(end);
        return paramString;
    }

    private Tree.Identifier getIdentifier(Tree.Parameter parameter) {
        if (parameter instanceof Tree.InitializerParameter) {
            return ((Tree.InitializerParameter) parameter).getIdentifier();
        }
        else if (parameter instanceof Tree.ParameterDeclaration) {
            return ((Tree.ParameterDeclaration) parameter).getTypedDeclaration().getIdentifier();
        }
        else {
            throw new RuntimeException();
        }
    }

    private static String getSpecifier(Tree.Parameter parameter) {
        if (parameter instanceof 
                Tree.FunctionalParameterDeclaration) {
            return " => ";
        }
        else {
            return " = ";
        }
    }

    private String getInlinedArg(Parameter p, String argString) {
        if (argString == null || argString.isEmpty()) {
            argString = originalDefaultArgs.get(p.getModel());
            if (argString == null || argString.isEmpty()) {
                argString = "nothing";
            }
        }
        String params = paramLists.get(p.getModel());
        if (params != null) {
            argString = params + " => " + argString;
        }
        return argString;
    }

    private String getInlinedNamedArg(Parameter p, String argString) {
        if (argString == null || argString.isEmpty()) {
            argString = originalDefaultArgs.get(p.getModel());
            if (argString == null || argString.isEmpty()) {
                argString = "nothing";
            }
        }
        String paramList = paramLists.get(p.getModel());
        if (paramList == null) {
            int index = order.indexOf(parameters.indexOf(p));
            return names.get(index) + " = " + argString;
        }
        else {
            return "function " + p.getName() + paramList + 
                    " => " + argString;
        }
    }

    private String getNewDefaultArg(Parameter p) {
        String argString = defaultArgs.get(p.getModel());
        if (argString == null || argString.isEmpty()) {
            argString = "nothing";
        }
        return argString;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

}
