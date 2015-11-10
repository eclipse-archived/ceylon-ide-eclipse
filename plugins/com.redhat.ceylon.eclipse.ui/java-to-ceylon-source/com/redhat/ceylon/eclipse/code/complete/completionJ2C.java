package com.redhat.ceylon.eclipse.code.complete;

import java.util.Map;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Scope;

public class completionJ2C {
    public static EclipseCompletionProcessor newCompletionProcessor(CeylonEditor editor) {
        return new EclipseCompletionManager(editor);
    }
    
    public static Map<String,DeclarationWithProximity> getProposals(Node node, Scope scope, Tree.CompilationUnit rootNode) {
        return dummyInstance_.get_().getProposals(node, scope, "", false, rootNode);
    }
}
