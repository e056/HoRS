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
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomType;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.NoRoomAllocationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author ANGELY
 */
public class FrontOfficeModule {

    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private Employee currEmployee;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private GuestSessionBeanRemote guestSessionBeanRemote;

    public FrontOfficeModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public FrontOfficeModule(RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, Employee currEmployee, GuestSessionBeanRemote guestSessionBeanRemote) {
        this();
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.currEmployee = currEmployee;

        this.guestSessionBeanRemote = guestSessionBeanRemote;
    }

    public void menuFrontOffice() throws InvalidAccessRightException {
        if (currEmployee.getAccessRightEnum() != AccessRightEnum.GUEST_RELATION && currEmployee.getAccessRightEnum() != AccessRightEnum.SYSTEM_ADMIN) {
            throw new InvalidAccessRightException("You don't have SYSADMIN or GUEST RELATION OFFICER rights to access the front office operation module.");
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
                    doCheckInGuest();
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

//        try {
//            guest = walkInGuestSessionBeanRemote.retrieveWalkInGuestByPassportNo(passportNo);
//            guestId = guest.getWalkInGuestId();
//            System.out.println("Guest Found!");
//        } catch (WalkInGuestNotFoundException ex) {
//            System.out.println("Guest not registered!");
//            System.out.print("Enter name> ");
//            name = scanner.nextLine().trim();
//            noAccount = true;
//        } finally {
//            if (noAccount) {
//                guest.setPassportNumber(passportNo);
//                guest.setName(name);
//            }
//        }
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
            System.out.print("Enter Check-In Date (dd/MM/yyyy)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Check-Out Date (dd/MM/yyyy)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.println("------------------------");
//
//            long duration = endDate.getTime() - startDate.getTime();
//            int days = (int) Math.round(TimeUnit.MILLISECONDS.toDays(duration));

            List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveRoomTypesAvailableForReservation(numOfRooms, startDate, endDate);
            System.out.printf("%8s%20s%30s\n", "ID", "Room Type", "Total Price");
            for (RoomType rt : roomTypes) {
                BigDecimal price = reservationSessionBeanRemote.calculateFinalWalkInReservationAmount(rt, startDate, endDate, numOfRooms);
                System.out.printf("%8s%20s%30s\n", rt.getRoomTypeId(), rt.getName(),
                        NumberFormat.getCurrencyInstance().format(price));
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
                System.out.printf("%20s%20s%30s%30s%30s\n", "Room Type", "Num of Rooms", "Total Price", "CI", "CO");
                BigDecimal totalPrice = reservationSessionBeanRemote.calculateFinalWalkInReservationAmount(roomTypeToReserve, startDate, endDate, numOfRooms);

                System.out.printf("%20s%20s%30s%30s%30s\n", roomTypeToReserve.getName(), numOfRooms, NumberFormat.getCurrencyInstance().format(totalPrice),
                        outputDateFormat.format(startDate), outputDateFormat.format(endDate));
                System.out.print("Confirm? ('Y' to confirm)> ");

                comfirmReservation = scanner.nextLine().trim();

                if (comfirmReservation.equals("Y")) {
                    Reservation reservation = new Reservation(startDate, endDate, numOfRooms, totalPrice, roomTypeToReserve);
                    reservation.setAllocated(false);

                    Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);

                    if (constraintViolations.isEmpty()) {
                        reservation = reservationSessionBeanRemote.createNewReservation(reservation);
//                    if (noAccount) {
//                        Long id = walkInGuestSessionBeanRemote.createNewWalkInGuest(guest, reservation.getReservationId());
//                    } else {
//                        walkInGuestSessionBeanRemote.associateGuestWithReservation(reservation, guestId);
//                    }

                        System.out.println("Reservation completed successfully! Id = " + reservation.getReservationId() + "\n");
                    } else {
                        showInputDataValidationErrorsForReservation(constraintViolations);
                    }
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
        } catch (InputDataValidationException ex) {
            Logger.getLogger(FrontOfficeModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    private void checkInGuest() {
//        Scanner scanner = new Scanner(System.in);
//        Long resId;
//        System.out.println("*** Hotel Reservation System :: Check In Guest ***\n");
//      
//        resId = scanner.nextLong();
//
////        boolean isGuest = false;
////        boolean isWalkInGuest = false;
////        int type = 0;
////        WalkInGuest walkInGuest = new WalkInGuest();
////        Guest guest = new Guest();
////        try {
////            walkInGuest = walkInGuestSessionBeanRemote.retrieveWalkInGuestByPassportNo(passportNo);
////            isWalkInGuest = true;
////            System.out.println("Walk in Guest found! ");
////        } catch (WalkInGuestNotFoundException ex) {
////            System.out.println("Walk in Guest not found!");
////        }
//
////        try {
////            guest = guestSessionBeanRemote.retrieveGuestByPassportNum(passportNo);
////            isGuest = true;
////        } catch (GuestNotFoundException ex) {
////            System.out.println("Guest not found!");
////        }
//
//        if (isGuest && isWalkInGuest) {
//            System.out.print("Please select select guest (1 for walk-in, 2 for registered)>");
//            type = scanner.nextInt();
//            if (type == 1) {
//                doCheckInWalkInGuest(walkInGuest);
//            } else if (type == 2) {
//                doCheckInGuest(guest);
//            } else {
//                System.out.println("Invalid input! try again");
//            }
//        } else if (isWalkInGuest && !isGuest) {
//            doCheckInWalkInGuest(walkInGuest);
//        } else if (isGuest && !isWalkInGuest) {
//            doCheckInGuest(guest);
//        } else if (!isGuest && !isWalkInGuest) {
//            System.out.println("Guest not found!");
//        }
//
//    }
    private void checkOutGuest() {
        Scanner scanner = new Scanner(System.in);
        Long resId;
        System.out.println("*** Hotel Reservation System :: Check Out Guest ***\n");
        System.out.print("Enter your reservation Id> ");
        resId = scanner.nextLong();

        try {

            Reservation res = reservationSessionBeanRemote.retrieveReservationByReservationId(resId);
            if (!res.isCheckedIn()) {
                System.out.println("Guest is currently not checked into this reservation!");
            } else if (res.isCheckedOut()) {
                System.out.println("Guest has already checked out!");
            } else {
                reservationSessionBeanRemote.checkOutGuest(res);
                System.out.println("You have successfully checked out from Reservation " + res.getReservationId());

            }

        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation not found.");
        }

    }

    private void doCheckInGuest() {
        SimpleDateFormat df = new SimpleDateFormat("d/M/yy");
        Scanner scanner = new Scanner(System.in);
        Long resId;
        System.out.println("*** Hotel Reservation System :: Check In Guest ***\n");
        System.out.print("Enter reservation Id> ");
        resId = scanner.nextLong();

        try {

            System.out.println("List Of reservation to check in:\n");

            System.out.printf("%20s%20s%20s\n", "Reservation ID", "Start date", "End date");
            Reservation res = reservationSessionBeanRemote.retrieveReservationByReservationId(resId);
            System.out.printf("%20s%20s%20s\n", res.getReservationId(), df.format(res.getStartDate()), df.format(res.getEndDate()));

            System.out.printf("%20s%20s%20s\n", "Room Id", "Room Type", "Room Number");

            for (Room allocatedRooms : res.getAllocatedRooms()) {
                System.out.printf("%20s%20s%20s\n", allocatedRooms.getRoomId(), allocatedRooms.getRoomType().getName(), allocatedRooms.getRoomNumber());
            }

            if (!res.getAllocatedRooms().isEmpty()) {
                reservationSessionBeanRemote.checkInGuest(res);
            }

            //System.out.println(res.getException().getNumOfTypeTwo());
            if (res.getException() != null) {
                //System.out.println("here");
                RoomAllocationException rae = reservationSessionBeanRemote.retrieveraeByReservationId(resId);

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
            System.out.println("Guest has been successfully checked-in.\n");

        } catch (ReservationNotFoundException ex) {
            System.out.println("Error when checking in:" + ex.getMessage());
        } catch (NoRoomAllocationException ex) {
            System.out.println("Error when checking in:" + ex.getMessage());
        }
    }

//    private void doCheckInGuesta(Guest guest) {
//        Scanner scanner = new Scanner(System.in);
//        try {
//
//            System.out.println("Select which reservation to check in:\n");
//            System.out.printf("%20s%20s%30s\n", "Reservation ID", "Start date", "End date");
//            List<Reservation> reslist = guest.getReservations();
//            for (Reservation res : reslist) {
//                System.out.printf("%20s%20s%30s\n", res.getReservationId(), res.getStartDate(), res.getEndDate());
//            }
//            Long id = scanner.nextLong();
//            Reservation res = reservationSessionBeanRemote.retrieveReservationByReservationId(id);
//            res.setCheckedIn(true);
//            System.out.printf("%20s%20s%30s\n", "Room Id", "Room Type", "Room Number");
//            for (Room allocatedRooms : res.getAllocatedRooms()) {
//                System.out.printf("%20s%30s%30s\n", allocatedRooms.getRoomId(), allocatedRooms.getRoomType().getName(), allocatedRooms.getRoomNumber());
//            }
//            if (res.getException() != null) {
//                RoomAllocationException rae = reservationSessionBeanRemote.retrieveraeByReservationId(id);
//                for (Room allocatedRooms : rae.getTypeOneExceptions()) {
//                    System.out.printf("%20s%20s%30s\n", allocatedRooms.getRoomId(), allocatedRooms.getRoomType(), allocatedRooms.getRoomNumber());
//                }
//                int numTypeTwo = rae.getNumOfTypeTwo();
//                if (numTypeTwo > 0) {
//                    System.out.println(numTypeTwo + " room(s) were unable to be allocated!");
//                }
//            }
//
//        } catch (ReservationNotFoundException ex) {
//            System.out.println("Error when checking in:" + ex.getMessage());
//        } catch (NoRoomAllocationException ex) {
//            System.out.println("Reservation has no allocation exceptions!");
//        }
//    }
    private void showInputDataValidationErrorsForReservation(Set<ConstraintViolation<Reservation>> constraintViolations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
