package com.redhat.ceylon.test.eclipse.plugin.model;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE_JS;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;

public class TestRun {

    private static final Object NULL_LOCK = new Object();

    public static Object acquireLock(TestRun testRun) {
        return testRun != null ? testRun : NULL_LOCK;
    }

    private final Date startDate;
    private final ILaunch launch;
    private TestElement root;
    private List<TestElement> atomicTests = new ArrayList<TestElement>();
    private Map<String, List<TestElement>> testsByPackages = new LinkedHashMap<String, List<TestElement>>();
    private boolean isRunning = true;
    private boolean isFinished;
    private boolean isInterrupted;
    private boolean isPinned;
    private int startedCount = 0;
    private int successCount = 0;
    private int failureCount = 0;
    private int errorCount = 0;
    private int skippedOrAbortedCount = 0;

    public TestRun(ILaunch launch) {
        this.launch = launch;
        this.startDate = new Date();
    }

    public ILaunch getLaunch() {
        return launch;
    }
    
    public TestElement getRoot() {
        return root;
    }
    
    public List<TestElement> getAtomicTests() {
        return atomicTests;
    }
    
    public Map<String, List<TestElement>> getTestsByPackages() {
        return testsByPackages;
    }
    
    public boolean isJvm() {
        try {
            return LAUNCH_CONFIG_TYPE.equals(launch.getLaunchConfiguration().getType().getIdentifier());
        } catch (CoreException e) {
            return false;
        }
    }

    public boolean isJs() {
        try {
            return LAUNCH_CONFIG_TYPE_JS.equals(launch.getLaunchConfiguration().getType().getIdentifier());
        } catch (CoreException e) {
            return false;
        }
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

    public boolean isPinned() {
    	return isPinned;
    }
    
	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}
    
    public boolean isSuccess() {
        return failureCount == 0 && errorCount == 0;
    }

    public boolean isFailureOrError() {
        return failureCount != 0 || errorCount != 0;
    }

    public int getTotalCount() {
        return atomicTests.size();
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
    
    public int getSkippedOrAbortedCount() {
        return skippedOrAbortedCount;
    }

    public int getFinishedCount() {
        return successCount + failureCount + errorCount;
    }

    public State getPackageState(String packageName) {
        int undefined = 0;
        int success = 0;
        int failure = 0;
        int error = 0;
        int skippedOrAborted = 0;
        int total = 0;
        
        List<TestElement> testsInPackage = testsByPackages.get(packageName);
        if (testsInPackage != null) {
            total = testsInPackage.size();
            for (TestElement testElement : testsInPackage) {
                switch(testElement.getState()) {
                    case UNDEFINED : undefined++; break;
                    case SUCCESS: success++; break;
                    case FAILURE: failure++; break;
                    case ERROR: error++; break;
                    case SKIPPED_OR_ABORTED: skippedOrAborted++; break;
                    default: /* noop */ break;
                }
            }
        }
        
        if (error > 0) {
            return State.ERROR;
        } else if (failure > 0) {
            return State.FAILURE;
        } else if (skippedOrAborted == total) {
            return State.SKIPPED_OR_ABORTED;
        } else if (undefined == total) {
            return State.UNDEFINED;
        } else if (success + skippedOrAborted == total) {
            return State.SUCCESS;
        } else if (total > 0) {
            return State.RUNNING;
        }
        return State.UNDEFINED;
    }

    public long getPackageElapsedTimeInMilis(String packageName) {
        long elapsedTimeInMilis = 0;

        List<TestElement> testsInPackage = testsByPackages.get(packageName);
        if (testsInPackage != null) {
            for (TestElement testElement : testsInPackage) {
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

        if( root != null && root.getChildren() != null ) {
            for (TestElement testElement : root.getChildren()) {
                if (testElement.getState().isFinished()) {
                    elapsedTimeInMilis += testElement.getElapsedTimeInMilis();
                }
            }
        }

        return elapsedTimeInMilis;
    }

    public Date getRunStartDate() {
        return startDate;
    }

    public synchronized void processRemoteTestEvent(TestEventType eventType, TestElement element) {
        switch (eventType) {
        case TEST_RUN_STARTED:
            updateRootElement(element);
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
            updateTestElement(eventType, element);
            updateCounters(eventType, element);
            fireTestStarted(element);
            break;
        case TEST_FINISHED:
            updateTestElement(eventType, element);
            updateCounters(eventType, element);
            fireTestFinished(element);
            break;
        }
    }

    public synchronized void processLaunchTerminatedEvent() {
        if( isRunning ) {
            new TestVisitor() {
                @Override
                public void visitElement(TestElement e) {
                    if (e.getState() == State.RUNNING) {
                        e.setState(State.UNDEFINED);
                    }
                }
            }.visitElements(root);
            
            isRunning = false;
            isFinished = false;
            isInterrupted = true;
            fireTestRunInterrupted();
        }
    }
    
    private void updateRootElement(final TestElement root) {
        this.root = root;
        
        new TestVisitor() {
            @Override
            public void visitElement(TestElement e) {
                if (e != root && (e.getChildren() == null || e.getChildren().size() == 0)) {
                    atomicTests.add(e);
                }
            }

        }.visitElements(root);
        
        testsByPackages.clear();
        if( root.getChildren() != null ) {
            for(TestElement e : root.getChildren()) {
                List<TestElement> testElementsInPackage = testsByPackages.get(e.getPackageName());
                if (testElementsInPackage == null) {
                    testElementsInPackage = new ArrayList<TestElement>();
                    testsByPackages.put(e.getPackageName(), testElementsInPackage);
                }
                testElementsInPackage.add(e);
            }
        }
    }

    private void updateTestElement(final TestEventType eventType, final TestElement testElement) {
        new TestVisitor() {
            @Override
            public void visitElement(TestElement e) {
                if (e.equals(testElement)) {
                    e.setState(testElement.getState());
                    e.setException(testElement.getException());
                    e.setExpectedValue(testElement.getExpectedValue());
                    e.setActualValue(testElement.getActualValue());
                    e.setElapsedTimeInMilis(testElement.getElapsedTimeInMilis());
                }
                else if (eventType == TestEventType.TEST_STARTED 
                        && testElement.getVariant() != null
                        && testElement.getVariantIndex() != null
                        && Objects.equals(e.getQualifiedName(), testElement.getQualifiedName())
                        && e.getVariant() == null 
                        && e.getVariantIndex() == null
                        && !e.getChildren().contains(testElement)) {
                    e.addChild(testElement);
                    atomicTests.remove(e);
                    atomicTests.add(testElement);
                }
            }
        }.visitElements(root);
    }

    private void updateCounters(TestEventType eventType, TestElement element) {
        if (eventType == TestEventType.TEST_STARTED) {
            startedCount++;
        }
        if (eventType == TestEventType.TEST_FINISHED) {
            State state = element.getState();
            switch (state) {
            case SUCCESS:
                if (atomicTests.contains(element)) {
                    successCount++;
                }
                break;
            case FAILURE:
                if (element.getException() != null) {
                    failureCount++;
                }
                break;
            case ERROR:
                if (element.getException() != null) {
                    errorCount++;
                }
                break;
            case SKIPPED_OR_ABORTED:
                skippedOrAbortedCount++;
                break;
            default:
                throw new IllegalStateException(element.toString());
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

    public static abstract class TestVisitor {

        public final void visitElements(TestElement e) {
            if (e != null) {
                visitElement(e);
                List<TestElement> children = e.getChildren();
                if (children != null) {
                    for (TestElement child : children) {
                        visitElements(child);
                    }
                }
            }
        }

        public abstract void visitElement(TestElement e);

    }

}