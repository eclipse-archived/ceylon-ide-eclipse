/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.model;

import static org.eclipse.ceylon.test.eclipse.plugin.model.TestElement.State.ERROR;
import static org.eclipse.ceylon.test.eclipse.plugin.model.TestElement.State.FAILURE;
import static org.eclipse.ceylon.test.eclipse.plugin.model.TestElement.State.SKIPPED_OR_ABORTED;
import static org.eclipse.ceylon.test.eclipse.plugin.model.TestElement.State.RUNNING;
import static org.eclipse.ceylon.test.eclipse.plugin.model.TestElement.State.SUCCESS;
import static org.eclipse.ceylon.test.eclipse.plugin.model.TestElement.State.UNDEFINED;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunch;

import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class TestEventListener {
    
    private final static byte EOT = 0x4;

    public static void startListenerThread(ILaunch launch, int port) {
        TestEventListenerThread thread = new TestEventListenerThread(launch, port);
        thread.start();
    }

    private static class TestEventListenerThread extends Thread {

        private ILaunch launch;
        private int port;
        private ServerSocket serverSocket;
        private Socket socket;
        
        public TestEventListenerThread(ILaunch launch, int port) {
            super("TestEventListenerThread");
            this.launch = launch;
            this.port = port;
        }

        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
                
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                StringBuffer buf = new StringBuffer(128);

                int ch;
                boolean esc = false;
                while (true) {
                    ch = isr.read();
                    if (ch == -1) {
                        break;
                    }
                    
                    if (ch == EOT && esc == false) {
                        JSONObject json = (JSONObject) JSONValue.parse(buf.toString());
                        
                        TestEventType eventType = parseTestEventType((String) json.get("event"));
                        TestElement element = parseTestElement((JSONObject) json.get("element"));
                        
                        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
                        TestRun testRun = testRunContainer.getOrCreateTestRun(launch);
                        testRun.processRemoteTestEvent(eventType, element);
                        
                        buf.setLength(0);
                    } else {
                        if (ch == EOT && esc == true) {
                            buf.setCharAt(buf.length() - 1, (char) ch);
                        } else {
                            buf.append((char) ch);
                        }
                    }
                    
                    esc = (ch == '\\');
                }
            } catch (EOFException e) {
                // noop
            } catch (IOException e) {
                CeylonTestPlugin.logError("", e);
            } finally {
                dispose();
            }
        }
        
        private TestEventType parseTestEventType(String event) {
            switch (event) {
                case "testRunStarted":
                    return TestEventType.TEST_RUN_STARTED;
                case "testRunFinished":
                    return TestEventType.TEST_RUN_FINISHED;
                case "testStarted":
                    return TestEventType.TEST_STARTED;
                case "testFinished":
                case "testError":
                case "testSkipped":
                case "testAborted":
                    return TestEventType.TEST_FINISHED;
                default:
                    throw new IllegalArgumentException(event);
            }
        }

        private TestElement parseTestElement(JSONObject json) {
            TestElement e = null;
            if (json != null) {
                e = new TestElement();
                e.setQualifiedName((String) json.get("name"));
                if( json.containsKey("variant") ) {
                    e.setVariant((String)json.get("variant"));
                }
                if( json.containsKey("variantIndex") ) {
                    Number variantIndex = (Number) json.get("variantIndex");
                    e.setVariantIndex(variantIndex.longValue());
                }
                if (json.containsKey("state")) {
                    String state = (String) json.get("state");
                    switch (state) {
                        case "running":
                            e.setState(RUNNING);
                            break;
                        case "success":
                            e.setState(SUCCESS);
                            break;
                        case "failure":
                            e.setState(FAILURE);
                            break;
                        case "error":
                            e.setState(ERROR);
                            break;
                        case "skipped":
                            e.setState(SKIPPED_OR_ABORTED);
                            break;
                        case "aborted":
                            e.setState(SKIPPED_OR_ABORTED);
                            break;
                        default:
                            e.setState(UNDEFINED);
                            break;
                    }
                }
                if (json.containsKey("children")) {
                    JSONArray children = (JSONArray) json.get("children");
                    if (children != null) {
                        List<TestElement> childrenList = new ArrayList<TestElement>();
                        for (Object child : children) {
                            childrenList.add(parseTestElement((JSONObject) child));
                        }
                        e.setChildren(childrenList);
                    }
                }
                if (json.containsKey("elapsedTime")) {
                    Number elapsedTime = (Number) json.get("elapsedTime");
                    e.setElapsedTimeInMilis(elapsedTime.longValue());
                }
                if (json.containsKey("exception")) {
                    e.setException((String) json.get("exception"));
                    e.setExpectedValue((String) json.get("expectedValue"));
                    e.setActualValue((String) json.get("actualValue"));
                }
            }
            return e;
        }

        private void dispose() {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // noop
                }
                socket = null;
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    // noop
                }
                socket = null;
            }
        }
    }

}