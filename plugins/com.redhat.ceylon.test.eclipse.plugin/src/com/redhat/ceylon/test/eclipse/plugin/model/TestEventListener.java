package com.redhat.ceylon.test.eclipse.plugin.model;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.debug.core.ILaunch;

import com.redhat.ceylon.test.eclipse.TestEvent;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;

public class TestEventListener {

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

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    Object event = ois.readObject();
                    if (event instanceof TestEvent) {
                        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
                        TestRun testRun = testRunContainer.getOrCreateTestRun(launch);
                        testRun.processRemoteTestEvent((TestEvent) event);
                    }
                }
            } catch (EOFException e) {
                // noop
            } catch (IOException e) {
                CeylonTestPlugin.logError("", e);
            } catch (ClassNotFoundException e) {
                CeylonTestPlugin.logError("", e);
            } finally {
                dispose();
            }
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