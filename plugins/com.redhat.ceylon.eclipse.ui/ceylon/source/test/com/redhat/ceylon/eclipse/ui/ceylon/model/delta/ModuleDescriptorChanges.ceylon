import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    ...
}
import ceylon.test {
    test
}

test void addUnsharedModuleImport() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "
                 module dir \"1.0.0\" {}
                 ";
        newContents =
                "
                 module dir \"1.0.0\" {
                     import imported \"2.0.0\";
                 }
                 ";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [ ModuleImportAdded("imported", "2.0.0", invisibleOutside) ];
        };
    };
}

test void addSharedModuleImport() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "
                 module dir \"1.0.0\" {}
                 ";
        newContents =
                "
                 module dir \"1.0.0\" {
                     shared import imported \"2.0.0\";
                 }
                 ";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [ ModuleImportAdded("imported", "2.0.0", visibleOutside) ];
        };
    };
}

test void makeModuleImportShared() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "
                 module dir \"1.0.0\" {
                     import imported \"2.0.0\";
                 }
                 ";
        newContents =
                "
                 module dir \"1.0.0\" {
                     shared import imported \"2.0.0\";
                 }
                 ";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [];
            childrenDeltas = {
                ModuleImportDeltaMockup {
                    changedElementString = "ModuleImport[imported, 2.0.0]";
                    changes = [ madeVisibleOutsideScope ];
                }
            };
        };
    };
}

test void makeModuleImportUnshared() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "
                 module dir \"1.0.0\" {
                     shared import imported \"2.0.0\";
                 }
                 ";
        newContents =
                "
                 module dir \"1.0.0\" {
                     import imported \"2.0.0\";
                 }
                 ";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [];
            childrenDeltas = {
                ModuleImportDeltaMockup {
                    changedElementString = "ModuleImport[imported, 2.0.0]";
                    changes = [ madeInvisibleOutsideScope ];
                }
            };
        };
    };
}

test void makeModuleImportOptional() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "
                 module dir \"1.0.0\" {
                     import imported \"2.0.0\";
                 }
                 ";
        newContents =
                "
                 module dir \"1.0.0\" {
                     optional import imported \"2.0.0\";
                 }
                 ";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [];
            childrenDeltas = {
                ModuleImportDeltaMockup {
                    changedElementString = "ModuleImport[imported, 2.0.0]";
                    changes = [ structuralChange ];
                }
            };
        };
    };
}

test void makeModuleImportMandatory() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "
                 module dir \"1.0.0\" {
                     optional import imported \"2.0.0\";
                 }
                 ";
        newContents =
                "
                 module dir \"1.0.0\" {
                     import imported \"2.0.0\";
                 }
                 ";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [];
            childrenDeltas = {
                ModuleImportDeltaMockup {
                    changedElementString = "ModuleImport[imported, 2.0.0]";
                    changes = [ structuralChange ];
                }
            };
        };
    };
}

test void makeModuleImportNative() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
              """
                 module dir "1.0.0" {
                     import imported "2.0.0";
                 }
                 """;
        newContents =
                """
                   module dir "1.0.0" {
                     native("jvm") import imported "2.0.0";
                   }
                   """;
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [];
            childrenDeltas = {
                ModuleImportDeltaMockup {
                    changedElementString = "ModuleImport[imported, 2.0.0]";
                    changes = [ structuralChange ];
                }
            };
        };
    };
}

test void makeModuleImportNonNative() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                """
                   module dir "1.0.0" {
                     native("jvm") import imported "2.0.0";
                   }
                   """;
        newContents =
                """
                   module dir "1.0.0" {
                     import imported "2.0.0";
                   }
                   """;
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [];
            childrenDeltas = {
                ModuleImportDeltaMockup {
                    changedElementString = "ModuleImport[imported, 2.0.0]";
                    changes = [ structuralChange ];
                }
            };
        };
    };
}

test void changeModuleImportNativeBackend() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                """
                   module dir "1.0.0" {
                     native("jvm") import imported "2.0.0";
                   }
                   """;
        newContents =
                """
                   module dir "1.0.0" {
                     native("js") import imported "2.0.0";
                   }
                   """;
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [];
            childrenDeltas = {
                ModuleImportDeltaMockup {
                    changedElementString = "ModuleImport[imported, 2.0.0]";
                    changes = [ structuralChange ];
                }
            };
        };
    };
}

test void removeModuleImport() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "
                 module dir \"1.0.0\" {
                     import imported \"2.0.0\";
                 }
                 ";
        newContents =
                "
                 module dir \"1.0.0\" {}
                 ";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [];
            childrenDeltas = {
                ModuleImportDeltaMockup {
                    changedElementString = "ModuleImport[imported, 2.0.0]";
                    changes = [ removed ];
                }
            };
        };
    };
}


test void changeModuleName() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "module dir \"1.0.0\" {}";
        newContents =
                "module dir2 \"1.0.0\" {}";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [ structuralChange ];
        };
    };
}

test void changeModuleVersion() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "module dir \"1.0.0\" {}";
        newContents =
                "module dir \"1.0.1\" {}";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [ structuralChange ];
        };
    };
}

test void makeModuleNative() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                """
                   module dir "1.0.0" {
                   }
                   """;
        newContents =
                """
                   native ("jvm") module dir "1.0.0" {
                   }
                   """;
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [ structuralChange ];
        };
    };
}

test void makeModuleNonNative() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                """
                   native ("jvm") module dir "1.0.0" {
                   }
                   """;
        newContents =
                """
                   module dir "1.0.0" {
                   }
                   """;
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [ structuralChange ];
        };
    };
}

test void changeModuleNativeBackend() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                """
                   native ("jvm") module dir "1.0.0" {
                   }
                   """;
        newContents =
                """
                   native ("js") module dir "1.0.0" {
                   }
                   """;
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [ structuralChange ];
        };
    };
}

test void noChangesInModule() {
    comparePhasedUnits {
        path = "dir/module.ceylon";
        oldContents =
                "module dir \"1.0.0\" {}";
        newContents =
                "module dir \"1.0.0\" {}";
        expectedDelta =
                ModuleDescriptorDeltaMockup {
            changedElementString = "Module[dir, 1.0.0]";
            changes = [ ];
        };
    };
}

