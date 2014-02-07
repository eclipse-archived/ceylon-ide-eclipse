package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.FindUtils.findDeclaration;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class ChangeRefiningTypeProposal {

	static void addChangeRefiningTypeProposal(IFile file,
	        Tree.CompilationUnit cu, Collection<ICompletionProposal> proposals,
	        Node node) {
	    Tree.Declaration decNode = findDeclaration(cu, node);
	    Declaration dec = decNode.getDeclarationModel();
		Declaration rd = dec.getRefinedDeclaration();
	    if (rd instanceof TypedDeclaration) {
	    	ProducedType supertype = ((TypeDeclaration) dec.getContainer()).getType()
	    			.getSupertype((TypeDeclaration) rd.getContainer());
	    	ProducedType t = ((TypedDeclaration) rd).getProducedTypedReference(supertype, 
	    			Collections.<ProducedType>emptyList()).getType();
	    	String type = t.getProducedTypeName(decNode.getUnit());
	    	TextFileChange change = new TextFileChange("Change Type", file);
	    	change.setEdit(new ReplaceEdit(node.getStartIndex(), 
	    			node.getStopIndex()-node.getStartIndex()+1, 
	    			type));
	    	proposals.add(new ChangeCorrectionProposal("Change type to '" + type + "'", change));
	    }
	}

}
