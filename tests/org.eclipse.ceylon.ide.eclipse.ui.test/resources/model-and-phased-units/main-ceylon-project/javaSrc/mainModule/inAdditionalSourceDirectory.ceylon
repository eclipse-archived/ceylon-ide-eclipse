/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import binary_only_external_module { CeylonTopLevelClass_External_Binary }
doc ("""
        Test forBug 563 : This source file is in the "javaSrc" source directory, that contains no module descriptor at all.
        Howover since the mainModule folder contains both a package descriptor and a module descriptor in the other source directory (src),
        this source file is also seen as belonging to the module and package named "mainModule".
       """)
void forBug563() {
    CeylonTopLevelClass_External_Binary();
}
