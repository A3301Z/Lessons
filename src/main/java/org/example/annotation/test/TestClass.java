package org.example.annotation.test;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.annotation.annotations.AfterSuite;
import org.example.annotation.annotations.BeforeSuite;
import org.example.annotation.annotations.Test;


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