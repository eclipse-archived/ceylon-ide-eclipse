package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.defaultValue;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class AddInitializerProposal extends InitializerProposal {
    
	private AddInitializerProposal(TypedDeclaration dec, int offset, int length,
	        TextChange change) {
        super("Add initializer to '" + dec.getName() + "'", 
        		change, dec, dec.getType(), 
        		new Region(offset, length),
        		MINOR_CHANGE, -1, null);
    }
    
    private static void addInitializerProposal(Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.TypedDeclaration decNode, Tree.SpecifierOrInitializerExpression sie) {
        FunctionOrValue dec = (FunctionOrValue) decNode.getDeclarationModel();
        if (dec==null) return;
        if (dec.getInitializerParameter()==null && !dec.isFormal()) {
            TextChange change = new TextFileChange("Add Initializer", file);
            int offset = decNode.getEndIndex()-1;
        	String defaultValue = defaultValue(cu.getUnit(), dec.getType());
            String def;
            int selectionOffset;
            if (decNode instanceof Tree.MethodDeclaration) {
				def = " => " + defaultValue;
				selectionOffset = offset + 4;
            }
            else {
                def = " = " + defaultValue;
                selectionOffset = offset + 3;
            }
            
            change.setEdit(new InsertEdit(offset, def));
            proposals.add(new AddInitializerProposal(dec, 
                    selectionOffset, defaultValue.length(), 
                    change));
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