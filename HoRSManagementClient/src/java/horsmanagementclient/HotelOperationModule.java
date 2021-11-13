/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.RoomAllocationExceptionSessionBeanRemote;
import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.AccessRightEnum;
import util.enumeration.RoomRateType;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomRateException;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomRateException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author ANGELY NOTE: allocate room to current day reservations not added
 */
public class HotelOperationModule {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomAllocationSessionBeanRemote roomAllocationSessionBeanRemote;
    private RoomAllocationExceptionSessionBeanRemote roomAllocationExceptionSessionBeanRemote;

    private Employee currEmployee;

    public HotelOperationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationModule(RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomAllocationSessionBeanRemote roomAllocationSessionBeanRemote, RoomAllocationExceptionSessionBeanRemote roomAllocationExceptionSessionBeanRemote, Employee currEmployee) {
        this();
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomAllocationSessionBeanRemote = roomAllocationSessionBeanRemote;
        this.roomAllocationExceptionSessionBeanRemote = roomAllocationExceptionSessionBeanRemote;
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
            System.out.println("2: Sales Manager Functions");
            System.out.println("3: Allocate Room To Reservation For a Date");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
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
                    allocate();

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

    private void allocate() {
        try {
            Date startDate;
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** HoRS System :: Allocate Room For Reservation (Abitrary date) ***\n");
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/yy");
            System.out.print("Enter Check-In Date (dd/mm/yyyy)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.println(startDate);
            roomAllocationSessionBeanRemote.allocate(startDate);
            System.out.println("Rooms have been allocated. Please view exception report for exceptions.");
        } catch (ParseException ex) {
            System.out.println("Invalid Date Input!");
        }

    }

    public void menuOperationManager() throws InvalidAccessRightException {
        if (currEmployee.getAccessRightEnum() != AccessRightEnum.OPERATION_MANAGER) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Operation Manager Menu ***");
            System.out.println("** Hotel Operation Module **\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details [Update, Delete]");
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
                    doViewRoomTypeDetails();
                } else if (response == 3) {
                    doViewAllRoomTypes();
                } else if (response == 4) {
                    doCreateNewRoom();
                } else if (response == 5) {
                    doUpdateRoom();
                } else if (response == 6) {
                    doDeleteRoom();
                } else if (response == 7) {
                    doViewAllRooms();
                } else if (response == 8) {
                    doViewExceptionReport();
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

    // 1: Create new Room Type
    private void doCreateNewRoomType() {
        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: Create new Room Type ***\n");
        System.out.println("------------------------");
        Scanner scanner = new Scanner(System.in);
        String input;
        String nextHigherRoomTypeName = "";
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
        String capacity = scanner.nextLine().trim();
        newRoomType.setCapacity(capacity);

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
        if (roomTypes.size() != 0) {
            System.out.printf("%8s%30s\n", "Room Name", "Next Higher Room Type");
            for (RoomType roomType : roomTypes) {
                if (roomType.getNextHigherRoomType() == null) {
                    System.out.printf("%8s%30s\n", roomType.getName(), "None");
                } else {
                    System.out.printf("%8s%30s\n", roomType.getName(), roomType.getNextHigherRoomType().getName());
                }

            }
            System.out.println("------------------------");

            System.out.print("Enter Next Higher Room Type (Type 'None' if this will be the highest room type)>");
            nextHigherRoomTypeName = scanner.nextLine().trim();

            newRoomType.setNextHigherRoomType(null);

        } else {
            newRoomType.setNextHigherRoomType(null);
        }
        Set<ConstraintViolation<RoomType>> constraintViolations = validator.validate(newRoomType);

        if (constraintViolations.isEmpty()) {

            try {
                Long roomTypeId = roomTypeSessionBeanRemote.createNewRoomType(newRoomType, nextHigherRoomTypeName);

                System.out.println("New room type created with id = " + roomTypeId + "\n");
            } catch (RoomTypeNameExistException ex) {
                System.out.println(ex.getMessage());
            } catch (RoomTypeNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (UnknownPersistenceException ex) {
                System.out.println(ex.getMessage());
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }

        } else {
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }
    }

    // 2: 
    public void doViewRoomTypeDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: View Room Type ***\n");
        System.out.print("Enter Room Type Name> ");
        String rtName = scanner.nextLine().trim();

        try {
            RoomType rt = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeName(rtName);
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s%20s%20s\n", "Room Type ID", "Room Type", "Description", "Room Size", "Room Bed", "Capacity", "Amenities", "Next Higher room type", "Enabled?");
            String enabled = (rt.getEnabled()) ? "Enabled" : "Disabled";
            String getHigherRoom = "";
            if (rt.getNextHigherRoomType() == null) {
                getHigherRoom = "None";
            } else {
                getHigherRoom = rt.getNextHigherRoomType().getName();
            }
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s%20s%20s\n", rt.getRoomTypeId(),
                    rt.getName(), rt.getDescription(), rt.getSize(), rt.getBed(), rt.getCapacity(), rt.getAmenities(), getHigherRoom, enabled);
            System.out.println("------------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                doUpdateRoomType(rt);

            } else if (response == 2) {
                doDeleteRoomType(rt);
            }
            return;
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving Room Type: " + ex.getMessage() + "\n");
        }

    }

    // 3: View All Room Types
    public void doViewAllRoomTypes() {
        Scanner scanner = new Scanner(System.in);
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllEnabledRoomTypes();
        if (roomTypes.size() != 0) {
            System.out.printf("%8s%30s\n", "Room Name", "Next Higher Room Type");
            for (RoomType roomType : roomTypes) {
                if (roomType.getNextHigherRoomType() == null) {
                    System.out.printf("%8s%30s\n", roomType.getName(), "None");
                } else {
                    System.out.printf("%8s%30s\n", roomType.getName(), roomType.getNextHigherRoomType().getName());
                }

            }
            System.out.println("------------------------");
        }
    }

    // 4: Create new room
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
        System.out.print("Enter Room Type > ");
        String roomType = scanner.nextLine().trim();

        Set<ConstraintViolation<Room>> constraintViolations = validator.validate(newRoom);

        if (constraintViolations.isEmpty()) {
            try {
                Long newRoomId = roomSessionBeanRemote.createNewRoom(newRoom, roomType);
                System.out.println("New room created successfully!: " + newRoomId + "\n");
            } catch (RoomNumberExistException ex) {
                System.out.println("An error has occurred while creating the new Room!: The room number already exist\n");
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("An error has occurred while creating the new Room!: No such room type exists\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new Room!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            showInputDataValidationErrorsForRoom(constraintViolations);
        }
    }

    // 5: Update Room
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
            if (input.equals("Y")) {
                room.setIsAvailable(Boolean.TRUE);
            } else if (input.equals("N")) {
                room.setIsAvailable(Boolean.FALSE);
            }

            Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);

            if (constraintViolations.isEmpty()) {

                roomSessionBeanRemote.updateRoom(room);
                System.out.println("Room updated successfully!\n");
            } else {
                showInputDataValidationErrorsForRoom(constraintViolations);
            }

        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while updating: The room does not exist.");
        } catch (UpdateRoomException ex) {
            System.out.println("An error has occurred while updating: " + ex.getMessage());
        } catch (InputDataValidationException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // 6: Delete Room
    public void doDeleteRoom() {
        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: Delete Room ***\n");
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.print("Enter Room number > ");
        String roomNumber = scanner.nextLine().trim();

        try {
            Room room = roomSessionBeanRemote.retrieveRoomByRoomNumber(roomNumber);
            System.out.print("Confirm deletion? ('Y' to proceed)> ");
            input = scanner.nextLine().trim();
            if (input.equals("Y")) {
                roomSessionBeanRemote.deleteRoom(room.getRoomId());
                System.out.println("Room has been deleted!");
            } else {
                System.out.println("Room deletion cancelled...");
            }

        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while deleting: The room does not exist.");
        } catch (DeleteRoomException ex) {
            // disables the room
            System.out.println(ex.getMessage());
        }

    }

    // 7: View All Rooms
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

    // 8 : View Exception Report
    public void doViewExceptionReport() {

        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: View Exception Report ***\n");
        System.out.println("=========================Start Of Report============================");

        List<RoomAllocationException> exceptions = roomAllocationExceptionSessionBeanRemote.retrieveAllRoomAllocationExceptions();

        for (RoomAllocationException e : exceptions) {
            Reservation reservation = e.getReservation();

            System.out.println("Reservation id = " + reservation.getReservationId() + ", Exception id = " + e.getRoomAllocationExceptionId());
            System.out.println("Number of Type One Exceptions = " + e.getTypeOneExceptions().size());
            System.out.println("Number of Type Two Exceptions = " + e.getNumOfTypeTwo());

            System.out.printf("%20s%20s%20s%30s\n", "Exception Type", "Original Room Type", "Upgraded Type", "Upgraded Room Number");
            for (Room room : e.getTypeOneExceptions()) {
                System.out.printf("%20s%20s%20s%30s\n", "Type One", reservation.getRoomType().getName(),
                        room.getRoomType().getName(), room.getRoomNumber());
            }

            for (int i = 0; i < e.getNumOfTypeTwo(); i++) {
                System.out.printf("%20s%20s%20s%30s\n", "Type Two", reservation.getRoomType().getName(), "NA", "NA");
            }
            System.out.println("------------------------------------------");

        }
        System.out.println("=========================End Of Report============================");

    }

    /**
     * =======================================================================
     * Start of Sales Manager Functions
     * =======================================================================
     */
    public void menuSalesManager() throws InvalidAccessRightException {
        if (currEmployee.getAccessRightEnum() != AccessRightEnum.SALES_MANAGER) {
            throw new InvalidAccessRightException("You don't have SALES MANAGER rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: Sales Manager Menu ***");
            System.out.println("** Hotel Operation Module **\n");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details [Update, Delete]");
            System.out.println("3: View All Room Rates");

            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewRoomRate();

                } else if (response == 2) {
                    doViewRoomRateDetails();

                } else if (response == 3) {
                    doViewAllRoomRates();
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

    // 1: Create new room rate
    public void doCreateNewRoomRate() {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
        try {
            System.out.println("*** HoRS System :: Hotel Operation Module [Sales Manager] :: Create New Room Rate ***\n");
            RoomRate roomRate = new RoomRate();
            System.out.print("Enter Room Rate Name> ");
            roomRate.setName(scanner.nextLine().trim());

            System.out.println("Select Room Rate Type> ");
            Integer type;
            while (true) {
                System.out.print("(1: Published 2: Normal 3: Peak 4: Promotion)> ");
                type = scanner.nextInt();

                if (type >= 1 && type <= 4) {
                    roomRate.setType(RoomRateType.values()[type - 1]);
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            System.out.print("Set Rate per Night> ");
            roomRate.setRatePerNight(scanner.nextBigDecimal());

            scanner.nextLine();

            if (type == 3 || type == 4) {
                Date start;
                Date end;
                System.out.print("Enter Validity Start Date (dd/mm/yyyy)> ");
                start = inputDateFormat.parse(scanner.nextLine().trim());
                System.out.print("Enter Validity End Date (dd/mm/yyyy)> ");
                end = inputDateFormat.parse(scanner.nextLine().trim());

                roomRate.setValidityStart(start);
                roomRate.setValidityEnd(end);
            }

            System.out.println("Select the Room Type> ");
            List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllEnabledRoomTypes();

            System.out.printf("%5s%20s\n", "ID", "Room Type Name");
            for (RoomType rt : roomTypes) {
                System.out.printf("%5s%20s\n", rt.getRoomTypeId(), rt.getName());
            }
            System.out.print("Enter roomTypeId > ");
            Long roomTypeId = scanner.nextLong();
            Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRate);
        
        if(constraintViolations.isEmpty())
        {

            Long roomRateId = roomRateSessionBeanRemote.createNewRoomRate(roomRate, roomTypeId);
            System.out.println("New room rate created! id: " + roomRateId);
        }else
        {
            showInputDataValidationErrorsForRoomRate(constraintViolations);
        }

        } catch (ParseException ex) {
            System.out.println("Invalid date format!");

        } catch (RoomRateNameExistException ex) {
            System.out.println("Room Rate Name already exist.");

        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        } catch (RoomTypeNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (InputDataValidationException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // 2: View room rate details
    public void doViewRoomRateDetails() {
        SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        System.out.println("*** POS System :: System Administration :: View Room Rate Details ***\n");
        System.out.print("Enter Room Rate Name> ");
        String name = scanner.nextLine().trim();

        try {
            RoomRate rr = roomRateSessionBeanRemote.retrieveRoomRateByRoomRateName(name);
            System.out.printf("%8s%40s%20s%20s%20s%40s%40s%20s\n", "Room Rate ID", "Name", "Room Rate Type", "Room Type", "Rate Per Night", "Validity Start", "Validity End", "Enabled?");
            String enabled = (rr.getEnabled()) ? "Enabled" : "Disabled";
            String start = (rr.getValidityStart() == null) ? "-" : df.format(rr.getValidityStart());
            String end = (rr.getValidityEnd() == null) ? "-" : df.format(rr.getValidityEnd());
            System.out.printf("%8s%40s%20s%20s%20s%40s%40s%20s\n", rr.getRoomRateId(), rr.getName(),
                    rr.getType().toString(), rr.getRoomType().getName(),
                    NumberFormat.getCurrencyInstance().format(rr.getRatePerNight()),
                    start, end,
                    enabled);
            System.out.println("------------------------");
            System.out.println("1: Update Room Rate");
            System.out.println("2: Delete Room Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                doUpdateRoomRate(rr);

            } else if (response == 2) {
                doDeleteRoomRate(rr);
            }
            return;
        } catch (RoomRateNotFoundException ex) {
            System.out.println("An error has occurred while retrieving Room Rate: " + ex.getMessage() + "\n");
        }
    }

    // can rate type be changed?
    public void doUpdateRoomRate(RoomRate rr) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            Scanner scanner = new Scanner(System.in);
            String input;
            Integer integerInput;
            BigDecimal bigDecimalInput;

            System.out.println("*** HoRs System :: Hotel Operation Module :: View Room Rate Details :: Update Room Rate ***\n");

            System.out.print("Set Rate per Night (blank if no change> ");

            bigDecimalInput = scanner.nextBigDecimal();
            scanner.nextLine();
            if (bigDecimalInput.compareTo(BigDecimal.ZERO) > 0) {
                rr.setRatePerNight(bigDecimalInput);
            }

            if (!(rr.getType().equals(RoomRateType.NORMAL) || rr.getType().equals(RoomRateType.PUBLISHED))) {

                Date date;
                System.out.print("Enter Validity Start Date (dd/mm/yyyy) (blank if no change)> ");
                input = scanner.nextLine().trim();
                if (input.length() > 0) {

                    date = inputDateFormat.parse(input);
                    rr.setValidityStart(date);

                }
                System.out.print("Enter Validity End Date (dd/mm/yyyy) (blank if no change)> ");
                input = scanner.nextLine().trim();
                if (input.length() > 0) {

                    date = inputDateFormat.parse(input);
                    rr.setValidityEnd(date);

                }

            }
             Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(rr);
        
        if(constraintViolations.isEmpty())
        {
            roomRateSessionBeanRemote.updateRoomRate(rr);
            System.out.println("Successfully updated!");
            }
        else
        {
            showInputDataValidationErrorsForRoomRate(constraintViolations);
        }

        } catch (ParseException ex) {
            System.out.println("Invalid date format!");

        } catch (RoomRateNotFoundException ex) {
            System.out.println("An error has occurred while updating: " + ex.getMessage());

        } catch (UpdateRoomRateException ex) {
            System.out.println("An error has occurred while updating: " + ex.getMessage());
        } catch (InputDataValidationException ex) {
            System.out.println(ex.getMessage() + "\n");
        }
    }

    public void doDeleteRoomRate(RoomRate rr) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** HoRs System :: Hotel Operation Module :: View Room Rate Details :: Delete Room Rate ***\n");
        System.out.printf("Confirm Delete Room Rate %s (Enter 'Y' to Delete)> ", rr.getName());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                roomRateSessionBeanRemote.deleteRoomRate(rr.getRoomRateId());
                System.out.println("Room Rate deleted successfully!\n");
            } catch (RoomRateNotFoundException | DeleteRoomRateException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    // 3: View all room rates
    public void doViewAllRoomRates() {

        SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
        System.out.println("*** HoRS System :: Hotel Operation Module [Sales Manager] :: View All Room Rates ***\n");
        System.out.println("------------------------");

        List<RoomRate> rrs = roomRateSessionBeanRemote.retrieveAllRoomRates();
        System.out.printf("%8s%20s%20s%20s%30s%20s%20s%20s\n", "Room Rate ID", "Room Rate Type", "Room Type", "Rate Per Night", "Name", "Validity Start", "Validity End", "Enabled?");
        for (RoomRate rr : rrs) {
            String enabled = (rr.getEnabled()) ? "Enabled" : "Disabled";
            String start = (rr.getValidityStart() == null) ? "-" : df.format(rr.getValidityStart());
            String end = (rr.getValidityEnd() == null) ? "-" : df.format(rr.getValidityEnd());

            System.out.printf("%8s%20s%20s%20s%30s%20s%20s%20s\n", rr.getRoomRateId(),
                    rr.getType().toString(), rr.getRoomType().getName(),
                    NumberFormat.getCurrencyInstance().format(rr.getRatePerNight()),
                    rr.getName(),
                    start, end,
                    enabled);

        }

        System.out.println("------------------------");

    }

    //business assumption: room ranking cannot be changed
    private void doUpdateRoomType(RoomType rt) {
        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: Update Room Type***\n");

        Scanner scanner = new Scanner(System.in);
        String desc;
        String name;
        String size;
        String bed;
        String capacity;
        String amen;

        try {
            RoomType roomTypeToUpdate = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeName(rt.getName());

            System.out.println("Leave blank for no change. ");
            System.out.print("Enter Room Type Name> ");
            name = scanner.nextLine().trim();
            if (name.length() > 0) {
                roomTypeToUpdate.setName(name);

            }

            System.out.print("Enter Description> ");
            desc = scanner.nextLine().trim();
            roomTypeToUpdate.setDescription(desc);
            System.out.print("Enter Size> ");
            size = scanner.nextLine().trim();
            roomTypeToUpdate.setSize(size);
            System.out.print("Enter Bed> ");
            bed = scanner.nextLine().trim();
            roomTypeToUpdate.setBed(bed);
            System.out.print("Enter Capacity> ");
            capacity = scanner.nextLine().trim();
            roomTypeToUpdate.setCapacity(capacity);
            System.out.print("Enter Amen> ");
            amen = scanner.nextLine().trim();
            roomTypeToUpdate.setAmenities(amen);
            
            
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomTypeToUpdate);
        
        if(constraintViolations.isEmpty())
        {

            roomTypeSessionBeanRemote.updateRoomType(roomTypeToUpdate);
            System.out.println("Room Type updated successfully!\n");
        }else
        {
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }

        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while updating: The room type does not exist.");
        } catch (UpdateRoomTypeException ex) {
            System.out.println("An error has occurred while updating: " + ex.getMessage());
        } catch (InputDataValidationException ex) {
            System.out.println(ex.getMessage() + "\n");
        } catch (RoomTypeNameExistException ex) {
         System.out.println("An error has occurred while updating: " + ex.getMessage());
        }
    }

    private void doDeleteRoomType(RoomType rt) {
        System.out.println("*** HoRS System :: Hotel Operation Module [Operation Manager] :: Delete Room Type***\n");
        try {
            roomTypeSessionBeanRemote.deleteRoomType(rt);
            System.out.println("Room Type " + rt.getRoomTypeId() + " deleted successfully!\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while deleting: " + ex.getMessage());
        } catch (DeleteRoomTypeException ex) {
            System.out.println("An error has occurred while deleting: " + ex.getMessage());
        }

    }

    private void showInputDataValidationErrorsForRoomType(Set<ConstraintViolation<RoomType>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    private void showInputDataValidationErrorsForRoom(Set<ConstraintViolation<Room>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    private void showInputDataValidationErrorsForRoomRate(Set<ConstraintViolation<RoomRate>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

}
