/*
 * Copyright Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the authors tag. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License version 2.
 * 
 * This particular file is subject to the "Classpath" exception as provided in the 
 * LICENSE file that accompanied this code.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.redhat.ceylon.eclipse.core.model.loader.model;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.impl.reflect.ReflectionModelLoader;
import com.redhat.ceylon.compiler.loader.model.LazyModuleManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;

public class JDTModuleManager extends LazyModuleManager {

    private AbstractModelLoader modelLoader;
    private IJavaProject javaProject;

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public JDTModuleManager(Context context, IJavaProject javaProject) {
        super(context);
        this.javaProject = javaProject;
    }

    @Override
    public void initCoreModules() {
        super.initCoreModules();
        Modules modules = getContext().getModules();
        // FIXME: this should go away somewhere else, but we need it to be set otherwise
        // when we load the module from compiled sources, ModuleManager.getOrCreateModule() will not
        // return the language module because its version is null
        Module languageModule = modules.getLanguageModule();
        languageModule.setVersion(TypeChecker.LANGUAGE_MODULE_VERSION);
    }
    
    @Override
    public AbstractModelLoader getModelLoader() {
        if(modelLoader == null){
            Modules modules = getContext().getModules();
            modelLoader = new JDTModelLoader(this, modules);
        }
        return modelLoader;
    }

    /**
     * Return true if this module should be loaded from source we are compiling
     * and not from its compiled artifact at all. Returns false by default, so
     * modules will be laoded from their compiled artifact.
     */
    @Override
    protected boolean isModuleLoadedFromSource(String moduleName){
        return false;
    }
    
    @Override
    protected Module createModule(List<String> moduleName) {
        Module module = null;
        String moduleNameString = Util.getName(moduleName);
        if(isModuleLoadedFromSource(moduleNameString))
            module = new Module();
        else {
            try {
                for (IPackageFragmentRoot root : javaProject.getAllPackageFragmentRoots()) {
                    if (root.getPackageFragment(moduleNameString).exists()) {
                        module = new JDTModule(this, root);
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        if (module == null) {
            module = new JDTModule(this, null);
        }
        module.setName(moduleName);
        
        return module;
    }

    @Override
    public void prepareForTypeChecking() {
        getModelLoader().loadStandardModules();
        getModelLoader().loadPackageDescriptors();
    }
    
    @Override
    public Iterable<String> getSearchedArtifactExtensions() {
        return Arrays.asList("car", "jar");
    }
}
