package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;

class AddAnnotionProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    AddAnnotionProposal(Declaration dec, String annotation,
            int offset, IFile file, TextFileChange change) {
        super("Make '" + dec.getName() + "' " + annotation +
            (dec.getContainer() instanceof TypeDeclaration ?
                    "in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'" : ""), 
                    change, 10, CORRECTION);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, 0);
    }

    static void addAddAnnotationProposal(Node node, String annotation, String desc, ProblemLocation problem, 
            Declaration dec, Collection<ICompletionProposal> proposals, IProject project) {
        if (dec!=null) {
            for (PhasedUnit unit: CeylonBuilder.getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    //TODO: "object" declarations?
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(dec);
                    unit.getCompilationUnit().visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    if (decNode!=null) {
                        IFile file = CeylonBuilder.getFile(unit);
                        TextFileChange change = new TextFileChange(desc, file);
                        change.setEdit(new MultiTextEdit());
                        Integer offset = decNode.getStartIndex();
                        change.addEdit(new InsertEdit(offset, annotation));
                        if (decNode instanceof Tree.TypedDeclaration) {
                            Tree.Type type = ((Tree.TypedDeclaration) decNode).getType();
                            if (type instanceof Tree.FunctionModifier 
                                    || type instanceof Tree.ValueModifier) {
                                String explicitType = type.getTypeModel().getProducedTypeName();
                                change.addEdit(new ReplaceEdit(type.getStartIndex(), type.getText().length(), 
                                        explicitType));
                            }
                        }
                        proposals.add(new AddAnnotionProposal(dec, annotation, offset, file, change));
                    }
                    break;
                }
            }
        }
    }
    
}