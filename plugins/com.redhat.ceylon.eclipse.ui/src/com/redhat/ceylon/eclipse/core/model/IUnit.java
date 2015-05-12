package com.redhat.ceylon.eclipse.core.model;
import com.redhat.ceylon.model.typechecker.model.Package;
public interface IUnit {
    JDTModule getModule();
    Package getPackage();
}
