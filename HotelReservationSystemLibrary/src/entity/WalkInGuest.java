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

/**
 *
 * @author PYT
 */
@Entity
public class WalkInGuest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkInGuestId;
    
    @Column(nullable = false, length = 64, unique = true)
    private String passportNumber;
    
    @OneToMany(mappedBy = "walkInGuest")
    private List<Reservation> reservations;
    
    @Column
    private String name;
    
    
    public WalkInGuest() {
        this.reservations = new ArrayList();
    }
    

    public WalkInGuest(String passportNumber, String name) {
        
        this();
        this.passportNumber = passportNumber;
        this.name = name;
        
    }
 

    public Long getWalkInGuestId() {
        return walkInGuestId;
    }

    public void setWalkInGuestId(Long walkInGuestId) {
        this.walkInGuestId = walkInGuestId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (walkInGuestId != null ? walkInGuestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the walkInGuestId fields are not set
        if (!(object instanceof WalkInGuest)) {
            return false;
        }
        WalkInGuest other = (WalkInGuest) object;
        if ((this.walkInGuestId == null && other.walkInGuestId != null) || (this.walkInGuestId != null && !this.walkInGuestId.equals(other.walkInGuestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.WalkInGuest[ id=" + walkInGuestId + " ]";
    }

    /**
     * @return the passportNumber
     */
    public String getPassportNumber() {
        return passportNumber;
    }

    /**
     * @param passportNumber the passportNumber to set
     */
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    /**
     * @return the reservations
     */
    public List<Reservation> getReservations() {
        return reservations;
    }

    /**
     * @param reservations the reservations to set
     */
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
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
    
}
