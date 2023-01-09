package org.fpm.di;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DummyContainer implements Container {
    private final DummyBinder binder;

    // конструктор класу, параметер binder для отримання даних з бази даних
    public DummyContainer(DummyBinder binder) {
        this.binder = binder;
    }

    // основний метод класу для отримання згенерованих об'єктів
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

    // метод для створення екземпляру класу з пустого конструктору
    private <T> T createInstanceOfClassT(Class<T> clazz){
        if (checkInjection(clazz)!=null)
            return checkInjection(clazz);
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // метод для перевірки анотації Inject у конструкторів і повернення екземпляру класу при її наявності
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
