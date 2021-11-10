/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CreateNewReservationException;
import util.exception.NoRoomAllocationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author ANGELY
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "RoomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @Resource
    private EJBContext eJBContext;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Reservation createNewReservation(Reservation reservation) throws CreateNewReservationException, RoomTypeNotFoundException {
        RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(reservation.getRoomType().getName());

        rt.getReservations().add(reservation);
        em.persist(rt);

        em.persist(reservation);

        for (Room room : reservation.getAllocatedRooms()) {
            if (!room.getEnabled() || !room.getIsAvailable()) {
                eJBContext.setRollbackOnly();
                throw new CreateNewReservationException("Room(s) is not available/enabled for reservation!");
            }
        }

        em.flush();

        return reservation;
    }


    @Override
    public void checkInGuest(Reservation reservation) throws ReservationNotFoundException {
        Reservation reservationToUpdate = retrieveReservationByReservationId(reservation.getReservationId());
        if (reservation != null && reservation.getReservationId()!= null) {
    

            reservationToUpdate.setCheckedIn(true);

        } else {
            throw new ReservationNotFoundException("Reservation ID not provided for Reservation to be updated");
        }
    }
    
    @Override
    public void checkOutGuest(Reservation reservation) throws ReservationNotFoundException {
        Reservation reservationToUpdate = retrieveReservationByReservationId(reservation.getReservationId());
        if (reservation != null && reservation.getReservationId()!= null) {
    

            reservationToUpdate.setCheckedOut(true);
        } else {
            throw new ReservationNotFoundException("Reservation ID not provided for Reservation to be updated");
        }
    }

    @Override
    public List<Reservation> retrieveReservationsByDate(Date dateToday) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :inDateToday");
        query.setParameter("inDateToday", dateToday);

        return query.getResultList();
    }

    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        reservation.getAllocatedRooms().size();
        reservation.getException();
        return reservation;

    }

    @Override
    public List<Reservation> retrieveReservationByWalkInGuestId(Long guestId) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.walkInGuest.walkInGuestId = :inId");
        query.setParameter("inId", guestId);

        return query.getResultList();
    }

    @Override
    public List<Reservation> retrieveCheckedInReservationByGuestId(Long guestId) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.checkedIn = TRUE AND r.walkInGuest.walkInGuestId = :inId");
        query.setParameter("inId", guestId);
         List<Reservation> res = query.getResultList();
        for(Reservation r : res)
        {
            r.getAllocatedRooms().size();
        }

        return query.getResultList();
    }

    @Override
    public List<Reservation> retrieveCheckedInReservationByWalkInGuestId(Long guestId) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.checkedIn = TRUE AND r.walkInGuest = :inId");
        query.setParameter("inId", guestId);

        return query.getResultList();
    }

    @Override
    public RoomAllocationException retrieveraeByReservationId(Long reservationId) throws NoRoomAllocationException {
        try {
            Query query = em.createQuery("SELECT r FROM RoomAllocationException r WHERE r.reservation.reservationId = :inId");
            query.setParameter("inId", reservationId);
            RoomAllocationException rae = (RoomAllocationException) query.getSingleResult();
            rae.getTypeOneExceptions().size();
            return rae;
        } catch (NoResultException ex) {
            throw new NoRoomAllocationException("Reservation does not have any room allocation exception!");
        }
    }

}
