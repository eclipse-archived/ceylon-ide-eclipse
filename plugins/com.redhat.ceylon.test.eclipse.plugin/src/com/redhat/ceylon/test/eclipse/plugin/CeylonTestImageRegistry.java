package com.redhat.ceylon.test.eclipse.plugin;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class CeylonTestImageRegistry {

    public static final String ERROR_OVR = "error_ovr.gif";
    public static final String FAILED_OVR = "failed_ovr.gif";
    public static final String SUCCESS_OVR = "success_ovr.gif";
    
    public static final String TEST = "test.gif";
    public static final String TEST_ERROR = "test_error.gif";
    public static final String TEST_FAILED = "test_failed.gif";
    public static final String TEST_SKIPPED = "test_skipped.gif";
    public static final String TEST_RUNNING = "test_running.gif";
    public static final String TEST_SUCCESS = "test_success.gif";
    
    public static final String TESTS = "tests.gif";
    public static final String TESTS_ERROR = "tests_error.gif";
    public static final String TESTS_FAILED = "tests_failed.gif";
    public static final String TESTS_SKIPPED = "tests_skipped.gif";
    public static final String TESTS_RUNNING = "tests_running.gif";
    public static final String TESTS_SUCCESS = "tests_success.gif";
    public static final String TESTS_INTERRUPTED = "tests_interrupted.gif";
    
    public static final String STACK_TRACE = "stack_trace.gif";
    public static final String STACK_TRACE_FILTER = "stack_trace_filter.gif";
    public static final String STACK_TRACE_LINE = "stack_trace_line.gif";
    
    public static final String SHOW_FAILURES = "show_failures.gif";
    public static final String SHOW_NEXT = "show_next.gif";
    public static final String SHOW_PREV = "show_prev.gif";
    
    public static final String STATE_FIXED = "state_fixed.gif";
    public static final String STATE_REGRESSED_ERROR = "state_regressed_error.gif";
    public static final String STATE_REGRESSED_FAILURE = "state_regressed_failed.gif";
    public static final String STATE_CHANGED = "state_changed.gif";
    public static final String STATE_UNCHANGED = "state_unchanged.gif";
    public static final String STATE_ADDED = "state_added.gif";
    public static final String STATE_REMOVED = "state_removed.gif";
    
    public static final String RELAUNCH = "relaunch.gif";
    public static final String STOP = "stop.gif";
    public static final String SCROLL_LOCK = "scroll_lock.gif";
    public static final String COLLAPSE_ALL = "collapse_all.gif";
    public static final String EXPAND_ALL = "expand_all.gif";
    public static final String COMPARE = "compare.gif";
    public static final String HISTORY = "history.gif";
    public static final String PIN = "pin.gif";

    private static final IPath ICONS_PATH = new Path("icons/");

    public static Image getImage(String key) {
        return CeylonTestPlugin.getDefault().getImageRegistry().get(key);
    }

    public static ImageDescriptor getImageDescriptor(String key) {
        return CeylonTestPlugin.getDefault().getImageRegistry().getDescriptor(key);
    }

    public static void init(ImageRegistry imageRegistry) {
        imageRegistry.put(ERROR_OVR, image(ERROR_OVR));
        imageRegistry.put(FAILED_OVR, image(FAILED_OVR));
        imageRegistry.put(SUCCESS_OVR, image(SUCCESS_OVR));
        
        imageRegistry.put(TEST, image(TEST));
        imageRegistry.put(TEST_ERROR, image(TEST_ERROR));
        imageRegistry.put(TEST_FAILED, image(TEST_FAILED));
        imageRegistry.put(TEST_SKIPPED, image(TEST_SKIPPED));
        imageRegistry.put(TEST_RUNNING, image(TEST_RUNNING));
        imageRegistry.put(TEST_SUCCESS, image(TEST_SUCCESS));
        
        imageRegistry.put(TESTS, image(TESTS));
        imageRegistry.put(TESTS_ERROR, image(TESTS_ERROR));
        imageRegistry.put(TESTS_FAILED, image(TESTS_FAILED));
        imageRegistry.put(TESTS_SKIPPED, image(TESTS_SKIPPED));
        imageRegistry.put(TESTS_RUNNING, image(TESTS_RUNNING));
        imageRegistry.put(TESTS_SUCCESS, image(TESTS_SUCCESS));
        imageRegistry.put(TESTS_INTERRUPTED, image(TESTS_INTERRUPTED));
        
        imageRegistry.put(STACK_TRACE, image(STACK_TRACE));
        imageRegistry.put(STACK_TRACE_FILTER, image(STACK_TRACE_FILTER));
        imageRegistry.put(STACK_TRACE_LINE, image(STACK_TRACE_LINE));
        
        imageRegistry.put(SHOW_FAILURES, image(SHOW_FAILURES));
        imageRegistry.put(SHOW_NEXT, image(SHOW_NEXT));
        imageRegistry.put(SHOW_PREV, image(SHOW_PREV));
        
        imageRegistry.put(STATE_FIXED, image(STATE_FIXED));
        imageRegistry.put(STATE_REGRESSED_ERROR, image(STATE_REGRESSED_ERROR));
        imageRegistry.put(STATE_REGRESSED_FAILURE, image(STATE_REGRESSED_FAILURE));
        imageRegistry.put(STATE_CHANGED, image(STATE_CHANGED));
        imageRegistry.put(STATE_UNCHANGED, image(STATE_UNCHANGED));
        imageRegistry.put(STATE_ADDED, image(STATE_ADDED));
        imageRegistry.put(STATE_REMOVED, image(STATE_REMOVED));
        
        imageRegistry.put(RELAUNCH, image(RELAUNCH));
        imageRegistry.put(STOP, image(STOP));
        imageRegistry.put(SCROLL_LOCK, image(SCROLL_LOCK));
        imageRegistry.put(COLLAPSE_ALL, image(COLLAPSE_ALL));
        imageRegistry.put(EXPAND_ALL, image(EXPAND_ALL));
        imageRegistry.put(COMPARE, image(COMPARE));
        imageRegistry.put(HISTORY, image(HISTORY));
        imageRegistry.put(PIN, image(PIN));
    }

    private static ImageDescriptor image(String file) {
        Bundle bundle = CeylonTestPlugin.getDefault().getBundle();
        URL url = FileLocator.find(bundle, ICONS_PATH.append(file), null);
        if (url != null) {
            return ImageDescriptor.createFromURL(url);
        }
        else {
            return null;
        }
    }

}