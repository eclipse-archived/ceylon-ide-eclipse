package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

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
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PackageDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Parameter;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.util.FindDocumentableNodeVisitor;

public class AddDocAnnotationProposal extends ChangeCorrectionProposal {
    
    public static void addDocAnnotationProposal(Collection<ICompletionProposal> proposals, Node node, CompilationUnit cu, IFile file, IDocument doc) {
        node = determineDocumentableNode(node, cu);
        if (node == null) {
            return;
        }
        
        if (isAlreadyPresent(node)) {
            return;
        }
        
        StringBuilder docBuilder = new StringBuilder("doc \"\"");
        if (node instanceof Parameter) {
            docBuilder.append(" ");
        } else {
            docBuilder.append(System.getProperty("line.separator"));
            docBuilder.append(CeylonQuickFixAssistant.getIndent(node, doc));
        }

        TextFileChange change = new TextFileChange("Add doc annotation", file);
        change.setEdit(new InsertEdit(node.getStartIndex(), docBuilder.toString()));

        AddDocAnnotationProposal proposal = new AddDocAnnotationProposal(change, file, node.getStartIndex() + 5, determineDocumentableNodeName(node));
        if (!proposals.contains(proposal)) {
            proposals.add(proposal);
        }
    }

    private static Node determineDocumentableNode(Node node, CompilationUnit cu) {
        FindDocumentableNodeVisitor fcv = new FindDocumentableNodeVisitor(node);
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
            name = determineDocumentableNodeName(importPath);
        } else if (node instanceof PackageDescriptor) {
            ImportPath importPath = ((PackageDescriptor) node).getImportPath();
            name = determineDocumentableNodeName(importPath);
        } else if (node instanceof Assertion) {
            name = "assert";
        }

        return name;
    }
    
    private static String determineDocumentableNodeName(ImportPath importPath) {
        StringBuilder name = new StringBuilder();
        if (importPath != null) {
            for (Identifier identifier : importPath.getIdentifiers()) {
                if (name.length() > 0) {
                    name.append(".");
                }
                name.append(identifier.getText());
            }
        }
        return name.toString();
    }
    
    private static boolean isAlreadyPresent(Node node) {
        AnnotationList annotationList = null;

        if (node instanceof Tree.Declaration) {
            annotationList = ((Tree.Declaration) node).getAnnotationList();
        } else if (node instanceof ModuleDescriptor) {
            annotationList = ((ModuleDescriptor) node).getAnnotationList();
        } else if (node instanceof PackageDescriptor) {
            annotationList = ((PackageDescriptor) node).getAnnotationList();
        } else if (node instanceof Assertion) {
            annotationList = ((Assertion) node).getAnnotationList();
        }
        
        if (annotationList != null) {
            for (Annotation annotation : annotationList.getAnnotations()) {
                if (annotation.getPrimary() instanceof BaseMemberExpression) {
                    String annotationName = ((BaseMemberExpression) annotation.getPrimary()).getIdentifier().getText();
                    if ("doc".equals(annotationName)) {
                        return true;
                    }
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