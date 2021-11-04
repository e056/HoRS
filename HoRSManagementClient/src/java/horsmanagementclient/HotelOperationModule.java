/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Room;
import entity.RoomType;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.AccessRightEnum;
import util.exception.InvalidAccessRightException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author ANGELY NOTE: allocate room to current day reservations not added
 */
public class HotelOperationModule {

    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomAllocationSessionBeanRemote roomAllocationSessionBeanRemote;

    private Employee currEmployee;

    public HotelOperationModule(RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomAllocationSessionBeanRemote roomAllocationSessionBeanRemote, Employee currEmployee) {
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomAllocationSessionBeanRemote = roomAllocationSessionBeanRemote;
        this.currEmployee = currEmployee;
    }

    public void menuHotelOperation() throws InvalidAccessRightException {
        if (currEmployee.getAccessRightEnum() != AccessRightEnum.OPERATION_MANAGER
                && currEmployee.getAccessRightEnum() != AccessRightEnum.SALES_MANAGER) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER or SALES MANAGER rights to access the hotel operation module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Hotel Operation Menu ***\n");
            System.out.println("1: Operation Manager Functions");
            System.out.println("-----------------------");
            System.out.println("2: Sales Manager Functions");
            System.out.println("3: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        menuOperationManager();
                    } catch (InvalidAccessRightException ex) {
                        throw new InvalidAccessRightException("You don't have OPERATION MANAGER or SALES MANAGER rights to access the hotel operation module.");

                    }
                } else if (response == 2) {
                    try {
                        menuSalesManager();
                    } catch (InvalidAccessRightException ex) {
                        throw new InvalidAccessRightException("You don't have OPERATION MANAGER or SALES MANAGER rights to access the hotel operation module.");

                    }
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

    public void menuOperationManager() throws InvalidAccessRightException {
        if (currEmployee.getAccessRightEnum() != AccessRightEnum.OPERATION_MANAGER) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Operation Manager Menu ***\n");
            System.out.println("** Hotel Operation Module **\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details [Update, Delete here]");
            System.out.println("3: View All Room Types");
            System.out.println("-----------------------");
            System.out.println("4: Create New Room");
            System.out.println("5: Update Room");
            System.out.println("6: Delete Room");
            System.out.println("7: View all Rooms");
            System.out.println("8: View Room Allocation Exception Report");
            System.out.println("-----------------------");
            System.out.println("9: Back\n");
            response = 0;

            while (response < 1 || response > 9) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewRoomType();
                } else if (response == 2) {
                    //doViewRoomTypeDetails();
                } else if (response == 3) {
                    //doViewAllRoomTypes();
                } else if (response == 4) {
                    doCreateNewRoom();
                } else if (response == 5) {
                    //doViewRoomDetails();
                } else if (response == 6) {
                    doUpdateRoom();
                } else if (response == 7) {
                    doViewAllRooms();
                } else if (response == 8) {
                    //doViewAllRooms();
                } else if (response == 9) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 9) {
                break;
            }
        }

    }

    public void doCreateNewRoom() {
        Scanner scanner = new Scanner(System.in);
        Room newRoom = new Room();

        System.out.println("*** HoRS System :: Hotel Operation Moduule [Operation Manager] :: Create New Room ***\n");
        System.out.print("Enter Room Number (First two digits floor number, last two digits room sequence) > ");
        String roomNumber = scanner.nextLine().trim();
        newRoom.setRoomNumber(roomNumber);
        newRoom.setIsAvailable(true);
        newRoom.setEnabled(true);

        List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllEnabledRoomTypes();

        System.out.printf("%5s%20s\n", "ID", "Room Type Name");
        for (RoomType rt : roomTypes) {
            System.out.printf("%5s%20s\n", rt.getRoomTypeId(), rt.getName());
        }
        System.out.print("Enter roomTypeId > ");
        Long roomTypeId = scanner.nextLong();

        try {
            Long newRoomId = roomSessionBeanRemote.createNewRoom(newRoom, roomTypeId);
            System.out.println("New room created successfully!: " + newRoomId + "\n");
        } catch (RoomNumberExistException ex) {
            System.out.println("An error has occurred while creating the new Room!: The room number already exist\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while creating the new Room!: No such room type exists\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An unknown error has occurred while creating the new Room!: " + ex.getMessage() + "\n");
        }

    }

    public void doUpdateRoom() {
        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: Update Room ***\n");

        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.print("Enter Room number > ");
        String roomNumber = scanner.nextLine().trim();

        try {
            Room room = roomSessionBeanRemote.retrieveRoomByRoomNumber(roomNumber);
            System.out.print("Enter Availability ('Y' if available, 'N' for unavailable, anything else for no change)> ");
            input = scanner.nextLine().trim();
            if (input == "Y") {
                room.setIsAvailable(Boolean.TRUE);
            } else if (input == "N") {
                room.setIsAvailable(Boolean.FALSE);
            }

            roomSessionBeanRemote.updateRoom(room);
            System.out.println("Staff updated successfully!\n");

        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while updating: The room does not exist.");
        } catch (UpdateRoomException ex) {
            System.out.println("An error has occurred while updating: " + ex.getMessage());
        }

    }

    public void doViewAllRooms() {

        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: View All Rooms ***\n");
        System.out.println("------------------------");

        List<Room> rooms = roomSessionBeanRemote.retrieveAllRooms();
        System.out.printf("%8s%20s%20s%20s\n", "Room ID", "Room Number", "Room Status", "Room Enabled");
        for (Room room : rooms) {
            String roomStatus = (room.getIsAvailable()) ? "Available" : "Not Available";
            String roomEnabled = (room.getEnabled()) ? "Enabled" : "Disabled";
            System.out.printf("%8s%20s%20s%20s\n", room.getRoomId(), room.getRoomNumber(), roomStatus, roomEnabled);
        }

        System.out.println("------------------------");

    }

    public void menuSalesManager() throws InvalidAccessRightException {
        if (currEmployee.getAccessRightEnum() != AccessRightEnum.SALES_MANAGER) {
            throw new InvalidAccessRightException("You don't have SALES MANAGER rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Sales Manager Menu ***\n");
            System.out.println("** Hotel Operation Module **\n");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details [Update, Delete here]");
            System.out.println("3: View All Room Rates");

            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    //doCreateNewRoomRate();

                } else if (response == 2) {
                    //doViewRoomRateDetails();

                } else if (response == 3) {
                    //doViewAllRoomRates();
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

    private void doCreateNewRoomType() {
        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: Create new Room Type ***\n");
        System.out.println("------------------------");
        Scanner scanner = new Scanner(System.in);
        String input;
        Integer count = 0;
        Integer rank = 0;
        RoomType newRoomType = new RoomType();
        //List<String> amenities = new ArrayList();

        System.out.print("Enter Room Type Name > ");
        String name = scanner.nextLine().trim();
        newRoomType.setName(name);

        System.out.print("Enter Room Type Description > ");
        String desc = scanner.nextLine().trim();
        newRoomType.setDescription(desc);

        System.out.print("Enter Room Size > ");
        String size = scanner.nextLine().trim();
        newRoomType.setSize(size);

        System.out.print("Enter Room Bed > ");
        String bed = scanner.nextLine().trim();
        newRoomType.setBed(bed);

        System.out.print("Enter Room Capacity > ");
        Long capacity = scanner.nextLong();
        newRoomType.setCapacity(capacity);

        scanner.nextLine();
        System.out.print("Enter Room Amenities > ");
        String amenities = scanner.nextLine().trim();
        newRoomType.setAmenities(amenities);

//        do{
//            System.out.print("Enter Room Amenities > ");
//            String amenity = scanner.nextLine().trim();
//            amenities.add(amenity);
//            
//            System.out.print("More amenity? (Enter 'N' to stop adding amenities)> ");
//            input = scanner.nextLine().trim();
//        }
//        while(!input.equals("N"));
        //newRoomType.setAmenities(amenities);
        System.out.println("Select room rank. Current room types: ");
        System.out.println("------------------------");
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllEnabledRoomTypes();
        System.out.printf("%8s%20s\n", "Room Name", "Room Rank (1 means most basic)");
        for (RoomType roomType : roomTypes) {
            if (roomType.getRank() > count) {
                count = roomType.getRank();
            }
            System.out.printf("%8s%20s\n", roomType.getName(), roomType.getRank());
        }
        System.out.println("------------------------");
        count++;
        System.out.print("Enter rank (integer from 1 to " + count + ") >");
        rank = scanner.nextInt();
        if (rank < count) {
            roomTypeSessionBeanRemote.rearrangingRank(rank);
        }

        newRoomType.setRank(rank);
        try {
            Long roomTypeId = roomTypeSessionBeanRemote.createNewRoomType(newRoomType);
            System.out.println("New room type created with id = " + roomTypeId);
        } catch (RoomTypeNameExistException ex) {
            System.out.println(ex.getMessage());
        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        }

    }

}
