/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import entity.Reservation;
import entity.Room;
import entity.RoomReservationLineEntity;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CreateNewReservationException;
import util.exception.RoomRateNotFoundException;

/**
 *
 * @author ANGELY
 */
@Remote
public interface RoomReservationSessionBeanRemote {

    public List<Room> walkInSearchRoom(Date checkInDate, Date checkOutDate);

    public Reservation walkInReserveRoom() throws CreateNewReservationException;

    public int getNumOfRooms();

    public void clear();

    public List<RoomReservationLineEntity> getLineEntities();

    public BigDecimal getTotalAmount();

    public Date getCheckInDate();

    public Date getCheckOutDate();

    public BigDecimal walkInAddRoom(Room room) throws RoomRateNotFoundException;

}
