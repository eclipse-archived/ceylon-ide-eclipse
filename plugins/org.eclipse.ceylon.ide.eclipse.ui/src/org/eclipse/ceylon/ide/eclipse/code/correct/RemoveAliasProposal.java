/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.REMOVE_CORR;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getAbstraction;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.util.DocLinks;

class RemoveAliasProposal extends CorrectionProposal {
        
    private static final class AliasRemovalVisitor extends Visitor {
        private final Declaration dec;
        private final TextFileChange change;
        private final Tree.Identifier aid;

        private AliasRemovalVisitor(Declaration dec, 
                TextFileChange change, Tree.Identifier aid) {
            this.dec = dec;
            this.change = change;
            this.aid = aid;
        }

        @Override
        public void visit(
                Tree.StaticMemberOrTypeExpression that) {
            super.visit(that);
            addRemoval(that.getIdentifier(), 
                    that.getDeclaration());
        }

        @Override
        public void visit(Tree.SimpleType that) {
            super.visit(that);
            addRemoval(that.getIdentifier(), 
                    that.getDeclarationModel());
        }

        @Override
        public void visit(Tree.MemberLiteral that) {
            super.visit(that);
            addRemoval(that.getIdentifier(), 
                    that.getDeclaration());
        }

        private void addRemoval(Tree.Identifier id, Declaration d) {
            if (id!=null && d!=null && 
                    dec.equals(getAbstraction(d)) && 
                    id.getText().equals(aid.getText())) {
                change.addEdit(new ReplaceEdit(
                        id.getStartIndex(), 
                        id.getDistance(), 
                        dec.getName()));
            }
        }
        
        @Override
        public void visit(Tree.DocLink that) {
            super.visit(that);
            //TODO: copy/paste from EnterAliasRefactoring
            Declaration base = that.getBase();
            if (base!=null && dec.equals(base)) {
                Region region = DocLinks.nameRegion(that, 0);
                change.addEdit(new ReplaceEdit(
                        region.getOffset(), 
                        region.getLength(), 
                        dec.getName()));
            }
        }
    }

    private RemoveAliasProposal(IFile file, Declaration dec, 
            TextFileChange change) {
        super("Remove alias of '" + dec.getName() + "'", 
                change, null, REMOVE_CORR);
    }
    
    static void addRemoveAliasProposal(
            Tree.ImportMemberOrType imt,  
            Collection<ICompletionProposal> proposals, 
            IFile file, CeylonEditor editor) {
        if (imt!=null) {
            Declaration dec = imt.getDeclarationModel();
            Tree.CompilationUnit upToDateAndTypechecked = 
                    editor.getParseController()
                        .getTypecheckedRootNode();
            if (dec!=null && imt.getAlias()!=null
                    && upToDateAndTypechecked != null) {
                TextFileChange change = 
                        new TextFileChange("Remove Alias", 
                                file);
                change.setEdit(new MultiTextEdit());
                Tree.Identifier aid = 
                        imt.getAlias().getIdentifier();
                change.addEdit(new DeleteEdit(
                        aid.getStartIndex(), 
                        imt.getIdentifier().getStartIndex()
                            -aid.getStartIndex()));
                upToDateAndTypechecked.visit(
                        new AliasRemovalVisitor(dec, change, aid));
                proposals.add(new RemoveAliasProposal(file, dec, change));
            }
        }
    }
    
}
