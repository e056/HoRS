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
public class RoomTypeHasNoRoomException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeHasNoRoomException</code> without
     * detail message.
     */
    public RoomTypeHasNoRoomException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeHasNoRoomException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeHasNoRoomException(String msg) {
        super(msg);
    }
}
