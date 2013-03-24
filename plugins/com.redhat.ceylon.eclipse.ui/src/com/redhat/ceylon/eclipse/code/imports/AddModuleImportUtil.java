package com.redhat.ceylon.eclipse.code.imports;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModuleList;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class AddModuleImportUtil {

    public static void addModuleImport(IProject project, Module target, String moduleName, String moduleVersion) {
        PhasedUnit unit = findPhasedUnit(project, target);
        InsertEdit edit = createEdit(unit, moduleName, moduleVersion);

        TextFileChange textFileChange = new TextFileChange("Add module import", getFile(unit));
        textFileChange.setEdit(edit);
        try {
            textFileChange.perform(new NullProgressMonitor());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
        gotoLocation(CeylonSourcePositionLocator.getNodePath(unit.getCompilationUnit(), project, 
        		getProjectTypeChecker(project)), edit.getOffset(), edit.getLength());
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
        ImportModuleList iml = getImportList(unit);        
        int offset;
        if (iml.getImportModules().isEmpty()) {
            offset = iml.getStartIndex() + 1;
        } else {
            offset = iml.getImportModules().get(iml.getImportModules().size() - 1).getStopIndex() + 1;
        }
        String newline = System.getProperty("line.separator");
        StringBuilder importModule = new StringBuilder();
		importModule.append(newline);
        importModule.append(CeylonAutoEditStrategy.getDefaultIndent());
        importModule.append("import ");
        importModule.append(moduleName);
        importModule.append(" '");
        importModule.append(moduleVersion);
        importModule.append("';");
		if (iml.getEndToken().getLine()==iml.getToken().getLine()) {
			importModule.append(newline);
		}
        return new InsertEdit(offset, importModule.toString());
    }

	private static ImportModuleList getImportList(PhasedUnit unit) {
		return unit.getCompilationUnit().getModuleDescriptor().getImportModuleList();
	}

}