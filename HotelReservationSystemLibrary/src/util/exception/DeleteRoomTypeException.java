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
public class DeleteRoomTypeException extends Exception{

    /**
     * Creates a new instance of <code>DeleteRoomTypeException</code> without
     * detail message.
     */
    public DeleteRoomTypeException() {
    }

    /**
     * Constructs an instance of <code>DeleteRoomTypeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteRoomTypeException(String msg) {
        super(msg);
    }
}
