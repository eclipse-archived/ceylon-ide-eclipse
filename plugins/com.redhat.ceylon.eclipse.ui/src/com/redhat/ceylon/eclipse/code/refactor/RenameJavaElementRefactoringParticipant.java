package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.DocLinks.nameRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.DocLink;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.util.DocLinks;

public class RenameJavaElementRefactoringParticipant extends RenameParticipant {

    private IMember javaDeclaration;

    protected boolean initialize(Object element) {
        javaDeclaration = (IMember) element;
        return getArguments().getUpdateReferences() && 
                getProjectTypeChecker(javaDeclaration.getJavaProject().getProject())!=null;
    }

    public String getName() {
        return "Rename participant for Ceylon source";
    }
    
    public RefactoringStatus checkConditions(IProgressMonitor pm, 
            CheckConditionsContext context) {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException {
        try {
            final IProject project = javaDeclaration.getJavaProject().getProject();
            final String newName = getArguments().getNewName();
            final String oldName = javaDeclaration.getElementName();

            final HashMap<IFile,Change> changes= new HashMap<IFile,Change>();
            TypeChecker tc = getProjectTypeChecker(project);
            if (tc==null) return null;
            for (PhasedUnit phasedUnit: tc.getPhasedUnits().getPhasedUnits()) {
                final List<ReplaceEdit> edits = new ArrayList<ReplaceEdit>();
                Tree.CompilationUnit cu = phasedUnit.getCompilationUnit();
                cu.visit(new Visitor() {
                    @Override
                    public void visit(ImportMemberOrType that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), that.getDeclarationModel());
                    }
                    @Override
                    public void visit(QualifiedMemberOrTypeExpression that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), that.getDeclaration());
                    }
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
                    @Override
                    public void visit(QualifiedType that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), that.getDeclarationModel());
                    }
                    protected void visitIt(Tree.Identifier id, Declaration dec) {
                        visitIt(id.getText(), id.getStartIndex(), dec);
                    }
                    protected void visitIt(String name, int offset, Declaration dec) {
                        if (dec!=null && 
                                dec.getQualifiedNameString()
                                    .equals(getQualifiedName(javaDeclaration)) &&
                                name.equals(javaDeclaration.getElementName())) { //don't rename if aliased
                            edits.add(new ReplaceEdit(offset, oldName.length(), newName));
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
                    @Override
                    public void visit(DocLink that) {
                        super.visit(that);
                        Declaration base = that.getBase();
                        List<Declaration> qualified = that.getQualified();
                        if (base!=null) {
                            Region region = nameRegion(that, 0);
                            visitIt(DocLinks.name(that, 0), region.getOffset(), base);
                            if (qualified!=null) {
                                for (int i=0; i<qualified.size(); i++) {
                                    visitIt(DocLinks.name(that, i+1), 
                                            nameRegion(that, i+1).getOffset(), 
                                            qualified.get(i));
                                }
                            }
                        }
                    }
                });
                if (!edits.isEmpty()) {
                    try {
                        IFile file = ((IFileVirtualFile) phasedUnit.getUnitFile()).getFile();
                        TextFileChange change= new TextFileChange(file.getName(), file);
                        change.setEdit(new MultiTextEdit());
                        changes.put(file, change);
                        for (ReplaceEdit edit: edits) {
                            change.addEdit(edit);
                        }
                    }       
                    catch (Exception e) { 
                        e.printStackTrace(); 
                    }
                }
            }

            if (changes.isEmpty())
                return null;

            CompositeChange result= new CompositeChange("Ceylon source changes");
            for (Iterator<Change> iter= changes.values().iterator(); iter.hasNext();) {
                result.add((Change) iter.next());
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}