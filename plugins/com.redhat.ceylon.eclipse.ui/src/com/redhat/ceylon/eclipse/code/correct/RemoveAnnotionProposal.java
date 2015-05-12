package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getRootNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;

class RemoveAnnotionProposal extends CorrectionProposal {
    
    private final Declaration dec;
    private final String annotation;
    
    RemoveAnnotionProposal(Declaration dec, String annotation,
            int offset, TextFileChange change) {
        super("Make '" + dec.getName() + "' non-" + annotation + " " +
            (dec.getContainer() instanceof TypeDeclaration ?
                    "in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'" : ""), 
                    change, new Region(offset, 0));
        this.dec = dec;
        this.annotation = annotation;
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
    
    static void addRemoveAnnotationProposal(Node node, String annotation,
            Collection<ICompletionProposal> proposals, IProject project) {
        Referenceable dec = Nodes.getReferencedDeclaration(node);
        if (dec instanceof Declaration) {
            addRemoveAnnotationProposal(node, annotation, 
                    "Make Non" + annotation,  
                    (Declaration) dec, proposals, project);
        }
    }

    static void addMakeContainerNonfinalProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            Scope container = 
                    ((Tree.Declaration) node).getDeclarationModel().getContainer();
            if (container instanceof Declaration) {
                dec = (Declaration) container;
            }
            else {
                return;
            }
        }
        else {
            dec = (Declaration) node.getScope();
        }
        addRemoveAnnotationProposal(node, 
                "final", "Make Nonfinal", 
                dec, proposals, project);
    }

    static void addRemoveAnnotationProposal(Node node, String annotation, String desc,
            Declaration dec, Collection<ICompletionProposal> proposals, IProject project) {
        if (dec!=null && dec.getName()!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    //TODO: "object" declarations?
                    FindDeclarationNodeVisitor fdv = 
                            new FindDeclarationNodeVisitor(dec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = 
                            (Tree.Declaration) fdv.getDeclarationNode();
                    if (decNode!=null) {
                        addRemoveAnnotationProposal(annotation, desc, dec,
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
                new RemoveAnnotionProposal(dec, annotation, offset, change);
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