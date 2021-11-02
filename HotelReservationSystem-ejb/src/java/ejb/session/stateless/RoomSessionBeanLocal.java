/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteRoomException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author ANGELY
 */
@Local
public interface RoomSessionBeanLocal {

    public Long createNewRoom(Room room, Long roomTypeId) throws  RoomNumberExistException, UnknownPersistenceException, RoomTypeNotFoundException;

    public List<Room> retrieveAllRooms();

    public Room retrieveRoomByRoomId(Long id) throws RoomNotFoundException;

    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomNotFoundException;

    public void updateRoom(Room room, RoomType roomType, Reservation reservation) throws RoomNotFoundException, UpdateRoomException;

    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException;
}
