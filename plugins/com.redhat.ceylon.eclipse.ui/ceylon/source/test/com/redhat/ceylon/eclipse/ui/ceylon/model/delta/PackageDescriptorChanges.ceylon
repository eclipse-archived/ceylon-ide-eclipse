import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    buildDeltas,
    DeclarationMemberAdded,
    removed,
    NodeComparisonListener,
    TopLevelDeclarationAdded,
    MadeVisibleOutsideScope,
    madeInvisibleOutsideScope,
    madeVisibleOutsideScope,
    structuralChange
}
import ceylon.test {
    test,
    assertEquals
}
import ceylon.collection {
    HashSet
}
import com.redhat.ceylon.compiler.typechecker.model {
    Declaration
}
import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    comparePhasedUnits,
    PackageDescriptorDeltaMockup
}

test void sharePackage() {
    comparePhasedUnits {
        path = "dir/package.ceylon";
        oldContents = 
                "package dir;
                 ";
        newContents =
                "shared package dir;
                 ";
        expectedDelta = 
            PackageDescriptorDeltaMockup {
                changedElementString = "Package[dir]";
                changes = [ madeVisibleOutsideScope ];
            };
    };
}

test void unsharePackage() {
    comparePhasedUnits {
        path = "dir/package.ceylon";
        oldContents = 
                "shared package dir;
                 ";
        newContents =
                "package dir;
                 ";
        expectedDelta = 
                PackageDescriptorDeltaMockup {
            changedElementString = "Package[dir]";
            changes = [ madeInvisibleOutsideScope ];
        };
    };
}

test void changePackageName() {
    comparePhasedUnits {
        path = "dir/package.ceylon";
        oldContents = 
                "shared package dir;
                 ";
        newContents =
                "shared package dir2;
                 ";
        expectedDelta = 
                PackageDescriptorDeltaMockup {
            changedElementString = "Package[dir]";
            changes = [ structuralChange ];
        };
    };

    comparePhasedUnits {
        path = "dir/package.ceylon";
        oldContents = 
                "package dir;
                 ";
        newContents =
                "shared package dir2;
                 ";
        expectedDelta = 
                PackageDescriptorDeltaMockup {
            changedElementString = "Package[dir]";
            changes = [ structuralChange ];
        };
    };
}

test void noChangesInPackage() {
    comparePhasedUnits {
        path = "dir/package.ceylon";
        oldContents = 
                "shared package dir;
                 ";
        newContents =
                "shared package dir;
                 ";
        expectedDelta = 
                PackageDescriptorDeltaMockup {
            changedElementString = "Package[dir]";
            changes = [ ];
        };
    };
}

