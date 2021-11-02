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
import util.exception.ReservationNotFoundException;
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


    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException;


    public List<Room> retrieveRoomByReservationId(Long reservationId) throws ReservationNotFoundException;

    public List<Room> retrieveRoomByRoomType(Integer roomRank);

    public void updateRoom(Room room) throws UpdateRoomException, RoomNotFoundException;

}
