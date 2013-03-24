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
        PhasedUnit unit = findPhasedUnit(project, target);
        InsertEdit edit = createEdit(unit, moduleName, moduleVersion);

        TextFileChange textFileChange = new TextFileChange("Add module import", CeylonBuilder.getFile(unit));
        textFileChange.setEdit(edit);
        try {
            textFileChange.perform(new NullProgressMonitor());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static PhasedUnit findPhasedUnit(IProject project, Module module) {
        String moduleFullPath = module.getUnit().getFullPath();
        List<PhasedUnit> phasedUnits = CeylonBuilder.getUnits(project);
        for (PhasedUnit phasedUnit : phasedUnits) {
            if (phasedUnit.getUnit().getFullPath().equals(moduleFullPath)) {
                return phasedUnit;
            }
        }
        return null;
    }

    private static InsertEdit createEdit(PhasedUnit unit, String moduleName, String moduleVersion) {
        CompilationUnit cu = unit.getCompilationUnit();
        ModuleDescriptor md = cu.getModuleDescriptor();
        ImportModuleList iml = md.getImportModuleList();
        
        int offset;
        if (iml.getImportModules().isEmpty()) {
            offset = iml.getStartIndex() + 1;
        } else {
            offset = iml.getImportModules().get(iml.getImportModules().size() - 1).getStopIndex() + 1;
        }
        
        StringBuilder importModule = new StringBuilder();
        importModule.append(System.getProperty("line.separator"));
        importModule.append(CeylonAutoEditStrategy.getDefaultIndent());
        importModule.append("import ");
        importModule.append(moduleName);
        importModule.append(" '");
        importModule.append(moduleVersion);
        importModule.append("';");
        if( iml.getImportModules().isEmpty() && iml.getMainToken().getLine() == iml.getEndToken().getLine() ) {
            importModule.append(System.getProperty("line.separator"));
        }
        
        return new InsertEdit(offset, importModule.toString());
    }

}