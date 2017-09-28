package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.ceylon.ide.common.model.IResourceAware;
import org.eclipse.ceylon.ide.common.typechecker.ProjectPhasedUnit;

public interface ICrossProjectReference extends IResourceAware {
    ProjectSourceFile getOriginalSourceFile();
    ProjectPhasedUnit getOriginalPhasedUnit();
}
