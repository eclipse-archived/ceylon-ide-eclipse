package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class ConvertToBlockProposal extends CorrectionProposal {
    
    ConvertToBlockProposal(String desc, int offset, TextChange change) {
        super(desc, change, new Region(offset, 0));
    }
    
    static void addConvertToBlockProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, IFile file,
            Node decNode) {
        TextChange change = new TextFileChange("Convert to Block", file);
        change.setEdit(new MultiTextEdit());
        int offset;
        int len;
        String semi;
        boolean isVoid;
        String addedKeyword = null;
        String desc = "Convert => to block";
        if (decNode instanceof Tree.MethodDeclaration) {
            Tree.MethodDeclaration md = (Tree.MethodDeclaration) decNode;
            Method dm = md.getDeclarationModel();
            if (dm==null || dm.isParameter()) return;
            isVoid = dm.isDeclaredVoid();
            List<Tree.ParameterList> pls = md.getParameterLists();
            if (pls.isEmpty()) return;
            offset = pls.get(pls.size()-1).getStopIndex()+1;
            len = md.getSpecifierExpression().getExpression().getStartIndex() - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.AttributeDeclaration) {
            Tree.AttributeDeclaration ad = (Tree.AttributeDeclaration) decNode;
            Value dm = ad.getDeclarationModel();
            if (dm==null || dm.isParameter()) return;
            isVoid = false;
            offset = ad.getIdentifier().getStopIndex()+1;
            len = ad.getSpecifierOrInitializerExpression().getExpression().getStartIndex() - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.AttributeSetterDefinition) {
            Tree.AttributeSetterDefinition asd = (Tree.AttributeSetterDefinition) decNode;
            isVoid = true;
            offset = asd.getIdentifier().getStopIndex()+1;
            len = asd.getSpecifierExpression().getExpression().getStartIndex() - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.MethodArgument) {
            Tree.MethodArgument ma = (Tree.MethodArgument) decNode;
            Method dm = ma.getDeclarationModel();
            if (dm==null) return;
            isVoid = dm.isDeclaredVoid();
            if (ma.getType().getToken()==null) {
                addedKeyword = "function ";
            }
            List<Tree.ParameterList> pls = ma.getParameterLists();
            if (pls.isEmpty()) return;
            offset = pls.get(pls.size()-1).getStopIndex()+1;
            len = ma.getSpecifierExpression().getExpression().getStartIndex() - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.AttributeArgument) {
            Tree.AttributeArgument aa = (Tree.AttributeArgument) decNode;
            isVoid = false;            
            if (aa.getType().getToken()==null) {
                addedKeyword = "value ";
            }
            offset = aa.getIdentifier().getStopIndex()+1;
            len = aa.getSpecifierExpression().getExpression().getStartIndex() - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.FunctionArgument) {
            Tree.FunctionArgument fun = (Tree.FunctionArgument) decNode;
            Method dm = fun.getDeclarationModel();
            if (dm==null) return;
            isVoid = dm.isDeclaredVoid();
            List<Tree.ParameterList> pls = fun.getParameterLists();
            if (pls.isEmpty()) return;
            offset = pls.get(pls.size()-1).getStopIndex()+1;
            len = fun.getExpression().getStartIndex() - offset;
            semi = ";";
            desc = "Convert anonymous function => to block";
        }
        else {
            return;
        }
        if (addedKeyword!=null) {
            change.addEdit(new InsertEdit(decNode.getStartIndex(), addedKeyword));
        }
        change.addEdit(new ReplaceEdit(offset, len, " {" + (isVoid?"":" return") + " "));
        change.addEdit(new InsertEdit(decNode.getStopIndex()+1, semi + " }"));
        proposals.add(new ConvertToBlockProposal(desc, offset + 3, change));
    }

}