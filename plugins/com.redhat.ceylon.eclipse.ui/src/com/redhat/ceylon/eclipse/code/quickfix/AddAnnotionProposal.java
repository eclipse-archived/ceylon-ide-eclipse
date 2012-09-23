package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnknownType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Annotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnnotationList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnyAttribute;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnyMethod;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Assertion;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ObjectDefinition;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PackageDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Parameter;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypedDeclaration;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

class AddAnnotionProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    final Declaration dec;
    final String annotation;
    
    AddAnnotionProposal(Declaration dec, String annotation,
            int offset, IFile file, TextFileChange change) {
        super("Make '" + dec.getName() + "' " + annotation +
            (dec.getContainer() instanceof TypeDeclaration ?
                    " in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'" : ""), 
                    change, 10, CORRECTION);
        this.offset=offset;
        this.file=file;
        this.dec = dec;
        this.annotation = annotation;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddAnnotionProposal) {
            AddAnnotionProposal that = (AddAnnotionProposal) obj;
            return that.dec.equals(dec) && 
                    that.annotation.equals(annotation);
        }
        else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return dec.hashCode();
    }

    static void addAddAnnotationProposal(String annotation, String desc, 
            Declaration dec, Collection<ICompletionProposal> proposals, 
            PhasedUnit unit, Tree.Declaration decNode) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange(desc, file);
        change.setEdit(new MultiTextEdit());
        InsertEdit insertEdit = createInsertAnnotationEdit(annotation, decNode, getDocument(change));
        change.addEdit(insertEdit);
        if (decNode instanceof Tree.TypedDeclaration &&
                !(decNode instanceof Tree.ObjectDefinition)) {
            Tree.Type type = ((Tree.TypedDeclaration) decNode).getType();
            if (type instanceof Tree.FunctionModifier 
                    || type instanceof Tree.ValueModifier) {
                ProducedType it = type.getTypeModel();
                if (it!=null && !(it.getDeclaration() instanceof UnknownType)) {
                    String explicitType = it.getProducedTypeName();
                    change.addEdit(new ReplaceEdit(type.getStartIndex(), type.getText().length(), 
                            explicitType));
                }
            }
        }
        AddAnnotionProposal p = new AddAnnotionProposal(dec, annotation, insertEdit.getOffset(), file, change);
        if (!proposals.contains(p)) {
            proposals.add(p);
        }
    }

    public static InsertEdit createInsertAnnotationEdit(String newAnnotation, Node node, IDocument doc) {
        String newAnnotationName = getAnnotationWithoutParam(newAnnotation);

        Annotation prevAnnotation = null;
        Annotation nextAnnotation = null;
        AnnotationList annotationList = getAnnotationList(node);
        if (annotationList != null) {
            for (Annotation annotation : annotationList.getAnnotations()) {
                if (isAnnotationAfter(newAnnotationName, getAnnotationIdentifier(annotation))) {
                    prevAnnotation = annotation;
                } else if (nextAnnotation == null) {
                    nextAnnotation = annotation;
                    break;
                }
            }
        }

        int nextNodeStartIndex;
        if (nextAnnotation != null) {
            nextNodeStartIndex = nextAnnotation.getStartIndex();
        } else {
            if (node instanceof AnyAttribute || node instanceof AnyMethod ) {
                nextNodeStartIndex = ((TypedDeclaration) node).getType().getStartIndex();
            } else if (node instanceof ObjectDefinition ) {
                nextNodeStartIndex = ((CommonToken) node.getMainToken()).getStartIndex();
            } else if (node instanceof ClassOrInterface) {
                nextNodeStartIndex = ((CommonToken) node.getMainToken()).getStartIndex();
            } else {
                nextNodeStartIndex = node.getStartIndex();
            }
        }

        int newAnnotationOffset;
        StringBuilder newAnnotationText = new StringBuilder();

        if (isAnnotationOnSeparateLine(newAnnotationName) && !(node instanceof Parameter)) {
            if (prevAnnotation != null && isAnnotationOnSeparateLine(getAnnotationIdentifier(prevAnnotation))) {
                newAnnotationOffset = prevAnnotation.getStopIndex() + 1;
                newAnnotationText.append(System.getProperty("line.separator"));
                newAnnotationText.append(CeylonQuickFixAssistant.getIndent(node, doc));
                newAnnotationText.append(newAnnotation);
            } else {
                newAnnotationOffset = nextNodeStartIndex;
                newAnnotationText.append(newAnnotation);
                newAnnotationText.append(System.getProperty("line.separator"));
                newAnnotationText.append(CeylonQuickFixAssistant.getIndent(node, doc));
            }
        } else {
            newAnnotationOffset = nextNodeStartIndex;
            newAnnotationText.append(newAnnotation);
            newAnnotationText.append(" ");
        }

        return new InsertEdit(newAnnotationOffset, newAnnotationText.toString());
    }

    public static AnnotationList getAnnotationList(Node node) {
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
        return annotationList;
    }

    public static String getAnnotationIdentifier(Annotation annotation) {
        String annotationName = null;
        if (annotation != null) {
            if (annotation.getPrimary() instanceof BaseMemberExpression) {
                annotationName = ((BaseMemberExpression) annotation.getPrimary()).getIdentifier().getText();
            }
        }
        return annotationName;
    }

    private static String getAnnotationWithoutParam(String annotation) {
        int index = annotation.indexOf("(");
        if (index != -1) {
            return annotation.substring(0, index).trim();
        }
        index = annotation.indexOf("\"");
        if (index != -1) {
            return annotation.substring(0, index).trim();
        }
        index = annotation.indexOf(" ");
        if (index != -1) {
            return annotation.substring(0, index).trim();
        }
        return annotation.trim();
    }

    private static boolean isAnnotationAfter(String annotation1, String annotation2) {
        int index1 = ANNOTATIONS_ORDER.indexOf(annotation1);
        int index2 = ANNOTATIONS_ORDER.indexOf(annotation2);
        return index1 >= index2;
    }

    private static boolean isAnnotationOnSeparateLine(String annotation) {
        return ANNOTATIONS_ON_SEPARATE_LINE.contains(annotation);
    }

    private static IDocument getDocument(TextFileChange change) {
        try {
            return change.getCurrentDocument(null);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static final List<String> ANNOTATIONS_ORDER = Arrays.asList("doc", "throws", "see", "tagged", "shared", "abstract", "actual", "formal", "default", "variable");
    private static final List<String> ANNOTATIONS_ON_SEPARATE_LINE = Arrays.asList("doc", "throws", "see", "tagged");
    
}