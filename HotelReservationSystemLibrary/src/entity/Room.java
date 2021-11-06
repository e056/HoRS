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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author PYT
 */
@Entity
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, length = 64, unique = true)
    private String roomNumber;
    // FORMAT: 1234, where 12 refers to the floor number and 34 refers to the sequence number => floor 12 seq 34

    @Column(nullable = false)
    private Boolean isAvailable; // room status

    @Column(nullable = false)
    private Boolean enabled; //whether room is enabled or not. Will be set to false if room is used during deletion.

    @ManyToOne
    private RoomType roomType;
//
//    @ManyToOne
//    private Reservation reservation;

    @OneToMany(mappedBy = "room")
    private List<RoomReservationLineEntity> roomReservationLineEntities;

    public Room() {
        this.roomReservationLineEntities = new ArrayList<>();
    }

    public Room(String roomNumber, boolean isAvailable) {
        this();
        this.roomNumber = roomNumber;
        this.isAvailable = isAvailable;
        this.enabled = Boolean.TRUE;
        this.roomReservationLineEntities = new ArrayList<>();
    }

    public List<RoomReservationLineEntity> getRoomReservationLineEntities() {
        return roomReservationLineEntities;
    }

    public void setRoomReservationLineEntities(List<RoomReservationLineEntity> roomReservationLineEntities) {
        this.roomReservationLineEntities = roomReservationLineEntities;
    }

    public String getFloorNumber() {
        return this.roomNumber.substring(0, 2);
    }

    public String getSequenceNumber() {
        return this.roomNumber.substring(2, 4);
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomId != null ? roomId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomId fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomId == null && other.roomId != null) || (this.roomId != null && !this.roomId.equals(other.roomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Room[ id=" + roomId + " ]";
    }

    /**
     * @return the roomNumber
     */
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * @param roomNumber the roomNumber to set
     */
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    /**
     * @return the isAvailable
     */
    public Boolean getIsAvailable() {
        return isIsAvailable();
    }

    /**
     * @param isAvailable the isAvailable to set
     */
    public void setIsAvailable(Boolean isAvailable) {
        this.setIsAvailable((boolean) isAvailable);
    }

    /**
     * @return the isAvailable
     */
    public boolean isIsAvailable() {
        return isAvailable;
    }

    /**
     * @param isAvailable the isAvailable to set
     */
    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    /**
     * @return the roomType
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * @param roomType the roomType to set
     */
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
