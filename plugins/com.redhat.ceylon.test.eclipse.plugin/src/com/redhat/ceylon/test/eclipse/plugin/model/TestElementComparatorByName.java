package com.redhat.ceylon.test.eclipse.plugin.model;

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