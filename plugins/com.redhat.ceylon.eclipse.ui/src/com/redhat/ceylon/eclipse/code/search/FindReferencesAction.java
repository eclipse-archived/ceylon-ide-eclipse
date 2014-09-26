package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;

public class FindReferencesAction extends AbstractFindAction {

    //TODO: copy/pasted from RenameRefactoring!
    private static class FindDocLinkReferencesVisitor extends Visitor {
        private Referenceable declaration;
        private List<Tree.DocLink> links = 
                new ArrayList<Tree.DocLink>();
        List<Tree.DocLink> getLinks() {
            return links;
        }
        FindDocLinkReferencesVisitor(Referenceable declaration) {
            this.declaration = declaration;
        }
        @Override
        public void visit(Tree.DocLink that) {
            //TODO: what about package/module doc links!!
            if (that.getBase()!=null) {
                if (that.getBase().equals(declaration)) {
                    links.add(that);
                }
                else if (that.getQualified()!=null) {
                    if (that.getQualified().contains(declaration)) {
                        links.add(that);
                    }
                }
            }
        }
    }
    
    private static final class Query extends FindSearchQuery {
        private Query(Referenceable referencedDeclaration, IProject project) {
            super(referencedDeclaration, project);
        }

        @Override
        protected Set<Node> getNodes(Tree.CompilationUnit cu,
                Referenceable referencedDeclaration) {
            FindReferencesVisitor frv = 
                    new FindReferencesVisitor(referencedDeclaration);
            cu.visit(frv);
            FindDocLinkReferencesVisitor fdlrv =
                    new FindDocLinkReferencesVisitor(referencedDeclaration);
            cu.visit(fdlrv);
            Set<Node> result = frv.getNodes();
            result.addAll(fdlrv.getLinks());
            return result;
        }
        
        @Override
        int limitTo() {
            return IJavaSearchConstants.ALL_OCCURRENCES;
        }

        @Override
        protected String labelString() {
            return "references to";
        }
    }

    public FindReferencesAction() {
        super("Find References");
        setActionDefinitionId(PLUGIN_ID + ".action.findReferences");
    }
    
    public FindReferencesAction(CeylonSearchResultPage page, ISelection selection) {
        super("Find References", page, selection);
        setActionDefinitionId(PLUGIN_ID + ".action.findReferences");
    }
    
    public FindReferencesAction(IEditorPart editor) {
        super("Find References", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.findReferences");
    }
    
    public FindReferencesAction(IEditorPart editor, Declaration dec) {
        super("Find References", editor, dec);
        setActionDefinitionId(PLUGIN_ID + ".action.findReferences");
    }
    
    @Override
    boolean isValidSelection() {
        return declaration!=null;
    }

    @Override
    public FindSearchQuery createSearchQuery() {
        return new Query(declaration, project);
    }

}