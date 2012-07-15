package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;

import java.util.Collection;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.eclipse.code.editor.Util;

class SplitDeclarationProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    SplitDeclarationProposal(Declaration dec, int offset, IFile file, TextChange change) {
        super("Split declaration of '" + dec.getName() + "'", change, 10, CORRECTION);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }

    static void addSplitDeclarationProposal(IDocument doc, Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.AttributeDeclaration decNode) {
        Value dec = decNode.getDeclarationModel();
        TextChange change = new DocumentChange("Split Declaration", doc);
        change.setEdit(new MultiTextEdit());
        Integer offset = decNode.getIdentifier().getStopIndex()+1;
        change.addEdit(new InsertEdit(offset, ";\n" + getIndent(decNode, doc) + dec.getName()));
        Type type = decNode.getType();
        if (type instanceof Tree.LocalModifier) {
            Integer typeOffset = type.getStartIndex();
            String explicitType = SpecifyTypeProposal.inferType(cu, type);
            change.addEdit(new ReplaceEdit(typeOffset, type.getText().length(), explicitType));
        }
        proposals.add(new SplitDeclarationProposal(dec, offset, file, change));
    }
    
}