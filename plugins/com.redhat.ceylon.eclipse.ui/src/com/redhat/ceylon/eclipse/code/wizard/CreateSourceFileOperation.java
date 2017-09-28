package org.eclipse.ceylon.ide.eclipse.code.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ui.ide.undo.CreateFileOperation;

class CreateSourceFileOperation extends CreateFileOperation {

    private IPackageFragment packageFragment;
    private String unitName;
    private IPackageFragmentRoot sourceDir;

    CreateSourceFileOperation(IPackageFragmentRoot sourceDir,
            IPackageFragment packageFragment, String unitName, 
            boolean includePreamble, String contents) {
        super(getFile(sourceDir, packageFragment, unitName), null,
                new CeylonSourceStream(getProject(sourceDir), 
                        includePreamble, contents), 
                unitName.equals("package") ? 
                        "Create Ceylon Package" : 
                        "Create Ceylon Source File");
        this.sourceDir = sourceDir;
        this.packageFragment = packageFragment;
        this.unitName = unitName;
    }
    
    IFile getFile() {
        return getFile(sourceDir, packageFragment, unitName);
    }
    
    private static IFile getFile(IPackageFragmentRoot sourceDir,
            IPackageFragment packageFragment, String unitName) {
        IPath path = 
                packageFragment.getPath()
                    .append(unitName + ".ceylon");
        IProject project = getProject(sourceDir);
        IPath relativePath = 
                path.makeRelativeTo(project.getFullPath());
        return project.getFile(relativePath);
    }

    private static IProject getProject(IPackageFragmentRoot sourceDir) {
        return sourceDir.getJavaProject().getProject();
    }

    
}