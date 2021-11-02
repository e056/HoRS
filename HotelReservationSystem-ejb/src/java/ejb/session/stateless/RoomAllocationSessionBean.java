/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 *
 * @author ANGELY
 * Retrieve a list of all reservations for check-in on the current 
 * date and allocate the required room(s) for the reserved room 
 * type. If the required room(s) for the reserved room type is not 
 * available, raise an exception in the exception report (see use 
 * case 16).
 * This use case should normally be triggered by a server-side 
 * timer daily at 2 am. But for development and evaluation 
 * purposes, the system should also allow the use case to be 
 * triggered arbitrarily.
 */
@Stateless
public class RoomAllocationSessionBean implements RoomAllocationSessionBeanRemote, RoomAllocationSessionBeanLocal {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @Schedule(dayOfWeek = "*", hour = "2")
    public void allocateRoomToReservation()
    {
        Date dateToday = new Date();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println("********** RoomAllocationSessionBean.allocateRoomToReservation(): Timeout at " + timeStamp);
        List<Reservation> reservations = reservationSessionBeanLocal.retrieveReservationsByDate(dateToday);
        
        
    }
    
    
}
