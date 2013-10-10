package com.redhat.ceylon.test.eclipse.plugin.runner;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import ceylon.test.AssertionComparisonException;
import ceylon.test.TestDescription;
import ceylon.test.TestListener;
import ceylon.test.TestListener$impl;
import ceylon.test.TestResult;
import ceylon.test.TestRunResult;
import ceylon.test.TestState;
import ceylon.test.error_;
import ceylon.test.failure_;
import ceylon.test.ignored_;
import ceylon.test.success_;

import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;
import com.redhat.ceylon.test.eclipse.plugin.runner.RemoteTestEvent.Type;

public class RemoteTestEventPublisher implements TestListener {

    private final ObjectOutputStream oos;

    public RemoteTestEventPublisher(ObjectOutputStream oos) {
        this.oos = oos;
    }

    @Override
    public Object testRunStart(TestDescription description) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_RUN_STARTED);
        event.setTestElement(convertTestDescription(description, true));
        publishEvent(event);
        return null;
    }

    @Override
    public Object testRunFinish(TestRunResult result) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_RUN_FINISHED);
        publishEvent(event);
        return null;
    }

    @Override
    public Object testStart(TestDescription description) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_STARTED);
        TestElement element = convertTestDescription(description, false);
        element.setState(State.RUNNING);
        event.setTestElement(element);
        publishEvent(event);
        return null;
    }

    @Override
    public Object testFinish(TestResult result) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_FINISHED);
        event.setTestElement(convertTestResult(result));
        publishEvent(event);
        return null;
    }

    @Override
    public Object testError(TestResult result) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_FINISHED);
        event.setTestElement(convertTestResult(result));
        publishEvent(event);
        return null;
    }

    @Override
    public Object testIgnored(TestResult result) {
        RemoteTestEvent event = new RemoteTestEvent();
        event.setType(Type.TEST_FINISHED);
        event.setTestElement(convertTestResult(result));
        publishEvent(event);
        return null;
    }

    @Override
    public TestListener$impl $ceylon$test$TestListener$impl() {
        return null;
    }

    private TestElement convertTestDescription(TestDescription description, boolean recursively) {
        TestElement testElement = new TestElement();
        testElement.setQualifiedName(description.getName());
        testElement.setState(State.UNDEFINED);

        if (recursively) {
            List<TestElement> children = new ArrayList<TestElement>();

            ceylon.language.Iterator<?> childrenIterator = description.getChildren().iterator();
            Object next;
            do {
                next = childrenIterator.next();
                if (next instanceof TestDescription) {
                    children.add(convertTestDescription((TestDescription) next, true));
                }
            } while (!(next instanceof ceylon.language.Finished));

            if (!children.isEmpty()) {
                testElement.setChildren(children.toArray(new TestElement[] {}));
            }
        }

        return testElement;
    }

    private TestElement convertTestResult(TestResult result) {
        TestElement testElement = new TestElement();
        testElement.setQualifiedName(result.getDescription().getName());
        testElement.setState(convertTestState(result.getState()));
        testElement.setException(convertThrowable(result.getException()));
        testElement.setElapsedTimeInMilis(result.getElapsedTime());

        if (result.getException() instanceof AssertionComparisonException) {
            AssertionComparisonException ace = (AssertionComparisonException) result.getException();
            testElement.setExpectedValue(ace.getExpectedValue());
            testElement.setActualValue(ace.getActualValue());
        }

        return testElement;
    }

    private State convertTestState(TestState testState) {
        if (testState instanceof success_) {
            return State.SUCCESS;
        } else if (testState instanceof failure_) {
            return State.FAILURE;
        } else if (testState instanceof error_) {
            return State.ERROR;
        } else if (testState instanceof ignored_) {
            return State.IGNORED;
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

}