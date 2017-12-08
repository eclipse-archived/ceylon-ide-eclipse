/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.ExternalPhasedUnit;
import org.eclipse.ceylon.ide.common.model.CeylonUnit;

/*
 * Created inside the JDTModelLoader.getCompiledUnit() function if the unit is a ceylon one
 */
public class CeylonBinaryUnit extends CeylonUnit implements IJavaModelAware {
    
    IClassFile classFileElement;
    CeylonToJavaMatcher ceylonToJavaMatcher;
    
    public CeylonBinaryUnit(IClassFile typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super();
        this.classFileElement = typeRoot;
        ceylonToJavaMatcher = new CeylonToJavaMatcher(typeRoot);
        setFilename(fileName);
        setRelativePath(relativePath);
        setFullPath(fullPath);
        setPackage(pkg);
    }

    
    /*
     * Might be null if no source is linked to this ModelLoader-originating unit
     * 
     * (non-Javadoc)
     * @see org.eclipse.ceylon.ide.eclipse.core.model.CeylonUnit#getPhasedUnit()
     */
    
    @Override
    public ExternalPhasedUnit getPhasedUnit() {
        return (ExternalPhasedUnit) super.getPhasedUnit();
    }
    
    public IClassFile getTypeRoot() {
        return classFileElement;
    }

    public IProject getProject() {
        if (getTypeRoot() != null) {
            return (IProject) getTypeRoot().getJavaProject().getProject();
        }
        return null;
    }
    
    @Override
    protected ExternalPhasedUnit setPhasedUnitIfNecessary() {
        ExternalPhasedUnit phasedUnit = null;
        if (phasedUnitRef != null) {
            phasedUnit = (ExternalPhasedUnit) phasedUnitRef.get();
        }
        
        if (phasedUnit == null) {
            try {
                JDTModule module = getModule();
                if (module.getArtifact() != null) {
                    String binaryUnitRelativePath = getFullPath().replace(module.getArtifact().getPath() + "!/", "");
                    String sourceUnitRelativePath = module.toSourceUnitRelativePath(binaryUnitRelativePath);
                    if (sourceUnitRelativePath != null) {
                        phasedUnit = (ExternalPhasedUnit) module.getPhasedUnitFromRelativePath(sourceUnitRelativePath);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return phasedUnit != null ? createPhasedUnitRef(phasedUnit) : null;
    }

    @Override
    public String getSourceFileName() {
        String sourceRelativePath = getSourceRelativePath();
        if (sourceRelativePath == null) {
            return null;
        }
        String[] pathElements = sourceRelativePath.split("/");
        return pathElements[pathElements.length-1];
    }
    
    @Override
    public String getSourceRelativePath() {
        return getModule().toSourceUnitRelativePath(getRelativePath());
    }
    
    @Override
    public String getSourceFullPath() {
        String sourceArchivePath = getModule().getSourceArchivePath();
        if (sourceArchivePath == null) {
            return null;
        }
        return sourceArchivePath + "!/" + getSourceRelativePath();
    }

    @Override
    public String getCeylonSourceRelativePath() {
        return getModule().getCeylonDeclarationFile(getSourceRelativePath());
    }
    
    @Override
    public String getCeylonSourceFullPath() {
        String sourceArchivePath = getModule().getSourceArchivePath();
        if (sourceArchivePath == null) {
            return null;
        }
        return sourceArchivePath + "!/" + getCeylonSourceRelativePath();
    }
    
    @Override
    public IJavaElement toJavaElement(Declaration ceylonDeclaration, IProgressMonitor monitor) {
        return ceylonToJavaMatcher.searchInClass(ceylonDeclaration, monitor);
    }

    @Override
    public IJavaElement toJavaElement(Declaration ceylonDeclaration) {
        return ceylonToJavaMatcher.searchInClass(ceylonDeclaration, null);
    }

    @Override
    public String getCeylonFileName() {
        String ceylonSourceRelativePath = getCeylonSourceRelativePath();
        if (ceylonSourceRelativePath == null || ceylonSourceRelativePath.isEmpty()) {
            return null;
        }
        String[] splitedPath = ceylonSourceRelativePath.split("/");
        return splitedPath[splitedPath.length-1];
    }
}
