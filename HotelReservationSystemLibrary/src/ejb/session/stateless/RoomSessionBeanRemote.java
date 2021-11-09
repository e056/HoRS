/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeHasNoRoomException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author ANGELY
 */
@Remote
public interface RoomSessionBeanRemote {

    public List<Room> retrieveAllRooms();

    public Room retrieveRoomByRoomId(Long id) throws RoomNotFoundException;

    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomNotFoundException;

    public List<Room> retrieveRoomByRoomType(String roomType) throws RoomTypeHasNoRoomException;
    
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException;

    public Long createNewRoom(Room room, String roomTypeName) throws RoomNumberExistException, UnknownPersistenceException, RoomTypeNotFoundException;
    
    public void updateRoom(Room room) throws UpdateRoomException, RoomNotFoundException;


    public List<Room> retrieveAvailableAndEnabledRooms();

    public List<Room> retrieveRoomsAvailableForReservation(Date checkInDate, Date checkOutDate);


    
}
