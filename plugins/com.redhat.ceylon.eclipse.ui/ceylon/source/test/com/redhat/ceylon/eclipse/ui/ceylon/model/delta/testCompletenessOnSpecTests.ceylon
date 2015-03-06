import ceylon.interop.java {
    CeylonIterable
}

import com.redhat.ceylon.compiler.typechecker {
    TypeCheckerBuilder
}

import java.io {
    File
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    ModuleManager {
        moduleDescriptorFileName=MODULE_FILE,
        packageDescriptorFileName=PACKAGE_FILE
    }
}
import ceylon.test {
    test
}
import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    DeltaBuilderFactory
}


test
shared void testCompletenessOnSpecTests() {
    variable File? dir = File("").absoluteFile;
    variable File? specDir = null;
    while (exists existingDir = dir) {
        value triedDir = File(existingDir, "ceylon-spec");
        if (triedDir.\iexists()) {
            specDir = triedDir;
            break;
        }
        dir = dir?.parentFile;
    }
    "The ceylon-spec root directory is not found"
    assert (exists specRootDir = specDir);

    value typeChecker = TypeCheckerBuilder()
        .statistics(true)
        .verbose(false)
        .addSrcDirectory(File(specDir, "test/main"))
        .typeChecker;
    typeChecker.process();

    for (phasedUnit in CeylonIterable(typeChecker.phasedUnits.phasedUnits)) {
        assert (exists unit = phasedUnit.unit);
        assert(exists unitName = phasedUnit.unitFile?.name);
        compare {
            oldPhasedUnit = phasedUnit;
            newPhasedUnit = phasedUnit;
            expectedDelta = if (unitName == moduleDescriptorFileName) then ModuleDescriptorDeltaMockup(unit.string, [], [])
                            else if (unitName == packageDescriptorFileName) then PackageDescriptorDeltaMockup(unit.string, [])
                            else RegularCompilationUnitDeltaMockup(unit.string, [], []);
            printNodeComparisons = true;
            deltaBuilderFactory = DeltaBuilderFactory(true);
        };
    }
}

