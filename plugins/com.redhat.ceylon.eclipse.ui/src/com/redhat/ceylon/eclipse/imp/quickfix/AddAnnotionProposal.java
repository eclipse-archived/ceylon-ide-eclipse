package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnknownType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.editor.Util;

class AddAnnotionProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    final Declaration dec;
    final String annotation;
    
    AddAnnotionProposal(Declaration dec, String annotation,
            int offset, IFile file, TextFileChange change) {
        super("Make '" + dec.getName() + "' " + annotation +
            (dec.getContainer() instanceof TypeDeclaration ?
                    "in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'" : ""), 
                    change, 10, CORRECTION);
        this.offset=offset;
        this.file=file;
        this.dec = dec;
        this.annotation = annotation;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, 0);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddAnnotionProposal) {
            AddAnnotionProposal that = (AddAnnotionProposal) obj;
            return that.dec.equals(dec) && 
                    that.annotation.equals(annotation);
        }
        else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return dec.hashCode();
    }

    static void addAddAnnotationProposal(String annotation, String desc, 
            Declaration dec, Collection<ICompletionProposal> proposals, 
            PhasedUnit unit, Tree.Declaration decNode) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange(desc, file);
        change.setEdit(new MultiTextEdit());
        Integer offset = decNode.getStartIndex();
        change.addEdit(new InsertEdit(offset, annotation));
        if (decNode instanceof Tree.TypedDeclaration &&
                !(decNode instanceof Tree.ObjectDefinition)) {
            Tree.Type type = ((Tree.TypedDeclaration) decNode).getType();
            if (type instanceof Tree.FunctionModifier 
                    || type instanceof Tree.ValueModifier) {
                ProducedType it = type.getTypeModel();
                if (it!=null && !(it.getDeclaration() instanceof UnknownType)) {
                    String explicitType = it.getProducedTypeName();
                    change.addEdit(new ReplaceEdit(type.getStartIndex(), type.getText().length(), 
                            explicitType));
                }
            }
        }
        AddAnnotionProposal p = new AddAnnotionProposal(dec, annotation, offset, file, change);
        if (!proposals.contains(p)) {
            proposals.add(p);
        }
    }
    
}