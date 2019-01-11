package com.redhat.cajun.navy.responder.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ResponderRowMapper implements RowMapper<Responder> {


    @Override
    public Responder mapRow(ResultSet rs, int rowNum) throws SQLException {

        Responder responder = new Responder();
        responder.setId(rs.getLong("responder_id"));
        responder.setName(rs.getString("responder_name"));
        responder.setPhoneNumber(rs.getString("responder_phone_number"));
        responder.setBoatCapacity(rs.getInt("boat_capacity"));
        responder.setMedicalKit(rs.getBoolean("has_medical_kit"));
        responder.setLatitude(BigDecimal.valueOf(rs.getDouble("responder_current_gps_lat")));
        responder.setLongitude(BigDecimal.valueOf(rs.getDouble("responder_current_gps_long")));
        return responder;
    }
}
