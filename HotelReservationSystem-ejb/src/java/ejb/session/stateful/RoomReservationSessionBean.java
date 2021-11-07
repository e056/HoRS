/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import entity.Reservation;
import entity.Room;
import entity.RoomReservationLineEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import util.exception.CreateNewReservationException;
import util.exception.RoomRateNotFoundException;

/**
 *
 * @author ANGELY
 */
@Stateful
public class RoomReservationSessionBean implements RoomReservationSessionBeanRemote {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    private Date checkInDate;
    private Date checkOutDate;
    private int days;
    private int numOfRooms;
    private List<RoomReservationLineEntity> lineEntities;
    private BigDecimal totalAmount;

    public RoomReservationSessionBean() {
        initialiseState();
    }

    private void initialiseState() {
        this.checkInDate = null;
        this.checkOutDate = null;
        this.numOfRooms = 0;
        this.lineEntities = new ArrayList<>();
        this.totalAmount = new BigDecimal("0.00");
        this.numOfRooms = 0;

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public List<Room> walkInSearchRoom(Date checkInDate, Date checkOutDate) {
        initialiseState();
        this.checkInDate = checkInDate;

        this.checkOutDate = checkOutDate;


        long duration =checkOutDate.getTime() -checkInDate.getTime();
        System.out.print(duration);

        this.days = (int)Math.round(TimeUnit.MILLISECONDS.toDays(duration));

        return roomSessionBeanLocal.retrieveRoomsAvailableForReservation(checkInDate, checkOutDate);

    }

    @Override
    public BigDecimal walkInAddRoom(Room room) throws RoomRateNotFoundException {
        System.out.println("Add room roomTypeId = " + room.getRoomType().getRoomTypeId());
                
        System.out.print(checkInDate.toString());
        System.out.print(checkOutDate.toString());

        BigDecimal roomCost = roomRateSessionBeanLocal.retrievePublishedRoomRateByRoomType(room.getRoomType().getRoomTypeId()).getRatePerNight().multiply(BigDecimal.valueOf(days));
        System.out.println(days);

        this.lineEntities.add(new RoomReservationLineEntity(room, roomCost));
        this.numOfRooms++;
        totalAmount = totalAmount.add(roomCost);
        return roomCost;

    }

    public Reservation walkInReserveRoom() throws CreateNewReservationException {
        Reservation reservation = new Reservation();
        reservation.setRoomReservationLineEntities(lineEntities);
        for (RoomReservationLineEntity le : lineEntities) {
            le.setReservation(reservation);
        }
        reservation.setStartDate(checkInDate);
        reservation.setEndDate(checkOutDate);
        reservation.setTotalPrice(totalAmount);
        initialiseState();
        return reservationSessionBeanLocal.createNewReservation(reservation);

    }
    
    public int getNumOfRooms() {
        return numOfRooms;
    }
    
    public void clear() {
        initialiseState();
    }
    
    public List<RoomReservationLineEntity> getLineEntities() {
        return this.lineEntities;
    }
    
    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }
    
    public Date getCheckInDate() {
        return this.checkInDate;
    }
    
    public Date getCheckOutDate() {
        return this.checkOutDate;
    }
    
    
    
}
