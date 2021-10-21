/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.Local;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 */
@Local
public interface PartnerSessionBeanLocal {

    public Long createNewPartner(Partner newPartner) throws PartnerUsernameExistException, UnknownPersistenceException;

    public List<Partner> retrieveAllPartners();

    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerNotFoundException;

    public List<Reservation> retrieveReservationsByPartnerId(Long partnerId) throws PartnerNotFoundException;

    public Partner retrievePartnerByUsername(String username) throws PartnerNotFoundException;

    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialException;

}
