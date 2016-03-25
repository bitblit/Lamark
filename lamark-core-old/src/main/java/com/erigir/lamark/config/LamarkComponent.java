package com.erigir.lamark.config;

import com.erigir.lamark.Lamark;
import com.sun.tools.corba.se.idl.StringGen;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by cweiss on 7/17/15.
 */
public class LamarkComponent {
    private Object targetObject;
    private Method targetMethod;
    private LamarkComponentType type;

    public LamarkComponent(Object targetObject, Method targetMethod, LamarkComponentType type) {
        Objects.requireNonNull(targetObject,"targetObject may not be null");
        Objects.requireNonNull(targetMethod,"targetMethod may not be null");
        Objects.requireNonNull(type,"type may not be null");

        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.type = type;
    }

    public Annotation getMethodAnnotation()
    {
        return targetMethod.getAnnotation(type.annotationClass);
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    public LamarkComponentType getType() {
        return type;
    }

    public void setType(LamarkComponentType type) {
        this.type = type;
    }

    /**
     * Where the magic happens - we make a call to the component to execute
     * @param lamark
     * @return
     */
    public Object execute(Lamark lamark)
    {
        return null;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("LamarkComponent [type=").append(type).append(", targetObject=").append(targetObject.getClass())
                .append(", method=").append(targetMethod.getName()).append("]");
        return sb.toString();
    }
}
