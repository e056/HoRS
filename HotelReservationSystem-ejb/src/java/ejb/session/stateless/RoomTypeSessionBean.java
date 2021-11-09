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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteRoomTypeException;
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

    public void persist(Object object) {
        entityManager.persist(object);
    }

    @Override
    public Long createNewRoomType(RoomType roomType, String nextHigherRoomTypeName) throws RoomTypeNameExistException, UnknownPersistenceException, RoomTypeNotFoundException {
        Boolean lowestRank = false;
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
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException {
        if (roomType != null && roomType.getName() != null) {
            RoomType roomTypeToUpdate = retrieveRoomTypeByRoomTypeName(roomType.getName());

            if (roomTypeToUpdate.getName().equals(roomType.getName())) {
                roomTypeToUpdate.setDescription(roomType.getDescription());
                roomTypeToUpdate.setSize(roomType.getSize());
                roomTypeToUpdate.setBed(roomType.getBed());
                roomTypeToUpdate.setCapacity(roomType.getCapacity());
                roomTypeToUpdate.setAmenities(roomType.getAmenities());

            } else {
                throw new UpdateRoomTypeException("Name of room type record to be updated does not match the existing record");
            }
        } else {
            throw new RoomTypeNotFoundException("Room type name not provided for room type to be updated");
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
                    entityManager.remove(roomTypeToRemove);
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
            List<Room> roomsToDisable = rt.getRooms();
            for (Room room : roomsToDisable) {
                Long roomId = room.getRoomId();
                Room roomFound = entityManager.find(Room.class, roomId);
                roomFound.setEnabled(Boolean.FALSE);

            }
            rt.setEnabled(Boolean.FALSE);
            throw new DeleteRoomTypeException("Room Type cannot be deleted as it is currently used by Room or Room Rate. Room Type and its rooms have been set to disabled!");
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

    public RoomType retrieveRoomTypeByRoomId(Long roomId) throws RoomTypeNotFoundException {
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
        System.out.println("Here");
        List<RoomType> rts = retrieveAllEnabledRoomTypes();
        List<RoomType> finalRts = retrieveAllEnabledRoomTypes();

        for (RoomType rt : rts) {
            int inventory = rt.getRooms().size();
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

}
