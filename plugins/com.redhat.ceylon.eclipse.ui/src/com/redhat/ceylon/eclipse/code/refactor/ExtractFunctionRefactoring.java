package com.redhat.ceylon.eclipse.code.refactor;

import static org.antlr.runtime.Token.HIDDEN_CHANNEL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LINE_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;
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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Util;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Return;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;

public class ExtractFunctionRefactoring extends AbstractRefactoring {
    
    private final class FindOuterReferencesVisitor extends Visitor {
        final Declaration declaration;
        int refs = 0;
        FindOuterReferencesVisitor(Declaration declaration) {
            this.declaration = declaration;
        }
        @Override
        public void visit(Tree.MemberOrTypeExpression that) {
            super.visit(that);
            if (that.getDeclaration().equals(declaration)) {
                refs++;
            }
        }
        @Override
        public void visit(Tree.Declaration that) {
            super.visit(that);
            if (that.getDeclarationModel().equals(declaration)) {
                refs++;
            }
        }
        @Override
        public void visit(Tree.Type that) {
            super.visit(that);
            if (that.getTypeModel().getDeclaration().equals(declaration)) {
                refs++;
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
        final Collection<Statement> statements;
        CheckStatementsVisitor(Tree.Body scope, 
                Collection<Statement> statements) {
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
            if (that.getBaseMemberExpression() instanceof Tree.MemberOrTypeExpression) {
                Declaration d = ((Tree.MemberOrTypeExpression) that.getBaseMemberExpression()).getDeclaration();
                if (notResultRef(d) && hasOuterRefs(d, scope, statements)) {
                    problem = "a specification statement for a declaration used or defined elsewhere";
                }
            }
        }
        @Override
        public void visit(Tree.AssignmentOp that) {
            super.visit(that);
            if (that.getLeftTerm() instanceof Tree.MemberOrTypeExpression) {
                Declaration d = ((Tree.MemberOrTypeExpression) that.getLeftTerm()).getDeclaration();
                if (notResultRef(d) && hasOuterRefs(d, scope, statements)) {
                    problem = "an assignment to a declaration used or defined elsewhere";
                }
            }
        }
        private boolean notResultRef(Declaration d) {
            return result==null || !result.getDeclarationModel().equals(d);
        }
        @Override
        public void visit(Tree.Directive that) {
            super.visit(that);
            problem = "a directive statement";
        }
    }

    private boolean hasOuterRefs(Declaration d, Tree.Body scope, 
            Collection<Statement> statements) {
        FindOuterReferencesVisitor v = 
                new FindOuterReferencesVisitor(d);
        for (Statement s: scope.getStatements()) {
            if (!statements.contains(s)) {
                s.visit(v);
            }
        }
        return v.refs>0;
    }
    
    private final class FindResultVisitor extends Visitor {
        Tree.AttributeDeclaration result = null;
        final Tree.Body scope;
        final Collection<Statement> statements;
        FindResultVisitor(Tree.Body scope, 
                Collection<Statement> statements) {
            this.scope = scope;
            this.statements = statements;
        }
        @Override
        public void visit(Tree.Body that) {}
        @Override
        public void visit(Tree.AttributeDeclaration that) {
            super.visit(that);
            if (hasOuterRefs(that.getDeclarationModel(), 
                    scope, statements)) {
                result = that;
            }
        }
    }

    private final class FindReturnsVisitor extends Visitor {
        final Collection<Return> returns;
        FindReturnsVisitor(Collection<Return> returns) {
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
            //TODO: things nested inside control structures
            Declaration currentDec = that.getDeclaration();
            for (Tree.BaseMemberExpression bme: localReferences) {
                Declaration dec = bme.getDeclaration();
                if (dec.equals(currentDec)) {
                    return;
                }
                if (currentDec instanceof TypedDeclaration) {
                    TypedDeclaration od = ((TypedDeclaration)currentDec).getOriginalDeclaration();
                    if (od!=null && od.equals(dec)) return;
                }
            }
            if (currentDec.isDefinedInScope(scope) && 
                !currentDec.isDefinedInScope(targetScope)) {
                localReferences.add(that);
            }
        }
    }

    private String newName;
    private boolean explicitType;
    private Tree.AttributeDeclaration result;
    private List<Statement> statements;
    List<Return> returns;

    public ExtractFunctionRefactoring(ITextEditor editor) {
        super(editor);
        if (editor instanceof CeylonEditor && 
                editor.getSelectionProvider()!=null) {
            init((ITextSelection) editor.getSelectionProvider()
                    .getSelection());
        }
        if (result!=null) {
            newName = result.getDeclarationModel().getName();
        }
        else {
            newName = guessName(node);
        }
    }

    private void init(ITextSelection selection) {
        if (node instanceof Tree.Body) {
            Tree.Body body = (Tree.Body) node;
            statements = getStatements(body, selection);
            for (Statement s: statements) {
                FindResultVisitor v = new FindResultVisitor(body, statements);
                s.visit(v);
                if (v.result!=null) {
                    result = v.result;
                }
            }
            returns = new ArrayList<Return>();
            for (Statement s: statements) {
                FindReturnsVisitor v = new FindReturnsVisitor(returns);
                s.visit(v);
            }
            
        }
    }

    @Override
    boolean isEnabled() {
        return node instanceof Tree.Term || 
                node instanceof Tree.Body && 
                    !statements.isEmpty();
    }
    
    public String getName() {
        return "Extract Function";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        if (node instanceof Tree.Body) {
            Tree.Body body = (Tree.Body) node;
            for (Statement s: statements) {
                CheckStatementsVisitor v = new CheckStatementsVisitor(body, statements);
                s.visit(v);
                if (v.problem!=null) {
                    return createWarningStatus("Selected statements contain "
                            + v.problem + " at  " + s.getLocation());
                }
            }
        }
        else if (node instanceof Tree.Term) {
            CheckExpressionVisitor v = new CheckExpressionVisitor();
            node.visit(v);
            if (v.problem!=null) {
                return createWarningStatus("Selected expression contains "
                        + v.problem);
            }
        }
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        Declaration existing = node.getScope()
                .getMemberOrParameter(node.getUnit(), newName, null, false);
        if (null!=existing) {
            return createWarningStatus("An existing declaration named '" +
                    newName + "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        TextChange tfc = newLocalChange();
        if (node instanceof Tree.Term) {
            extractExpressionInFile(tfc);
        }
        else if (node instanceof Tree.Body) {
            extractStatementsInFile(tfc);
        }
        return tfc;
    }

    private void extractExpressionInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = tfc.getCurrentDocument(null);
        
        Tree.Term term = (Tree.Term) node;
        Integer start = term.getStartIndex();
        int length = term.getStopIndex()-start+1;
        String exp = toString(unparenthesize(term));
        FindContainerVisitor fsv = new FindContainerVisitor(term);
        rootNode.visit(fsv);
        Tree.Declaration decNode = fsv.getDeclaration();
        Declaration dec = decNode.getDeclarationModel();
        FindLocalReferencesVisitor flrv = new FindLocalReferencesVisitor(node.getScope(), 
                getContainingScope(decNode));
        term.visit(flrv);
        List<TypeDeclaration> localTypes = new ArrayList<TypeDeclaration>();
        for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
            addLocalType(dec, node.getUnit().denotableType(bme.getTypeModel()), 
                    localTypes, new ArrayList<ProducedType>());
        }
        
        String params = "";
        String args = "";
        if (!flrv.getLocalReferences().isEmpty()) {
            for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
                Declaration pdec = bme.getDeclaration();
                if (pdec instanceof TypedDeclaration && 
                        ((TypedDeclaration) pdec).isDynamicallyTyped()) {
                    params += "dynamic";
                }
                else {
                    params += node.getUnit().denotableType(bme.getTypeModel()).getProducedTypeName();
                }
                params += " " + bme.getIdentifier().getText() + ", ";
                args += bme.getIdentifier().getText() + ", ";
            }
            params = params.substring(0, params.length()-2);
            args = args.substring(0, args.length()-2);
        }
        
        String indent = System.lineSeparator() + 
        		getIndent(decNode, doc);
        String extraIndent = indent + getDefaultIndent();

        String typeParams = "";
        String constraints = "";
        if (!localTypes.isEmpty()) {
            for (TypeDeclaration t: localTypes) {
                typeParams += t.getName() + ", ";
                if (!t.getSatisfiedTypes().isEmpty()) {
                    constraints += extraIndent + getDefaultIndent() + 
                            "given " + t.getName() + " satisfies ";
                    for (ProducedType pt: t.getSatisfiedTypes()) {
                        constraints += pt.getProducedTypeName() + "&";
                    }
                    constraints = constraints.substring(0, constraints.length()-1);
                }
            }
            typeParams = "<" + typeParams.substring(0, typeParams.length()-2) + ">";
        }
        
        String type;
        ProducedType tt = term.getTypeModel();
        if (tt==null || tt.isUnknown()) {
            type = "dynamic";
        }
        else {
            boolean isVoid = (tt.getDeclaration() instanceof Class) && 
                    tt.getDeclaration().equals(term.getUnit().getAnythingDeclaration());
            if (isVoid) {
                type = "void";
            }
            else {
                if (explicitType || dec.isToplevel()) {
                    ProducedType returnType = node.getUnit().denotableType(tt);
                    type = returnType.getProducedTypeName();
                    HashSet<Declaration> decs = new HashSet<Declaration>();
                    importType(decs, returnType, rootNode);
                    applyImports(tfc, decs, rootNode);
                }
                else {
                    type = "function";
                }
            }
        }

        tfc.addEdit(new InsertEdit(decNode.getStartIndex(),
                type + " " + newName + typeParams + "(" + params + ")" + 
                        constraints + 
                        " => " + exp + ";" 
                        + indent + indent));
        tfc.addEdit(new ReplaceEdit(start, length, newName + "(" + args + ")"));
    }

    private Scope getContainingScope(Tree.Declaration decNode) {
    	return decNode.getDeclarationModel().getContainer().getScope();
    }

    private void extractStatementsInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = tfc.getCurrentDocument(null);
        final Unit unit = node.getUnit();
        
        Tree.Body body = (Tree.Body) node;
        
        Integer start = statements.get(0).getStartIndex();
        int length = statements.get(statements.size()-1)
                .getStopIndex()-start+1;
        FindContainerVisitor fsv = new FindContainerVisitor(body);
        rootNode.visit(fsv);
        Tree.Declaration decNode = fsv.getDeclaration();
        final Declaration dec = decNode.getDeclarationModel();
        FindLocalReferencesVisitor flrv = new FindLocalReferencesVisitor(node.getScope(),
                getContainingScope(decNode));
        for (Statement s: statements) {
            s.visit(flrv);
        }
        final List<TypeDeclaration> localTypes = new ArrayList<TypeDeclaration>();
        for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
            addLocalType(dec, unit.denotableType(bme.getTypeModel()), 
                  localTypes, new ArrayList<ProducedType>());
        }
        for (Statement s: statements) {
            new Visitor() {
                public void visit(Tree.TypeArgumentList that) {
                    for (ProducedType pt: that.getTypeModels()) {
                        addLocalType(dec, unit.denotableType(pt), 
                                localTypes, new ArrayList<ProducedType>());
                    }
                }
            }.visit(s);
        }
        //TODO: what was all this for?!
//      List<Tree.BaseMemberExpression> localRefs = new ArrayList<Tree.BaseMemberExpression>();
//        for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
//            if (result==null || !bme.getDeclaration().equals(result.getDeclarationModel())) {
//                FindOuterReferencesVisitor v = new FindOuterReferencesVisitor(bme.getDeclaration());
//                for (Statement s: body.getStatements()) {
//                    if (!statements.contains(s)) {
//                        s.visit(v);
//                    }
//                }
//                if (v.refs>0) {
//                    addLocalType(dec, unit.denotableType(bme.getTypeModel()), 
//                            localTypes, new ArrayList<ProducedType>());
//                    localRefs.add(bme);
//                }
//            }
//        }
        
        HashSet<Declaration> movingDecs = new HashSet<Declaration>();
        for (Statement s: statements) {
            if (s instanceof Tree.Declaration) {
                movingDecs.add(((Tree.Declaration) s).getDeclarationModel());
            }
        }
        
        String params = "";
        String args = "";
        Set<Declaration> done = new HashSet<Declaration>(movingDecs);
        boolean nonempty = false;
        for (Tree.BaseMemberExpression bme: flrv.getLocalReferences()) {
            Declaration bmed = bme.getDeclaration();
            if (done.add(bmed)) {
                if (bmed instanceof TypedDeclaration && 
                        ((TypedDeclaration) bmed).isDynamicallyTyped()) {
                    params += "dynamic";
                }
                else {
                    params += unit.denotableType(bme.getTypeModel()).getProducedTypeName();
                }
                params += " " + bme.getIdentifier().getText() + ", ";
                args += bme.getIdentifier().getText() + ", ";
                nonempty = true;
            }
        }
        if (nonempty) {
            params = params.substring(0, params.length()-2);
            args = args.substring(0, args.length()-2);
        }
        
        String indent = System.lineSeparator() + 
        		getIndent(decNode, doc);
        String extraIndent = indent + getDefaultIndent();

        String typeParams = "";
        String constraints = "";
        if (!localTypes.isEmpty()) {
            for (TypeDeclaration t: localTypes) {
                typeParams += t.getName() + ", ";
                if (!t.getSatisfiedTypes().isEmpty()) {
                    constraints += extraIndent + getDefaultIndent() + 
                            "given " + t.getName() + " satisfies ";
                    for (ProducedType pt: t.getSatisfiedTypes()) {
                        constraints += pt.getProducedTypeName() + "&";
                    }
                    constraints = constraints.substring(0, constraints.length()-1);
                }
            }
            typeParams = "<" + typeParams.substring(0, typeParams.length()-2) + ">";
        }
        
        ProducedType returnType;
        if (result!=null) {
            returnType = unit.denotableType(result.getDeclarationModel().getType());
        }
        else if (!returns.isEmpty())  {
            UnionType ut = new UnionType(unit);
            List<ProducedType> list = new ArrayList<ProducedType>();
            for (Return r: returns) {
                Util.addToUnion(list, r.getExpression().getTypeModel());
            }
            ut.setCaseTypes(list);
            returnType = ut.getType();
        }
        else {
            returnType = null;
        }
        String content;
        if (result!=null||!returns.isEmpty()) {
            if (returnType.isUnknown()) {
                content = "dynamic";
            }
            else if (explicitType||dec.isToplevel()) {
                content = returnType.getProducedTypeName();
                HashSet<Declaration> already = new HashSet<Declaration>();
                importType(already, returnType, rootNode);
                applyImports(tfc, already, rootNode);
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
        Statement last = statements.isEmpty() ?
        		null : statements.get(statements.size()-1);
        for (Statement s: statements) {
            content += extraIndent + toString(s);
            int i = s.getEndToken().getTokenIndex();
            CommonToken tok;
            while ((tok=tokens.get(++i)).getChannel()==HIDDEN_CHANNEL) {
            	String text = tok.getText();
				if (tok.getType()==LINE_COMMENT) {
            		content += " " + text.substring(0, text.length()-1);
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
        if (result!=null) {
            content += extraIndent + "return " + 
            		result.getDeclarationModel().getName() + ";";
        }
        content += indent + "}" + indent + indent;
        
        String invocation = newName + "(" + args + ");";
        if (result!=null) {
            String modifs;
            if (result.getDeclarationModel().isShared()) {
                modifs = "shared " + returnType.getProducedTypeName() + " ";
            }
            else {
                modifs = "value ";
            }
            invocation = modifs + result.getDeclarationModel().getName() + 
                    "=" + invocation;
        }
        else if (!returns.isEmpty()) {
            invocation = "return " + invocation;
        }
        
        tfc.addEdit(new InsertEdit(decNode.getStartIndex(), content));        
        tfc.addEdit(new ReplaceEdit(start, length, invocation));
    }

    private List<Statement> getStatements(Tree.Body body, ITextSelection selection) {
        List<Statement> statements = new ArrayList<Statement>();
        for (Tree.Statement s: body.getStatements()) {
            if (s.getStartIndex()>=selection.getOffset() &&
                    s.getStopIndex()<=selection.getOffset()+selection.getLength()) {
                statements.add(s);
            }
        }
        return statements;
    }

    private void addLocalType(Declaration dec, ProducedType type,
            List<TypeDeclaration> localTypes, List<ProducedType> visited) {
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
        for (ProducedType pt: td.getSatisfiedTypes()) {
            addLocalType(dec, pt, localTypes, visited);
        }
        for (ProducedType pt: type.getTypeArgumentList()) {
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
    
}
