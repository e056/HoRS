/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @EJB(name = "RoomAllocationSessionBeanLocal")
    private RoomAllocationSessionBeanLocal roomAllocationSessionBeanLocal;

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

    public Reservation createNewOnlineReservation(Reservation reservation, Guest guest) throws RoomTypeNotFoundException, CreateNewReservationException {

        RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(reservation.getRoomType().getName());
        Guest g = em.find(Guest.class, guest.getGuestId());

        g.getReservations().add(reservation);
        reservation.setGuest(g);
        rt.getReservations().add(reservation);

        em.persist(rt);
        em.persist(reservation);

        em.flush();

        return reservation;
    }

    @Override
    public void checkInGuest(Reservation reservation) throws ReservationNotFoundException {
        Reservation reservationToUpdate = retrieveReservationByReservationId(reservation.getReservationId());
        if (reservation != null && reservation.getReservationId() != null) {

            reservationToUpdate.setCheckedIn(true);

        } else {
            throw new ReservationNotFoundException("Reservation ID not provided for Reservation to be updated");
        }
    }

    @Override
    public void checkOutGuest(Reservation reservation) throws ReservationNotFoundException {
        Reservation reservationToUpdate = retrieveReservationByReservationId(reservation.getReservationId());
        List<Room> roomsToUpdate = reservationToUpdate.getAllocatedRooms();
        if (reservation != null && reservation.getReservationId() != null) {
            // disassociation done here:
            reservationToUpdate.setAllocatedRooms(new ArrayList<Room>());

            for (Room r : roomsToUpdate) {
                Room roomToDis = em.find(Room.class, r.getRoomId());
                roomToDis.getReservations().remove(reservation);

            }
            // ends here

            reservationToUpdate.setCheckedOut(true);
        } else {
            throw new ReservationNotFoundException("Reservation ID not provided for Reservation to be updated");
        }
    }

    @Override
    public List<Reservation> retrieveReservationsByDate(Date dateToday) {

        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :indate");
        query.setParameter("indate", dateToday);
//        List<Reservation> neededReservations = new ArrayList<>();
//        List<Reservation> reservations = query.getResultList();
//
//        for (Reservation r : reservations) {
//            System.out.println("Found: " + r.getReservationId());
//            System.out.println("Start Date: " + r.getStartDate());
//            System.out.println("Start Date: " + dateToday);
//
//            if (r.getStartDate().compareTo(dateToday) == 0) {
//                neededReservations.add(r);
//            }
//        }

        return query.getResultList();
    }

    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class,
                reservationId);
        reservation.getAllocatedRooms().size();
        reservation.getException();
        return reservation;

    }

    @Override
    public List<Reservation> retrieveReservationByWalkInGuestId(Long guestId
    ) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.walkInGuest.walkInGuestId = :inId");
        query.setParameter("inId", guestId);

        return query.getResultList();
    }

    @Override
    public List<Reservation> retrieveCheckedInReservationByGuestId(Long guestId
    ) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.checkedIn = TRUE AND r.walkInGuest.walkInGuestId = :inId");
        query.setParameter("inId", guestId);
        List<Reservation> res = query.getResultList();
        for (Reservation r : res) {
            r.getAllocatedRooms().size();
        }

        return query.getResultList();
    }

    @Override
    public List<Reservation> retrieveCheckedInReservationByWalkInGuestId(Long guestId
    ) {
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

    public void allocateReservation(Reservation reservation) {
        Reservation r = em.find(Reservation.class,
                reservation.getReservationId());
        roomAllocationSessionBeanLocal.allocateAReservation(r);

    }

}
