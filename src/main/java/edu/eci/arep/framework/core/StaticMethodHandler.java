package edu.eci.arep.framework.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.eci.arep.framework.core.Handler;

/**
 * StaticMethodHandler
 */
public class StaticMethodHandler implements Handler {
    Method m;

    public StaticMethodHandler(Method method) {
        m = method;
    }

    @Override
    public String process() {
        try {
            return (String) m.invoke(null, null);
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return null;
    }

    @Override
    public String process(String[] params) {
        try {
            return (String)m.invoke(null, params);
        } catch (Exception e) {
        }
        return null;
    }


}