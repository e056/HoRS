/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author PYT
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date endDate;
    
    @Column(nullable = false)
    private int numOfRooms;
    
    @Column(nullable = false)
    private boolean checkedIn;
    
    @Column
    private BigDecimal totalPrice;
    
    @ManyToOne
    //@JoinColumn(nullable = false)
    private Guest guest;
    
    @ManyToOne
    //@JoinColumn(nullable = false)
    private Partner partner;
    
    @ManyToMany
    private List<Room> allocatedRooms;
    
    @ManyToOne
    private RoomType roomType;
    
    @OneToOne(mappedBy = "reservation")
    private RoomAllocationException exception;
    
    private boolean allocated;

    public Reservation() {
        this.allocatedRooms = new ArrayList<>();
        this.allocated = false;
        this.checkedIn = false;
    }

    public Reservation(Date startDate, Date endDate, int numOfRooms, BigDecimal totalPrice, RoomType roomType) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.numOfRooms = numOfRooms;
        this.totalPrice = totalPrice;
        this.roomType = roomType;
    }


    
    

    
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    
    public Long getReservationId() {
        return reservationId;
    }

//    public List<Room> getRooms() {
//        return rooms;
//    }
//
//    public void setRooms(List<Room> rooms) {
//        this.rooms = rooms;
//    }

//    public Employee getEmployee() {
//        return employee;
//    }
//
//    public void setEmployee(Employee employee) {
//        this.employee = employee;
//    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the allocated
     */
    public boolean isAllocated() {
        return allocated;
    }

    /**
     * @param allocated the allocated to set
     */
    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
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
     * @return the numOfRooms
     */
    public int getNumOfRooms() {
        return numOfRooms;
    }

    /**
     * @param numOfRooms the numOfRooms to set
     */
    public void setNumOfRooms(int numOfRooms) {
        this.numOfRooms = numOfRooms;
    }

    /**
     * @return the allocatedRooms
     */
    public List<Room> getAllocatedRooms() {
        return allocatedRooms;
    }

    /**
     * @param allocatedRooms the allocatedRooms to set
     */
    public void setAllocatedRooms(List<Room> allocatedRooms) {
        this.allocatedRooms = allocatedRooms;
    }

    /**
     * @return the exception
     */
    public RoomAllocationException getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(RoomAllocationException exception) {
        this.exception = exception;
    }
    
}
