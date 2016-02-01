package com.redhat.ceylon.eclipse.core.typechecker;

import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class IdePhasedUnitUtils {

    public static boolean isCentralModelDeclaration(Declaration declaration) {
        return declaration == null ||
                IdePhasedUnitUtils.isCentralModelUnit(declaration.getUnit());
    }

    public static boolean isCentralModelUnit(Unit unit) {
        return ! (unit instanceof CeylonUnit) ||
                    unit instanceof ProjectSourceFile ||
                    !(unit.getPackage() instanceof SingleSourceUnitPackage);
    }

}
