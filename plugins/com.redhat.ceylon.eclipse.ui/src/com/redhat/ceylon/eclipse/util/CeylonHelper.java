package com.redhat.ceylon.eclipse.util;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.util.toJavaIterable_;
import com.redhat.ceylon.ide.common.util.toJavaList_;
import com.redhat.ceylon.ide.common.vfs.ResourceVirtualFile;

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
        return toJavaList_.toJavaList(td(klass), (ceylon.language.Iterable<? extends Type, ? extends Object>) ceylonIterable);
    }

    public static <Type> Iterable<Type> iterable(Class<Type> klass,  ceylon.language.Iterable<? extends Type, ? extends Object> ceylonIterable) {
        return toJavaIterable_.toJavaIterable(td(klass), (ceylon.language.Iterable<? extends Type, ? extends Object>) ceylonIterable);
    }
}
