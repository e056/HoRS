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
    private String name;
    @Column(nullable = false, length = 64)
    private String description;
    @Column(nullable = false, length = 64)
    private String size;
    @Column(nullable = false, length = 64)
    private String bed;
    @Column(nullable = false)
    private Long capacity;
    @Column(nullable = false, length = 64)
    private List<String> amenities;
    @Column(nullable = false)
    private Boolean enabled;
    @Column(nullable = false, unique = true)
    private Integer rank;
    
    @OneToMany(mappedBy = "roomType")
    private List<RoomRate> roomRates;
    
    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;

    public RoomType() {
        roomRates = new ArrayList<>();
        amenities = new ArrayList<>();
        this.enabled = Boolean.TRUE;
    }

    public RoomType(String roomTypeName, String roomTypeDescription, String roomTypeSize, String roomTypeBed, Long roomTypeCapacity, List<String> roomTypeAmmenities, Boolean isDisabled, List<RoomRate> roomRates) {
        this();
        
        
        this.name = roomTypeName;
        this.description = roomTypeDescription;
        this.size = roomTypeSize;
        this.bed = roomTypeBed;
        this.capacity = roomTypeCapacity;
        this.amenities = roomTypeAmmenities;
        this.enabled = isDisabled;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the bed
     */
    public String getBed() {
        return bed;
    }

    /**
     * @param bed the bed to set
     */
    public void setBed(String bed) {
        this.bed = bed;
    }

    /**
     * @return the capacity
     */
    public Long getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the amenities
     */
    public List<String> getAmenities() {
        return amenities;
    }

    /**
     * @param amenities the amenities to set
     */
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
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
     * @return the rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * @return the rank
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * @param rank the rank to set
     */
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    
}
