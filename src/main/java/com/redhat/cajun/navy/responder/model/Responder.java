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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public int getBoatCapacity() {
        return boatCapacity;
    }

    public void setBoatCapacity(int boatCapacity) {
        this.boatCapacity = boatCapacity;
    }

    public boolean isMedicalKit() {
        return medicalKit;
    }

    public void setMedicalKit(boolean medicalKit) {
        this.medicalKit = medicalKit;
    }
}
