"Module that allows developping the Ceylon IDE in Ceylon"
by("David Festal")
native("jvm")
module com.redhat.ceylon.eclipse "1.3.3" {
    shared import com.redhat.ceylon.model "1.3.3-SNAPSHOT";
    shared import com.redhat.ceylon.typechecker "1.3.3-SNAPSHOT";
    shared import com.redhat.ceylon.compiler.java "1.3.3-SNAPSHOT";
    shared import com.redhat.ceylon.compiler.js "1.3.3-SNAPSHOT";
    shared import "com.redhat.ceylon.module-resolver" "1.3.3-SNAPSHOT";
    shared import ceylon.runtime  "1.3.3-SNAPSHOT";
    shared import ceylon.bootstrap  "1.3.3-SNAPSHOT";
    shared import ceylon.collection "1.3.3-SNAPSHOT";
    shared import ceylon.formatter "1.3.3-SNAPSHOT";
    shared import ceylon.interop.java "1.3.3-SNAPSHOT";
    shared import java.base "7";
    shared import java.compiler "7";
    shared import javax.xml "7";
    shared import com.redhat.ceylon.ide.common "1.3.3-SNAPSHOT";
    shared import com.redhat.ceylon.eclipseDependencies "1.3.3";
    shared import com.redhat.ceylon.dist "1.3.3-SNAPSHOT";
    shared import org.tautua.markdownpapers.core "1.2.7";
    shared import com.github.rjeschke.txtmark "0.13";
    shared import zip4j "1.3.2";
    shared import "org.antlr.antlr4-runtime-osgi" "4.5.1";
    shared import ceylon.tool.converter.java2ceylon "1.3.3-SNAPSHOT";
    shared import com.redhat.ceylon.eclipse.ui.jdt.debug.fragment "current";
    shared import org.antlr.runtime "3.4";
}
