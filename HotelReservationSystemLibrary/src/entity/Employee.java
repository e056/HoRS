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
import util.enumeration.AccessRightEnum;

/**
 *
 * @author PYT
 */
@Entity
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(nullable = false, length = 64)
    private String employeeName;
    @Column(nullable = false, length = 64, unique = true)
    private String employeeUsername;
    @Column(nullable = false, length = 64)
    private String employeePassword;
    @Column(nullable = false)
    private AccessRightEnum accessRightEnum;

    @OneToMany(mappedBy = "employee") // lazy fetching
    private List<Reservation> reservations;

    public Employee() {
        this.reservations = new ArrayList<>();
    }

    public Employee(Long employeeId, String employeeName, String employeeUsername, String employeePassword, AccessRightEnum accessRightEnum) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeUsername = employeeUsername;
        this.employeePassword = employeePassword;
        this.accessRightEnum = accessRightEnum;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.EmployeeEntity[ id=" + employeeId + " ]";
    }

    /**
     * @return the employeeName
     */
    public String getEmployeeName() {
        return employeeName;
    }

    /**
     * @param employeeName the employeeName to set
     */
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    /**
     * @return the employeeUsername
     */
    public String getEmployeeUsername() {
        return employeeUsername;
    }

    /**
     * @param employeeUsername the employeeUsername to set
     */
    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    /**
     * @return the employeePassword
     */
    public String getEmployeePassword() {
        return employeePassword;
    }

    /**
     * @param employeePassword the employeePassword to set
     */
    public void setEmployeePassword(String employeePassword) {
        this.employeePassword = employeePassword;
    }

    /**
     * @return the accessRightEnum
     */
    public AccessRightEnum getAccessRightEnum() {
        return accessRightEnum;
    }

    /**
     * @param accessRightEnum the accessRightEnum to set
     */
    public void setAccessRightEnum(AccessRightEnum accessRightEnum) {
        this.accessRightEnum = accessRightEnum;
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

}
