"""Test module for module
   [[org.eclipse.ceylon.ide.eclipse|module org.eclipse.ceylon.ide.eclipse]]."""
by("David Festal")
native("jvm")
module test.org.eclipse.ceylon.ide.eclipse "1.3.4" {
    import org.eclipse.ceylon.ide.eclipse "1.3.4";
    import "org.eclipse.ceylon.module-resolver" "1.3.4-SNAPSHOT";
    import java.base "7";
    import ceylon.interop.java "1.3.4-SNAPSHOT";
    import ceylon.test "1.3.4-SNAPSHOT";
}
