/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.NoRoomAllocationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author ANGELY
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "RoomRateSessionBeanLocal")
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

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

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ReservationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Reservation createNewReservation(Reservation reservation) throws CreateNewReservationException, RoomTypeNotFoundException, InputDataValidationException {
        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);
        if (constraintViolations.isEmpty()) {
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
            allocateAfter2am(reservation);

            return reservation;

        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public Reservation createNewOnlineReservation(Reservation reservation, Guest guest) throws RoomTypeNotFoundException, CreateNewReservationException, InputDataValidationException {
        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);
        if (constraintViolations.isEmpty()) {
            RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(reservation.getRoomType().getName());
            Guest g = em.find(Guest.class, guest.getGuestId());

            g.getReservations().add(reservation);
            reservation.setGuest(g);
            rt.getReservations().add(reservation);

            em.persist(rt);
            em.persist(reservation);

            em.flush();

            allocateAfter2am(reservation);

            return reservation;
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public Reservation createNewPartnerReservation(Reservation reservation, Partner partner) throws RoomTypeNotFoundException, CreateNewReservationException, InputDataValidationException {
        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);
        if (constraintViolations.isEmpty()) {
            RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(reservation.getRoomType().getName());
            Partner p = em.find(Partner.class, partner.getPartnerId());

            p.getReservations().add(reservation);
            reservation.setPartner(p);
            rt.getReservations().add(reservation);

            em.persist(rt);
            em.persist(reservation);

            em.flush();

            allocateAfter2am(reservation);

            return reservation;
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    public void allocateAfter2am(Reservation reservation) {
        try {
            Date currDate = new java.util.Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateWithTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aa");
            System.out.println("Time this reservation is made: " + dateWithTimeFormat.format(currDate));
            String dateString2am = dateFormat.format(currDate) + " 02:00:00 AM";
            String dateStringOnly = dateFormat.format(currDate);
            if (dateFormat.parse(dateStringOnly).compareTo(reservation.getStartDate()) == 0) {
                if (dateWithTimeFormat.parse(dateString2am).compareTo(currDate) < 0) {
                    System.out.println("Same day check-in after 2am, allocating rooms... ");
                    allocateReservation(reservation);

                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(ReservationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void allocateReservation(Reservation reservation) {
        Reservation r = em.find(Reservation.class,
                reservation.getReservationId());
        roomAllocationSessionBeanLocal.allocateAReservation(r);

    }

    @Override
    public BigDecimal calculateFinalOnlineReservationAmount(RoomType roomTypeToReserve, Date startDate, Date endDate, int numOfRooms) {
        return roomRateSessionBeanLocal.retrievePriceForOnlineReservationByRoomType(roomTypeToReserve.getRoomTypeId(), startDate, endDate).multiply(BigDecimal.valueOf(numOfRooms));

    }

    @Override
    public BigDecimal calculateFinalWalkInReservationAmount(RoomType rt, Date startDate, Date endDate, int numOfRooms) throws RoomRateNotFoundException {

    
            long duration = endDate.getTime() - startDate.getTime();
            int days = (int) Math.round(TimeUnit.MILLISECONDS.toDays(duration));
            BigDecimal price = roomRateSessionBeanLocal.retrievePublishedRoomRateByRoomType(rt.getRoomTypeId()).getRatePerNight().multiply(BigDecimal.valueOf(days));
            return price.multiply(BigDecimal.valueOf(numOfRooms));
   

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
    public List<Reservation> retrieveReservationByGuestId(Long guestId
    ) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.guest.guestId = :inId");
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

    @Override
    public Reservation retrieveReservationByOnlineGuestIdAndReservationId(Long guestId, Long reservationId) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.guest.guestId = :inId AND r.reservationId = :rId");

        query.setParameter("inId", guestId);
        query.setParameter("rId", reservationId);
        try {
            return (Reservation) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new ReservationNotFoundException("This reservation for this guest " + guestId + " does not exist.");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
