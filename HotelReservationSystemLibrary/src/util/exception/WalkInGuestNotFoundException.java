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
public class WalkInGuestNotFoundException extends Exception{

    /**
     * Creates a new instance of <code>WalkInGuestNotFoundException</code>
     * without detail message.
     */
    public WalkInGuestNotFoundException() {
    }

    /**
     * Constructs an instance of <code>WalkInGuestNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public WalkInGuestNotFoundException(String msg) {
        super(msg);
    }
}
