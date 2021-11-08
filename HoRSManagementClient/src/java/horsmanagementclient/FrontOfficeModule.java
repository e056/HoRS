/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomReservationLineEntity;
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
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNotFoundException;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.RoomType;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.RoomTypeNotFoundException;

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

    public FrontOfficeModule(RoomTypeSessionBeanRemote roomeTypeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, Employee currEmployee) {
        this.roomeTypeSessionBeanRemote = roomeTypeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.currEmployee = currEmployee;
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
                    //checkInGuest();
                } else if (response == 3) {
                    //checkOutGuest();
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
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/yy");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate;
            Date endDate;
            int numOfRooms;
            RoomType roomTypeToReserve;
            String comfirmReservation = "";

            System.out.println("*** Hotel Reservation System :: Walk-In Search Room ***\n");
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
        }

    }

//    public void searchRoom() {
//        try {
//            Scanner scanner = new Scanner(System.in);
//            Integer response = 0;
//            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/yy");
//            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            Date startDate;
//            Date endDate;
//            Room room;
//            String roomNumber;
//            String moreItem = "";
//            String comfirmReservation = "";
//
//            System.out.println("*** Hotel Reservation System :: Walk-In Search Room ***\n");
//            System.out.print("Enter Check-In Date (dd/mm/yyyy)> ");
//            startDate = inputDateFormat.parse(scanner.nextLine().trim());
//            System.out.print("Enter Check-Out Date (dd/mm/yyyy)>");
//            endDate = inputDateFormat.parse(scanner.nextLine().trim());
//            System.out.println("------------------------");
//
//            List<Room> rooms = walkInRoomReservationSessionBeanRemote.walkInSearchRoom(startDate, endDate);
//            System.out.printf("%8s%20s%20s\n", "ID", "Room Number", "Room Type");
//            for (Room r : rooms) {
//                System.out.printf("%8s%20s%20s\n", r.getRoomId(), r.getRoomNumber(), r.getRoomType().getName());
//            }
//
//            System.out.println("------------------------");
//            System.out.println("1: Make Reservation");
//            System.out.println("2: Back\n");
//            System.out.print("> ");
//            response = scanner.nextInt();
//            scanner.nextLine();
//
//            if (response == 1) {
//                do {
//                    System.out.print("Enter Room Number> ");
//                    roomNumber = scanner.nextLine().trim();
//
//                    try {
//                        room = roomSessionBeanRemote.retrieveRoomByRoomNumber(roomNumber);
//                        System.out.println("Selecting Room = " + room.getRoomNumber());
//                        BigDecimal subTotal = walkInRoomReservationSessionBeanRemote.walkInAddRoom(room);
//                        System.out.println(room.getRoomNumber() + " added successfully!: "
//                                + "Price of stay for this room @ " + NumberFormat.getCurrencyInstance().format(subTotal) + "\n");
//                    } catch (RoomNotFoundException ex) {
//                        System.out.println("An error has occurred while retrieving room: " + ex.getMessage() + "\n");
//                    } catch (RoomRateNotFoundException ex) {
//                        System.out.println("An error has occurred while retrieving room: The room has no published rate");
//
//                    }
//
//                    System.out.print("More item? (Enter 'N' to complete reservation)> ");
//                    moreItem = scanner.nextLine().trim();
//                } while (!moreItem.equals("N"));
//
//                if (walkInRoomReservationSessionBeanRemote.getNumOfRooms() > 0) {
//                    System.out.println("Reserving the following rooms:\n");
//                    System.out.printf("\n%20s%30s", "Room Number", "Price of room for duration of stay");
//
//                    for (RoomReservationLineEntity roomReservationLineEntity : walkInRoomReservationSessionBeanRemote.getLineEntities()) {
//                        System.out.printf("\n%20s%30s",
//                                roomReservationLineEntity.getRoom().getRoomNumber(),
//                                NumberFormat.getCurrencyInstance().format(roomReservationLineEntity.getPrice()));
//                    }
//                    System.out.printf("\nNumber of Rooms: %d, Total Amount: %s, Check-in: %s, Check-out:%s\n",
//                            walkInRoomReservationSessionBeanRemote.getNumOfRooms(),
//                            NumberFormat.getCurrencyInstance().format(walkInRoomReservationSessionBeanRemote.getTotalAmount()),
//                            outputDateFormat.format(startDate), outputDateFormat.format(endDate));
//
//                    System.out.println("------------------------");
//                    System.out.print("Confirm reservation? (Enter 'Y' to complete reservation)> ");
//                    comfirmReservation = scanner.nextLine().trim();
//                    if (comfirmReservation.equals("Y")) {
//
//                        Reservation reservation = walkInRoomReservationSessionBeanRemote.walkInReserveRoom();
//                        System.out.println("Reservation completed successfully!: " + reservation.getReservationId() + "\n");
//                    } else {
//                        System.out.println("Cancelled.");
//                        walkInRoomReservationSessionBeanRemote.clear();
//                    }
//
//                } else {
//                    System.out.println("Nothing to reserve!");
//                    walkInRoomReservationSessionBeanRemote.clear();
//                }
//
//            }
//        } catch (ParseException ex) {
//            System.out.println("Invalid date input!\n");
//        } catch (CreateNewReservationException ex) {
//            System.out.println("Error when creating new Reservation: " + ex.getMessage());
//        }
//    }
}
