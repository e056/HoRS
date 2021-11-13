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
import java.util.ArrayList;
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
import util.exception.InputDataValidationException;
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

//        for (Reservation reservation : partner.getReservations()) {
//            em.detach(reservation);
//            reservation.setPartner(null);
//        }
        partner.setReservations(null);

        return partner;

    }

    @WebMethod(operationName = "viewReservation")
    public List<Reservation> viewReservation(Long partnerId) throws PartnerNotFoundException {

        List<Reservation> reservations = partnerSessionBeanLocal.retrieveReservationsByPartnerId(partnerId);
        //Partner partner = partnerSessionBeanLocal.retrievePartnerByPartnerId(partnerId);
        for (Reservation res : reservations) {

            em.detach(res);
            res.setAllocatedRooms(null);
            res.setPartner(null);
            res.setRoomType(null);
            
        }

        return reservations;

    }
    
    @WebMethod(operationName = "retrieveRoomTypeNameByReservation")
    public String retrieveRoomTypeNameByReservation(Long reservationId) {
        return roomTypeSessionBeanLocal.retrieveRoomTypeNameByReservation(reservationId);
        
    }

    @WebMethod(operationName = "viewReservationDetails")
    public Reservation viewReservationDetails(Long reservationId) throws InvalidLoginCredentialException, ReservationNotFoundException {
        Reservation res = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId);

        em.detach(res);
        res.setRoomType(null);
        res.setPartner(null);
        return res;

    }

    @WebMethod(operationName = "searchRoom")
    public List<RoomType> searchRoom(int numOfRooms, Date start, Date end) {
        System.out.println("Here");
        List<RoomType> rts = roomTypeSessionBeanLocal.retrieveRoomTypesAvailableForReservation(numOfRooms, start, end);
        List<RoomType> newRts = new ArrayList<>();
        // RESERVATION : ROOMTYPE = N : 1 BIDIRECTIONAL
        
        for (RoomType rt : rts) {
            em.detach(rt);
            for (Reservation reservation : rt.getReservations()) {
                em.detach(reservation);
                reservation.setRoomType(null);

            }

            rt.setNextHigherRoomType(null);

            rt.getReservations().clear();
            rt.setRoomRates(null);
            rt.setRooms(null);
            newRts.add(rt);
        }

        return newRts;
    }

    @WebMethod(operationName = "onlineReservationAmount")
    public BigDecimal onlineReservationAmount(RoomType roomTypeToReserve, Date startDate,
            Date endDate, int numOfRooms
    ) {
        BigDecimal price = reservationSessionBeanLocal.calculateFinalOnlineReservationAmount(roomTypeToReserve, startDate, endDate, numOfRooms);
        return price;
    }

    @WebMethod(operationName = "retrievePriceForOnlineReservationByRoomType")
    public BigDecimal retrievePriceForOnlineReservationByRoomType(Long roomTypeId, Date checkInDate,
            Date checkOutDate
    ) {
        BigDecimal price = roomRateSessionBeanLocal.retrievePriceForOnlineReservationByRoomType(roomTypeId, checkInDate, checkOutDate);

        return price;
    }
    
    @WebMethod(operationName = "calculateFinalOnlineReservationAmount")
    public BigDecimal calculateFinalOnlineReservationAmount(RoomType roomTypeToReserve, Date startDate, Date endDate, int numOfRooms)  {
        return reservationSessionBeanLocal.calculateFinalOnlineReservationAmount(roomTypeToReserve, startDate, endDate, numOfRooms);
    }

    @WebMethod(operationName = "createNewOnlineReservation")
    public Reservation createNewOnlineReservation(Reservation reservation, Guest guest) throws RoomTypeNotFoundException, CreateNewReservationException, InputDataValidationException {
        Reservation res = reservationSessionBeanLocal.createNewOnlineReservation(reservation, guest);
        em.detach(res);
        res.getRoomType().setNextHigherRoomType(null);
        res.setPartner(null);
        //res.setRoomType(null);
        RoomType rt = res.getRoomType();
        List<Reservation> reservationsToDetatch = rt.getReservations();
        
        for(Reservation resToDetatch : reservationsToDetatch) {
            em.detach(resToDetatch);
            resToDetatch.getRoomType().setNextHigherRoomType(null);
            resToDetatch.setRoomType(null);
        }

        res.setAllocatedRooms(null);
        res.setGuest(null);
        return res;
    }

    @WebMethod(operationName = "createNewReservation")
    public Reservation createNewReservation(Reservation reservation) throws CreateNewReservationException, RoomTypeNotFoundException, InputDataValidationException {

        Reservation res = reservationSessionBeanLocal.createNewReservation(reservation);
        em.detach(res);
        res.setAllocatedRooms(null);
        res.setException(null);
        res.setPartner(null);
        res.setGuest(null);
        res.setRoomType(null);
//        List<Reservation> reservationsToRemove = new ArrayList<>();

//        for (Room room : res.getAllocatedRooms()) {
//            em.detach(room);
//            for (Reservation ress : room.getReservations()) {
//                reservationsToRemove.add(ress);
//
//            }
//            room.getReservations().removeAll(reservationsToRemove);
//        }
//        RoomType roomType = res.getRoomType();
//        em.detach(roomType);
//        
//        for (Reservation roomTypeR : roomType.getReservations()) {
//            roomType.getReservations().remove(roomTypeR);
//        }
//        
//        if (res.getException() != null) {
//            RoomAllocationException rae = res.getException();
//            em.detach(rae);
//            rae.setReservation(null);
//        }
//        
//        Partner partner = res.getPartner();
//        if (partner != null) {
//            em.detach(partner);
//            reservationsToRemove = new ArrayList<>();
//            for (Reservation partnerR : partner.getReservations()) {
//                reservationsToRemove.add(partnerR);
//                
//            }
//            partner.getReservations().removeAll(reservationsToRemove);
//            
//        }
        return res;

    }

    @WebMethod(operationName = "createNewPartnerReservation")
    public Reservation createNewPartnerReservation(Reservation reservation, Partner partner) throws CreateNewReservationException, RoomTypeNotFoundException, InputDataValidationException {

        Reservation res = reservationSessionBeanLocal.createNewPartnerReservation(reservation, partner);
        em.detach(res);
        res.setAllocatedRooms(null);
        res.setException(null);
        res.setPartner(null);
        res.setGuest(null);
        res.setRoomType(null);
//        List<Reservation> reservationsToRemove = new ArrayList<>();

//        for (Room room : res.getAllocatedRooms()) {
//            em.detach(room);
//            for (Reservation ress : room.getReservations()) {
//                reservationsToRemove.add(ress);
//
//            }
//            room.getReservations().removeAll(reservationsToRemove);
//        }
//        RoomType roomType = res.getRoomType();
//        em.detach(roomType);
//        
//        for (Reservation roomTypeR : roomType.getReservations()) {
//            roomType.getReservations().remove(roomTypeR);
//        }
//        
//        if (res.getException() != null) {
//            RoomAllocationException rae = res.getException();
//            em.detach(rae);
//            rae.setReservation(null);
//        }
//        
//        Partner partner = res.getPartner();
//        if (partner != null) {
//            em.detach(partner);
//            reservationsToRemove = new ArrayList<>();
//            for (Reservation partnerR : partner.getReservations()) {
//                reservationsToRemove.add(partnerR);
//                
//            }
//            partner.getReservations().removeAll(reservationsToRemove);
//            
//        }
        return res;

    }

    @WebMethod(operationName = "retrieveRoomTypeByRoomId")
    public RoomType retrieveRoomTypeByRoomId(Long roomId) throws RoomTypeNotFoundException {
        RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeId(roomId);
        em.detach(rt);
        rt.setNextHigherRoomType(null);
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

}
