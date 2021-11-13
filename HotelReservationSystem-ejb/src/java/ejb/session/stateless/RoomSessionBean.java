/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeHasNoRoomException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
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

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewRoom(Room room, String roomTypeName) throws RoomNumberExistException, UnknownPersistenceException, RoomTypeNotFoundException, InputDataValidationException {
        

       
            try {

                RoomType roomType = roomTypeSessionBeanLocal.retrieveEnabledRoomTypeByRoomTypeName(roomTypeName);

                room.setRoomType(roomType);
                Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);
                 if (constraintViolations.isEmpty()) {
                roomType.getRooms().add(room);

                entityManager.persist(room);
                entityManager.flush();

                return room.getRoomId();
                } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
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

            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeId(roomTypeId);

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
            r.getReservations();
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
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException {
        Room roomToRemove = retrieveRoomByRoomId(roomId);

        List<Reservation> reservations = retrieveRoomByRoomId(roomId).getReservations();

        if (reservations.size() == 0) {
            entityManager.remove(roomToRemove);
        } else {
            roomToRemove.setEnabled(Boolean.FALSE);
            throw new DeleteRoomException("Room is associated with reservation, disabling room for future use.");

        }
    }

    @Override
    public void updateRoom(Room room) throws UpdateRoomException, RoomNotFoundException, InputDataValidationException {
        if (room != null && room.getRoomId() != null) {
            Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);

            if (constraintViolations.isEmpty()) {
                Room roomToUpdate = retrieveRoomByRoomId(room.getRoomId());
                if (roomToUpdate.getRoomNumber().equals(room.getRoomNumber())) {
                    roomToUpdate.setIsAvailable(room.getIsAvailable());
                } else {
                    throw new UpdateRoomException("Room number of room to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
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
        if (rooms.isEmpty()) {
            throw new RoomTypeHasNoRoomException("No Room is using this room type");
        } else {
            return rooms;
        }

    }

    public List<Room> retrieveAvailableAndEnabledRooms() {
        Query query = entityManager.createQuery("SELECT r FROM Room r WHERE r.isAvailable = true AND r.enabled = true");
        return query.getResultList();

    }

    public List<Room> retrieveAvailableAndEnabledRoomsByRoomType(Long roomTypeId) {

        Query query = entityManager.createQuery("SELECT r FROM Room r WHERE r.isAvailable = true AND r.enabled = true AND r.roomType.roomTypeId = :rid");
        query.setParameter("rid", roomTypeId);
        return query.getResultList();
    }

    @Override
    public List<Room> retrieveAvailableRoomsByRoomType(Long roomTypeId) {
        Query query = entityManager.createQuery("SELECT r FROM Room r WHERE r.isAvailable = true AND r.roomType.roomTypeId = :rid");
        query.setParameter("rid", roomTypeId);
        return query.getResultList();
    }

//    public List<Room> retrieveAvailableRoomsByRoomType(Long roomTypeId) {
//
//        Query query = entityManager.createQuery("SELECT r FROM Room r WHERE r.isAvailable = true AND r.roomType.roomTypeId = :rid");
//        query.setParameter("rid", roomTypeId);
//        return query.getResultList();
//    }
    public List<Room> retrieveRoomsAvailableForReservation(Date checkInDate, Date checkOutDate) {
        Query query = entityManager.createQuery("SELECT r FROM Reservation r WHERE r.startDate >=:startDate AND r.startDate <= :endDate OR r.endDate > :startDate AND r.endDate <= :endDate ");
        query.setParameter("startDate", checkInDate);
        query.setParameter("endDate", checkOutDate);

        List<Reservation> reservations = query.getResultList();
        List<Room> rooms = retrieveAvailableAndEnabledRooms();

        for (Reservation reservation : reservations) {
            for (Room room : reservation.getAllocatedRooms()) {
                rooms.remove(room);
            }
        }

        return rooms;

    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Room>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
