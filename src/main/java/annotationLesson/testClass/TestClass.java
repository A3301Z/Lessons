package annotationLesson.testClass;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import annotationLesson.annotations.AfterSuite;
import annotationLesson.annotations.BeforeSuite;
import annotationLesson.annotations.Test;


/***
 * Класс для объявления методов с тестовыми аннотациями
 */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class TestClass {

    @Test(priority = 6)
    public static void test1() {}

    @Test(priority = 8)
    public static void test2() {}

    @AfterSuite
    public static void afterSuiteTest() {}

    @BeforeSuite
    public static void beforeSuiteTest() {}
}