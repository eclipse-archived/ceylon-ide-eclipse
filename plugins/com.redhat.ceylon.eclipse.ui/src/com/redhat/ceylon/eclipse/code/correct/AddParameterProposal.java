package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.defaultValue;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_CORR;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;

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

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;

class AddParameterProposal extends InitializerProposal {
    
	private AddParameterProposal(Declaration d, Declaration dec, 
	        Type type, int offset, int len, TextChange change, 
	        int exitPos, CeylonEditor editor) {
        super("Add '" + d.getName() + "' to parameter list of '" + dec.getName() + "'", 
                change, dec, type, new Region(offset, len), ADD_CORR, exitPos, editor);
    }
	
    private static void addParameterProposal(Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.TypedDeclaration decNode, 
            Tree.SpecifierOrInitializerExpression sie, Node node,
            CeylonEditor editor) {
        FunctionOrValue dec = (FunctionOrValue) decNode.getDeclarationModel();
        if (dec==null) return;
        if (dec.getInitializerParameter()==null && !dec.isFormal()) {
            TextChange change = new TextFileChange("Add Parameter", file);
            change.setEdit(new MultiTextEdit());
            IDocument doc = EditorUtil.getDocument(change);
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
            else if (container instanceof Tree.Constructor) {
                pl = ((Tree.Constructor) container).getParameterList();
                if (pl==null) {
                    return;
                }
            }
            else {
                return;
            }
            String def;
            int len;
            if (sie==null) {
            	String defaultValue = 
            			defaultValue(cu.getUnit(), dec.getType());
            	len = defaultValue.length();
            	if (decNode instanceof Tree.MethodDeclaration) {
            		def = " => " + defaultValue;
            	}
            	else {
            		def = " = " + defaultValue;
            	}
            }
            else {
                len = 0;
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
            if (params!=null) {
                def = " = " + params + def;
            }
            String param = (pl.getParameters().isEmpty() ? "" : ", ") + 
                    dec.getName() + def;
            Integer offset = pl.getStopIndex();
            change.addEdit(new InsertEdit(offset, param));
            Tree.Type type = decNode.getType();
            int shift=0;
            Type paramType;
            if (type instanceof Tree.LocalModifier) {
                Integer typeOffset = type.getStartIndex();
                paramType = type.getTypeModel();
                String explicitType;
                if (paramType==null) {
                    explicitType = "Object";
                    paramType = type.getUnit().getObjectDeclaration().getType();
                }
                else {
                    explicitType = paramType.asString();
                    HashSet<Declaration> decs = new HashSet<Declaration>();
                    importType(decs, paramType, cu);
                    shift = applyImports(change, decs, cu, doc);
                }
                change.addEdit(new ReplaceEdit(typeOffset, type.getText().length(), 
                        explicitType));
            }
            else {
                paramType = type.getTypeModel();
            }
            int exitPos = node.getStopIndex()+1;
            proposals.add(new AddParameterProposal(dec, container.getDeclarationModel(), 
                    paramType, offset+param.length()+shift-len, len, change, exitPos, editor));
        }
    }

	static void addParameterProposals(Collection<ICompletionProposal> proposals,
			IFile file, Tree.CompilationUnit cu, Node node, CeylonEditor editor) {
		if (node instanceof Tree.AttributeDeclaration) {
	        Tree.AttributeDeclaration attDecNode = (Tree.AttributeDeclaration) node;
	        Tree.SpecifierOrInitializerExpression sie = 
	                attDecNode.getSpecifierOrInitializerExpression();
	        if (!(sie instanceof Tree.LazySpecifierExpression)) {
	            addParameterProposal(cu, proposals, file, attDecNode, sie, node, editor);
	        }
	    }
	    if (node instanceof Tree.MethodDeclaration) {
	        Tree.MethodDeclaration methDecNode = (Tree.MethodDeclaration) node;
	        Tree.SpecifierExpression sie = methDecNode.getSpecifierExpression();
	        addParameterProposal(cu, proposals, file, methDecNode, sie, node, editor);
	    }
	}
    
}