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
import entity.Guest;
import entity.Reservation;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import util.exception.CreateNewReservationException;
import util.exception.GuestPassportNumExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 */
public class MainApp {

    private GuestSessionBeanRemote guestSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;

    private Guest currGuest;

    public MainApp(GuestSessionBeanRemote guestSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote) {
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;

    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Hotel Reservation Client ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

                        menuMain();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doRegisterGuest();
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String passport = "";
        String password = "";

        System.out.println("*** HoRS Reservation System :: Login ***\n");
        System.out.print("Enter Passport Number> ");
        passport = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();

        if (passport.length() > 0 && password.length() > 0) {
            System.out.println("Logging in...");
            this.currGuest = guestSessionBeanRemote.guestLogin(passport, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    public void doRegisterGuest() {
        Scanner scanner = new Scanner(System.in);
        Guest newGuest = new Guest();

        System.out.println("*** HoRS Reservation Client :: Register As Guest ***\n");
        System.out.print("Enter First Name> ");
        newGuest.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newGuest.setLastName(scanner.nextLine().trim());
        System.out.print("Enter Passport Number> ");
        newGuest.setPassportNumber(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newGuest.setPassword(scanner.nextLine().trim());

        try {
            Long newGuestId = guestSessionBeanRemote.createNewGuest(newGuest);
            System.out.println("Successfully registered! Please log in with your credentials. ");
        } catch (GuestPassportNumExistException ex) {
            System.out.println("An error has occurred while creating the new guest: The passport number already exist\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An unknown error has occurred while creating the new guest: " + ex.getMessage() + "\n");
        }
    }

    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS Reservation Client ***\n");
            System.out.println("You are login as " + currGuest.getFullName());
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    searchRoom();
                } else if (response == 2) {
                    viewReservationDetails();

                } else if (response == 3) {
                    viewAllReservations();

                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    public void viewReservationDetails() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        System.out.println("***  Hotel Reservation System ::  View My Reservation Details ***\n");
        System.out.print("Enter Reservation ID [Use View All Reservations to find the ID]> ");
        long reservationId = scanner.nextLong();

        scanner.nextLine();

        try {
            Reservation r = reservationSessionBeanRemote.retrieveReservationByOnlineGuestIdAndReservationId(this.currGuest.getGuestId(), reservationId);
            System.out.printf("%8s%20s%20s%20s%20s%20s\n", "ID", "Num. Of Rooms", "Check-In", "Check-Out", "Room Type", "Total Price");
            System.out.printf("%8s%20s%20s%20s%20s%20s\n", r.getReservationId(), r.getNumOfRooms(),
                    df.format(r.getStartDate()), df.format(r.getEndDate()),
                    r.getRoomType().getName(), NumberFormat.getCurrencyInstance().format(r.getTotalPrice()));

        } catch (ReservationNotFoundException ex) {
            System.out.println("An error has occurred while retrieving Reservation: " + ex.getMessage() + "\n");
        }

    }

    public void viewAllReservations() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        System.out.println("***  Hotel Reservation System ::  View All Reservations ***\n");

        List<Reservation> reservations = reservationSessionBeanRemote.retrieveReservationByGuestId(this.currGuest.getGuestId());
        int count = 1;
        if (reservations.size() == 0) {
            System.out.println("Guest has no reservations.");
        }
        for (Reservation r : reservations) {
            System.out.printf("%8s%20s\n", "No.", "Reservation ID");
            System.out.printf("%8s%20s\n", count, r.getReservationId());
            count++;

        }
    }

    public void searchRoom() {
        System.out.println("*** Hotel Reservation System :: Search Room ***\n");
        Scanner scanner = new Scanner(System.in);
        String name = "";

        try {

            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/yy");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate;
            Date endDate;
            int numOfRooms;
            RoomType roomTypeToReserve;
            String comfirmReservation = "";

            System.out.print("Enter Number of Rooms> ");
            numOfRooms = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Check-In Date (dd/mm/yyyy)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Check-Out Date (dd/mm/yyyy)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.println("------------------------");

            long duration = endDate.getTime() - startDate.getTime();
            int days = (int) Math.round(TimeUnit.MILLISECONDS.toDays(duration));

            List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveRoomTypesAvailableForReservation(numOfRooms, startDate, endDate);
            System.out.printf("%8s%20s%30s%30s\n", "ID", "Room Type", "Price (each room)", "Total Price");
            for (RoomType rt : roomTypes) {
                BigDecimal priceEachRoom = roomRateSessionBeanRemote.retrievePriceForOnlineReservationByRoomType(rt.getRoomTypeId(), startDate, endDate);
                System.out.printf("%8s%20s%30s%30s\n", rt.getRoomTypeId(), rt.getName(),
                        NumberFormat.getCurrencyInstance().format(priceEachRoom),
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
                    roomTypeToReserve = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeId(scanner.nextLong());
                    scanner.nextLine();
                    if (!roomTypes.contains(roomTypeToReserve)) {
                        throw new CreateNewReservationException("This room type is not available for reservation! Cancelling...");
                    }
                } catch (RoomTypeNotFoundException ex) {
                    System.out.println("An error has occurred while retrieving Room Type: " + ex.getMessage() + "\n");
                    System.out.println("Cancelling reservation...");
                    return;
                }

                System.out.println("Reserving the following:\n");
                System.out.printf("%20s%20s%30s\n", "Room Type", "Num of Rooms", "Total Price");
                BigDecimal totalPrice = roomRateSessionBeanRemote.retrievePriceForOnlineReservationByRoomType(roomTypeToReserve.getRoomTypeId(), startDate, endDate);
                totalPrice = totalPrice.multiply(BigDecimal.valueOf(numOfRooms));

                System.out.printf("%20s%20s%30s\n", roomTypeToReserve.getName(), numOfRooms, NumberFormat.getCurrencyInstance().format(totalPrice));
                System.out.print("Confirm? ('Y' to confirm)> ");

                comfirmReservation = scanner.nextLine().trim();

                if (comfirmReservation.equals("Y")) {
                    Reservation reservation = new Reservation(startDate, endDate, numOfRooms, totalPrice, roomTypeToReserve);
                    reservation.setAllocated(false);

                    reservation = reservationSessionBeanRemote.createNewOnlineReservation(reservation, currGuest);

                    System.out.println("Reservation completed successfully!: " + reservation.getReservationId() + "\n");

                } else {
                    System.out.println("Cancelled reservation.");

                }
            }

        } catch (CreateNewReservationException ex) {
            System.out.println("Error when creating new Reservation: " + ex.getMessage());
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Error when creating new Reservation: " + ex.getMessage());
        } catch (java.text.ParseException ex) {
            System.out.println("Invalid date input!");
        }

    }

}
