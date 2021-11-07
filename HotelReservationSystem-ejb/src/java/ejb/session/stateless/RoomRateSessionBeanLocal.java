/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteRoomRateException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author PYT
 */
@Local
public interface RoomRateSessionBeanLocal {

    public Long createNewRoomRate(RoomRate roomRate, Long roomTypeId) throws UnknownPersistenceException, RoomTypeNotFoundException, RoomRateNameExistException;

    public List<RoomRate> retrieveAllRoomRates();

    public RoomRate retrieveRoomRateByRoomRateId(Long id) throws RoomRateNotFoundException;

    public RoomRate retrieveRoomRateByRoomRateName(String name) throws RoomRateNotFoundException;

    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, UpdateRoomRateException;

    public void deleteRoomRate(Long rrId) throws RoomRateNotFoundException, DeleteRoomRateException;

    public RoomRate retrievePublishedRoomRateByRoom(Long roomId)  throws RoomRateNotFoundException;

}
