package com.supply.chain.inventory.exceptions;

public class EntityInUseException extends RuntimeException {
    public EntityInUseException(String message) {
        super(message);
    }
}
