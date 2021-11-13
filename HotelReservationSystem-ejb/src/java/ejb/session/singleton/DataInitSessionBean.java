/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.AccessRightEnum;
import util.enumeration.RoomRateType;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.RoomNumberExistException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
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
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

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

    private void doLoadData(){
        try {
            //employee 
            Employee employee1 = new Employee("sysad", "one", "sysadmin", "password", AccessRightEnum.SYSTEM_ADMIN);
            Employee employee2 = new Employee("opman", "two", "opmanager", "password", AccessRightEnum.OPERATION_MANAGER);
            Employee employee3 = new Employee("saleman", "three", "salesmanager", "password", AccessRightEnum.SALES_MANAGER);
            Employee employee4 = new Employee("guestrln", "four", "guestrelo", "password", AccessRightEnum.GUEST_RELATION);

            employeeSessionBeanLocal.createNewEmployee(employee1);
            employeeSessionBeanLocal.createNewEmployee(employee2);
            employeeSessionBeanLocal.createNewEmployee(employee3);
            employeeSessionBeanLocal.createNewEmployee(employee4);
            //room type
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
            
            //room
            Boolean isAvailable = Boolean.TRUE;
            Room dr1 = new Room("0101", isAvailable);
            Room dr2 = new Room("0201", isAvailable);
            Room dr3 = new Room("0301", isAvailable);
            Room dr4 = new Room("0401", isAvailable);
            Room dr5 = new Room("0501", isAvailable); 
            
            Room pr1 = new Room("0102", isAvailable);
            Room pr2 = new Room("0202", isAvailable);
            Room pr3 = new Room("0302", isAvailable);
            Room pr4 = new Room("0402", isAvailable);
            Room pr5 = new Room("0502", isAvailable);
            
            Room fr1 = new Room("0103", isAvailable);
            Room fr2 = new Room("0203", isAvailable);
            Room fr3 = new Room("0303", isAvailable);
            Room fr4 = new Room("0403", isAvailable);
            Room fr5 = new Room("0503", isAvailable); 
            
            Room js1 = new Room("0104", isAvailable);
            Room js2 = new Room("0204", isAvailable);
            Room js3 = new Room("0304", isAvailable);
            Room js4 = new Room("0404", isAvailable);
            Room js5 = new Room("0504", isAvailable);
            
            Room gs1 = new Room("0105", isAvailable);
            Room gs2 = new Room("0205", isAvailable);
            Room gs3 = new Room("0305", isAvailable);
            Room gs4 = new Room("0405", isAvailable);
            Room gs5 = new Room("0505", isAvailable);
            
            String dr = "Deluxe Room";
            String pr = "Premier Room";
            String fr = "Family Room";
            String js = "Junior Suite";
            String gs = "Grand Suite";
            
            roomSessionBeanLocal.createNewRoom(dr1, dr);
            roomSessionBeanLocal.createNewRoom(dr2, dr);
            roomSessionBeanLocal.createNewRoom(dr3, dr);
            roomSessionBeanLocal.createNewRoom(dr4, dr);
            roomSessionBeanLocal.createNewRoom(dr5, dr);
            
            roomSessionBeanLocal.createNewRoom(pr1, pr);
            roomSessionBeanLocal.createNewRoom(pr2, pr);
            roomSessionBeanLocal.createNewRoom(pr3, pr);
            roomSessionBeanLocal.createNewRoom(pr4, pr);
            roomSessionBeanLocal.createNewRoom(pr5, pr);
           
            roomSessionBeanLocal.createNewRoom(fr1, fr);
            roomSessionBeanLocal.createNewRoom(fr2, fr);
            roomSessionBeanLocal.createNewRoom(fr3, fr);
            roomSessionBeanLocal.createNewRoom(fr4, fr);
            roomSessionBeanLocal.createNewRoom(fr5, fr);
            
            
            roomSessionBeanLocal.createNewRoom(js1, js);
            roomSessionBeanLocal.createNewRoom(js2, js);
            roomSessionBeanLocal.createNewRoom(js3, js);
            roomSessionBeanLocal.createNewRoom(js4, js);
            roomSessionBeanLocal.createNewRoom(js5, js);
            
            roomSessionBeanLocal.createNewRoom(gs1, gs);
            roomSessionBeanLocal.createNewRoom(gs2, gs);
            roomSessionBeanLocal.createNewRoom(gs3, gs);
            roomSessionBeanLocal.createNewRoom(gs4, gs);
            roomSessionBeanLocal.createNewRoom(gs5, gs);
            
            //room rate
            RoomRateType pub = RoomRateType.PUBLISHED;
            RoomRateType nor = RoomRateType.NORMAL;
            RoomRateType peak = RoomRateType.PEAK;
            RoomRateType pro = RoomRateType.PROMOTION;
            
            RoomRate drp = new RoomRate("Deluxe Room Published", deluxe, pub, BigDecimal.valueOf(100));
            RoomRate drn = new RoomRate("Deluxe Room Normal", deluxe, nor, BigDecimal.valueOf(50));
            
            RoomRate prp = new RoomRate("Premier Room Published", premier, pub, BigDecimal.valueOf(200));
            RoomRate prn = new RoomRate("Premier Room Normal", premier, nor, BigDecimal.valueOf(100));
            
            RoomRate frp = new RoomRate("Family Room Published", family, pub, BigDecimal.valueOf(300));
            RoomRate frn = new RoomRate("Family Room Normal", family, nor, BigDecimal.valueOf(150));
            
            RoomRate jsp = new RoomRate("Junior Suite Published", junior, pub, BigDecimal.valueOf(400));
            RoomRate jsn = new RoomRate("Junior Suite Normal", junior, nor, BigDecimal.valueOf(200));
            
            RoomRate gsp = new RoomRate("Grand Suite Published", grand, pub, BigDecimal.valueOf(500));
            RoomRate gsn = new RoomRate("Grand Suite Normal", grand, nor, BigDecimal.valueOf(250));
            
            roomRateSessionBeanLocal.createNewRoomRate(drp, deluxe.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(drn, deluxe.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(prp, premier.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(prn, premier.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(frp, family.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(frn, family.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(jsp, junior.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(jsn, junior.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(gsp, grand.getRoomTypeId());
            roomRateSessionBeanLocal.createNewRoomRate(gsn, grand.getRoomTypeId());
            
            
        } catch (EmployeeUsernameExistException ex) {
            ex.printStackTrace();
        } catch (RoomTypeNameExistException ex) {
            ex.printStackTrace();
        } catch (UnknownPersistenceException ex) {
            ex.printStackTrace();
        } catch (RoomNumberExistException ex) {
            ex.printStackTrace();
        } catch (RoomTypeNotFoundException ex) {
            ex.printStackTrace();
        } catch (RoomRateNameExistException ex) {
            ex.printStackTrace();
        } catch (InputDataValidationException ex) {
            ex.printStackTrace();
        }
    }
}


