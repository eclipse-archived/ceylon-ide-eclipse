package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IResource;

import com.redhat.ceylon.compiler.typechecker.model.Unit;

public abstract class IdeUnit extends Unit {
    public abstract IResource getResource();
}
