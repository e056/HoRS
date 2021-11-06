/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
import entity.RoomType;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.AccessRightEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.RoomTypeNameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PYT
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
     public DataInitSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        try {
            employeeSessionBeanLocal.retrieveEmployeeByEmployeeId(1l);
        } catch (EmployeeNotFoundException ex) {
            doLoadData();
        }

    }

    private void doLoadData() {
        try {
            Employee employee1 = new Employee("sysad", "one", "sa", "password", AccessRightEnum.SYSTEM_ADMIN);
            Employee employee2 = new Employee("opman", "two", "om", "password", AccessRightEnum.OPERATION_MANAGER);
            Employee employee3 = new Employee("saleman", "three", "sm", "password", AccessRightEnum.SALES_MANAGER);
            Employee employee4 = new Employee("guestrln", "four", "gro", "password", AccessRightEnum.GUEST_RELATION);
            
            employeeSessionBeanLocal.createNewEmployee(employee1);
            employeeSessionBeanLocal.createNewEmployee(employee2);
            employeeSessionBeanLocal.createNewEmployee(employee3);
            employeeSessionBeanLocal.createNewEmployee(employee4);
            
            RoomType grand = new RoomType("Grand Suite",null);
            RoomType junior = new RoomType("Junior Suite", grand);
            RoomType family = new RoomType("Family Room", junior);
            RoomType premier = new RoomType("Premier Room", family);
            RoomType deluxe = new RoomType("Deluxe Room", premier);
            
            roomTypeSessionBeanLocal.createNewRoomType(grand);
            roomTypeSessionBeanLocal.createNewRoomType(junior);
            roomTypeSessionBeanLocal.createNewRoomType(family);
            roomTypeSessionBeanLocal.createNewRoomType(premier);
            roomTypeSessionBeanLocal.createNewRoomType(deluxe);
            
        } catch (EmployeeUsernameExistException ex) {
            ex.printStackTrace();
        } catch (RoomTypeNameExistException ex) {
            ex.printStackTrace();
        } catch (UnknownPersistenceException ex) {
            ex.printStackTrace();
        }
    }
}


