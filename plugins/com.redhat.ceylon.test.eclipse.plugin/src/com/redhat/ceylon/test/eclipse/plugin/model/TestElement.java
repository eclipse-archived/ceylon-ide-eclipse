package com.redhat.ceylon.test.eclipse.plugin.model;

import java.io.Serializable;

public class TestElement implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum State {

        UNDEFINED(0),
        RUNNING(2),
        SUCCESS(1),
        FAILURE(3),
        ERROR(4);
        
        private final int priority;
        
        private State(int priority) {
            this.priority = priority;
        }
        
        public int getPriority() {
            return priority;            
        }
        
        public boolean isFinished() {
            return this == SUCCESS || this == FAILURE || this == ERROR;
        }

        public boolean isFailureOrError() {
            return this == FAILURE || this == ERROR;
        }

    }

    private String name;
    private String packageName;
    private String qualifiedName;
    private State state;
    private String exception;
    private long elapsedTimeInMilis;
    
    public String getName() {
        return name;
    }
    
    public String getPackageName() {
        return packageName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        
        int packageSeparatorIndex = qualifiedName.indexOf("::");
        if (packageSeparatorIndex != -1) {
            name = qualifiedName.substring(packageSeparatorIndex + 2);
            packageName = qualifiedName.substring(0, packageSeparatorIndex);
        } else {
            name = qualifiedName;
            packageName = "";
        }
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
    
    public long getElapsedTimeInMilis() {
        return elapsedTimeInMilis;
    }
    
    public void setElapsedTimeInMilis(long elapsedTimeInMilis) {
        this.elapsedTimeInMilis = elapsedTimeInMilis;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (qualifiedName != null && obj instanceof TestElement) {
            return qualifiedName.equals(((TestElement) obj).qualifiedName);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return qualifiedName != null ? qualifiedName.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestElement");
        builder.append("[");
        builder.append("name=").append(qualifiedName).append(", ");
        builder.append("state=").append(state);
        builder.append("]");
        return builder.toString();
    }

}