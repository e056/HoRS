/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.CreateNewReservationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author PYT
 */
@WebService(serviceName = "horswebservice")
@Stateless()
public class horswebservice {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialException {
        Partner partner = partnerSessionBeanLocal.partnerLogin(username, password);
        em.detach(partner);

        for (Reservation reservation : partner.getReservations()) {
            em.detach(reservation);
            reservation.setPartner(null);
        }

        return partner;

    }

    @WebMethod(operationName = "searchRoom")
    public void searchRoom() throws InvalidLoginCredentialException {

    }

    @WebMethod(operationName = "viewReservation")
    public List<Reservation> viewReservation(Long partnerId) throws PartnerNotFoundException {
        List<Reservation> reservations = partnerSessionBeanLocal.retrieveReservationsByPartnerId(partnerId);
        Partner partner = partnerSessionBeanLocal.retrievePartnerByPartnerId(partnerId);
        for (Reservation res : reservations) {
            em.detach(res);
            for (Room room : res.getAllocatedRooms()) {
                em.detach(room);
                for (Reservation reservation : room.getReservations()) {
                    room.getReservations().remove(reservation);
                }
            }
            Guest guest = res.getGuest();
            em.detach(guest);
            for (Reservation guestR : guest.getReservations()) {
                guest.getReservations().remove(guestR);
            }
            RoomType roomType = res.getRoomType();
            em.detach(roomType);
            for (Reservation roomTypeR : roomType.getReservations()) {
                roomType.getReservations().remove(roomTypeR);
            }

            if (res.getException() != null) {
                RoomAllocationException rae = res.getException();
                em.detach(rae);
                rae.setReservation(null);
            }

            em.detach(partner);
            for (Reservation partnerR : partner.getReservations()) {
                partner.getReservations().remove(partnerR);
            }

//            if(res.getWalkInGuest() != null)
//            {
//                W
//            }
//            WalkInGuest walkInGuest = res.getWalkInGuest();
//            em.detach(guest);
        }

        return reservations;

    }

    @WebMethod(operationName = "viewReservationDetails")
    public Reservation viewReservationDetails(Long reservationId) throws InvalidLoginCredentialException, ReservationNotFoundException {
        Reservation res = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId);
        em.detach(res);
        for (Room room : res.getAllocatedRooms()) {
            em.detach(room);
            for (Reservation reservation : room.getReservations()) {
                room.getReservations().remove(reservation);
            }
        }
        Guest guest = res.getGuest();
        em.detach(guest);
        for (Reservation guestR : guest.getReservations()) {
            guest.getReservations().remove(guestR);
        }
        RoomType roomType = res.getRoomType();
        em.detach(roomType);
        for (Reservation roomTypeR : roomType.getReservations()) {
            roomType.getReservations().remove(roomTypeR);
        }

        if (res.getException() != null) {
            RoomAllocationException rae = res.getException();
            em.detach(rae);
            rae.setReservation(null);
        }

        Partner partner = res.getPartner();

        em.detach(partner);
        for (Reservation partnerR : partner.getReservations()) {
            partner.getReservations().remove(partnerR);
        }

        return res;

    }

    @WebMethod(operationName = "searchRoom")
    public List<RoomType> searchRoom(int numOfRooms, Date start, Date end) {
        List<RoomType> rts = roomTypeSessionBeanLocal.retrieveRoomTypesAvailableForReservation(numOfRooms, start, end);
        for (RoomType rt : rts) {
            em.detach(rt);
            List<RoomRate> rr = rt.getRoomRates();
            for (RoomRate rate : rr) {
                em.detach(rate);
                rate.setRoomType(null);
            }
            List<Room> r = rt.getRooms();
            for (Room room : r) {
                em.detach(room);
                room.setRoomType(null);
            }
            List<Reservation> reservations = rt.getReservations();
            for (Reservation res : reservations) {
                em.detach(res);
                res.setRoomType(null);
            }
        }

        return rts;
    }

    @WebMethod(operationName = "onlineReservationAmount")
    public BigDecimal onlineReservationAmount(RoomType roomTypeToReserve, Date startDate, Date endDate, int numOfRooms) {
        BigDecimal price = reservationSessionBeanLocal.calculateFinalOnlineReservationAmount(roomTypeToReserve, startDate, endDate, numOfRooms);
        return price;
    }

    @WebMethod(operationName = "retrievePriceForOnlineReservationByRoomType")
    public BigDecimal retrievePriceForOnlineReservationByRoomType(Long roomTypeId, Date checkInDate, Date checkOutDate) {
        BigDecimal price = roomRateSessionBeanLocal.retrievePriceForOnlineReservationByRoomType(roomTypeId, checkInDate, checkOutDate);
        return price;
    }

    @WebMethod(operationName = "createNewOnlineReservation")
    public Reservation createNewOnlineReservation(Reservation reservation, Guest guest) throws RoomTypeNotFoundException, CreateNewReservationException {
        Reservation res = reservationSessionBeanLocal.createNewOnlineReservation(reservation, guest);
        em.detach(res);
        for (Room room : res.getAllocatedRooms()) {
            em.detach(room);
            for (Reservation ress : room.getReservations()) {
                room.getReservations().remove(reservation);
            }
        }

        em.detach(guest);
        for (Reservation guestR : guest.getReservations()) {
            guest.getReservations().remove(guestR);
        }
        RoomType roomType = res.getRoomType();
        em.detach(roomType);
        for (Reservation roomTypeR : roomType.getReservations()) {
            roomType.getReservations().remove(roomTypeR);
        }

        if (res.getException() != null) {
            RoomAllocationException rae = res.getException();
            em.detach(rae);
            rae.setReservation(null);
        }

        Partner partner = res.getPartner();

        em.detach(partner);
        for (Reservation partnerR : partner.getReservations()) {
            partner.getReservations().remove(partnerR);
        }
        return res;
    }

    @WebMethod(operationName = "retrieveRoomTypeByRoomId")
    public RoomType retrieveRoomTypeByRoomId(Long roomId) throws RoomTypeNotFoundException {
        RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomId(roomId);
        em.detach(rt);
        List<RoomRate> rr = rt.getRoomRates();
        for (RoomRate rate : rr) {
            em.detach(rate);
            rate.setRoomType(null);
        }
        List<Room> r = rt.getRooms();
        for (Room room : r) {
            em.detach(room);
            room.setRoomType(null);
        }
        List<Reservation> reservations = rt.getReservations();
        for (Reservation res : reservations) {
            em.detach(res);
            res.setRoomType(null);
        }
        return rt;
    }

    public void persist(Object object) {
        em.persist(object);
    }

}
