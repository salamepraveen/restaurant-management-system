package com.prav.pizza.exception;

public class PizzaOperationException extends RuntimeException {

    public PizzaOperationException(String message) {
        super(message);
    }

    public PizzaOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}