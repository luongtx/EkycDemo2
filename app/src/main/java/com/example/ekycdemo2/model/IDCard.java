package com.example.ekycdemo2.model;

import com.google.firebase.database.Exclude;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IDCard {

    private String id;
    private String name;
    private String dob;
    private String address;
    private String issuedDate;
    private String issuedAddress;
    @Exclude
    private ArrayList<File> storedFiles = new ArrayList<>(2);
    @Exclude
    public static int FRONT = 0;
    @Exclude
    public static int BACK = 1;

    public IDCard() {

    }

    public IDCard(String id, String name, String dob, String address, String issuedDate, String issuedAddress) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.issuedDate = issuedDate;
        this.issuedAddress = issuedAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getIssuedAddress() {
        return issuedAddress;
    }

    public void setIssuedAddress(String issuedAddress) {
        this.issuedAddress = issuedAddress;
    }

    @Exclude
    public ArrayList<File> getStoredFiles() {
        return storedFiles;
    }

    public void setStoredFiles(ArrayList<File> storedFiles) {
        this.storedFiles = storedFiles;
    }

    public void extract(List<Prediction> predictions) {
        if (id == null) {
            id = predictions.get(0).getOcrText();
            name = predictions.get(1).getOcrText();
            dob = predictions.get(2).getOcrText();
            address = predictions.get(3).getOcrText();
        } else {
            issuedDate = predictions.get(0).getOcrText();
            issuedAddress = predictions.get(1).getOcrText();
        }
    }

    public boolean isFilled() {
        return (id != null) && (issuedDate != null);
    }

}
