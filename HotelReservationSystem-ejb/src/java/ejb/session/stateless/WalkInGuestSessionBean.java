/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;


import entity.Reservation;
import entity.WalkInGuest;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GuestPassportNumExistException;
import util.exception.UnknownPersistenceException;
import util.exception.WalkInGuestNotFoundException;
import util.exception.WalkInGuestPassportNumExistException;

/**
 *
 * @author PYT
 */
@Stateless
public class WalkInGuestSessionBean implements WalkInGuestSessionBeanRemote, WalkInGuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager entityManager;
    
    @Override
    public Long createNewWalkInGuest(WalkInGuest newGuest, Long reservationId) throws UnknownPersistenceException{
        try {
            Reservation reservation = entityManager.find(Reservation.class, reservationId);
            newGuest.getReservations().add(reservation);
            reservation.setWalkInGuest(newGuest);
            entityManager.persist(newGuest);
            entityManager.flush();

            return newGuest.getWalkInGuestId();
        } catch (PersistenceException ex) {
            
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    
    
    @Override
    public WalkInGuest retrieveWalkInGuestByPassportNo(String passportNo) throws WalkInGuestNotFoundException
    {
        try{
            Query query = entityManager.createQuery("SELECT w FROM WalkInGuest w WHERE w.passportNumber = :inNo");
            query.setParameter("inNo", passportNo);
        return (WalkInGuest) query.getSingleResult();
        } catch(NoResultException ex)
        {
            throw new WalkInGuestNotFoundException("Walk In Guest does not exist!");
        }
        
    }
    
    @Override
    public void associateGuestWithReservation(Reservation reservation, Long guestId)
    {
        WalkInGuest guest = entityManager.find(WalkInGuest.class,guestId);
        Reservation res = entityManager.find(Reservation.class,reservation.getReservationId());
        res.setWalkInGuest(guest);
        guest.getReservations().add(res);
    }
    
    @Override
    public WalkInGuest retrieveWalkInGuestById(Long id)
    {
        return entityManager.find(WalkInGuest.class, id);
    }
    
}
