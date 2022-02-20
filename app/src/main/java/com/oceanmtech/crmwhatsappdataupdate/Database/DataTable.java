package com.oceanmtech.crmwhatsappdataupdate.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DataTable {

    @PrimaryKey (autoGenerate = true)
    int id;

    String name, mobile, imgpath, message;

    public DataTable(String name, String mobile, String imgpath, String message) {
        this.name = name;
        this.mobile = mobile;
        this.imgpath = imgpath;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CRMModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", imgpath='" + imgpath + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

