package com.example.ekycdemo2.model;

import java.util.Date;

public class IDCard {
    private Integer id;
    private String name;
    private Date birthDay;
    private String location;
    private String signedLocation;
    private String feature;
    private String issueDate;
    private String issueLocation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSignedLocation() {
        return signedLocation;
    }

    public void setSignedLocation(String signedLocation) {
        this.signedLocation = signedLocation;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueLocation() {
        return issueLocation;
    }

    public void setIssueLocation(String issueLocation) {
        this.issueLocation = issueLocation;
    }

    @Override
    public String toString() {
        return "IDCard{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDay=" + birthDay +
                ", location='" + location + '\'' +
                ", signedLocation='" + signedLocation + '\'' +
                ", feature='" + feature + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", issueLocation='" + issueLocation + '\'' +
                '}';
    }

    enum facing {
        FRONT, BACK
    }
}
