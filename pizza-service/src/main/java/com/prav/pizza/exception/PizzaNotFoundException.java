package com.prav.pizza.exception;

public class PizzaNotFoundException extends RuntimeException {

    public PizzaNotFoundException(String message) {
        super(message);
    }

    public PizzaNotFoundException(Long pizzaId) {
        super("Pizza not found with id: " + pizzaId);
    }
}