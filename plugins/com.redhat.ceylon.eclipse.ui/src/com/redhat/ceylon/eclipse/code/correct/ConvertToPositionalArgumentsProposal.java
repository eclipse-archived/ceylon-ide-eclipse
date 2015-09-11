package com.redhat.ceylon.eclipse.code.correct;

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

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Parameter;

class ConvertToPositionalArgumentsProposal extends CorrectionProposal {
    
    public ConvertToPositionalArgumentsProposal(int offset, Change change) {
        super("Convert to positional arguments", change, new Region(offset, 0));
    }

    public static void addConvertToPositionalArgumentsProposal(Collection<ICompletionProposal> proposals, 
            IFile file, Tree.CompilationUnit cu, CeylonEditor editor, int currentOffset) {
        Tree.NamedArgumentList nal = 
                findNamedArgumentList(currentOffset, cu);
        if (nal==null) {
            return;
        }
        final TextChange tc = 
                new TextFileChange("Convert to Positional Arguments", file);
        Integer start = nal.getStartIndex();
        try {
            if (EditorUtil.getDocument(tc).getChar(start-1)==' ') {
                start--;
            }
        }
        catch (BadLocationException e1) {
            e1.printStackTrace();
        }
        int length = nal.getEndIndex()-start;
        StringBuilder result = new StringBuilder().append("(");
        List<CommonToken> tokens = editor.getParseController().getTokens();
        List<Tree.NamedArgument> args = nal.getNamedArguments();
        Tree.SequencedArgument sa = nal.getSequencedArgument();
        for (Parameter p: nal.getNamedArgumentList().getParameterList()
                .getParameters()) {
            boolean found = false;
            if (sa!=null) {
                Parameter param = sa.getParameter();
                if (param==null) {
                    return;
                }
                if (param.getModel().equals(p.getModel())) {
                    found = true;
                    result.append("{ ")
                        .append(Nodes.toString(sa, tokens))
                        .append(" }");
                }
            }
            for (Tree.NamedArgument na: args) {
                Parameter param = na.getParameter();
                if (param==null) {
                    return;
                }
                if (param.getModel().equals(p.getModel())) {
                    found = true;
                    if (na instanceof Tree.SpecifiedArgument) {
                        Tree.SpecifiedArgument sna = (Tree.SpecifiedArgument) na;
                        Tree.SpecifierExpression se = sna.getSpecifierExpression();
                        if (se!=null && se.getExpression()!=null) {
                            result.append(Nodes.toString(se.getExpression(), tokens));
                        }
                        break;
                    }
                    else if (na instanceof Tree.MethodArgument) {
                        Tree.MethodArgument ma = (Tree.MethodArgument) na;
                        if (ma.getDeclarationModel().isDeclaredVoid()) {
                            result.append("void ");
                        }
                        for (Tree.ParameterList pl: ma.getParameterLists()) {
                            result.append(Nodes.toString(pl, tokens));
                        }
                        if (ma.getBlock()!=null) {
                            result.append(" ")
                            .append(Nodes.toString(ma.getBlock(), tokens));
                        }
                        if (ma.getSpecifierExpression()!=null) {
                            result.append(" ")
                            .append(Nodes.toString(ma.getSpecifierExpression(), tokens));
                        }
                    }
                    else {
                        return;
                    }
                }
            }
            if (found) {
                result.append(", ");
            }
        }
        if (result.length()>1) {
            result.setLength(result.length()-2);
        }
        result.append(")");
        tc.setEdit(new ReplaceEdit(start, length, result.toString()));
        int offset = start+result.toString().length();
        proposals.add(new ConvertToPositionalArgumentsProposal(offset, tc));
    }
    
    private static Tree.NamedArgumentList findNamedArgumentList(
            int currentOffset, Tree.CompilationUnit cu) {
        FindNamedArgumentsVisitor fpav = 
                new FindNamedArgumentsVisitor(currentOffset);
        fpav.visit(cu);
        return fpav.getArgumentList();
    }

    private static class FindNamedArgumentsVisitor 
        extends Visitor {
        
        Tree.NamedArgumentList argumentList;
        int offset;
        
        private Tree.NamedArgumentList getArgumentList() {
            return argumentList;
        }

        private FindNamedArgumentsVisitor(int offset) {
            this.offset = offset;
        }
        
        @Override
        public void visit(Tree.NamedArgumentList that) {
            if (offset>=that.getStartIndex() && 
                    offset<=that.getEndIndex()) {
                argumentList = that;
            }
            super.visit(that); 
        }
    }
    
}