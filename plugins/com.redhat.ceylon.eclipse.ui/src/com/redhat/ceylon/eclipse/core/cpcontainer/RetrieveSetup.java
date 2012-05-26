/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.redhat.ceylon.eclipse.core.cpcontainer;

/**
 * This class is just a simple bean defining the properties which configure an IvyDE classpath
 * container.
 */
public class RetrieveSetup {

    private boolean doRetrieve;

    private String retrievePattern;

    private boolean retrieveSync = false;

    private String retrieveConfs = "*";

    private String retrieveTypes = "*";

    /**
     * Default constructor
     */
    public RetrieveSetup() {
        // default constructor
    }

    public void set(RetrieveSetup setup) {
        this.doRetrieve = setup.doRetrieve;
        this.retrievePattern = setup.retrievePattern;
        this.retrieveConfs = setup.retrieveConfs;
        this.retrieveTypes = setup.retrieveTypes;
    }

    public boolean isDoRetrieve() {
        return doRetrieve;
    }

    public void setDoRetrieve(boolean doRetrieve) {
        this.doRetrieve = doRetrieve;
    }

    public String getRetrievePattern() {
        return retrievePattern;
    }

    public void setRetrievePattern(String retrievePattern) {
        this.retrievePattern = retrievePattern;
    }

    public boolean isRetrieveSync() {
        return retrieveSync;
    }

    public void setRetrieveSync(boolean retrieveSync) {
        this.retrieveSync = retrieveSync;
    }

    public String getRetrieveConfs() {
        return retrieveConfs;
    }

    public void setRetrieveConfs(String retrieveConfs) {
        this.retrieveConfs = retrieveConfs;
    }

    public String getRetrieveTypes() {
        return retrieveTypes;
    }

    public void setRetrieveTypes(String retrieveTypes) {
        this.retrieveTypes = retrieveTypes;
    }

}
