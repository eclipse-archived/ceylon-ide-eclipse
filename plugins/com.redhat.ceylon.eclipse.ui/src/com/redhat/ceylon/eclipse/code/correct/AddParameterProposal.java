package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.defaultValue;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.correct.SpecifyTypeProposal.inferType;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_CORR;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;

class AddParameterProposal extends CorrectionProposal {
    
	private AddParameterProposal(Declaration d, Declaration dec, 
			int offset, TextChange change) {
        super("Add '" + d.getName() + "' to parameter list of '" + dec.getName() + "'", 
                change, new Point(offset, 0), ADD_CORR);
    }
    
    private static void addParameterProposal(Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.TypedDeclaration decNode, Tree.SpecifierOrInitializerExpression sie) {
        MethodOrValue dec = (MethodOrValue) decNode.getDeclarationModel();
        if (dec==null) return;
        if (dec.getInitializerParameter()==null && !dec.isFormal()) {
            TextChange change = new TextFileChange("Add Parameter", file);
            change.setEdit(new MultiTextEdit());
            IDocument doc;
			try {
				doc = change.getCurrentDocument(null);
			}
			catch (CoreException e) {
				e.printStackTrace();
				return;
			}
            //TODO: copy/pasted from SplitDeclarationProposal 
            String params = null;
            if (decNode instanceof Tree.MethodDeclaration) {
                List<ParameterList> pls = 
                        ((Tree.MethodDeclaration) decNode).getParameterLists();
                if (pls.isEmpty()) {
                    return;
                } 
                else {
                    Integer start = pls.get(0).getStartIndex();
                    Integer end = pls.get(pls.size()-1).getStopIndex();
                    try {
                        params = doc.get(start, end-start+1);
                    } 
                    catch (BadLocationException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            Tree.Declaration container = findDeclarationWithBody(cu, decNode);
            Tree.ParameterList pl;
            if (container instanceof Tree.ClassDefinition) {
                pl = ((Tree.ClassDefinition) container).getParameterList();
                if (pl==null) {
                    return;
                }
            }
            else if (container instanceof Tree.MethodDefinition) {
                List<Tree.ParameterList> pls = 
                        ((Tree.MethodDefinition) container).getParameterLists();
                if (pls.isEmpty()) {
                    return;
                }
                pl = pls.get(0);
            }
            else {
                return;
            }
            String def;
            if (sie==null) {
            	String defaultValue = 
            			defaultValue(cu.getUnit(), dec.getType());
            	if (decNode instanceof Tree.MethodDeclaration) {
            		def = " => " + defaultValue;
            	}
            	else {
            		def = " = " + defaultValue;
            	}
            }
            else {
            	int start;
                try {
                	def = doc.get(sie.getStartIndex(), 
                			sie.getStopIndex()-sie.getStartIndex()+1);
                	start = sie.getStartIndex();
                    if (start>0 && doc.get(start-1,1).equals(" ")) {
                        start--;
                        def = " " + def;
                    }
                } 
                catch (BadLocationException e) {
                    e.printStackTrace();
                    return;
                }
                change.addEdit(new DeleteEdit(start, sie.getStopIndex()-start+1));
            }
            if (params!=null) def = " = " + params + def;
            String param = (pl.getParameters().isEmpty() ? "" : ", ") + 
                    dec.getName() + def;
            Integer offset = pl.getStopIndex();
            change.addEdit(new InsertEdit(offset, param));
            Type type = decNode.getType();
            int shift=0;
            if (type instanceof Tree.LocalModifier) {
                Integer typeOffset = type.getStartIndex();
                ProducedType infType = inferType(cu, type);
                String explicitType;
                if (infType==null) {
                    explicitType = "Object";
                }
                else {
                    explicitType = infType.getProducedTypeName();
                    HashSet<Declaration> decs = new HashSet<Declaration>();
                    importType(decs, infType, cu);
                    shift = applyImports(change, decs, cu, doc);
                }
                change.addEdit(new ReplaceEdit(typeOffset, type.getText().length(), 
                        explicitType));
            }
            proposals.add(new AddParameterProposal(dec, container.getDeclarationModel(), 
                    offset+param.length()+shift, change));
        }
    }

	static void addParameterProposals(Collection<ICompletionProposal> proposals,
			IFile file, Tree.CompilationUnit cu, Node node) {
		if (node instanceof Tree.AttributeDeclaration) {
	        Tree.AttributeDeclaration attDecNode = (Tree.AttributeDeclaration) node;
	        Tree.SpecifierOrInitializerExpression sie = 
	                attDecNode.getSpecifierOrInitializerExpression();
	        if (!(sie instanceof Tree.LazySpecifierExpression)) {
	            addParameterProposal(cu, proposals, file, attDecNode, sie);
	        }
	    }
	    if (node instanceof Tree.MethodDeclaration) {
	        Tree.MethodDeclaration methDecNode = (Tree.MethodDeclaration) node;
	        Tree.SpecifierExpression sie = methDecNode.getSpecifierExpression();
	        addParameterProposal(cu, proposals, file, methDecNode, sie);
	    }
	}
    
}