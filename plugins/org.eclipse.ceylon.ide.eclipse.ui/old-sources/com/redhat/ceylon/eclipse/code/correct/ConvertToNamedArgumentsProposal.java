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

import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getDocument;
import static java.lang.Character.isWhitespace;

import java.util.Collection;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Expression;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.Unit;

class ConvertToNamedArgumentsProposal extends CorrectionProposal {
    
    public ConvertToNamedArgumentsProposal(int offset, Change change) {
        super("Convert to named arguments", change, new Region(offset, 0));
    }
    
    public static void addConvertToNamedArgumentsProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file, Tree.CompilationUnit cu, 
            CeylonEditor editor, int currentOffset) {
        Tree.PositionalArgumentList pal = 
                findPositionalArgumentList(currentOffset, cu);
        if (canConvert(pal)) {
            final TextChange tc = 
                    new TextFileChange("Convert to Named Arguments", file);
            Integer start = pal.getStartIndex();
            int length = pal.getDistance();
            StringBuilder result = new StringBuilder();
            try {
                if (!isWhitespace(getDocument(tc).getChar(start-1))) {
                    result.append(" ");
                }
            }
            catch (BadLocationException e1) {
                e1.printStackTrace();
            }
            result.append("{ ");
            boolean sequencedArgs = false;
            List<CommonToken> tokens = 
                    editor.getParseController().getTokens();
            final List<Tree.PositionalArgument> args = 
                    pal.getPositionalArguments();
            int i=0;
            for (Tree.PositionalArgument arg: args) {
                Parameter param = arg.getParameter();
                if (param==null) {
                    return;
                }
                if (param.isSequenced()) {
                    if (sequencedArgs) {
                        result.append(", ");
                    }
                    else {
                        //TODO: if we _only_ have a single spread 
                        //      argument we don't need to wrap it
                        //      in a sequence, we only need to
                        //      get rid of the * operator
                        result.append(param.getName())
                            .append(" = [");
                        sequencedArgs=true;
                    }
                    result.append(Nodes.text(arg, tokens));
                }
                else {
                    if (sequencedArgs) {
                        return;
                    }
                    if (arg instanceof Tree.ListedArgument) {
                        final Expression e = 
                                ((Tree.ListedArgument) arg).getExpression();
                        if (e!=null) {
                            Tree.Term term = e.getTerm();
                            if (term instanceof Tree.FunctionArgument) {
                                Tree.FunctionArgument fa = 
                                        (Tree.FunctionArgument) term;
                                if (fa.getType() instanceof Tree.VoidModifier) {
                                    result.append("void ");
                                }
                                else {
                                    result.append("function ");
                                }
                                result.append(param.getName());
                                Unit unit = cu.getUnit();
                                Nodes.appendParameters(result, fa, unit, tokens);
                                if (fa.getBlock()!=null) {
                                    result.append(" ")
                                          .append(Nodes.text(fa.getBlock(), tokens))
                                          .append(" ");
                                }
                                else {
                                    result.append(" => ");
                                }
                                if (fa.getExpression()!=null) {
                                    result.append(Nodes.text(fa.getExpression(), tokens))
                                          .append("; ");
                                }
                                continue;
                            }
                            if (++i==args.size() && 
                                    term instanceof Tree.SequenceEnumeration) {
                                Tree.SequenceEnumeration se = 
                                        (Tree.SequenceEnumeration) term;
                                Tree.SequencedArgument sa = se.getSequencedArgument();
                                if (sa!=null) {
                                    result.append(Nodes.text(sa, tokens))
                                          .append(" ");
                                }
                                continue;
                            }
                        }
                    }
                    result.append(param.getName())
                        .append(" = ")
                        .append(Nodes.text(arg, tokens))
                        .append("; ");
                }
            }
            if (sequencedArgs) {
                result.append("]; ");
            }
            result.append("}");
            tc.setEdit(new ReplaceEdit(start, length, result.toString()));
            int offset = start+result.toString().length();
            proposals.add(new ConvertToNamedArgumentsProposal(offset, tc));
        }
    }

    public static boolean canConvert(Tree.PositionalArgumentList pal) {
        if (pal==null) {
            return false;
        }
        else {
            //if it is an indirect invocations, or an 
            //invocation of an overloaded Java method
            //or constructor, we can't call it using
            //named arguments!
            for (Tree.PositionalArgument arg: pal.getPositionalArguments()) {
                Parameter param = arg.getParameter();
                if (param==null) return false;
            }
            return true;
        }
    }
    
    private static Tree.PositionalArgumentList findPositionalArgumentList(
            int currentOffset, Tree.CompilationUnit cu) {
        FindPositionalArgumentsVisitor fpav = 
                new FindPositionalArgumentsVisitor(currentOffset);
        fpav.visit(cu);
        return fpav.getArgumentList();
    }

    private static class FindPositionalArgumentsVisitor 
        extends Visitor {
        
        Tree.PositionalArgumentList argumentList;
        int offset;
        
        private Tree.PositionalArgumentList getArgumentList() {
            return argumentList;
        }

        private FindPositionalArgumentsVisitor(int offset) {
            this.offset = offset;
        }
        
        @Override
        public void visit(Tree.ExtendedType that) {
            //don't add proposals for extends clause
        }
        
        @Override
        public void visit(Tree.PositionalArgumentList that) {
            Integer start = that.getStartIndex();
            Integer stop = that.getEndIndex();
            if (start!=null && offset>=start && 
                stop!=null && offset<=stop) {
                argumentList = that;
            }
            super.visit(that); 
        }
    }
    
}