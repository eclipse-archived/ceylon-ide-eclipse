package com.redhat.ceylon.eclipse.java2ceylon;

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.complete.EclipseCompletionProcessor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.ide.common.completion.IdeCompletionManager;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Scope;

public interface CompletionJ2C {

    IdeCompletionManager<ICompletionProposal> dummyCompletionManager();

    EclipseCompletionProcessor newCompletionProcessor(CeylonEditor editor);

    Map<String, DeclarationWithProximity> getProposals(Node node, Scope scope,
            Tree.CompilationUnit rootNode);

}