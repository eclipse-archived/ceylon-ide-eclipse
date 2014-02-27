package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring;

class ConvertToPositionalArgumentsProposal extends CorrectionProposal {
    
    private final int offset; 
    private final IFile file;
    
    public ConvertToPositionalArgumentsProposal(int offset, IFile file, Change change) {
        super("Convert to positional arguments", change);
        this.offset=offset;
        this.file=file;
    }

    @Override
    public void apply(IDocument document) {
         super.apply(document);
         EditorUtil.gotoLocation(file, offset);
    }
    
    public static void addConvertToPositionalArgumentsProposal(Collection<ICompletionProposal> proposals, 
            IFile file, Tree.CompilationUnit cu, CeylonEditor editor, int currentOffset) {
        Tree.NamedArgumentList nal = 
                findNamedArgumentList(currentOffset, cu);
        if (nal==null) {
            return;
        }
        final TextChange tc = 
                new TextFileChange("Convert To Positional Arguments", file);
        Integer start = nal.getStartIndex();
        int length = nal.getStopIndex()-start+1;
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
                        .append(AbstractRefactoring.toString(sa, tokens))
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
                            result.append(AbstractRefactoring.toString(se.getExpression(), tokens));
                        }
                        break;
                    }
                    else if (na instanceof Tree.MethodArgument) {
                        Tree.MethodArgument ma = (Tree.MethodArgument) na;
                        if (ma.getDeclarationModel().isDeclaredVoid()) {
                            result.append("void ");
                            for (Tree.ParameterList pl: ma.getParameterLists()) {
                                result.append(AbstractRefactoring.toString(pl, tokens));
                            }
                            if (ma.getBlock()!=null) {
                                result.append(" ")
                                    .append(AbstractRefactoring.toString(ma.getBlock(), tokens));
                            }
                            if (ma.getSpecifierExpression()!=null) {
                                result.append(" ")
                                    .append(AbstractRefactoring.toString(ma.getSpecifierExpression(), tokens));
                            }
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
        proposals.add(new ConvertToPositionalArgumentsProposal(offset, file, tc));
    }
    
    private static Tree.NamedArgumentList findNamedArgumentList(
            int currentOffset, Tree.CompilationUnit cu) {
        FindNamedArgumentsVisitor fpav = 
                new FindNamedArgumentsVisitor(currentOffset);
        fpav.visit(cu);
        return fpav.getArgumentList();
    }

    private static class FindNamedArgumentsVisitor 
        extends Visitor 
        implements NaturalVisitor {
        
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
                    offset<=that.getStopIndex()+1) {
                argumentList = that;
            }
            super.visit(that); 
        }
    }
    
}