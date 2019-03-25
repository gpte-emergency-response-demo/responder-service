package com.redhat.cajun.navy.responder.model;

import java.math.BigDecimal;

public class Responder {

    private long id;

    private String name;

    private String phoneNumber;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private int boatCapacity;

    private boolean medicalKit;

    private boolean available;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public int getBoatCapacity() {
        return boatCapacity;
    }

    public boolean isMedicalKit() {
        return medicalKit;
    }

    public boolean isAvailable() {
        return available;
    }

    public static class Builder {

        private final Responder responder;

        public Builder(long id) {
            this.responder = new Responder();
            responder.id = id;
        }

        public Builder name(String name) {
            responder.name = name;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            responder.phoneNumber = phoneNumber;
            return this;
        }

        public Builder latitude(BigDecimal latitude) {
            responder.latitude = latitude;
            return this;
        }

        public Builder longitude(BigDecimal longitude) {
            responder.longitude = longitude;
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

        public Responder build() {
            return responder;
        }

    }
}
