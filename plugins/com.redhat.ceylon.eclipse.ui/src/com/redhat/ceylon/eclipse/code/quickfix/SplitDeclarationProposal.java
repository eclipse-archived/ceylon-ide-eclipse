package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;
import static com.redhat.ceylon.eclipse.code.quickfix.SpecifyTypeProposal.inferType;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
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
        if (dec==null) return;
        Tree.Identifier id = decNode.getIdentifier();
        if (id==null || id.getToken()==null) return;
        TextChange change = new DocumentChange("Split Declaration", doc);
        change.setEdit(new MultiTextEdit());
        Integer offset = id.getStopIndex()+1;
        change.addEdit(new InsertEdit(offset, ";\n" + getIndent(decNode, doc) + dec.getName()));
        Type type = decNode.getType();
		int il;
        if (type instanceof Tree.LocalModifier) {
            Integer typeOffset = type.getStartIndex();
            ProducedType infType = inferType(cu, type);
			String explicitType;
			if (infType==null) {
				explicitType = "Object";
				il=0;
			}
			else {
				explicitType = infType.getProducedTypeName();
				HashSet<Declaration> decs = new HashSet<Declaration>();
				importType(decs, infType, cu);
				il=applyImports(change, decs, cu);
			}
            change.addEdit(new ReplaceEdit(typeOffset, type.getText().length(), explicitType));
        }
        else {
        	il=0;
        }
        proposals.add(new SplitDeclarationProposal(dec, offset+il, file, change));
    }
    
}