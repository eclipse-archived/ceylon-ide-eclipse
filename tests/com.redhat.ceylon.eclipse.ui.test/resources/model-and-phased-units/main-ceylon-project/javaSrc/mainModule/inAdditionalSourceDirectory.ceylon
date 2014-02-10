import binary_only_external_module { CeylonTopLevelClass_External_Binary }
doc ("""
        Test forBug 563 : This source file is in the "javaSrc" source directory, that contains no module descriptor at all.
        Howover since the mainModule folder contains both a package descriptor and a module descriptor in the other source directory (src),
        this source file is also seen as belonging to the module and package named "mainModule".
       """)
void forBug563() {
    CeylonTopLevelClass_External_Binary();
}
