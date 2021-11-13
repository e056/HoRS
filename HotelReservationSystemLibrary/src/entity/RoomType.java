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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ANGELY
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
      
    @Column(nullable = false, length = 32, unique = true)
    @NotNull
    @Size(min = 1, max = 32)
    private String name;
    
    @Column(length = 128)
    @Size(max = 128)
    private String description;
    
    @Column(length = 32)
    @Size(max = 32)
    private String size;
    
    @Column(length = 32)
    @Size(max = 32)
    private String bed;
    
    @Column(length = 32)
    @Size(max = 32)
    private String capacity;
    
    @Column
    @Size(max = 32)
    private String amenities;
    
    @Column
    private Boolean enabled;
    //@Column(nullable = false, unique = true)
    //private Integer ranking;

    @OneToOne
    @JoinColumn(unique = true)
    //@XmlTransient
    private RoomType nextHigherRoomType;

    @OneToMany(mappedBy = "roomType")
    private List<RoomRate> roomRates;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;

    @OneToMany(mappedBy = "roomType")
    private List<Reservation> reservations;

    public RoomType() {
        this.roomRates = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.enabled = true;
    }

    public RoomType(String name, String description, String size, String bed, String capacity, String amenities, Boolean enabled, RoomType roomType) {
        this();
        this.name = name;
        this.description = description;
        this.size = size;
        this.bed = bed;
        this.capacity = capacity;
        this.amenities = amenities;
        this.enabled = enabled;
        //this.ranking = ranking;
        this.nextHigherRoomType = roomType;
    }

    public RoomType(String name, RoomType roomType) {
        this();
        this.name = name;
        this.enabled = Boolean.TRUE;
        this.nextHigherRoomType = roomType;
        this.roomRates = new ArrayList<>();
        this.rooms = new ArrayList<>();
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
    public String getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(String capacity) {
        this.capacity = capacity;
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
     * @return the ranking
     *
     * public Integer getRanking() { return ranking; }
     */
    /**
     * @param ranking the ranking to set
     *
     * public void setRanking(Integer ranking) { this.ranking = ranking; }
     */
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
     * @return the amenities
     */
    public String getAmenities() {
        return amenities;
    }

    /**
     * @param amenities the amenities to set
     */
    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    /**
     * @return the nextHigherRoomType
     */
    //@XmlTransient
    public RoomType getNextHigherRoomType() {
        return nextHigherRoomType;
    }

    /**
     * @param nextHigherRoomType the nextHigherRoomType to set
     */
    //@XmlTransient
    public void setNextHigherRoomType(RoomType nextHigherRoomType) {
        this.nextHigherRoomType = nextHigherRoomType;
    }

    /**
     * @return the reservations
     */
    //@XmlTransient
    public List<Reservation> getReservations() {
        return reservations;
    }

    /**
     * @param reservations the reservations to set
     */
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

}
