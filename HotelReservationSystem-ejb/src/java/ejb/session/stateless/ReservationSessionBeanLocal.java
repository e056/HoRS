/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author ANGELY
 */
@Local
public interface ReservationSessionBeanLocal {

    public List<Reservation> retrieveReservationsByDate(Date dateToday);

    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;

    public Reservation createNewReservation(Reservation reservation) throws CreateNewReservationException, RoomTypeNotFoundException, InputDataValidationException;

//    public List<Reservation> retrieveReservationByWalkInGuestId(Long guestId);

//    public List<Reservation> retrieveCheckedInReservationByGuestId(Long guestId);

//    public List<Reservation> retrieveCheckedInReservationByWalkInGuestId(Long guestId);

    public void allocateReservation(Reservation reservation);

    public List<Reservation> retrieveReservationByGuestId(Long guestId);

    public Reservation retrieveReservationByOnlineGuestIdAndReservationId(Long guestId, Long reservationId) throws ReservationNotFoundException;

    public BigDecimal calculateFinalOnlineReservationAmount(RoomType roomTypeToReserve, Date startDate, Date endDate, int numOfRooms);
    
   public Reservation createNewOnlineReservation(Reservation reservation, Guest guest) throws RoomTypeNotFoundException, CreateNewReservationException, InputDataValidationException;

    public Reservation createNewPartnerReservation(Reservation reservation, Partner partner) throws RoomTypeNotFoundException, CreateNewReservationException, InputDataValidationException;
    
  
}
