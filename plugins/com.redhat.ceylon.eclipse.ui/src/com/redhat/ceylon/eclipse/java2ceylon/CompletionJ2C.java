package com.redhat.ceylon.eclipse.java2ceylon;

import java.util.Map;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.complete.EclipseCompletionProcessor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.ide.common.completion.completionManager_;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Scope;

public interface CompletionJ2C {

    completionManager_ dummyCompletionManager();

    EclipseCompletionProcessor newCompletionProcessor(CeylonEditor editor);

    Map<String, DeclarationWithProximity> getProposals(Node node, Scope scope,
            Tree.CompilationUnit rootNode);

}