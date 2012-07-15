package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
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
                change, 10, CORRECTION);
        this.offset = offset;
        this.length = type.length();
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addChangeTypeProposal(Tree.Type type, ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, TypedDeclaration typedDec, 
            ProducedType newType, IFile file) {
        TextFileChange change =  new TextFileChange("Change Type", file);
        String typeName = newType.getProducedTypeName();
        int offset = type.getStartIndex();
        int length = type.getStopIndex()-offset+1;
        change.setEdit(new ReplaceEdit(offset, length, typeName));
        proposals.add(new ChangeTypeProposal(problem, file, typedDec.getName(), 
                typeName, offset, change));
    }
    
}
