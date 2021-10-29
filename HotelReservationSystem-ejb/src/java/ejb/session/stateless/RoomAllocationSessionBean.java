/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import javax.ejb.EJB;
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

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    
}
