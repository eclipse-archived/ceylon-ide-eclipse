package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.findImportNode;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importEditForMove;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importEdits;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class MoveFileRefactoringParticipant extends MoveParticipant {

    private IFile file;
    private IFile newFile;
    
    @Override
    protected boolean initialize(Object element) {
        file = (IFile) element;
        return getProcessor() instanceof MoveProcessor &&
                getProjectTypeChecker(file.getProject())!=null &&
                file.getFileExtension()!=null &&
                (file.getFileExtension().equals("ceylon") ||
                file.getFileExtension().equals("java"));
    }

    @Override
    public String getName() {
        return "Move file participant for Ceylon source";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm,
            CheckConditionsContext context) throws OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        try {
            final IProject project = file.getProject();
            final String newName = ((IFolder) getArguments().getDestination())
                    .getProjectRelativePath().removeFirstSegments(1)
                    .toPortableString().replace('/', '.');
            String movedRelFilePath = file.getProjectRelativePath()
                    .removeFirstSegments(1).toPortableString();
            String movedRelPath = file.getParent().getProjectRelativePath()
                    .removeFirstSegments(1).toPortableString();
            final String oldName = movedRelPath.replace('/', '.');
            newFile = ((IFolder) getArguments().getDestination()).getFile(file.getName());

            final List<Change> changes= new ArrayList<Change>();

            if (file.getFileExtension().equals("java")) {
                updateRefsToMovedJavaFile(project, newName, oldName, changes);
            }
            else {
                PhasedUnit movedPhasedUnit= getProjectTypeChecker(project)
                        .getPhasedUnitFromRelativePath(movedRelFilePath);
                if (movedPhasedUnit==null) return null;
                final List<Declaration> declarations = movedPhasedUnit.getDeclarations();
                if (newName.equals(oldName)) return null;
                updateRefsFromMovedCeylonFile(project, newName, oldName, changes, 
                        movedPhasedUnit, declarations);
                updateRefsToMovedCeylonFile(project, newName, oldName, changes, 
                        movedPhasedUnit, declarations);
            }

            if (changes.isEmpty())
                return null;

            CompositeChange result= new CompositeChange("Ceylon source changes");
            for (Change change: changes) {
                result.add(change);
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void updateRefsFromMovedCeylonFile(final IProject project,
            final String newName, final String oldName,
            final List<Change> changes, final PhasedUnit movedPhasedUnit,
            final List<Declaration> declarations) {
        final Map<Declaration,String> imports = new HashMap<Declaration,String>();
        movedPhasedUnit.getCompilationUnit().visit(new Visitor() {
            @Override
            public void visit(ImportMemberOrType that) {
                super.visit(that);
                visitIt(that.getIdentifier(), that.getDeclarationModel());
            }
//            @Override
//            public void visit(QualifiedMemberOrTypeExpression that) {
//                super.visit(that);
//                visitIt(that.getIdentifier(), that.getDeclaration());
//            }
            @Override
            public void visit(BaseMemberOrTypeExpression that) {
                super.visit(that);
                visitIt(that.getIdentifier(), that.getDeclaration());
            }
            @Override
            public void visit(BaseType that) {
                super.visit(that);
                visitIt(that.getIdentifier(), that.getDeclarationModel());
            }
//            @Override
//            public void visit(QualifiedType that) {
//                super.visit(that);
//                visitIt(that.getIdentifier(), that.getDeclarationModel());
//            }
            protected void visitIt(Tree.Identifier id, Declaration dec) {
                if (dec!=null && !declarations.contains(dec) &&
                        dec.getUnit().getPackage()
                                .equals(movedPhasedUnit.getPackage())) {
                    imports.put(dec, id.getText());
                }
            }
            //TODO: DocLinks!!
        });
        collectEditsToMovedFile(newName, oldName, changes, movedPhasedUnit, imports);
    }
    
    protected void updateRefsToMovedCeylonFile(final IProject project,
            final String newName, final String oldName,
            final List<Change> changes, PhasedUnit movedPhasedUnit, 
            final List<Declaration> declarations) {
        if (!getArguments().getUpdateReferences()) return;
        for (PhasedUnit phasedUnit: getProjectTypeChecker(project)
                .getPhasedUnits().getPhasedUnits()) {
            if (phasedUnit==movedPhasedUnit) {
                continue;
            }
            final Map<Declaration,String> imports = 
                    new HashMap<Declaration,String>();
            phasedUnit.getCompilationUnit().visit(new Visitor() {
                @Override
                public void visit(ImportMemberOrType that) {
                    super.visit(that);
                    visitIt(that.getIdentifier(), that.getDeclarationModel());
                }
//                    @Override
//                    public void visit(QualifiedMemberOrTypeExpression that) {
//                        super.visit(that);
//                        visitIt(that.getIdentifier(), that.getDeclaration());
//                    }
                @Override
                public void visit(BaseMemberOrTypeExpression that) {
                    super.visit(that);
                    visitIt(that.getIdentifier(), that.getDeclaration());
                }
                @Override
                public void visit(BaseType that) {
                    super.visit(that);
                    visitIt(that.getIdentifier(), that.getDeclarationModel());
                }
//                    @Override
//                    public void visit(QualifiedType that) {
//                        super.visit(that);
//                        visitIt(that.getIdentifier(), that.getDeclarationModel());
//                    }
                protected void visitIt(Tree.Identifier id, Declaration dec) {
                    if (dec!=null && declarations.contains(dec)) {
                        imports.put(dec, id.getText());
                    }
                }
              //TODO: DocLinks!!
            });
            collectEdits(newName, oldName, changes, phasedUnit, imports);
        }
    }

    protected void updateRefsToMovedJavaFile(final IProject project,
            final String newName, final String oldName,
            final List<Change> changes) throws JavaModelException {
        if (!getArguments().getUpdateReferences()) return;
        ICompilationUnit jcu = (ICompilationUnit) JavaCore.create(file);
        final IType[] types = jcu.getTypes();
        TypeChecker tc = getProjectTypeChecker(project);
        if (tc==null) return;
        for (PhasedUnit phasedUnit: tc.getPhasedUnits().getPhasedUnits()) {
            final Map<Declaration,String> imports = 
                    new HashMap<Declaration,String>();
            phasedUnit.getCompilationUnit().visit(new Visitor() {
                @Override
                public void visit(ImportMemberOrType that) {
                    super.visit(that);
                    visitIt(that.getIdentifier(), that.getDeclarationModel());
                }
//                    @Override
//                    public void visit(QualifiedMemberOrTypeExpression that) {
//                        super.visit(that);
//                        visitIt(that.getIdentifier(), that.getDeclaration());
//                    }
                @Override
                public void visit(BaseMemberOrTypeExpression that) {
                    super.visit(that);
                    visitIt(that.getIdentifier(), that.getDeclaration());
                }
                @Override
                public void visit(BaseType that) {
                    super.visit(that);
                    visitIt(that.getIdentifier(), that.getDeclarationModel());
                }
//                    @Override
//                    public void visit(QualifiedType that) {
//                        super.visit(that);
//                        visitIt(that.getIdentifier(), that.getDeclarationModel());
//                    }
                protected void visitIt(Tree.Identifier id, Declaration dec) {
                    for (IType type: types) {
                        if (dec!=null && dec.getQualifiedNameString()
                                .equals(getQualifiedName(type))) {
                           imports.put(dec, id.getText());
                        }
                    }
                }
                protected String getQualifiedName(IMember dec) {
                    IJavaElement parent = dec.getParent();
                    if (parent instanceof ICompilationUnit) {
                        return parent.getParent().getElementName() + "::" + 
                                dec.getElementName();
                    }
                    else if (dec.getDeclaringType()!=null) {
                        return getQualifiedName(dec.getDeclaringType()) + "." + 
                                dec.getElementName();
                    }
                    else {
                        return "@";
                    }
                }
            });
            collectEdits(newName, oldName, changes, phasedUnit, imports);
        }
    }
    
    private void collectEditsToMovedFile(final String newName, final String oldName,
            final List<Change> changes, PhasedUnit movedPhasedUnit,
            final Map<Declaration, String> imports) {
        try {
            CompilationUnit cu = movedPhasedUnit.getCompilationUnit();
            TextFileChange change = 
                    new MovingTextFileChange(newFile.getName(), newFile, file);
            change.setEdit(new MultiTextEdit());
            if (!imports.isEmpty()) {
                List<InsertEdit> edits = importEdits(cu, 
                        imports.keySet(), imports.values(), null, 
                        EditorUtil.getDocument(change));
                for (TextEdit edit: edits) {
                    change.addEdit(edit);
                }
            }
            Tree.Import toDelete = findImportNode(cu, newName);
            if (toDelete!=null) {
                change.addEdit(new DeleteEdit(toDelete.getStartIndex(), 
                        toDelete.getStopIndex()-toDelete.getStartIndex()+1));
            }
            changes.add(change);
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    
    private void collectEdits(final String newName, final String oldName,
            final List<Change> changes, PhasedUnit phasedUnit,
            final Map<Declaration, String> imports) {
        try {
            if (!imports.isEmpty()) {
                IFile file = 
                        ((IFileVirtualFile) phasedUnit.getUnitFile()).getFile();
                TextFileChange change = 
                        new TextFileChange(file.getName(), file);
                List<TextEdit> edits = 
                        importEditForMove(phasedUnit.getCompilationUnit(), 
                                imports.keySet(), imports.values(), 
                                newName, oldName, 
                                EditorUtil.getDocument(change));
                if (!edits.isEmpty()) {
                    change.setEdit(new MultiTextEdit());
                    for (TextEdit edit: edits) {
                        change.addEdit(edit);
                    }
                    changes.add(change);
                }
            }
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

}
