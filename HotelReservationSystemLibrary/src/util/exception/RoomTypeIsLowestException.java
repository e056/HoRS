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
public class RoomTypeIsLowestException extends Exception{

    /**
     * Creates a new instance of <code>RoomTypeIsLowestException</code> without
     * detail message.
     */
    public RoomTypeIsLowestException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeIsLowestException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeIsLowestException(String msg) {
        super(msg);
    }
}
