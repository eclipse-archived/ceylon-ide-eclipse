package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;

public interface ICrossProjectReference extends IResourceAware {
    ProjectSourceFile getOriginalSourceFile();
    ProjectPhasedUnit getOriginalPhasedUnit();
}
