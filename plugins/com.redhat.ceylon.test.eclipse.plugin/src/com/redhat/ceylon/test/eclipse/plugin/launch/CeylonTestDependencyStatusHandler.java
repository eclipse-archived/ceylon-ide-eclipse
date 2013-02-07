package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getModule;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getModules;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getPackage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getProject;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.getShell;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.cmr.api.ModuleQuery.Type;
import com.redhat.ceylon.cmr.api.ModuleVersionQuery;
import com.redhat.ceylon.cmr.api.ModuleVersionResult;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModuleList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;

public class CeylonTestDependencyStatusHandler implements IStatusHandler {

    public static final IStatus CODE = new Status(IStatus.ERROR, CeylonTestPlugin.PLUGIN_ID, 1001, "", null);

    private static final String CEYLON_TEST_MODULE_NAME = "ceylon.test";
    private static final String CEYLON_TEST_MODULE_DEFAULT_VERSION = "0.5";

    @Override
    public Object handleStatus(IStatus status, Object source) throws CoreException {
        return validateCeylonTestDependency((ILaunchConfiguration) source);
    }

    private boolean validateCeylonTestDependency(ILaunchConfiguration config) throws CoreException {
        IProject project = getProject(config.getAttribute(ATTR_PROJECT_NAME, (String) null));
        Set<Module> modules = getTestedModules(config);
        Set<Module> modulesWithoutDependency = getTestedModulesWithoutDependency(modules);

        if (!modulesWithoutDependency.isEmpty()) {
            boolean answer = showMissingCeylonTestDependencyDialog(project, modulesWithoutDependency);
            if (answer) {
                addCeylonTestImport(project, modulesWithoutDependency);
            }
            return answer;
        }

        return true;
    }

    private Set<Module> getTestedModules(ILaunchConfiguration config) throws CoreException {
        Set<Module> modules = new HashSet<Module>();
    
        List<CeylonTestLaunchConfigEntry> entries = CeylonTestLaunchConfigEntry.buildFromLaunchConfig(config);
        for (CeylonTestLaunchConfigEntry entry : entries) {
            IProject project = getProject(entry.getProjectName());
            switch (entry.getType()) {
            case PROJECT:
                modules.addAll(getModules(project));
                break;
            case MODULE:
                Module module = getModule(project, entry.getModPkgDeclName());
                modules.add(module);
                break;
            case PACKAGE:
                Package pkg = getPackage(project, entry.getModPkgDeclName());
                modules.add(pkg.getModule());
                break;
            case CLASS:
            case CLASS_LOCAL:
            case METHOD:
            case METHOD_LOCAL:
                String[] split = entry.getModPkgDeclName().split("::");
                String pkgName = split[0];
                Package pkg2 = getPackage(project, pkgName);
                modules.add(pkg2.getModule());
                break;
            }
        }
    
        return modules;
    }

    private Set<Module> getTestedModulesWithoutDependency(Set<Module> modules) {
        Set<Module> modulesWithoutCeylonTestImport = new HashSet<Module>();
        
        for (Module module : modules) {
            boolean containtsCeylonTest = false;
            for (ModuleImport moduleImport : module.getImports()) {
                if (moduleImport.getModule().getNameAsString().equals(CEYLON_TEST_MODULE_NAME)) {
                    containtsCeylonTest = true;
                    break;
                }
            }
            if (!containtsCeylonTest) {
                modulesWithoutCeylonTestImport.add(module);
            }
        }
        return modulesWithoutCeylonTestImport;
    }

    private boolean showMissingCeylonTestDependencyDialog(final IProject project, final Set<Module> modulesWithoutDependency) {
        final StringBuilder moduleNames = new StringBuilder();
        moduleNames.append(System.getProperty("line.separator"));
        for(Module module : modulesWithoutDependency) {
            moduleNames.append(System.getProperty("line.separator"));
            moduleNames.append(module.getNameAsString());
        }
        
        boolean answer = MessageDialog.openQuestion(getShell(), 
                CeylonTestMessages.errorDialogTitle, 
                CeylonTestMessages.errorMissingCeylonTestImport + moduleNames.toString());
        
        return answer;
    }

    private void addCeylonTestImport(IProject project, Set<Module> modulesWithoutDependency) throws CoreException {
        for (Module module : modulesWithoutDependency) {
            addCeylonTestImport(project, module);
        }
    }
    
    private void addCeylonTestImport(IProject project, Module module) throws CoreException {
        PhasedUnit unit = determinePhasedUnit(project, module);
        String changeText = determineChangeText(project);
        int changeOffset = determineChangeOffset(unit);

        TextEdit textEdit = new InsertEdit(changeOffset, changeText.toString());
        TextFileChange textFileChange = new TextFileChange(CeylonTestMessages.importCeylonTestModule, CeylonBuilder.getFile(unit));
        textFileChange.setEdit(textEdit);
        textFileChange.perform(new NullProgressMonitor());
    }
    
    private PhasedUnit determinePhasedUnit(IProject project, Module module) {
        String moduleFullPath = module.getUnit().getFullPath();
        List<PhasedUnit> phasedUnits = CeylonBuilder.getUnits(project);
        for (PhasedUnit phasedUnit : phasedUnits) {
            if (phasedUnit.getUnit().getFullPath().equals(moduleFullPath)) {
                return phasedUnit;
            }
        }
        return null;
    }

    private int determineChangeOffset(PhasedUnit unit) {
        CompilationUnit cu = unit.getCompilationUnit();
        ModuleDescriptor md = cu.getModuleDescriptor();
        ImportModuleList iml = md.getImportModuleList();
        if (iml.getImportModules().isEmpty()) {
            return iml.getStartIndex() + 1;
        } else {
            return iml.getImportModules().get(iml.getImportModules().size() - 1).getStopIndex() + 1;
        }
    }

    private String determineChangeText(IProject project) {
        StringBuilder importCeylonTest = new StringBuilder();
        importCeylonTest.append(System.getProperty("line.separator"));
        importCeylonTest.append(CeylonAutoEditStrategy.getDefaultIndent());
        importCeylonTest.append("import ");
        importCeylonTest.append(CEYLON_TEST_MODULE_NAME);
        importCeylonTest.append(" '");
        importCeylonTest.append(determineCeylonTestVersion(project));
        importCeylonTest.append("';");
        return importCeylonTest.toString();
    }

    private String determineCeylonTestVersion(IProject project) {
        RepositoryManager repositoryManager = CeylonBuilder.getProjectRepositoryManager(project);
        ModuleVersionQuery query = new ModuleVersionQuery(CEYLON_TEST_MODULE_NAME, null, Type.JVM);
        ModuleVersionResult result = repositoryManager.completeVersions(query);
        if (result != null 
                && result.getVersions() != null
                && result.getVersions().size() > 0 ) {
            return result.getVersions().lastEntry().getKey();
        } else {
            return CEYLON_TEST_MODULE_DEFAULT_VERSION;
        }
    }

}