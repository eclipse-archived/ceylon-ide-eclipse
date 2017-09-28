package org.eclipse.ceylon.ide.eclipse.core.model;
import org.eclipse.ceylon.model.typechecker.model.Package;
public interface IUnit {
    JDTModule getModule();
    Package getPackage();
}
