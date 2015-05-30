package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedDeclaration;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createFatalErrorStatus;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
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
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Setter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;

public class InlineRefactoring extends AbstractRefactoring {
    
    private final Declaration declaration;
    private boolean delete = true;
    private boolean justOne = false;

    public InlineRefactoring(IEditorPart editor) {
        super(editor);
        Referenceable ref = getReferencedDeclaration(node);
        if (ref instanceof Declaration) {
            declaration = (Declaration) ref;
        }
        else {
            declaration = null;
        }
    }
    
    boolean isReference() {
        return !(node instanceof Tree.Declaration) &&
                getIdentifyingNode(node) instanceof Tree.Identifier;
    }
    
    @Override
    public boolean isEnabled() {
        return  declaration!=null &&
                project != null &&
                inSameProject(declaration) &&
                declaration instanceof FunctionOrValue &&
                !declaration.isParameter() &&
                !(declaration instanceof Setter) &&
                !declaration.isDefault() &&
                !declaration.isFormal() &&
                (((FunctionOrValue)declaration).getTypeDeclaration()!=null) &&
                (!((FunctionOrValue)declaration).getTypeDeclaration().isAnonymous()) &&
                (declaration.isToplevel() || !declaration.isShared() ||
                        (!declaration.isFormal() && !declaration.isDefault() && !declaration.isActual())) &&
                (!declaration.getUnit().equals(rootNode.getUnit()) || 
                 !(getDeclararionNode(rootNode) instanceof Tree.Variable)); //not a Destructure
                //TODO: && !declaration is a control structure variable 
                //TODO: && !declaration is a value with lazy init
    }
    
    public int getCount() {
        return declaration==null ? 0 : countDeclarationOccurrences();
    }
    
    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindReferencesVisitor frv = 
        		new FindReferencesVisitor(declaration);
        cu.visit(frv);
        return frv.getNodes().size();
    }

    public String getName() {
        return "Inline";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        final RefactoringStatus result = new RefactoringStatus();
        Tree.Declaration declarationNode=null;
        Tree.CompilationUnit declarationUnit=null;
        if (searchInEditor()) {
            Tree.CompilationUnit cu = 
            		editor.getParseController().getRootNode();
            if (cu.getUnit().equals(declaration.getUnit())) {
                declarationUnit = cu;
            }
        }
        if (declarationUnit==null) {
            for (final PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                if (pu.getUnit().equals(declaration.getUnit())) {
                    declarationUnit = pu.getCompilationUnit();
                    break;
                }
            }
        }
        declarationNode = getDeclararionNode(declarationUnit);
        if (declarationNode instanceof Tree.AttributeDeclaration &&
                ((Tree.AttributeDeclaration) declarationNode).getSpecifierOrInitializerExpression()==null ||
            declarationNode instanceof Tree.MethodDeclaration &&
                ((Tree.MethodDeclaration) declarationNode).getSpecifierExpression()==null) {
            return createFatalErrorStatus("Cannot inline forward declaration: " + 
                    declaration.getName());
        }
        if (declarationNode instanceof Tree.AttributeGetterDefinition) {
            Tree.AttributeGetterDefinition attributeGetterDefinition = 
                    (Tree.AttributeGetterDefinition) declarationNode;
            List<Tree.Statement> statements = 
                    attributeGetterDefinition.getBlock().getStatements();
            if (statements.size()!=1) {
                return createFatalErrorStatus("Getter body is not a single statement: " + 
                        declaration.getName());
            }
            if (!(statements.get(0) instanceof Tree.Return)) {
                return createFatalErrorStatus("Getter body is not a return statement: " + 
                        declaration.getName());
            }
        }
        if (declarationNode instanceof Tree.MethodDefinition) {
            Tree.MethodDefinition methodDefinition = 
                    (Tree.MethodDefinition) declarationNode;
            List<Tree.Statement> statements = 
                    methodDefinition.getBlock().getStatements();
            if (statements.size()!=1) {
                return createFatalErrorStatus("Function body is not a single statement: " + 
                        declaration.getName());
            }
            if (methodDefinition.getType() instanceof Tree.VoidModifier) {
                if (!(statements.get(0) instanceof Tree.ExpressionStatement)) {
                    return createFatalErrorStatus("Function body is not an expression: " + 
                            declaration.getName());
                }
            }
            else {
                if (!(statements.get(0) instanceof Tree.Return)) {
                    return createFatalErrorStatus("Function body is not a return statement: " + 
                            declaration.getName());
                }
            }
        }
        if (declarationNode instanceof Tree.AnyAttribute &&
                ((Tree.AnyAttribute) declarationNode).getDeclarationModel().isVariable()) {
            result.merge(createWarningStatus("Inlined value is variable"));
        }
        declarationNode.visit(new Visitor() {
            @Override
            public void visit(Tree.BaseMemberOrTypeExpression that) {
                super.visit(that);
                if (that.getDeclaration()==null) {
                    result.merge(createWarningStatus("Definition contains unresolved reference"));
                }
                else if (declaration.isShared() &&
                        !that.getDeclaration().isShared() &&
                        !that.getDeclaration().isParameter()) {
                    result.merge(createWarningStatus("Definition contains reference to unshared declaration: " +
                            that.getDeclaration().getName()));
                }
            }
        });
        return result;
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) 
    		throws CoreException, OperationCanceledException {
        
        Tree.Declaration declarationNode = null;
        Tree.CompilationUnit declarationUnit = null;
        Tree.Term term = null;
        List<CommonToken> declarationTokens = null;
        Tree.CompilationUnit editorRootNode = 
        		editor.getParseController().getRootNode();
        List<CommonToken> editorTokens = 
        		editor.getParseController().getTokens();
        if (declaration!=null) {
            if (searchInEditor()) {
                if (editorRootNode.getUnit()
                		.equals(declaration.getUnit())) {
                    declarationUnit = editorRootNode;
                    declarationTokens = editorTokens;
                }
            }
            if (declarationUnit==null) {
                for (PhasedUnit pu: getAllUnits()) {
                    if (pu.getUnit().equals(declaration.getUnit())) {
                        declarationUnit = pu.getCompilationUnit();
                        declarationTokens = pu.getTokens();
                        break;
                    }
                }
            }
            declarationNode = getDeclararionNode(declarationUnit);
            term = getInlinedTerm(declarationNode);
        }
        
        CompositeChange cc = new CompositeChange(getName());
        if (declarationNode!=null) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu) && 
                        affectsUnit(pu.getUnit())) {
                    TextFileChange tfc = newTextFileChange(pu);
                    Tree.CompilationUnit cu = pu.getCompilationUnit();
                    inlineInFile(tfc, cc, declarationNode, 
                            declarationUnit, term, declarationTokens, 
                            cu, pu.getTokens());
                }
            }
        }
        if (searchInEditor() && 
                affectsUnit(editorRootNode.getUnit())) {
            DocumentChange dc = newDocumentChange();
            inlineInFile(dc, cc, declarationNode, declarationUnit, 
                    term, declarationTokens,
                    editorRootNode, editorTokens);
        }
        return cc;
        
    }

    private Tree.Declaration getDeclararionNode(Tree.CompilationUnit declarationUnit) {
        FindDeclarationNodeVisitor fdv = 
        		new FindDeclarationNodeVisitor(declaration);
        declarationUnit.visit(fdv);
        return (Tree.Declaration) fdv.getDeclarationNode();
    }

    private boolean affectsUnit(Unit unit) {
        return delete && unit.equals(declaration.getUnit()) ||
                !justOne || unit.equals(node.getUnit());
    }
    
    private boolean addImports(final TextChange change, 
            final Tree.Declaration declarationNode, 
            final Tree.CompilationUnit cu) {
        
        final Package decPack = declarationNode.getUnit().getPackage();
        final Package filePack = cu.getUnit().getPackage();
        
        final class AddImportsVisitor extends Visitor {
            private final Set<Declaration> already;
            boolean importedFromDeclarationPackage;

            private AddImportsVisitor(Set<Declaration> already) {
                this.already = already;
            }

            @Override
            public void visit(Tree.BaseMemberOrTypeExpression that) {
                super.visit(that);
                if (that.getDeclaration()!=null) {
                    importDeclaration(already, that.getDeclaration(), cu);
                    Package refPack = that.getDeclaration().getUnit().getPackage();
                    importedFromDeclarationPackage = importedFromDeclarationPackage ||
                            //result!=0 &&
                            refPack.equals(decPack) && 
                            !decPack.equals(filePack); //unnecessary
                }
            }
        }

        final Set<Declaration> already = new HashSet<Declaration>();
        AddImportsVisitor aiv = new AddImportsVisitor(already);
        declarationNode.visit(aiv);
        applyImports(change, already, 
                declarationNode.getDeclarationModel(), cu, document);
        return aiv.importedFromDeclarationPackage;
    }

    private void inlineInFile(TextChange tfc, CompositeChange cc, 
            Tree.Declaration declarationNode, 
            Tree.CompilationUnit declarationUnit, Tree.Term term, 
            List<CommonToken> declarationTokens,
            Tree.CompilationUnit cu, List<CommonToken> tokens) {
        tfc.setEdit(new MultiTextEdit());
        inlineReferences(declarationNode, declarationUnit, 
        		term, declarationTokens, cu, tokens, tfc);
        boolean inlined = tfc.getEdit().hasChildren();
        deleteDeclaration(declarationNode, declarationUnit, 
        		cu, tokens, tfc);
        boolean importsAddedToDeclarationPackage = false;
        if (inlined) {
            importsAddedToDeclarationPackage = 
            		addImports(tfc, declarationNode, cu);
        }
        deleteImports(tfc, declarationNode, cu, tokens, 
        		importsAddedToDeclarationPackage);
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

    private void deleteImports(TextChange tfc, Tree.Declaration declarationNode, 
            Tree.CompilationUnit cu, List<CommonToken> tokens, 
            boolean importsAddedToDeclarationPackage) {
        Tree.ImportList il = cu.getImportList();
        if (il!=null) {
            for (Tree.Import i: il.getImports()) {
                List<Tree.ImportMemberOrType> list = 
                		i.getImportMemberOrTypeList()
                		        .getImportMemberOrTypes();
                for (Tree.ImportMemberOrType imt: list) {
                    Declaration d = imt.getDeclarationModel();
                    if (d!=null && d.equals(declarationNode.getDeclarationModel())) {
                        if (list.size()==1 && !importsAddedToDeclarationPackage) {
                            //delete the whole import statement
                            tfc.addEdit(new DeleteEdit(i.getStartIndex(), 
                                    i.getStopIndex()-i.getStartIndex()+1));
                        }
                        else {
                            //delete just the item in the import statement...
                            tfc.addEdit(new DeleteEdit(imt.getStartIndex(), 
                                    imt.getStopIndex()-imt.getStartIndex()+1));
                            //...along with a comma before or after
                            int ti = Nodes.getTokenIndexAtCharacter(tokens, 
                            		imt.getStartIndex());
                            CommonToken prev = tokens.get(ti-1);
                            if (prev.getChannel()==CommonToken.HIDDEN_CHANNEL) {
                                prev = tokens.get(ti-2);
                            }
                            CommonToken next = tokens.get(ti+1);
                            if (next.getChannel()==CommonToken.HIDDEN_CHANNEL) {
                                next = tokens.get(ti+2);
                            }
                            if (prev.getType()==CeylonLexer.COMMA) {
                                tfc.addEdit(new DeleteEdit(prev.getStartIndex(), 
                                        imt.getStartIndex()-prev.getStartIndex()));
                            }
                            else if (next.getType()==CeylonLexer.COMMA) {
                                tfc.addEdit(new DeleteEdit(imt.getStopIndex()+1, 
                                        next.getStopIndex()-imt.getStopIndex()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void deleteDeclaration(Tree.Declaration declarationNode, 
            Tree.CompilationUnit declarationUnit, Tree.CompilationUnit cu, 
            List<CommonToken> tokens, TextChange tfc) {
        if (delete && cu.getUnit().equals(declarationUnit.getUnit())) {
            CommonToken from = (CommonToken) declarationNode.getToken();
            Tree.AnnotationList anns = declarationNode.getAnnotationList();
            if (!anns.getAnnotations().isEmpty()) {
                from = (CommonToken) anns.getAnnotations().get(0).getToken();
            }
            int prevIndex = from.getTokenIndex()-1;
            if (prevIndex>=0) {
                CommonToken tok = tokens.get(prevIndex);
                if (tok.getChannel()==Token.HIDDEN_CHANNEL) {
                    from=tok;
                }
            }
            tfc.addEdit(new DeleteEdit(from.getStartIndex(), 
                    declarationNode.getStopIndex()-from.getStartIndex()+1));
        }
    }

    private static Tree.Term getInlinedTerm(Tree.Declaration declarationNode) {
        if (declarationNode!=null) {
            if (declarationNode instanceof Tree.AttributeDeclaration) {
                Tree.AttributeDeclaration att = 
                		(Tree.AttributeDeclaration) declarationNode;
                return att.getSpecifierOrInitializerExpression()
                		.getExpression().getTerm();
            }
            else if (declarationNode instanceof Tree.MethodDefinition) {
                Tree.MethodDefinition meth = 
                		(Tree.MethodDefinition) declarationNode;
                List<Tree.Statement> statements = meth.getBlock().getStatements();
                if (meth.getType() instanceof Tree.VoidModifier) {
                    //TODO: in the case of a void method, tolerate 
                    //      multiple statements , including control
                    //      structures, not just expression statements
                    if (statements.size()!=1 ||
                            !(statements.get(0) instanceof Tree.ExpressionStatement)) {
                        throw new RuntimeException("method body is not a single expression statement");
                    }
                    Tree.ExpressionStatement e = 
                    		(Tree.ExpressionStatement) statements.get(0);
                    return e.getExpression().getTerm();
                    
                }
                else {
                    if (statements.size()!=1 ||
                            !(statements.get(0) instanceof Tree.Return)) {
                        throw new RuntimeException("method body is not a single expression statement");
                    }
                    Tree.Return r = (Tree.Return) statements.get(0);
                    return r.getExpression().getTerm();
                }
            }
            else if (declarationNode instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration meth = (Tree.MethodDeclaration) declarationNode;
                return meth.getSpecifierExpression().getExpression().getTerm();
            }
            else if (declarationNode instanceof Tree.AttributeGetterDefinition) {
                Tree.AttributeGetterDefinition att = 
                		(Tree.AttributeGetterDefinition) declarationNode;
                List<Tree.Statement> statements = att.getBlock().getStatements();
                if (statements.size()!=1 ||
                        !(statements.get(0) instanceof Tree.Return)) {
                    throw new RuntimeException("getter body is not a single expression statement");
                }
                Tree.Return r = (Tree.Return) att.getBlock().getStatements().get(0);
                return r.getExpression().getTerm();
            }
            else {
                throw new RuntimeException("not a value or function");
            }
        }
        else {
            return null;
        }
    }

    private void inlineReferences(Tree.Declaration declarationNode,
            Tree.CompilationUnit declarationUnit, Tree.Term term, 
            List<CommonToken> declarationTokens, Tree.CompilationUnit pu, 
            List<CommonToken> tokens, TextChange tfc) {
        if (declarationNode instanceof Tree.AnyAttribute) {
            inlineAttributeReferences(pu, tokens, term, 
            		declarationTokens, tfc);
        }
        else if (declarationNode instanceof Tree.AnyMethod) {
            inlineFunctionReferences(pu, tokens, term,
            		(Tree.AnyMethod) declarationNode, 
            		declarationTokens, tfc);
        }
    }

    private void inlineFunctionReferences(final Tree.CompilationUnit pu, 
            final List<CommonToken> tokens, final Tree.Term term, 
            final Tree.AnyMethod declaration, 
            final List<CommonToken> declarationTokens, 
            final TextChange tfc) {
        new Visitor() {
            private boolean needsParens = false;
            @Override
            public void visit(final Tree.InvocationExpression that) {
                super.visit(that);
                if (that.getPrimary() instanceof Tree.MemberOrTypeExpression) {
                    Tree.MemberOrTypeExpression mte = 
                            (Tree.MemberOrTypeExpression) that.getPrimary();
                    inlineDefinition(tokens, declarationTokens, 
                    		term, tfc, that, mte, needsParens);
                }
            }
            @Override
            public void visit(final Tree.MemberOrTypeExpression that) {
            	 super.visit(that);
            	 Declaration d = that.getDeclaration();
            	 if (!that.getDirectlyInvoked() && inlineRef(that, d)) {
            		 StringBuilder text = new StringBuilder();
            		 Function dec = declaration.getDeclarationModel();
            		 if (dec.isDeclaredVoid()) {
            			 text.append("void ");
            		 }
            		 for (Tree.ParameterList pl: declaration.getParameterLists()) {
            			 text.append(Nodes.toString(pl, declarationTokens));
            		 }
            		 text.append(" => ");
            		 text.append(Nodes.toString(term, declarationTokens));
            		 tfc.addEdit(new ReplaceEdit(that.getStartIndex(), 
            				 that.getStopIndex()-that.getStartIndex()+1, 
            				 text.toString()));
            	 }
            }
            @Override
            public void visit(Tree.OperatorExpression that) {
                boolean onp = needsParens;
                needsParens=true;
                super.visit(that);
                needsParens = onp;
            }
            @Override
            public void visit(Tree.StatementOrArgument that) {
                boolean onp = needsParens;
                needsParens = false;
                super.visit(that);
                needsParens = onp;
            }
            @Override
            public void visit(Tree.Expression that) {
                boolean onp = needsParens;
                needsParens = false;
                super.visit(that);
                needsParens = onp;
            }
        }.visit(pu);
    }
    
    private void inlineAttributeReferences(final Tree.CompilationUnit pu, 
            final List<CommonToken> tokens, final Term term, 
            final List<CommonToken> declarationTokens, final TextChange tfc) {
        new Visitor() {
            private boolean needsParens = false;
            @Override
            public void visit(Tree.Variable that) {
                if (that.getType() instanceof Tree.SyntheticVariable) {
                    TypedDeclaration od = 
                            that.getDeclarationModel().getOriginalDeclaration();
                    if (od!=null && od.equals(declaration) && delete) {
                        Integer startIndex = 
                        		that.getSpecifierExpression().getStartIndex();
						tfc.addEdit(new InsertEdit(startIndex, 
                                that.getIdentifier().getText()+" = "));
                    }
                }
                super.visit(that);
            }
            @Override
            public void visit(Tree.MemberOrTypeExpression that) {
                super.visit(that);
                inlineDefinition(tokens, declarationTokens, term, 
                        tfc, null, that, needsParens);
            }
            @Override
            public void visit(Tree.OperatorExpression that) {
                boolean onp = needsParens;
                needsParens=true;
                super.visit(that);
                needsParens = onp;
            }
            @Override
            public void visit(Tree.QualifiedMemberOrTypeExpression that) {
                boolean onp = needsParens;
                needsParens=true;
                super.visit(that);
                needsParens = onp;
            }
            @Override
            public void visit(Tree.StatementOrArgument that) {
                boolean onp = needsParens;
                needsParens = false;
                super.visit(that);
                needsParens = onp;
            }
            @Override
            public void visit(Tree.Expression that) {
                boolean onp = needsParens;
                needsParens = false;
                super.visit(that);
                needsParens = onp;
            }
        }.visit(pu);
    }

    private void inlineDefinitionReference(List<CommonToken> tokens,
            List<CommonToken> declarationTokens, 
            Tree.MemberOrTypeExpression re, Tree.InvocationExpression ie, 
            StringBuilder result, Tree.StaticMemberOrTypeExpression it) {
        Declaration dec = it.getDeclaration();
        if (dec.isParameter() && ie!=null && 
                it instanceof Tree.BaseMemberOrTypeExpression) {
            Parameter param = 
                    ((FunctionOrValue) dec).getInitializerParameter();
            if (param.getDeclaration().equals(declaration)) {
                boolean sequenced = param.isSequenced();
                if (ie.getPositionalArgumentList()!=null) {
                    interpolatePositionalArguments(result, 
                    		ie, it, sequenced, tokens);
                }
                if (ie.getNamedArgumentList()!=null) {
                    interpolateNamedArguments(result, 
                    		ie, it, sequenced, tokens);
                }
                return; //NOTE: early exit!
            }
        }

        String expressionText = Nodes.toString(it, declarationTokens);
        if (re instanceof Tree.QualifiedMemberOrTypeExpression) {
            Tree.QualifiedMemberOrTypeExpression qmtre = 
                    (Tree.QualifiedMemberOrTypeExpression) re;
            String prim = Nodes.toString(qmtre.getPrimary(), tokens);
            if (it instanceof Tree.QualifiedMemberOrTypeExpression) {
                //TODO: handle more depth, for example, foo.bar.baz
                Tree.QualifiedMemberOrTypeExpression qmte = 
                        (Tree.QualifiedMemberOrTypeExpression) it;
                Tree.Primary p = qmte.getPrimary();
                if (p instanceof Tree.This) {
                    result.append(prim)
                    .append(qmte.getMemberOperator().getText())
                    .append(qmte.getIdentifier().getText());
                }
                else {
                    String primaryText = Nodes.toString(p, declarationTokens);
                    if (p instanceof Tree.MemberOrTypeExpression) {
                        if (((Tree.MemberOrTypeExpression) p).getDeclaration()
                                .isClassOrInterfaceMember()) {
                            result.append(prim)
                            .append(".")
                            .append(primaryText);
                        }
                    }
                    else {
                        result.append(primaryText);
                    }
                }
            }
            else {
                if (it.getDeclaration().isClassOrInterfaceMember()) {
                    result.append(prim)
                    .append(".")
                    .append(expressionText);
                }
                else {
                    result.append(expressionText);
                }
            }
        }
        else {
            result.append(expressionText);
        }

    }
    
    private void inlineDefinition(final List<CommonToken> tokens,
            final List<CommonToken> declarationTokens,
            final Tree.Term term, final TextChange tfc,
            final Tree.InvocationExpression that,
            final Tree.MemberOrTypeExpression mte, 
            final boolean needsParens) {
        Declaration d = mte.getDeclaration();
        if (inlineRef(mte, d)) {
            //TODO: breaks for invocations like f(f(x, y),z)
            final StringBuilder result = new StringBuilder();
            class InterpolationVisitor extends Visitor {
                int start = 0;
                final String template = Nodes.toString(term, declarationTokens);
                final int templateStart = term.getStartIndex();
                void text(Node it) {
                    result.append(template.substring(start,
                    		it.getStartIndex()-templateStart));
                    start = it.getStopIndex()-templateStart+1;
                }
                @Override
                public void visit(Tree.BaseMemberExpression it) {
                    super.visit(it);
                    text(it);
                    inlineDefinitionReference(tokens, declarationTokens, 
                            mte, that, result, it);
                }
                @Override
                public void visit(Tree.QualifiedMemberExpression it) {
                    super.visit(it);
                    text(it);
                    inlineDefinitionReference(tokens, declarationTokens, 
                            mte, that, result, it);

                }
                void finish() {
                    result.append(template.substring(start, template.length()));
                }
            }
            InterpolationVisitor iv = new InterpolationVisitor();
            iv.visit(term);
            iv.finish();
            if (needsParens && 
                    (term instanceof Tree.OperatorExpression ||
                    term instanceof Tree.IfExpression ||
                    term instanceof Tree.SwitchExpression ||
                    term instanceof Tree.ObjectExpression ||
                    term instanceof Tree.LetExpression ||
                    term instanceof Tree.FunctionArgument)) {
                result.insert(0,'(').append(')');
            }
            Node node = that==null ? mte : that;
            tfc.addEdit(new ReplaceEdit(node.getStartIndex(), 
                    node.getStopIndex()-node.getStartIndex()+1, 
                    result.toString()));
        }
    }
    
    private boolean inlineRef(Tree.MemberOrTypeExpression that, Declaration d) {
        return (!justOne || that.getUnit().equals(node.getUnit()) && 
                    that.getStartIndex()!=null &&
                    that.getStartIndex().equals(node.getStartIndex())) &&
                d!=null && d.equals(declaration);
    }
    
    private static void interpolatePositionalArguments(StringBuilder result, 
            Tree.InvocationExpression that, Tree.StaticMemberOrTypeExpression it,
            boolean sequenced, List<CommonToken> tokens) {
        boolean first = true;
        boolean found = false;
        if (sequenced) {
            result.append("{");
        }
        for (Tree.PositionalArgument arg: that.getPositionalArgumentList()
                .getPositionalArguments()) {
            if (it.getDeclaration().equals(arg.getParameter().getModel())) {
                if (arg.getParameter().isSequenced() &&
                        arg instanceof Tree.ListedArgument) {
                    if (first) result.append(" ");
                    if (!first) result.append(", ");
                    first = false;
                }
                result.append(Nodes.toString(arg, tokens));
                found = true;
            }
        }
        if (sequenced) {
            if (!first) result.append(" ");
            result.append("}");
        }
        if (!found) {} //TODO: use default value!
    }

    private static void interpolateNamedArguments(StringBuilder result,
            Tree.InvocationExpression that, Tree.StaticMemberOrTypeExpression it,
            boolean sequenced, List<CommonToken> tokens) {
        boolean found = false;
        for (Tree.NamedArgument arg: 
        	    that.getNamedArgumentList().getNamedArguments()) {
            if (it.getDeclaration().equals(arg.getParameter().getModel())) {
                Tree.SpecifiedArgument sa = (Tree.SpecifiedArgument) arg;
				Tree.Term argTerm = sa.getSpecifierExpression()
                                .getExpression().getTerm();
                result//.append(template.substring(start,it.getStartIndex()-templateStart))
                    .append(Nodes.toString(argTerm, tokens) );
                //start = it.getStopIndex()-templateStart+1;
                found=true;
            }
        }
        Tree.SequencedArgument seqArg = 
        		that.getNamedArgumentList().getSequencedArgument();
        if (seqArg!=null && 
        		it.getDeclaration().equals(seqArg.getParameter())) {
            result//.append(template.substring(start,it.getStartIndex()-templateStart))
                .append("{");
            //start = it.getStopIndex()-templateStart+1;;
            boolean first=true;
            for (Tree.PositionalArgument pa: 
            	    seqArg.getPositionalArguments()) {
                if (first) result.append(" ");
                if (!first) result.append(", ");
                first=false;
                result.append(Nodes.toString(pa, tokens));
            }
            if (!first) result.append(" ");
            result.append("}");
            found=true;
        }
        if (!found) {
            if (sequenced) {
                result.append("{}");
            }
            else {} //TODO: use default value!
        }
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
    public void setDelete() {
        this.delete = !delete;
    }
    
    public void setJustOne() {
        this.justOne = !justOne;
    }
}
