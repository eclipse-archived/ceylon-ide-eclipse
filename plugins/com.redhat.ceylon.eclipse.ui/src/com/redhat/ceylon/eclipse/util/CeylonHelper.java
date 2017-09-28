package org.eclipse.ceylon.ide.eclipse.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.ceylon.compiler.java.runtime.model.TypeDescriptor;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import org.eclipse.ceylon.ide.common.vfs.ResourceVirtualFile;

import ceylon.language.Iterator;
import ceylon.language.Sequence;
import ceylon.language.finished_;

public class CeylonHelper {
    
    public static TypeDescriptor td(Class<?> klass) {
        TypeDescriptor[] typeArguments = new TypeDescriptor[0];
        
        if (ResourceVirtualFile.class.isAssignableFrom(klass) ||
                CeylonProject.class.equals(klass)) {
            typeArguments = new TypeDescriptor[] {
                    TypeDescriptor.klass(IProject.class),
                    TypeDescriptor.klass(IResource.class),
                    TypeDescriptor.klass(IFolder.class),
                    TypeDescriptor.klass(IFile.class)
            };
        }
        return TypeDescriptor.klass(klass, typeArguments);
    }
    
    public static <Type> List<Type> list(Class<Type> klass,  ceylon.language.Iterable<? extends Type, ? extends Object> ceylonIterable) {
        if (ceylonIterable == null) {
            return null;
        }
        ArrayList<Type> arrayList = new ArrayList<>();
        Iterator<?> iterator = ceylonIterable.iterator();
        Object elem;
        while ((elem = iterator.next())!=finished_.get_()) {
            arrayList.add((Type) elem);
        }
        return arrayList;
    }

    public static List<String> toJavaStringList(ceylon.language.Iterable<? extends ceylon.language.String, ? extends Object> ceylonIterable) {
        if (ceylonIterable == null) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator<?> iterator = ceylonIterable.iterator();
        Object elem;
        while ((elem = iterator.next())!=finished_.get_()) {
            arrayList.add(elem == null ? null : ((ceylon.language.String)elem).value);
        }
        return arrayList;
    }

    public static String[] toJavaStringArray(Sequence<? extends ceylon.language.String> sequence) {
        if (sequence == null) {
            return null;
        }
        long length = sequence.getSize();
        String[] result = new String[(int) length];
        for (int i=0; i<length; i++) {
            ceylon.language.String str = sequence.getFromFirst(i);
            result[i] = str==null ? null : str.value;
        }
        return result;
    }
}
