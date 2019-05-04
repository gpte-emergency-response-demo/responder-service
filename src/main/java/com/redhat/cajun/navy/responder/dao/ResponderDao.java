package com.redhat.cajun.navy.responder.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.redhat.cajun.navy.responder.entity.ResponderEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(ResponderEntity responder) {
        entityManager.persist(responder);
    }

    void deleteAll() {
        Query deleteAll = entityManager.createQuery("DELETE FROM ResponderEntity");
        deleteAll.executeUpdate();
    }

    public ResponderEntity findById(long id) {
        return entityManager.find(ResponderEntity.class, id, LockModeType.OPTIMISTIC);
    }

    @SuppressWarnings("unchecked")
    public ResponderEntity findByName(String name) {
        Query q = entityManager.createQuery("SELECT r FROM ResponderEntity r WHERE r.name = :name");
        q.setParameter("name", name);
        List<ResponderEntity> results = q.getResultList();
        if (results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new NonUniqueResultException("Found several Responders with name '" + name + "'");
        }
    }

    public ResponderEntity merge(ResponderEntity responder) {
        ResponderEntity r = entityManager.merge(responder);
        entityManager.flush();
        return r;
    }

    @SuppressWarnings("unchecked")
    public List<ResponderEntity> availableResponders() {
        return (List<ResponderEntity>) entityManager.createQuery("SELECT r FROM ResponderEntity r WHERE r.available = true and r.enrolled = true")
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public void reset() {
        Query select = entityManager.createQuery("SELECT r FROM ResponderEntity r");
        List<ResponderEntity> results = select.getResultList();
        results.stream()
            .map(r -> {
                ResponderEntity.Builder rb = new ResponderEntity.Builder(r).available(true).enrolled(false);
                if (r.isPerson()) {
                    rb.currentPositionLatitude(null).currentPositionLongitude(null);
                }
                return rb.build();
            }).forEach(r -> entityManager.merge(r));
        entityManager.flush();
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        Query deleteNonPersons = entityManager.createQuery("DELETE FROM ResponderEntity r where r.person = false");
        deleteNonPersons.executeUpdate();
        Query persons = entityManager.createQuery("SELECT r FROM ResponderEntity r where r.person = true");
        List<ResponderEntity> results = persons.getResultList();
        results.stream()
                .map(r -> new ResponderEntity.Builder(r).available(true).enrolled(false)
                        .currentPositionLatitude(null).currentPositionLongitude(null).build())
                .forEach(r -> entityManager.merge(r));
        entityManager.flush();
    }

    public Long enrolledRespondersCount() {
        return (Long) entityManager.createQuery("SELECT COUNT(r.id) FROM ResponderEntity r WHERE r.enrolled = true").getSingleResult();
    }

    public Long activeRespondersCount() {
        return (Long) entityManager
                .createQuery("SELECT COUNT(r.id) FROM ResponderEntity r WHERE r.enrolled = true AND r.available = false").getSingleResult();

    }
}
