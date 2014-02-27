package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.overloads;
import static com.redhat.ceylon.eclipse.code.complete.RefinementCompletionProposal.FORMAL_REFINEMENT;
import static com.redhat.ceylon.eclipse.code.complete.RefinementCompletionProposal.getRefinedProducedReference;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;

class ImplementFormalAndAmbiguouslyInheritedMembersProposal extends CorrectionProposal {

    private final int offset;
    private final IFile file;
    private final Set<String> refinementsNames;

    ImplementFormalAndAmbiguouslyInheritedMembersProposal(String name, Set<String> refinementsNames, int offset, IFile file, TextFileChange change) {
        super(name, change, FORMAL_REFINEMENT);
        this.offset = offset;
        this.file = file;
        this.refinementsNames = refinementsNames;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        EditorUtil.gotoLocation(file, offset);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImplementFormalAndAmbiguouslyInheritedMembersProposal) {
            return refinementsNames.equals(((ImplementFormalAndAmbiguouslyInheritedMembersProposal) obj).refinementsNames);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return refinementsNames.hashCode();
    }
    
    static void addImplementFormalAndAmbiguouslyInheritedMembersProposal(Tree.CompilationUnit cu, Node node, 
            Collection<ICompletionProposal> proposals, IFile file, IDocument doc) {
        Tree.ClassBody body = null;
        TypeDeclaration td = null;
        
        if (node instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition def = (Tree.ClassDefinition) node;
            body = def.getClassBody();
            td = def.getDeclarationModel();
        }
        else if (node instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition def = (Tree.ObjectDefinition) node;
            body = def.getClassBody();
            td = def.getAnonymousClass();
        }

        if (body == null || td == null) {
            return;
        }
        
        List<Tree.Statement> statements = body.getStatements();
        int offset;
        String indent;
        String bodyIndent = getIndent(body, doc);
        String delim = getDefaultLineDelimiter(doc);
        if (statements.isEmpty()) {
            indent = delim + bodyIndent + getDefaultIndent();
            offset = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = statements.get(statements.size()-1);
            indent = delim + getIndent(statement, doc);
            offset = statement.getStopIndex()+1;
        }
        
        StringBuilder result = new StringBuilder(delim);
        Set<Declaration> already = new HashSet<Declaration>();
        
        Set<String> formalDeclNames = new HashSet<String>();
        ClassOrInterface ci = (ClassOrInterface) node.getScope();
        Collection<DeclarationWithProximity> members = td
                .getMatchingMemberDeclarations(ci, "", 0).values();
        for (DeclarationWithProximity dwp: members) {
            Declaration dec = dwp.getDeclaration();
            for (Declaration d: overloads(dec)) {
                if (d.isFormal() && ci.isInheritedFromSupertype(d)) {
                    formalDeclNames.add(d.getName());
                    ProducedReference pr = getRefinedProducedReference(ci, d);
                    result.append(indent)
                        .append(getRefinementTextFor(d, pr, node.getUnit(), 
                                false, ci, indent, true))
                        .append(indent);
                    importSignatureTypes(d, cu, already);
                }
            }
        }
        
        Set<String> ambiguouslyDeclNames = new HashSet<String>();
        for (TypeDeclaration superType : td.getSuperTypeDeclarations()) {
            for (Declaration m : superType.getMembers()) {
                if (m.isShared()) {
                    Declaration r = td.getMember(m.getName(), null, false);
                    if (r==null || 
                            !r.refines(m) && 
                            !r.getContainer().equals(td) && 
                            !ambiguouslyDeclNames.contains(m.getName())) {
                        ambiguouslyDeclNames.add(m.getName());
                        ProducedReference pr = getRefinedProducedReference(ci, m);
                        result.append(indent)
                            .append(getRefinementTextFor(m, pr, node.getUnit(), 
                                    false, null, indent, true))
                            .append(indent);
                        importSignatureTypes(m, cu, already);
                    }
                }
            }
        }
        
        try {
            if (doc.getChar(offset)=='}' && result.length()>0) {
                result.append(delim).append(bodyIndent);
            }
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        String name;
        if (!formalDeclNames.isEmpty() && !ambiguouslyDeclNames.isEmpty()) {
            name = "Refine formal and ambiguously inherited members";
        }
        else if (formalDeclNames.isEmpty() && !ambiguouslyDeclNames.isEmpty()) {
            name = "Refine ambiguously inherited members";
        }
        else if (!formalDeclNames.isEmpty() && ambiguouslyDeclNames.isEmpty()) {
            name = "Refine formal members";
        } else {
            return;
        }
        
        Set<String> refinementsNames = new HashSet<String>();
        refinementsNames.addAll(formalDeclNames);
        refinementsNames.addAll(ambiguouslyDeclNames);
        
        TextFileChange change = new TextFileChange(name, file);
        change.setEdit(new MultiTextEdit());
        applyImports(change, already, cu, doc);
        change.addEdit(new InsertEdit(offset, result.toString()));
        ImplementFormalAndAmbiguouslyInheritedMembersProposal proposal = new ImplementFormalAndAmbiguouslyInheritedMembersProposal(name, refinementsNames, offset, file, change);
        if (!proposals.contains(proposal)) {
            proposals.add(proposal);
        }
    }
    
}