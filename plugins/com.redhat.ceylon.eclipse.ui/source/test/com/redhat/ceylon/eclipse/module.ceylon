"""Test module for module
   [[com.redhat.ceylon.eclipse|module com.redhat.ceylon.eclipse]]."""
by("David Festal")
native("jvm")
module test.com.redhat.ceylon.eclipse "1.3.4" {
    import com.redhat.ceylon.eclipse "1.3.4";
    import "com.redhat.ceylon.module-resolver" "1.3.4-SNAPSHOT";
    import java.base "7";
    import ceylon.interop.java "1.3.4-SNAPSHOT";
    import ceylon.test "1.3.4-SNAPSHOT";
}
