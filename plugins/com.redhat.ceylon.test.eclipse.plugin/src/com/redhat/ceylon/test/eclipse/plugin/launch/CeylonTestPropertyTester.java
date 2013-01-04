package com.redhat.ceylon.test.eclipse.plugin.launch;

import org.eclipse.core.expressions.PropertyTester;

public class CeylonTestPropertyTester extends PropertyTester {

    private static final String CAN_LAUNCH_AS_CEYLON_TEST_PROPERTY = "canLaunchAsCeylonTest";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (CAN_LAUNCH_AS_CEYLON_TEST_PROPERTY.equals(property)) {
            // TODO test that receiver is testable method/class
            return true;
        }
        return false;
    }

}