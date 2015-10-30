package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LINE_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importProposals;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.util.Indents.indents;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.addToUnion;
import static java.util.Collections.singletonList;
import static org.antlr.runtime.Token.HIDDEN_CHANNEL;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.ide.common.refactoring.ExtractLinkedModeEnabled;
import com.redhat.ceylon.ide.common.util.FindContainerVisitor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.UnionType;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;

public class ExtractFunctionRefactoring extends AbstractRefactoring implements ExtractLinkedModeEnabled<IRegion> {
    
    private final class FindOuterReferencesVisitor extends Visitor {
        final Declaration declaration;
        int refs = 0;
        FindOuterReferencesVisitor(Declaration declaration) {
            this.declaration = declaration;
        }
        @Override
        public void visit(Tree.MemberOrTypeExpression that) {
            super.visit(that);
            Declaration dec = that.getDeclaration();
            if (declaration.equals(dec)) {
                refs++;
            }
        }
        @Override
        public void visit(Tree.Declaration that) {
            super.visit(that);
            Declaration dec = that.getDeclarationModel();
            if (declaration.equals(dec)) {
                refs++;
            }
        }
        @Override
        public void visit(Tree.Type that) {
            super.visit(that);
            Type type = that.getTypeModel();
            if (type!=null) {
                if (type.isClassOrInterface()) {
                    TypeDeclaration td = 
                            type.getDeclaration();
                    if (declaration.equals(td)) {
                        refs++;
                    }
                }
            }
        }
    }
    
    private final class CheckExpressionVisitor extends Visitor {
        String problem = null;
        @Override
        public void visit(Tree.Body that) {}
        @Override
        public void visit(Tree.AssignmentOp that) {
            super.visit(that);
            problem = "an assignment";
        }
    }

    private final class CheckStatementsVisitor extends Visitor {
        final Tree.Body scope;
        final Collection<Tree.Statement> statements;
        CheckStatementsVisitor(Tree.Body scope, 
                Collection<Tree.Statement> statements) {
            this.scope = scope;
            this.statements = statements;
        }
        String problem = null;
        @Override
        public void visit(Tree.Body that) {
            if (that.equals(scope)) {
                super.visit(that);
            }
        }
        @Override
        public void visit(Tree.Declaration that) {
            super.visit(that);
            if (result==null || !that.equals(result)) {
                Declaration d = that.getDeclarationModel();
                if (d.isShared()) {
                    problem = "a shared declaration";
                }
                else {
                    if (hasOuterRefs(d, scope, statements)) {
                        problem = "a declaration used elsewhere";
                    }
                }
            }
        }
        @Override
        public void visit(Tree.SpecifierStatement that) {
            super.visit(that);
            if (result==null || !that.equals(result)) {
                Tree.Term term = that.getBaseMemberExpression();
                if (term instanceof Tree.MemberOrTypeExpression) {
                    Tree.MemberOrTypeExpression mte = 
                            (Tree.MemberOrTypeExpression) term;
                    Declaration d = 
                            mte.getDeclaration();
                    if (notResultRef(d) && 
                            hasOuterRefs(d, scope, statements)) {
                        problem = "a specification statement for a declaration used or defined elsewhere";
                    }
                }
            }
        }
        @Override
        public void visit(Tree.AssignmentOp that) {
            super.visit(that);
            if (result==null || !that.equals(result)) {
                if (that.getLeftTerm() instanceof Tree.MemberOrTypeExpression) {
                    Tree.MemberOrTypeExpression mte = 
                            (Tree.MemberOrTypeExpression) 
                                that.getLeftTerm();
                    Declaration d = 
                            mte.getDeclaration();
                    if (notResultRef(d) && 
                            hasOuterRefs(d, scope, statements)) {
                        problem = "an assignment to a declaration used or defined elsewhere";
                    }
                }
            }
        }
        private boolean notResultRef(Declaration d) {
            return resultDeclaration==null || 
                    !resultDeclaration.equals(d);
        }
        @Override
        public void visit(Tree.Directive that) {
            super.visit(that);
            problem = "a directive statement";
        }
    }

    private boolean hasOuterRefs(Declaration d, Tree.Body scope, 
            Collection<Tree.Statement> statements) {
        if (scope==null) return false; //TODO: what case is this?
        FindOuterReferencesVisitor v = 
                new FindOuterReferencesVisitor(d);
        for (Tree.Statement s: scope.getStatements()) {
            if (!statements.contains(s)) {
                s.visit(v);
            }
        }
        return v.refs>0;
    }
    
    private final class FindResultVisitor extends Visitor {
        Node result = null;
        TypedDeclaration resultDeclaration = null;
        final Tree.Body scope;
        final Collection<Tree.Statement> statements;
        FindResultVisitor(Tree.Body scope, 
                Collection<Tree.Statement> statements) {
            this.scope = scope;
            this.statements = statements;
        }
        @Override
        public void visit(Tree.Body that) {
            if (that instanceof Tree.Block) {
                super.visit(that);
            }
        }
        @Override
        public void visit(Tree.AttributeDeclaration that) {
            super.visit(that);
            Value dec = that.getDeclarationModel();
            if (hasOuterRefs(dec, scope, statements)) {
                result = that;
                resultDeclaration = dec;
            }
        }
        @Override
        public void visit(Tree.AssignmentOp that) {
            super.visit(that);
            Tree.Term leftTerm = that.getLeftTerm();
            if (leftTerm instanceof Tree.StaticMemberOrTypeExpression) {
                Tree.StaticMemberOrTypeExpression smte = 
                        (Tree.StaticMemberOrTypeExpression) 
                            leftTerm;
                Declaration dec = 
                        smte.getDeclaration();
                if (hasOuterRefs(dec, scope, statements) && 
                        isDefinedLocally(dec)) {
                    result = that;
                    resultDeclaration = (TypedDeclaration) dec;
                }
            }
        }
        @Override
        public void visit(Tree.SpecifierStatement that) {
            super.visit(that);
            Tree.Term term = that.getBaseMemberExpression();
            if (term instanceof Tree.StaticMemberOrTypeExpression) {
                Tree.StaticMemberOrTypeExpression smte = 
                        (Tree.StaticMemberOrTypeExpression) 
                            term;
                Declaration dec = 
                        smte.getDeclaration();
                if (hasOuterRefs(dec, scope, statements) && 
                        isDefinedLocally(dec)) {
                    result = that;
                    resultDeclaration = (TypedDeclaration) dec;
                }
            }
        }
        private boolean isDefinedLocally(Declaration dec) {
            return !ModelUtil.contains(dec.getScope(), 
                    scope.getScope().getContainer());
        }
    }

    private final class FindReturnsVisitor extends Visitor {
        final Collection<Tree.Return> returns;
        FindReturnsVisitor(Collection<Tree.Return> returns) {
            this.returns = returns;
        }
        @Override
        public void visit(Tree.Declaration that) {}
        @Override
        public void visit(Tree.Return that) {
            super.visit(that);
            if (that.getExpression()!=null) {
                returns.add(that);
            }
        }
    }

    private static final class FindLocalReferencesVisitor extends Visitor {
        List<Tree.BaseMemberExpression> localReferences = 
                new ArrayList<Tree.BaseMemberExpression>();
        private Scope scope;
        private Scope targetScope;
        private FindLocalReferencesVisitor(Scope scope, Scope targetScope) {
            this.scope = scope;
            this.targetScope = targetScope;
        }
        public List<Tree.BaseMemberExpression> getLocalReferences() {
            return localReferences;
        }
        @Override
        public void visit(Tree.BaseMemberExpression that) {
            super.visit(that);
            //TODO: don't treat assignments as references, but
            //      then we have to declare a new local in the
            //      extracted function!
//            if (!that.getAssigned()) {
                //TODO: things nested inside control structures
                Declaration currentDec = that.getDeclaration();
                for (Tree.BaseMemberExpression bme: localReferences) {
                    Declaration dec = bme.getDeclaration();
                    if (dec.equals(currentDec)) {
                        return;
                    }
                    if (currentDec instanceof TypedDeclaration) {
                        TypedDeclaration od = 
                                ((TypedDeclaration)currentDec).getOriginalDeclaration();
                        if (od!=null && od.equals(dec)) return;
                    }
                }
                if (currentDec.isDefinedInScope(scope) && 
                        !currentDec.isDefinedInScope(targetScope)) {
                    localReferences.add(that);
                }
            }
//        }
    }

    private String newName;
    private boolean explicitType;
    private Node result;
    private TypedDeclaration resultDeclaration;
    private List<Tree.Statement> statements;
    List<Tree.Return> returns;
    private Type returnType;

    public ExtractFunctionRefactoring(IEditorPart editor) {
        super(editor);
        if (rootNode!=null) {
            if (editor instanceof CeylonEditor) {
                CeylonEditor ce = (CeylonEditor) editor;
                if (ce.getSelectionProvider()!=null) {
                    init(getSelection(ce));
                }
            }
            if (resultDeclaration!=null) {
                newName = resultDeclaration.getName();
            }
            else {
                newName = Nodes.nameProposals(node)[0];
                if ("it".equals(newName)) {
                    newName = "do";
                }
            }
        }
    }

    private void init(ITextSelection selection) {
        Tree.Body body;
        if (node instanceof Tree.Body) {
            body = (Tree.Body) node;
            statements = getStatements(body, selection);
        }
        else if (node instanceof Tree.Statement) {
            class FindBodyVisitor extends Visitor {
                Tree.Body body;
                @Override
                public void visit(Tree.Body that) {
                    super.visit(that);
                    if (that.getStatements().contains(node)) {
                        body = that;
                    }
                }
            }
            FindBodyVisitor fbv = new FindBodyVisitor();
            fbv.visit(rootNode);
            body = fbv.body;
            statements = singletonList((Tree.Statement) node);
            node = body; //TODO: wow, ugly!!!!!
        }
        else {
            return;
        }
        for (Tree.Statement s: statements) {
            FindResultVisitor v = 
                    new FindResultVisitor(body, statements);
            s.visit(v);
            if (v.result!=null) {
                result = v.result;
                resultDeclaration = v.resultDeclaration;
                break;
            }
        }
        returns = new ArrayList<Tree.Return>();
        for (Tree.Statement s: statements) {
            FindReturnsVisitor v = 
                    new FindReturnsVisitor(returns);
            s.visit(v);
        }
    }

    @Override
    public boolean getEnabled() {
        return sourceFile!=null &&
                getEditable() &&
                !sourceFile.getName().equals("module.ceylon") &&
                !sourceFile.getName().equals("package.ceylon") &&
                (node instanceof Tree.Term || 
                 node instanceof Tree.Body &&
                    !statements.isEmpty() &&
                    !containsConstructor(statements));
    }
    
    private boolean containsConstructor(List<Tree.Statement> statements) {
        for (Tree.Statement statement : statements) {
            if (statement instanceof Tree.Constructor) {
                return true;
            }
        }
        return false;
    }
    
    public String getName() {
        return "Extract Function";
    }
    
    public boolean forceWizardMode() {
        if (node instanceof Tree.Body) {
            Tree.Body body = (Tree.Body) node;
            for (Tree.Statement s: statements) {
                CheckStatementsVisitor v = 
                        new CheckStatementsVisitor(body, 
                                statements);
                s.visit(v);
                if (v.problem!=null) {
                    return true;
                }
            }
        }
        else if (node instanceof Tree.Term) {
            CheckExpressionVisitor v = 
                    new CheckExpressionVisitor();
            node.visit(v);
            if (v.problem!=null) {
                return true;
            }
        }
        Declaration existing = 
                node.getScope()
                    .getMemberOrParameter(node.getUnit(), 
                            newName, null, false);
        return existing!=null;
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        if (node instanceof Tree.Body) {
            Tree.Body body = (Tree.Body) node;
            for (Tree.Statement s: statements) {
                CheckStatementsVisitor v = 
                        new CheckStatementsVisitor(body, 
                                statements);
                s.visit(v);
                if (v.problem!=null) {
                    return createWarningStatus(
                            "Selected statements contain "
                            + v.problem + " at  " + 
                                    s.getLocation());
                }
            }
        }
        else if (node instanceof Tree.Term) {
            CheckExpressionVisitor v = 
                    new CheckExpressionVisitor();
            node.visit(v);
            if (v.problem!=null) {
                return createWarningStatus(
                        "Selected expression contains "
                        + v.problem);
            }
        }
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        Declaration existing = 
                node.getScope()
                    .getMemberOrParameter(node.getUnit(), 
                            newName, null, false);
        if (null!=existing) {
            return createWarningStatus(
                    "An existing declaration named '" +
                    newName + 
                    "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    private IRegion decRegion;
    private IRegion refRegion;
    private IRegion typeRegion;

    @Override
    public IRegion getTypeRegion() {
        return typeRegion;
    }
    @Override
    public void setTypeRegion(IRegion region) {
        typeRegion = region;
    }
    @Override
    public IRegion getDecRegion() {
        return decRegion;
    }
    @Override
    public void setDecRegion(IRegion region) {
        decRegion = region;
    }
    @Override
    public IRegion getRefRegion() {
        return refRegion;
    }
    @Override
    public void setRefRegion(IRegion region) {
        refRegion=region;
    }

    
	private boolean canBeInferred;

    public Change createChange(IProgressMonitor pm) 
            throws CoreException,
                   OperationCanceledException {
        TextChange tfc = newLocalChange();
        extractInFile(tfc);
        return tfc;
    }

    public void extractInFile(TextChange tfc) {
        if (node instanceof Tree.Term) {
            extractExpressionInFile(tfc);
        }
        else if (node instanceof Tree.Body) {
            extractStatementsInFile(tfc);
        }
    }

    private void extractExpressionInFile(TextChange tfc) {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = EditorUtil.getDocument(tfc);
        
        Tree.Term term = (Tree.Term) node;
        Integer start = term.getStartIndex();
        int length = term.getDistance();
        Tree.Term unparened = unparenthesize(term);
        String body;
        TypecheckerUnit unit = node.getUnit();
        if (unparened instanceof Tree.FunctionArgument) {
            Tree.FunctionArgument fa = 
                    (Tree.FunctionArgument) unparened;
            returnType = fa.getType().getTypeModel();
            if (fa.getBlock()!=null) {
                body = toString(fa.getBlock());
            }
            else if (fa.getExpression()!=null) {
                body = "=> " + toString(fa.getExpression()) + ";";
            }
            else {
                body = "=>;";
            }
        }
        else {
            returnType = unit
                    .denotableType(term.getTypeModel());
            body = "=> " + toString(unparened) + ";";
        }
        
        FindContainerVisitor fsv = 
                new FindContainerVisitor(term);
        rootNode.visit(fsv);
        Tree.Declaration decNode = fsv.getDeclaration();
        Declaration dec = decNode.getDeclarationModel();
        FindLocalReferencesVisitor flrv = 
                new FindLocalReferencesVisitor(
                        node.getScope(), 
                        getContainingScope(decNode));
        term.visit(flrv);
        List<Tree.BaseMemberExpression> localRefs = 
                flrv.getLocalReferences();
        List<TypeDeclaration> localTypes = 
                new ArrayList<TypeDeclaration>();
        for (Tree.BaseMemberExpression bme: localRefs) {
            addLocalType(dec, 
                    unit.denotableType(bme.getTypeModel()), 
                    localTypes, 
                    new ArrayList<Type>());
        }
        
        StringBuilder params = new StringBuilder();
        StringBuilder args = new StringBuilder();
        if (!localRefs.isEmpty()) {
            boolean first = true;
            for (Tree.BaseMemberExpression bme: localRefs) {
                if (first) {
                    first = false;
                }
                else {
                    params.append(", ");
                    args.append(", ");
                }
                Declaration pdec = bme.getDeclaration();
                if (pdec instanceof TypedDeclaration && 
                        ((TypedDeclaration) pdec).isDynamicallyTyped()) {
                    params.append("dynamic");
                }
                else {
                    params.append(unit.denotableType(bme.getTypeModel())
                            .asSourceCodeString(unit));
                }
                String name = bme.getIdentifier().getText();
                params.append(" ").append(name);
                args.append(name);
            }
        }
        
        String indent = 
                indents().getDefaultLineDelimiter(doc) + 
                indents().getIndent(decNode, doc);
        String extraIndent = indent + indents().getDefaultIndent();

        StringBuilder typeParams = new StringBuilder();
        StringBuilder constraints = new StringBuilder();
        if (!localTypes.isEmpty()) {
            typeParams.append("<");
            boolean first = true;
            for (TypeDeclaration t: localTypes) {
                if (first) {
                    first = false;
                }
                else {
                    typeParams.append(", ");
                }
                typeParams.append(t.getName());
                if (!t.getSatisfiedTypes().isEmpty()) {
                    constraints.append(extraIndent)
                            .append(indents().getDefaultIndent()) 
                            .append("given ")
                            .append(t.getName())
                            .append(" satisfies ");
                    boolean firstConstraint = true;
                    for (Type pt: t.getSatisfiedTypes()) {
                        if (firstConstraint) {
                            firstConstraint = false;
                        }
                        else {
                            constraints.append("&");
                        }
                        constraints.append(pt.asSourceCodeString(unit));
                    }
                }
            }
            typeParams.append(">");
        }
        
        int il;
        String type;
        if (returnType==null || returnType.isUnknown()) {
            type = "dynamic";
            il = 0;
        }
        else {
            boolean isVoid = returnType.isAnything();
            if (isVoid) {
                type = "void";
                il = 0;
            }
            else if (explicitType || dec.isToplevel()) {
                type = returnType.asSourceCodeString(unit);
                HashSet<Declaration> decs = new HashSet<Declaration>();
                importProposals().importType(decs, returnType, rootNode);
                il = (int) importProposals().applyImports(tfc, decs, rootNode, doc);
            }
            else {
                type = "function";
                il = 0;
                canBeInferred = true;
            }
        }

        String text = 
                type + " " + newName + typeParams + "(" + params + ")" + 
                constraints + " " + body + indent + indent;
        String invocation;
        int refStart;
        if (unparened instanceof Tree.FunctionArgument) {
            Tree.FunctionArgument fa = 
                    (Tree.FunctionArgument) node;
            Tree.ParameterList cpl = 
                    fa.getParameterLists().get(0);
            if (cpl.getParameters().size()==localRefs.size()) {
                invocation = newName;
                refStart = start;
            }
            else {
                String header = 
                        Nodes.toString(cpl, tokens) + " => ";
                invocation = header + newName + "(" + args + ")";
                refStart = start + header.length(); {
                    
                }
            }
        }
        else {
            invocation = newName + "(" + args + ")";
            refStart = start;
        }
        Integer decStart = decNode.getStartIndex();
        tfc.addEdit(new InsertEdit(decStart, text));
        tfc.addEdit(new ReplaceEdit(start, length, invocation));
        typeRegion = new Region(decStart+il, type.length());
        int nl = newName.length();
        decRegion = new Region(decStart+il+type.length()+1, nl);
        refRegion = new Region(refStart+il+text.length(), nl);
    }

    private Scope getContainingScope(Tree.Declaration decNode) {
        return decNode.getDeclarationModel().getContainer();
    }

    private void extractStatementsInFile(TextChange tfc) {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = EditorUtil.getDocument(tfc);
        final Unit unit = node.getUnit();
        
        Tree.Body body = (Tree.Body) node;
        
        int start = 
                statements.get(0)
                    .getStartIndex();
        int length = 
                statements.get(statements.size()-1)
                    .getEndIndex() 
                        - start;
        FindContainerVisitor fsv = 
                new FindContainerVisitor(body);
        rootNode.visit(fsv);
        Tree.Declaration decNode = fsv.getDeclaration();
        FindLocalReferencesVisitor flrv = 
                new FindLocalReferencesVisitor(
                        node.getScope(),
                        getContainingScope(decNode));
        for (Tree.Statement s: statements) {
            s.visit(flrv); {
                
            }
        }
        final List<TypeDeclaration> localTypes = 
                new ArrayList<TypeDeclaration>();
        List<BaseMemberExpression> localReferences = 
                flrv.getLocalReferences();
        final Declaration dec = 
                decNode.getDeclarationModel();
        for (Tree.BaseMemberExpression bme: localReferences) {
            addLocalType(dec, 
                    unit.denotableType(
                            bme.getTypeModel()), 
                            localTypes, 
                            new ArrayList<Type>());
        }
        for (Tree.Statement s: statements) {
            new Visitor() {
                public void visit(Tree.TypeArgumentList that) {
                    for (Type pt: that.getTypeModels()) {
                        addLocalType(dec, 
                                unit.denotableType(pt), 
                                localTypes, 
                                new ArrayList<Type>());
                    }
                }
            }.visit(s);
        }
        
        HashSet<Declaration> movingDecs = 
                new HashSet<Declaration>();
        for (Tree.Statement s: statements) {
            if (s instanceof Tree.Declaration) {
                Tree.Declaration d = (Tree.Declaration) s;
                movingDecs.add(d.getDeclarationModel());
            }
        }
        
        String params = "";
        String args = "";
        Set<Declaration> done = 
                new HashSet<Declaration>(movingDecs);
        boolean nonempty = false;
        for (Tree.BaseMemberExpression bme: 
                localReferences) {
            Declaration bmed = bme.getDeclaration();
            if (resultDeclaration==null ||
                    !bmed.equals(resultDeclaration) || 
                    resultDeclaration.isVariable()) { //TODO: wrong condition, check if initialized!
                if (done.add(bmed)) {
                    if (bmed instanceof Value && 
                            ((Value) bmed).isVariable()) {
                        params += "variable ";
                    } { {
                        
                    }
                        
                    }
                    if (bmed instanceof TypedDeclaration && 
                            ((TypedDeclaration) bmed).isDynamicallyTyped()) {
                        params += "dynamic";
                    }
                    else {
                        params += unit.denotableType(bme.getTypeModel())
                                .asSourceCodeString(unit);
                    }
                    params += " " + bme.getIdentifier().getText() + ", ";
                    args += bme.getIdentifier().getText() + ", ";
                    nonempty = true;
                }
            }
        }
        if (nonempty) {
            params = params.substring(0, params.length()-2);
            args = args.substring(0, args.length()-2);
        }
        
        String indent = 
                indents().getDefaultLineDelimiter(doc) + 
                indents().getIndent(decNode, doc);
        String extraIndent = 
                indent + indents().getDefaultIndent();

        String typeParams = "";
        String constraints = "";
        if (!localTypes.isEmpty()) {
            for (TypeDeclaration t: localTypes) {
                typeParams += t.getName() + ", ";
                List<Type> sts = 
                        t.getSatisfiedTypes();
                if (!sts.isEmpty()) {
                    constraints += extraIndent + 
                            indents().getDefaultIndent() + 
                            "given " + 
                            t.getName() + 
                            " satisfies ";
                    for (Type pt: sts) {
                        constraints += 
                                pt.asSourceCodeString(unit) + "&";
                    }
                    constraints = 
                            constraints.substring(0, 
                                    constraints.length()-1);
                }
            }
            typeParams = "<" + typeParams.substring(0, typeParams.length()-2) + ">";
        }
        
        if (resultDeclaration!=null) {
            returnType = unit.denotableType(resultDeclaration.getType());
        }
        else if (!returns.isEmpty())  {
            UnionType ut = new UnionType(unit);
            List<Type> list = 
                    new ArrayList<Type>();
            for (Tree.Return r: returns) {
                addToUnion(list, 
                        r.getExpression().getTypeModel());
            }
            ut.setCaseTypes(list);
            returnType = ut.getType();
        }
        else {
            returnType = null;
        }
        String content;
        int il = 0;
        if (resultDeclaration!=null || !returns.isEmpty()) {
            if (returnType.isUnknown()) {
                content = "dynamic";
            }
            else if (explicitType||dec.isToplevel()) {
                content = returnType.asSourceCodeString(unit);
                HashSet<Declaration> already = 
                        new HashSet<Declaration>();
                importProposals().importType(already, returnType, rootNode);
                il = (int) importProposals().applyImports(tfc, already, rootNode, doc);
            }
            else {
                content = "function";
            }
        }
        else {
            content = "void";
        }
        content += " " + newName + typeParams + "(" + params + ")" + 
                constraints + " {";
        if (resultDeclaration!=null && 
                !(result instanceof Tree.Declaration) &&
                !resultDeclaration.isVariable()) { //TODO: wrong condition, check if initialized!
            content += extraIndent +
                resultDeclaration.getType()
                    .asSourceCodeString(unit) +
                " " + resultDeclaration.getName() + ";";
        }
        Tree.Statement last = 
                statements.isEmpty() ? null : 
                    statements.get(statements.size()-1);
        for (Tree.Statement s: statements) {
            content += extraIndent + toString(s);
            int i = s.getEndToken().getTokenIndex();
            CommonToken tok;
            while ((tok=tokens.get(++i)).getChannel()==HIDDEN_CHANNEL) {
                String text = tok.getText();
                if (tok.getType()==LINE_COMMENT) {
                    content += " " + 
                            text.substring(0, text.length()-1);
                    if (s==last) {
                        length += text.length();
                    }
                }
                if (tok.getType()==MULTI_COMMENT) {
                    content += " " + text;
                    if (s==last) {
                        length += text.length()+1;
                    }
                }
            }
        }
        if (resultDeclaration!=null) {
            content += extraIndent + "return " + 
                    resultDeclaration.getName() + ";";
        }
        content += indent + "}" + indent + indent;
        
        String invocation = newName + "(" + args + ");";
        if (resultDeclaration!=null) {
            String modifs = "";
            if (result instanceof Tree.AttributeDeclaration) {
                if (resultDeclaration.isShared()) {
                    modifs = "shared " + 
                            returnType.asSourceCodeString(unit) + 
                            " ";
                }
                else {
                    modifs = "value ";
                }
            }
            invocation = modifs + 
                    resultDeclaration.getName() + 
                    "=" + invocation;
        }
        else if (!returns.isEmpty()) {
            invocation = "return " + invocation;
        }
        
        Integer decStart = decNode.getStartIndex();
        tfc.addEdit(new InsertEdit(decStart, content));        
        tfc.addEdit(new ReplaceEdit(start, length, invocation));
        typeRegion = new Region(decStart+il, content.indexOf(' '));
        decRegion = new Region(decStart+il+content.indexOf(' ')+1, newName.length());
        refRegion = new Region(start+content.length()+il+invocation.indexOf('=')+1, newName.length());
    }

    private List<Tree.Statement> getStatements(Tree.Body body, ITextSelection selection) {
        List<Tree.Statement> statements = new ArrayList<Tree.Statement>();
        for (Tree.Statement s: body.getStatements()) {
            if (s.getStartIndex()>=selection.getOffset() &&
                s.getEndIndex()<=selection.getOffset()+selection.getLength()) {
                statements.add(s);
            }
        }
        return statements;
    }

    private void addLocalType(Declaration dec, Type type,
            List<TypeDeclaration> localTypes, List<Type> visited) {
        if (visited.contains(type)) {
            return;
        }
        else {
            visited.add(type);
        }
        TypeDeclaration td = type.getDeclaration();
        if (td.getContainer()==dec) {
            boolean found=false;
            for (TypeDeclaration typeDeclaration: localTypes) {
                if (typeDeclaration==td) {
                    found=true; 
                    break;
                }
            }
            if (!found) {
                localTypes.add(td);
            }
        }
        for (Type pt: type.getSatisfiedTypes()) {
            addLocalType(dec, pt, localTypes, visited);
        }
        for (Type pt: type.getTypeArgumentList()) {
            addLocalType(dec, pt, localTypes, visited);
        }
    }


    public void setNewName(String text) {
        newName = text;
    }
    
    public String getNewName() {
        return newName;
    }
    
    public void setExplicitType() {
        this.explicitType = !explicitType;
    }

    Type getType() {
        return returnType;
    }
    
	public String[] getNameProposals() {
		return Nodes.nameProposals(node);
	}
    
    public boolean canBeInferred() {
        return canBeInferred;
    }

    @Override
    public IRegion newRegion(long start, long length) {
        return new Region((int) start, (int) length);
    }
}
