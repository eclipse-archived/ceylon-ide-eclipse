package org.eclipse.ceylon.test.eclipse.plugin.launch;

import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.msg;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_ENTRIES_KEY;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.extractAnonymousClassIfRequired;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getModule;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getPackage;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getProject;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.TreePath;

import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages;
import org.eclipse.ceylon.test.eclipse.plugin.util.MethodWithContainer;

public class CeylonTestLaunchConfigEntry {

    private static final String TYPE_SEPARATOR = "=";
    private static final String PROJECT_SEPARATOR = ";";
    private static final String PACKAGE_SEPARATOR = "::";
    private static final String MEMBER_SEPARATOR = ".";

    public enum Type {
        PROJECT,
        MODULE,
        PACKAGE,
        CLASS,
        CLASS_LOCAL,
        METHOD,
        METHOD_LOCAL
    }

    public static CeylonTestLaunchConfigEntry build(IProject project, Type type, String modPkgDeclName) {
        CeylonTestLaunchConfigEntry entry = new CeylonTestLaunchConfigEntry();
        entry.projectName = project.getName();
        entry.type = type;
        entry.modPkgDeclName = modPkgDeclName;
        return entry;
    }

    public static CeylonTestLaunchConfigEntry buildFromTreePath(TreePath treePath) {
        Object firstSegment = treePath.getFirstSegment();
        Object lastSegment = treePath.getLastSegment();

        CeylonTestLaunchConfigEntry entry = new CeylonTestLaunchConfigEntry();
        entry.projectName = ((IProject) firstSegment).getName();

        if (lastSegment instanceof IProject) {
            entry.type = Type.PROJECT;
        } else if (lastSegment instanceof Module) {
            entry.type = Type.MODULE;
            entry.modPkgDeclName = ((Module) lastSegment).getNameAsString();
        } else if (lastSegment instanceof Package) {
            entry.type = Type.PACKAGE;
            entry.modPkgDeclName = ((Package) lastSegment).getNameAsString();
        } else if (lastSegment instanceof Class) {
            Class clazz = (Class) lastSegment;
            entry.type = clazz.isShared() ? Type.CLASS : Type.CLASS_LOCAL; 
            entry.modPkgDeclName = clazz.getQualifiedNameString();
        } else if (lastSegment instanceof Function) {
            Function method = (Function) lastSegment;
            entry.type = method.isShared() ? Type.METHOD : Type.METHOD_LOCAL;
            entry.modPkgDeclName = method.getQualifiedNameString();
        } else if( lastSegment instanceof MethodWithContainer ) {
            MethodWithContainer methodWithContainer = (MethodWithContainer) lastSegment;
            entry.type = methodWithContainer.getMethod().isShared() ? Type.METHOD : Type.METHOD_LOCAL;
            entry.modPkgDeclName = methodWithContainer.getContainer().getQualifiedNameString() + "." + methodWithContainer.getMethod().getName();
        }

        return entry;
    }

    public static List<CeylonTestLaunchConfigEntry> buildFromLaunchConfig(ILaunchConfiguration config) throws CoreException {
        List<CeylonTestLaunchConfigEntry> entries = new ArrayList<CeylonTestLaunchConfigEntry>();
        List<String> attributes = config.getAttribute(LAUNCH_CONFIG_ENTRIES_KEY, new ArrayList<String>());
        for (String attribute : attributes) {
            CeylonTestLaunchConfigEntry entry = buildFromLaunchConfigAttribute(attribute);
            entries.add(entry);
        }
        return entries;
    }

    private static CeylonTestLaunchConfigEntry buildFromLaunchConfigAttribute(String attribute) {
        CeylonTestLaunchConfigEntry entry = new CeylonTestLaunchConfigEntry();

        int projectSeparatorIndex = attribute.indexOf(PROJECT_SEPARATOR);
        entry.projectName = attribute.substring(Type.PROJECT.name().length() + 1, projectSeparatorIndex);
        if (attribute.length() > projectSeparatorIndex + 1) {
            attribute = attribute.substring(projectSeparatorIndex + 1);
            int typeSeparatorIndex = attribute.indexOf(TYPE_SEPARATOR);
            entry.type = Type.valueOf(attribute.substring(0, typeSeparatorIndex));
            entry.modPkgDeclName = attribute.substring(typeSeparatorIndex + 1);
        } else {
            entry.type = Type.PROJECT;
        }
        
        entry.validate();

        return entry;
    }

    private static String buildLaunchConfigAttribute(CeylonTestLaunchConfigEntry entry) {
        StringBuilder attribute = new StringBuilder();
        attribute.append(Type.PROJECT);
        attribute.append(TYPE_SEPARATOR);
        attribute.append(entry.getProjectName());
        attribute.append(PROJECT_SEPARATOR);
        if (entry.getType() != Type.PROJECT) {
            attribute.append(entry.getType());
            attribute.append(TYPE_SEPARATOR);
            attribute.append(entry.getModPkgDeclName());
        }
        return attribute.toString();
    }
    
    public static List<String> buildLaunchConfigAttributes(List<CeylonTestLaunchConfigEntry> entries) {
        List<String> attributes = new ArrayList<String>();
        for (CeylonTestLaunchConfigEntry entry : entries) {
            attributes.add(buildLaunchConfigAttribute(entry));
        }
        return attributes;
    }

    private Type type;
    private String projectName;
    private String moduleName;
    private String modPkgDeclName;
    private String errorMessage;

    public Type getType() {
        return type;
    }

    public String getProjectName() {
        return projectName;
    }
    
    public String getModuleName() {
        return moduleName;
    }

    public String getModPkgDeclName() {
        return modPkgDeclName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isValid() {
        return errorMessage == null;
    }

    public void validate() {
        errorMessage = null;

        IProject project = validateProject();
        if( !isValid() || type == Type.PROJECT )
            return;
        
        validateModule(project);
        if( !isValid() || type == Type.MODULE )
            return;

		Package pkg = validatePackage(project);
		if(pkg != null) {
			moduleName = pkg.getModule().getNameAsString();
		}
		if(!isValid() || type == Type.PACKAGE) {
			return;
		}
		
        validateDeclaration(pkg);
    }

    private void validateDeclaration(Package pkg) {
        String[] paths = parsePaths();
        Scope scope = pkg;
        Declaration d = null;
        
        if( paths == null|| paths.length == 0 || paths.length > 2 ) {
            errorMessage = msg(CeylonTestMessages.errorCanNotFindDeclaration, modPkgDeclName, projectName);
            return;
        }
        
        d = scope.getMember(paths[0], null, false);
        d = extractAnonymousClassIfRequired(d);
        if (d instanceof Class) {
            scope = (Class) d;
        }
        if (paths.length == 2) {
            d = scope.getMember(paths[1], null, false);
        }
        
        if( !(d instanceof Class) && (type == Type.CLASS || type == Type.CLASS_LOCAL) ) {
            errorMessage = msg(CeylonTestMessages.errorCanNotFindDeclaration, modPkgDeclName, projectName);            
        }
        if( !(d instanceof Function) && (type == Type.METHOD || type == Type.METHOD_LOCAL) ) {
            errorMessage = msg(CeylonTestMessages.errorCanNotFindDeclaration, modPkgDeclName, projectName);            
        }
    }

    private IProject validateProject() {
        IProject project = getProject(projectName);
        if (project == null) {
            errorMessage = msg(CeylonTestMessages.errorCanNotFindProject, projectName);
        }
        return project;
    }

    private void validateModule(IProject project) {
        if (type == Type.MODULE) {
            Module module = getModule(project, modPkgDeclName);
            if (module == null) {
                errorMessage = msg(CeylonTestMessages.errorCanNotFindModule, modPkgDeclName, projectName);
            }            
        }
    }

    private Package validatePackage(IProject project) {
        String pkgName = parsePackageName();
        Package pkg = getPackage(project, pkgName);
        if (pkg == null) {
            errorMessage = msg(CeylonTestMessages.errorCanNotFindPackage, pkgName, projectName);
        }
        return pkg;
    }

    private String parsePackageName() {
        String pkgName = null;
        if (type == Type.PACKAGE) {
            pkgName = modPkgDeclName;
        } else if (type == Type.CLASS || type == Type.CLASS_LOCAL || type == Type.METHOD || type == Type.METHOD_LOCAL) {
            int pkgSeparatorIndex = modPkgDeclName.indexOf(PACKAGE_SEPARATOR);
            if (pkgSeparatorIndex != -1) {
                pkgName = modPkgDeclName.substring(0, pkgSeparatorIndex);
            }
        }
        return pkgName;
    }
    
    private String[] parsePaths() {
        String[] paths = new String[0];
        int pkgSeparatorIndex = modPkgDeclName.indexOf(PACKAGE_SEPARATOR);
        if (pkgSeparatorIndex != -1) {
            String path = modPkgDeclName.substring(pkgSeparatorIndex + 2);
            paths = path.split("\\" + MEMBER_SEPARATOR);
        }
        return paths;
    }

}