package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.defaultValue;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importProposals;
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

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Type;

class AddParameterProposal extends InitializerProposal {
    
	private AddParameterProposal(String desc,
	        Declaration d, Declaration dec,
	        Type type, int offset, int len,
	        TextChange change,
	        int exitPos) {
        super(desc, change, dec, type,
                new Region(offset, len),
                ADD_CORR, exitPos);
    }
	
    private static void addParameterProposal(
            Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals,
            IFile file, Tree.TypedDeclaration decNode,
            Tree.SpecifierOrInitializerExpression sie,
            Node node) {
        FunctionOrValue dec =
                (FunctionOrValue)
                    decNode.getDeclarationModel();
        if (dec==null) return;
        if (dec.getInitializerParameter()==null &&
                !dec.isFormal() &&
                dec.getContainer() instanceof Functional) {
            TextChange change =
                    new TextFileChange("Add Parameter",
                            file);
            change.setEdit(new MultiTextEdit());
            IDocument doc = EditorUtil.getDocument(change);
            //TODO: copy/pasted from SplitDeclarationProposal 
            String params = null;
            if (decNode instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration md =
                        (Tree.MethodDeclaration) decNode;
                List<ParameterList> pls =
                        md.getParameterLists();
                if (pls.isEmpty()) {
                    return;
                } 
                else {
                    int start = pls.get(0).getStartIndex();
                    int end = pls.get(pls.size()-1).getEndIndex();
                    try {
                        params = doc.get(start, end - start);
                    } 
                    catch (BadLocationException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            Tree.Declaration container =
                    findDeclarationWithBody(rootNode,
                            decNode);
            Tree.ParameterList pl;
            if (container instanceof Tree.ClassDefinition) {
                Tree.ClassDefinition cd =
                        (Tree.ClassDefinition) container;
                pl = cd.getParameterList();
                if (pl==null) {
                    return;
                }
            }
            else if (container instanceof Tree.MethodDefinition) {
                Tree.MethodDefinition md =
                        (Tree.MethodDefinition) container;
                List<Tree.ParameterList> pls =
                        md.getParameterLists();
                if (pls.isEmpty()) {
                    return;
                }
                pl = pls.get(0);
            }
            else if (container instanceof Tree.Constructor) {
                Tree.Constructor cd =
                        (Tree.Constructor) container;
                pl = cd.getParameterList();
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
            			defaultValue(rootNode.getUnit(),
            			        dec.getType());
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
                			sie.getDistance());
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
                change.addEdit(new DeleteEdit(start,
                        sie.getEndIndex()-start));
            }
            if (params!=null) {
                def = " = " + params + def;
            }
            String param =
                    (pl.getParameters().isEmpty() ? "" : ", ") +
                    dec.getName() + def;
            Integer offset = pl.getEndIndex()-1;
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
                    paramType = type.getUnit().getObjectType();
                }
                else {
                    explicitType = paramType.asString();
                    HashSet<Declaration> decs =
                            new HashSet<Declaration>();
                    importProposals().importType(decs, paramType, rootNode);
                    shift = (int) importProposals().applyImports(change, decs, rootNode, doc);
                }
                change.addEdit(new ReplaceEdit(typeOffset,
                        type.getText().length(),
                        explicitType));
            }
            else {
                paramType = type.getTypeModel();
            }
            int exitPos = node.getEndIndex();
            String desc =
                    "Add '" + dec.getName() +
                    "' to parameter list";
            Declaration cont =
                    container.getDeclarationModel();
            if (cont.getName()!=null) {
                desc += " of '" + cont.getName() + "'";
            }
            proposals.add(new AddParameterProposal(desc,
                    dec, cont, paramType,
                    offset+param.length()+shift-len, len,
                    change, exitPos));
        }
    }

	static void addParameterProposals(
	        Collection<ICompletionProposal> proposals,
			IFile file, Tree.CompilationUnit cu, Node node) {
		if (node instanceof Tree.AttributeDeclaration) {
	        Tree.AttributeDeclaration attDecNode =
	                (Tree.AttributeDeclaration) node;
	        Tree.SpecifierOrInitializerExpression sie = 
	                attDecNode.getSpecifierOrInitializerExpression();
	        if (!(sie instanceof Tree.LazySpecifierExpression)) {
	            addParameterProposal(cu, proposals, file,
	                    attDecNode, sie, node);
	        }
	    }
	    if (node instanceof Tree.MethodDeclaration) {
	        Tree.MethodDeclaration methDecNode =
	                (Tree.MethodDeclaration) node;
	        Tree.SpecifierExpression sie =
	                methDecNode.getSpecifierExpression();
	        addParameterProposal(cu, proposals, file,
	                methDecNode, sie, node);
	    }
	}
    
}