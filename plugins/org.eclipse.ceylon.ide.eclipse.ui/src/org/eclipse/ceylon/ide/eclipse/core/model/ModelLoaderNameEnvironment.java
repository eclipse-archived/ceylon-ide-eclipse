/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.isInCeylonClassesOutputFolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.internal.jobs.InternalJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.core.BasicCompilationUnit;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementRequestor;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.NameLookup;
import org.eclipse.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.core.SourceTypeElementInfo;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;

public class ModelLoaderNameEnvironment extends SearchableEnvironment {
    public ModelLoaderNameEnvironment(IJavaProject javaProject) throws JavaModelException {
        super((JavaProject)javaProject, (WorkingCopyOwner) null);
    }

    public IJavaProject getJavaProject() {
        return project;
    }
    
    public IType findTypeInNameLookup(char[][] compoundTypeName) {
        if (compoundTypeName == null) return null;

        int length = compoundTypeName.length;
        if (length <= 1) {
            if (length == 0) return null;
            return findTypeInNameLookup(new String(compoundTypeName[0]), IPackageFragment.DEFAULT_PACKAGE_NAME);
        }

        int lengthM1 = length - 1;
        char[][] packageName = new char[lengthM1][];
        System.arraycopy(compoundTypeName, 0, packageName, 0, lengthM1);

        return findTypeInNameLookup(
            new String(compoundTypeName[lengthM1]),
            CharOperation.toString(packageName));
    }
    
    private Method getProgressMonitorMethod = null;
    private IProgressMonitor getProgressMonitor(Job job) {
        if (job==null) {
            return new NullProgressMonitor();
        }
        try {
            if (getProgressMonitorMethod == null) {
                for (Method m : InternalJob.class.getDeclaredMethods()) {
                    if ("getProgressMonitor".equals(m.getName())) {
                        getProgressMonitorMethod = m;
                        getProgressMonitorMethod.setAccessible(true);
                        break;
                    }
                }
            }
            
            Object o = getProgressMonitorMethod.invoke(job);
            if (o instanceof IProgressMonitor) {
                return (IProgressMonitor) o;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    
    private boolean endsWith(char [] charArray, char[][] suffixes) {
        int arrayLength = charArray.length;
        for (char[] suffix : suffixes) {
            int suffixLength = suffix.length;
            if (arrayLength >= suffixLength) {
                if (CharOperation.fragmentEquals(suffix, charArray, arrayLength - suffixLength, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public IType findTypeInNameLookup(String typeName, String packageName) {
        JavaElementRequestor packageRequestor = new JavaElementRequestor();
        nameLookup.seekPackageFragments(packageName, false, packageRequestor);
        LinkedList<IPackageFragment> packagesToSearchIn = new LinkedList<>();
        
        for (IPackageFragment pf : packageRequestor.getPackageFragments()) {
            IPackageFragmentRoot packageRoot = (IPackageFragmentRoot) pf.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
            try {
                IJavaProject packageProject = packageRoot.getJavaProject();
                if (packageProject != null 
                        && !CeylonNature.isEnabled(packageProject.getProject())) {
                    continue;
                }
                if (packageRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    packagesToSearchIn.addFirst(pf);
                    continue;
                }
                if (isInCeylonClassesOutputFolder(packageRoot.getPath())) {
                    continue;
                }
                packagesToSearchIn.addLast(pf);
            } catch (JavaModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        for (IPackageFragment pf : packagesToSearchIn) {

            // We use considerSecondTypes = false because we will do it explicitly afterwards, in order to use waitForIndexes=true
            IType type = nameLookup.findType(typeName, pf, false, NameLookup.ACCEPT_ALL);
            if (type != null) {
                return type;
            }
        }
        
        char[] typeNameCharArray = typeName.toCharArray();
        if (CharOperation.equals(TypeConstants.PACKAGE_INFO_NAME, typeNameCharArray) ||
                CharOperation.equals(LookupEnvironmentUtilities.packageDescriptorName, typeNameCharArray) ||
                CharOperation.equals(LookupEnvironmentUtilities.moduleDescriptorName, typeNameCharArray) ||
                CharOperation.equals(LookupEnvironmentUtilities.oldPackageDescriptorName, typeNameCharArray) ||
                CharOperation.equals(LookupEnvironmentUtilities.oldModuleDescriptorName, typeNameCharArray) ||
                endsWith(typeNameCharArray, LookupEnvironmentUtilities.descriptorClassNames)) {
            // Don't search for secondary types whose name ends with is a quoted of unquoted descriptors
            // or ends with a quoted descriptor (in case it would be searching for an inner class)
            return null;
        }
        
        if (LookupEnvironmentUtilities.isSettingInterfaceCompanionClass() && typeName.endsWith("$impl")) {
            // Don't search for Ceylon interface companion classes in Java Secondary types.
            return null;
        }
        
        Job currentJob = Job.getJobManager().currentJob();
        IProgressMonitor currentMonitor = getProgressMonitor(currentJob);
        for (IPackageFragment pf : packagesToSearchIn) {
            IType type = findSecondaryType(typeName, packageName, pf,
                    currentMonitor);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    // This is a Copy / Paste from :
    // org.eclipse.jdt.internal.core.NameLookup.findSecondaryType(...), in order to be able to call it with waitForIndexes = true:
    // type = nameLookup.findSecondaryType(pf.getElementName(), typeName, pf.getJavaProject(), true, null);
    //
    // However the copied method has been changed to adapt it to the model loader needs.
    private IType findSecondaryType(String typeName, String packageName,
            IPackageFragment pf, IProgressMonitor currentMonitor) {
        JavaModelManager manager = JavaModelManager.getJavaModelManager();
        try {
            IJavaProject javaProject = pf.getJavaProject();
            @SuppressWarnings("rawtypes")
            Map secondaryTypePaths = manager.secondaryTypes(javaProject, true, currentMonitor);
            if (secondaryTypePaths.size() > 0) {
                @SuppressWarnings("rawtypes")
                Map types = (Map) secondaryTypePaths.get(packageName==null?"":packageName); //$NON-NLS-1$
                if (types != null && types.size() > 0) {
                    boolean startsWithDollar = false;
                    if(typeName.startsWith("$")) {
                        startsWithDollar = true;
                        typeName = typeName.substring(1);
                    }
                    String[] parts = typeName.split("(\\.|\\$)");
                    if (startsWithDollar) {
                        parts[0] = "$" + parts[0];
                    }
                    int index = 0;
                    String topLevelClassName = parts[index++];
                    IType currentClass = (IType) types.get(topLevelClassName);
                    IType result = currentClass;
                    while (index < parts.length) {
                        result = null;
                        String nestedClassName = parts[index++];
                        if (currentClass != null && currentClass.exists()) {
                            currentClass = currentClass.getType(nestedClassName);
                            result = currentClass;
                        } else {
                            break;
                        }
                    }
                    return result;
                }
            }
        }
        catch (JavaModelException jme) {
            // give up
        }
        return null;
    }
    
    private static Constructor<NameEnvironmentAnswer> nameEnvironmentAnswerFromSoureTypesConstructor = null;
    private static boolean hasAdditionalStringParameter = false;
    
    private static void loadNameEnvironmentAnswerFromSoureTypesConstructor() throws NoSuchMethodException, SecurityException {
        if (nameEnvironmentAnswerFromSoureTypesConstructor == null) {
            Constructor<NameEnvironmentAnswer> c = null;
            try {
                c = NameEnvironmentAnswer.class.getConstructor((new ISourceType[0]).getClass(), AccessRestriction.class);
            } catch (NoSuchMethodException e) {
                c = NameEnvironmentAnswer.class.getConstructor((new ISourceType[0]).getClass(), AccessRestriction.class, String.class);
                hasAdditionalStringParameter = true;
            }
            c.setAccessible(true);
            nameEnvironmentAnswerFromSoureTypesConstructor = c;
        }
    }
    
    private static NameEnvironmentAnswer createNameEnvironmentAnswerFromSoureTypes(ISourceType[] sourceTypes) {
        try {
            loadNameEnvironmentAnswerFromSoureTypesConstructor();
            if (hasAdditionalStringParameter) {
                return nameEnvironmentAnswerFromSoureTypesConstructor.newInstance(sourceTypes, null, null);
            } else {
                return nameEnvironmentAnswerFromSoureTypesConstructor.newInstance(sourceTypes, null);
            }
        } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected NameEnvironmentAnswer find(String typeName, String packageName) {
        if (packageName == null)
            packageName = IPackageFragment.DEFAULT_PACKAGE_NAME;
        if (this.owner != null) {
            String source = this.owner.findSource(typeName, packageName);
            if (source != null) {
                ICompilationUnit cu = new BasicCompilationUnit(source.toCharArray(), 
                        CharOperation.splitOn('.', packageName.toCharArray()), 
                        typeName + org.eclipse.jdt.internal.core.util.Util.defaultJavaExtension());
                return new NameEnvironmentAnswer(cu, null);
            }
        }

        IType type = findTypeInNameLookup(typeName, packageName);
        
        if (type != null) {
            // construct name env answer
            if (type instanceof BinaryType) { // BinaryType
                try {
                    return new NameEnvironmentAnswer((IBinaryType) ((BinaryType) type).getElementInfo(), null);
                } catch (JavaModelException npe) {
                    // fall back to using owner
                }
            } else { //SourceType
                try {
                    // retrieve the requested type
                    SourceTypeElementInfo sourceType = (SourceTypeElementInfo)((SourceType) type).getElementInfo();
                    ISourceType topLevelType = sourceType;
                    while (topLevelType.getEnclosingType() != null) {
                        topLevelType = topLevelType.getEnclosingType();
                    }
                    // find all siblings (other types declared in same unit, since may be used for name resolution)
                    IType[] types = sourceType.getHandle().getCompilationUnit().getTypes();
                    ISourceType[] sourceTypes = new ISourceType[types.length];

                    // in the resulting collection, ensure the requested type is the first one
                    sourceTypes[0] = sourceType;
                    int length = types.length;
                    for (int i = 0, index = 1; i < length; i++) {
                        ISourceType otherType =
                            (ISourceType) ((JavaElement) types[i]).getElementInfo();
                        if (!otherType.equals(topLevelType) && index < length) // check that the index is in bounds (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=62861)
                            sourceTypes[index++] = otherType;
                    }
                    return createNameEnvironmentAnswerFromSoureTypes(sourceTypes);
                } catch (JavaModelException jme) {
                    if (jme.isDoesNotExist() && String.valueOf(TypeConstants.PACKAGE_INFO_NAME).equals(typeName)) {
                        // in case of package-info.java the type doesn't exist in the model,
                        // but the CU may still help in order to fetch package level annotations.
                        return new NameEnvironmentAnswer((ICompilationUnit)type.getParent(), null);
                    }
                    // no usable answer
                }
            }
        }
        return null;
    }
}