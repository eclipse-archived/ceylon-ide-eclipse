/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package referencedCeylonProject;

import org.eclipse.ceylon.compiler.java.metadata.Ceylon;
import org.eclipse.ceylon.compiler.java.metadata.Object;

@Ceylon(major = 8) 
@Object
public class javaCeylonTopLevelObject_Referenced_Ceylon_Project_ {
    private final static javaCeylonTopLevelObject_Referenced_Ceylon_Project_ value = new javaCeylonTopLevelObject_Referenced_Ceylon_Project_();

    public static javaCeylonTopLevelObject_Referenced_Ceylon_Project_ get_(){
        return value;
    }
}
