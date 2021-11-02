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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteRoomException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author ANGELY Added: Create new room, view all rooms Unfinished: update
 * room, delete room
 *
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;

    public Long createNewRoom(Room room, String roomTypeName) throws RoomNumberExistException, UnknownPersistenceException, RoomTypeNotFoundException {

        Long roomTypeId = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName).getRoomTypeId();
        return createNewRoom(room, roomTypeId);

    }

    // Added: Use em to find RoomType, did associations both ways, persisted
 
    public Long createNewRoom(Room room, Long roomTypeId) throws RoomNumberExistException, UnknownPersistenceException, RoomTypeNotFoundException {
        try {

            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomId(roomTypeId);

            room.setRoomType(roomType);
            roomType.getRooms().add(room);

            entityManager.persist(room);
            entityManager.flush();

            return room.getRoomId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomNumberExistException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Room> retrieveAllRooms() {
        Query query = entityManager.createQuery("SELECT s FROM Room s");

        List<Room> rooms = query.getResultList();

        for (Room r : rooms) {
            r.getReservation();
            r.getRoomType();
        }
        return rooms;
    }

    @Override
    public Room retrieveRoomByRoomId(Long id) throws RoomNotFoundException {
        Room r = entityManager.find(Room.class, id);

        if (r != null) {
            return r;
        } else {
            throw new RoomNotFoundException("Room ID " + id + " does not exist!");
        }
    }

    @Override
    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomNotFoundException {
        Query query = entityManager.createQuery("SELECT p FROM Room p WHERE p.roomNumber = :inRoomNumber");
        query.setParameter("inRoomNumber", roomNumber);

        try {
            return (Room) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomNotFoundException("Room numbered" + roomNumber + " does not exist!");
        }
    }

    @Override
    public void updateRoom(Room room, RoomType roomType, Reservation reservation) throws RoomNotFoundException, UpdateRoomException {
        if (room != null && room.getRoomId() != null) {
            Room roomToUpdate = retrieveRoomByRoomId(room.getRoomId());

            if (roomToUpdate.getRoomNumber().equals(room.getRoomNumber())) {
                roomToUpdate.setIsAvailable(room.getIsAvailable());
                roomToUpdate.setRoomType(roomType);
                roomToUpdate.setReservation(reservation);
                // added setRoomType and setReservation
            } else {
                throw new UpdateRoomException("Room number of room to be updated does not match the existing record");
            }
        } else {
            throw new RoomNotFoundException("Room ID not provided for room to be updated");
        }
    }

    @Override
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException {
        Room roomToRemove = retrieveRoomByRoomId(roomId);

        List<Reservation> reservations = reservationSessionBeanLocal.retrieveReservationsByRoomId(roomId);

        if (reservations.isEmpty()) {
            entityManager.remove(roomToRemove);
        } else {
            roomToRemove.setIsEnabled(Boolean.FALSE);
        }
    }

}
