/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.RoomAllocationException;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CreateNewReservationException;
import util.exception.NoRoomAllocationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author ANGELY
 */
@Remote
public interface ReservationSessionBeanRemote {

    //public List<Reservation> retrieveReservationsByRoomId(Long roomId) throws RoomNotFoundException;
    public List<Reservation> retrieveReservationsByDate(Date dateToday);

    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;

    public Reservation createNewReservation(Reservation reservation) throws CreateNewReservationException, RoomTypeNotFoundException;

    public List<Reservation> retrieveReservationByWalkInGuestId(Long guestId);

    public List<Reservation> retrieveCheckedInReservationByGuestId(Long guestId);

    public List<Reservation> retrieveCheckedInReservationByWalkInGuestId(Long guestId);

    public RoomAllocationException retrieveraeByReservationId(Long reservationId) throws NoRoomAllocationException;

    public void checkInGuest(Reservation reservation) throws ReservationNotFoundException;
    
    public void checkOutGuest(Reservation reservation) throws ReservationNotFoundException;

    public Reservation createNewOnlineReservation(Reservation reservation, Guest guest) throws RoomTypeNotFoundException, CreateNewReservationException;
    public void allocateReservation(Reservation reservation);

    public List<Reservation> retrieveReservationByGuestId(Long guestId);
}
