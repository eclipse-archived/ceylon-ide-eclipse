package com.redhat.ceylon.eclipse.core.typechecker;

import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class ProjectPhasedUnit extends IdePhasedUnit {
    private IFolder sourceFolderResource;
    private WeakHashMap<EditedPhasedUnit, Object> workingCopies = new WeakHashMap<EditedPhasedUnit, Object>();
    
    public ProjectPhasedUnit(ResourceVirtualFile unitFile, ResourceVirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, typeChecker, tokenStream);
        sourceFolderResource = (IFolder) srcDir.getResource();
        srcDir.getResource().getProject();
    }
    
    public ProjectPhasedUnit(PhasedUnit other) {
        super(other);
    }

    public IFile getSourceFileResource() {
        return (IFile) ((ResourceVirtualFile) getUnitFile()).getResource();
    }
    

    public IFolder getSourceFolderResource() {
        return sourceFolderResource;
    }
    

    public IProject getProjectResource() {
        return sourceFolderResource.getProject();
    }

    @Override
    protected Unit newUnit() {
        return new ProjectSourceFile(this);
    }
    
    public void addWorkingCopy(EditedPhasedUnit workingCopy) {
        if (! workingCopies.containsKey(workingCopy)) {
            workingCopies.put(workingCopy, null);
        }
    }
    
    public Iterator<EditedPhasedUnit> getWorkingCopies() {
        return workingCopies.keySet().iterator();
    }
}
