/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.debug.core.ILaunch;

public class TestRunContainer {

    private static final int MAX_RUNS_COUNT = 10;

    private final List<TestRun> testRuns = new CopyOnWriteArrayList<TestRun>();
    private final List<TestRunListener> testRunListeners = new CopyOnWriteArrayList<TestRunListener>();

    public List<TestRunListener> getTestRunListeners() {
        return Collections.unmodifiableList(testRunListeners);
    }

    public void addTestRunListener(TestRunListener testRunListener) {
        testRunListeners.add(testRunListener);
    }

    public void removeTestRunListener(TestRunListener testRunListener) {
        testRunListeners.remove(testRunListener);
    }

    public List<TestRun> getTestRuns() {
        return Collections.unmodifiableList(testRuns);
    }

    public TestRun getTestRun(ILaunch launch) {
        TestRun result = null;

        for (TestRun testRun : testRuns) {
            if (testRun.getLaunch() == launch) {
                result = testRun;
                break;
            }
        }

        return result;
    }

    public TestRun getOrCreateTestRun(ILaunch launch) {
        TestRun result = getTestRun(launch);

        if (result == null) {
            result = new TestRun(launch);
            testRuns.add(0, result);
            fireTestRunAdded(result);

            if (testRuns.size() > MAX_RUNS_COUNT) {
                List<TestRun> obsoleteRuns = testRuns.subList(MAX_RUNS_COUNT, testRuns.size());
                for (TestRun obsoleteRun : obsoleteRuns) {
					if (!obsoleteRun.isRunning() && !obsoleteRun.isPinned()) {
						removeTestRun(obsoleteRun);
					}
                }
            }
        }

        return result;
    }

    public void removeTestRun(TestRun testRun) {
        if( testRun.isRunning() || testRun.isPinned() ) {
            throw new IllegalStateException();
        }
        testRuns.remove(testRun);
        fireTestRunRemoved(testRun);
    }

    private void fireTestRunAdded(TestRun testRun) {
        for (TestRunListener testRunListener : testRunListeners) {
            testRunListener.testRunAdded(testRun);
        }
    }

    private void fireTestRunRemoved(TestRun testRun) {
        for (TestRunListener testRunListener : testRunListeners) {
            testRunListener.testRunRemoved(testRun);
        }
    }

}