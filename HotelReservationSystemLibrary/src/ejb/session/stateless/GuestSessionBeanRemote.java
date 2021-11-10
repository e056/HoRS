/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Remote;
import util.exception.GuestNotFoundException;
import util.exception.GuestPassportNumExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 */
@Remote
public interface GuestSessionBeanRemote {

    public Guest guestLogin(String passport, String password) throws InvalidLoginCredentialException;

    public Long createNewGuest(Guest newGuest) throws UnknownPersistenceException, GuestPassportNumExistException;

    public Guest retrieveGuestByPassportNum(String passport) throws GuestNotFoundException;
    
}
