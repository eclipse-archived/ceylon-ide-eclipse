package com.redhat.ceylon.test.eclipse.plugin.runner;

import java.io.Serializable;
import java.util.List;

import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;

public class RemoteTestEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum Type {

        TEST_RUN_STARTED,
        TEST_RUN_FINISHED,
        TEST_STARTED,
        TEST_FINISHED

    }

    private Type type;
    private TestElement testElement;
    private List<TestElement> testElements;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TestElement getTestElement() {
        return testElement;
    }

    public void setTestElement(TestElement testElement) {
        this.testElement = testElement;
    }

    public List<TestElement> getTestElements() {
        return testElements;
    }

    public void setTestElements(List<TestElement> testElements) {
        this.testElements = testElements;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestRunnerEvent");
        builder.append("[");
        builder.append("type=").append(type).append(", ");
        if (testElement != null) {
            builder.append("testElement=").append(testElement);
        }
        if (testElements != null) {
            builder.append("testElements=").append(testElements);
        }
        builder.append("]");
        return builder.toString();
    }

}
