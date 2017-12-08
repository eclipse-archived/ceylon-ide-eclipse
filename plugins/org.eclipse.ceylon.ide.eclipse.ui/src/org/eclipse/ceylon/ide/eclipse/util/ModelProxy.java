/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getModelLoader;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getTypeCheckers;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toJavaString;

import java.lang.ref.SoftReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.common.model.BaseIdeModelLoader;
import org.eclipse.ceylon.ide.common.model.CeylonBinaryUnit;
import org.eclipse.ceylon.ide.common.model.ExternalSourceFile;
import org.eclipse.ceylon.ide.common.model.IProjectAware;
import org.eclipse.ceylon.ide.common.model.IResourceAware;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.Unit;

public class ModelProxy {
    
    private final String qualifiedName;
    private final String unitName;
    private final String packageName;
    private final String moduleName;
    private final String moduleVersion;
    private final String name;
    private final IProject project;
    
    private SoftReference<Declaration> declaration;
    
    private Declaration toModelDeclaration(Declaration declaration) {
        Unit unit = declaration.getUnit();
        if (unit instanceof ExternalSourceFile) {
            ExternalSourceFile externalSourceFile = (ExternalSourceFile) unit;
            if (externalSourceFile.getBinaryDeclarationSource()) {
                Declaration binaryDeclaration = externalSourceFile.retrieveBinaryDeclaration(declaration);
                if (binaryDeclaration != null) {
                    return binaryDeclaration;
                }
            }
        }
        return declaration;
    }
    
    public ModelProxy(Declaration declaration) {
        declaration = toModelDeclaration(declaration);
        Unit unit = declaration.getUnit();
        this.name = declaration.getName();
        this.qualifiedName = declaration.getQualifiedNameString();
        //TODO: persist the signature somehow, to handle overloads
        this.unitName = unit.getFilename();
        Package pack = unit.getPackage();
        this.packageName = pack.getNameAsString();
        this.moduleName = pack.getModule().getNameAsString();
        this.moduleVersion = pack.getModule().getVersion();
        this.declaration = new SoftReference<Declaration>(declaration);
        if (unit instanceof IResourceAware) {
            project = ((IResourceAware<IProject, IFolder, IFile>) unit).getResourceProject(); 
            // TODO In case of a cross project dependency (ICrossProjectReference-derived classes),
            // it will return the original project containing the source declaration.
            // Is is intentional ? In this case I wonder whether we should'nt also add a reference to 
            // the original source declaration in the original source unit of original project :
            // which means search the equivalent declaration in ((ICrossProjectReference)unit).getOriginalSourceFile().
            // I wonder whether it's not better to keep per-project ModelProxys.
        }
        else if (unit instanceof IProjectAware) {
            project = ((IProjectAware<IProject>) unit).getProject();
        }
        else {
            project = null;
        }
    }
    
    public Declaration get() {
        Declaration dec = declaration.get();
        if (dec!=null) return dec;
        
        //first handle the case of new declarations 
        //defined in a dirty editor, and local declarations
        //in an external source file
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor /*&& part.isDirty()*/) {
            CeylonParseController controller =
                    ((CeylonEditor) editor).getParseController();
            Tree.CompilationUnit rootNode = controller.getLastCompilationUnit();
            if (rootNode!=null) {
                Unit unit = rootNode.getUnit();
                if (unit!=null) {
                    Package p = unit.getPackage();
                    String pname = p.getNameAsString();
                    String mname = p.getModule().getNameAsString();
                    String mversion = p.getModule().getVersion();
                    if (pname.equals(packageName) &&
                        mname.equals(moduleName) &&
                        mversion.equals(moduleVersion)) {
                        Declaration result = 
                                getDeclarationInUnit(qualifiedName, unit);
                        if (result!=null) {
                            result = toModelDeclaration(result);
                            declaration = new SoftReference<Declaration>(result);
                            return result;
                        }
                    }
                }
            }
        }
        
        TypeChecker typeChecker = 
                getTypeChecker(moduleName, moduleVersion, project);
        if (typeChecker!=null) {
            Package pack = getModelLoader(typeChecker)
                    .getLoadedModule(moduleName, moduleVersion)
                    .getPackage(packageName);
            boolean searchForCeylonSourceFileUnit = 
                    unitName.endsWith(".ceylon");
            for (Unit unit: pack.getUnits()) {
                boolean foundTheUnit = false;
                if (unit.getFilename().equals(unitName)) {
                    foundTheUnit = true;
                } 
                else if (searchForCeylonSourceFileUnit && 
                        unit instanceof CeylonBinaryUnit) {
                    // This is only to accommodate for cases when 
                    // the source file name of a binary unit has 
                    // been stored in the 'fileName' field of the 
                    // proxy (in the serialized ModelProxy objects 
                    // of the history for example)
                    String ceylonSourceFileName = 
                            toJavaString(((CeylonBinaryUnit) unit).getCeylonFileName());
                    if (ceylonSourceFileName != null) {
                        if (ceylonSourceFileName.equals(unitName)) {
                            foundTheUnit = true;
                        }
                    }
                }
                if (foundTheUnit) {
                    Declaration result = 
                            getDeclarationInUnit(qualifiedName, unit);
                    if (result!=null) {
                        declaration = new SoftReference<Declaration>(result);
                        return result;
                    }
                }
            }
        }
        
        return null;
    }

    public String getName() {
        return name;
    }
    
    public String getQualifiedName() {
        return qualifiedName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this==obj) {
            return true;
        }
        else if (obj==null) {
            return false;
        }
        else if (obj instanceof ModelProxy) {
            ModelProxy proxy = (ModelProxy) obj;
            return qualifiedName.equals(proxy.qualifiedName) &&
                    moduleName.equals(proxy.moduleName) &&
                    moduleVersion.equals(proxy.moduleVersion);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return qualifiedName.hashCode();
    }
    
    @Override
    public String toString() {
        return qualifiedName;
    }
    
    private static TypeChecker getTypeChecker(String moduleName, String moduleVersion, 
            /*optional*/ IProject project) {
        if (project==null) {
            for (TypeChecker typeChecker: getTypeCheckers()) {
                BaseIdeModelLoader modelLoader = getModelLoader(typeChecker);
                Module module = modelLoader.getLoadedModule(moduleName, moduleVersion);
                if (module!=null) {
                    return typeChecker;
                }
            }
            return null;
        }
        else {
            return getProjectTypeChecker(project);
        }
    }
    
    public static Declaration getDeclarationInUnit(String qualifiedName, Unit unit) {
        for (Declaration d: unit.getDeclarations()) {
            String qn = d.getQualifiedNameString();
            if (qn.equals(qualifiedName)) {
                return d;
            }
            else if (qualifiedName.startsWith(qn)) {
                //TODO: I have to do this because of the
                //      shortcut refinement syntax, but
                //      I guess that's really a bug in
                //      the typechecker!
                for (Declaration m: d.getMembers()) {
                    if (m.getQualifiedNameString().equals(qualifiedName)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    
}
