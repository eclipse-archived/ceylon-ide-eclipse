package com.redhat.ceylon.eclipse.code.complete;

import java.util.Map;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.java2ceylon.CompletionJ2C;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Scope;

public class completionJ2C implements CompletionJ2C {
    /* (non-Javadoc)
     * @see com.redhat.ceylon.eclipse.code.complete.CompletionJ2C#newCompletionProcessor(com.redhat.ceylon.eclipse.code.editor.CeylonEditor)
     */
    @Override
    public EclipseCompletionProcessor newCompletionProcessor(CeylonEditor editor) {
        return new EclipseCompletionManager(editor);
    }
    
    /* (non-Javadoc)
     * @see com.redhat.ceylon.eclipse.code.complete.CompletionJ2C#getProposals(com.redhat.ceylon.compiler.typechecker.tree.Node, com.redhat.ceylon.model.typechecker.model.Scope, com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit)
     */
    @Override
    public Map<String,DeclarationWithProximity> getProposals(Node node, Scope scope, Tree.CompilationUnit rootNode) {
        return dummyInstance_.get_().getProposals(node, scope, "", false, rootNode);
    }
}
