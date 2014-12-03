package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.java.codegen.CodegenUtil.getJavaNameOfDeclaration;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonClassesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.CLASS_AND_INTERFACE;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.METHOD;
import static org.eclipse.jdt.core.search.SearchPattern.R_EXACT_MATCH;
import static org.eclipse.jdt.core.search.SearchPattern.createOrPattern;
import static org.eclipse.jdt.core.search.SearchPattern.createPattern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.corext.util.SearchUtils;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.JDTModule;

public class JavaSearch {

    public static SearchPattern createSearchPattern(
            Declaration declaration, int limitTo) {
        String pattern;
        try {
            pattern = getJavaNameOfDeclaration(declaration);
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
        if (declaration instanceof Method) {
            return createPattern(pattern, METHOD, limitTo, R_EXACT_MATCH);
        }
        else if (declaration instanceof Value) {
            int loc = pattern.lastIndexOf('.') + 1;
            String setter = pattern.substring(0,loc) + 
                    "set" + pattern.substring(loc+3);
            SearchPattern getterPattern = 
                    createPattern(pattern, METHOD, limitTo, R_EXACT_MATCH);
            SearchPattern setterPattern = 
                    createPattern(setter, METHOD, limitTo, R_EXACT_MATCH);
            switch (limitTo) {
            case IJavaSearchConstants.WRITE_ACCESSES:
                return setterPattern;
            case IJavaSearchConstants.READ_ACCESSES:
                return getterPattern;
            default:
                return createOrPattern(getterPattern, setterPattern);
            }
        }
        else {
            SearchPattern searchPattern = 
                    createPattern(pattern, CLASS_AND_INTERFACE, 
                            limitTo, R_EXACT_MATCH);
            //weirdly, ALL_OCCURRENCES doesn't return all occurrences
            /*if (limitTo==IJavaSearchConstants.ALL_OCCURRENCES) {
                searchPattern = createOrPattern(createPattern(pattern, CLASS_AND_INTERFACE, 
                    IJavaSearchConstants.IMPLEMENTORS, R_EXACT_MATCH),
                    searchPattern);
            }*/
            return searchPattern;
        }
    }

    public static IProject[] getProjectAndReferencingProjects(IProject project) {
        IProject[] referencingProjects = project.getReferencingProjects();
        IProject[] projects = new IProject[referencingProjects.length+1];
        projects[0] = project;
        System.arraycopy(referencingProjects, 0, projects, 1, referencingProjects.length);
        return projects;
    }

    public static IProject[] getProjectAndReferencedProjects(IProject project) {
        IProject[] referencedProjects;
        try {
            referencedProjects = project.getReferencedProjects();
            IProject[] projects = new IProject[referencedProjects.length+1];
            projects[0] = project;
            System.arraycopy(referencedProjects, 0, projects, 1, referencedProjects.length);
            return projects;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new IProject[] { project };
        }
    }

    public static void runSearch(IProgressMonitor pm, SearchEngine searchEngine,
            SearchPattern searchPattern, IProject[] projects,
            SearchRequestor requestor) 
                    throws OperationCanceledException {
        try {
            searchEngine.search(searchPattern, 
                    SearchUtils.getDefaultSearchParticipants(),
                    SearchEngine.createJavaSearchScope(projects), 
                    requestor, pm);
        }
        catch (OperationCanceledException oce) {
            throw oce;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getQualifiedName(IMember dec) {
        IPackageFragment packageFragment = (IPackageFragment) 
                dec.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
        IType type = (IType) dec.getAncestor(IJavaElement.TYPE);
        
        String qualifier = packageFragment.getElementName();
        if (! qualifier.isEmpty()) {
            qualifier += '.';
        }
        String name = dec.getElementName();
        
        if (dec instanceof IMethod && name.equals("get_")) {
            return getQualifiedName(type);
        }
        else if (dec instanceof IType && name.endsWith("_")) {
            return qualifier + 
                    name.substring(0, name.length()-1);
        }
        
        if (dec instanceof IMethod) {
            if (name.startsWith("$")) {
                name = name.substring(1);
            }
            else if (name.startsWith("get") ||
                     name.startsWith("set")) {
                name = Character.toLowerCase(name.charAt(3)) + 
                        name.substring(4);
            }
            else if (name.equals("toString")) {
               name = "string";
            }
            else if (name.equals("hashCode")) {
                name = "hash";
             }
        }
        
        if (dec!=type) {
            String fullyQualifiedTypeName = type.getFullyQualifiedName();
            String[] parts = fullyQualifiedTypeName.split("\\.");
            String typeName = parts.length == 0 ? fullyQualifiedTypeName : parts[parts.length-1];
            if (typeName.endsWith("$impl")) {
                typeName = typeName.substring(0, typeName.length()-5);
            }
            if (typeName.endsWith(name + "_")) {
                return qualifier + name;
            }
            else {
                if (typeName.length()>2 && 
                        !Character.isUpperCase(typeName.charAt(0))
                        && typeName.endsWith("_")) {
                    // case of an object value
                    typeName = typeName.substring(0, typeName.length()-1);
                }
                return qualifier + 
                        typeName + '.' + name;
            }
        }
        else {
            return qualifier + name;
        }
    }

    public static boolean isDeclarationOfLinkedElement(Declaration d, 
            IJavaElement javaElement) {
        return d.getQualifiedNameString().replace("::", ".")
                .equals(getQualifiedName((IMember) javaElement));
    }

    public static IProject[] getProjectsToSearch(IProject project) {
        if (project.getName().equals("Ceylon Source Archives")) {
            return CeylonBuilder.getProjects().toArray(new IProject[0]);
        }
        else {
            return getProjectAndReferencingProjects(project);
        }
    }

    public static Declaration toCeylonDeclaration(IJavaElement javaElement,
            List<? extends PhasedUnit> phasedUnits) {
        for (PhasedUnit pu: phasedUnits) {
            for (Declaration declaration: pu.getDeclarations()) {
                if (isDeclarationOfLinkedElement(declaration, javaElement)) {
                    return declaration;
                }
            }
        }
        return null;
    }

    private static boolean belongsToModule(IJavaElement javaElement,
            JDTModule module) {
        return javaElement.getAncestor(PACKAGE_FRAGMENT).getElementName()
                .startsWith(module.getNameAsString());
    }

    public static Declaration toCeylonDeclaration(IProject project, IJavaElement javaElement) {
        Set<String> searchedArchives = new HashSet<String>();
        for (IProject referencedProject: getProjectAndReferencedProjects(project)) {
            if (CeylonNature.isEnabled(referencedProject)) {
                TypeChecker typeChecker = getProjectTypeChecker(referencedProject);
                if (typeChecker!=null) {
                    Declaration result = toCeylonDeclaration(javaElement, 
                            typeChecker.getPhasedUnits().getPhasedUnits());
                    if (result!=null) return result;
                    Modules modules = typeChecker.getContext().getModules();
                    for (Module m: modules.getListOfModules()) {
                        if (m instanceof JDTModule) {
                            JDTModule module = (JDTModule) m;
                            if (module.isCeylonArchive() && 
                                    !module.isProjectModule() && 
                                    module.getArtifact()!=null) {
                                String archivePath = module.getArtifact().getAbsolutePath();
                                if (searchedArchives.add(archivePath) &&
                                        belongsToModule(javaElement, module)) {
                                    result = toCeylonDeclaration(javaElement, 
                                            module.getPhasedUnits());
                                    if (result!=null) return result;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isCeylonDeclaration(IJavaElement javaElement) {
        IProject project = javaElement.getJavaProject().getProject();
        if (javaElement.getPath().getFileExtension().equals("car") ||
                (isExplodeModulesEnabled(project)
                        && getCeylonClassesOutputFolder(project).getFullPath()
                            .isPrefixOf(javaElement.getPath()))) {
            return true;
        }
        return false;
    }

    
}
