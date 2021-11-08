/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomReservationLineEntity;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CreateNewReservationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author ANGELY
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "RoomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @Resource
    private EJBContext eJBContext;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Reservation createNewReservation(Reservation reservation) throws CreateNewReservationException, RoomTypeNotFoundException {
        RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(reservation.getRoomType().getName());

        rt.getReservations().add(reservation);
        em.persist(rt);

        em.persist(reservation);

        for (RoomReservationLineEntity lineEntity : reservation.getRoomReservationLineEntities()) {
            if (!lineEntity.getRoom().getEnabled() || !lineEntity.getRoom().getIsAvailable()) {
                eJBContext.setRollbackOnly();
                throw new CreateNewReservationException("Room(s) is not available/enabled for reservation!");
            }
        }

        em.flush();

        return reservation;
    }

    @Override
    public List<Reservation> retrieveReservationsByDate(Date dateToday) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :inDateToday");
        query.setParameter("inDateToday", dateToday);

        return query.getResultList();
    }

    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class,
                reservationId);
        return reservation;

    }

}
