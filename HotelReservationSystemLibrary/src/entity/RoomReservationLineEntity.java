/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author PYT
 */
@Entity
public class RoomReservationLineEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomReservationLineId;
    
    @Column
    private BigDecimal price;
    
    @ManyToOne
    private Room room;
    
    @ManyToOne
    private Reservation reservation;

    public RoomReservationLineEntity() {
    }

    
    
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Long getRoomReservationLineId() {
        return roomReservationLineId;
    }

    public void setRoomReservationLineId(Long roomReservationLineId) {
        this.roomReservationLineId = roomReservationLineId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomReservationLineId != null ? roomReservationLineId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomReservationLineId fields are not set
        if (!(object instanceof RoomReservationLineEntity)) {
            return false;
        }
        RoomReservationLineEntity other = (RoomReservationLineEntity) object;
        if ((this.roomReservationLineId == null && other.roomReservationLineId != null) || (this.roomReservationLineId != null && !this.roomReservationLineId.equals(other.roomReservationLineId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomReservationLineEntity[ id=" + roomReservationLineId + " ]";
    }
    
}
