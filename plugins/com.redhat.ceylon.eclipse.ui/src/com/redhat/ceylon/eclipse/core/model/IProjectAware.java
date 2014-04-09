package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IProject;

public interface IProjectAware extends IUnit {
    IProject getProject();
}
