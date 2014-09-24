package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.java.codegen.CodegenUtil.getJavaNameOfDeclaration;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.CLASS_AND_INTERFACE;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.METHOD;
import static org.eclipse.jdt.core.search.SearchPattern.R_EXACT_MATCH;
import static org.eclipse.jdt.core.search.SearchPattern.createOrPattern;
import static org.eclipse.jdt.core.search.SearchPattern.createPattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Value;

public class JavaSearch {

    public static SearchPattern createSearchPattern(
            Declaration declaration, int limitTo) {
        String pattern = getJavaNameOfDeclaration(declaration);
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
            return createPattern(pattern, CLASS_AND_INTERFACE, 
                    limitTo, R_EXACT_MATCH);
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
            SearchRequestor requestor) {
        try {
            searchEngine.search(searchPattern, 
                    SearchUtils.getDefaultSearchParticipants(),
                    SearchEngine.createJavaSearchScope(projects), 
                    requestor, pm);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public static String getQualifiedName(IMember dec) {
        IPackageFragment packageFragment = (IPackageFragment) 
                dec.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
        IType type = (IType) dec.getAncestor(IJavaElement.TYPE);
        
        String qualifier = packageFragment.getElementName();
        String name = dec.getElementName();
        
        if (dec instanceof IMethod && name.equals("get_")) {
            return getQualifiedName(type);
        }
        else if (dec instanceof IType && name.endsWith("_")) {
            return qualifier + '.' + 
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
        }
        
        if (dec!=type) {
            String typeName = type.getElementName();
            if (typeName.endsWith(name + "_")) {
                return qualifier + '.' + name;
            }
            else {
                return qualifier + '.' + 
                        type.getElementName() + '.' + name;
            }
        }
        else {
            return qualifier + '.' + name;
        }
    }

    public static boolean isDeclarationOfLinkedElement(Declaration d, 
            IJavaElement javaElement) {
        return d.getQualifiedNameString().replace("::", ".")
                .equals(getQualifiedName((IMember) javaElement));
    }

}
