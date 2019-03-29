package com.redhat.cajun.navy.responder.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ResponderRowMapper implements RowMapper<Responder> {


    @Override
    public Responder mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Responder.Builder(Long.toString(rs.getLong("responder_id")))
                .name(rs.getString("responder_name"))
                .phoneNumber(rs.getString("responder_phone_number"))
                .boatCapacity(rs.getInt("boat_capacity"))
                .medicalKit(rs.getBoolean("has_medical_kit"))
                .latitude(BigDecimal.valueOf(rs.getDouble("responder_current_gps_lat")))
                .longitude(BigDecimal.valueOf(rs.getDouble("responder_current_gps_long")))
                .available(rs.getBoolean("available"))
                .build();
    }
}
