package com.example.studentdatacard.Document;


import java.io.Serializable;

public class model implements Serializable {
    String userId,iD,name,course,email,purl;
    model()
    {

    }

    public model(String userId, String iD, String name, String course, String email, String purl) {
        this.userId = userId;
        this.iD = iD;
        this.name = name;
        this.course = course;
        this.email = email;
        this.purl = purl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }
}
