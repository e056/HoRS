/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author PYT
 */
public class WalkInGuestPassportNumExistException extends Exception{

    /**
     * Creates a new instance of
     * <code>WalkInGuestPassportNumExistException</code> without detail message.
     */
    public WalkInGuestPassportNumExistException() {
    }

    /**
     * Constructs an instance of
     * <code>WalkInGuestPassportNumExistException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public WalkInGuestPassportNumExistException(String msg) {
        super(msg);
    }
}
