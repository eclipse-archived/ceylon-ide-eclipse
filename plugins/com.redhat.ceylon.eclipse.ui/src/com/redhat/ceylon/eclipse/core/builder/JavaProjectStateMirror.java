package com.redhat.ceylon.eclipse.core.builder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.builder.ClasspathLocation;
import org.eclipse.jdt.internal.core.builder.ClasspathMultiDirectory;
import org.eclipse.jdt.internal.core.builder.State;

public class JavaProjectStateMirror extends CompilationParticipant {
    private final static Map<IProject, State> javaProjectCurrentStates = new HashMap<IProject, State>();
    private final static Map<IProject, State> javaProjectLastStates = new HashMap<IProject, State>();

    @Override
    public boolean isActive(IJavaProject javaProject) {
        if (! javaProject.getProject().isAccessible()) {
            return false;
        }
        return CeylonNature.isEnabled(javaProject.getProject());
    }

    @Override
    public int aboutToBuild(IJavaProject javaProject) {
        javaProjectLastStates.put(javaProject.getProject(), (State)JavaModelManager.getJavaModelManager().getLastBuiltState(javaProject.getProject(), null));
        return READY_FOR_BUILD;
    }

    @Override
    public void buildFinished(IJavaProject javaProject) {
        javaProjectCurrentStates.put(javaProject.getProject(), (State)JavaModelManager.getJavaModelManager().getLastBuiltState(javaProject.getProject(), null));
    }

    public static State getProjectLastState(IProject project) {
        return javaProjectLastStates.get(project);
    }

    public static State getProjectCurrentState(IProject project) {
        return javaProjectCurrentStates.get(project);
    }


    public static void cleanup(IProject project) {
        javaProjectLastStates.remove(project);
        javaProjectCurrentStates.remove(project);
    }
    
    static boolean isSourceFolderEmpty(State state, IContainer sourceFolder) {
        String sourceFolderName = sourceFolder.getProjectRelativePath().addTrailingSeparator().toString();
        Object[] table = state.typeLocators.valueTable;
        for (int i = 0, l = table.length; i < l; i++)
            if (table[i] != null && ((String) table[i]).startsWith(sourceFolderName))
                return false;
        return true;
    }
    

    private static IContainer getSourceFolder(ClasspathMultiDirectory classpathMultiDirectory) {
        try {
            Field field = classpathMultiDirectory.getClass().getDeclaredField("sourceFolder");
            field.setAccessible(true);
            return (IContainer) field.get(classpathMultiDirectory);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }

    private static ClasspathLocation[] getBinaryLocations(State state) {
        try {
            Field field = state.getClass().getDeclaredField("binaryLocations");
            field.setAccessible(true);
            return (ClasspathLocation[]) field.get(state);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }
    
    public static boolean hasClasspathChanged(IProject project) {
        State currentState = getProjectCurrentState(project);        
        State lastState = getProjectLastState(project);
        if (lastState == null || currentState == null) {
            return true;
        }
        ClasspathMultiDirectory[] newSourceLocations = currentState.sourceLocations;
        ClasspathMultiDirectory[] oldSourceLocations = lastState.sourceLocations;
        int newLength = newSourceLocations.length;
        int oldLength = oldSourceLocations.length;
        int n, o;
        for (n = o = 0; n < newLength && o < oldLength; n++, o++) {
            if (newSourceLocations[n].equals(oldSourceLocations[o])) continue; // checks source & output folders
            try {
                if (getSourceFolder(newSourceLocations[n]).members().length == 0) { // added new empty source folder
                    o--;
                    continue;
                } else if (isSourceFolderEmpty(lastState, getSourceFolder(oldSourceLocations[o]))) {
                    n--;
                    continue;
                }
            } catch (CoreException ignore) { // skip it
            }
            return true;
        }
        while (n < newLength) {
            try {
                if (getSourceFolder(newSourceLocations[n]).members().length == 0) { // added new empty source folder
                    n++;
                    continue;
                }
            } catch (CoreException ignore) {
                // skip it
            }
            return true;
        }
        while (o < oldLength) {
            if (isSourceFolderEmpty(lastState, getSourceFolder(oldSourceLocations[o]))) {
                o++;
                continue;
            }
            return true;
        }

        ClasspathLocation[] newBinaryLocations = getBinaryLocations(currentState);
        ClasspathLocation[] oldBinaryLocations = getBinaryLocations(lastState);
        newLength = newBinaryLocations.length;
        oldLength = oldBinaryLocations.length;
        for (n = o = 0; n < newLength && o < oldLength; n++, o++) {
            if (newBinaryLocations[n].toString().equals(oldBinaryLocations[o].toString())) continue;
            return true;
        }
        if (n < newLength || o < oldLength) {
            return true;
        }
        return false;
    }
}