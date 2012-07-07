package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.core.resources.IProject;

class WithProject {
    Object element;
    IProject project;
    
    WithProject(Object element, IProject project) {
        this.element = element;
        this.project = project;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WithProject) {
            WithProject that = (WithProject) obj;
            return that.element.equals(element) &&
                    that.project.equals(project);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return element.hashCode();
    }
}
