/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteRoomTypeException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author ANGELY
 */
@Local
public interface RoomTypeSessionBeanLocal {

    public List<RoomType> retrieveAllRoomTypes();

     public Long createNewRoomType(RoomType roomType, String nextHigherRoomTypeName) throws RoomTypeNameExistException, UnknownPersistenceException, RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException;

    public void deleteRoomType(RoomType roomType) throws RoomTypeNotFoundException, DeleteRoomTypeException;

    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeNotFoundException;

    public RoomType retrieveRoomTypeByRoomId(Long roomId) throws RoomTypeNotFoundException;
    
    public Long createNewRoomType(RoomType roomType) throws RoomTypeNameExistException, UnknownPersistenceException;
}
