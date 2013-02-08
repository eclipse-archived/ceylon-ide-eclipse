package com.redhat.ceylon.test.eclipse.plugin.util;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_DEFAULT_VERSION;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_NAME;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.cmr.api.ModuleQuery.Type;
import com.redhat.ceylon.cmr.api.ModuleVersionQuery;
import com.redhat.ceylon.cmr.api.ModuleVersionResult;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModuleList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages;

public class AddCeylonTestImport {
    
    public static void addCeylonTestImport(IProject project, Module module) throws CoreException {
        PhasedUnit unit = determinePhasedUnit(project, module);
        String changeText = determineChangeText(project);
        int changeOffset = determineChangeOffset(unit);

        TextEdit textEdit = new InsertEdit(changeOffset, changeText.toString());
        TextFileChange textFileChange = new TextFileChange(CeylonTestMessages.addCeylonTestImport, CeylonBuilder.getFile(unit));
        textFileChange.setEdit(textEdit);
        textFileChange.perform(new NullProgressMonitor());
    }
    
    private static PhasedUnit determinePhasedUnit(IProject project, Module module) {
        String moduleFullPath = module.getUnit().getFullPath();
        List<PhasedUnit> phasedUnits = CeylonBuilder.getUnits(project);
        for (PhasedUnit phasedUnit : phasedUnits) {
            if (phasedUnit.getUnit().getFullPath().equals(moduleFullPath)) {
                return phasedUnit;
            }
        }
        return null;
    }

    private static int determineChangeOffset(PhasedUnit unit) {
        CompilationUnit cu = unit.getCompilationUnit();
        ModuleDescriptor md = cu.getModuleDescriptor();
        ImportModuleList iml = md.getImportModuleList();
        if (iml.getImportModules().isEmpty()) {
            return iml.getStartIndex() + 1;
        } else {
            return iml.getImportModules().get(iml.getImportModules().size() - 1).getStopIndex() + 1;
        }
    }

    private static String determineChangeText(IProject project) {
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

    private static String determineCeylonTestVersion(IProject project) {
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