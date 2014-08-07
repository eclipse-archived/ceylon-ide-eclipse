package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;

class SplitDeclarationProposal extends CorrectionProposal {
    
	private SplitDeclarationProposal(Declaration dec, 
	        int offset, TextChange change) {
        super("Split declaration of '" + dec.getName() + "'", change,
                new Region(offset, 0));
    }
    
	private static void addSplitDeclarationProposal(IDocument doc, 
	        Tree.TypedDeclaration decNode, Tree.CompilationUnit cu, 
	        IFile file, Collection<ICompletionProposal> proposals) {
        TypedDeclaration dec = decNode.getDeclarationModel();
        if (dec==null) return;
        if (dec.isToplevel()) return;
        Tree.Identifier id = decNode.getIdentifier();
        if (id==null || id.getToken()==null) return;
        int idStartOffset = id.getStartIndex();
        int idEndOffset = id.getStopIndex()+1;
        int startOffset = decNode.getStartIndex();
        int paramsEndOffset = idEndOffset;
        String paramsString = "";
        String typeString;
        try {
            int typeStartOffset = decNode.getType().getStartIndex();
            int typeEndOffset = decNode.getType().getStopIndex()+1;
            typeString = doc.get(typeStartOffset, typeEndOffset-typeStartOffset);
            if (decNode instanceof Tree.MethodDeclaration) {
                List<ParameterList> pls = 
                        ((Tree.MethodDeclaration) decNode).getParameterLists();
                if (pls.isEmpty()) {
                    return;
                } 
                else {
                    int paramsOffset = pls.get(0).getStartIndex();
                    paramsEndOffset = pls.get(pls.size()-1).getStopIndex()+1;
                    paramsString = doc.get(paramsOffset, paramsEndOffset-paramsOffset);
                }
            }
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
            return;
        }
        TextChange change = new TextFileChange("Split Declaration", file);
        change.setEdit(new MultiTextEdit());
        String delim = getDefaultLineDelimiter(doc);
        String indent = getIndent(decNode, doc);
        if (dec.isParameter()) {
            //TODO: does not handle default args correctly for callable parameters
            change.addEdit(new DeleteEdit(startOffset, idStartOffset-startOffset));
            change.addEdit(new DeleteEdit(idEndOffset, paramsEndOffset-idEndOffset));
            Node containerNode = (Tree.Declaration) 
                    getReferencedNode((Declaration) dec.getContainer(), cu);
            Tree.Body body;
            if (containerNode instanceof Tree.ClassDefinition) {
                body = ((Tree.ClassDefinition) containerNode).getClassBody();
            }
            else if (containerNode instanceof Tree.MethodDefinition) {
                body = ((Tree.MethodDefinition) containerNode).getBlock();
            }
            else {
                return;
            }
            if (body.getStatements().contains(decNode)) {
                return;
            }
            Tree.AnnotationList al = decNode.getAnnotationList();
            String annotations;
            if (al==null || al.getToken()==null) {
                annotations = "";
            }
            else {
                try {
                    int len = al.getStopIndex()-al.getStartIndex()+1;
                    if (len==0) {
                        annotations = "";
                    }
                    else {
                        annotations = doc.get(al.getStartIndex(), len) + " ";
                    }
                }
                catch (BadLocationException e) {
                    annotations = "";
                }
            }
            String text = delim + indent + getDefaultIndent() + 
                    annotations + typeString + " " + dec.getName() + 
                    paramsString + ";";
            if (body.getStopIndex()==body.getStartIndex()+1) {
                text += delim + indent;
            }
            change.addEdit(new InsertEdit(body.getStartIndex()+1, text));
        }
        else {
            String text = paramsString +";" + 
                    delim + indent + dec.getName();
            change.addEdit(new InsertEdit(idEndOffset, text));
        }
        Type type = decNode.getType();
        int il;
        if (type instanceof Tree.LocalModifier) {
            ProducedType infType = type.getTypeModel();
            String explicitType;
            if (infType==null) {
                explicitType = "Object";
                il=0;
            }
            else {
                explicitType = infType.getProducedTypeName();
                HashSet<Declaration> decs = new HashSet<Declaration>();
                importType(decs, infType, cu);
                il=applyImports(change, decs, cu, doc);
            }
            Integer typeOffset = type.getStartIndex();
            Integer typeLen = type.getStopIndex()-typeOffset+1;
            change.addEdit(new ReplaceEdit(typeOffset, typeLen, 
                    explicitType));
        }
        else {
            il=0;
        }
        proposals.add(new SplitDeclarationProposal(dec, 
                idEndOffset+il, change));
    }

	static void addSplitDeclarationProposals(
			Collection<ICompletionProposal> proposals, IDocument doc,
			IFile file, Tree.CompilationUnit cu, Tree.Declaration decNode) {
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
	        if (decNode instanceof Tree.MethodDeclaration) {
	            Tree.MethodDeclaration methDecNode = 
	                    (Tree.MethodDeclaration) decNode;
	            Tree.SpecifierExpression sie = 
	                    methDecNode.getSpecifierExpression();
	            if (sie!=null || dec.isParameter()) {
	                addSplitDeclarationProposal(doc, methDecNode, cu, file, proposals);
	            }
	        }
	    }
	}
    
}