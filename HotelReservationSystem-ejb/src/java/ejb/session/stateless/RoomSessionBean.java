/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomReservationLineEntity;
import entity.RoomType;
import java.util.Date;
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
import util.exception.RoomTypeHasNoRoomException;
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

    @Override
    public Long createNewRoom(Room room, String roomTypeName) throws RoomNumberExistException, UnknownPersistenceException, RoomTypeNotFoundException {
        try {

            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);

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

    // Added: Use em to find RoomType, did associations both ways, persisted
   /* public Long createNewRoom(Room room, Long roomTypeId) throws RoomNumberExistException, UnknownPersistenceException, RoomTypeNotFoundException {
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
    } */

    @Override
    public List<Room> retrieveAllRooms() {
        Query query = entityManager.createQuery("SELECT s FROM Room s");

        List<Room> rooms = query.getResultList();

        for (Room r : rooms) {
            r.getRoomReservationLineEntities();
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

//    @Override
//    public void updateRoom(Room room, RoomType roomType, Reservation reservation) throws RoomNotFoundException, UpdateRoomException {
//        if (room != null && room.getRoomId() != null) {
//            Room roomToUpdate = retrieveRoomByRoomId(room.getRoomId());
//
//            if (roomToUpdate.getRoomNumber().equals(room.getRoomNumber())) {
//                roomToUpdate.setIsAvailable(room.getIsAvailable());
//                roomToUpdate.setRoomType(roomType);
//                roomToUpdate.setReservation(reservation);
//                // added setRoomType and setReservation
//            } else {
//                throw new UpdateRoomException("Room number of room to be updated does not match the existing record");
//            }
//        } else {
//            throw new RoomNotFoundException("Room ID not provided for room to be updated");
//        }
//    }
    @Override
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException {
        Room roomToRemove = retrieveRoomByRoomId(roomId);

        List<RoomReservationLineEntity> roomReservations = retrieveRoomReservationsByRoomId(roomId);

        if (roomReservations.size() == 0) {
            entityManager.remove(roomToRemove);
        } else {
            roomToRemove.setEnabled(Boolean.FALSE);
            throw new DeleteRoomException("Room is associated with reservation, disabling room for future use.");

        }
    }

    @Override
    public List<RoomReservationLineEntity> retrieveRoomReservationsByRoomId(Long roomId) {
        {
            Query query = entityManager.createQuery("SELECT rr FROM RoomReservationLineEntity rr WHERE rr.room.roomId = :inRoomId");
            query.setParameter("inRoomId", roomId);

            return query.getResultList();
        }

    }

    @Override
    public void updateRoom(Room room) throws UpdateRoomException, RoomNotFoundException {
        if (room != null && room.getRoomId() != null) {
            Room roomToUpdate = retrieveRoomByRoomId(room.getRoomId());
            if (roomToUpdate.getRoomNumber().equals(room.getRoomNumber())) {
                roomToUpdate.setIsAvailable(room.getIsAvailable());
            } else {
                throw new UpdateRoomException("Room number of room to be updated does not match the existing record");
            }
        } else {
            throw new RoomNotFoundException("Room ID not provided for room to be updated");
        }
    }


    @Override
    public List<Room> retrieveRoomByRoomType(String roomType) throws RoomTypeHasNoRoomException {
        Query query = entityManager.createQuery("SELECT r FROM Room r WHERE r.roomType.name = :inroomType");
        query.setParameter("inroomType", roomType);
        List<Room> rooms = query.getResultList();
        //notes: query.getResultList returns a list, if got no results it just returns an empty list, not NULL
        if(rooms.isEmpty())
        {
            throw new RoomTypeHasNoRoomException("No Room is using this room type");
        } else {
            return rooms;
        }
   
    }
    
    public List<Room> retrieveAvailableAndEnabledRooms() {
        Query query = entityManager.createQuery("SELECT r FROM Room r WHERE r.isAvailable = true AND r.enabled = true");
        return query.getResultList();
        
    }

    public List<Room> retrieveRoomsAvailableForReservation(Date checkInDate, Date checkOutDate) {
        Query query = entityManager.createQuery("SELECT r FROM Reservation r WHERE r.startDate BETWEEN :ci AND :co ");
        query.setParameter(":ci", checkInDate);
        query.setParameter(":co", checkOutDate);
        
        List<Reservation> reservations = query.getResultList();
        List<Room> rooms = retrieveAvailableAndEnabledRooms();
        
        for (Reservation reservation : reservations) {
            for (RoomReservationLineEntity rrle: reservation.getRoomReservationLineEntities()) {
                rooms.remove(rrle.getRoom());
            }   
        }
        
        return rooms;

    }

}
