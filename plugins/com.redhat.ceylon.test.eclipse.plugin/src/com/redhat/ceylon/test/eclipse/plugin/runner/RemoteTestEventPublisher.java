package com.redhat.ceylon.test.eclipse.plugin.runner;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import ceylon.test.AssertComparisonException;
import ceylon.test.TestListener;
import ceylon.test.TestListener$impl;
import ceylon.test.TestResult;
import ceylon.test.TestRunner;
import ceylon.test.TestState;
import ceylon.test.TestUnit;
import ceylon.test.error_;
import ceylon.test.failure_;
import ceylon.test.running_;
import ceylon.test.success_;
import ceylon.test.undefined_;

import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;
import com.redhat.ceylon.test.eclipse.plugin.runner.RemoteTestEvent.Type;

public class RemoteTestEventPublisher implements TestListener {

    private final ObjectOutputStream oos;

    public RemoteTestEventPublisher(ObjectOutputStream oos) {
        this.oos = oos;
    }

    @Override
    public Object testRunStarted(TestRunner testRunner) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_RUN_STARTED);
        event.setTestElements(convertTestUnits(testRunner));
        publishEvent(event);   
        return null;
    }

    @Override
    public Object testRunFinished(TestRunner testRunner, TestResult testResult) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_RUN_FINISHED);
        publishEvent(event);   
        return null;
    }

    @Override
    public Object testStarted(TestUnit testUnit) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_STARTED);
        event.setTestElement(convertTestUnit(testUnit));
        publishEvent(event);        
        return null;
    }

    @Override
    public Object testFinished(TestUnit testUnit) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_FINISHED);
        event.setTestElement(convertTestUnit(testUnit));
        publishEvent(event);
        return null;
    }
    
    private List<TestElement> convertTestUnits(TestRunner testRunner) {
        List<TestElement> testElements = new ArrayList<TestElement>();

        ceylon.language.List<?> testUnits = testRunner.getTests();
        ceylon.language.Iterator<?> testUnitsIterator = testUnits.getIterator();

        Object next;
        do {
            next = testUnitsIterator.next();
            if (next instanceof TestUnit) {
                testElements.add(convertTestUnit((TestUnit) next));
            }
        } while (!(next instanceof ceylon.language.Finished));

        return testElements;
    }

    private TestElement convertTestUnit(TestUnit testUnit) {
        TestElement testElement = new TestElement();
        testElement.setQualifiedName(testUnit.getName());
        testElement.setState(convertTestState(testUnit.getState()));
        testElement.setException(convertThrowable(testUnit.getException()));
        testElement.setElapsedTimeInMilis(testUnit.getElapsedTimeInMilis());
        
        if (testUnit.getException() instanceof AssertComparisonException) {
            AssertComparisonException ace = (AssertComparisonException) testUnit.getException();
            testElement.setExpectedValue(ace.getExpectedValue());
            testElement.setActualValue(ace.getActualValue());
        }
        
        return testElement;
    }

    private State convertTestState(TestState testState) {
        if (testState instanceof undefined_) {
            return State.UNDEFINED;
        } else if (testState instanceof running_) {
            return State.RUNNING;
        } else if (testState instanceof success_) {
            return State.SUCCESS;
        } else if (testState instanceof failure_) {
            return State.FAILURE;
        } else if (testState instanceof error_) {
            return State.ERROR;
        } else {
            return null;
        }
    }

    private String convertThrowable(Throwable t) {
        if (t != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            return sw.toString();
        }
        return null;
    }

    private void publishEvent(RemoteTestEvent event) {
        try {
            oos.writeObject(event);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	@Ignore
	public TestListener$impl $ceylon$test$TestListener$impl() {
		// no need for that
		return null;
	}

}