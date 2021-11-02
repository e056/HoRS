/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.ReservationNotFoundException;
import util.exception.RoomNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    

    
    @Override
    public List<Reservation> retrieveReservationsByRoomId(Long roomId) throws RoomNotFoundException
    {
        
        Room room = roomSessionBeanLocal.retrieveRoomByRoomId(roomId);
        
        Query query = em.createQuery("SELECT r.reservation FROM Room r WHERE r.roomId = :inRoomId");
        query.setParameter("inRoomId", roomId);
        
        return query.getResultList();
       
    }
    
    @Override
    public List<Reservation> retrieveReservationsByDate(Date dateToday)
    {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :inDateToday");
        query.setParameter("inDateToday", dateToday);
        
        return query.getResultList();
    }
    
    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException
    {
        Reservation reservation =  em.find(Reservation.class, reservationId);
        return reservation;
        
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
