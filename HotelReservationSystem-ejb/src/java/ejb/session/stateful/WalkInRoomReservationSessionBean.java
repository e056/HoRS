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
public class WalkInRoomReservationSessionBean implements RoomReservationSessionBeanRemote {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    private Date checkInDate;
    private Date checkOutDate;
    private long days;
    private int numOfRooms;
    private List<RoomReservationLineEntity> lineEntities;
    private BigDecimal totalAmount;

    public WalkInRoomReservationSessionBean() {
        initialiseState();
    }

    private void initialiseState() {
        this.checkInDate = null;
        this.checkOutDate = null;
        this.days = 0;
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

        this.checkOutDate = this.checkOutDate;

        long duration = checkInDate.getTime() - checkOutDate.getTime();

        this.days = Math.round(TimeUnit.MILLISECONDS.toDays(duration));

        return roomSessionBeanLocal.retrieveRoomsAvailableForReservation(checkInDate, checkOutDate);

    }

    public BigDecimal addRoom(Room room) throws RoomRateNotFoundException {

        BigDecimal roomCost = roomRateSessionBeanLocal.retrievePublishedRoomRateByRoom(room.getRoomId()).getRatePerNight();
        this.lineEntities.add(new RoomReservationLineEntity(room));
        this.numOfRooms++;
        totalAmount = totalAmount.add(roomCost);
        this.numOfRooms++;
        return roomCost;

    }

    public Reservation walkInReserveRoom() throws CreateNewReservationException {
        Reservation reservation = new Reservation();
        reservation.setRoomReservationLineEntities(lineEntities);
        reservation.setStartDate(checkInDate);
        reservation.setEndDate(checkOutDate);
        reservation.setTotalPrice(totalAmount);
        initialiseState();
        return reservationSessionBeanLocal.createNewReservation(reservation);

    }
}
