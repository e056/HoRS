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
public class NoRoomAllocationException extends Exception{

    /**
     * Creates a new instance of <code>NoRoomAllocationException</code> without
     * detail message.
     */
    public NoRoomAllocationException() {
    }

    /**
     * Constructs an instance of <code>NoRoomAllocationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoRoomAllocationException(String msg) {
        super(msg);
    }
}
