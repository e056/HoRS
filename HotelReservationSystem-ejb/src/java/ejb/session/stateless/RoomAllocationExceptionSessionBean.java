/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomAllocationException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ANGELY
 */
@Stateless
public class RoomAllocationExceptionSessionBean implements RoomAllocationExceptionSessionBeanRemote, RoomAllocationExceptionSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public List<RoomAllocationException> retrieveAllRoomAllocationExceptions() {
        Query query = em.createQuery("SELECT rae FROM RoomAllocationException rae");
        List<RoomAllocationException> rae = query.getResultList();
        for (RoomAllocationException r : rae){
            r.getTypeOneExceptions().size();
        }
        return rae;
    }
}
