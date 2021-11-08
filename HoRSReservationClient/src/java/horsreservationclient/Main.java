/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateful.RoomReservationSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author ANGELY
 */
public class Main {

    @EJB
    private static RoomReservationSessionBeanRemote roomReservationSessionBean;

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp ma = new MainApp(roomReservationSessionBean, guestSessionBeanRemote);
        ma.runApp();
        
        
    }
    
}