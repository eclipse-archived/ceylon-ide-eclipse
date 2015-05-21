"Module that allows developping the Ceylon IDE in Ceylon"
by("David Festal")
native("jvm")
module com.redhat.ceylon.eclipse.ui "1.1.1" {
    shared import com.redhat.ceylon.typechecker "1.1.1";
    shared import com.redhat.ceylon.model "1.1.1";
    shared import ceylon.collection "1.1.1";
    import ceylon.interop.java "1.1.1";
    shared import java.base "7";
}
