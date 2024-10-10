package org.example.annotation.processors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.annotation.annotations.AfterSuite;
import org.example.annotation.annotations.BeforeSuite;
import org.example.annotation.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationProcessor {

    private static final Set<IllegalArgumentException> notStaticMethodExceptionsList = new HashSet<>();

    /**
     * Инициализация проверки методов класса
     */
    public static void initValidation(Class<?> clazz) {
        validateSingleAnnotatedMethod(clazz);
        isTheMethodStatic(clazz);
        rangeValidation(clazz);
    }

    /**
     * Проверка статичности методов класса для аннотаций {@link BeforeSuite} и {@link AfterSuite}
     */
    private static void isTheMethodStatic(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (
                    method.isAnnotationPresent(BeforeSuite.class) &&
                            !java.lang.reflect.Modifier.isStatic(method.getModifiers()) ||
                            method.isAnnotationPresent(AfterSuite.class) &&
                                    !java.lang.reflect.Modifier.isStatic(method.getModifiers())
            ) {
                notStaticMethodExceptionsList.add(new IllegalArgumentException("Method #" + method.getName() + " is not static"));
            }
        }
    }

    /**
     * Проверка диапазона допустимых значений поля priority аннотации {@link Test}
     */
    private static void rangeValidation(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                Test test = method.getAnnotation(Test.class);
                int value = test.priority();
                if (value < 1 || value > 10) {
                    notStaticMethodExceptionsList.add(
                            new IllegalArgumentException(
                                    "Method #" + method.getName() + "priority range should be between 1 and 10. Value = " + value
                            )
                    );
                }
            }
        }
    }

    @SneakyThrows
    private static void validateSingleAnnotatedMethod(Class<?> clazz) {

        List<Method> beforeSuiteCount = new ArrayList<>();
        List<Method> afterSuiteCount = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {

            if (method.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuiteCount.add(method);
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                afterSuiteCount.add(method);
            }
        }

        if (beforeSuiteCount.size() > 1) {
            notStaticMethodExceptionsList.add(new IllegalArgumentException(
                    "Method #" + beforeSuiteCount.getFirst().getName() + "can only be called once"
            ));
        }

        if (afterSuiteCount.size() > 1) {
            notStaticMethodExceptionsList.add(new IllegalArgumentException(
                    "Method #" + afterSuiteCount.getFirst().getName() + " can only be called once"
            ));
        }
    }


    /**
     * Получить список исключений валидации
     */
    public static void getExceptionsMessages() {
        for (IllegalArgumentException exception : notStaticMethodExceptionsList) {
            log.error(exception.getMessage());
        }
    }
}
