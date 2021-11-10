/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.Date;
import javax.ejb.Remote;

/**
 *
 * @author ANGELY
 */
@Remote
public interface RoomAllocationSessionBeanRemote {

    public void allocate(Date date);

    public void allocateAReservation(Reservation reservation);
    
}
