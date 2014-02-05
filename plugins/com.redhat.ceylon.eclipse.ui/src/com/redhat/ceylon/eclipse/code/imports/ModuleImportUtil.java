package com.redhat.ceylon.eclipse.code.imports;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getNodeEndOffset;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getNodeLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getNodeStartOffset;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static java.util.Collections.singletonMap;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModule;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModuleList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class ModuleImportUtil {

    public static void removeModuleImports(IProject project, Module target, 
            List<String> moduleNames) {
        if (moduleNames.isEmpty()) return;
        PhasedUnit unit = findPhasedUnit(project, target);
        removeModuleImports(getFile(unit), 
                unit.getCompilationUnit(), 
                moduleNames);
    }

    public static void removeModuleImports(IFile file, CompilationUnit cu, 
            List<String> moduleNames) {
        TextFileChange textFileChange = new TextFileChange("Remove Module Import", file);
        textFileChange.setEdit(new MultiTextEdit());
        for (String moduleName: moduleNames) {
            DeleteEdit edit = createRemoveEdit(cu, moduleName);
            if (edit!=null) {
                textFileChange.addEdit(edit);
            }
        }
        try {
            textFileChange.perform(new NullProgressMonitor());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void addModuleImport(IProject project, Module target, 
            String moduleName, String moduleVersion) {
        int offset = addModuleImports(project, target, 
                singletonMap(moduleName, moduleVersion));
        gotoLocation(CeylonSourcePositionLocator.getNodePath(
                findPhasedUnit(project, target).getCompilationUnit(), project, 
                getProjectTypeChecker(project)), 
                offset + moduleName.length() + 
                        getDefaultIndent().length() + 10, 
                moduleVersion.length());
    }
    
    public static int addModuleImports(IProject project, Module target, 
            Map<String,String> moduleNamesAndVersions) {
        if (moduleNamesAndVersions.isEmpty()) return 0;
        PhasedUnit unit = findPhasedUnit(project, target);
        return addModuleImports(getFile(unit), 
                unit.getCompilationUnit(), 
                moduleNamesAndVersions);
    }

    public static int addModuleImports(IFile file, CompilationUnit cu, 
            Map<String, String> moduleNamesAndVersions) {
        TextFileChange textFileChange = new TextFileChange("Add Module Imports", file);
        textFileChange.setEdit(new MultiTextEdit());
        for (Map.Entry<String, String> entry: moduleNamesAndVersions.entrySet()) {
            InsertEdit edit = createAddEdit(cu, entry.getKey(), entry.getValue());
            if (edit!=null) {
                textFileChange.addEdit(edit);
            }
        }
        try {
            textFileChange.perform(new NullProgressMonitor());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
        return textFileChange.getEdit().getOffset();
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

    private static InsertEdit createAddEdit(CompilationUnit unit, 
            String moduleName, String moduleVersion) {
        ImportModuleList iml = getImportList(unit);    
        if (iml==null) return null;
        int offset;
        if (iml.getImportModules().isEmpty()) {
            offset = iml.getStartIndex() + 1;
        } else {
            offset = iml.getImportModules()
                    .get(iml.getImportModules().size() - 1)
                    .getStopIndex() + 1;
        }
        String newline = System.getProperty("line.separator");
        StringBuilder importModule = new StringBuilder();
        appendImportStatement(importModule, 
                moduleName, moduleVersion, newline);
        if (iml.getEndToken().getLine()==iml.getToken().getLine()) {
            importModule.append(newline);
        }
        return new InsertEdit(offset, importModule.toString());
    }

    public static void appendImportStatement(StringBuilder importModule,
            String moduleName, String moduleVersion, String newline) {
        importModule.append(newline);
        importModule.append(getDefaultIndent());
        importModule.append("import ");
        importModule.append(moduleName);
        importModule.append(" \"");
        importModule.append(moduleVersion);
        importModule.append("\";");
    }

    private static DeleteEdit createRemoveEdit(CompilationUnit unit, String moduleName) {
        ImportModuleList iml = getImportList(unit);    
        if (iml==null) return null;
        ImportModule prev = null;
        for (ImportModule im: iml.getImportModules()) {
            ImportPath ip = im.getImportPath();
            if (ip!=null && formatPath(ip.getIdentifiers()).equals(moduleName)) {
                int startOffset = getNodeStartOffset(im);
                int length = getNodeLength(im);
                //TODO: handle whitespace for first import in list
                if (prev!=null) {
                    int endOffset = getNodeEndOffset(prev);
                    length += startOffset-endOffset;
                    startOffset = endOffset;
                }
                return new DeleteEdit(startOffset, length);
            }
            prev = im;
        }
        return null;
    }

    private static ImportModuleList getImportList(CompilationUnit unit) {
        List<ModuleDescriptor> moduleDescriptors = unit.getModuleDescriptors();
        if (!moduleDescriptors.isEmpty()) {
            return moduleDescriptors.get(0).getImportModuleList();
        }
        else {
            return null;
        }
    }

}