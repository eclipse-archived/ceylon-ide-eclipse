package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.getProposals;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.overloads;
import static com.redhat.ceylon.eclipse.code.complete.RefinementCompletionProposal.FORMAL_REFINEMENT;
import static com.redhat.ceylon.eclipse.code.complete.RefinementCompletionProposal.getRefinedProducedReference;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;

class RefineFormalMembersProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final Tree.CompilationUnit rootNode;
    private final String description;
    private Node node;
    
    public RefineFormalMembersProposal(Node node, 
            Tree.CompilationUnit rootNode,
            String description) {
        this.node = node;
        this.description = description;
        this.rootNode = rootNode;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return FORMAL_REFINEMENT;
    }

    @Override
    public String getDisplayString() {
        return description;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), false);
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        //TODO: list the members that will be refined!
        return null;
    }

    @Override
    public void apply(IDocument doc) {
        try {
            refineFormalMembers(doc);
        } 
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    private void refineFormalMembers(IDocument document) 
            throws ExecutionException {
        if (rootNode==null) return;
        TextChange change = 
                new DocumentChange("Refine Members", document);
        change.setEdit(new MultiTextEdit());
        //TODO: copy/pasted from CeylonQuickFixAssistant
        Tree.Body body;
        int offset;
        if (node instanceof Tree.ClassDefinition) {
            body = ((Tree.ClassDefinition) node).getClassBody();
            offset = -1;
        }
        else if (node instanceof Tree.InterfaceDefinition) {
            body = ((Tree.InterfaceDefinition) node).getInterfaceBody();
            offset = -1;
        }
        else if (node instanceof Tree.ObjectDefinition) {
            body = ((Tree.ObjectDefinition) node).getClassBody();
            offset = -1;
        }
        else if (node instanceof Tree.ClassBody || 
                node instanceof Tree.InterfaceBody) {
            body = (Tree.Body) node;
            IEditorPart editor = EditorUtil.getCurrentEditor();
            if (editor instanceof CeylonEditor) {
                offset = ((CeylonEditor) editor).getSelection().getOffset();
            }
            else {
                offset = -1;
            }
        }
        else {
            //TODO run a visitor to find the containing body!
            return; //TODO popup error dialog
        }
        boolean isInterface = body instanceof Tree.InterfaceBody;
        List<Statement> statements = body.getStatements();
        String indent;
        String bodyIndent = getIndent(body, document);
        String delim = getDefaultLineDelimiter(document);
        if (statements.isEmpty()) {
            indent = delim + bodyIndent + getDefaultIndent();
            if (offset<0) offset = body.getStartIndex()+1;
        }
        else {
            Statement statement = statements.get(statements.size()-1);
            indent = delim + getIndent(statement, document);
            if (offset<0) offset = statement.getStopIndex()+1;
        }
        StringBuilder result = new StringBuilder();
        Set<Declaration> already = new HashSet<Declaration>();
        ClassOrInterface ci = (ClassOrInterface) node.getScope();
        Unit unit = node.getUnit();
        Set<String> ambiguousNames = new HashSet<String>();
        for (DeclarationWithProximity dwp: 
                getProposals(node, ci, rootNode).values()) {
            Declaration dec = dwp.getDeclaration();
            for (Declaration d: overloads(dec)) {
                try {
                    if (d.isFormal() && 
                            ci.isInheritedFromSupertype(d)) {
                        appendRefinementText(isInterface, indent, result, ci, unit, d);
                        importSignatureTypes(d, rootNode, already);
                        ambiguousNames.add(d.getName());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (TypeDeclaration superType: ci.getSupertypeDeclarations()) {
            for (Declaration m: superType.getMembers()) {
                try {
                    if (m.isShared()) {
                        Declaration r = ci.getMember(m.getName(), null, false);
                        if ((r==null || 
                                !r.refines(m) && 
                                !r.getContainer().equals(ci)) && 
                                ambiguousNames.add(m.getName())) {
                            appendRefinementText(isInterface, indent, result, ci, unit, m);
                            importSignatureTypes(m, rootNode, already);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {
            if (document.getChar(offset)=='}' && result.length()>0) {
                result.append(delim).append(bodyIndent);
            }
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        applyImports(change, already, rootNode, document);
        change.addEdit(new InsertEdit(offset, result.toString()));
        change.initializeValidationData(null);
        try {
            getWorkspace().run(new PerformChangeOperation(change), 
                    new NullProgressMonitor());
        }
        catch (CoreException ce) {
            throw new ExecutionException("Error cleaning imports", ce);
        }
    }

    private void appendRefinementText(boolean isInterface, String indent,
            StringBuilder result, ClassOrInterface ci, Unit unit, 
            Declaration member) {
        ProducedReference pr = getRefinedProducedReference(ci, member);
        String rtext = getRefinementTextFor(member, pr, unit, 
                isInterface, ci, indent, true);
        result.append(indent).append(rtext).append(indent);
    }
    
    static void addRefineFormalMembersProposal(Collection<ICompletionProposal> proposals, 
            Node node, Tree.CompilationUnit rootNode, boolean ambiguousError) {
        for (ICompletionProposal p: proposals) {
            if (p instanceof RefineFormalMembersProposal) {
                return;
            }
        }
        if (node instanceof Tree.ClassBody ||
                node instanceof Tree.InterfaceBody ||
                node instanceof Tree.ClassDefinition ||
                node instanceof Tree.InterfaceDefinition ||
                node instanceof Tree.ObjectDefinition) {
            Scope scope = node.getScope();
            if (scope instanceof ClassOrInterface) {
                ClassOrInterface ci = (ClassOrInterface) scope;
                String desc = ambiguousError ?
                        "Refine inherited ambiguous and formal members of '" + ci.getName() + "'":
                        "Refine inherited formal members of '" + ci.getName() + "'";
                proposals.add(new RefineFormalMembersProposal(node, rootNode, desc));
            }
        }
    }

}