package com.redhat.ceylon.test.eclipse.plugin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;
import com.redhat.ceylon.test.eclipse.plugin.runner.RemoteTestEvent;
import com.redhat.ceylon.test.eclipse.plugin.runner.RemoteTestEvent.Type;

public class TestRun {

    private static final Object NULL_LOCK = new Object();

    public static Object acquireLock(TestRun testRun) {
        return testRun != null ? testRun : NULL_LOCK;
    }

    private final Date startDate;
    private final ILaunch launch;
    private final List<TestElement> testElements = new ArrayList<TestElement>();
    private final Map<String, List<TestElement>> testElementsByPackages = new LinkedHashMap<String, List<TestElement>>();
    private boolean isRunning;
    private boolean isFinished;
    private boolean isInterrupted;
    private int startedCount = 0;
    private int successCount = 0;
    private int failureCount = 0;
    private int errorCount = 0;

    public TestRun(ILaunch launch) {
        this.launch = launch;
        this.startDate = new Date();
    }

    public ILaunch getLaunch() {
        return launch;
    }

    public List<TestElement> getTestElements() {
        return testElements;
    }

    public Map<String, List<TestElement>> getTestElementsByPackages() {
        return testElementsByPackages;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public boolean isSuccess() {
        return failureCount == 0 && errorCount == 0;
    }

    public boolean isFailureOrError() {
        return failureCount != 0 || errorCount != 0;
    }

    public int getTotalCount() {
        return testElements.size();
    }

    public int getStartedCount() {
        return startedCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getFinishedCount() {
        return successCount + failureCount + errorCount;
    }

    public State getPackageState(String packageName) {
        State result = State.UNDEFINED;

        List<TestElement> testElementsInPackage = testElementsByPackages.get(packageName);
        if (testElementsInPackage != null) {
            for (TestElement testElement : testElementsInPackage) {
                if (testElement.getState().getPriority() > result.getPriority()) {
                    result = testElement.getState();
                }
            }
        }

        return result;
    }

    public long getPackageElapsedTimeInMilis(String packageName) {
        long elapsedTimeInMilis = 0;

        List<TestElement> testElementsInPackage = testElementsByPackages.get(packageName);
        if (testElementsInPackage != null) {
            for (TestElement testElement : testElementsInPackage) {
                if (testElement.getState().isFinished()) {
                    elapsedTimeInMilis += testElement.getElapsedTimeInMilis();
                }
            }
        }

        return elapsedTimeInMilis;
    }

    public String getRunName() {
        String name = null;
        ILaunchConfiguration launchConfig = launch.getLaunchConfiguration();
        if (launchConfig != null) {
            name = launchConfig.getName();
        }
        return name;
    }

    public long getRunElapsedTimeInMilis() {
        long elapsedTimeInMilis = 0;

        for (TestElement testElement : testElements) {
            if (testElement.getState().isFinished()) {
                elapsedTimeInMilis += testElement.getElapsedTimeInMilis();
            }
        }

        return elapsedTimeInMilis;
    }

    public Date getRunStartDate() {
        return startDate;
    }

    public synchronized void processRemoteTestEvent(RemoteTestEvent event) {
        switch (event.getType()) {
        case TEST_RUN_STARTED:
            updateTestElements(event.getTestElements());
            isRunning = true;
            isFinished = false;
            isInterrupted = false;
            fireTestRunStarted();
            break;
        case TEST_RUN_FINISHED:
            isRunning = false;
            isFinished = true;
            isInterrupted = false;
            fireTestRunFinished();
            break;
        case TEST_STARTED:
            updateTestElement(event.getTestElement());
            updateCounters(event);
            fireTestStarted(event.getTestElement());
            break;
        case TEST_FINISHED:
            updateTestElement(event.getTestElement());
            updateCounters(event);
            fireTestFinished(event.getTestElement());
            break;
        }
    }

    public synchronized void processLaunchTerminatedEvent() {
        if( isRunning ) {
            for (TestElement testElement : testElements) {
                if (testElement.getState() == State.RUNNING) {
                    testElement.setState(State.UNDEFINED);
                }
            }
            isRunning = false;
            isFinished = false;
            isInterrupted = true;
            fireTestRunInterrupted();
        }
    }

    private void updateTestElements(List<TestElement> testElementList) {
        testElements.clear();
        testElements.addAll(testElementList);

        testElementsByPackages.clear();
        for (TestElement testElement : testElements) {
            String packageName = testElement.getPackageName();
            List<TestElement> testElementsInPackage = testElementsByPackages.get(packageName);
            if (testElementsInPackage == null) {
                testElementsInPackage = new ArrayList<TestElement>();
                testElementsByPackages.put(packageName, testElementsInPackage);
            }
            testElementsInPackage.add(testElement);
        }
    }

    private void updateTestElement(TestElement testElement) {
        int index = testElements.indexOf(testElement);
        testElements.set(index, testElement);

        List<TestElement> testElementsInPackage = testElementsByPackages.get(testElement.getPackageName());
        index = testElementsInPackage.indexOf(testElement);
        testElementsInPackage.set(index, testElement);
    }

    private void updateCounters(RemoteTestEvent event) {
        if (event.getType() == Type.TEST_STARTED) {
            startedCount++;
        }
        if (event.getType() == Type.TEST_FINISHED) {
            State state = event.getTestElement().getState();
            switch (state) {
            case SUCCESS:
                successCount++;
                break;
            case FAILURE:
                failureCount++;
                break;
            case ERROR:
                errorCount++;
                break;
            default:
                throw new IllegalStateException(event.toString());
            }
        }
    }

    private List<TestRunListener> getTestRunListeners() {
        return CeylonTestPlugin.getDefault().getModel().getTestRunListeners();
    }

    private void fireTestRunStarted() {
        for (TestRunListener listener : getTestRunListeners()) {
            listener.testRunStarted(this);
        }
    }

    private void fireTestRunFinished() {
        for (TestRunListener listener : getTestRunListeners()) {
            listener.testRunFinished(this);
        }
    }

    private void fireTestRunInterrupted() {
        for (TestRunListener listener : getTestRunListeners()) {
            listener.testRunInterrupted(this);
        }
    }

    private void fireTestStarted(TestElement testElement) {
        for (TestRunListener listener : getTestRunListeners()) {
            listener.testStarted(this, testElement);
        }
    }

    private void fireTestFinished(TestElement testElement) {
        for (TestRunListener listener : getTestRunListeners()) {
            listener.testFinished(this, testElement);
        }
    }

}