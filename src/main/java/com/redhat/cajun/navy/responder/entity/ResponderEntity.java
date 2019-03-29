package com.redhat.cajun.navy.responder.entity;

import java.math.BigDecimal;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Access(AccessType.FIELD)
@SequenceGenerator(name="ResponderSeq", sequenceName="responder_sequence")
@Table(name = "Responder")
public class ResponderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="ResponderSeq")
    @Column(name = "responder_id")
    private long id;

    @Column(name = "responder_name")
    private String name;

    @Column(name = "responder_phone_number")
    private String phoneNumber;

    @Column(name = "responder_current_gps_lat", scale = 5, precision = 7)
    private BigDecimal currentPositionLatitude;

    @Column(name = "responder_current_gps_long", scale = 5, precision = 7)
    private BigDecimal currentPositionLongitude;

    @Column(name = "boat_capacity")
    private Integer boatCapacity;

    @Column(name = "has_medical_kit")
    private Boolean medicalKit;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "version")
    @Version
    private long version;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public BigDecimal getCurrentPositionLatitude() {
        return currentPositionLatitude;
    }

    public BigDecimal getCurrentPositionLongitude() {
        return currentPositionLongitude;
    }

    public Integer getBoatCapacity() {
        return boatCapacity;
    }

    public Boolean getMedicalKit() {
        return medicalKit;
    }

    public Boolean isAvailable() {
        return available;
    }

    public long getVersion() {
        return version;
    }

    public static class Builder {

        private final ResponderEntity responder;

        public Builder() {
            this.responder = new ResponderEntity();
        }

        public Builder(long id) {
            this.responder = new ResponderEntity();
            responder.id = id;
        }

        public Builder(ResponderEntity r) {
            this.responder = new ResponderEntity();
            responder.id = r.getId();
            responder.available = r.isAvailable();
            responder.medicalKit = r.getMedicalKit();
            responder.boatCapacity = r.getBoatCapacity();
            responder.currentPositionLatitude = r.getCurrentPositionLatitude();
            responder.currentPositionLongitude = r.getCurrentPositionLongitude();
            responder.name = r.getName();
            responder.phoneNumber = r.getPhoneNumber();
            responder.version = r.getVersion();
        }

        public Builder name(String name) {
            responder.name = name;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            responder.phoneNumber = phoneNumber;
            return this;
        }

        public Builder currentPositionLatitude(BigDecimal latitude) {
            responder.currentPositionLatitude = latitude;
            return this;
        }

        public Builder currentPositionLongitude(BigDecimal longitude) {
            responder.currentPositionLongitude = longitude;
            return this;
        }

        public Builder boatCapacity(int boatCapacity) {
            responder.boatCapacity = boatCapacity;
            return this;
        }

        public Builder medicalKit(boolean medicalKit) {
            responder.medicalKit = medicalKit;
            return this;
        }

        public Builder available(boolean available) {
            responder.available = available;
            return this;
        }

        public ResponderEntity build() {
            return responder;
        }

    }
}
