/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CreateNewReservationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomNotFoundException;

/**
 *
 * @author ANGELY
 */
@Remote
public interface ReservationSessionBeanRemote {

    //public List<Reservation> retrieveReservationsByRoomId(Long roomId) throws RoomNotFoundException;
    public List<Reservation> retrieveReservationsByDate(Date dateToday);

    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;

    public Reservation createNewReservation(Reservation reservation) throws CreateNewReservationException;
}
