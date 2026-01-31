package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    
    private final Calculator calculator = new Calculator();
    
    @Test
    void testAddMethod() {
        assertEquals(15, calculator.add(10, 5));
    }
    
    @ParameterizedTest
    @CsvSource({
        "10, 5, add, 15",
        "10, 5, add-again, 15",
        "10, 5, sub, 5",
        "10, 5, sub-again, 5",
        "10, 5, mul, 50",
        "10, 2, div, 5",
        "10, 0, div, 0",
        "10, 3, mod, 1",
        "2, 3, pow, 8",
        "10, 5, unknown, 0"
    })
    void testCalculate(int a, int b, String operation, int expected) {
        assertEquals(expected, calculator.calculate(a, b, operation));
    }
    
    @Test
    void testCalculateDivisionByZeroReturnsZero() {
        assertEquals(0, calculator.calculate(10, 0, "div"));
    }
    
    @Test
    void testCalculateUnknownOperationReturnsZero() {
        assertEquals(0, calculator.calculate(10, 5, "unknown-operation"));
    }
    
    @Test
    void testPowerCalculation() {
        assertEquals(27, calculator.calculate(3, 3, "pow"));
        assertEquals(1, calculator.calculate(5, 0, "pow"));
        assertEquals(16, calculator.calculate(2, 4, "pow"));
    }
}