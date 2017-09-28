package org.eclipse.ceylon.ide.eclipse.core.typechecker;

import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper.ModuleDependencyAnalysisError;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnits;
import org.eclipse.ceylon.compiler.typechecker.context.TypecheckerUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Message;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.core.model.JDTModule;
import org.eclipse.ceylon.ide.eclipse.core.model.ProjectSourceFile;
import org.eclipse.ceylon.ide.eclipse.core.vfs.vfsJ2C;
import org.eclipse.ceylon.ide.common.vfs.ResourceVirtualFile;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;

public class ProjectPhasedUnit extends ModifiablePhasedUnit {
    private IFolder sourceFolderResource;
    private WeakHashMap<EditedPhasedUnit, String> workingCopies = new WeakHashMap<EditedPhasedUnit, String>();
    
    public ProjectPhasedUnit(ResourceVirtualFile<IResource, IFolder, IFile> unitFile, 
            ResourceVirtualFile<IResource, IFolder, IFile> srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            ModuleSourceMapper moduleSourceMapper,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker, tokenStream);
        sourceFolderResource = (IFolder) srcDir.getNativeResource();
        srcDir.getNativeResource().getProject();
    }
    
    public ProjectPhasedUnit(PhasedUnit other) {
        super(other);
    }

    @Override
    public IFile getResourceFile() {
        return (IFile) vfsJ2C.getIFileVirtualFile(getUnitFile()).getNativeResource();
    }
    

    @Override
    public IFolder getResourceRootFolder() {
        return sourceFolderResource;
    }
    
    @Override
    public IProject getResourceProject() {
        return sourceFolderResource.getProject();
    }

    @Override
    protected TypecheckerUnit newUnit() {
        return new ProjectSourceFile(this);
    }
    
    public void addWorkingCopy(EditedPhasedUnit workingCopy) {
        synchronized (workingCopies) {
            String fullPath = workingCopy.getUnit() != null ? workingCopy.getUnit().getFullPath() : null;
            Iterator<String> itr = workingCopies.values().iterator();
            while (itr.hasNext()) {
                String workingCopyPath = itr.next();
                if (workingCopyPath.equals(fullPath)) {
                    itr.remove();
                }
            }
            workingCopies.put(workingCopy, fullPath);
        }
    }
    
    public Iterator<EditedPhasedUnit> getWorkingCopies() {
        return workingCopies.keySet().iterator();
    }

    public void install() {
        TypeChecker typechecker = getTypeChecker();
        if (typechecker == null) {
            return;
        }
        PhasedUnits phasedUnits = typechecker.getPhasedUnits();
        ProjectPhasedUnit oldPhasedUnit = (ProjectPhasedUnit) phasedUnits.getPhasedUnitFromRelativePath(getPathRelativeToSrcDir());
        if (oldPhasedUnit == this) {
            return; // Nothing to do : the PhasedUnit is already installed in the typechecker
        }
        if (oldPhasedUnit != null) {
            ProjectSourceFile oldSourceFile = (ProjectSourceFile) oldPhasedUnit.getUnit();
            getUnit().getDependentsOf().addAll(oldSourceFile.getDependentsOf());

            Iterator<EditedPhasedUnit> workingCopies = oldPhasedUnit.getWorkingCopies(); 
            while (workingCopies.hasNext()) {
                addWorkingCopy(workingCopies.next());
            }
            
            final Tree.CompilationUnit newCompilationUnit = getCompilationUnit();
            new Visitor() {
                @Override
                public void visitAny(Node node) {
                    super.visitAny(node);
                    for (Message error: node.getErrors()) {
                        if (error instanceof ModuleDependencyAnalysisError) {
                            newCompilationUnit.addError(error);
                        }
                    }
                }
            }.visit(oldPhasedUnit.getCompilationUnit());
            
            oldPhasedUnit.remove();
            
            // pour les ICrossProjectReference, le but c'est d'enlever ce qu'il y avait (binaires ou source) 
            // Ensuite pour les éléments nouveaux , dans le cas binaire il seront normalement trouvés si le 
            // classpath est normalement remis à jour, et pour les éléments source, on parcourt tous les projets
            // 
        }
        
        phasedUnits.addPhasedUnit(getUnitFile(), this);
        JDTModule module = (JDTModule) getPackage().getModule();
        for (JDTModule moduleInReferencingProject : module.getModuleInReferencingProjects()) {
        	moduleInReferencingProject.addedOriginalUnit(getPathRelativeToSrcDir());
        }
        
        // Pour tous les projets dépendants, on appelle addPhasedUnit () sur le module correspondant, qui doit être un module source externe
        // Attention : penser à ajouter une étape de retypecheck des modules dépendants à la compil incrémentale. De toute manière ceux qui sont déjà faits ne seront pas refaits.  
    }

    public void remove() {
        TypeChecker typechecker = getTypeChecker();
        if (typechecker == null) {
            return;
        }

        PhasedUnits phasedUnits = typechecker.getPhasedUnits();
        phasedUnits.removePhasedUnitForRelativePath(getPathRelativeToSrcDir()); // remove also the ProjectSourceFile (unit) from the Package
        JDTModule module = (JDTModule) getPackage().getModule();
        for (JDTModule moduleInReferencingProject : module.getModuleInReferencingProjects()) {
        	moduleInReferencingProject.removedOriginalUnit(getPathRelativeToSrcDir());
        }
    }
}
