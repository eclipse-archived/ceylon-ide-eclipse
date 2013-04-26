package com.redhat.ceylon.test.eclipse.plugin.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ceylon.language.Callable;

public class TestCallable implements Callable<Object> {

    private String ceylonQualifiedName;

    public TestCallable(String ceylonQualifiedName) {
        this.ceylonQualifiedName = ceylonQualifiedName;
    }

    @Override
    public Object $call() {
        try {
            int pkgSeparatorIndex = ceylonQualifiedName.indexOf("::");
            int methodSeparatorIndex = ceylonQualifiedName.indexOf(".", pkgSeparatorIndex);

            String testClassName;
            String testMethodName;

            if (methodSeparatorIndex == -1) {
                testClassName = ceylonQualifiedName.replaceAll("::", ".") + "_";
                testMethodName = ceylonQualifiedName.substring(pkgSeparatorIndex + 2);
            } else {
                testClassName = ceylonQualifiedName.substring(0, methodSeparatorIndex).replaceAll("::", ".");
                testMethodName = ceylonQualifiedName.substring(methodSeparatorIndex + 1);
            }

            Class<?> testClazz = Class.forName(testClassName);
            Method method = testClazz.getDeclaredMethod(testMethodName, new Class[] {});
            method.setAccessible(true);
            if (methodSeparatorIndex == -1) {
                method.invoke(null, new Object[] {});
            } else {
                Constructor<?> constructor = testClazz.getDeclaredConstructor(new Class<?>[]{});
                constructor.setAccessible(true);
                Object instance = constructor.newInstance((Object[])null);
                method.invoke(instance, new Object[] {});                
            }
        } catch (InvocationTargetException e) {
            if(e.getTargetException() instanceof RuntimeException)
            	throw (RuntimeException)e.getTargetException();
            else
            	throw new RuntimeException(e.getTargetException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return null;
    }

    @Override
    public Object $call(Object arg0) {
        return $call();
    }

    @Override
    public Object $call(Object... arg0) {
        return $call();
    }

    @Override
    public Object $call(Object arg0, Object arg1) {
        return $call();
    }

    @Override
    public Object $call(Object arg0, Object arg1, Object arg2) {
        return $call();
    }

	@Override
	public short $getVariadicParameterIndex() {
		return -1;
	}

}