package com.prav.pizza.exception;

public class ToppingNotFoundException extends RuntimeException {

    public ToppingNotFoundException(String message) {
        super(message);
    }

    public ToppingNotFoundException(Long toppingId) {
        super("Topping not found with id: " + toppingId);
    }
}