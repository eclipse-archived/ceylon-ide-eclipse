/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.java2ceylon;

public class Java2CeylonProxies {
    static CompletionJ2C completion;
    static CorrectJ2C correct;
    static EditorJ2C editor;
    static HoverJ2C hover;
    static ImportsJ2C imports;
    static RefactorJ2C refactor;
    static ModelJ2C model;
    static VfsJ2C vfs;
    static UtilJ2C util;
    static PlatformJ2C platform;

    static {
        completion = new org.eclipse.ceylon.ide.eclipse.code.complete.completionJ2C();
        correct = new org.eclipse.ceylon.ide.eclipse.code.correct.correctJ2C();
        editor = new org.eclipse.ceylon.ide.eclipse.code.editor.editorJ2C();
        hover = new org.eclipse.ceylon.ide.eclipse.code.hover.hoverJ2C();
        imports = new org.eclipse.ceylon.ide.eclipse.code.imports.importsJ2C();
        refactor = new org.eclipse.ceylon.ide.eclipse.code.refactor.refactorJ2C();
        model = new org.eclipse.ceylon.ide.eclipse.core.model.modelJ2C();
        vfs = new org.eclipse.ceylon.ide.eclipse.core.vfs.vfsJ2C();
        util = new org.eclipse.ceylon.ide.eclipse.util.utilJ2C();
        platform = new org.eclipse.ceylon.ide.eclipse.platform.platformJ2C();
    }
    
    public static CompletionJ2C completionJ2C() { return completion; }
    public static CorrectJ2C correctJ2C() { return correct; }
    public static EditorJ2C editorJ2C() { return editor; }
    public static HoverJ2C hoverJ2C() { return hover; }
    public static ImportsJ2C importsJ2C() { return imports; }
    public static RefactorJ2C refactorJ2C() { return refactor; }
    public static ModelJ2C modelJ2C() { return model; }
    public static VfsJ2C vfsJ2C() { return vfs; }
    public static UtilJ2C utilJ2C() { return util; }
    public static PlatformJ2C platformJ2C() { return platform; }
}
