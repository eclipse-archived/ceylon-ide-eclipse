package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.defaultValue;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class AddInitializerProposal extends CorrectionProposal {
    
	private AddInitializerProposal(Declaration dec, int offset, 
			TextChange change) {
        super("Add initializer to '" + dec.getName() + "'", 
        		change, new Point(offset, 0));
    }
    
    private static void addInitializerProposal(Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.TypedDeclaration decNode, Tree.SpecifierOrInitializerExpression sie) {
        MethodOrValue dec = (MethodOrValue) decNode.getDeclarationModel();
        if (dec==null) return;
        if (dec.getInitializerParameter()==null && !dec.isFormal()) {
            TextChange change = new TextFileChange("Add Initializer", file);
        	String defaultValue = defaultValue(cu.getUnit(), dec.getType());
            String def;
            if (decNode instanceof Tree.MethodDeclaration) {
				def = " => " + defaultValue;
            }
            else {
                def = " = " + defaultValue;
            }
            
            Integer offset = decNode.getStopIndex();
            change.setEdit(new InsertEdit(offset, def));
            proposals.add(new AddInitializerProposal(dec, offset+def.length(), change));
        }
    }

	static void addInitializerProposals(Collection<ICompletionProposal> proposals,
			IFile file, Tree.CompilationUnit cu, Node node) {
		if (node instanceof Tree.AttributeDeclaration) {
	        Tree.AttributeDeclaration attDecNode = (Tree.AttributeDeclaration) node;
	        Tree.SpecifierOrInitializerExpression sie = 
	                attDecNode.getSpecifierOrInitializerExpression();
	        if (!(sie instanceof Tree.LazySpecifierExpression)) {
	            addInitializerProposal(cu, proposals, file, attDecNode, sie);
	        }
	    }
	    if (node instanceof Tree.MethodDeclaration) {
	        Tree.MethodDeclaration methDecNode = (Tree.MethodDeclaration) node;
	        Tree.SpecifierExpression sie = methDecNode.getSpecifierExpression();
	        addInitializerProposal(cu, proposals, file, methDecNode, sie);
	    }
	}
    
}