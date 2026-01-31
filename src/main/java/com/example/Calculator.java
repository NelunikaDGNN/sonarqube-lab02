package com.example;

public class Calculator {

    public int calculate(int a, int b, String op) {
        return switch (op) {
            case "add", "add-again" -> a + b;
            case "sub", "sub-again" -> a - b;
            case "mul" -> a * b;
            case "div" -> b != 0 ? a / b : 0;
            case "mod" -> a % b;
            case "pow" -> (int) Math.pow(a, b);
            default -> 0;
        };
    }

    //  remove duplicate methods
    public int add(int a, int b) {
        return a + b;
    }
}