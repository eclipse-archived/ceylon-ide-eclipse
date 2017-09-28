package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.ceylon.common.log.Logger;


public class EclipseLogger implements Logger {

    private int errors;
    
    public int getErrors(){
        return errors;
    }

    @Override
    public void error(String str) {
        errors++;
        System.err.println("Error: "+str);
    }

    @Override
    public void warning(String str) {
        System.err.println("Warning: "+str);
    }

    @Override
    public void info(String str) {
        System.err.println("Note: "+str);
    }

    @Override
    public void debug(String str) {
        //System.err.println("Debug: "+str);
    }

}
