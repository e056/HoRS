/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import javax.ejb.EJB;
import ejb.session.stateful.WalkInRoomReservationSessionBeanRemote;

/**
 *
 * @author PYT
 */
public class Main {

    @EJB
    private static WalkInRoomReservationSessionBeanRemote walkInRoomReservationSessionBeanRemote;

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
                roomAllocationSessionBeanRemote, walkInRoomReservationSessionBeanRemote);

        mainApp.runApp();

    }

}
