/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocationException;
import entity.RoomType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.RoomTypeHasNoRoomException;

/**
 *
 * @author ANGELY Retrieve a list of all reservations for check-in on the
 * current date and allocate the required room(s) for the reserved room type. If
 * the required room(s) for the reserved room type is not available, raise an
 * exception in the exception report (see use case 16). This use case should
 * normally be triggered by a server-side timer daily at 2 am. But for
 * development and evaluation purposes, the system should also allow the use
 * case to be triggered arbitrarily.
 */
@Stateless
public class RoomAllocationSessionBean implements RoomAllocationSessionBeanRemote, RoomAllocationSessionBeanLocal {

    @EJB(name = "RoomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

//    @Schedule(dayOfWeek = "*", hour = "2")
//    public void allocateRoomToReservation() throws ReservationNotFoundException
//    {
//        Date dateToday = new Date();
//        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//        System.out.println("********** RoomAllocationSessionBean.allocateRoomToReservation(): Timeout at " + timeStamp);
//        List<Reservation> reservations = reservationSessionBeanLocal.retrieveReservationsByDate(dateToday);
//        for(Reservation reservation : reservations)
//        {
//            List<Room> rooms = roomSessionBeanLocal.retrieveRoomByReservationId(reservation.getReservationId());
//            for(Room room : rooms)
//            {
//                if(room.getEnabled()== false || room.getIsAvailable() == false)
//                {
//                    //check next higher room type. room has to be avail from this duration of reservation
//                    
//                }
//            }
//        }
//        
//    }
//    
    public void allocate(Date date) {

        List<Reservation> reservations = reservationSessionBeanLocal.retrieveReservationsByDate(date);

        for (Reservation reservation : reservations) {
            if (reservation.isAllocated()) {
                continue;
            }
            Date checkInDate = reservation.getStartDate();
            Date checkOutDate = reservation.getEndDate();
            int numOfRooms = reservation.getNumOfRooms();

            RoomType reservationRoomType = reservation.getRoomType();
            List<Room> usableRooms = roomSessionBeanLocal.retrieveAvailableRoomsByRoomType(reservationRoomType.getRoomTypeId());
            List<Room> roomsToRemove = new ArrayList<>();

            for (Room room : usableRooms) {
                List<Reservation> roomReservations = room.getReservations();
                for (Reservation roomReservation : roomReservations) {
                    if ((reservation.getStartDate().compareTo(checkInDate) >= 0 && reservation.getStartDate().compareTo(checkOutDate) <= 0)
                            || (reservation.getEndDate().compareTo(checkInDate) > 0 && reservation.getEndDate().compareTo(checkOutDate) <= 0)) {
                        roomsToRemove.add(room);
                    }
                }
            }
            usableRooms.removeAll(roomsToRemove);

            if (numOfRooms <= usableRooms.size()) { // inventory is enough
                for (int i = 0; i < numOfRooms; i++) {
                    Room roomToAllocate = usableRooms.get(i);

                    roomToAllocate.getReservations().add(reservation);
                    reservation.getAllocatedRooms().add(roomToAllocate);
                    em.persist(roomToAllocate);

                }
                em.persist(reservation);
            } else { // type one or type two exception WILL occur
                RoomAllocationException allocationException = new RoomAllocationException();

                int remaining = numOfRooms - usableRooms.size();
                for (int i = 0; i < usableRooms.size(); i++) { // allocate as much rooms as possible normally
                    Room roomToAllocate = usableRooms.get(i);

                    roomToAllocate.getReservations().add(reservation);
                    reservation.getAllocatedRooms().add(roomToAllocate);

                }

                // get next level
                RoomType nextLevelType = reservationRoomType.getNextHigherRoomType();
                if (nextLevelType == null) { // highest level room, no more higher levels
                    reservation.setAllocated(true);
                    allocationException.setReservation(reservation);
                    allocationException.setNumOfTypeTwo(remaining);
                    em.persist(allocationException);
                    return;
                }

                List<Room> usableRoomsNextLevel = roomSessionBeanLocal.retrieveAvailableRoomsByRoomType(nextLevelType.getRoomTypeId());
                roomsToRemove = new ArrayList<>();
                // extract usable rooms
                for (Room room : usableRoomsNextLevel) {
                    List<Reservation> roomReservations = room.getReservations();
                    for (Reservation roomReservation : roomReservations) {
                        if ((reservation.getStartDate().compareTo(checkInDate) >= 0 && reservation.getStartDate().compareTo(checkOutDate) <= 0)
                                || (reservation.getEndDate().compareTo(checkInDate) > 0 && reservation.getEndDate().compareTo(checkOutDate) <= 0)) {
                            roomsToRemove.add(room);
                        }
                    }
                }
                usableRoomsNextLevel.removeAll(roomsToRemove);

                int nextLevelInventory = usableRoomsNextLevel.size();
                if (remaining <= nextLevelInventory) { // inventory of next level is enough, all remaining rooms are type 1 exceptions
                    for (int i = 0; i < remaining; i++) {
                        Room roomToAllocate = usableRoomsNextLevel.get(i);

                        roomToAllocate.getReservations().add(reservation);
                        reservation.getAllocatedRooms().add(roomToAllocate);
                        allocationException.getTypeOneExceptions().add(roomToAllocate);
                        em.persist(roomToAllocate);

                    }

                } else { // not enough, type 2 WILL occur
                    int numOfTypeTwo = remaining - nextLevelInventory;
                    allocationException.setNumOfTypeTwo(numOfTypeTwo);
                    for (int i = 0; i < nextLevelInventory; i++) { // allocate next level w type 1
                        Room roomToAllocate = usableRoomsNextLevel.get(i);

                        roomToAllocate.getReservations().add(reservation);
                        reservation.getAllocatedRooms().add(roomToAllocate);
                        allocationException.getTypeOneExceptions().add(roomToAllocate);
                        em.persist(roomToAllocate);

                    }

                }
                reservation.setAllocated(true);
                allocationException.setReservation(reservation);
                em.persist(allocationException);

            }

        }
    }

}
