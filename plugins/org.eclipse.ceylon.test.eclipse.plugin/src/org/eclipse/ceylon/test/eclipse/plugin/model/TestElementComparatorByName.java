/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.model;

import java.text.Collator;
import java.util.Comparator;

public class TestElementComparatorByName implements Comparator<TestElement> {

    public static final TestElementComparatorByName INSTANCE = new TestElementComparatorByName();

    private final Collator collator = Collator.getInstance();

    @Override
    public int compare(TestElement testElement1, TestElement testElement2) {
        if (testElement1 == null && testElement2 != null) {
            return 1;
        }
        if (testElement1 != null && testElement2 == null) {
            return -1;
        }
        if (testElement1 == null && testElement2 == null) {
            return 0;
        }

        String name1 = testElement1.getQualifiedName();
        String name2 = testElement2.getQualifiedName();

        int result = collator.compare(name1, name2);
        if( result == 0 ) {
            if( testElement1.getVariantIndex() != null && testElement2.getVariantIndex() != null ) {
                result = testElement1.getVariantIndex().compareTo(testElement2.getVariantIndex());
            }
        }
        
        return result;
    }

}