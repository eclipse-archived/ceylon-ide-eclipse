/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;

class FillInArgumentNameProposal extends CorrectionProposal {

    public FillInArgumentNameProposal(String name, Change change) {
        super("Fill in argument name '" + name + "'", change, null);
    }

    static void addFillInArgumentNameProposal(Collection<ICompletionProposal> proposals, 
            IDocument doc, IFile file, Tree.SpecifiedArgument sa) {
        Tree.Identifier id = sa.getIdentifier();
        if (id.getToken()==null) {
            TextChange change = new TextFileChange("Convert to Block", file);
            change.setEdit(new MultiTextEdit());
            Tree.Expression e = sa.getSpecifierExpression().getExpression();
            if (e!=null) {
                final String name = id.getText();
                if (e.getTerm() instanceof Tree.FunctionArgument) {
                    //convert anon functions to typed named argument
                    //i.e.     (Param param) => result;
                    //becomes  function fun(Param param) => result;
                    //and      (Param param) { return result; };
                    //becomes  function fun(Param param) { return result; }
                    //and      void (Param param) {};
                    //becomes  void fun(Param param) {}
                    Tree.FunctionArgument fa = (Tree.FunctionArgument) e.getTerm();
                    if (!fa.getParameterLists().isEmpty()) {
                        int startIndex = fa.getParameterLists().get(0).getStartIndex();
                        if (fa.getType().getToken()==null) {
                            //only really necessary if the anon 
                            //function has a block instead of => 
                            change.addEdit(new InsertEdit(startIndex, "function "));
                        }
                        change.addEdit(new InsertEdit(startIndex, name));
                        try {
                            //if it is an anon function with a body,
                            //we must remove the trailing ; which is
                            //required by the named arg list syntax
                            if (fa.getBlock()!=null) {
                                int offset = sa.getEndIndex()-1;
                                if (doc.getChar(offset)==';') {
                                    change.addEdit(new DeleteEdit(offset, 1));
                                }
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
                else {
                    //convert other args to specified named args
                    //i.e.     arg;
                    //becomes  name = arg;
                    change.addEdit(new InsertEdit(sa.getStartIndex(), name + " = "));
                }
                if (change.getEdit().hasChildren()) {
                    proposals.add(new FillInArgumentNameProposal(name, change));
                }
            }
        }
    }

}
