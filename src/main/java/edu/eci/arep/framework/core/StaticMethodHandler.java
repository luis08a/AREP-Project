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
            m.invoke(null, null);
        } catch (Exception e) {
        }
        return null;
    }


}