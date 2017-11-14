/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedDeclaration;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createFatalErrorStatus;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.util.FindReferencesVisitor;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;
import org.eclipse.ceylon.ide.common.model.CeylonUnit;
import org.eclipse.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import org.eclipse.ceylon.ide.common.util.FindDeclarationNodeVisitor;
import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.ClassOrInterface;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.Generic;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.Setter;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeAlias;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypeParameter;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

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
                getIdentifyingNode(node) 
                    instanceof Tree.Identifier;
    }

    @Override
    public boolean getEnabled() {
        if (declaration!=null &&
                project != null &&
                inSameProject(declaration)) {
            if (declaration instanceof FunctionOrValue) {
                FunctionOrValue fov = 
                        (FunctionOrValue) declaration;
                return  !fov.isParameter() &&
                        !(fov instanceof Setter) &&
                        !fov.isDefault() &&
                        !fov.isFormal() &&
                        !fov.isNative() &&
                        (fov.getTypeDeclaration()!=null) &&
                        (!fov.getTypeDeclaration().isAnonymous()) &&
                        (fov.isToplevel() || !fov.isShared() ||
                                (!fov.isFormal() && 
                                 !fov.isDefault() && 
                                 !fov.isActual())) &&
                        (!fov.getUnit().equals(rootNode.getUnit()) || 
                                !(getDeclararionNode(rootNode) 
                                        instanceof Tree.Variable)); //not a Destructure
                //TODO: && !declaration is a control structure variable 
                //TODO: && !declaration is a value with lazy init
            }
            else if (declaration instanceof TypeAlias) {
                return true;
            }
            else if (declaration instanceof ClassOrInterface) {
                return ((ClassOrInterface) declaration).isAlias();
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    public int getCount() {
        return declaration==null ? 0 : 
            countDeclarationOccurrences();
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

    public RefactoringStatus checkInitialConditions
            (IProgressMonitor pm)
                    throws CoreException, 
                           OperationCanceledException {
        final RefactoringStatus result = 
                new RefactoringStatus();
        Tree.CompilationUnit declarationUnit = null;
        Unit unit = declaration.getUnit();
        if (unit instanceof CeylonUnit) {
            CeylonUnit cu = (CeylonUnit) unit;
            declarationUnit = 
                    cu.getPhasedUnit()
                    .getCompilationUnit();
        }
        Tree.Declaration declarationNode = 
                getDeclararionNode(declarationUnit);
        if (declarationNode 
                    instanceof Tree.AttributeDeclaration &&
                ((Tree.AttributeDeclaration) declarationNode)
                    .getSpecifierOrInitializerExpression()==null ||
            declarationNode 
                    instanceof Tree.MethodDeclaration &&
                ((Tree.MethodDeclaration) declarationNode)
                    .getSpecifierExpression()==null) {
            return createFatalErrorStatus(
                    "Cannot inline forward declaration: " + 
                            declaration.getName());
        }
        if (declarationNode 
                    instanceof Tree.AttributeGetterDefinition) {
            Tree.AttributeGetterDefinition getterDefinition = 
                    (Tree.AttributeGetterDefinition) 
                        declarationNode;
            List<Tree.Statement> statements = 
                    getterDefinition.getBlock()
                        .getStatements();
            if (statements.size()!=1) {
                return createFatalErrorStatus(
                        "Getter body is not a single statement: " + 
                                declaration.getName());
            }
            if (!(statements.get(0) instanceof Tree.Return)) {
                return createFatalErrorStatus(
                        "Getter body is not a return statement: " + 
                                declaration.getName());
            }
        }
        if (declarationNode instanceof Tree.MethodDefinition) {
            Tree.MethodDefinition methodDefinition = 
                    (Tree.MethodDefinition) 
                        declarationNode;
            List<Tree.Statement> statements = 
                    methodDefinition.getBlock()
                        .getStatements();
            if (statements.size()!=1) {
                return createFatalErrorStatus(
                        "Function body is not a single statement: " + 
                                declaration.getName());
            }
            Tree.Statement statement = statements.get(0);
            if (methodDefinition.getType() 
                    instanceof Tree.VoidModifier) {
                if (!(statement 
                        instanceof Tree.ExpressionStatement)) {
                    return createFatalErrorStatus(
                            "Function body is not an expression: " + 
                                    declaration.getName());
                }
            }
            else {
                if (!(statement instanceof Tree.Return)) {
                    return createFatalErrorStatus(
                            "Function body is not a return statement: " + 
                                    declaration.getName());
                }
            }
        }
        if (declarationNode instanceof Tree.AnyAttribute) {
            Tree.AnyAttribute attribute = 
                    (Tree.AnyAttribute) 
                        declarationNode;
            if (attribute.getDeclarationModel().isVariable()) {
                result.merge(createWarningStatus(
                        "Inlined value is variable"));
            }
        }
        declarationNode.visit(new Visitor() {
            @Override
            public void visit(Tree.BaseMemberOrTypeExpression that) {
                super.visit(that);
                Declaration dec = that.getDeclaration();
                if (dec==null) {
                    result.merge(createWarningStatus(
                            "Definition contains unresolved reference"));
                }
                else if (declaration.isShared() &&
                        !dec.isShared() &&
                        !dec.isParameter()) {
                    result.merge(createWarningStatus(
                            "Definition contains reference to unshared declaration: " +
                                    dec.getName()));
                }
            }
        });
        return result;
    }

    public RefactoringStatus checkFinalConditions
            (IProgressMonitor pm)
                    throws CoreException, 
                           OperationCanceledException {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) 
    		throws CoreException, 
    		       OperationCanceledException {
        
        Tree.Declaration declarationNode = null;
        Tree.CompilationUnit declarationUnit = null;
        Node term = null;
        List<CommonToken> declarationTokens = null;
        Tree.CompilationUnit editorRootNode = 
        		editor.getParseController()
        		    .getLastCompilationUnit();
        List<CommonToken> editorTokens = 
        		editor.getParseController()
        		    .getTokens();
        if (declaration!=null) {
            Unit unit = declaration.getUnit();
            if (searchInEditor()) {
                if (editorRootNode.getUnit()
                		.equals(unit)) {
                    declarationUnit = editorRootNode;
                    declarationTokens = editorTokens;
                }
            }
            if (declarationUnit==null) {
                for (PhasedUnit pu: getAllUnits()) {
                    if (pu.getUnit().equals(unit)) {
                        declarationUnit = 
                                pu.getCompilationUnit();
                        declarationTokens = pu.getTokens();
                        break;
                    }
                }
            }
            declarationNode = 
                    getDeclararionNode(declarationUnit);
            term = getInlinedTerm(declarationNode);
        }
        
        CompositeChange cc = new CompositeChange(getName());
        if (declarationNode!=null) {
            for (PhasedUnit pu: getAllUnits()) {
                if (searchInFile(pu) && 
                        affectsUnit(pu.getUnit())) {
                    ProjectPhasedUnit ppu = 
                            (ProjectPhasedUnit<IProject,IResource,IFolder,IFile>) pu;
                    TextFileChange tfc = 
                            newTextFileChange(ppu);
                    Tree.CompilationUnit cu = 
                            pu.getCompilationUnit();
                    inlineInFile(tfc, cc, 
                            declarationNode, 
                            declarationUnit, term, 
                            declarationTokens, cu, 
                            pu.getTokens());
                }
            }
        }
        if (searchInEditor() && 
                affectsUnit(editorRootNode.getUnit())) {
            DocumentChange dc = newDocumentChange();
            inlineInFile(dc, cc, 
                    declarationNode, 
                    declarationUnit, term, 
                    declarationTokens,
                    editorRootNode, 
                    editorTokens);
        }
        return cc;
        
    }

    private Tree.Declaration getDeclararionNode(
            Tree.CompilationUnit declarationUnit) {
        FindDeclarationNodeVisitor fdv = 
        		new FindDeclarationNodeVisitor(declaration);
        declarationUnit.visit(fdv);
        return (Tree.Declaration) fdv.getDeclarationNode();
    }

    private boolean affectsUnit(Unit unit) {
        return delete && unit.equals(declaration.getUnit()) 
                || !justOne 
                || unit.equals(node.getUnit());
    }
    
    private boolean addImports(
            final TextChange change, 
            final Tree.Declaration declarationNode, 
            final Tree.CompilationUnit cu) {
        
        final Package decPack = 
                declarationNode.getUnit().getPackage();
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
                Declaration dec = that.getDeclaration();
                if (dec!=null) {
                    importProposals().importDeclaration(already, dec, cu);
                    Package refPack = 
                            dec.getUnit().getPackage();
                    importedFromDeclarationPackage = 
                            importedFromDeclarationPackage ||
                                refPack.equals(decPack) && 
                                !decPack.equals(filePack); //unnecessary
                }
            }
        }

        final Set<Declaration> already = 
                new HashSet<Declaration>();
        AddImportsVisitor aiv = 
                new AddImportsVisitor(already);
        declarationNode.visit(aiv);
        Declaration dnd = 
                declarationNode.getDeclarationModel();
        importProposals().applyImports(change, already, cu, document, dnd);
        return aiv.importedFromDeclarationPackage;
    }

    private void inlineInFile(TextChange tfc, 
            CompositeChange cc, 
            Tree.Declaration declarationNode, 
            Tree.CompilationUnit declarationUnit, 
            Node term, 
            List<CommonToken> declarationTokens,
            Tree.CompilationUnit cu, 
            List<CommonToken> tokens) {
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

    private void deleteImports(TextChange tfc, 
            Tree.Declaration declarationNode, 
            Tree.CompilationUnit cu, 
            List<CommonToken> tokens, 
            boolean importsAddedToDeclarationPackage) {
        Tree.ImportList il = cu.getImportList();
        if (il!=null) {
            for (Tree.Import i: il.getImports()) {
                List<Tree.ImportMemberOrType> list = 
                		i.getImportMemberOrTypeList()
                		        .getImportMemberOrTypes();
                for (Tree.ImportMemberOrType imt: list) {
                    Declaration d = imt.getDeclarationModel();
                    Declaration dnd = 
                            declarationNode.getDeclarationModel();
                    if (d!=null && d.equals(dnd)) {
                        if (list.size()==1 && 
                                !importsAddedToDeclarationPackage) {
                            //delete the whole import statement
                            tfc.addEdit(new DeleteEdit(
                                    i.getStartIndex(), 
                                    i.getDistance()));
                        }
                        else {
                            //delete just the item in the import statement...
                            tfc.addEdit(new DeleteEdit(
                                    imt.getStartIndex(), 
                                    imt.getDistance()));
                            //...along with a comma before or after
                            int ti = 
                                    Nodes.getTokenIndexAtCharacter(
                                        tokens, 
                                		imt.getStartIndex());
                            CommonToken prev = 
                                    tokens.get(ti-1);
                            if (prev.getChannel()==CommonToken.HIDDEN_CHANNEL) {
                                prev = tokens.get(ti-2);
                            }
                            CommonToken next = 
                                    tokens.get(ti+1);
                            if (next.getChannel()==CommonToken.HIDDEN_CHANNEL) {
                                next = tokens.get(ti+2);
                            }
                            if (prev.getType()==CeylonLexer.COMMA) {
                                tfc.addEdit(new DeleteEdit(
                                        prev.getStartIndex(), 
                                        imt.getStartIndex()
                                            - prev.getStartIndex()));
                            }
                            else if (next.getType()==CeylonLexer.COMMA) {
                                tfc.addEdit(new DeleteEdit(
                                        imt.getEndIndex(), 
                                        next.getStopIndex()
                                            - imt.getEndIndex()
                                            + 1));
                            }
                        }
                    }
                }
            }
        }
    }

    private void deleteDeclaration(
            Tree.Declaration declarationNode, 
            Tree.CompilationUnit declarationUnit, 
            Tree.CompilationUnit cu, 
            List<CommonToken> tokens, 
            TextChange tfc) {
        if (delete) {
            Unit unit = declarationUnit.getUnit();
            if (cu.getUnit().equals(unit)) {
                CommonToken from = 
                        (CommonToken) 
                        declarationNode.getToken();
                Tree.AnnotationList anns = 
                        declarationNode.getAnnotationList();
                if (!anns.getAnnotations().isEmpty()) {
                    from = (CommonToken) 
                            anns.getAnnotations()
                            .get(0)
                            .getToken();
                }
                int prevIndex = from.getTokenIndex()-1;
                if (prevIndex>=0) {
                    CommonToken tok = tokens.get(prevIndex);
                    if (tok.getChannel()==Token.HIDDEN_CHANNEL) {
                        from=tok;
                    }
                }
                tfc.addEdit(new DeleteEdit(
                        from.getStartIndex(), 
                        declarationNode.getEndIndex()
                            - from.getStartIndex()));
            }
        }
    }

    private static Node getInlinedTerm(
            Tree.Declaration declarationNode) {
        if (declarationNode!=null) {
            if (declarationNode 
                    instanceof Tree.AttributeDeclaration) {
                Tree.AttributeDeclaration att = 
                		(Tree.AttributeDeclaration) 
                		    declarationNode;
                return att.getSpecifierOrInitializerExpression()
                		.getExpression()
                		.getTerm();
            }
            else if (declarationNode 
                    instanceof Tree.MethodDefinition) {
                Tree.MethodDefinition meth = 
                		(Tree.MethodDefinition) 
                		    declarationNode;
                List<Tree.Statement> statements = 
                        meth.getBlock()
                            .getStatements();
                if (meth.getType() 
                        instanceof Tree.VoidModifier) {
                    //TODO: in the case of a void method, tolerate 
                    //      multiple statements , including control
                    //      structures, not just expression statements
                    if (!isSingleExpression(statements)) {
                        throw new RuntimeException(
                                "method body is not a single expression statement");
                    }
                    Tree.ExpressionStatement e = 
                    		(Tree.ExpressionStatement) 
                    		    statements.get(0);
                    return e.getExpression().getTerm();
                    
                }
                else {
                    if (!isSingleReturn(statements)) {
                        throw new RuntimeException(
                                "method body is not a single expression statement");
                    }
                    Tree.Return ret = 
                            (Tree.Return) 
                                statements.get(0);
                    return ret.getExpression().getTerm();
                }
            }
            else if (declarationNode 
                    instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration meth = 
                        (Tree.MethodDeclaration) 
                            declarationNode;
                return meth.getSpecifierExpression()
                        .getExpression().getTerm();
            }
            else if (declarationNode 
                    instanceof Tree.AttributeGetterDefinition) {
                Tree.AttributeGetterDefinition att = 
                		(Tree.AttributeGetterDefinition) 
                		    declarationNode;
                List<Tree.Statement> statements = 
                        att.getBlock()
                            .getStatements();
                if (!isSingleReturn(statements)) {
                    throw new RuntimeException(
                            "getter body is not a single expression statement");
                }
                Tree.Return r = 
                        (Tree.Return) 
                            att.getBlock()
                                .getStatements()
                                .get(0);
                return r.getExpression().getTerm();
            }
            else if (declarationNode 
                        instanceof Tree.ClassDeclaration) {
                Tree.ClassDeclaration alias = 
                        (Tree.ClassDeclaration) 
                            declarationNode;
                return alias.getClassSpecifier();
            }
            else if (declarationNode 
                        instanceof Tree.InterfaceDeclaration) {
                Tree.InterfaceDeclaration alias = 
                        (Tree.InterfaceDeclaration) 
                            declarationNode;
                return alias.getTypeSpecifier();
            }
            else if (declarationNode 
                        instanceof Tree.TypeAliasDeclaration) {
                Tree.TypeAliasDeclaration alias = 
                        (Tree.TypeAliasDeclaration) 
                            declarationNode;
                return alias.getTypeSpecifier();
            }
            else {
                throw new RuntimeException(
                        "not a value, function, or type alias");
            }
        }
        else {
            return null;
        }
    }

    public static boolean isSingleExpression(
            List<Tree.Statement> statements) {
        return statements.size()==1 &&
                statements.get(0) 
                        instanceof Tree.ExpressionStatement;
    }

    public static boolean isSingleReturn(
            List<Tree.Statement> statements) {
        return statements.size()==1 &&
                statements.get(0) 
                        instanceof Tree.Return;
    }

    private void inlineReferences(
            Tree.Declaration declarationNode,
            Tree.CompilationUnit declarationUnit, 
            Node definition, 
            List<CommonToken> declarationTokens, 
            Tree.CompilationUnit pu, 
            List<CommonToken> tokens, 
            TextChange tfc) {
        if (declarationNode 
                    instanceof Tree.AnyAttribute) {
//            Tree.AnyAttribute value = 
//                    (Tree.AnyAttribute) declarationNode;
            Tree.Term expression = (Tree.Term) definition;
            inlineAttributeReferences(pu, tokens, expression, 
            		declarationTokens, tfc);
        }
        else if (declarationNode 
                    instanceof Tree.AnyMethod) {
            Tree.Term expression = (Tree.Term) definition;
            Tree.AnyMethod method = 
                    (Tree.AnyMethod) 
                        declarationNode;
            inlineFunctionReferences(pu, tokens, expression,
            		method, declarationTokens, tfc);
        }
        else if (declarationNode 
                    instanceof Tree.ClassDeclaration) {
            Tree.ClassSpecifier spec = 
                    (Tree.ClassSpecifier) 
                        definition;
            Tree.ClassDeclaration classAlias = 
                    (Tree.ClassDeclaration) 
                        declarationNode;
            inlineClassAliasReferences(pu, tokens, 
                    spec.getInvocationExpression(), 
                    spec.getType(),
                    classAlias, 
                    declarationTokens, tfc);
        }
        else if (declarationNode 
                    instanceof Tree.TypeAliasDeclaration ||
                declarationNode 
                    instanceof Tree.InterfaceDeclaration) {
            Tree.TypeSpecifier spec = 
                    (Tree.TypeSpecifier) 
                        definition;
            inlineTypeAliasReferences(pu, tokens, 
                    spec.getType(), 
                    declarationTokens, tfc);
        }
    }

    private void inlineFunctionReferences(
            final Tree.CompilationUnit pu, 
            final List<CommonToken> tokens, 
            final Tree.Term term, 
            final Tree.AnyMethod decNode, 
            final List<CommonToken> declarationTokens, 
            final TextChange tfc) {
        new Visitor() {
            private boolean needsParens = false;
            @Override
            public void visit(
                    final Tree.InvocationExpression that) {
                super.visit(that);
                Tree.Primary primary = that.getPrimary();
                if (primary instanceof Tree.MemberOrTypeExpression) {
                    Tree.MemberOrTypeExpression mte = 
                            (Tree.MemberOrTypeExpression) 
                                primary;
                    inlineDefinition(tokens, declarationTokens, 
                    		term, tfc, that, mte, needsParens);
                }
            }
            @Override
            public void visit(
                    final Tree.MemberOrTypeExpression that) {
            	 super.visit(that);
            	 Declaration dec = that.getDeclaration();
            	 if (!that.getDirectlyInvoked() && 
            	         inlineRef(that, dec)) {
            		 StringBuilder text = new StringBuilder();
            		 Function function = 
            		         decNode.getDeclarationModel();
            		 if (function.isDeclaredVoid()) {
            			 text.append("void ");
            		 }
            		 for (Tree.ParameterList pl: 
            		         decNode.getParameterLists()) {
            			 text.append(Nodes.text(pl, 
            			         declarationTokens));
            		 }
            		 text.append(" => ");
            		 text.append(Nodes.text(term, 
            		         declarationTokens));
            		 tfc.addEdit(new ReplaceEdit(
            		         that.getStartIndex(), 
            				 that.getDistance(), 
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
    
    private void inlineTypeAliasReferences(
            final Tree.CompilationUnit pu, 
            final List<CommonToken> tokens, 
            final Tree.Type term, 
            final List<CommonToken> declarationTokens, 
            final TextChange tfc) {
        new Visitor() {
            @Override
            public void visit(Tree.SimpleType that) {
                super.visit(that);
                inlineDefinition(tokens, declarationTokens, 
                        term, tfc, null, that, false);
            }
        }.visit(pu);
    }

    private void inlineClassAliasReferences(
            final Tree.CompilationUnit pu, 
            final List<CommonToken> tokens, 
            final Tree.InvocationExpression term,
            final Tree.Type type,
            final Tree.ClassDeclaration decNode, 
            final List<CommonToken> declarationTokens, 
            final TextChange tfc) {
        new Visitor() {
            @Override
            public void visit(Tree.SimpleType that) {
                super.visit(that);
                inlineDefinition(tokens, declarationTokens, 
                        type, tfc, null, that, false);
            }
            private boolean needsParens = false;
            @Override
            public void visit(
                    final Tree.InvocationExpression that) {
                super.visit(that);
                Tree.Primary primary = that.getPrimary();
                if (primary instanceof Tree.MemberOrTypeExpression) {
                    Tree.MemberOrTypeExpression mte = 
                            (Tree.MemberOrTypeExpression) 
                                primary;
                    inlineDefinition(tokens, declarationTokens, 
                            term, tfc, that, mte, needsParens);
                }
            }
            @Override
            public void visit(
                    final Tree.MemberOrTypeExpression that) {
                 super.visit(that);
                 Declaration d = that.getDeclaration();
                 if (!that.getDirectlyInvoked() && 
                         inlineRef(that, d)) {
                     StringBuilder text = 
                             new StringBuilder();
                     Class dec = 
                             decNode.getDeclarationModel();
                     if (dec.isDeclaredVoid()) {
                         text.append("void ");
                     }
                     Tree.ParameterList pl = 
                             decNode.getParameterList();
                     text.append(Nodes.text(pl, 
                             declarationTokens));
                     text.append(" => ");
                     text.append(Nodes.text(term, 
                             declarationTokens));
                     tfc.addEdit(new ReplaceEdit(
                             that.getStartIndex(), 
                             that.getDistance(), 
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
    
    private void inlineAttributeReferences(
            final Tree.CompilationUnit pu, 
            final List<CommonToken> tokens, 
            final Tree.Term term, 
            final List<CommonToken> declarationTokens, 
            final TextChange tfc) {
        new Visitor() {
            private boolean needsParens = false;
            @Override
            public void visit(Tree.Variable that) {
                if (that.getType() 
                        instanceof Tree.SyntheticVariable) {
                    TypedDeclaration od = 
                            that.getDeclarationModel()
                                .getOriginalDeclaration();
                    if (od!=null && 
                            od.equals(declaration) && delete) {
                        Integer startIndex = 
                        		that.getSpecifierExpression()
                        		    .getStartIndex();
						String text = 
						        that.getIdentifier().getText()
						            + " = ";
                        tfc.addEdit(new InsertEdit(startIndex, text));
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

    private void inlineAliasDefinitionReference(
            List<CommonToken> tokens,
            List<CommonToken> declarationTokens,
            Node reference, 
            StringBuilder result, 
            Tree.Type it) {
        Type t = it.getTypeModel();
        TypeDeclaration td = t.getDeclaration();
        if (td instanceof TypeParameter) {
            Generic ta = (Generic) declaration;
            int index = ta.getTypeParameters().indexOf(td);
            if (index>=0) {
                if (reference 
                            instanceof Tree.SimpleType) {
                    Tree.SimpleType st = 
                            (Tree.SimpleType) 
                                reference;
                    Tree.TypeArgumentList tal = 
                            st.getTypeArgumentList();
                    List<Tree.Type> types = tal.getTypes();
                    if (types.size()>index) {
                        Tree.Type type = types.get(index);
                        result.append(
                                Nodes.text(type, 
                                        tokens));
                        return;
                    }
                }
                else if (reference 
                            instanceof Tree.MemberOrTypeExpression) {
                    Tree.StaticMemberOrTypeExpression st = 
                            (Tree.StaticMemberOrTypeExpression) 
                                reference;
                    Tree.TypeArguments tas = 
                            st.getTypeArguments();
                    if (tas instanceof Tree.TypeArgumentList) {
                        Tree.TypeArgumentList tal = 
                                (Tree.TypeArgumentList) tas;
                        List<Tree.Type> types = tal.getTypes();
                        if (types.size()>index) {
                            Tree.Type type = types.get(index);
                            if (type!=null) {
                                result.append(
                                        Nodes.text(type, 
                                                tokens));
                            }
                            return;
                        }
                    }
                    else {
                        List<Type> types = tas.getTypeModels();
                        if (types.size()>index) {
                            Type type = types.get(index);
                            if (type!=null) {
                                result.append(
                                        type.asSourceCodeString(
                                                it.getUnit()));
                            }
                            return;
                        }
                    }
                }
            }
        }
        result.append(Nodes.text(it, declarationTokens));
    }
    
    private void inlineDefinitionReference(
            List<CommonToken> tokens,
            List<CommonToken> declarationTokens, 
            Node reference, 
            Tree.InvocationExpression ie, 
            StringBuilder result, 
            Tree.StaticMemberOrTypeExpression it) {
        Declaration dec = it.getDeclaration();
        if (dec.isParameter() && ie!=null && 
                it instanceof Tree.BaseMemberOrTypeExpression) {
            FunctionOrValue fov = (FunctionOrValue) dec;
            Parameter param = fov.getInitializerParameter();
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

        String expressionText = 
                Nodes.text(it, declarationTokens);
        if (reference 
                instanceof Tree.QualifiedMemberOrTypeExpression) {
            Tree.QualifiedMemberOrTypeExpression qmtre = 
                    (Tree.QualifiedMemberOrTypeExpression) 
                        reference;
            String prim = 
                    Nodes.text(qmtre.getPrimary(), 
                            tokens);
            if (it instanceof Tree.QualifiedMemberOrTypeExpression) {
                //TODO: handle more depth, for example, foo.bar.baz
                Tree.QualifiedMemberOrTypeExpression qmte = 
                        (Tree.QualifiedMemberOrTypeExpression) it;
                Tree.Primary p = qmte.getPrimary();
                if (p instanceof Tree.This) {
                    String op = 
                            qmte.getMemberOperator()
                                .getText();
                    String id = 
                            qmte.getIdentifier()
                                .getText();
                    result.append(prim).append(op).append(id);
                }
                else {
                    String primaryText = 
                            Nodes.text(p, 
                                    declarationTokens);
                    if (p instanceof Tree.MemberOrTypeExpression) {
                        Tree.MemberOrTypeExpression mte = 
                                (Tree.MemberOrTypeExpression) p;
                        if (mte.getDeclaration()
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
                if (it.getDeclaration()
                        .isClassOrInterfaceMember()) {
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
    
    private void inlineDefinition(
            final List<CommonToken> tokens,
            final List<CommonToken> declarationTokens,
            final Node definition, 
            final TextChange tfc,
            final Tree.InvocationExpression that,
            final Node reference, 
            final boolean needsParens) {
        Declaration dec;
        if (reference 
                    instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression) 
                        reference;
            dec = mte.getDeclaration();
        }
        else if (reference 
                    instanceof Tree.SimpleType) {
            Tree.SimpleType st = 
                    (Tree.SimpleType) 
                        reference;
            dec = st.getDeclarationModel();
        }
        else {
            //can't happen
            return;
        }
        if (inlineRef(reference, dec)) {
            //TODO: breaks for invocations like f(f(x, y),z)
            final StringBuilder result = new StringBuilder();
            class InterpolationVisitor extends Visitor {
                int start = 0;
                final String template = 
                        Nodes.text(definition, 
                                declarationTokens);
                final int templateStart = 
                        definition.getStartIndex();
                void text(Node it) {
                    String text = 
                            template.substring(start,
                                    it.getStartIndex()
                                        - templateStart);
                    result.append(text);
                    start = it.getEndIndex()-templateStart;
                }
                @Override
                public void visit(Tree.BaseMemberExpression it) {
                    super.visit(it);
                    text(it);
                    inlineDefinitionReference(tokens, 
                            declarationTokens, reference, 
                            that, result, it);
                }
                @Override
                public void visit(Tree.QualifiedMemberExpression it) {
                    super.visit(it);
                    text(it);
                    inlineDefinitionReference(tokens, 
                            declarationTokens, reference, 
                            that, result, it);

                }
                @Override
                public void visit(Tree.Type it) {
                    super.visit(it);
                    text(it);
                    inlineAliasDefinitionReference(
                            tokens, declarationTokens, 
                            reference, result, it);
                }
                void finish() {
                    String text = 
                            template.substring(start, 
                                    template.length());
                    result.append(text);
                }
            }
            InterpolationVisitor iv = 
                    new InterpolationVisitor();
            definition.visit(iv);
            iv.finish();
            if (needsParens && 
                    (definition instanceof Tree.OperatorExpression ||
                     definition instanceof Tree.IfExpression ||
                     definition instanceof Tree.SwitchExpression ||
                     definition instanceof Tree.ObjectExpression ||
                     definition instanceof Tree.LetExpression ||
                     definition instanceof Tree.FunctionArgument)) {
                result.insert(0,'(').append(')');
            }
            Node node = that==null ? reference : that;
            tfc.addEdit(new ReplaceEdit(
                    node.getStartIndex(), 
                    node.getDistance(), 
                    result.toString()));
        }
    }
    
    private boolean inlineRef(Node that, Declaration dec) {
        return (!justOne || 
                    that.getUnit().equals(node.getUnit()) && 
                    that.getStartIndex()!=null &&
                    that.getStartIndex()
                        .equals(node.getStartIndex())) &&
                dec!=null && dec.equals(declaration);
    }
    
    private static void interpolatePositionalArguments(
            StringBuilder result, 
            Tree.InvocationExpression that, 
            Tree.StaticMemberOrTypeExpression it,
            boolean sequenced, 
            List<CommonToken> tokens) {
        boolean first = true;
        boolean found = false;
        if (sequenced) {
            result.append("{");
        }
        List<Tree.PositionalArgument> args = 
                that.getPositionalArgumentList()
                    .getPositionalArguments();
        for (Tree.PositionalArgument arg: args) {
            Parameter param = arg.getParameter();
            FunctionOrValue model = param.getModel();
            if (it.getDeclaration().equals(model)) {
                if (param.isSequenced() &&
                        arg instanceof Tree.ListedArgument) {
                    if (first) result.append(" ");
                    if (!first) result.append(", ");
                    first = false;
                }
                result.append(Nodes.text(arg, tokens));
                found = true;
            }
        }
        if (sequenced) {
            if (!first) result.append(" ");
            result.append("}");
        }
        if (!found) {} //TODO: use default value!
    }

    private static void interpolateNamedArguments(
            StringBuilder result,
            Tree.InvocationExpression that, 
            Tree.StaticMemberOrTypeExpression it,
            boolean sequenced, 
            List<CommonToken> tokens) {
        boolean found = false;
        List<Tree.NamedArgument> args = 
                that.getNamedArgumentList()
                    .getNamedArguments();
        for (Tree.NamedArgument arg: args) {
            FunctionOrValue pm = 
                    arg.getParameter()
                        .getModel();
            if (it.getDeclaration().equals(pm)) {
                Tree.SpecifiedArgument sa = 
                        (Tree.SpecifiedArgument) arg;
				Tree.Term argTerm = 
				        sa.getSpecifierExpression()
				            .getExpression()
				            .getTerm();
                result//.append(template.substring(start,it.getStartIndex()-templateStart))
                    .append(Nodes.text(argTerm, tokens));
                //start = it.getStopIndex()-templateStart+1;
                found=true;
            }
        }
        Tree.SequencedArgument seqArg = 
        		that.getNamedArgumentList()
        		    .getSequencedArgument();
        if (seqArg!=null) {
            FunctionOrValue spm = 
                    seqArg.getParameter()
                        .getModel();
            if (it.getDeclaration().equals(spm)) {
                result//.append(template.substring(start,it.getStartIndex()-templateStart))
                    .append("{");
                //start = it.getStopIndex()-templateStart+1;;
                boolean first = true;
                List<Tree.PositionalArgument> pargs = 
                        seqArg.getPositionalArguments();
                for (Tree.PositionalArgument pa: pargs) {
                    if (first) result.append(" ");
                    if (!first) result.append(", ");
                    first=false;
                    result.append(Nodes.text(pa, tokens));
                }
                if (!first) result.append(" ");
                result.append("}");
                found=true;
            }
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
