package me.nikonbite.api.lib.inventory.util;

/**
 * Used to specify a method to be executed later with a given argument.
 */
public interface Callback<T> {
    /**
     * Call this method to invoke the callback
     *
     * @param param The parameter expected by the callback. In the case of a 'Void' Callback this should be null.
     */
    void call(T param);
}
