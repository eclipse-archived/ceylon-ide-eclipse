/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import java.util.logging {
    Logger {
        getLogger
    }
}

import referencedCeylonProject {
    CeylonTopLevelClass_Referenced_Ceylon_Project,
    ceylonTopLevelObject_Referenced_Ceylon_Project,
    ceylonTopLevelMethod_Referenced_Ceylon_Project,
    JavaCeylonTopLevelClass_Referenced_Ceylon_Project,
    javaCeylonTopLevelObject_Referenced_Ceylon_Project,
    javaCeylonTopLevelMethod_Referenced_Ceylon_Project,
    JavaClassInCeylonModule_Referenced_Ceylon_Project,
    CeylonTopLevelInterface_Referenced_Ceylon_Project
}

import source_and_binary_external_module {
    CeylonTopLevelClass_External_Source_Binary,
    ceylonTopLevelObject_External_Source_Binary,
    ceylonTopLevelMethod_External_Source_Binary
}

import usedModule {
    CeylonTopLevelClass_Main_Ceylon_Project,
    ceylonTopLevelObject_Main_Ceylon_Project,
    ceylonTopLevelMethod_Main_Ceylon_Project,
    CeylonTopLevelInterface_Main_Ceylon_Project
}

doc ("Run the module `mainModule`.")
shared void run() {
    value logger = getLogger("logger");
    
    value v1 = CeylonTopLevelClass_Main_Ceylon_Project();
    v1.method(v1.attribute);
    v1.InnerClass(v1.obj);
    value v2 = ceylonTopLevelObject_Main_Ceylon_Project;
    ceylonTopLevelMethod_Main_Ceylon_Project();
    
    value v3 = JavaCeylonTopLevelClass_Main_Ceylon_Project();
    value v4 = javaCeylonTopLevelObject_Main_Ceylon_Project;
    javaCeylonTopLevelMethod_Main_Ceylon_Project();
    value v5 = JavaClassInCeylonModule_Main_Ceylon_Project();
    value v5_1 = JavaSecondaryClassInCeylonModule_Main_Ceylon_Project();
    
    value v6 = CeylonTopLevelClass_External_Source_Binary();
    v6.static();
    value v7 = ceylonTopLevelObject_External_Source_Binary;
    ceylonTopLevelMethod_External_Source_Binary();
    
    value v8 = CeylonTopLevelClass_Referenced_Ceylon_Project();
    v8.method(v8.attribute);
    v8.InnerClass(v8.obj);
    value v9 = ceylonTopLevelObject_Referenced_Ceylon_Project;
    ceylonTopLevelMethod_Referenced_Ceylon_Project();
    
    value v10 = JavaCeylonTopLevelClass_Referenced_Ceylon_Project();
    value v11 = javaCeylonTopLevelObject_Referenced_Ceylon_Project;
    javaCeylonTopLevelMethod_Referenced_Ceylon_Project();
    value v12 = JavaClassInCeylonModule_Referenced_Ceylon_Project();

    value v13 = object satisfies CeylonTopLevelInterface_Main_Ceylon_Project {};
    v13.interfaceMethod(v13.interfaceAttribute);
    v13.InterfaceClass(v13);
    
    value v14 = object satisfies CeylonTopLevelInterface_Referenced_Ceylon_Project {};
    v14.interfaceMethod(v14.interfaceAttribute);
    v14.InterfaceClass(v14);
    
}
