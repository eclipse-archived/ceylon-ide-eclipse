package org.eclipse.ceylon.test.eclipse.plugin.model;

public interface TestRunListener {

    void testRunAdded(TestRun testRun);

    void testRunRemoved(TestRun testRun);

    void testRunStarted(TestRun testRun);

    void testRunFinished(TestRun testRun);

    void testRunInterrupted(TestRun testRun);

    void testStarted(TestRun testRun, TestElement testElement);

    void testFinished(TestRun testRun, TestElement testElement);

}