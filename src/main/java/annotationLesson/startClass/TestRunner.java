package annotationLesson.startClass;

import lombok.extern.slf4j.Slf4j;
import annotationLesson.annotations.AfterSuite;
import annotationLesson.annotations.BeforeSuite;
import annotationLesson.annotations.Test;
import annotationLesson.testClass.TestClass;
import annotationLesson.processor.ValidationProcessor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestRunner {

    public static void main(String[] args) {
        runTests(TestClass.class);
    }

    public static void runTests(Class<?> clazz) {

        // 1) Проверка методов и сбор исключений
        ValidationProcessor.initValidation(clazz);
        ValidationProcessor.getExceptionsMessages();

        // 2) Выполнение методов
        Method beforeSuite = null;
        Method afterSuite = null;

        List<Method> testMethods = new ArrayList<>();

        // 3) Собираем методы с аннотациями
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuite = method;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                afterSuite = method;
            }
            if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }

        try {
            // 4) Создаем экземпляр класса для нестатических методов
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // 5) Если есть @BeforeSuite - выполняем
            if (beforeSuite != null) {
                log.info("Выполнение @BeforeSuite. Метод: " + beforeSuite.getName() + " класса " + clazz.getSimpleName());
                if (Modifier.isStatic(beforeSuite.getModifiers())) {
                    beforeSuite.invoke(null);
                } else {
                    beforeSuite.invoke(instance);
                }
            }

            // 6) Сортировка @Test по приоритету
            testMethods.sort((m1, m2) -> {
                int priority1 = m1.getAnnotation(Test.class).priority();
                int priority2 = m2.getAnnotation(Test.class).priority();
                return Integer.compare(priority2, priority1);
            });

            // 7) Выполняем методы помеченные аннотацией @Test
            for (Method testMethod : testMethods) {
                log.info("Выполнение @Test. Метод: " + testMethod.getName() + " с приоритетом " + testMethod.getAnnotation(Test.class).priority() + " класса " + clazz.getSimpleName());
                if (Modifier.isStatic(testMethod.getModifiers())) {
                    testMethod.invoke(null);  // Статический метод
                } else {
                    testMethod.invoke(instance);  // Нестатический метод
                }
            }

            // 8) Выполняем @AfterSuite, если есть
            if (afterSuite != null) {
                log.info("Выполнение @AfterSuite. метод: " + afterSuite.getName() + " класса " + clazz.getSimpleName());
                if (Modifier.isStatic(afterSuite.getModifiers())) {
                    afterSuite.invoke(null);
                } else {
                    afterSuite.invoke(instance);
                }
            }

        } catch (Exception e) {
            log.error("Ошибка выполнения тестов: ", e);
        }
    }
}
