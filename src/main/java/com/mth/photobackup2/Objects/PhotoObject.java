/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2.Objects;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mth
 */
@Entity
@Table(name = "PhotoObjects")
@XmlRootElement
public class PhotoObject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fileName;
    private String dateTaken;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    private String locationOnDisk;
    private String dateUploaded;
    private String locationOnDevice;

    public PhotoObject() {
    }

    public PhotoObject(String fileName, String locationOnDisk) {
        this.fileName = fileName;
        this.locationOnDisk = locationOnDisk;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public String getLocationOnDisk() {
        return locationOnDisk;
    }

    public void setLocationOnDisk(String locationOnDisk) {
        this.locationOnDisk = locationOnDisk;
    }

    public String getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(String dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationOnDevice() {
        return locationOnDevice;
    }

    public void setLocationOnDevice(String locationOnDevice) {
        this.locationOnDevice = locationOnDevice;
    }

    @Override
    public String toString() {
        return "PhotoObject{" + "fileName=" + fileName + ", dateTaken=" + dateTaken + ", longitude=" + longitude + ", latitude=" + latitude + ", altitude=" + altitude + ", locationOnDisk=" + locationOnDisk + ", dateUploaded=" + dateUploaded + '}';
    }

    public String toStringCompare() {
        return "PhotoObject{" + "fileName=" + fileName + ", dateTaken=" + dateTaken + ", longitude=" + longitude + ", latitude=" + latitude + ", altitude=" + altitude + '}';
    }
}
