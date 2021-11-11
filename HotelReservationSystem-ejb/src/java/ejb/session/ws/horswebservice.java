/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomType;
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
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author PYT
 */
@WebService(serviceName = "horswebservice")
@Stateless()
public class horswebservice {

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

}
