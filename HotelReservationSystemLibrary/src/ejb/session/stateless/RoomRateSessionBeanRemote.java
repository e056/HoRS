/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
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
@Remote
public interface RoomRateSessionBeanRemote {

    public Long createNewRoomRate(RoomRate roomRate, Long roomTypeId) throws UnknownPersistenceException, RoomTypeNotFoundException, RoomRateNameExistException;

    public List<RoomRate> retrieveAllRoomRates();

    public RoomRate retrieveRoomRateByRoomRateId(Long id) throws RoomRateNotFoundException;

    public RoomRate retrieveRoomRateByRoomRateName(String name) throws RoomRateNotFoundException;

    public RoomRate retrievePublishedRoomRateByRoomType(Long roomTypeId)  throws RoomRateNotFoundException;

    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, UpdateRoomRateException;

    public void deleteRoomRate(Long rrId) throws RoomRateNotFoundException, DeleteRoomRateException;

    public BigDecimal retrieveTotalPriceForOnlineReservationByRoomTyoe(Long roomTypeId, Date checkInDate, Date checkOutDate);
}
