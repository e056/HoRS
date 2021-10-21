/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Long createNewPartner(Partner newPartner) throws PartnerUsernameExistException, UnknownPersistenceException {
        try {
            em.persist(newPartner);
            em.flush();

            return newPartner.getPartnerId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new PartnerUsernameExistException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    

    public List<Partner> retrieveAllPartners()
    {
        Query query = em.createQuery("SELECT s FROM Partner s");
        
        List<Partner> partners = query.getResultList();
        
        for (Partner p : partners) {
            p.getReservations().size();
            
        }
        
        return query.getResultList();
    }

    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerNotFoundException {
        Partner partner = em.find(Partner.class, partnerId);

        if (partner != null) {
            return partner;
        } else {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist!");
        }
    }

    public List<Reservation> retrieveReservationsByPartnerId(Long partnerId) throws PartnerNotFoundException {
        Partner partner = retrievePartnerByPartnerId(partnerId);
        List<Reservation> reservations = partner.getReservations();

        // Might have to do smth here? Lazy fetching for rooms
        return reservations;
    }

    public Partner retrievePartnerByUsername(String username) throws PartnerNotFoundException {
        Query query = em.createQuery("SELECT e FROM Partner e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);

        try {
            return (Partner) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new PartnerNotFoundException("Partner Username " + username + " does not exist!");
        
        }
    }
        

    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            Partner partner = retrievePartnerByUsername(username);

            if (partner.getPartnerPassword().equals(password)) {
                partner.getReservations().size();
                return partner;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (PartnerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
}
