native("jvm") module mainModule "1.0.0" {
    import binary_only_external_module "1.0.0";
    import source_and_binary_external_module "1.0.0";
    import referencedCeylonProject "1.0.0";
    import java.logging "7";
    import java.base "7";
    import usedModule "1.0.0";
}
