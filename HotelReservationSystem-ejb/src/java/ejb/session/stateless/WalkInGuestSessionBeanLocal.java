/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.WalkInGuest;
import javax.ejb.Local;
import util.exception.UnknownPersistenceException;
import util.exception.WalkInGuestNotFoundException;

/**
 *
 * @author PYT
 */
@Local
public interface WalkInGuestSessionBeanLocal {

    public void associateGuestWithReservation(Reservation reservation, Long guestId);

    public WalkInGuest retrieveWalkInGuestById(Long id);

    public Long createNewWalkInGuest(WalkInGuest newGuest, Long reservationId) throws UnknownPersistenceException;
    
    public WalkInGuest retrieveWalkInGuestByPassportNo(String passportNo) throws WalkInGuestNotFoundException;
    
}
