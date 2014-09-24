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
        SearchPattern searchPattern;
        if (declaration instanceof Method) {
            searchPattern = createPattern(pattern, METHOD, 
                    limitTo, R_EXACT_MATCH);
        }
        else if (declaration instanceof Value) {
            int loc = pattern.lastIndexOf('.')+1;
            String setter = pattern.substring(0,loc) + 
                    "set" + pattern.substring(loc+3);
            searchPattern = createOrPattern(
                    createPattern(pattern, METHOD, 
                            limitTo, R_EXACT_MATCH),
                    createPattern(setter, METHOD, 
                            limitTo, R_EXACT_MATCH));
        }
        else {
            searchPattern = createPattern(pattern, CLASS_AND_INTERFACE, 
                    limitTo, R_EXACT_MATCH);
        }
        return searchPattern;
    }

    public static IProject[] getProjects(IProject project) {
        IProject[] referencingProjects = project.getReferencingProjects();
        IProject[] projects = new IProject[referencingProjects.length+1];
        projects[0] = project;
        System.arraycopy(referencingProjects, 0, projects, 1, referencingProjects.length);
        return projects;
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

}
