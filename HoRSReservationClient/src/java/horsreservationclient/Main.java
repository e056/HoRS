/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;


import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author ANGELY
 */
public class Main {

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    @EJB
    private  static ReservationSessionBeanRemote reservationSessionBeanRemote;

    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        MainApp ma = new MainApp(guestSessionBeanRemote, roomTypeSessionBeanRemote, roomRateSessionBeanRemote, reservationSessionBeanRemote);
        ma.runApp();
        
    }
    
}
