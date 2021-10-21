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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author PYT
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    
    @Column(nullable = false, length = 64, unique = true)
    private String roomTypeName;
    @Column(nullable = false, length = 64)
    private String roomTypeDescription;
    @Column(nullable = false, length = 64)
    private String roomTypeSize;
    @Column(nullable = false, length = 64)
    private String roomTypeBed;
    @Column(nullable = false)
    private Long roomTypeCapacity;
    @Column(nullable = false, length = 64)
    private String roomTypeAmmenities;
    @Column(nullable = false)
    private Boolean isDisabled;
    
    @OneToMany(mappedBy = "roomType")
    private List<RoomRate> roomRates;
    
    @OneToOne(mappedBy = "roomType")
    private Room room;

    public RoomType() {
        roomRates = new ArrayList<>();
    }

    public RoomType(Long roomTypeId, String roomTypeName, String roomTypeDescription, String roomTypeSize, String roomTypeBed, Long roomTypeCapacity, String roomTypeAmmenities, Boolean isDisabled, List<RoomRate> roomRates) {
        this();
        
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.roomTypeDescription = roomTypeDescription;
        this.roomTypeSize = roomTypeSize;
        this.roomTypeBed = roomTypeBed;
        this.roomTypeCapacity = roomTypeCapacity;
        this.roomTypeAmmenities = roomTypeAmmenities;
        this.isDisabled = isDisabled;
        this.roomRates = roomRates;
    }
    

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
    }

    /**
     * @return the roomTypeName
     */
    public String getRoomTypeName() {
        return roomTypeName;
    }

    /**
     * @param roomTypeName the roomTypeName to set
     */
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    /**
     * @return the roomTypeDescription
     */
    public String getRoomTypeDescription() {
        return roomTypeDescription;
    }

    /**
     * @param roomTypeDescription the roomTypeDescription to set
     */
    public void setRoomTypeDescription(String roomTypeDescription) {
        this.roomTypeDescription = roomTypeDescription;
    }

    /**
     * @return the roomTypeSize
     */
    public String getRoomTypeSize() {
        return roomTypeSize;
    }

    /**
     * @param roomTypeSize the roomTypeSize to set
     */
    public void setRoomTypeSize(String roomTypeSize) {
        this.roomTypeSize = roomTypeSize;
    }

    /**
     * @return the roomTypeBed
     */
    public String getRoomTypeBed() {
        return roomTypeBed;
    }

    /**
     * @param roomTypeBed the roomTypeBed to set
     */
    public void setRoomTypeBed(String roomTypeBed) {
        this.roomTypeBed = roomTypeBed;
    }

    /**
     * @return the roomTypeCapacity
     */
    public Long getRoomTypeCapacity() {
        return roomTypeCapacity;
    }

    /**
     * @param roomTypeCapacity the roomTypeCapacity to set
     */
    public void setRoomTypeCapacity(Long roomTypeCapacity) {
        this.roomTypeCapacity = roomTypeCapacity;
    }

    /**
     * @return the roomTypeAmmenities
     */
    public String getRoomTypeAmmenities() {
        return roomTypeAmmenities;
    }

    /**
     * @param roomTypeAmmenities the roomTypeAmmenities to set
     */
    public void setRoomTypeAmmenities(String roomTypeAmmenities) {
        this.roomTypeAmmenities = roomTypeAmmenities;
    }

    /**
     * @return the isDisabled
     */
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    /**
     * @param isDisabled the isDisabled to set
     */
    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * @return the roomRates
     */
    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    /**
     * @param roomRates the roomRates to set
     */
    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    /**
     * @return the room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(Room room) {
        this.room = room;
    }
    
}
