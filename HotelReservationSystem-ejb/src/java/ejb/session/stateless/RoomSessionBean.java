/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.RoomNumberExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY 
 * Added: Create new room, view all rooms
 * Not added: update room, delete room
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Long createNewRoom(Room room) throws RoomNumberExistException, UnknownPersistenceException {
        try {
            em.persist(room);
            em.flush();

            return room.getRoomId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomNumberExistException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT s FROM Room s");

        List<Room> rooms = query.getResultList();

        for (Room r : rooms) {
           r.getReservation();
           r.getRoomType();
        }
        return rooms;
    }

}
