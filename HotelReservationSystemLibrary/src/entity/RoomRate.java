/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import util.enumeration.RoomRateType;

/**
 *
 * @author PYT
 */
@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    @Column(nullable = false, length = 64)
    private String name;
    @Column(nullable = false)
    private RoomRateType type;
    @Column(nullable = false)
    private Long ratePerNight;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date validityStart;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date validityEnd;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRate[ id=" + roomRateId + " ]";
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
     * @return the type
     */
    public RoomRateType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(RoomRateType type) {
        this.type = type;
    }

    /**
     * @return the ratePerNight
     */
    public Long getRatePerNight() {
        return ratePerNight;
    }

    /**
     * @param ratePerNight the ratePerNight to set
     */
    public void setRatePerNight(Long ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    /**
     * @return the validityStart
     */
    public Date getValidityStart() {
        return validityStart;
    }

    /**
     * @param validityStart the validityStart to set
     */
    public void setValidityStart(Date validityStart) {
        this.validityStart = validityStart;
    }

    /**
     * @return the validityEnd
     */
    public Date getValidityEnd() {
        return validityEnd;
    }

    /**
     * @param validityEnd the validityEnd to set
     */
    public void setValidityEnd(Date validityEnd) {
        this.validityEnd = validityEnd;
    }
    
}
