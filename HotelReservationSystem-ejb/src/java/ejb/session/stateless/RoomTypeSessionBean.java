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
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeHasNoRoomException;
import util.exception.RoomTypeIsLowestException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author ANGELY
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @EJB(name = "RoomSessionBeanLocal")
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomTypeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public void persist(Object object) {
        entityManager.persist(object);
    }

    @Override
    public Long createNewRoomType(RoomType roomType, String nextHigherRoomTypeName) throws RoomTypeNameExistException, UnknownPersistenceException, RoomTypeNotFoundException, InputDataValidationException {
        Boolean lowestRank = false;
        Set<ConstraintViolation<RoomType>> constraintViolations = validator.validate(roomType);

        if (constraintViolations.isEmpty()) {
            try {
                if (nextHigherRoomTypeName.equals("None")) {
                    RoomType highestRoomType = retrieveHighestRoomType();
                    highestRoomType.setNextHigherRoomType(roomType);
                    entityManager.persist(roomType);
                    entityManager.flush();
                } else {
                    RoomType currentRoomType = retrieveRoomTypeByNextHighestRoomType(nextHigherRoomTypeName);
                    if (currentRoomType != null) {
                        System.out.println(currentRoomType.getName());
                    }
                    RoomType nextHigherRoomType = currentRoomType.getNextHigherRoomType();
                    currentRoomType.setNextHigherRoomType(null);

                    roomType.setNextHigherRoomType(nextHigherRoomType);

                    entityManager.persist(roomType);

                    entityManager.flush();
                    currentRoomType.setNextHigherRoomType(roomType);

                }
                return roomType.getRoomTypeId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new RoomTypeNameExistException("A roomType with this room name already exists!");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (RoomTypeIsLowestException ex) {
                lowestRank = true;
            } finally {
                if (lowestRank == true) {
                    roomType.setNextHigherRoomType(retrieveRoomTypeByRoomTypeName(nextHigherRoomTypeName));
                    entityManager.persist(roomType);
                    entityManager.flush();
                    return roomType.getRoomTypeId();
                } else {
                    return roomType.getRoomTypeId();
                }

            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public Long createNewRoomType(RoomType roomType) throws RoomTypeNameExistException, UnknownPersistenceException {
        try {
            entityManager.persist(roomType);
            entityManager.flush();
            return roomType.getRoomTypeId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomTypeNameExistException("A roomType with this room name already exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException, RoomTypeNameExistException {
        List<RoomType> rts = retrieveAllRoomTypes();
        for (RoomType rt : rts) {
            if (rt.getName().equals(roomType.getName()) && !rt.getRoomTypeId().equals(roomType.getRoomTypeId())) {

                throw new RoomTypeNameExistException("A roomType with this room name already exists!");

            }
            if (roomType != null && roomType.getName() != null) {

                Set<ConstraintViolation<RoomType>> constraintViolations = validator.validate(roomType);

                if (constraintViolations.isEmpty()) {
                    RoomType roomTypeToUpdate = retrieveRoomTypeByRoomTypeId(roomType.getRoomTypeId());

                    roomTypeToUpdate.setName(roomType.getName());
                    roomTypeToUpdate.setDescription(roomType.getDescription());
                    roomTypeToUpdate.setSize(roomType.getSize());
                    roomTypeToUpdate.setBed(roomType.getBed());
                    roomTypeToUpdate.setCapacity(roomType.getCapacity());
                    entityManager.persist(roomTypeToUpdate);

                    roomTypeToUpdate.setAmenities(roomType.getAmenities());
                } else {
                    throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }

            } else {
                throw new RoomTypeNotFoundException("Room type name not provided for room type to be updated");
            }
        }
    }

    // check if room type is linked to any room or room rate. if not, only then can delete & rearrange the ranks
    // if room type is linked to any rooms and we disable room type, we must also disable all rooms belonging to this room type
    @Override
    public void deleteRoomType(RoomType roomTypeToRemove) throws RoomTypeNotFoundException, DeleteRoomTypeException {
        Boolean isLowest = false;
        Boolean canDelete = true;

        try {
            roomSessionBeanLocal.retrieveRoomByRoomType(roomTypeToRemove.getName());
            canDelete = false;
            System.out.println("Cannot delete");
        } catch (RoomTypeHasNoRoomException ex) {
            System.out.println("Can delete");
            canDelete = true;
        }

        if (canDelete) {
            try {
                if (retrieveHighestRoomType().equals(roomTypeToRemove)) {
                    RoomType prevRoomType = retrieveRoomTypeByNextHighestRoomType(roomTypeToRemove.getName());
                    prevRoomType.setNextHigherRoomType(null);
                    RoomType managedRt = entityManager.find(RoomType.class, roomTypeToRemove.getRoomTypeId());
                    entityManager.remove(managedRt);
                } else {
                    Long roomTypeId = roomTypeToRemove.getRoomTypeId();
                    RoomType rt = entityManager.find(RoomType.class, roomTypeId);
                    RoomType prevRoomType = retrieveRoomTypeByNextHighestRoomType(roomTypeToRemove.getName());
                    Long prtId = prevRoomType.getRoomTypeId();
                    RoomType prt = entityManager.find(RoomType.class, prtId);
                    RoomType higherRoomType = roomTypeToRemove.getNextHigherRoomType();
                    rt.setNextHigherRoomType(null);
                    entityManager.remove(rt);
                    prt.setNextHigherRoomType(higherRoomType);
                }

            } catch (RoomTypeIsLowestException ex) {
                isLowest = true;
            } finally {
                if (isLowest) {
                    //notes: must use em.find BEFORE em.remove for em.remove to work. im not sure why but it solved the bug
                    Long roomTypeId = roomTypeToRemove.getRoomTypeId();
                    RoomType rt = entityManager.find(RoomType.class, roomTypeId);
                    rt.setNextHigherRoomType(null);
                    entityManager.remove(rt);
                }
            }
        } else {
            Long id = roomTypeToRemove.getRoomTypeId();
            RoomType rt = entityManager.find(RoomType.class, id);
//            List<Room> roomsToDisable = rt.getRooms();
//            for (Room room : roomsToDisable) {
//                Long roomId = room.getRoomId();
//                Room roomFound = entityManager.find(Room.class, roomId);
//                roomFound.setEnabled(Boolean.FALSE);
//
//            }
            rt.setEnabled(Boolean.FALSE);
            throw new DeleteRoomTypeException("Room Type cannot be deleted as it is currently used by Room or Room Rate. Room Type has been set to disabled!");
        }

    }

    @Override
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeNotFoundException {

        Query query = entityManager.createQuery("SELECT r FROM RoomType r WHERE r.name = :inName");
        query.setParameter("inName", roomTypeName);
        try {
            RoomType rt = (RoomType) query.getSingleResult();
            rt.getReservations().size();
            rt.getRoomRates().size();
            rt.getRooms().size();
            return rt;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeNotFoundException("Room Type " + roomTypeName + " does not exist!");
        }

    }

    @Override
    public RoomType retrieveEnabledRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeNotFoundException {

        Query query = entityManager.createQuery("SELECT r FROM RoomType r WHERE r.name = :inName AND r.enabled = TRUE");
        query.setParameter("inName", roomTypeName);
        try {
            RoomType rt = (RoomType) query.getSingleResult();
            rt.getReservations().size();
            rt.getRoomRates().size();
            rt.getRooms().size();
            return rt;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeNotFoundException("Room Type " + roomTypeName + " does not exist!");
        }

    }

    public RoomType retrieveRoomTypeByRoomTypeId(Long roomId) throws RoomTypeNotFoundException {
        RoomType roomType = entityManager.find(RoomType.class, roomId);
        if (roomType == null) {
            throw new RoomTypeNotFoundException("Room Type ID " + roomId + " does not exist!");
        } else {
            return roomType;
        }

    }

    @Override
    public List<RoomType> retrieveAllRoomTypes() {
        Query query = entityManager.createQuery("SELECT r FROM RoomType r");

        return query.getResultList();
    }

    @Override
    public List<RoomType> retrieveAllEnabledRoomTypes() {
        Query query = entityManager.createQuery("SELECT r FROM RoomType r WHERE r.enabled = TRUE");

        return query.getResultList();

    }

    @Override
    public RoomType retrieveRoomTypeByNextHighestRoomType(String nextHighestRoomTypeName) throws RoomTypeIsLowestException {

        try {
            Query query = entityManager.createQuery("SELECT r FROM RoomType r WHERE r.nextHigherRoomType.name = :inName");
            query.setParameter("inName", nextHighestRoomTypeName);
            return (RoomType) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomTypeIsLowestException("Room type is lowest");
        }

    }

    public RoomType retrieveHighestRoomType() {
        Query query = entityManager.createQuery("SELECT r FROM RoomType r WHERE r.nextHigherRoomType IS NULL");

        RoomType roomType = (RoomType) query.getSingleResult();

        return roomType;
    }

    public List<RoomType> retrieveRoomTypesAvailableForReservation(int numOfRooms, Date checkInDate, Date checkOutDate) {

        List<RoomType> rts = retrieveAllEnabledRoomTypes();
        List<RoomType> finalRts = retrieveAllEnabledRoomTypes();

        for (RoomType rt : rts) {
            int inventory = roomSessionBeanLocal.retrieveAvailableAndEnabledRoomsByRoomType(rt.getRoomTypeId()).size();
            System.out.println("Curr Room Type: " + rt.getName());
            System.out.println("Inventory: " + inventory);
            List<Reservation> roomTypeReservations = rt.getReservations();
            for (Reservation reservation : roomTypeReservations) {
                if ((reservation.getStartDate().compareTo(checkInDate) >= 0 && reservation.getStartDate().compareTo(checkOutDate) <= 0)
                        || (reservation.getEndDate().compareTo(checkInDate) > 0 && reservation.getEndDate().compareTo(checkOutDate) <= 0)) {
                    inventory -= reservation.getNumOfRooms();
                }

            }
            if (inventory < numOfRooms) {
                finalRts.remove(rt);
            }
            System.out.println("Final Inventory: " + inventory);

        }
        return finalRts;

    }

    public String retrieveRoomTypeNameByReservation(Long reservationId) {
        Reservation res = entityManager.find(Reservation.class, reservationId);
        res.getRoomType();
        res.getAllocatedRooms();
        res.getGuest();
        res.getReservationId();
        System.out.println(res.getTotalPrice());
        
        return res.getRoomType().getName();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomType>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
