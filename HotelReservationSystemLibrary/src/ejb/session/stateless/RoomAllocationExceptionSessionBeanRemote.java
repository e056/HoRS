/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomAllocationException;
import java.util.List;
import javax.ejb.Remote;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PYT
 */
@Remote
public interface RoomAllocationExceptionSessionBeanRemote {

    public Long createRoomAllocationException(RoomAllocationException exc, Long reservationId) throws UnknownPersistenceException, ReservationNotFoundException;

    public RoomAllocationException retrieveRoomAllocationException(Long id);

    public List<RoomAllocationException> retrieveAllRoomAllocationException();

}
