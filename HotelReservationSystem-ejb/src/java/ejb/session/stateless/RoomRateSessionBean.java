/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Calendar;
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
import util.enumeration.RoomRateType;
import util.exception.DeleteRoomRateException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author PYT
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;

    @Override
    public Long createNewRoomRate(RoomRate roomRate, Long roomTypeId) throws UnknownPersistenceException, RoomTypeNotFoundException, RoomRateNameExistException {
        try {

            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomId(roomTypeId);

            roomRate.setRoomType(roomType);
            roomType.getRoomRates().add(roomRate);

            entityManager.persist(roomRate);
            entityManager.flush();

            return roomRate.getRoomRateId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomRateNameExistException();
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
        Query query = entityManager.createQuery("SELECT s FROM RoomRate s ORDER BY s.roomRateId, s.type");

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
    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, UpdateRoomRateException {

        if (roomRate != null && roomRate.getRoomRateId() != null) {
            RoomRate roomRateToUpdate = retrieveRoomRateByRoomRateId(roomRate.getRoomRateId());

            if (roomRateToUpdate.getName().equals(roomRate.getName())) {
                //roomRateToUpdate.setName(roomRate.getName());
                //roomRateToUpdate.setType(roomRate.getType());
                roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                roomRateToUpdate.setValidityStart(roomRate.getValidityStart());
                roomRateToUpdate.setValidityEnd(roomRate.getValidityEnd());

            } else {
                throw new UpdateRoomRateException("Room Rate name of Room Rate to be updated does not match the existing record");
            }
        } else {
            throw new RoomRateNotFoundException("RoomRateId not provided for room type to be updated");
        }
    }

    /*Deletion of room rate
    Can only be deleted if it is not used (by a reservation)
    Otherwise, mark as disabled (setEnabled = false) if it is currently being used
     */
    @Override
    public void deleteRoomRate(Long rrId) throws RoomRateNotFoundException, DeleteRoomRateException {

        RoomRate roomRate = retrieveRoomRateByRoomRateId(rrId);

        if (!roomRate.getRoomType().getRooms().isEmpty()) {
            for (Room room : roomRate.getRoomType().getRooms()) {
                if (room.getReservations().size() != 0) {
                    roomRate.setEnabled(Boolean.FALSE);
                    break;
                }
            }
            entityManager.remove(roomRate);
        } else {
            roomRate.setEnabled(Boolean.FALSE);
            throw new DeleteRoomRateException("Room Rate is associated with reservation, disabling room for future use.");
        }

    }

    public void persist(Object object) {
        entityManager.persist(object);
    }

    @Override
    public RoomRate retrieveRoomRateByRoomRateName(String name) throws RoomRateNotFoundException {
        Query query = entityManager.createQuery("SELECT r FROM RoomRate r WHERE r.name = :inName");
        query.setParameter("inName", name);

        try {
            return (RoomRate) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateNotFoundException("Room Rate " + name + " does not exist!");

        }

    }

    public RoomRate retrievePublishedRoomRateByRoomType(Long roomTypeId) throws RoomRateNotFoundException {
        //System.out.println("Room type id is " + roomTypeId);

        Query query = entityManager.createQuery("SELECT r FROM RoomRate r WHERE r.roomType.roomTypeId = :roomTypeId AND r.type = :inType");
        query.setParameter("roomTypeId", roomTypeId);
        query.setParameter("inType", RoomRateType.PUBLISHED);

        try {
            return (RoomRate) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateNotFoundException("Room Rate does not exist!");

        }

    }

    public List<RoomRate> retrieveRoomRatesByRoomType(Long roomTypeId) {
        Query query = entityManager.createQuery("SELECT r FROM RoomRate r WHERE r.roomType.roomTypeId = :roomTypeId AND r.type != :published");
        query.setParameter("roomTypeId", roomTypeId);
        query.setParameter("published", RoomRateType.PUBLISHED);

        List<RoomRate> roomRates = query.getResultList();
        return roomRates;

    }

//The total reservation fee payable by the guest for a reservation is calculated by summing the 
//prevailing rate per night of each night of stay for the entire duration of stay. For example, if a 
//guest books a Deluxe Room for 3 nights, the reservation fee will be the sum of first day’s rate 
//per night, second day’s rate per night and third day’s rate per night. If either peak rate or 
//promotion rate is defined for a particular room type on a particular date, it will take 
//precedence over the normal rate. If both peak rate and promotion rate are defined for a 
//particular room type on a particular date, the promotion rate will take precedence.
    public BigDecimal retrieveTotalPriceForOnlineReservationByRoomTyoe(Long roomTypeId, Date checkInDate, Date checkOutDate) {
        List<RoomRate> rates = retrieveRoomRatesByRoomType(roomTypeId);
        RoomRate normal = new RoomRate();
        RoomRate peak = new RoomRate();
        RoomRate promotion = new RoomRate();

        for (RoomRate rate : rates) {
            if (rate.getType().equals(RoomRateType.PEAK)) {
                peak = rate;
            } else if (rate.getType().equals(RoomRateType.PROMOTION)) {
                promotion = rate;
            } else if (rate.getType().equals(RoomRateType.NORMAL)) {
                normal = rate;
            }
        }

        final Calendar current = Calendar.getInstance();
        current.setTime(checkInDate);

        BigDecimal totalAmount = BigDecimal.ZERO;

        while (!current.getTime().after(checkOutDate)) {
            if (peak != null && promotion != null) {
                totalAmount.add(peak.getRatePerNight());
            } else if (peak == null) {
                totalAmount.add(promotion.getRatePerNight());
            } else if (promotion == null) {
                totalAmount.add(peak.getRatePerNight());
            } else {
                totalAmount.add(normal.getRatePerNight());
            }

            current.add(Calendar.DATE, 1);
        }
        return totalAmount;
    }
}
