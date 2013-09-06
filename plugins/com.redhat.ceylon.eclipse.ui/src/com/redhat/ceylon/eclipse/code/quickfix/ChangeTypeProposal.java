package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;

class ChangeTypeProposal extends ChangeCorrectionProposal {

    final int offset;
    final int length;
    final IFile file;
    
    ChangeTypeProposal(ProblemLocation problem, IFile file, 
            String name, String type, int offset,
            TextFileChange change) {
        super("Change type of '"+ name + "' to '" + type + "'", 
                change);
        this.offset = offset;
        this.length = type.length();
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addChangeTypeProposal(Node node, ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, Declaration dec, 
            ProducedType newType, IFile file, Tree.CompilationUnit cu) {
        // better safe than throwing
        if(node.getStartIndex() == null || node.getStopIndex() == null)
            return;
        TextFileChange change =  new TextFileChange("Change Type", file);
        change.setEdit(new MultiTextEdit());
        String typeName = newType.getProducedTypeName();
        int offset = node.getStartIndex();
        int length = node.getStopIndex()-offset+1;
        HashSet<Declaration> decs = new HashSet<Declaration>();
		importType(decs, newType, cu);
		int il=applyImports(change, decs, cu);
        change.addEdit(new ReplaceEdit(offset, length, typeName));
        proposals.add(new ChangeTypeProposal(problem, file, dec.getName(), 
                typeName, offset+il, change));
    }
    
}
