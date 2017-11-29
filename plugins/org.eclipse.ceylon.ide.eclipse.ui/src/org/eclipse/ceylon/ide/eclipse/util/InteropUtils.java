/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

public class InteropUtils {
    public static String toJavaString(ceylon.language.String string) {
        return string==null ? null : string.toString();
    }
    public static Boolean toJavaBoolean(ceylon.language.Boolean bool) {
        return bool==null ? null : bool.booleanValue();
    }
    public static ceylon.language.String toCeylonString(String string) {
        return string==null ? null : ceylon.language.String.instance(string);
    }
    public static ceylon.language.Boolean toCeylonBoolean(Boolean bool) {
        return bool==null ? null : ceylon.language.Boolean.instance(bool);
    }
}
