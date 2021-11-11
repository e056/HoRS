/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.CreateNewReservationException_Exception;
import ws.client.Horswebservice_Service;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.Partner;
import ws.client.PartnerNotFoundException_Exception;
import ws.client.Reservation;
import ws.client.ReservationNotFoundException_Exception;
import ws.client.RoomTypeNotFoundException_Exception;

/**
 *
 * @author PYT
 */
public class HolidayReservationSystemClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Horswebservice_Service service = new Horswebservice_Service();
        //to call methods enter: service.getHorswebservicePort().<insert method here>
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2: Exit\n");

            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {

                    doLogin();
                    System.out.println("Login successful!\n");

                } else if (response == 2) {
                    break;
                }

            }
        }
    }

    private static void doLogin() {
        Scanner scanner = new Scanner(System.in);
        Horswebservice_Service service = new Horswebservice_Service();
        Integer response = 0;

        String username = "";
        String password = "";

        System.out.println("*** Holiday Reservation System :: Partner Login ***\n");
        System.out.print("Enter Username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();

        try {
            Partner partner = service.getHorswebservicePort().partnerLogin(username, password);
            menuMain(partner);
        } catch (InvalidLoginCredentialException_Exception ex) {
            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
        }

    }

    private static void menuMain(Partner partner) {
        Scanner scanner = new Scanner(System.in);
        Horswebservice_Service service = new Horswebservice_Service();
        Integer response = 0;
        while (true) {
            System.out.println("*** Holiday Reservation System :: Logged In***\n");
            System.out.println("1: Search room");
            System.out.println("2: View details for a reservation");
            System.out.println("3: View all reservations");
            System.out.println("4: Partner Logout\n");

            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doSearchRoom();
                } else if (response == 2) {
                    doViewReservationDetails();
                } else if (response == 3) {
                    doViewAllReservations(partner);
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }

            }
        }
    }

    private static void doSearchRoom() {
        try {
            Scanner scanner = new Scanner(System.in);
            Horswebservice_Service service = new Horswebservice_Service();
            System.out.println("*** Holiday Reservation System :: Partner Search Room ***\n");
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate;
            Date endDate;
            int numOfRooms;
            ws.client.RoomType roomTypeToReserve;
            String comfirmReservation = "";

            System.out.print("Enter Number of Rooms> ");
            numOfRooms = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Check-In Date (dd/mm/yyyy)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Check-Out Date (dd/mm/yyyy)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());

            GregorianCalendar c = new GregorianCalendar();
            c.setTime(startDate);
            XMLGregorianCalendar startDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

            c = new GregorianCalendar();
            c.setTime(endDate);
            XMLGregorianCalendar endDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            
            System.out.println(startDateXML);
            System.out.println(endDateXML);

            List<ws.client.RoomType> roomTypes = service.getHorswebservicePort().searchRoom(numOfRooms, startDateXML, endDateXML);
            System.out.printf("%8s%20s%30s\n", "ID", "Room Type", "Total Price");
            for (ws.client.RoomType rt : roomTypes) {
                BigDecimal priceEachRoom = service.getHorswebservicePort().retrievePriceForOnlineReservationByRoomType(rt.getRoomTypeId(), startDateXML, endDateXML);
                System.out.printf("%8s%20s%30s\n", rt.getRoomTypeId(), rt.getName(),
                        NumberFormat.getCurrencyInstance().format(priceEachRoom.multiply(BigDecimal.valueOf(numOfRooms))));
     
            }
            
              System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            if (response == 1) {

                System.out.print("Enter Room Type Id to reserve> ");
                try {
                    roomTypeToReserve = service.getHorswebservicePort().retrieveRoomTypeByRoomId(scanner.nextLong());
                    scanner.nextLine();
   
                } catch (ws.client.RoomTypeNotFoundException_Exception ex) {
                    System.out.println("An error has occurred while retrieving Room Type: " + ex.getMessage() + "\n");
                    System.out.println("Cancelling reservation...");
                    return;
                }

                System.out.println("Reserving the following:\n");
                System.out.printf("%20s%20s%30s\n", "Room Type", "Num of Rooms", "Total Price");
                BigDecimal totalPrice = service.getHorswebservicePort().retrievePriceForOnlineReservationByRoomType(roomTypeToReserve.getRoomTypeId(), startDateXML, endDateXML);
                totalPrice = totalPrice.multiply(BigDecimal.valueOf(numOfRooms));

                System.out.printf("%20s%20s%30s\n", roomTypeToReserve.getName(), numOfRooms, NumberFormat.getCurrencyInstance().format(totalPrice));
                System.out.print("Confirm? ('Y' to confirm)> ");

                comfirmReservation = scanner.nextLine().trim();

                if (comfirmReservation.equals("Y")) {
                    ws.client.Reservation reservation = new Reservation();
                    reservation.setStartDate(startDateXML);
                    reservation.setEndDate(endDateXML);
                    reservation.setNumOfRooms(numOfRooms);
                    reservation.setTotalPrice(totalPrice);
                    reservation.setRoomType(roomTypeToReserve);
                    // ws.client.Reservation reservation = new ws.client.Reservation(startDate, endDate, numOfRooms, totalPrice, roomTypeToReserve);
                    
                    reservation.setAllocated(false);

                    reservation = service.getHorswebservicePort().createNewReservation(reservation);

                    //System.out.println("Reservation completed successfully!: " + reservation.getReservationId() + "\n");

                } else {
                    System.out.println("Cancelled reservation.");

                }
            }
            
            
            
        } catch (ParseException ex) {
            System.out.println("Invalid Date Format!");
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(HolidayReservationSystemClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CreateNewReservationException_Exception ex) {
            Logger.getLogger(HolidayReservationSystemClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RoomTypeNotFoundException_Exception ex) {
            Logger.getLogger(HolidayReservationSystemClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void doViewReservationDetails() {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Horswebservice_Service service = new Horswebservice_Service();
        System.out.println("*** Holiday Reservation System :: View Reservation Details ***\n");
        Long resId;
        System.out.print("Enter reservation Id>");
        resId = scanner.nextLong();

        try {
            Reservation r = service.getHorswebservicePort().viewReservationDetails(resId);
            System.out.printf("%8s%20s%20s%20s%20s%20s\n", "ID", "Num. Of Rooms", "Check-In", "Check-Out", "Room Type", "Total Price");
            System.out.printf("%8s%20s%20s%20s%20s%20s\n", r.getReservationId(), r.getNumOfRooms(),
                    df.format(r.getStartDate()), df.format(r.getEndDate()),
                    r.getRoomType().getName(), NumberFormat.getCurrencyInstance().format(r.getTotalPrice()));
        } catch (InvalidLoginCredentialException_Exception ex) {
            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
        } catch (ReservationNotFoundException_Exception ex) {
            System.out.println("An error has occurred while viewing reservation details: " + ex.getMessage() + "\n");
        }

    }

    private static void doViewAllReservations(Partner partner) {
        Horswebservice_Service service = new Horswebservice_Service();
        System.out.println("***  Holiday Reservation System ::  View All Reservations ***\n");
        try {
            List<Reservation> reservations = service.getHorswebservicePort().viewReservation(partner.getPartnerId());
            int count = 1;
            if (reservations.isEmpty()) {
                System.out.println("Partner has no reservations.");
            }
            for (Reservation r : reservations) {
                System.out.printf("%8s%20s\n", "No.", "Reservation ID");
                System.out.printf("%8s%20s\n", count, r.getReservationId());
                count++;

            }
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("An error has occurred while viewing all reservations: " + ex.getMessage() + "\n");
        }
    }

}
