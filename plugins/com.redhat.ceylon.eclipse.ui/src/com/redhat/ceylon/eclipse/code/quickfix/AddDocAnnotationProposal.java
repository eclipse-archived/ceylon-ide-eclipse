package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.createInsertAnnotationEdit;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.getAnnotationIdentifier;
import static com.redhat.ceylon.eclipse.code.quickfix.AddAnnotionProposal.getAnnotationList;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Annotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnnotationList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Assertion;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PackageDescriptor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.util.FindDocumentableVisitor;

public class AddDocAnnotationProposal extends ChangeCorrectionProposal {
    
    public static void addDocAnnotationProposal(Collection<ICompletionProposal> proposals, Node node, CompilationUnit cu, IFile file, IDocument doc) {
        node = determineDocumentableNode(node, cu);
        if (node == null) {
            return;
        }
        
        if (isAlreadyPresent(node)) {
            return;
        }
        
        InsertEdit docAnnotationInsertEdit = createInsertAnnotationEdit("doc \"\"", node, doc);
        TextFileChange docAnnotationChange = new TextFileChange("Add doc annotation", file);
        docAnnotationChange.setEdit(docAnnotationInsertEdit);

        AddDocAnnotationProposal proposal = new AddDocAnnotationProposal(docAnnotationChange, file, docAnnotationInsertEdit.getOffset() + 5, determineDocumentableNodeName(node));
        if (!proposals.contains(proposal)) {
            proposals.add(proposal);
        }
    }

    private static Node determineDocumentableNode(Node node, CompilationUnit cu) {
        FindDocumentableVisitor fcv = new FindDocumentableVisitor(node);
        fcv.visit(cu);
        return fcv.getDocumentableNode();
    }
    
    private static String determineDocumentableNodeName(Node node) {
        String name = "";
        
        if (node instanceof Tree.Declaration) {
            Identifier identifier = ((Tree.Declaration) node).getIdentifier();
            if (identifier != null) {
                name = identifier.getText();
            }
        } else if (node instanceof ModuleDescriptor) {
            ImportPath importPath = ((ModuleDescriptor) node).getImportPath();
            name = formatPath(importPath.getIdentifiers());
        } else if (node instanceof PackageDescriptor) {
            ImportPath importPath = ((PackageDescriptor) node).getImportPath();
            name = formatPath(importPath.getIdentifiers());
        } else if (node instanceof Assertion) {
            name = "assert";
        }

        return name;
    }
    
    private static boolean isAlreadyPresent(Node node) {
        AnnotationList annotationList = getAnnotationList(node);
        if (annotationList != null) {
            for (Annotation annotation : annotationList.getAnnotations()) {
                String annotationIdentifier = getAnnotationIdentifier(annotation);
                if ("doc".equals(annotationIdentifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    private IFile file;
    private int offset;

    private AddDocAnnotationProposal(Change change, IFile file, int offset, String nodeName) {
        super("Add 'doc' annotation to '" + nodeName + "'", change, 10, CORRECTION);
        this.file = file;
        this.offset = offset;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }    

}