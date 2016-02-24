package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.overloads;
import static com.redhat.ceylon.eclipse.code.complete.RefinementCompletionProposal.DEFAULT_REFINEMENT;
import static com.redhat.ceylon.eclipse.code.complete.RefinementCompletionProposal.getRefinedProducedReference;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.Arrays;
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

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ClassDefinition;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.FindBodyContainerVisitor;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Unit;

class RefineEqualsHashProposal 
        implements ICompletionProposal,
                   ICompletionProposalExtension6 {

    private final Tree.CompilationUnit rootNode;
    private final String description;
    private Node node;
    
    public RefineEqualsHashProposal(Node node, 
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
        return DEFAULT_REFINEMENT;
    }

    @Override
    public String getDisplayString() {
        return description;
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "com.redhat.ceylon.eclipse.ui.action.refineEqualsHash");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
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
            refinEqualsHash(doc);
        } 
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    private void refinEqualsHash(IDocument document) 
            throws ExecutionException {
        if (rootNode==null) return;
        TextChange change = 
                new DocumentChange("Refine Equals and Hash", 
                        document);
        change.setEdit(new MultiTextEdit());
        //TODO: copy/pasted from CeylonQuickFixAssistant
        Tree.Body body;
        int offset;
        if (node instanceof Tree.ClassDefinition) {
            ClassDefinition classDefinition = 
                    (Tree.ClassDefinition) node;
            body = classDefinition.getClassBody();
            offset = -1;
        }
        else if (node instanceof Tree.InterfaceDefinition) {
            Tree.InterfaceDefinition interfaceDefinition = 
                    (Tree.InterfaceDefinition) node;
            body = interfaceDefinition.getInterfaceBody();
            offset = -1;
        }
        else if (node instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition objectDefinition = 
                    (Tree.ObjectDefinition) node;
            body = objectDefinition.getClassBody();
            offset = -1;
        }
        else if (node instanceof Tree.ObjectExpression) {
            Tree.ObjectExpression objectExpression = 
                    (Tree.ObjectExpression) node;
            body = objectExpression.getClassBody();
            offset = -1;
        }
        else if (node instanceof Tree.ClassBody || 
                node instanceof Tree.InterfaceBody) {
            body = (Tree.Body) node;
            IEditorPart editor = getCurrentEditor();
            if (editor instanceof CeylonEditor) {
                CeylonEditor ce = (CeylonEditor) editor;
                offset = ce.getSelection().getOffset();
            }
            else {
                offset = -1;
            }
        }
        else {
            return;
        }
        if (body==null) {
            return;
        }
        boolean isInterface = 
                body instanceof Tree.InterfaceBody;
        List<Tree.Statement> statements = 
                body.getStatements();
        String indent;
//        String bodyIndent = getIndent(body, document);
        String bodyIndent = getIndent(node, document);
        String delim = getDefaultLineDelimiter(document);
        if (statements.isEmpty()) {
            indent = delim + bodyIndent + getDefaultIndent();
            if (offset<0) {
                offset = body.getStartIndex()+1;
            }
        }
        else {
            Tree.Statement statement = 
                    statements.get(statements.size()-1);
            indent = delim + getIndent(statement, document);
            if (offset<0) {
                offset = statement.getEndIndex();
            }
        }
        StringBuilder result = new StringBuilder();
        Set<Declaration> already = 
                new HashSet<Declaration>();
        ClassOrInterface ci = 
                (ClassOrInterface) 
                    node.getScope();
        Unit unit = node.getUnit();
//        Set<String> ambiguousNames = new HashSet<String>();
        Declaration equals = 
                ci.getMember("equals", null, false);
        Declaration hash = 
                ci.getMember("hash", null, false);
        for (Declaration dec: Arrays.asList(equals, hash)) {
            for (Declaration d: overloads(dec)) {
                try {
                    if (ci.isInheritedFromSupertype(d)) {
                        appendRefinementText(isInterface, 
                                indent, result, ci, unit, d);
                        importSignatureTypes(d, rootNode, already);
//                        ambiguousNames.add(d.getName());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {
            if (document.getChar(offset)=='}' && 
                    result.length()>0) {
                result.append(delim)
                    .append(bodyIndent);
            }
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        applyImports(change, already, rootNode, document);
        change.addEdit(new InsertEdit(offset, result.toString()));
        change.initializeValidationData(null);
        try {
            getWorkspace()
                .run(new PerformChangeOperation(change), 
                        new NullProgressMonitor());
        }
        catch (CoreException ce) {
            ce.printStackTrace();
        }
    }

    private void appendRefinementText(boolean isInterface, 
            String indent, StringBuilder result, 
            ClassOrInterface ci, Unit unit, Declaration member) {
        Reference pr = 
                getRefinedProducedReference(ci, member);
        String rtext = 
                getRefinementTextFor(member, pr, unit, 
                        isInterface, ci, indent, true);
        result.append(indent)
            .append(rtext)
            .append(indent);
    }
    
    static void addRefineEqualsHashProposal(
            Collection<ICompletionProposal> proposals, 
            Node n, Tree.CompilationUnit rootNode) {
        for (ICompletionProposal p: proposals) {
            if (p instanceof RefineEqualsHashProposal) {
                return;
            }
        }
        Node node;
        if (n instanceof Tree.ClassBody ||
                n instanceof Tree.InterfaceBody ||
                n instanceof Tree.ClassDefinition ||
                n instanceof Tree.InterfaceDefinition ||
                n instanceof Tree.ObjectDefinition ||
                n instanceof Tree.ObjectExpression) {
            node = n;
        }
        else {
            FindBodyContainerVisitor v = 
                    new FindBodyContainerVisitor(n);
            v.visit(rootNode);
            node = v.getDeclarationNode();
        }
        if (node!=null) {
            Scope scope = node.getScope();
            if (scope instanceof ClassOrInterface) {
                ClassOrInterface ci = (ClassOrInterface) scope;
                String name = ci.getName();
                if (name==null) {
                    return;
                }
                else if (name.startsWith("anonymous#")) {
                    name = "anonymous class";
                }
                else {
                    name = "'" + name + "'";
                }
                
                Declaration equals = 
                        ci.getMember("equals", null, false);
                Declaration hash = 
                        ci.getMember("hash", null, false);
                boolean hasEquals = true;
                for (Declaration e: overloads(equals)) {
                    if (ci.isInheritedFromSupertype(e)) {
                        hasEquals = false;
                    }
                }
                boolean hasHash = true;
                for (Declaration h: overloads(hash)) {
                    if (ci.isInheritedFromSupertype(h)) {
                        hasHash = false;
                    }
                }
                
                String desc;
                if (hasEquals && hasHash) {
                    return;
                }
                else if (hasEquals) {
                    desc = "Refine 'hash' attribute of " + name;
                }
                else if (hasHash) {
                    desc = "Refine 'equals()' method of " + name;
                }
                else {
                    desc = "Refine 'equals()' and 'hash' of " + name;
                }
                proposals.add(new RefineEqualsHashProposal(node, rootNode, desc));
            }
        }
    }

}