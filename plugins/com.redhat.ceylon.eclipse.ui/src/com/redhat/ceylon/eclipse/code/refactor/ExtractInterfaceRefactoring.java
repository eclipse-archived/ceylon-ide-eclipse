package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.createEditorChange;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.getImportText;
import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.getImports;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.correct.AddAnnotionProposal;
import com.redhat.ceylon.eclipse.util.Nodes;

public class ExtractInterfaceRefactoring extends AbstractRefactoring {

    String newInterfaceName;
    Tree.TypedDeclaration[] extractedMembers;
    Tree.TypedDeclaration[] extractableMembers;

    private Tree.Declaration container;
    private Tree.ClassOrInterface containerAsClassOrInter;
    private Tree.ObjectDefinition containerAsObjectDef;
    private String packageName;

    private LinkedHashSet<TypeParameter> extractedTypeParameters = new LinkedHashSet<TypeParameter>();
    private HashMap<Declaration, String> extractedImports = new HashMap<Declaration, String>();
    private HashSet<String> extractedImportsPackages = new HashSet<String>();

    public ExtractInterfaceRefactoring(IEditorPart editor) {
        super(editor);

        ClassOrInterface clazz = null;
        if (node != null) {
            InternalFindContainerVisitor findContainerVisitor = new InternalFindContainerVisitor(node);
            findContainerVisitor.visit(rootNode);
            container = findContainerVisitor.container;

            if (container instanceof Tree.ClassOrInterface) {
                containerAsClassOrInter = (Tree.ClassOrInterface) container;
                clazz = containerAsClassOrInter.getDeclarationModel();
            }
            if (container instanceof Tree.ObjectDefinition) {
                containerAsObjectDef = (Tree.ObjectDefinition) container;
                clazz = containerAsObjectDef.getAnonymousClass();
            }
        }
        if (clazz != null) {
            extractableMembers = findExtractableMembers(clazz);
            packageName = findPackageName(clazz);
        }
    }

    @Override
    public boolean isEnabled() {
        return extractableMembers != null && extractableMembers.length > 0;
    }

    @Override
    public String getName() {
        return "Extract Interface";
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        collectExtractedTypeParametersFromMembers();
        collectExtractedTypeParametersFromTheirConstrains();
        collectExtractedImports();

        StringBuilder newUnitBuilder = new StringBuilder();
        addImports(newUnitBuilder);
        addInterfaceHeader(newUnitBuilder);
        addInterfaceTypeParameters(newUnitBuilder);
        addInterfaceTypeConstraints(newUnitBuilder);
        addInterfaceBody(newUnitBuilder);

        Change newUnitChange = createNewUnitChange(newUnitBuilder);
        Change originalUnitChange = createOriginalUnitChange();

        CompositeChange cc = new CompositeChange(getName());
        cc.add(newUnitChange);
        cc.add(originalUnitChange);

        return cc;
    }

    private Change createNewUnitChange(StringBuilder newUnitBuilder) {
        IPath path = sourceFile.getParent().getProjectRelativePath().append(newInterfaceName + ".ceylon");
        IFile file = project.getFile(path);
        String desc = "Create source file '" + file.getProjectRelativePath() + "'";
        CreateUnitChange newUnitChange = new CreateUnitChange(file, true, newUnitBuilder.toString(), project, desc);
        return newUnitChange;
    }

    private Change createOriginalUnitChange() {
        TextChange originalUnitChange = createEditorChange(editor, document);
        originalUnitChange.setEdit(new MultiTextEdit());
        addSatisfies(originalUnitChange);
        addSharedAndActualAnnotations(originalUnitChange);
        return originalUnitChange;
    }

    private void collectExtractedTypeParametersFromMembers() {
        for (Tree.TypedDeclaration extractedMember : extractedMembers) {

            TypedDeclaration d = extractedMember.getDeclarationModel();
            if (d != null) {
                collectExtractedTypeParameters(d.getType());
            }

            if (extractedMember instanceof Tree.AnyMethod) {
                Tree.AnyMethod method = (Tree.AnyMethod) extractedMember;
                if (method.getParameterLists() != null) {
                    for (Tree.ParameterList parameterList : method.getParameterLists()) {
                        for (Tree.Parameter parameter : parameterList.getParameters()) {
                            Parameter parameterModel = parameter.getParameterModel();
                            if (parameterModel != null) {
                                collectExtractedTypeParameters(parameterModel.getType());
                            }
                        }
                    }
                }
                if (method.getTypeConstraintList() != null) {
                    for (Tree.TypeConstraint typeConstraint : method.getTypeConstraintList().getTypeConstraints()) {
                        TypeParameter typeConstraintModel = typeConstraint.getDeclarationModel();
                        if (typeConstraintModel != null) {
                            collectExtractedTypeParameters(typeConstraintModel.getType());
                        }
                    }
                }
            }
        }
    }

    private void collectExtractedTypeParametersFromTheirConstrains() {
        if (containerAsClassOrInter != null && !extractedTypeParameters.isEmpty()) {
            if (containerAsClassOrInter.getTypeConstraintList() != null) {
                boolean repeat = true;
                while (repeat) {
                    int size = extractedTypeParameters.size();
                    for (Tree.TypeConstraint tc : containerAsClassOrInter.getTypeConstraintList().getTypeConstraints()) {
                        if (extractedTypeParameters.contains(tc.getDeclarationModel())) {
                            if (tc.getSatisfiedTypes() != null && tc.getSatisfiedTypes().getTypes() != null) {
                                for (Tree.StaticType t : tc.getSatisfiedTypes().getTypes()) {
                                    collectExtractedTypeParameters(t.getTypeModel());
                                }
                            }
                        }
                    }
                    if (size == extractedTypeParameters.size()) {
                        repeat = false;
                    }
                }
            }
        }
    }

    private void collectExtractedTypeParameters(ProducedType pt) {
        TypeDeclaration d = pt.getDeclaration();
        if (d instanceof TypeParameter) {
            extractedTypeParameters.add((TypeParameter) d);
            for (ProducedType st : d.getSatisfiedTypes()) {
                collectExtractedTypeParameters(st);
            }
        }
        else if (d instanceof UnionType) {
            for (ProducedType ct : pt.getCaseTypes()) {
                collectExtractedTypeParameters(ct);
            }
        }
        else if (d instanceof IntersectionType) {
            for (ProducedType st : pt.getSatisfiedTypes()) {
                collectExtractedTypeParameters(st);
            }
        }
        if (pt.getTypeArgumentList() != null) {
            for (ProducedType ta : pt.getTypeArgumentList()) {
                collectExtractedTypeParameters(ta);
            }
        }
    }

    private void collectExtractedImports() {
        if (containerAsClassOrInter != null && !extractedTypeParameters.isEmpty()) {
            if (containerAsClassOrInter.getTypeParameterList() != null) {
                for (Tree.TypeParameterDeclaration tp : containerAsClassOrInter.getTypeParameterList().getTypeParameterDeclarations()) {
                    if (extractedTypeParameters.contains(tp.getDeclarationModel())) {
                        collectExtractedImports(tp);
                    }
                }
            }
            if (containerAsClassOrInter.getTypeConstraintList() != null) {
                for (Tree.TypeConstraint tc : containerAsClassOrInter.getTypeConstraintList().getTypeConstraints()) {
                    if (extractedTypeParameters.contains(tc.getDeclarationModel())) {
                        collectExtractedImports(tc);
                    }
                }
            }
        }

        for (Tree.TypedDeclaration member : extractedMembers) {
            collectExtractedImports(member.getType());
            if (member instanceof Tree.AnyMethod) {
                Tree.AnyMethod method = (Tree.AnyMethod) member;
                for (Tree.ParameterList parameterList : method.getParameterLists()) {
                    collectExtractedImports(parameterList);
                }
                collectExtractedImports(method.getTypeParameterList());
                collectExtractedImports(method.getTypeConstraintList());
            }
        }

    }

    private void collectExtractedImports(Node node) {
        if (node != null) {
            Map<Declaration, String> imports = 
                    getImports(node, packageName, null, 
                            extractedImportsPackages);
            extractedImports.putAll(imports);
        }
    }

    private void addImports(StringBuilder content) {
        String delim = getDefaultLineDelimiter(document);
        String importText = 
                getImportText(extractedImportsPackages, 
                        extractedImports, delim);
        if (!importText.isEmpty()) {
            content.append(importText);
            content.append(delim);
        }
    }

    private void addInterfaceHeader(StringBuilder content) {
        content.append("shared interface ");
        content.append(newInterfaceName);
    }

    private void addInterfaceTypeParameters(StringBuilder content) {
        if (containerAsClassOrInter != null && !extractedTypeParameters.isEmpty()) {
            if (containerAsClassOrInter.getTypeParameterList() != null) {
                boolean first = true;
                for (Tree.TypeParameterDeclaration tp : containerAsClassOrInter.getTypeParameterList().getTypeParameterDeclarations()) {
                    if (extractedTypeParameters.contains(tp.getDeclarationModel())) {
                        if (first) {
                            first = false;
                            content.append("<");
                        } else {
                            content.append(", ");
                        }
                        content.append(toString(tp));
                    }
                }
                if (!first) {
                    content.append(">");
                }
            }
        }
    }

    private void addInterfaceTypeConstraints(StringBuilder content) {
        if (containerAsClassOrInter != null && !extractedTypeParameters.isEmpty()) {
            if (containerAsClassOrInter.getTypeConstraintList() != null) {
                for (Tree.TypeConstraint tc : containerAsClassOrInter.getTypeConstraintList().getTypeConstraints()) {
                    if (extractedTypeParameters.contains(tc.getDeclarationModel())) {
                        content.append(" ");
                        content.append(toString(tc));
                    }
                }
            }
        }
    }

    private void addInterfaceBody(StringBuilder content) {
        String delim = getDefaultLineDelimiter(document);
        String indent = getDefaultIndent();

        content.append(" {");
        content.append(delim);

        for (Tree.TypedDeclaration member : extractedMembers) {
            content.append(delim);
            content.append(indent);

            Tree.AnnotationList annotationList = member.getAnnotationList();
            Tree.AnonymousAnnotation anonymousAnnotation = annotationList.getAnonymousAnnotation();
            if (anonymousAnnotation != null) {
                content.append(toString(anonymousAnnotation)).append(delim).append(indent);
            }
            content.append("shared formal ");
            if(member.getDeclarationModel().isVariable()){
                content.append("variable ");
            }
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                String annotationText = toString(annotation);
                if (annotationText.equals("shared") ||
                        annotationText.equals("variable") ||
                        annotationText.equals("late") ||
                        annotationText.equals("default") ||
                        annotationText.equals("actual") ||
                        annotationText.equals("formal")) {
                    continue;
                }
                content.append(annotationText).append(" ");
            }

            content.append(toString(member.getType()));
            content.append(" ");
            content.append(toString(member.getIdentifier()));

            if (member instanceof Tree.AnyMethod) {
                Tree.AnyMethod method = (Tree.AnyMethod) member;

                if (method.getTypeConstraintList() != null) {
                    content.append(toString(method.getTypeParameterList()));
                }
                for (Tree.ParameterList parameterList : method.getParameterLists()) {
                    content.append(toString(parameterList));
                }
                if (method.getTypeConstraintList() != null) {
                    content.append(" ");
                    content.append(toString(method.getTypeConstraintList()));
                }
            }

            content.append(";");
            content.append(delim);
        }

        content.append(delim);
        content.append("}");
    }

    private void addSatisfies(TextChange originalUnitChange) {
        int offset = -1;
        boolean containsSatisfies = false;
        if (containerAsClassOrInter != null) {
            if (containerAsClassOrInter.getSatisfiedTypes() != null) {
                offset = Nodes.getIdentifyingEndOffset(containerAsClassOrInter.getSatisfiedTypes());
                containsSatisfies = true;
            }
            else if (containerAsClassOrInter instanceof Tree.AnyClass && ((Tree.AnyClass) containerAsClassOrInter).getExtendedType() != null) {
                offset = Nodes.getIdentifyingEndOffset(((Tree.AnyClass) containerAsClassOrInter).getExtendedType());
            }
            else if (containerAsClassOrInter.getCaseTypes() != null) {
                offset = Nodes.getIdentifyingEndOffset(containerAsClassOrInter.getCaseTypes());
            }
            else if (containerAsClassOrInter instanceof Tree.AnyClass && ((Tree.AnyClass) containerAsClassOrInter).getParameterList() != null) {
                offset = Nodes.getIdentifyingEndOffset(((Tree.AnyClass) containerAsClassOrInter).getParameterList());
            }
            else if (containerAsClassOrInter.getTypeParameterList() != null) {
                offset = Nodes.getIdentifyingEndOffset(containerAsClassOrInter.getTypeParameterList());
            }
            else {
                offset = Nodes.getIdentifyingEndOffset(containerAsClassOrInter.getIdentifier());
            }
        }
        if (containerAsObjectDef != null) {
            if (containerAsObjectDef.getSatisfiedTypes() != null) {
                offset = Nodes.getIdentifyingEndOffset(containerAsObjectDef.getSatisfiedTypes());
                containsSatisfies = true;
            }
            else if (containerAsObjectDef.getExtendedType() != null) {
                offset = Nodes.getIdentifyingEndOffset(containerAsObjectDef.getExtendedType());
            }
            else {
                offset = Nodes.getIdentifyingEndOffset(containerAsObjectDef.getIdentifier());
            }
        }

        StringBuilder sb = new StringBuilder();
        if (containsSatisfies) {
            sb.append(" & ");
        }
        else {
            sb.append(" satisfies ");
        }
        sb.append(newInterfaceName);
        if (!extractedTypeParameters.isEmpty() && container instanceof Tree.ClassOrInterface) {
            Tree.ClassOrInterface clazz = (Tree.ClassOrInterface) container;
            if (clazz.getTypeParameterList() != null) {
                boolean first = true;
                for (Tree.TypeParameterDeclaration tp : clazz.getTypeParameterList().getTypeParameterDeclarations()) {
                    if (extractedTypeParameters.contains(tp.getDeclarationModel())) {
                        if (first) {
                            first = false;
                            sb.append("<");
                        } else {
                            sb.append(", ");
                        }
                        sb.append(toString(tp.getIdentifier()));
                    }
                }
                if (!first) {
                    sb.append(">");
                }
            }
        }
        sb.append(" ");

        originalUnitChange.addEdit(new InsertEdit(offset, sb.toString()));
    }

    private void addSharedAndActualAnnotations(TextChange originalUnitChange) {
        for (Tree.TypedDeclaration member : extractedMembers) {
            if (!member.getDeclarationModel().isShared()) {
                InsertEdit createInsertAnnotationEdit = AddAnnotionProposal.createInsertAnnotationEdit("shared", member, document);
                originalUnitChange.addEdit(createInsertAnnotationEdit);
            }
            if (!member.getDeclarationModel().isActual()) {
                InsertEdit createInsertAnnotationEdit = AddAnnotionProposal.createInsertAnnotationEdit("actual", member, document);
                originalUnitChange.addEdit(createInsertAnnotationEdit);
            }
        }
    }

    private Tree.TypedDeclaration[] findExtractableMembers(ClassOrInterface clazz) {
        InternalFindExtractableVisitor findExtractableVisitor = new InternalFindExtractableVisitor(clazz);
        findExtractableVisitor.visit(container);
        return findExtractableVisitor.extractableMembers.toArray(new Tree.TypedDeclaration[] {});
    }

    private String findPackageName(Scope scope) {
        String packageName = null;
        while (packageName == null) {
            if (scope == null) {
                break;
            }
            if (scope instanceof Package) {
                packageName = ((Package) scope).getNameAsString();
            }
            scope = scope.getContainer();
        }
        return packageName;
    }

    private static class InternalFindContainerVisitor extends Visitor {

        private Node node;
        private Tree.Declaration current;
        private Tree.Declaration container;

        private InternalFindContainerVisitor(Node node) {
            this.node = node;
        }

        @Override
        public void visit(Tree.ObjectDefinition that) {
            Tree.Declaration d = current;
            current = that;
            super.visit(that);
            current = d;
        }

        @Override
        public void visit(Tree.ClassDefinition that) {
            Tree.Declaration d = current;
            current = that;
            super.visit(that);
            current = d;
        }

        @Override
        public void visit(Tree.InterfaceDefinition that) {
            Tree.Declaration d = current;
            current = that;
            super.visit(that);
            current = d;
        }

        @Override
        public void visitAny(Node node) {
            if (this.node == node) {
                container = current;
            }
            if (container == null) {
                super.visitAny(node);
            }
        }

    }

    private static class InternalFindExtractableVisitor extends Visitor {

        private Declaration container;
        private List<Tree.TypedDeclaration> extractableMembers = new ArrayList<Tree.TypedDeclaration>();

        private InternalFindExtractableVisitor(Declaration container) {
            this.container = container;
        }

        @Override
        public void visit(Tree.AnyAttribute that) {
            if (that.getDeclarationModel().getContainer() == container) {
                extractableMembers.add(that);
            }
        }

        @Override
        public void visit(Tree.AnyMethod that) {
            if (that.getDeclarationModel().getContainer() == container) {
                extractableMembers.add(that);
            }
        }

    }

}