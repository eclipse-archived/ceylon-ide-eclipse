/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class CeylonTestImageRegistry {

    public static final String ERROR_OVR = "error_ovr.png";
    public static final String FAILED_OVR = "failed_ovr.png";
    public static final String SUCCESS_OVR = "success_ovr.png";
    
    public static final String TEST = "test.png";
    public static final String TEST_ERROR = "testerr.png";
    public static final String TEST_FAILED = "testfail.png";
    public static final String TEST_SKIPPED = "testignored.png";
    public static final String TEST_RUNNING = "testrun.png";
    public static final String TEST_SUCCESS = "testok.png";
    
    public static final String TESTS = "tsuite.png";
    public static final String TESTS_ERROR = "tsuiteerror.png";
    public static final String TESTS_FAILED = "tsuitefail.png";
    public static final String TESTS_SKIPPED = "tsuiteignored.png";
    public static final String TESTS_RUNNING = "tsuiterun.png";
    public static final String TESTS_SUCCESS = "tsuiteok.png";
    public static final String TESTS_INTERRUPTED = "tsuiteinterrupted.png";
    
    public static final String STACK_TRACE = "stackframe.png";
    public static final String STACK_TRACE_FILTER = "cfilter.png";
    public static final String STACK_TRACE_LINE = "stack_trace_line.gif";
    
    public static final String SHOW_FAILURES = "failures.png";
    public static final String SHOW_NEXT = "show_next.png";
    public static final String SHOW_PREV = "show_prev.png";
    
    public static final String STATE_FIXED = "state_fixed.gif";
    public static final String STATE_REGRESSED_ERROR = "state_regressed_error.gif";
    public static final String STATE_REGRESSED_FAILURE = "state_regressed_failed.gif";
    public static final String STATE_CHANGED = "state_changed.gif";
    public static final String STATE_UNCHANGED = "state_unchanged.gif";
    public static final String STATE_ADDED = "state_added.gif";
    public static final String STATE_REMOVED = "state_removed.gif";
    
    public static final String RELAUNCH = "relaunch.png";
    public static final String RELAUNCH_FAILED = "relaunchfailed.png";
    public static final String STOP = "stop.png";
    public static final String SCROLL_LOCK = "lock.png";
    public static final String COLLAPSE_ALL = "collapseall.png";
    public static final String EXPAND_ALL = "expandall.png";
    public static final String COMPARE = "compare.png";
    public static final String HISTORY = "history_list.png";
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
        imageRegistry.put(RELAUNCH_FAILED, image(RELAUNCH_FAILED));
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