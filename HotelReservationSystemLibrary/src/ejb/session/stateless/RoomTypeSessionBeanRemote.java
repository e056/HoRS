/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomTypeException;
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
    
    public Long createNewRoomType(RoomType roomType) throws RoomTypeNameExistException, UnknownPersistenceException;
    
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException;
    
    public void deleteRoomType(String roomTypeName) throws RoomTypeNotFoundException, DeleteRoomTypeException;
    
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeNotFoundException;
    
   
}
