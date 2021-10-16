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
    private String roomRateName;
    @Column(nullable = false)
    private RoomRateType roomRateType;
    @Column(nullable = false)
    private Long roomRatePerNight;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date validityStart;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date validityEnd;

    @ManyToOne
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
     * @return the roomRateName
     */
    public String getRoomRateName() {
        return roomRateName;
    }

    /**
     * @param roomRateName the roomRateName to set
     */
    public void setRoomRateName(String roomRateName) {
        this.roomRateName = roomRateName;
    }

    /**
     * @return the roomRateType
     */
    public RoomRateType getRoomRateType() {
        return roomRateType;
    }

    /**
     * @param roomRateType the roomRateType to set
     */
    public void setRoomRateType(RoomRateType roomRateType) {
        this.roomRateType = roomRateType;
    }

    /**
     * @return the roomRatePerNight
     */
    public Long getRoomRatePerNight() {
        return roomRatePerNight;
    }

    /**
     * @param roomRatePerNight the roomRatePerNight to set
     */
    public void setRoomRatePerNight(Long roomRatePerNight) {
        this.roomRatePerNight = roomRatePerNight;
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
