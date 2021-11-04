/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.ReservationNotFoundException;
import util.exception.RoomNotFoundException;

/**
 *
 * @author ANGELY
 */
@Local
public interface ReservationSessionBeanLocal {

    public List<Reservation> retrieveReservationsByRoomId(Long roomId) throws RoomNotFoundException;

    public List<Reservation> retrieveReservationsByDate(Date dateToday);

    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;
    
}