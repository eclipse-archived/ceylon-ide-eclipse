/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getDocument;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getDefaultArgSpecifier;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedExplicitDeclaration;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.text;

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
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.PositionalArgument;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.Constructor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.Functional;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.ParameterList;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.Unit;

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
                if (dec.refines(declaration)) {
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

    private Map<FunctionOrValue, String> arguments = 
            new HashMap<FunctionOrValue, String>();
    private final Map<FunctionOrValue, String> defaultArgs = 
            new HashMap<FunctionOrValue, String>();
    private final Map<FunctionOrValue, String> originalDefaultArgs = 
            new HashMap<FunctionOrValue, String>();
    private final Map<FunctionOrValue, String> paramLists = 
            new HashMap<FunctionOrValue, String>();

    public Map<FunctionOrValue, String> getDefaultArgs() {
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

    public Map<FunctionOrValue, String> getArguments() {
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
                Declaration dec = (Declaration) refDec;
                refDec = dec.getRefinedDeclaration();
                Functional fd = (Functional) refDec;
                List<ParameterList> pls = 
                        fd.getParameterLists();
                if (pls.isEmpty()) {
                    declaration = null;
                    parameters = null;
                }
                else {
                    if (dec instanceof Class) {
                        Class c = (Class) dec;
                        Constructor defaultConstructor = 
                                c.getDefaultConstructor();
                        if (defaultConstructor!=null) {
                            dec = defaultConstructor;
                        }
                    }
                    declaration = dec;
                    List<Parameter> paramList = 
                            pls.get(0).getParameters();
                    parameters = 
                            new ArrayList<Parameter>
                                (paramList);
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
                            FunctionOrValue pm = 
                                    p.getParameterModel()
                                        .getModel();
                            if (sie != null) {
                                Tree.Expression e = 
                                        sie.getExpression();
                                defaultArgs.put(pm, text(e, tokens));
                            }
                            if (p instanceof Tree.FunctionalParameterDeclaration) {
                                Tree.FunctionalParameterDeclaration fp = 
                                        (Tree.FunctionalParameterDeclaration) p;
                                Tree.MethodDeclaration pd = 
                                        (Tree.MethodDeclaration) 
                                            fp.getTypedDeclaration();
                                Tree.ParameterList first = 
                                        pd.getParameterLists()
                                            .get(0);
                                paramLists.put(pm, text(first, tokens));
                            }
                        }
                        originalDefaultArgs.putAll(defaultArgs);
                    }
                }
            }
            else {
                declaration = null;
                parameters = null;
            }
        }
        else {
            declaration = null;
            parameters = null;
        }
    }

    @Override
    public boolean getEnabled() {
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

    public RefactoringStatus checkInitialConditions(
            IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(
            IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        
        RefactoringStatus result = new RefactoringStatus();
        
        boolean foundDefaulted = false;
        boolean foundVariadic = false;
        for (int index=0; index<defaulted.size(); index++) {
            if (foundVariadic) {
                result.addWarning("parameters occur after variadic parameter");
            }
            Parameter p = parameters.get(order.get(index));
            if (p.isSequenced()) {
                foundVariadic = true;
            }
            if (defaulted.get(index)) {
                foundDefaulted = true;
            }
            else {
                if (foundDefaulted && !p.isSequenced()) {
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
    
    @Override
    protected void refactorInFile(TextChange tfc, 
            CompositeChange cc, Tree.CompilationUnit root, 
            List<CommonToken> tokens) {
        tfc.setEdit(new MultiTextEdit());
        if (declaration != null) {
            refactorArgumentLists(tfc, root, tokens);
            refactorDeclarations(tfc, root, tokens);
            refactorReferences(tfc, root);
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

    private void refactorReferences(TextChange tfc, 
            Tree.CompilationUnit root) {
        for (int i=0; i<names.size(); i++) {
            String newName = names.get(i);
            final Parameter param = 
                    parameters.get(order.get(i));
            FunctionOrValue model = param.getModel();
            FindReferencesVisitor fprv = 
                    new FindReferencesVisitor(model) {
                @Override
                public void visit(Tree.InitializerParameter that) {
                    //initializer parameters will be handled when
                    //we refactor the parameter list
                    Tree.SpecifierExpression se = 
                            that.getSpecifierExpression();
                    if (se!=null) {
                        se.visit(this);
                    }
                }
                @Override
                public void visit(Tree.ParameterDeclaration that) {
                    //don't confuse a parameter declaration with
                    //a split declaration below
                    Tree.TypedDeclaration td = 
                            that.getTypedDeclaration();
                    if (td instanceof Tree.AttributeDeclaration) {
                        Tree.AttributeDeclaration ad = 
                                (Tree.AttributeDeclaration) td;
                        Tree.SpecifierOrInitializerExpression se = 
                                ad.getSpecifierOrInitializerExpression();
                        if (se!=null) {
                            se.visit(this);
                        }
                    }
                    if (td instanceof Tree.MethodDeclaration) {
                        Tree.MethodDeclaration md = 
                                (Tree.MethodDeclaration) td;
                        Tree.SpecifierExpression se = 
                                md.getSpecifierExpression();
                        if (se!=null) {
                            se.visit(this);
                        }
                    }
                }
                @Override
                public void visit(Tree.TypedDeclaration that) {
                    //handle split declarations
                    super.visit(that);
                    Tree.Identifier id = that.getIdentifier();
                    if (id!=null &&
                            isReference(
                                    that.getDeclarationModel(), 
                                    id.getText())) {
                        nodes.add(that);
                    }
                }
                @Override
                protected boolean isReference(Parameter p) {
                    return isSameParameter(param, p);
                }
                @Override
                protected boolean isReference(
                        Declaration ref, String id) {
                    if (ref.isParameter()) {
                        FunctionOrValue fov = 
                                (FunctionOrValue) ref;
                        return isSameParameter(param, 
                                fov.getInitializerParameter());
                    }
                    else {
                        return false;
                    }
                }
            };
            root.visit(fprv);
            for (Node ref: fprv.getNodes()) {
                Node idn = getIdentifyingNode(ref);
                if (idn instanceof Tree.Identifier) {
                    Tree.Identifier id = 
                            (Tree.Identifier) idn;
                    if (!id.getText().equals(newName)) {
                        tfc.addEdit(new ReplaceEdit(
                                id.getStartIndex(), 
                                id.getDistance(), 
                                newName));
                    }
                }
            }
        }
    }

    private void refactorDeclarations(TextChange tfc,
            Tree.CompilationUnit root, 
            List<CommonToken> tokens) {
        
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
    
    private static boolean isSameParameter
            (Parameter x, Parameter y) {
        if (x==null || y==null) return false;
        Declaration xd = x.getDeclaration();
        Declaration yd = y.getDeclaration();
        Functional fx = (Functional) xd;
        Functional fy = (Functional) yd;
        List<ParameterList> xpl = fx.getParameterLists();
        List<ParameterList> ypl = fy.getParameterLists();
        return !xpl.isEmpty() && !ypl.isEmpty() &&
                xd.getRefinedDeclaration()
                    .equals(yd.getRefinedDeclaration())
                && xpl.get(0).getParameters().indexOf(x) ==
                   ypl.get(0).getParameters().indexOf(y);
    }

    private void refactorArgumentLists(TextChange tfc,
            Tree.CompilationUnit root, 
            List<CommonToken> tokens) {
                
        FindInvocationsVisitor fiv = 
                new FindInvocationsVisitor(declaration);
        root.visit(fiv);
        
        int requiredParams = countRequiredParameters();
        for (Tree.PositionalArgumentList pal: 
                fiv.getPositionalArgLists()) {
            tfc.addEdit(reorderArgsEdit(pal, 
                    reorderedArguments(requiredParams, 
                            pal.getPositionalArguments()), 
                    tokens));
        }

        for (Tree.NamedArgumentList nal: 
                fiv.getNamedArgLists()) {
            List<Tree.NamedArgument> nas = 
                    nal.getNamedArguments();
            Tree.NamedArgument last = null;
            for (Tree.NamedArgument na: nas) {
                Parameter nap = na.getParameter();
                if (nap != null) {
                    boolean found = false;
                    for (int i=0; i<defaulted.size(); i++) {
                        Parameter p = 
                                parameters.get(order.get(i));
                        if (isSameParameter(p,nap)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        int start = 
                                last == null ? 
                                nal.getStartIndex() + 1 : 
                                last.getEndIndex();
                        tfc.addEdit(new DeleteEdit(start, 
                                na.getEndIndex() - start));
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
                        if (isSameParameter(p,nap)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        String arg = 
                                arguments.get(p.getModel());
                        String argString = 
                                getInlinedNamedArg(p, arg);
                        int startOffset = nal.getStartIndex();
                        int stopOffset = nal.getStopIndex();
                        try {
                            IDocument doc = getDocument(tfc);
                            if (doc.getLineOfOffset(stopOffset) > 
                                doc.getLineOfOffset(startOffset)) {
                                argString = 
                                        utilJ2C().indents().getDefaultIndent() + 
                                        argString + ';' +
                                        utilJ2C().indents().getDefaultLineDelimiter(doc) + 
                                        utilJ2C().indents().getIndent(nal, doc);
                            }
                            else if (startOffset==stopOffset-1) {
                                argString = ' ' + argString + ';' + ' ';
                            }
                            else {
                                argString = argString + ';' + ' ';
                            }
                        }
                        catch (Exception e) {
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
                    decNode.getParameterLists()
                        .get(0);
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

    private Tree.PositionalArgument[] reorderedArguments(
            int requiredParams,
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
        for (int i=0; i<arguments.length; i++) {
            Tree.PositionalArgument elem = arguments[i];
            String argString;
            if (elem == null) {
                int index = order.get(i);
                Parameter p = parameters.get(index);
                String arg = 
                        this.arguments.get(p.getModel());
                argString = getInlinedArg(p, arg);
            }
            else {
                argString = text(elem, tokens);
            }
            sb.append(argString).append(", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")");
        return new ReplaceEdit(list.getStartIndex(), 
                list.getDistance(), sb.toString());
    }

    public ReplaceEdit reorderParamsEdit(Node list, 
            Tree.Parameter[] parameters,
            List<CommonToken> tokens) {
        StringBuilder sb = new StringBuilder("(");
        for (int i=0; i<parameters.length; i++) {
            String paramString = 
                    paramString(parameters[i], 
                            names.get(i), tokens);
            sb.append(paramString)
              .append(", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")");
        return new ReplaceEdit(list.getStartIndex(), 
                list.getDistance(), sb.toString());
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
                            .asString(unit) + 
                        ' ' + newName;
                if (defaulted.get(i) && !actual) {
                    FunctionOrValue model = 
                            addedParameter.getModel();
                    paramString += 
                            " = " + defaultArgs.get(model);
                }
            }
            else {
                paramString = 
                        paramStringWithoutDefaultArg(
                                parameter, newName, tokens);
                if (defaulted.get(i) && !actual) {
                    // now add the new default arg
                    // TODO: this results in incorrectly-typed
                    // code for void functional parameters
                    Parameter p = 
                            parameter.getParameterModel();
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
        return new ReplaceEdit(list.getStartIndex(), 
                list.getDistance(), sb.toString());
    }

    private String paramString(Tree.Parameter parameter, 
            String newName, List<CommonToken> tokens) {
        String paramString = text(parameter, tokens);
        int loc = parameter.getStartIndex();
        Tree.Identifier id = getIdentifier(parameter);
        int start = id.getStartIndex() - loc;
        int end = id.getEndIndex() - loc;
        return paramString.substring(0, start) + 
                newName + paramString.substring(end);
    }

    private String paramStringWithoutDefaultArg(
            Tree.Parameter parameter,
            String newName, List<CommonToken> tokens) {
        String paramString = text(parameter, tokens);
        // first remove the default arg
        Node sie = getDefaultArgSpecifier(parameter);
        int loc = parameter.getStartIndex();
        if (sie!=null) {
            int start = sie.getStartIndex() - loc;
            paramString = 
                    paramString.substring(0,start).trim();
        }
        Tree.Identifier id = getIdentifier(parameter);
        int start = id.getStartIndex() - loc;
        int end = id.getEndIndex() - loc;
        return paramString.substring(0, start) + 
                newName + paramString.substring(end);
    }

    private Tree.Identifier getIdentifier(Tree.Parameter parameter) {
        if (parameter instanceof Tree.InitializerParameter) {
            Tree.InitializerParameter ip = 
                    (Tree.InitializerParameter) parameter;
            return ip.getIdentifier();
        }
        else if (parameter instanceof Tree.ParameterDeclaration) {
            Tree.ParameterDeclaration pd = 
                    (Tree.ParameterDeclaration) parameter;
            return pd.getTypedDeclaration().getIdentifier();
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

    @Override
    protected boolean isAffectingOtherFiles() {
        if (declaration==null) {
            return false;
        }
        if (declaration.isToplevel() ||
            declaration.isShared()) {
            return true;
        }
        return false;
    }
    
}
