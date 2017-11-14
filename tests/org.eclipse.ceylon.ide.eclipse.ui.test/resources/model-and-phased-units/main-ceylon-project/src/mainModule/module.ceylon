/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
native("jvm") module mainModule "1.0.0" {
    import binary_only_external_module "1.0.0";
    import source_and_binary_external_module "1.0.0";
    import referencedCeylonProject "1.0.0";
    import java.logging "7";
    import java.base "7";
    import usedModule "1.0.0";
}
