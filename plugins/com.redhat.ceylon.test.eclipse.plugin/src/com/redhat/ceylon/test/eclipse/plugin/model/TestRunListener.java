package com.redhat.ceylon.test.eclipse.plugin.model;

public interface TestRunListener {
    
    void testRunAdded(TestRun testRun);
    
    void testRunRemoved(TestRun testRun);

}