/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GuestNotFoundException;
import util.exception.GuestPassportNumExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Long createNewGuest(Guest newGuest) throws UnknownPersistenceException, GuestPassportNumExistException {
        try {
            em.persist(newGuest);
            em.flush();

            return newGuest.getGuestId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new GuestPassportNumExistException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    public Guest guestLogin(String passport, String password) throws InvalidLoginCredentialException {
        try {
            Guest employee = retrieveGuestByPassportNum(passport);

            if (employee.getPassword().equals(password)) {

                return employee;
            } else {
                throw new InvalidLoginCredentialException("Passport does not exist or invalid password!");
            }
        } catch (GuestNotFoundException ex) {
            throw new InvalidLoginCredentialException("Passport does not exist or invalid password!");
        }
    }

    public Guest retrieveGuestByPassportNum(String passport) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.passportNumber = :passport");
        query.setParameter("passport", passport);

        try {
            return (Guest) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Guest Passport Number " + passport + " does not exist!");
        }
    }

}
