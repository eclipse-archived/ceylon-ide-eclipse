"""Test module for module 
   [[com.redhat.ceylon.eclipse.ui|module com.redhat.ceylon.eclipse.ui]]."""
by("David Festal")
native("jvm")
module test.com.redhat.ceylon.eclipse.ui "1.1.1" {
    import com.redhat.ceylon.eclipse.ui "1.1.1";
    import "com.redhat.ceylon.module-resolver" "1.1.1";
    import java.base "7";
    import ceylon.interop.java "1.1.1";
    import ceylon.test "1.1.1";
}
