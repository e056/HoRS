/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author PYT
 */
@Entity
public class RoomAllocationException implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomAllocationExceptionId;
    
    @OneToMany
    @JoinColumn(name = "roomAllocationExceptionId")
    private List<Room> typeOneExceptions;
    
    @OneToOne
    private Reservation reservation;
    
    @Column
    private int numOfTypeTwo;

    public RoomAllocationException() {
        this.typeOneExceptions = new ArrayList<>();
    }
    

    public Long getRoomAllocationExceptionId() {
        return roomAllocationExceptionId;
    }

    public void setRoomAllocationExceptionId(Long roomAllocationExceptionId) {
        this.roomAllocationExceptionId = roomAllocationExceptionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomAllocationExceptionId != null ? roomAllocationExceptionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomAllocationExceptionId fields are not set
        if (!(object instanceof RoomAllocationException)) {
            return false;
        }
        RoomAllocationException other = (RoomAllocationException) object;
        if ((this.roomAllocationExceptionId == null && other.roomAllocationExceptionId != null) || (this.roomAllocationExceptionId != null && !this.roomAllocationExceptionId.equals(other.roomAllocationExceptionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomAllocationExceptionEntity[ id=" + roomAllocationExceptionId + " ]";
    }

    /**
     * @return the typeOneExceptions
     */
    public List<Room> getTypeOneExceptions() {
        return typeOneExceptions;
    }

    /**
     * @param typeOneExceptions the typeOneExceptions to set
     */
    public void setTypeOneExceptions(List<Room> typeOneExceptions) {
        this.typeOneExceptions = typeOneExceptions;
    }

    /**
     * @return the reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * @param reservation the reservation to set
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    /**
     * @return the numOfTypeTwo
     */
    public int getNumOfTypeTwo() {
        return numOfTypeTwo;
    }

    /**
     * @param numOfTypeTwo the numOfTypeTwo to set
     */
    public void setNumOfTypeTwo(int numOfTypeTwo) {
        this.numOfTypeTwo = numOfTypeTwo;
    }
    
}
