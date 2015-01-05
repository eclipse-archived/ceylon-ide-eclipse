package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.java.codegen.CodegenUtil.getJavaNameOfDeclaration;
import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.CEYLON_CEYLON_ANNOTATION;
import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.CEYLON_LOCAL_DECLARATION_ANNOTATION;
import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.CEYLON_METHOD_ANNOTATION;
import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.CEYLON_NAME_ANNOTATION;
import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.CEYLON_OBJECT_ANNOTATION;
import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.CEYLON_ATTRIBUTE_ANNOTATION;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonClassesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.CLASS_AND_INTERFACE;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.METHOD;
import static org.eclipse.jdt.core.search.SearchPattern.R_EXACT_MATCH;
import static org.eclipse.jdt.core.search.SearchPattern.createOrPattern;
import static org.eclipse.jdt.core.search.SearchPattern.createPattern;
import static org.eclipse.jdt.internal.core.util.Util.getUnresolvedJavaElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
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

import com.redhat.ceylon.compiler.java.codegen.Naming.Suffix;
import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.Specification;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader.ActionOnResolvedGeneratedType;
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

    @SuppressWarnings("deprecation")
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

    public static String getJavaQualifiedName(IMember dec) {
        IPackageFragment packageFragment = (IPackageFragment) 
                dec.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
        IType type = (IType) dec.getAncestor(IJavaElement.TYPE);
        
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
                name = Character.toLowerCase(name.charAt(3)) + 
                        name.substring(4);
            }
        }
        
        if (dec!=type) {
            String fullyQualifiedTypeName = type.getFullyQualifiedName();
            String[] parts = fullyQualifiedTypeName.split("\\.");
            String typeName = parts.length == 0 ? fullyQualifiedTypeName : parts[parts.length-1];
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
            IAnnotation ceylonAnnotation = ((IAnnotatable) element).getAnnotation(CEYLON_CEYLON_ANNOTATION);
            return ceylonAnnotation.exists();
        }
        return false;
    }

    private static boolean isCeylonObject(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotation ceylonAnnotation = ((IAnnotatable) element).getAnnotation(CEYLON_OBJECT_ANNOTATION);
            return ceylonAnnotation.exists();
        }
        return false;
    }
    
    private static boolean isCeylonAttribute(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotation ceylonAnnotation = ((IAnnotatable) element).getAnnotation(CEYLON_ATTRIBUTE_ANNOTATION);
            return ceylonAnnotation.exists();
        }
        return false;
    }

    private static boolean isCeylonMethod(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotation ceylonAnnotation = ((IAnnotatable) element).getAnnotation(CEYLON_METHOD_ANNOTATION);
            return ceylonAnnotation.exists();
        }
        return false;
    }

    private static String getCeylonNameAnnotationValue(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotation nameAnnotation = ((IAnnotatable) element).getAnnotation(CEYLON_NAME_ANNOTATION);
            if (nameAnnotation.exists()) {
                try {
                    for (IMemberValuePair mvp : nameAnnotation.getMemberValuePairs()) {
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

    private static String getLocalDeclarationQualifier(IMember element) {
        if (element instanceof IAnnotatable) {
            IAnnotation nameAnnotation = ((IAnnotatable) element).getAnnotation(CEYLON_LOCAL_DECLARATION_ANNOTATION);
            if (nameAnnotation.exists()) {
                try {
                    for (IMemberValuePair mvp : nameAnnotation.getMemberValuePairs()) {
                        if ("qualifier".equals(mvp.getMemberName())) {
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
    
    /*
     * returns null if it's a method with no Ceylon equivalent
     * (internal getter of a Ceylon object value)
     */
    public static String getCeylonSimpleName(IMember dec) {
        String name = dec.getElementName();
        
        if (dec instanceof IMethod) {
            if (name.equals("get_")) {
                name = null;
            } else if (name.startsWith("$")) {
                name = name.substring(1);
            } else if (name.startsWith("get") ||
                     name.startsWith("set")) {
                name = Character.toLowerCase(name.charAt(3)) + 
                        name.substring(4);
                if (name.endsWith(Suffix.$priv$.name())) {
                    name = name.substring(0, name.length() - Suffix.$priv$.name().length());
                }
            } else if (name.equals("toString")) {
               name = "string";
            } else if (name.equals("hashCode")) {
                name = "hash";
            }
        } else if (dec instanceof IType) {
            IType type = (IType) dec;
            String nameAnnotationValue = getCeylonNameAnnotationValue(type);
            if (nameAnnotationValue != null) {
                return nameAnnotationValue;
            }
            
            if (name.endsWith("_")) {
                if (isCeylonObject(type) ||
                        isCeylonMethod(type)) {
                    name = name.substring(0, name.length()-1);
                }
            }
        }
        return name;
    }

    public static boolean elementEqualsDeclaration(IMember typeOrMethod, Declaration declaration) {
        // loadAllAnnotations(typeOrMethod); // Uncomment to easily load all annotations while debugging

        String javaElementSimpleName = getCeylonSimpleName(typeOrMethod);
        String ceylonDeclarationSimpleName = declaration.getName();
        if (javaElementSimpleName == null) {
            return false;
        }
        if (!javaElementSimpleName.equals(ceylonDeclarationSimpleName)) {
            if ( javaElementSimpleName.isEmpty()) {
                try {
                    if (!(typeOrMethod instanceof IType
                            && ((IType)typeOrMethod).isAnonymous()
                            && (declaration instanceof Method)
                            && ! isCeylonObject(typeOrMethod)
                            && AbstractCallable.class.getName().equals(((IType) typeOrMethod).getSuperclassName()))) {
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
            String localDeclarationQualifier = getLocalDeclarationQualifier(typeOrMethod);
            if (localDeclarationQualifier != null) {
                if (! localDeclarationQualifier.equals(declaration.getQualifier())) {
                    return false;
                }
            }
            if (isCeylonObject(typeOrMethod) 
                    && declaration.isToplevel() && !(declaration instanceof Value)) {
                return false;
            }
        }
        
        IMember declaringElement = getDeclaringElement(typeOrMethod);
        
        if (declaringElement != null) {
            declaringElement = toCeylonDeclarationElement(declaringElement);
            Declaration ceylonContainingDeclaration = getContainingDeclaration(declaration);
            
            if (ceylonContainingDeclaration != null) {
                return elementEqualsDeclaration(declaringElement, 
                        (Declaration) ceylonContainingDeclaration);
            }
        } else {
            Scope scope = declaration.getScope();
            if (scope instanceof Package) {
                String ceylonDeclarationPkgName = scope.getQualifiedNameString();
                IPackageFragment pkgFragment = (IPackageFragment) typeOrMethod.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
                String pkgFragmentName = pkgFragment != null ? pkgFragment.getElementName() : null;
                return ceylonDeclarationPkgName.equals(pkgFragmentName);
            }
        }
        return false;
    }

    public static void loadAllAnnotations(IMember typeOrMethod) {
        Map<String, Map<String, Object>> memberAnnotations = new HashMap<>();
        if (typeOrMethod instanceof IAnnotatable) {
            try {
                for (IAnnotation annotation : ((IAnnotatable) typeOrMethod).getAnnotations()) {
                    String annotationName = annotation.getElementName();
                    if (! memberAnnotations.containsKey(annotationName)) {
                        memberAnnotations.put(annotationName, new HashMap<String, Object>());
                    }
                    Map<String, Object> annotationMembers = memberAnnotations.get(annotationName);
                    for (IMemberValuePair pair : annotation.getMemberValuePairs()) {
                        annotationMembers.put(pair.getMemberName(), pair.getValue());
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        
        Map<String, Map<String, Object>> typeAnnotations = new HashMap<>();
        if (typeOrMethod instanceof IType) {
            IType type = (IType) typeOrMethod;
            try {
                for (IAnnotation annotation : ((IAnnotatable) type).getAnnotations()) {
                    String annotationName = annotation.getElementName();
                    if (! typeAnnotations.containsKey(annotationName)) {
                        typeAnnotations.put(annotationName, new HashMap<String, Object>());
                    }
                    Map<String, Object> annotationMembers = typeAnnotations.get(annotationName);
                    for (IMemberValuePair pair : annotation.getMemberValuePairs()) {
                        annotationMembers.put(pair.getMemberName(), pair.getValue());
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
    }

    public static Declaration getContainingDeclaration(Declaration declaration) {
        Declaration ceylonContainingDeclaration = null;
        if (declaration == null) {
            return null;
        }
        if (! declaration.isToplevel()) {
            Scope scope = declaration.getContainer();
            while (!(scope instanceof Package)) {
                if (scope instanceof Declaration) {
                    ceylonContainingDeclaration = (Declaration) scope;
                    break;
                }
                if (scope instanceof Specification) {
                    ceylonContainingDeclaration = ((Specification) scope).getDeclaration();
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
        
        IMember declaringElement = typeOrMethod.getDeclaringType();
        if (declaringElement == null) {
            if (typeOrMethod instanceof IType)  {
                IType type = (IType) typeOrMethod;
                try {
                    if (type.isLocal() ||type.isAnonymous() || false) {
                        final MethodBinding[] enclosingMethodBinding = new MethodBinding[1];
                        JDTModelLoader.doOnResolvedGeneratedType(type, new ActionOnResolvedGeneratedType() {
                            @Override
                            public void doWithBinding(IType classModel, ReferenceBinding classBinding,
                                    IBinaryType binaryType) {
                                char[] enclosingMethodSignature = binaryType.getEnclosingMethod();
                                if (enclosingMethodSignature != null 
                                        && enclosingMethodSignature.length > 0) {
                                    ReferenceBinding enclosingType = classBinding.enclosingType();
                                    if (enclosingType != null) {
                                        for (MethodBinding method : enclosingType.methods()) {
                                            if (CharOperation.equals(
                                                    CharOperation.concat(method.selector, method.signature()), 
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
                            IMethod enclosingMethod = (IMethod) getUnresolvedJavaElement(enclosingMethodBinding[0], null, null);
                            if (enclosingMethod != null && !enclosingMethod.isResolved()) {
                                enclosingMethod = (IMethod) ((JavaElement)enclosingMethod).resolved(enclosingMethodBinding[0]);
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
            if ((typeOrMethod instanceof IMethod) 
                    && declaringElement.getElementName() != null
                    && declaringElement.getElementName().equals(typeOrMethod.getElementName() + "_")
                    && isCeylonMethod(declaringElement)) {
                declaringElement = null;
            }
        }
        return declaringElement;
    }

    public static IProject[] getProjectsToSearch(IProject project) {
        if (project.getName().equals("Ceylon Source Archives")) {
            return CeylonBuilder.getProjects().toArray(new IProject[0]);
        }
        else {
            return getProjectAndReferencingProjects(project);
        }
    }

    public static IMember toCeylonDeclarationElement(IMember typeOrMethod) {
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
                        && (isCeylonObject(parentType) || isCeylonAttribute(parentType))) {
                        return toCeylonDeclarationElement(parentType);
                    }
                    if (methodName.equals(parentType.getElementName())
                            && method.isConstructor()
                            && isCeylon(parentType)) {
                        return toCeylonDeclarationElement(parentType);
                    }
                    if (methodName.equals("$call$")) {
                        return toCeylonDeclarationElement(parentType);
                    }
                    if (methodName.equals("$evaluate$")) {
                        return toCeylonDeclarationElement(getDeclaringElement(parentType));
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
            
        }
        if (typeOrMethod instanceof IType) {
            IType type = (IType) typeOrMethod;
            String fullyQualifiedTypeName = type.getFullyQualifiedName();
            if (fullyQualifiedTypeName.endsWith("$impl")) {
                IType interfaceType = null;
                try {
                    interfaceType = type.getJavaProject().findType(
                            fullyQualifiedTypeName.substring(0, fullyQualifiedTypeName.length() - 5),
                            (IProgressMonitor) null);
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
                if (interfaceType != null) {
                    return toCeylonDeclarationElement(interfaceType);
                }
            }
        }
        
        return typeOrMethod;
    }
    
    public static Declaration toCeylonDeclaration(IJavaElement javaElement,
            List<? extends PhasedUnit> phasedUnits) {
        if (! (javaElement instanceof IMember)) {
            return null;
        }
        IMember declarationElement = toCeylonDeclarationElement((IMember)javaElement);
        for (PhasedUnit pu: phasedUnits) {
            for (Declaration declaration: pu.getDeclarations()) {
                if (elementEqualsDeclaration(declarationElement, declaration)) {
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
