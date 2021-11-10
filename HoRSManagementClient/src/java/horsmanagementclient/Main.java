/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import ejb.session.stateless.WalkInGuestSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author PYT
 */
public class Main {

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    @EJB
    private static WalkInGuestSessionBeanRemote walkInGuestSessionBeanRemote;

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;

    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    @EJB
    private static RoomSessionBeanRemote roomSessionBeanRemote;

    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;

    @EJB
    private static RoomAllocationSessionBeanRemote roomAllocationSessionBeanRemote;

    @EJB
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    
    
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //MainApp mainApp = new MainApp(partnerSessionBeanRemote, employeeSessionBeanRemote); // will need to be changed as more beans are finished
        MainApp mainApp = new MainApp(partnerSessionBeanRemote,
                employeeSessionBeanRemote, roomTypeSessionBeanRemote,
                roomSessionBeanRemote, roomRateSessionBeanRemote,
                roomAllocationSessionBeanRemote, reservationSessionBeanRemote, walkInGuestSessionBeanRemote, guestSessionBeanRemote);

        mainApp.runApp();

    }

}
