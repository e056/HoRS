/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteRoomTypeException;
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

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;

    public void persist(Object object) {
        entityManager.persist(object);
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
                    throw new RoomTypeNameExistException();
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

    // if roomtype is not used by reservation then u can em.remove, if not you set enabled to false
    @Override
    public void deleteRoomType(String roomTypeName) throws RoomTypeNotFoundException, DeleteRoomTypeException {
        RoomType roomTypeToRemove = retrieveRoomTypeByRoomTypeName(roomTypeName);

        if (!roomTypeToRemove.getRooms().isEmpty()) {
            for (Room room : roomTypeToRemove.getRooms()) {
                if (room.getReservation() != null) {
                    roomTypeToRemove.setEnabled(Boolean.FALSE);
                    break;
                }
            }
            entityManager.remove(roomTypeToRemove);
        } else {
            roomTypeToRemove.setEnabled(Boolean.FALSE);
        }
    }

    @Override
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeNotFoundException {

        Query query = entityManager.createQuery("SELECT r FROM RoomType r WHERE r.name = :inName");
        query.setParameter("inName", roomTypeName);
        try {
            return (RoomType) query.getSingleResult();
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

}
