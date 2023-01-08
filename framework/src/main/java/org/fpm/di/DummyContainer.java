package org.fpm.di;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DummyContainer implements Container {
    private final DummyBinder binder;

    public DummyContainer(DummyBinder binder) {
        this.binder = binder;
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        Object data = binder.getData(clazz);
        Class<T> classData = (Class<T>) binder.getClassData(clazz);
        if (classData!=null)
            return getComponent(classData);
        if (data == null)
            return createInstanceOfClassT(clazz);
        return (T) data;

    }

    private <T> T createInstanceOfClassT(Class<T> clazz){
        if (checkInjection(clazz)!=null)
            return checkInjection(clazz);
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T checkInjection(Class<T> clazz) {
        for (Constructor<?> clazzConstructor:clazz.getConstructors()){
            if (clazzConstructor.isAnnotationPresent(Inject.class)) {
                try {
                    return (T) clazzConstructor.newInstance(getComponent(clazzConstructor.getParameterTypes()[0]));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
