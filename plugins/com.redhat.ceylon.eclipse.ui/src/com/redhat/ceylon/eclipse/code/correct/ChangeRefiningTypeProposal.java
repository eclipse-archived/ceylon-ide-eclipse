package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.FindUtils.findDeclaration;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class ChangeRefiningTypeProposal {

	static void addChangeRefiningTypeProposal(IFile file,
	        Tree.CompilationUnit cu, Collection<ICompletionProposal> proposals,
	        Node node) {
	    Tree.Declaration decNode = findDeclaration(cu, node);
	    Declaration rd = decNode.getDeclarationModel()
	    		.getRefinedDeclaration();
	    if (rd instanceof TypedDeclaration) {
	    	String type = ((TypedDeclaration) rd).getType()
	    			.getProducedTypeName(decNode.getUnit());
	    	TextFileChange change = new TextFileChange("Change Type", file);
	    	change.setEdit(new ReplaceEdit(node.getStartIndex(), 
	    			node.getStopIndex()-node.getStartIndex()+1, 
	    			type));
	    	proposals.add(new ChangeCorrectionProposal("Change type to '" + type + "'", change));
	    }
	}

}
