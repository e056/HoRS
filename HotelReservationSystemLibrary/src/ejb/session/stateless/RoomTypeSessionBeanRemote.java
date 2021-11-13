/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomTypeException;
import util.exception.RoomTypeIsLowestException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author ANGELY
 */
@Remote
public interface RoomTypeSessionBeanRemote {
    
    public List<RoomType> retrieveAllRoomTypes();
    
    public Long createNewRoomType(RoomType roomType, String nextHigherRoomTypeName) throws RoomTypeNameExistException, UnknownPersistenceException, RoomTypeNotFoundException;
    
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException, RoomTypeNameExistException;
    
    public void deleteRoomType(RoomType roomType) throws RoomTypeNotFoundException, DeleteRoomTypeException;
    
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeNotFoundException;

    public List<RoomType> retrieveAllEnabledRoomTypes();
    
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomId) throws RoomTypeNotFoundException;

    public RoomType retrieveRoomTypeByNextHighestRoomType(String roomTypeName)throws RoomTypeIsLowestException;

    public RoomType retrieveHighestRoomType();

    public Long createNewRoomType(RoomType roomType) throws RoomTypeNameExistException, UnknownPersistenceException;

    public List<RoomType> retrieveRoomTypesAvailableForReservation(int numOfRooms, Date checkInDate, Date checkOutDate);
    
    public RoomType retrieveEnabledRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeNotFoundException;


    
   
}
