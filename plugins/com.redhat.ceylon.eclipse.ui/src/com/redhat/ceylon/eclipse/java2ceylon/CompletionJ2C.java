package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import java.util.Map;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.complete.EclipseCompletionProcessor;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.common.completion.completionManager_;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Scope;

public interface CompletionJ2C {

    completionManager_ dummyCompletionManager();

    EclipseCompletionProcessor newCompletionProcessor(CeylonEditor editor);

    Map<String, DeclarationWithProximity> getProposals(Node node, Scope scope,
            Tree.CompilationUnit rootNode);

}