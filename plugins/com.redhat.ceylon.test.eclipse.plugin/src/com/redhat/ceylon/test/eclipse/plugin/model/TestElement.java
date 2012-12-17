package com.redhat.ceylon.test.eclipse.plugin.model;

import java.io.Serializable;

public class TestElement implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum State {

        UNDEFINED,
        RUNNING,
        SUCCESS,
        FAILURE,
        ERROR

    }

    private String name;
    private State state;
    private String exception;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (name != null && obj instanceof TestElement) {
            return name.equals(((TestElement) obj).name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestElement");
        builder.append("[");
        builder.append("name=").append(name).append(", ");
        builder.append("state=").append(state);
        builder.append("]");
        return builder.toString();
    }

}