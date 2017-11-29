/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.jdt.core {
    JavaModelException
}

"Returns the result of the [[do]] action or [[null]] if a JavaModelException occured."
shared Return? withJavaModel<Return>(Return do() , void onException(JavaModelException e) => e.printStackTrace()  ) {
    try {
        return do();
    } catch (JavaModelException e) {
        onException(e);
        return null;
    }
}