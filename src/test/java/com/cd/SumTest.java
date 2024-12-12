package com.cd;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumTest {

    Sum sumInstance;

    @BeforeAll
    static void init() {
        System.out.println("init()");
    }

    @AfterAll
    static void close() {
        System.out.println("close()");
    }

    @BeforeEach
    void setUp() {
        sumInstance = new SumImpl();
        System.out.println("SetUp");
    }

    @AfterEach
    void tearDown() {
        System.out.println("TearDown");
    }

    @Test
    void testSumWithZero() {
        System.out.println("testSumWithZero()");
        int actual = sumInstance.sum(0, 0);
        assertEquals(0, actual, "0+0=0");
    }

    @Test
    void testSumPositiveNumbers() {
        System.out.println("testSumWithZero()");
        int actual = sumInstance.sum(10, 10);
        assertEquals(20, actual, "10+10=20");
    }

    @Test
    void testSumNegativeNumbers() {
        System.out.println("testSumWithZero()");
        int actual = sumInstance.sum(10, -10);
        assertEquals(0, actual, "10-10=0");
    }
}
