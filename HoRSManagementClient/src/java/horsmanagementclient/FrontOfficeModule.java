/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import entity.Employee;
import entity.Reservation;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import util.enumeration.AccessRightEnum;
import util.exception.CreateNewReservationException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomRateNotFoundException;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import ejb.session.stateless.WalkInGuestSessionBeanRemote;
import entity.Guest;
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomType;
import entity.WalkInGuest;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.GuestNotFoundException;
import util.exception.NoRoomAllocationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.WalkInGuestNotFoundException;

/**
 *
 * @author ANGELY
 */
public class FrontOfficeModule {

    private RoomTypeSessionBeanRemote roomeTypeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private Employee currEmployee;
    private WalkInGuestSessionBeanRemote walkInGuestSessionBeanRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;

    public FrontOfficeModule(RoomTypeSessionBeanRemote roomeTypeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, Employee currEmployee, WalkInGuestSessionBeanRemote walkInGuestSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote) {
        this.roomeTypeSessionBeanRemote = roomeTypeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.currEmployee = currEmployee;
        this.walkInGuestSessionBeanRemote = walkInGuestSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
    }

    public void menuFrontOffice() throws InvalidAccessRightException {
        if (currEmployee.getAccessRightEnum() != AccessRightEnum.GUEST_RELATION) {
            throw new InvalidAccessRightException("You don't have GUEST RELATION OFFICER rights to access the front office operation module.");
        }
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Front Office ***\n");
            System.out.println("1: Walk-in Search Room");
            System.out.println("2: Check-In Guest");
            System.out.println("3: Check-Out Guest");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    searchRoom();
                } else if (response == 2) {
                    checkInGuest();
                } else if (response == 3) {
                    checkOutGuest();
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

    public void searchRoom() {
        System.out.println("*** Hotel Reservation System :: Walk-In Search Room ***\n");
        Scanner scanner = new Scanner(System.in);
        String passportNo = "";
        String name = "";
        WalkInGuest guest = new WalkInGuest();
        Long guestId = new Long(0);

        System.out.print("Enter passport number>");
        passportNo = scanner.nextLine().trim();
        boolean noAccount = false;

        try {
            guest = walkInGuestSessionBeanRemote.retrieveWalkInGuestByPassportNo(passportNo);
            guestId = guest.getWalkInGuestId();
            System.out.println("Guest Found!");
        } catch (WalkInGuestNotFoundException ex) {
            System.out.println("Guest not registered!");
            System.out.print("Enter name>");
            name = scanner.nextLine().trim();
            noAccount = true;
        } finally {
            if (noAccount) {
                guest.setPassportNumber(passportNo);
                guest.setName(name);
            }
        }

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

            List<RoomType> roomTypes = roomeTypeSessionBeanRemote.retrieveRoomTypesAvailableForReservation(numOfRooms, startDate, endDate);
            System.out.printf("%8s%20s%30s%30s\n", "ID", "Room Type", "Rate per night of stay", "Total price for stay");
            for (RoomType rt : roomTypes) {
                System.out.printf("%8s%20s%30s%30s\n", rt.getRoomTypeId(), rt.getName(),
                        NumberFormat.getCurrencyInstance().format(roomRateSessionBeanRemote.retrievePublishedRoomRateByRoomType(rt.getRoomTypeId()).getRatePerNight()),
                        NumberFormat.getCurrencyInstance().format(roomRateSessionBeanRemote.retrievePublishedRoomRateByRoomType(rt.getRoomTypeId()).getRatePerNight().multiply(BigDecimal.valueOf(days))));
            }

            System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();

            if (response == 1) {

                System.out.print("Enter Room Type Name to reserve> ");
                try {
                    roomTypeToReserve = roomeTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeName(scanner.nextLine().trim());
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
                BigDecimal totalPrice = roomRateSessionBeanRemote.retrievePublishedRoomRateByRoomType(roomTypeToReserve.getRoomTypeId()).getRatePerNight().multiply(BigDecimal.valueOf(days));

                System.out.printf("%20s%20s%30s\n", roomTypeToReserve.getName(), numOfRooms, totalPrice);
                System.out.print("Confirm? ('Y' to confirm)> ");

                comfirmReservation = scanner.nextLine().trim();

                if (comfirmReservation.equals("Y")) {
                    Reservation reservation = new Reservation(startDate, endDate, numOfRooms, totalPrice, roomTypeToReserve);
                    reservation.setAllocated(false);

                    reservation = reservationSessionBeanRemote.createNewReservation(reservation);
                    if (noAccount) {
                        Long id = walkInGuestSessionBeanRemote.createNewWalkInGuest(guest, reservation.getReservationId());
                    } else {
                        walkInGuestSessionBeanRemote.associateGuestWithReservation(reservation, guestId);
                    }

                    Date currDate = new java.util.Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat dateWithTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aa");
                    System.out.println("The time now is: " + dateWithTimeFormat.format(currDate));
                    String dateString2am = dateFormat.format(currDate) + " 02:00:00 AM";
                    String dateStringOnly = dateFormat.format(currDate);
                    
                    if (dateFormat.parse(dateStringOnly).compareTo(startDate) == 0) {
                        if (dateWithTimeFormat.parse(dateString2am).compareTo(currDate) < 0) {
                            System.out.println("Same day check-in after 2am, allocating rooms... ");
                            reservationSessionBeanRemote.allocateReservation(reservation);

                        }
                    }

                    System.out.println("Reservation completed successfully!: " + reservation.getReservationId() + "\n");

                } else {
                    System.out.println("Cancelled reservation.");

                }
            }

        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (RoomRateNotFoundException ex) {
            System.out.println("An error occured: Room type has no published rate.");
        } catch (CreateNewReservationException ex) {
            System.out.println("Error when creating new Reservation: " + ex.getMessage());
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Error when creating new Reservation: " + ex.getMessage());
        } catch (UnknownPersistenceException ex) {
            Logger.getLogger(FrontOfficeModule.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void checkInGuest() {
        Scanner scanner = new Scanner(System.in);
        String passportNo;
        System.out.println("*** Hotel Reservation System :: Check In Guest ***\n");
        System.out.print("Enter your passport number>");
        passportNo = scanner.nextLine().trim();

        boolean isGuest = false;
        boolean isWalkInGuest = false;
        int type = 0;
        WalkInGuest walkInGuest = new WalkInGuest();
        Guest guest = new Guest();
        try {
            walkInGuest = walkInGuestSessionBeanRemote.retrieveWalkInGuestByPassportNo(passportNo);
            isWalkInGuest = true;
            System.out.println("Walk in Guest found! ");
        } catch (WalkInGuestNotFoundException ex) {
            System.out.println("Walk in Guest not found!");
        }

        try {
            guest = guestSessionBeanRemote.retrieveGuestByPassportNum(passportNo);
            isGuest = true;
        } catch (GuestNotFoundException ex) {
            System.out.println("Guest not found!");
        }

        if (isGuest && isWalkInGuest) {
            System.out.print("Please select select guest (1 for walk-in, 2 for registered)>");
            type = scanner.nextInt();
            if (type == 1) {
                doCheckInWalkInGuest(walkInGuest);
            } else if (type == 2) {
                doCheckInGuest(guest);
            } else {
                System.out.println("Invalid input! try again");
            }
        } else if (isWalkInGuest && !isGuest) {
            doCheckInWalkInGuest(walkInGuest);
        } else if (isGuest && !isWalkInGuest) {
            doCheckInGuest(guest);
        } else if (!isGuest && !isWalkInGuest) {
            System.out.println("Guest not found!");
        }

    }

    private void checkOutGuest() {
        Scanner scanner = new Scanner(System.in);
        String passportNo;
        System.out.println("*** Hotel Reservation System :: Check Out Guest ***\n");
        System.out.print("Enter your passport number>");
        passportNo = scanner.nextLine().trim();
        String ans = "";
        try {
            WalkInGuest guest = walkInGuestSessionBeanRemote.retrieveWalkInGuestByPassportNo(passportNo);
            List<Reservation> checkedIn = reservationSessionBeanRemote.retrieveCheckedInReservationByGuestId(guest.getWalkInGuestId());
            if (checkedIn.isEmpty()) {
                System.out.println("Guest is currently not checked into any reservation!");
            } else {
                System.out.printf("%20s%20s\n", "Reservation Id", "Num of Rooms");
                for (Reservation res : checkedIn) {

                    System.out.printf("%20s%20s\n", res.getReservationId(), res.getAllocatedRooms().size());
                    System.out.print("Checkout? ('Y' to checkout)");
                    ans = scanner.nextLine().trim();
                    if (ans.equals("Y")) {
                        reservationSessionBeanRemote.checkOutGuest(res);
                        System.out.println("You have successfully checked out from Reservation " + res.getReservationId());
                    } else {
                        System.out.println("Check out cancelled.");
                    }
                }
            }
        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation not found.");
        } catch (WalkInGuestNotFoundException ex) {
            System.out.println("Walk-In Guest not found.");
        }

    }

    private void doCheckInWalkInGuest(WalkInGuest walkInGuest) {
        Scanner scanner = new Scanner(System.in);
        boolean checkedIn = false;
        Reservation res = new Reservation();
        try {

            System.out.println("List Of reservation to check in:\n");

            System.out.printf("%20s%20s%30s\n", "Reservation ID", "Start date", "End date");
            List<Reservation> reslist = reservationSessionBeanRemote.retrieveReservationByWalkInGuestId(walkInGuest.getWalkInGuestId());
            for (Reservation ress : reslist) {
                System.out.printf("%5s%30s%30s\n", ress.getReservationId(), ress.getStartDate(), ress.getEndDate());
            }

            System.out.print("Select reservation to check in>");
            Long id = scanner.nextLong();

            res = reservationSessionBeanRemote.retrieveReservationByReservationId(id);

            System.out.printf("%20s%20s%30s\n", "Room Id", "Room Type", "Room Number");

            for (Room allocatedRooms : res.getAllocatedRooms()) {
                System.out.printf("%5s%20s%20s\n", allocatedRooms.getRoomId(), allocatedRooms.getRoomType().getName(), allocatedRooms.getRoomNumber());
            }

            if (!res.getAllocatedRooms().isEmpty()) {
                reservationSessionBeanRemote.checkInGuest(res);
            }

            if (res.getException() != null) {
                RoomAllocationException rae = reservationSessionBeanRemote.retrieveraeByReservationId(id);
//                for (Room allocatedRooms : rae.getTypeOneExceptions()) {
//                    System.out.printf("%20s%20s%30s\n", allocatedRooms.getRoomId(), allocatedRooms.getRoomType().getName(), allocatedRooms.getRoomNumber());
//                }
                int numTypeTwo = rae.getNumOfTypeTwo();
                if (numTypeTwo > 0) {
                    System.out.println(numTypeTwo + " room(s) were unable to be allocated!");
                    if (numTypeTwo == res.getNumOfRooms()) {
                        reservationSessionBeanRemote.checkOutGuest(res);
                    } else {
                        reservationSessionBeanRemote.checkInGuest(res);
                    }
                } else {
                    reservationSessionBeanRemote.checkInGuest(res);
                }

            }
            reservationSessionBeanRemote.checkInGuest(res);
            System.out.println("Guest has been successfully checked-in.");

        } catch (ReservationNotFoundException ex) {
            System.out.println("Error when checking in:" + ex.getMessage());
        } catch (NoRoomAllocationException ex) {
            Logger.getLogger(FrontOfficeModule.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private void doCheckInGuest(Guest guest) {
        Scanner scanner = new Scanner(System.in);
        try {

            System.out.println("Select which reservation to check in:\n");
            System.out.printf("%20s%20s%30s\n", "Reservation ID", "Start date", "End date");
            List<Reservation> reslist = guest.getReservations();
            for (Reservation res : reslist) {
                System.out.printf("%20s%20s%30s\n", res.getReservationId(), res.getStartDate(), res.getEndDate());
            }
            Long id = scanner.nextLong();
            Reservation res = reservationSessionBeanRemote.retrieveReservationByReservationId(id);
            res.setCheckedIn(true);
            System.out.printf("%20s%20s%30s\n", "Room Id", "Room Type", "Room Number");
            for (Room allocatedRooms : res.getAllocatedRooms()) {
                System.out.printf("%20s%30s%30s\n", allocatedRooms.getRoomId(), allocatedRooms.getRoomType().getName(), allocatedRooms.getRoomNumber());
            }
            if (res.getException() != null) {
                RoomAllocationException rae = reservationSessionBeanRemote.retrieveraeByReservationId(id);
                for (Room allocatedRooms : rae.getTypeOneExceptions()) {
                    System.out.printf("%20s%20s%30s\n", allocatedRooms.getRoomId(), allocatedRooms.getRoomType(), allocatedRooms.getRoomNumber());
                }
                int numTypeTwo = rae.getNumOfTypeTwo();
                if (numTypeTwo > 0) {
                    System.out.println(numTypeTwo + " room(s) were unable to be allocated!");
                }
            }

        } catch (ReservationNotFoundException ex) {
            System.out.println("Error when checking in:" + ex.getMessage());
        } catch (NoRoomAllocationException ex) {
            System.out.println("Reservation has no allocation exceptions!");
        }
    }

}
