/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.util.DocLinks.hasPackage;
import static org.eclipse.ceylon.ide.eclipse.util.DocLinks.nameRegion;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findImport;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getAbstraction;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Alias;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

public class EnterAliasRefactoring extends AbstractRefactoring {
    
    private final class EnterAliasVisitor extends Visitor {
        private final TextChange change;

        private EnterAliasVisitor(TextChange change) {
            this.change = change;
        }

        @Override
        public void visit(
                Tree.StaticMemberOrTypeExpression that) {
            super.visit(that);
            addEdit(document, that.getIdentifier(), 
                    that.getDeclaration());
        }

        @Override
        public void visit(Tree.SimpleType that) {
            super.visit(that);
            addEdit(document, that.getIdentifier(), 
                    that.getDeclarationModel());
        }

        @Override
        public void visit(Tree.MemberLiteral that) {
            super.visit(that);
            addEdit(document, that.getIdentifier(), 
                    that.getDeclaration());
        }

        @Override
        public void visit(Tree.DocLink that) {
            super.visit(that);
            Declaration base = that.getBase();
            if (!hasPackage(that) && isReference(base)) {
                Region region = nameRegion(that, 0);
                change.addEdit(new ReplaceEdit(
                        region.getOffset(), 
                        region.getLength(), 
                        newName));
            }
        }

        private void addEdit(IDocument document, 
                Tree.Identifier id, Declaration d) {
            if (id!=null && isReference(d)) {
                int pos = id.getStartIndex();
                int len = id.getDistance();
                change.addEdit(new ReplaceEdit(pos, len, 
                        newName));
            }
        }
    }

    private String newName;
    private Tree.ImportMemberOrType element;
    
    boolean isReference(Declaration declaration) {
        return declaration!=null && 
                getElement().getDeclarationModel()
                    .equals(getAbstraction(declaration));
    }
    
    public Tree.ImportMemberOrType getElement() {
        return element;
    }
    
    public EnterAliasRefactoring(IEditorPart editor) {
        super(editor);
        element = findImport(rootNode, node);
        if (element!=null) {
            final Alias alias = element.getAlias();
            Tree.Identifier id;
            if (alias==null) {
                id = element.getIdentifier();
            }
            else {
                id = alias.getIdentifier();
            }
            newName = id.getText();
            if (id.getDistance() > newName.length()) {
                switch (id.getToken().getType()) {
                case CeylonLexer.UIDENTIFIER:
                    newName = "\\I" + newName;
                    break;
                case CeylonLexer.LIDENTIFIER:
                    newName = "\\i" + newName;
                    break;
                }
            }
        }
    }

    @Override
    public boolean getEnabled() {
        return element!=null &&
                element.getDeclarationModel()!=null;
    }

    public String getName() {
        return "Enter Alias";
    }

    public boolean forceWizardMode() {
        Declaration existing = element.getScope()
                .getMemberOrParameter(element.getUnit(), 
                        newName, null, false);
        return existing!=null;
    }
    
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        Declaration existing = element.getScope()
                .getMemberOrParameter(element.getUnit(), 
                        newName, null, false);
        if (null!=existing) {
            return createWarningStatus(
                    "An existing declaration named '" +
                    newName + 
                    "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    public TextChange createChange(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        TextChange tfc = newLocalChange();
        refactorInFile(tfc);
        return tfc;
    }
    
    @Override
    void refactorInFile(TextChange textChange, 
            CompositeChange compositChange, 
            Tree.CompilationUnit rootNode,
            List<CommonToken> tokens) {
        throw new UnsupportedOperationException();
    }
    
    int refactorInFile(final TextChange change) {
        change.setEdit(new MultiTextEdit());
        Tree.Alias alias = element.getAlias();
        Declaration dec = element.getDeclarationModel();
        
        final int adjust;
        boolean same = newName.equals(dec.getName());
        if (alias==null) {
//            if (!same) {
                change.addEdit(new InsertEdit(
                        element.getStartIndex(), 
                        newName + "="));
                adjust = newName.length()+1;
//            }
//            else {
//                adjust = 0;
//            }
        }
        else {
            Tree.Identifier id = alias.getIdentifier();
            int start = id.getStartIndex();
            int length = id.getDistance();
            if (same) {
                int stop = 
                        element.getIdentifier()
                            .getStartIndex();
                change.addEdit(new DeleteEdit(start, stop-start));
                adjust = start - stop; 
            }
            else {
                change.addEdit(new ReplaceEdit(start, length, 
                        newName));
                adjust = newName.length()-length;
            }
        }
        
        new EnterAliasVisitor(change).visit(rootNode);
        
        return adjust;
        
    }

    public void setNewName(String text) {
        newName = text;
    }
    
    public String getNewName() {
        return newName;
    }
    
    @Override
    protected boolean isAffectingOtherFiles() {
        return false;
    }
}
