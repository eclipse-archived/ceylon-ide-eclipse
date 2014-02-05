package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

class RemoveAnnotionProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    final Declaration dec;
    final String annotation;
    
    RemoveAnnotionProposal(Declaration dec, String annotation,
            int offset, IFile file, TextFileChange change) {
        super("Make '" + dec.getName() + "' non-" + annotation + " " +
            (dec.getContainer() instanceof TypeDeclaration ?
                    "in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'" : ""), 
                    change);
        this.offset=offset;
        this.file=file;
        this.dec = dec;
        this.annotation = annotation;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RemoveAnnotionProposal) {
            RemoveAnnotionProposal that = (RemoveAnnotionProposal) obj;
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

    static void addRemoveAnnotationProposal(Node node, String annotation, String desc,
            Declaration dec, Collection<ICompletionProposal> proposals, IProject project) {
        if (dec!=null && dec.getName()!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    //TODO: "object" declarations?
                    FindDeclarationNodeVisitor fdv = new FindDeclarationNodeVisitor(dec);
                    CeylonQuickFixAssistant.getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    if (decNode!=null) {
                        RemoveAnnotionProposal.addRemoveAnnotationProposal(annotation, desc, dec,
                                proposals, unit, decNode);
                    }
                    break;
                }
            }
        }
    }

    private static void addRemoveAnnotationProposal(String annotation,
            String desc, Declaration dec,
            Collection<ICompletionProposal> proposals, 
            PhasedUnit unit, Tree.Declaration decNode) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange(desc, file);
        change.setEdit(new MultiTextEdit());
        Integer offset = decNode.getStartIndex();
        for (Tree.Annotation a: decNode.getAnnotationList().getAnnotations()) {
            Identifier id = ((Tree.BaseMemberExpression)a.getPrimary()).getIdentifier();
            if (id!=null) {
                if (id.getText().equals(annotation)) {
                    boolean args = a.getPositionalArgumentList()!=null && 
                                a.getPositionalArgumentList().getToken()!=null ||
                            a.getNamedArgumentList()!=null;
                    change.addEdit(new DeleteEdit(a.getStartIndex(), 
                            a.getStopIndex()-a.getStartIndex()+1 + 
                                    (args?0:1))); //get rid of the trailing space
                }
            }
        }
        RemoveAnnotionProposal p = 
        		new RemoveAnnotionProposal(dec, annotation, offset, file, change);
        if (!proposals.contains(p)) {
            proposals.add(p);
        }
    }
    
    static void addRemoveAnnotationDecProposal(Collection<ICompletionProposal> proposals, 
            String annotation, IProject project, Node node) {
    	if (node instanceof Tree.Declaration) {
    		addRemoveAnnotationProposal(node, annotation, "Make Non" + annotation,  
    				((Tree.Declaration) node).getDeclarationModel(), 
    				proposals, project);
    	}
    }
    
}