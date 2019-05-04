package com.redhat.cajun.navy.responder.service;

import java.util.List;
import java.util.stream.Collectors;

import com.redhat.cajun.navy.responder.dao.ResponderDao;
import com.redhat.cajun.navy.responder.entity.ResponderEntity;
import com.redhat.cajun.navy.responder.model.Responder;
import com.redhat.cajun.navy.responder.model.ResponderStats;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResponderService {

    private static Logger log = LoggerFactory.getLogger(ResponderService.class);

    @Autowired
    private ResponderDao responderDao;

    @Transactional
    public ResponderStats getResponderStats() {
        ResponderStats stats = new ResponderStats();
        stats.setTotal(responderDao.enrolledRespondersCount().intValue());
        stats.setActive(responderDao.activeRespondersCount().intValue());
        return stats;
    }

    @Transactional
    public Responder getResponder(long id) {
        return toResponder(responderDao.findById(id));
    }

    @Transactional
    public Responder getResponderByName(String name) {
        return toResponder(responderDao.findByName(name));
    }

    @Transactional
    public List<Responder> availableResponders() {

        return responderDao.availableResponders().stream().map(responderEntity -> new Responder.Builder(Long.toString(responderEntity.getId()))
                .name(responderEntity.getName())
                .phoneNumber(responderEntity.getPhoneNumber())
                .latitude(responderEntity.getCurrentPositionLatitude())
                .longitude(responderEntity.getCurrentPositionLongitude())
                .boatCapacity(responderEntity.getBoatCapacity())
                .medicalKit(responderEntity.getMedicalKit())
                .available(responderEntity.isAvailable())
                .person(responderEntity.isPerson())
                .enrolled(responderEntity.isEnrolled())
                .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public Responder createResponder(Responder responder) {

        ResponderEntity entity = fromResponder(responder);
        responderDao.create(entity);
        return toResponder(entity);
    }

    @Transactional
    public void createResponders(List<Responder> responders) {
        responders.stream().map(this::fromResponder).forEach(e -> responderDao.create(e));
    }

    @Transactional
    public Triple<Boolean, String, Responder> updateResponder(Responder toUpdate) {

        ResponderEntity current = responderDao.findById(new Long(toUpdate.getId()));
        if (current == null) {
            log.warn("Responder with id '" + toUpdate.getId() + "' not found in the database");
            return new ImmutableTriple<>(false, "Responder with id + " + toUpdate.getId() + " not found.", null);
        }
        ResponderEntity toUpdateEntity = fromResponder(toUpdate, current);
        if (!stateChanged(current, toUpdateEntity)) {
            log.info("Responder with id '" + toUpdate.getId() + "' : state unchanged. Responder record is not updated.");
            return new ImmutableTriple<>(false, "Responder state not changed", toResponder(current));
        }
        try {
            ResponderEntity merged = responderDao.merge(toUpdateEntity);
            return new ImmutableTriple<>(true, "Responder updated", toResponder(merged));
        } catch (Exception e) {
            log.warn("Exception '" + e.getClass() + "' when updating Responder with id '" + toUpdate.getId() + "'. Responder record is not updated.");
            return new ImmutableTriple<>(false, "Exception '" + e.getClass() + "' when updating Responder", toResponder(current));
        }
    }

    @Transactional
    public void reset() {
        log.info("Reset called");
        responderDao.reset();
    }

    @Transactional
    public void init(List<Responder> responders) {
        log.info("Init called for " + responders.size() + " responders");
        responderDao.reset();
        responders.stream().map(r -> new ImmutablePair<>(r, responderDao.findById(new Long(r.getId()))))
                .filter(p -> p.getRight() != null)
            .map(p -> fromResponder(p.getLeft(), p.getRight()))
            .forEach(e -> responderDao.merge(e));
    }

    @Transactional
    public void init() {
        responderDao.init();
    }

    private boolean stateChanged(ResponderEntity current, ResponderEntity updated) {

        if (updated.getName() != null && !updated.getName().equals(current.getName())) {
            return true;
        }
        if (updated.getPhoneNumber() != null && !updated.getPhoneNumber().equals(current.getPhoneNumber())) {
            return true;
        }
        if (updated.getCurrentPositionLatitude() != null && !updated.getCurrentPositionLatitude().equals(current.getCurrentPositionLatitude())) {
            return true;
        }
        if (updated.getCurrentPositionLongitude() != null && !updated.getCurrentPositionLongitude().equals(current.getCurrentPositionLongitude())) {
            return true;
        }
        if (updated.getBoatCapacity() != null && !updated.getBoatCapacity().equals(current.getBoatCapacity())) {
            return true;
        }
        if (updated.getMedicalKit() != null && !updated.getMedicalKit().equals(current.getMedicalKit())) {
            return true;
        }
        if (updated.isAvailable() != null && !updated.isAvailable().equals(current.isAvailable())) {
            return true;
        }
        if (updated.isPerson() != null && !updated.isPerson().equals(current.isPerson())) {
            return true;
        }
        if (updated.isEnrolled() != null && !updated.isEnrolled().equals(current.isEnrolled())) {
            return true;
        }
        return false;
    }

    private ResponderEntity fromResponder(Responder responder) {

        if (responder == null) {
            return null;
        }

        return new ResponderEntity.Builder(responder.getId() == null ? 0 : new Long(responder.getId()), 0L)
                .name(responder.getName())
                .phoneNumber(responder.getPhoneNumber())
                .currentPositionLatitude(responder.getLatitude())
                .currentPositionLongitude(responder.getLongitude())
                .boatCapacity(responder.getBoatCapacity())
                .medicalKit(responder.isMedicalKit())
                .available(responder.isAvailable())
                .enrolled(responder.isEnrolled())
                .person(responder.isPerson())
                .build();
    }

    private ResponderEntity fromResponder(Responder responder, ResponderEntity current) {

        if (responder == null) {
            return null;
        }

        return new ResponderEntity.Builder(new Long(responder.getId()), current.getVersion())
                .name(responder.getName() == null ? current.getName() : responder.getName())
                .phoneNumber(responder.getPhoneNumber() == null ? current.getPhoneNumber() : responder.getPhoneNumber())
                .currentPositionLatitude(responder.getLatitude() == null ? current.getCurrentPositionLatitude() : responder.getLatitude())
                .currentPositionLongitude(responder.getLongitude() == null ? current.getCurrentPositionLongitude() : responder.getLongitude())
                .boatCapacity(responder.getBoatCapacity() == null ? current.getBoatCapacity() : responder.getBoatCapacity())
                .medicalKit(responder.isMedicalKit() == null ? current.getMedicalKit() : responder.isMedicalKit())
                .available(responder.isAvailable() == null ? current.isAvailable() : responder.isAvailable())
                .person(responder.isPerson() == null ? current.isPerson() : responder.isPerson())
                .enrolled(responder.isEnrolled() == null ? current.isEnrolled() : responder.isEnrolled())
                .build();
    }

    private Responder toResponder(ResponderEntity responder) {

        if (responder == null) {
            return null;
        }

        return new Responder.Builder(Long.toString(responder.getId()))
                .name(responder.getName())
                .phoneNumber(responder.getPhoneNumber())
                .latitude(responder.getCurrentPositionLatitude())
                .longitude(responder.getCurrentPositionLongitude())
                .boatCapacity(responder.getBoatCapacity())
                .medicalKit(responder.getMedicalKit())
                .available(responder.isAvailable())
                .person(responder.isPerson())
                .enrolled(responder.isEnrolled())
                .build();
    }
}
