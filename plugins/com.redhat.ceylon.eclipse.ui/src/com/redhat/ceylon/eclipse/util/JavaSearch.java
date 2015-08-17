package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.java.codegen.CodegenUtil.getJavaNameOfDeclaration;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonClassesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.ide.common.util.Escaping.toInitialLowercase;
import static com.redhat.ceylon.model.loader.AbstractModelLoader.CEYLON_ATTRIBUTE_ANNOTATION;
import static com.redhat.ceylon.model.loader.AbstractModelLoader.CEYLON_CEYLON_ANNOTATION;
import static com.redhat.ceylon.model.loader.AbstractModelLoader.CEYLON_LOCAL_DECLARATION_ANNOTATION;
import static com.redhat.ceylon.model.loader.AbstractModelLoader.CEYLON_METHOD_ANNOTATION;
import static com.redhat.ceylon.model.loader.AbstractModelLoader.CEYLON_NAME_ANNOTATION;
import static com.redhat.ceylon.model.loader.AbstractModelLoader.CEYLON_OBJECT_ANNOTATION;
import static java.util.Collections.emptyList;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.CLASS_AND_INTERFACE;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.FIELD;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.METHOD;
import static org.eclipse.jdt.core.search.SearchPattern.R_EXACT_MATCH;
import static org.eclipse.jdt.core.search.SearchPattern.createOrPattern;
import static org.eclipse.jdt.core.search.SearchPattern.createPattern;
import static org.eclipse.jdt.internal.core.util.Util.getUnresolvedJavaElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.corext.util.SearchUtils;

import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.IJavaModelAware;
import com.redhat.ceylon.eclipse.core.model.IUnit;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader.ActionOnResolvedGeneratedType;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.model.loader.ModelLoader.DeclarationType;
import com.redhat.ceylon.model.loader.NamingBase;
import com.redhat.ceylon.model.loader.NamingBase.Prefix;
import com.redhat.ceylon.model.loader.NamingBase.Suffix;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Specification;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;

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
        if (declaration instanceof Function) {
            return createPattern(pattern, METHOD, 
                    limitTo, R_EXACT_MATCH);
        }
        else if (declaration instanceof Value) {
            int loc = pattern.lastIndexOf('.') + 1;
            if (pattern.substring(loc).startsWith("get")) {
                String setter = 
                        pattern.substring(0,loc) + 
                        "set" + pattern.substring(loc+3);
                SearchPattern getterPattern = 
                        createPattern(pattern, METHOD, 
                                limitTo, R_EXACT_MATCH);
                SearchPattern setterPattern = 
                        createPattern(setter, METHOD, 
                                limitTo, R_EXACT_MATCH);
                switch (limitTo) {
                case IJavaSearchConstants.WRITE_ACCESSES:
                    return setterPattern;
                case IJavaSearchConstants.READ_ACCESSES:
                    return getterPattern;
                default:
                    return createOrPattern(getterPattern, 
                            setterPattern);
                }
            }
            else {
                SearchPattern fieldPattern = 
                        createPattern(pattern, FIELD, 
                                limitTo, R_EXACT_MATCH);
                return fieldPattern;
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
    
    public static IProject[] getProjectAndRelatedProjects(
            IProject project) {
        try {
            IProject[] referencingProjects = 
                    project.getReferencingProjects();
            IProject[] referencedProjects = 
                    project.getReferencedProjects();
            IProject[] projects = 
                    new IProject[referencedProjects.length+
                                 referencingProjects.length+
                                 1];
            projects[0] = project;
            System.arraycopy(referencingProjects, 0, 
                    projects, 1, referencingProjects.length);
            System.arraycopy(referencedProjects, 0, 
                    projects, referencingProjects.length+1, 
                    referencedProjects.length);
            return projects;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new IProject[] { project };
        }
    }

    public static IProject[] getProjectAndReferencingProjects(
            IProject project) {
        try {
            IProject[] referencingProjects = 
                    project.getReferencingProjects();
            IProject[] projects = 
                    new IProject[referencingProjects.length+1];
            projects[0] = project;
            System.arraycopy(referencingProjects, 0, 
                    projects, 1, referencingProjects.length);
            return projects;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new IProject[] { project };
        }
    }

    public static IProject[] getProjectAndReferencedProjects(
            IProject project) {
        try {
            IProject[] referencedProjects = 
                    project.getReferencedProjects();
            IProject[] projects = 
                    new IProject[referencedProjects.length+1];
            projects[0] = project;
            System.arraycopy(referencedProjects, 0, 
                    projects, 1, referencedProjects.length);
            return projects;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new IProject[] { project };
        }
    }

    @SuppressWarnings("deprecation")
    public static void runSearch(
            IProgressMonitor pm, 
            SearchEngine searchEngine, 
            SearchPattern searchPattern, 
            IProject[] projects, 
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

    public static String getJavaQualifiedName(IMember dec) {
        IPackageFragment packageFragment = 
                (IPackageFragment) 
                dec.getAncestor(PACKAGE_FRAGMENT);
        IType type = 
                (IType) 
                dec.getAncestor(IJavaElement.TYPE);
        
        String qualifier = packageFragment.getElementName();
        if (! qualifier.isEmpty()) {
            qualifier += '.';
        }
        String name = dec.getElementName();
        
        if (dec instanceof IMethod && name.equals("get_")) {
            return getJavaQualifiedName(type);
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
                name = toInitialLowercase(name.substring(3));
            }
        }
        
        if (dec!=type) {
            String fullyQualifiedTypeName = 
                    type.getFullyQualifiedName();
            String[] parts = 
                    fullyQualifiedTypeName.split("\\.");
            String typeName = 
                    parts.length == 0 ? 
                        fullyQualifiedTypeName : 
                        parts[parts.length-1];
            if (typeName.endsWith(name + "_")) {
                return qualifier + name;
            }
            else {
                return qualifier + 
                        typeName + '.' + name;
            }
        }
        else {
            return qualifier + name;
        }
    }
    
    private static boolean isCeylon(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotatable annotatable = 
                    (IAnnotatable) element;
            IAnnotation ceylonAnnotation = 
                    annotatable.getAnnotation(
                            CEYLON_CEYLON_ANNOTATION);
            return ceylonAnnotation.exists();
        }
        return false;
    }

    private static boolean isCeylonObject(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotatable annotatable = 
                    (IAnnotatable) element;
            IAnnotation ceylonAnnotation = 
                    annotatable.getAnnotation(
                            CEYLON_OBJECT_ANNOTATION);
            return ceylonAnnotation.exists();
        }
        return false;
    }
    
    private static boolean isCeylonAttribute(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotatable annotatable = 
                    (IAnnotatable) element;
            IAnnotation ceylonAnnotation = 
                    annotatable.getAnnotation(
                            CEYLON_ATTRIBUTE_ANNOTATION);
            return ceylonAnnotation.exists();
        }
        return false;
    }

    private static boolean isCeylonMethod(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotatable annotatable = 
                    (IAnnotatable) element;
            IAnnotation ceylonAnnotation = 
                    annotatable.getAnnotation(
                            CEYLON_METHOD_ANNOTATION);
            return ceylonAnnotation.exists();
        }
        return false;
    }

    private static String getCeylonNameAnnotationValue(
            IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotatable annotatable = 
                    (IAnnotatable) element;
            IAnnotation nameAnnotation = 
                    annotatable.getAnnotation(
                            CEYLON_NAME_ANNOTATION);
            if (nameAnnotation.exists()) {
                try {
                    for (IMemberValuePair mvp: 
                            nameAnnotation.getMemberValuePairs()) {
                        if ("value".equals(mvp.getMemberName())) {
                            Object value = mvp.getValue();
                            if ((value instanceof String) 
                                && ! "".equals(value)) {
                                return (String) value;
                            }
                        }
                    }
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String getLocalDeclarationQualifier(
            IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotatable annotatable = 
                    (IAnnotatable) element;
            IAnnotation nameAnnotation = 
                    annotatable.getAnnotation(
                            CEYLON_LOCAL_DECLARATION_ANNOTATION);
            if (nameAnnotation.exists()) {
                try {
                    for (IMemberValuePair mvp: 
                            nameAnnotation.getMemberValuePairs()) {
                        if ("qualifier".equals(
                                mvp.getMemberName())) {
                            Object value = mvp.getValue();
                            if ((value instanceof String) 
                                    && ! "".equals(value)) {
                                return (String) value;
                            }
                        }
                    }
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public static class JdtDefaultArgumentMethodSearch 
            extends DefaultArgumentMethodSearch<IMethod> {
        @Override
        protected String getMethodName(IMethod method) {
            return method.getElementName();
        }

        @Override
        protected boolean isMethodPrivate(IMethod method) {
            try {
                return (method.getFlags() & 
                        Flags.AccPrivate) 
                            != 0;
            } catch (JavaModelException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected List<IMethod> getMethodsOfDeclaringType(
                IMethod method, String searchedName) {
            IType declaringType = method.getDeclaringType();
            if (declaringType == null) {
                return emptyList();
            }
            List<IMethod> foundMethods = new ArrayList<>();
            try {
                for (IMethod m: declaringType.getMethods()) {
                    if (searchedName == null || 
                            searchedName.equals(
                                    m.getElementName())) {
                        foundMethods.add(m);
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
                return emptyList();
            }
            return foundMethods;
        }

        @Override
        protected List<String> getParameterNames(IMethod method) {
            List<String> paramNames = new ArrayList<>();
            try {
                for (ILocalVariable param: 
                        method.getParameters()) {
                    String paramName = null;
                    IAnnotation nameAnnotation = 
                            param.getAnnotation(
                                    CEYLON_NAME_ANNOTATION);
                    if (nameAnnotation != null && 
                            nameAnnotation.exists()) {
                        IMemberValuePair[] valuePairs = 
                                nameAnnotation.getMemberValuePairs();
                        if (valuePairs != null && 
                                valuePairs.length > 0) {
                            paramName = 
                                    (String) 
                                        valuePairs[0]
                                                .getValue();
                        }
                    }
                    if (paramName == null) {
                        paramName = param.getElementName();
                    }
                    paramNames.add(paramName);
                }
                return Arrays.asList(method.getParameterNames());
            } catch (JavaModelException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }
    }
    
    public static abstract class DefaultArgumentMethodSearch<MethodType> {
        public class Result {
            public Result(MethodType defaultArgumentMethod,
                    String defaultArgumentName,
                    MethodType overloadedMethod,
                    MethodType implementationMethod) {
                this.defaultArgumentMethod = defaultArgumentMethod;
                this.defaultArgumentName = defaultArgumentName;
                this.overloadedMethod = overloadedMethod;
                this.implementationMethod = implementationMethod;
            }
            public MethodType defaultArgumentMethod;
            public String defaultArgumentName;
            public MethodType overloadedMethod;
            public MethodType implementationMethod;
        }
        
        protected abstract String getMethodName(
                MethodType method);
        protected abstract boolean isMethodPrivate(
                MethodType method);
        protected abstract List<MethodType> getMethodsOfDeclaringType(
                MethodType method, String name);
        protected abstract List<String> getParameterNames(
                MethodType method);
        
        public Result search(MethodType method) {
            String methodName = getMethodName(method);
            if (methodName.endsWith(
                    NamingBase.Suffix.$canonical$.name())) {
                return new Result(null, null, null, method);
            }
            
            String[] parts = methodName.split("\\$");
            if (methodName.startsWith("$") && 
                    parts.length > 0) {
                parts[0] = "$" + parts[0];
            }
            String searchedMethodName = "";
            boolean canBeADefaultArgumentMethod;
            if (parts.length == 2 &&
                    ! methodName.endsWith("$")) {
                canBeADefaultArgumentMethod = true;
                searchedMethodName = parts[0];
                if (isMethodPrivate(method)) {
                    searchedMethodName += 
                            NamingBase.Suffix.$priv$.name();
                }
            } else {
                canBeADefaultArgumentMethod = false;
                searchedMethodName = methodName;
            }
            
            MethodType canonicalMethod = null;
            List<MethodType> canonicalMethodSearchResult = 
                    getMethodsOfDeclaringType(method, 
                            searchedMethodName + 
                            NamingBase.Suffix.$canonical$.name());
            if (canonicalMethodSearchResult != null && 
                    ! canonicalMethodSearchResult.isEmpty()) {
                canonicalMethod = 
                        canonicalMethodSearchResult.get(0);
            } else {
                List<String> defaultArguments = 
                        new ArrayList<>();
                List<MethodType> defaultArgumentMethods = 
                        new ArrayList<>();
                for (MethodType m: 
                        getMethodsOfDeclaringType(method, 
                                null)) {
                    if (getMethodName(m)
                            .startsWith(parts[0] + "$")) {
                        defaultArgumentMethods.add(m);
                    }
                }
                if (! defaultArgumentMethods.isEmpty()) {
                    for (MethodType defaultArgumentMethod: 
                            defaultArgumentMethods) {
                        String argumentName = 
                                getMethodName(
                                        defaultArgumentMethod)
                                    .substring(parts[0].length() + 1);
                        if (! argumentName.isEmpty()) {
                            defaultArguments.add(argumentName);
                        }
                    }
                }
                if (! defaultArguments.isEmpty()) {
                    List<MethodType> overloadedMethods = 
                            getMethodsOfDeclaringType(method, 
                                    parts[0]);
                    if (overloadedMethods.size() > 1) {
                        for (MethodType overloadedMethod: 
                                overloadedMethods) {
                            List<String> argumentNames = 
                                    getParameterNames(
                                            overloadedMethod);
                            if (argumentNames.size() 
                                    < defaultArguments.size()) {
                                continue;
                            }
                            if (! argumentNames.containsAll(
                                    defaultArguments)) {
                                continue;
                            }
                            canonicalMethod = overloadedMethod;
                            break;
                        }
                    }
                }
            }
            if (canonicalMethod != null) {
                if (canBeADefaultArgumentMethod) {
                    if (getParameterNames(canonicalMethod)
                            .contains(parts[1])) {
                        return new Result(method, parts[1], 
                                null, canonicalMethod);
                    }
                } else {
                    if (canonicalMethod.equals(method)) {
                        return new Result(null, null, null, 
                                canonicalMethod);
                    } else {
                        return new Result(null, null, method, 
                                canonicalMethod);
                    }
                }
            }
            return new Result(null, null, null, null);
        }
    }
    
    /*
     * returns null if it's a method with no Ceylon equivalent
     * (internal getter of a Ceylon object value)
     */
    public static String getCeylonSimpleName(IMember dec) {
        String name = dec.getElementName();

        String nameAnnotationValue = 
                getCeylonNameAnnotationValue(dec);
        if (nameAnnotationValue != null) {
            return nameAnnotationValue;
        }
        
        if (dec instanceof IMethod) {
            if (name.startsWith(Prefix.$default$.name())) {
                name = name.substring(
                        Prefix.$default$.name()
                            .length());
            } else if (name.equals("get_")) {
                boolean isStatic = false;
                int parameterCount = 0;
                IMethod method = (IMethod) dec;
                try {
                    isStatic = 
                            (method.getFlags() & 
                             Flags.AccStatic) 
                                != 0;
                    parameterCount = 
                            method.getParameterNames()
                                .length;
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
                if (isStatic && parameterCount == 0) {
                    name = null;
                }
            } else if (name.startsWith("$")) {
                name = name.substring(1);
            } else if ((name.startsWith("get") 
                     || name.startsWith("set")) 
                    && name.length() > 3) {
                StringBuffer newName = 
                        new StringBuffer(
                                Character.toLowerCase(
                                        name.charAt(3)));
                if (name.length() > 4) { 
                        newName.append(name.substring(4));
                }
                name = newName.toString();
            } else if (name.equals("toString")) {
               name = "string";
            } else if (name.equals("hashCode")) {
                name = "hash";
            } else if (name.contains("$")) {
                IMethod method = (IMethod) dec;
                JdtDefaultArgumentMethodSearch.Result searchResult = 
                        new JdtDefaultArgumentMethodSearch()
                            .search(method);
                if (searchResult.defaultArgumentName != null) {
                    name = searchResult.defaultArgumentName;
                }
            }
            if (name.endsWith(Suffix.$canonical$.name())) {
                name = name.substring(0, 
                        name.length() - 
                        Suffix.$canonical$.name().length());
            }
            if (name.endsWith(Suffix.$priv$.name())) {
                name = name.substring(0, 
                        name.length() - 
                        Suffix.$priv$.name().length());
            }
        } else if (dec instanceof IType) {
            IType type = (IType) dec;
            if (name.endsWith("_")) {
                if (isCeylonObject(type) ||
                        isCeylonMethod(type)) {
                    name = name.substring(0, 
                            name.length()-1);
                }
            }
        }
        return name;
    }

    public static boolean elementEqualsDeclaration(
            IMember typeOrMethod, Declaration declaration) {
        // loadAllAnnotations(typeOrMethod); // Uncomment to easily load all annotations while debugging

        String javaElementSimpleName = 
                getCeylonSimpleName(typeOrMethod);
        String ceylonDeclarationSimpleName = 
                declaration.getName();
        if (javaElementSimpleName == null) {
            return false;
        }
        if (!javaElementSimpleName.equals(
                ceylonDeclarationSimpleName)) {
            if ( javaElementSimpleName.isEmpty()) {
                try {
                    if (!(typeOrMethod instanceof IType
                            && ((IType)typeOrMethod).isAnonymous()
                            && (declaration instanceof Function)
                            && ! isCeylonObject(typeOrMethod)
                            && AbstractCallable.class.getName()
                                .equals(((IType) typeOrMethod)
                                        .getSuperclassName()))) {
                        return false;
                    }
                } catch (JavaModelException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        }

        
        if (typeOrMethod instanceof IType) {
            String localDeclarationQualifier = 
                    getLocalDeclarationQualifier(
                            typeOrMethod);
            if (localDeclarationQualifier != null) {
                if (!localDeclarationQualifier.equals(
                        declaration.getQualifier())) {
                    return false;
                }
            }
            if (isCeylonObject(typeOrMethod) 
                    && declaration.isToplevel() 
                    && !(declaration instanceof Value)) {
                return false;
            }
        }
        
        IMember declaringElement = 
                getDeclaringElement(typeOrMethod);
        
        if (declaringElement != null) {
            declaringElement = 
                    toCeylonDeclarationElement(
                            declaringElement);
            Declaration ceylonContainingDeclaration = 
                    getContainingDeclaration(declaration);
            
            if (ceylonContainingDeclaration != null) {
                return elementEqualsDeclaration(
                        declaringElement, 
                        (Declaration) 
                            ceylonContainingDeclaration);
            }
        } else {
            Scope scope = declaration.getScope();
            if (scope instanceof Package) {
                String ceylonDeclarationPkgName = 
                        scope.getQualifiedNameString();
                IPackageFragment pkgFragment = 
                        (IPackageFragment) 
                        typeOrMethod.getAncestor(
                                PACKAGE_FRAGMENT);
                String pkgFragmentName = 
                        pkgFragment == null ? null : 
                            pkgFragment.getElementName();
                return ceylonDeclarationPkgName.equals(
                        pkgFragmentName);
            }
        }
        return false;
    }

    public static void loadAllAnnotations(IMember typeOrMethod) {
        Map<String, Map<String, Object>> memberAnnotations = 
                new HashMap<String, Map<String, Object>>();
        if (typeOrMethod instanceof IAnnotatable) {
            try {
                IAnnotatable annotatable = 
                        (IAnnotatable) typeOrMethod;
                for (IAnnotation annotation: 
                        annotatable.getAnnotations()) {
                    String annotationName = 
                            annotation.getElementName();
                    if (!memberAnnotations.containsKey(
                            annotationName)) {
                        memberAnnotations.put(annotationName, 
                                new HashMap<String, Object>());
                    }
                    Map<String, Object> annotationMembers = 
                            memberAnnotations.get(
                                    annotationName);
                    for (IMemberValuePair pair: 
                            annotation.getMemberValuePairs()) {
                        annotationMembers.put(
                                pair.getMemberName(), 
                                pair.getValue());
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        
        Map<String, Map<String, Object>> typeAnnotations = 
                new HashMap<String, Map<String, Object>>();
        if (typeOrMethod instanceof IType) {
            IType type = (IType) typeOrMethod;
            try {
                IAnnotatable annotatable = 
                        (IAnnotatable) type;
                for (IAnnotation annotation: 
                        annotatable.getAnnotations()) {
                    String annotationName = 
                            annotation.getElementName();
                    if (! typeAnnotations.containsKey(
                            annotationName)) {
                        typeAnnotations.put(annotationName, 
                                new HashMap<String, Object>());
                    }
                    Map<String, Object> annotationMembers = 
                            typeAnnotations.get(annotationName);
                    for (IMemberValuePair pair: 
                            annotation.getMemberValuePairs()) {
                        annotationMembers.put(
                                pair.getMemberName(), 
                                pair.getValue());
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
    }

    public static Declaration getContainingDeclaration(
            Declaration declaration) {
        Declaration ceylonContainingDeclaration = null;
        if (declaration == null) {
            return null;
        }
        if (! declaration.isToplevel()) {
            Scope scope = declaration.getContainer();
            while (!(scope instanceof Package)) {
                if (scope instanceof Declaration) {
                    boolean shouldSkip = false;
                    if (scope instanceof Value) {
                        Value value = (Value) scope;
                        if (! value.isShared()
                                && ! value.isTransient()
                                && value.getContainer() instanceof FunctionOrValue) {
                            shouldSkip = true;
                        }
                    }
                    if (!shouldSkip) {
                        ceylonContainingDeclaration = 
                                (Declaration) scope;
                        break;
                    }
                }
                if (scope instanceof Specification) {
                    Specification specification = 
                            (Specification) scope;
                    ceylonContainingDeclaration = 
                            specification.getDeclaration();
                    break;
                }
                scope = scope.getContainer();
            }
        }
        return ceylonContainingDeclaration;
    }

    public static IMember getDeclaringElement(IMember typeOrMethod) {
        // TODO : also manage the case of local declarations that are 
        // wrongly top-level (specific retrieval of the parent IMember)
        
        IMember declaringElement = 
                typeOrMethod.getDeclaringType();
        if (declaringElement == null) {
            if (typeOrMethod instanceof IType)  {
                IType type = (IType) typeOrMethod;
                try {
                    if (type.isLocal() ||type.isAnonymous() || false) {
                        final MethodBinding[] enclosingMethodBinding = 
                                new MethodBinding[1];
                        JDTModelLoader.doOnResolvedGeneratedType(type, 
                                new ActionOnResolvedGeneratedType() {
                            @Override
                            public void doWithBinding(
                                    IType classModel, 
                                    ReferenceBinding classBinding,
                                    IBinaryType binaryType) {
                                char[] enclosingMethodSignature = 
                                        binaryType.getEnclosingMethod();
                                if (enclosingMethodSignature != null 
                                        && enclosingMethodSignature.length > 0) {
                                    ReferenceBinding enclosingType = 
                                            classBinding.enclosingType();
                                    if (enclosingType != null) {
                                        for (MethodBinding method: 
                                                enclosingType.methods()) {
                                            if (CharOperation.equals(
                                                    CharOperation.concat(
                                                            method.selector, 
                                                            method.signature()), 
                                                    enclosingMethodSignature)) {
                                                enclosingMethodBinding[0] = method;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        if (enclosingMethodBinding[0] != null) {
                            IMethod enclosingMethod = 
                                    (IMethod) 
                                    getUnresolvedJavaElement(
                                            enclosingMethodBinding[0], 
                                            null, null);
                            if (enclosingMethod != null && 
                                    !enclosingMethod.isResolved()) {
                                JavaElement je = 
                                        (JavaElement) 
                                            enclosingMethod;
                                enclosingMethod = 
                                        (IMethod) 
                                        je.resolved(enclosingMethodBinding[0]);
                                if (enclosingMethod != null) {
                                    declaringElement = enclosingMethod;
                                }
                            }
                        }
                    }
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (typeOrMethod instanceof IMethod) {
                IMethod method = (IMethod) typeOrMethod;
                String methodName = method.getElementName();
                if (declaringElement.getElementName() != null
                    && declaringElement.getElementName()
                        .equals(methodName + "_")
                    && isCeylonMethod(declaringElement)) {
                    IMember declaringElementDeclaringElement = 
                            getDeclaringElement(declaringElement);
                    return declaringElementDeclaringElement;
                } else if (methodName.contains("$")) {
                    if (declaringElement instanceof IType) {
                        JdtDefaultArgumentMethodSearch.Result searchResult = 
                                new JdtDefaultArgumentMethodSearch()
                                    .search(method);
                        if (searchResult.defaultArgumentMethod != null) {
                            return searchResult.implementationMethod;
                        }
                    }
                }
            }
        } 
        return declaringElement;
    }

    public static IProject[] getProjectsToSearch(IProject project) {
        if (project == null ||
                project.getName()
                    .equals("Ceylon Source Archives")) {
            return getProjects().toArray(new IProject[0]);
        }
        else {
            return getProjectAndRelatedProjects(project);
        }
    }

    public static IMember toCeylonDeclarationElement(
            IMember typeOrMethod) {
        if (typeOrMethod instanceof IMethod) {
            IMethod method = (IMethod)typeOrMethod;
            String methodName = method.getElementName();
            if (methodName == null) {
                return typeOrMethod;
            }
            
            try {
                IType parentType = method.getDeclaringType();
                if (parentType != null) {
                    if ("get_".equals(methodName)
                        && ((method.getFlags() & Flags.AccStatic) > 0)
                        && (isCeylonObject(parentType) || 
                            isCeylonAttribute(parentType))) {
                        return toCeylonDeclarationElement(
                                parentType);
                    }
                    if (methodName.equals(
                            parentType.getElementName())
                            && method.isConstructor()
                            && isCeylon(parentType)) {
                        String constructorName = 
                                getCeylonNameAnnotationValue(method);
                        if (constructorName != null) {
                            return method;
                        } else {
                            return toCeylonDeclarationElement(
                                    parentType);
                        }
                    }
                    if (methodName.equals(Naming.Unfix.$call$.name()) ||
                        methodName.equals(Naming.Unfix.$calltyped$.name()) ||
                        methodName.equals(Naming.Unfix.$callvariadic$.name())) {
                        return toCeylonDeclarationElement(parentType);
                    }
                    if (methodName.equals("$evaluate$")) {
                        return toCeylonDeclarationElement(
                                getDeclaringElement(parentType));
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
            
        }
        if (typeOrMethod instanceof IType) {
            IType type = (IType) typeOrMethod;
            String fullyQualifiedTypeName = 
                    type.getFullyQualifiedName();
            if (fullyQualifiedTypeName.endsWith("$impl")) {
                IType interfaceType = null;
                try {
                    String name = 
                            fullyQualifiedTypeName.substring(0, 
                                    fullyQualifiedTypeName.length() - 5);
                    interfaceType = 
                            type.getJavaProject()
                            .findType(name, 
                                    (IProgressMonitor) null);
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
                if (interfaceType != null) {
                    return toCeylonDeclarationElement(
                            interfaceType);
                }
            }
        }
        
        return typeOrMethod;
    }
    
    public static Declaration toCeylonDeclaration(
            IJavaElement javaElement,
            List<? extends PhasedUnit> phasedUnits) {
        if (! (javaElement instanceof IMember)) {
            return null;
        }
        IMember member = (IMember) javaElement;
        IMember declarationElement = 
                toCeylonDeclarationElement(member);
        IProject project = 
                javaElement.getJavaProject()
                    .getProject();
        Declaration javaSourceTypeDeclaration = 
                javaSourceElementToTypeDeclaration(
                    javaElement, project);
        if (javaSourceTypeDeclaration != null && 
                javaSourceTypeDeclaration.isNative()) {
            Declaration headerDeclaration = 
                    ModelUtil.getNativeHeader(
                            javaSourceTypeDeclaration.getContainer(), 
                            javaSourceTypeDeclaration.getName());
            if (headerDeclaration != null) {
                if (elementEqualsDeclaration(
                        declarationElement, 
                        headerDeclaration)) {
                    return headerDeclaration;
                }
                Unit nativeHeaderUnit = 
                        headerDeclaration.getUnit();
                if (nativeHeaderUnit instanceof CeylonUnit) {
                    CeylonUnit unit = 
                            (CeylonUnit) nativeHeaderUnit;
                    PhasedUnit phasedUnit = 
                            unit.getPhasedUnit();
                    if (phasedUnit != null) {
                        phasedUnits = 
                                Arrays.asList(phasedUnit);
                    }
                }
            }
        }
        for (PhasedUnit pu: phasedUnits) {
            for (Declaration declaration: 
                    pu.getDeclarations()) {
                if (elementEqualsDeclaration(
                        declarationElement, declaration)) {
                    return declaration;
                }
            }
        }
        return null;
    }

    private static boolean belongsToModule(
            IJavaElement javaElement, JDTModule module) {
        return javaElement.getAncestor(PACKAGE_FRAGMENT)
                .getElementName()
                .startsWith(module.getNameAsString());
    }

    public static Declaration toCeylonDeclaration(
            IProject project, IJavaElement javaElement) {
        Set<String> searchedArchives = new HashSet<String>();
        for (IProject referencedProject: 
                getProjectAndReferencedProjects(project)) {
            if (CeylonNature.isEnabled(referencedProject)) {
                TypeChecker typeChecker = 
                        getProjectTypeChecker(referencedProject);
                if (typeChecker!=null) {
                    List<PhasedUnit> phasedUnits = 
                            typeChecker.getPhasedUnits()
                                .getPhasedUnits();
                    Declaration result = 
                            toCeylonDeclaration(javaElement, 
                                    phasedUnits);
                    if (result!=null) return result;
                    Modules modules = 
                            typeChecker.getContext()
                                .getModules();
                    for (Module m: modules.getListOfModules()) {
                        if (m instanceof JDTModule) {
                            JDTModule module = (JDTModule) m;
                            if (module.isCeylonArchive() && 
                                    !module.isProjectModule() && 
                                    module.getArtifact()!=null) {
                                String archivePath = 
                                        module.getArtifact()
                                            .getAbsolutePath();
                                if (searchedArchives.add(archivePath) &&
                                        belongsToModule(javaElement, module)) {
                                    result = 
                                            toCeylonDeclaration(
                                                javaElement, 
                                                module.getPhasedUnits());
                                    if (result!=null) {
                                        return result;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isCeylonDeclaration(
            IJavaElement javaElement) {
        IProject project = 
                javaElement.getJavaProject()
                    .getProject();
        IJavaModelAware unit = 
                CeylonBuilder.getUnit(javaElement);
        IPath path = javaElement.getPath();
        if (path!=null) {
            if (unit instanceof CeylonBinaryUnit ||
                    (isExplodeModulesEnabled(project)
                            && getCeylonClassesOutputFolder(project)
                                    .getFullPath().isPrefixOf(path))) {
                return true;
            }
            
            Declaration decl = 
                    javaSourceElementToTypeDeclaration(
                            javaElement, project);
            if (decl != null) {
                return true;
            }
        }
        return false;
    }

    private static Declaration javaSourceElementToTypeDeclaration(
            IJavaElement javaElement, IProject project) {
        if (javaElement instanceof IMember) {
            IMember member = (IMember) javaElement;
            if (member.getTypeRoot() 
                    instanceof ICompilationUnit) {
                IType javaType = null;
                if (member instanceof IType) {
                    javaType = (IType) member;
                } else {
                    IJavaElement parent = member.getParent();
                    while (parent instanceof IMember) {
                        if (parent instanceof IType) {
                            javaType = (IType) parent;
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
                if (javaType != null) {
                    JDTModelLoader modelLoader = 
                            CeylonBuilder.getProjectModelLoader(
                                    project);
                    if (modelLoader != null) {
                        IJavaModelAware javaUnit = 
                                CeylonBuilder.getUnit(
                                        javaType);
                        if (javaUnit != null) {
                            JDTModule module = 
                                    ((IUnit)javaUnit).getModule();
                            if (module != null) {
                                return modelLoader.convertToDeclaration(
                                        module, 
                                        javaType.getFullyQualifiedName('.'), 
                                        DeclarationType.TYPE);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    
}
