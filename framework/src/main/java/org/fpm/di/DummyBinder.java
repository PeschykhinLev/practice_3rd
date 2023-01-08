package org.fpm.di;

import org.fpm.di.Binder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DummyBinder implements Binder {
    private Map<Class<?>, Object> data = new HashMap<>();
    private Map<Class<?>, Class<?>> classData = new HashMap<>();
    @Override
    public <T> void bind(Class<T> clazz) {
        if (clazz.isAnnotationPresent(Singleton.class)) {
            bind(clazz, createInstanceOfClassT(clazz));
            return;
        }
        data.put(clazz, null);

    }

    private <T> T createInstanceOfClassT(Class<T> clazz){
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public <T> void bind(Class<T> clazz, Class<? extends T> implementation) {
        classData.put(clazz,implementation);
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) {
        data.put(clazz,instance);
    }

    public Class<?> getClassData(Class<?> clazz) {
        return classData.getOrDefault(clazz, null);
    }

    public Object getData(Class<?> clazz){
        return data.getOrDefault(clazz, null);
    }
}
