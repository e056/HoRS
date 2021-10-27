/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteRoomRateException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author PYT
 */
@Local
public interface RoomRateSessionBeanLocal {
    
    public Long createNewRoomRate(RoomRate room, Long roomTypeId) throws UnknownPersistenceException;
    
    public List<RoomRate> retrieveAllRoomRates();
    
    public RoomRate retrieveRoomRateByRoomRateId(Long id) throws RoomRateNotFoundException;
    
    public void updateRoomRate(RoomType roomType, RoomRate roomRate) throws RoomRateNotFoundException, UpdateRoomRateException;
    
    public void deleteRoomRate(String roomRateName) throws RoomRateNotFoundException, DeleteRoomRateException;
}
