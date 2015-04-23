"Module that allows developping the Ceylon IDE in Ceylon"
by("David Festal")
module com.redhat.ceylon.eclipse "1.1.1" {
    shared import com.redhat.ceylon.typechecker "1.1.1";
    shared import com.redhat.ceylon.common "1.1.1";
    shared import com.redhat.ceylon.compiler.java "1.1.1";
    shared import com.redhat.ceylon.compiler.js "1.1.1";
    shared import "com.redhat.ceylon.module-resolver" "1.1.1";
    shared import ceylon.runtime  "1.1.1";
    shared import ceylon.bootstrap  "1.1.1";
    shared import ceylon.collection "1.1.1";
    shared import ceylon.formatter "1.1.1";
    shared import ceylon.interop.java "1.1.1";
    shared import java.base "7";
    shared import java.compiler "7";
    shared import javax.xml "7";
    shared import com.redhat.ceylon.ide.common "1.1.1";
    shared import com.redhat.ceylon.eclipseDependencies "1.1.1";
    shared import com.redhat.ceylon.dist "1.1.1";
    shared import org.tautua.markdownpapers.core "1.2.7";
    shared import com.github.rjeschke.txtmark "0.11";
    shared import jgrapht "0.8.3.jdk1_6";
    shared import zip4j "1.3.2";
    shared import com.redhat.ceylon.eclipse.ui.jdt.debug.fragment "current";
}
