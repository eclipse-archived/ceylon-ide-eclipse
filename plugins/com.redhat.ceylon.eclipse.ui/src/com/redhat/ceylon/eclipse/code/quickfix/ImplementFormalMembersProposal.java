package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.FORMAL_REFINEMENT;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getProposals;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getRefinedProducedReference;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importSignatureTypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;

class ImplementFormalMembersProposal extends ChangeCorrectionProposal {

    final int offset;
    final IFile file;

    ImplementFormalMembersProposal(int offset, IFile file, 
            TextFileChange change) {
        super("Refine formal members", 
                change, 10, FORMAL_REFINEMENT);
        this.offset = offset;
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }
    
    static void addImplementFormalMembersProposal(Tree.CompilationUnit cu, Node node, 
            Collection<ICompletionProposal> proposals, IFile file, IDocument doc) {
        TextFileChange change = new TextFileChange("Refine Formal Members", file);
        change.setEdit(new MultiTextEdit());
        Tree.ClassBody body;
        if (node instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition def = (Tree.ClassDefinition) node;
            body = def.getClassBody();
        }
        else if (node instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition def = (Tree.ObjectDefinition) node;
            body = def.getClassBody();
        }
        else {
            return;
        }
        List<Tree.Statement> statements = body.getStatements();
        int offset;
        String indent;
        String indentAfter;
        if (statements.isEmpty()) {
            indentAfter = "\n" + getIndent(body, doc);
            indent = indentAfter + getDefaultIndent();
            offset = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = statements.get(statements.size()-1);
            indent = "\n" + getIndent(statement, doc);
            indentAfter = "";
            offset = statement.getStopIndex()+1;
        }
        StringBuilder result = new StringBuilder();
        Set<Declaration> already = new HashSet<Declaration>();
        for (DeclarationWithProximity dwp: getProposals(node, cu).values()) {
            Declaration d = dwp.getDeclaration();
            if (d.isFormal() && 
                    ((ClassOrInterface) node.getScope()).isInheritedFromSupertype(d)) {
            	ProducedReference pr = getRefinedProducedReference(node, d);
                result.append(indent).append(getRefinementTextFor(d, pr, indent)).append(indentAfter);
                importSignatureTypes(d, cu, change, already);
            }
        }
        change.addEdit(new InsertEdit(offset, result.toString()));
        proposals.add(new ImplementFormalMembersProposal(offset, file, change));
    }
}