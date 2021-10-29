/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.enumeration.RoomRateType;
import util.exception.DeleteRoomRateException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author PYT
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;

    
    
    @Override
    public Long createNewRoomRate(RoomRate roomRate, Long roomTypeId) throws RoomNumberExistException, UnknownPersistenceException {
        try {
            
            RoomType roomType = entityManager.find(RoomType.class, roomTypeId);
            
            roomRate.setRoomType(roomType);
            roomType.getRoomRates().add(roomRate);
            
            entityManager.persist(roomRate);
            entityManager.flush();

            return roomRate.getRoomRateId();
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
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = entityManager.createQuery("SELECT s FROM RoomRate s");

        List<RoomRate> roomRates = query.getResultList();

        for (RoomRate r : roomRates) {
            r.getRoomType();
        }
        return roomRates;
    }

    @Override
    public RoomRate retrieveRoomRateByRoomRateId(Long id) throws RoomRateNotFoundException {
        RoomRate r = entityManager.find(RoomRate.class, id);

        if (r != null) {
            return r;
        } else {
            throw new RoomRateNotFoundException("Room Rate ID " + id + " does not exist!");
        }
    }

    @Override
    public void updateRoomRate(RoomType roomType, RoomRate roomRate) throws RoomRateNotFoundException, UpdateRoomRateException {
       
        if (roomRate != null && roomRate.getRoomRateId()!= null) {
            RoomRate roomRateToUpdate = retrieveRoomRateByRoomRateId(roomRate.getRoomRateId());

            if (roomRateToUpdate.getName().equals(roomRate.getName())) {
                roomRateToUpdate.setName(roomRate.getName());
                roomRateToUpdate.setType(roomRate.getType());
                roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                roomRateToUpdate.setValidityStart(roomRate.getValidityStart());
                roomRateToUpdate.setValidityEnd(roomRate.getValidityEnd());
                
            } else {
                throw new UpdateRoomRateException("Room number of room to be updated does not match the existing record");
            }
        } else {
            throw new RoomRateNotFoundException("Room type ID not provided for room type to be updated");
        }
    }

    /*Deletion of room rate
    Can only be deleted if it is not used (by a room entity) -> does this mean we need to link room rate with room?
    Otherwise, mark as disabled (setEnabled = false) if it is currently being used
    */
    @Override
    public void deleteRoomRate(String name) throws RoomRateNotFoundException, DeleteRoomRateException {
        
        RoomRate roomRate = retrieveRoomRateByRoomRateName(name);
        
        if(roomRate.getRoomType() == null)
        {
            entityManager.remove(roomRate);
        } else{
            roomRate.setEnabled(Boolean.FALSE);
        }
        
       
    }

    public void persist(Object object) {
        entityManager.persist(object);
    }

    @Override
    public RoomRate retrieveRoomRateByRoomRateName(String name) throws RoomRateNotFoundException {
        Query query = entityManager.createQuery("SELECT r FROM RoomRate r WHERE r.name = :inName");
        query.setParameter("inName", name);
        
        try{
            return (RoomRate)query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException ex){
            throw new RoomRateNotFoundException("Room Rate " + name + " does not exist!");
            
        }
        
        
        
    }

}
