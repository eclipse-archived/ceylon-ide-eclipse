package com.redhat.ceylon.eclipse.code.imports;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModuleList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class AddModuleImportUtil {

    public static void addModuleImport(IProject project, Module target, String moduleName, String moduleVersion) {
        PhasedUnit unit = determineUnit(project, target);
        String changeText = determineChangeText(moduleName, moduleVersion);
        int changeOffset = determineChangeOffset(unit);

        TextFileChange textFileChange = new TextFileChange("Add module import", CeylonBuilder.getFile(unit));
        textFileChange.setEdit(new InsertEdit(changeOffset, changeText));
        try {
            textFileChange.perform(new NullProgressMonitor());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static PhasedUnit determineUnit(IProject project, Module module) {
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

    private static String determineChangeText(String moduleName, String moduleVersion) {
        StringBuilder importModule = new StringBuilder();
        importModule.append(System.getProperty("line.separator"));
        importModule.append(CeylonAutoEditStrategy.getDefaultIndent());
        importModule.append("import ");
        importModule.append(moduleName);
        importModule.append(" '");
        importModule.append(moduleVersion);
        importModule.append("';");
        return importModule.toString();
    }

}