package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

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

class ConvertToNamedArgumentsProposal extends ChangeCorrectionProposal {
	
    final int offset; 
    final IFile file;
    
    public ConvertToNamedArgumentsProposal(int offset, IFile file, Change change) {
	    super("Convert to named arguments", change, CORRECTION);
        this.offset=offset;
        this.file=file;
    }

	@Override
    public void apply(IDocument document) {
		 super.apply(document);
		 EditorUtil.gotoLocation(file, offset);
    }
    
    public static void addConvertToNamedArgumentsProposal(Collection<ICompletionProposal> proposals, 
    		IFile file, Tree.CompilationUnit cu, CeylonEditor editor, int currentOffset) {
        Tree.PositionalArgumentList pal = findPositionalArgumentList(currentOffset, cu);
        if (canConvert(pal)) {
            final TextChange tc = new TextFileChange("Convert To Named Arguments", file);
            Integer start = pal.getStartIndex();
            int length = pal.getStopIndex()-start+1;
            StringBuilder result = new StringBuilder().append(" {");
            boolean sequencedArgs = false;
            List<CommonToken> tokens = editor.getParseController().getTokens();
            for (Tree.PositionalArgument arg: pal.getPositionalArguments()) {
                Parameter param = arg.getParameter();
                if (param==null) return;
				if (param.isSequenced() && (arg instanceof Tree.ListedArgument)) {
                    if (sequencedArgs) {
                    	result.append(",");
                    }
                    else {
                    	result.append(" " + param.getName() + " = [");
                    	sequencedArgs=true;
                    }
                    result.append(" " + AbstractRefactoring.toString(arg, tokens));
                }
                else {
                    result.append(" " + param.getName() + " = " + 
                            AbstractRefactoring.toString(arg, tokens) + ";");
                }
            }
            if (sequencedArgs) {
            	result.append("];");
            }
            result.append(" }");
            tc.setEdit(new ReplaceEdit(start, length, result.toString()));
            int offset = start+result.toString().length();
			proposals.add(new ConvertToNamedArgumentsProposal(offset, file, tc));
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
        extends Visitor 
        implements NaturalVisitor {
    	
    	Tree.PositionalArgumentList argumentList;
    	int offset;
    	
    	private Tree.PositionalArgumentList getArgumentList() {
	        return argumentList;
        }

		private FindPositionalArgumentsVisitor(int offset) {
	        this.offset = offset;
        }
		
		@Override
		public void visit(Tree.PositionalArgumentList that) {
			if (offset>=that.getStartIndex() && 
					offset<=that.getStopIndex()+1) {
				argumentList = that;
			}
		    super.visit(that); 
		}
    }
    
}