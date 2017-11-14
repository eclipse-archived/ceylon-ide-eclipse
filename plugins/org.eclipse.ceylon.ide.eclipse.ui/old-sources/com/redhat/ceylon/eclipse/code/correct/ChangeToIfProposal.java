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

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

@Deprecated
public class ChangeToIfProposal {

    static void addChangeToIfProposal(Collection<ICompletionProposal> proposals,
            IDocument doc, IFile file, Tree.CompilationUnit rootNode,
            final Tree.Statement statement) {
        if (statement instanceof Tree.Assertion) {
            Tree.ConditionList conditionList = 
                    ((Tree.Assertion) statement).getConditionList();
            if (conditionList!=null) {
                class FindBodyVisitor extends Visitor {
                    Tree.Body result;
                    @Override
                    public void visit(Tree.Body that) {
                        if (that.getStatements().contains(statement)) {
                            result = that;
                        }
                        else {
                            super.visit(that);
                        }
                    }
                }
                FindBodyVisitor fbv = new FindBodyVisitor();
                fbv.visit(rootNode);
                List<Tree.Statement> statements = fbv.result.getStatements();
                Tree.Statement last = statements.get(statements.size()-1);
                boolean isLast = statement==last;
                TextFileChange change = 
                        new TextFileChange("Change Assert To If", file);
                change.setEdit(new MultiTextEdit());
                String newline = utilJ2C().indents().getDefaultLineDelimiter(doc);
                String indent = utilJ2C().indents().getIndent(last, doc);
                int begin = statement.getStartIndex();
                int end = conditionList.getStartIndex();
                change.addEdit(new ReplaceEdit(begin, end-begin, "if "));
                change.addEdit(new ReplaceEdit(statement.getEndIndex()-1, 1, 
                        isLast ? " {}" : " {"));
                //TODO: this is wrong, need to look for lines, not statements!
                for (int i=statements.indexOf(statement)+1; i<statements.size(); i++) {
                    change.addEdit(new InsertEdit(statements.get(i).getStartIndex(), 
                            utilJ2C().indents().getDefaultIndent()));
                }
                if (!isLast) {
                    change.addEdit(new InsertEdit(last.getEndIndex(), 
                            newline + indent + "}"));
                }
                String elseBlock = newline + indent +
                        "else {" + newline + indent + utilJ2C().indents().getDefaultIndent() + 
                        "assert (false);" + newline + indent + "}" ;
                change.addEdit(new InsertEdit(last.getEndIndex(), elseBlock));
                proposals.add(new CorrectionProposal("Change 'assert' to 'if'", change, 
                        new Region(statement.getEndIndex()-3, 0)));
            }
        }
    }

}
