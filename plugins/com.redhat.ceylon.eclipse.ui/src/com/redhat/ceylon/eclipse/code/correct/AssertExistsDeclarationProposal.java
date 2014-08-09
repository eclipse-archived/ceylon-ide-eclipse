package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;

class AssertExistsDeclarationProposal extends CorrectionProposal {
    
	private AssertExistsDeclarationProposal(Declaration dec, 
	        int offset, TextChange change) {
        super("Change to 'assert (exists " + dec.getName() + ")'", change,
                new Region(offset, 0));
    }
    
	private static void addSplitDeclarationProposal(IDocument doc, 
	        Tree.AttributeDeclaration decNode, Tree.CompilationUnit cu, 
	        IFile file, Collection<ICompletionProposal> proposals) {
        Value dec = decNode.getDeclarationModel();
        Tree.SpecifierOrInitializerExpression sie = 
                decNode.getSpecifierOrInitializerExpression();
        if (dec==null || dec.isParameter() || dec.isToplevel() ||
                sie==null || sie.getExpression()==null) { 
            return;
        }
        ProducedType siet = sie.getExpression().getTypeModel();
        ProducedType nvt = cu.getUnit().getNullValueDeclaration().getType();
        if (isTypeUnknown(siet) || !nvt.isSubtypeOf(siet)) {
            return;
        }
        Tree.Identifier id = decNode.getIdentifier();
        if (id==null || id.getToken()==null) {
            return;
        }
//        int idStartOffset = id.getStartIndex();
        int idEndOffset = id.getStopIndex()+1;
        int semiOffset = decNode.getStopIndex();
        
        TextChange change = new TextFileChange("Assert Exists Declaration", file);
        change.setEdit(new MultiTextEdit());

        Type type = decNode.getType();
        Integer typeOffset = type.getStartIndex();
        Integer typeLen = type.getStopIndex()-typeOffset+1;
        change.addEdit(new ReplaceEdit(typeOffset, typeLen, 
                "assert (exists"));
        change.addEdit(new InsertEdit(semiOffset, ")"));
        proposals.add(new AssertExistsDeclarationProposal(dec, 
                idEndOffset + 14 - typeLen, change));
    }

	static void addAssertExistsDeclarationProposals(
			Collection<ICompletionProposal> proposals, IDocument doc,
			IFile file, Tree.CompilationUnit cu, Tree.Declaration decNode) {
	    if (decNode==null) return;
	    Declaration dec = decNode.getDeclarationModel();
	    if (dec!=null) {
	        if (decNode instanceof Tree.AttributeDeclaration) {
	            Tree.AttributeDeclaration attDecNode = 
	                    (Tree.AttributeDeclaration) decNode;
	            Tree.SpecifierOrInitializerExpression sie = 
	                    attDecNode.getSpecifierOrInitializerExpression();
	            if (sie!=null || dec.isParameter()) {
	                addSplitDeclarationProposal(doc, attDecNode, cu, file, proposals);
	            }
	        }
	    }
	}
    
}