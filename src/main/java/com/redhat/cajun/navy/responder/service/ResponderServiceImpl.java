package com.redhat.cajun.navy.responder.service;

import javax.sql.DataSource;

import com.redhat.cajun.navy.responder.ResponderStats;
import com.redhat.cajun.navy.responder.model.Responder;
import com.redhat.cajun.navy.responder.model.ResponderRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResponderServiceImpl implements ResponderService {

    @Autowired
    private DataSource datasource;

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
}
