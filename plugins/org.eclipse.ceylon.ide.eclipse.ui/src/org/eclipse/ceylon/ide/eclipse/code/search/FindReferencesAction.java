package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.common.util.FindReferencesVisitor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;

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
                    new FindReferencesVisitor(referencedDeclaration) {
                @Override
                public boolean isRefinedDeclarationReference(Declaration ref) {
                    return super.isRefinedDeclarationReference(ref) ||
                            ref instanceof FunctionOrValue 
                            && ((FunctionOrValue)ref).isShortcutRefinement()
                            && ref.getRefinedDeclaration().equals(getDeclaration());
                }
            };
            cu.visit(frv);
            FindDocLinkReferencesVisitor fdlrv =
                    new FindDocLinkReferencesVisitor(referencedDeclaration);
            cu.visit(fdlrv);
            Set<Node> result = frv.getReferenceNodeSet();
            result.addAll(fdlrv.getLinks());
            return result;
        }
        
        @Override
        int limitTo() {
            return IJavaSearchConstants.REFERENCES;
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
    
    public FindReferencesAction(IEditorPart editor, Referenceable dec) {
        super("Find References", editor, dec);
        setActionDefinitionId(PLUGIN_ID + ".action.findReferences");
    }
    
    @Override
    boolean isValidSelection() {
        return declaration!=null &&
                declaration.getNameAsString()!=null;
    }

    @Override
    public FindSearchQuery createSearchQuery() {
        return new Query(declaration, project);
    }

}