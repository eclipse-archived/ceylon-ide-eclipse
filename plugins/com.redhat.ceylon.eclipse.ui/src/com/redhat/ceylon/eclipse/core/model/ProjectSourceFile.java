package com.redhat.ceylon.eclipse.core.model;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Package;

public class ProjectSourceFile extends CeylonSourceFile {

    private IProject project;
    
    public ProjectSourceFile(ResourceVirtualFile unitFile, ResourceVirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, typeChecker, tokenStream);
        project = srcDir.getResource().getProject();
    }
    
    public ProjectSourceFile(PhasedUnit other) {
        super(other);
    }

    public IProject getProject() {
        return project;
    }
}
