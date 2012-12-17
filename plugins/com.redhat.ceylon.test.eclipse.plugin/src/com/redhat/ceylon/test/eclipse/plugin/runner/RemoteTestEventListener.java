package com.redhat.ceylon.test.eclipse.plugin.runner;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.debug.core.ILaunch;

import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRun;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunContainer;

public class RemoteTestEventListener {

    public static void startListenerThread(ILaunch launch, int port) {
        RemoteTestEventListenerThread thread = new RemoteTestEventListenerThread(launch, port);
        thread.start();
    }

    private static class RemoteTestEventListenerThread extends Thread {

        private ILaunch launch;
        private int port;
        private ServerSocket serverSocket;
        private Socket socket;
        
        public RemoteTestEventListenerThread(ILaunch launch, int port) {
            super("RemoteTestEventListenerThread");
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
                    if (event instanceof RemoteTestEvent) {
                        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
                        TestRun testRun = testRunContainer.getOrCreateTestRun(launch);
                        testRun.processRemoteTestEvent((RemoteTestEvent) event);
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