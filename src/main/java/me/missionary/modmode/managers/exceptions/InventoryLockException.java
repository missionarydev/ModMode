package me.missionary.modmode.managers.exceptions;

/**
 * Created by Missionary (missionarymc@gmail.com) on 5/6/2017.
 */
public class InventoryLockException extends Exception {
    /**
     * Constructs a new {@code InventoryLockException} with a message.
     *
     * @param cause the cause.
     */
    public InventoryLockException(String cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code InventoryLockException} without a message.
     */
    public InventoryLockException() {
        super();
    }
}
