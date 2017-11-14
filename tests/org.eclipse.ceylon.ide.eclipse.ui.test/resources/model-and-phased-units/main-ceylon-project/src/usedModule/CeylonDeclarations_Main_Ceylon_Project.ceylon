/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
shared class CeylonTopLevelClass_Main_Ceylon_Project() {
	shared void method(String argument) {}
	shared String attribute => string;
	shared class InnerClass(Object o) {}
	shared object obj {}
}

shared interface CeylonTopLevelInterface_Main_Ceylon_Project {
    shared void interfaceMethod(String argument) {}
    shared String interfaceAttribute => string;
    shared class InterfaceClass(Object o) {}
}

shared class CeylonTopLevelInterfaceImplementation_Main_Ceylon_Project() 
        satisfies CeylonTopLevelInterface_Main_Ceylon_Project {
}

shared void ceylonTopLevelMethod_Main_Ceylon_Project() {
}

shared object ceylonTopLevelObject_Main_Ceylon_Project {
}