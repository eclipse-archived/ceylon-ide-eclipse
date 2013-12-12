package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;

public interface ICrossProjectReference extends IResourceAware {
    JDTModule getModule();
    CrossProjectPhasedUnit getPhasedUnit();
    ProjectSourceFile getOriginalSourceFile();
    ProjectPhasedUnit getOriginalPhasedUnit();
}
