package com.redhat.ceylon.test.eclipse.plugin.runner;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ceylon.language.ArraySequence;
import ceylon.language.Sequential;
import ceylon.test.TestListener;
import ceylon.test.TestRunResult;
import ceylon.test.TestRunner;
import ceylon.test.createTestRunner_;

import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;

public class RemoteTestRunner {
    
    private static final int CONNECTION_ATTEMPTS = 10;
    private static final String CONNECTION_HOST = null;
    
    private int port = -1;
    private List<String> tests = new ArrayList<String>();
    private Socket socket;
    private ObjectOutputStream oos;
    
    public static void main(String[] args) {
        RemoteTestRunner runner = new RemoteTestRunner();
        try {
            runner.init(args);
            runner.connect();
            runner.run();
        } catch(Throwable e) {
            logError(e.getMessage(), e);
            throw e;                        
        } finally {
            runner.dispose();
        }
    }

    private void init(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-port")) {
                port = Integer.parseInt(args[++i]);
            } else if (arg.equals("-test")) {
                tests.add(args[++i]);
            }
        }
    }

    private void connect() {
        Exception lastException = null;
        for (int i = 0; i < CONNECTION_ATTEMPTS; i++) {
            try {
                socket = new Socket(CONNECTION_HOST, port);
                oos = new ObjectOutputStream(socket.getOutputStream());
                return;
            } catch (IOException e) {
                lastException = e;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // noop
            }
        }
        
        throw new RuntimeException("CeylonTestRunner: failed connect to port " + port, lastException);
    }

    private void run() {
        List<ceylon.language.String> sourcesList = new ArrayList<ceylon.language.String>();
        for(String test : tests) {
            sourcesList.add(ceylon.language.String.instance(test));
        }
        
        Sequential<Object> sources = ArraySequence.instance(
                TypeDescriptor.klass(Object.class),
                sourcesList.toArray());
        
        Sequential<TestListener> listeners = ArraySequence.instance(
                TypeDescriptor.klass(TestListener.class), 
                new Object[] { new RemoteTestEventPublisher(oos) });
        
        TestRunner testRunner = createTestRunner_.createTestRunner(sources, listeners);
        TestRunResult result = testRunner.run();
        
        System.out.println(result);
    }

    private void dispose() {
        try {
            if( oos != null ) {
                oos.close();
                oos = null;
            }
        } catch (IOException e) {
            // noop
        }
        
        try {
            if( socket != null ) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            // noop            
        }
    }

    private static void logError(String msg, Throwable e) {
        System.err.println(msg);
        if (e != null) {
            e.printStackTrace();
        }
    }

}