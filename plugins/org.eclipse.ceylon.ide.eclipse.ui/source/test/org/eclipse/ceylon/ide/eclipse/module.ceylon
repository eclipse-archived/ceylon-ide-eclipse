/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
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
