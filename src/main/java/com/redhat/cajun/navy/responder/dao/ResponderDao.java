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

    private String[] coordinates = {"34.16877, -77.87045", "34.18323, -77.84099", "34.23670, -77.83479", "34.14338, -77.88274",
            "34.29256, -77.86569", "34.12679, -77.87353", "34.29515, -77.81463", "34.29103, -77.86601", "34.24544, -77.83508",
            "34.15140, -77.89115", "34.12112, -77.94435", "34.12579, -77.89562", "34.26845, -77.84534", "34.24732, -77.82757",
            "34.15593, -77.88559", "34.25137, -77.82163", "34.28515, -77.81113", "34.22543, -77.89744", "34.21485, -77.88824",
            "34.17537, -77.83297", "34.23755, -77.84025", "34.18062, -77.82813", "34.28235, -77.83150", "34.13362, -77.87096",
            "34.22852, -77.88805"};

    @PersistenceContext
    private EntityManager entityManager;

    public void create(ResponderEntity responder) {
        entityManager.persist(responder);
    }

    public void deleteAll() {
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

    public void init() {
        Query select = entityManager.createQuery("SELECT r FROM ResponderEntity r");
        List<ResponderEntity> results = select.getResultList();
        results.stream().filter(responderEntity -> responderEntity.getId() <= 25)
                .map(r -> new ResponderEntity.Builder(r).available(true).build())
                .forEach(r -> entityManager.merge(r));
        entityManager.flush();
    }
}
