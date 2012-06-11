package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.getProposals;
import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.imp.quickfix.CeylonQuickFixAssistant.getIndent;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;

public class RefineFormalMembersHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        refineFormalMembers((CeylonEditor) getCurrentEditor());
        return null;
    }

    public static void refineFormalMembers(CeylonEditor editor) throws ExecutionException {
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu==null) return;
        Node node = getSelectedNode(editor);
        TextFileChange change = new TextFileChange("Refine Formal Members", 
                Util.getFile(editor.getEditorInput()));
        IDocument doc;
        try {
            doc = change.getCurrentDocument(null);
        }
        catch (CoreException e) {
            throw new RuntimeException(e);
        }
        //TODO: copy/pasted from CeylonQuickFixAssisitant
        Tree.Body body;
        int offset;
        if (node instanceof Tree.ClassDefinition) {
            body = ((Tree.ClassDefinition) node).getClassBody();
            offset = -1;
        }
        if (node instanceof Tree.InterfaceDefinition) {
            body = ((Tree.InterfaceDefinition) node).getInterfaceBody();
            offset = -1;
        }
        if (node instanceof Tree.ObjectDefinition) {
            body = ((Tree.ObjectDefinition) node).getClassBody();
            offset = -1;
        }
        else if (node instanceof Tree.ClassBody || 
                node instanceof Tree.InterfaceBody) {
            body = (Tree.Body) node;
            offset = editor.getSelectedRegion().getOffset();
        }
        else {
            //TODO run a visitor to find the containing body!
            return;//TODO popup error dialog
        }
        List<Statement> statements = body.getStatements();
        String indent;
        String indentAfter;
        if (statements.isEmpty()) {
            indentAfter = "\n" + getIndent(body, doc);
            indent = indentAfter + getDefaultIndent();
            if (offset<0) offset = body.getStartIndex()+1;
        }
        else {
            Statement statement = statements.get(statements.size()-1);
            indent = "\n" + getIndent(statement, doc);
            indentAfter = "";
            if (offset<0) offset = statement.getStopIndex()+1;
        }
        StringBuilder result = new StringBuilder();
        for (DeclarationWithProximity dwp: getProposals(node, cu).values()) {
            Declaration d = dwp.getDeclaration();
            if (d.isFormal() && 
                    ((ClassOrInterface) node.getScope()).isInheritedFromSupertype(d)) {
            	ProducedReference pr = CeylonContentProposer.getRefinedProducedReference(node, d);
                result.append(indent).append(getRefinementTextFor(d, pr, indent)).append(indentAfter);
            }
        }
        change.setEdit(new InsertEdit(offset, result.toString()));
        change.initializeValidationData(null);
        try {
            ResourcesPlugin.getWorkspace().run(new PerformChangeOperation(change), 
                    new NullProgressMonitor());
        }
        catch (CoreException ce) {
            throw new ExecutionException("Error cleaning imports", ce);
        }
    }
    
    @Override
    protected boolean isEnabled(CeylonEditor editor) {
        return canRefine(editor);
    }

    public static boolean canRefine(CeylonEditor editor) {
        Node node = getSelectedNode(editor);
        return node instanceof Tree.ClassBody ||
                node instanceof Tree.InterfaceBody ||
                node instanceof Tree.ClassDefinition ||
                node instanceof Tree.InterfaceDefinition ||
                node instanceof Tree.ObjectDefinition;
    }
}
