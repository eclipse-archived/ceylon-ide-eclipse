package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.ITypeRoot;

public interface IJavaModelAware {
    ITypeRoot getJavaElement();
}
