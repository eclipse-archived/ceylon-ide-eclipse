/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
"Module that allows developping the Ceylon IDE in Ceylon"
by("David Festal")
native("jvm")
module org.eclipse.ceylon.ide.eclipse "1.3.4" {
    shared import org.eclipse.ceylon.model "1.3.4-SNAPSHOT";
    shared import org.eclipse.ceylon.typechecker "1.3.4-SNAPSHOT";
    shared import org.eclipse.ceylon.compiler.java "1.3.4-SNAPSHOT";
    shared import org.eclipse.ceylon.compiler.js "1.3.4-SNAPSHOT";
    shared import "org.eclipse.ceylon.module-resolver" "1.3.4-SNAPSHOT";
    shared import ceylon.runtime  "1.3.4-SNAPSHOT";
    shared import ceylon.bootstrap  "1.3.4-SNAPSHOT";
    shared import ceylon.collection "1.3.4-SNAPSHOT";
    shared import ceylon.formatter "1.3.4-SNAPSHOT";
    shared import ceylon.interop.java "1.3.4-SNAPSHOT";
    shared import java.base "7";
    shared import java.compiler "7";
    shared import javax.xml "7";
    shared import org.eclipse.ceylon.ide.common "1.3.4-SNAPSHOT";
    shared import org.eclipse.ceylon.ide.eclipseDependencies "1.3.4";
    shared import org.eclipse.ceylon.dist "1.3.4-SNAPSHOT";
    shared import org.tautua.markdownpapers.core "1.3.4";
    shared import com.github.rjeschke.txtmark "0.13";
    shared import zip4j "1.3.2";
    shared import "org.antlr.antlr4-runtime-osgi" "4.5.1";
    shared import ceylon.tool.converter.java2ceylon "1.3.4-SNAPSHOT";
    shared import org.eclipse.ceylon.ide.eclipse.ui.jdt.debug.fragment "current";
    shared import org.antlr.runtime "3.5.2";
}
