package com.redhat.cajun.navy.responder.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import com.redhat.cajun.navy.responder.dao.ResponderDao;
import com.redhat.cajun.navy.responder.model.Responder;
import com.redhat.cajun.navy.responder.model.ResponderRowMapper;
import com.redhat.cajun.navy.responder.model.ResponderStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResponderServiceImpl implements ResponderService {

    @Autowired
    private DataSource datasource;

    @Autowired
    private ResponderDao responderDao;

    private JdbcTemplate jdbcTemplate;

    @Override
    public ResponderStats getResponderStats() {
        jdbcTemplate = new JdbcTemplate(datasource);
        ResponderStats stats = new ResponderStats();
        String sqlTotal = "SELECT count(responder_id) FROM responder";
        String sqlActive = "SELECT count(mission_id) FROM mission where current_status IN ('Assigned','Pickedup')";
        stats.setTotal(jdbcTemplate.queryForObject(sqlTotal, Integer.class));
        stats.setActive(jdbcTemplate.queryForObject(sqlActive, Integer.class));
        return stats;
    }

    @Override
    public Responder getResponder(long id) {
        jdbcTemplate = new JdbcTemplate(datasource);
        String sqlResponderById = "SELECT * from responder WHERE responder_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlResponderById, new Object[]{id}, new ResponderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<Responder> availableResponders() {

        return responderDao.availableResponders().stream().map(responderEntity -> new Responder.Builder(responderEntity.getId())
                .name(responderEntity.getName())
                .phoneNumber(responderEntity.getPhoneNumber())
                .latitude(responderEntity.getCurrentPositionLatitude())
                .longitude(responderEntity.getCurrentPositionLongitude())
                .boatCapacity(responderEntity.getBoatCapacity())
                .medicalKit(responderEntity.getMedicalKit())
                .available(responderEntity.isAvailable())
                .build())
                .collect(Collectors.toList());
    }
}
