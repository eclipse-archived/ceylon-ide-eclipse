package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importEditForMove;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

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
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;

public class MoveFileRefactoringParticipant extends MoveParticipant {

    private IFile file;
    
    @Override
    protected boolean initialize(Object element) {
        file = (IFile) element;
        return file.getFileExtension().equals("ceylon")||
                file.getFileExtension().equals("java");
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
        final IProject project = file.getProject();
        final String newName = ((IFolder) getArguments().getDestination())
                .getProjectRelativePath().removeFirstSegments(1)
                .toPortableString().replace('/', '.');
        String movedRelFilePath = file.getProjectRelativePath()
                .removeFirstSegments(1).toPortableString();
        String movedRelPath = file.getParent().getProjectRelativePath()
                .removeFirstSegments(1).toPortableString();
        final String oldName = movedRelPath.replace('/', '.');
        
        final HashMap<IFile,Change> changes= new HashMap<IFile,Change>();
        
        if (file.getFileExtension().equals("java")) {
            ICompilationUnit jcu = (ICompilationUnit) JavaCore.create(file);
            final IType[] types = jcu.getTypes();
            for (PhasedUnit phasedUnit: getProjectTypeChecker(project)
                    .getPhasedUnits().getPhasedUnits()) {
                final Map<Declaration,String> imports = new HashMap<Declaration,String>();
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
        
        else {
            PhasedUnit movedPhasedUnit= getProjectTypeChecker(project)
                    .getPhasedUnitFromRelativePath(movedRelFilePath);
            final List<Declaration> declarations = movedPhasedUnit.getDeclarations();
            if (newName.equals(oldName)) return null;

            for (PhasedUnit phasedUnit: getProjectTypeChecker(project)
                    .getPhasedUnits().getPhasedUnits()) {
                final Map<Declaration,String> imports = new HashMap<Declaration,String>();
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
                });
                collectEdits(newName, oldName, changes, phasedUnit, imports);
            }
        }

        if (changes.isEmpty())
            return null;
        
        CompositeChange result= new CompositeChange("Ceylon source changes");
        for (Change change: changes.values()) {
            result.add(change);
        }
        return result;
    }
    
    private void collectEdits(final String newName, final String oldName,
            final HashMap<IFile, Change> changes, PhasedUnit phasedUnit,
            final Map<Declaration, String> imports) {
        try {
            IFile file = ((IFileVirtualFile) phasedUnit.getUnitFile()).getFile();
            TextFileChange change= new TextFileChange(file.getName(), file);
            change.setEdit(new MultiTextEdit());
            changes.put(file, change);
            if (!imports.isEmpty()) {
                List<TextEdit> edits = importEditForMove(phasedUnit.getCompilationUnit(), 
                        imports.keySet(), imports.values(), newName, oldName);
                for (TextEdit edit: edits) {
                    change.addEdit(edit);
                }
            }
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

}
