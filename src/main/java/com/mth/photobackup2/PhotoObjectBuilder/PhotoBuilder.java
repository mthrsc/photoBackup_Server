/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2.PhotoObjectBuilder;

import com.mth.photobackup2.Objects.PhotoObject;
import java.io.File;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import static com.drew.metadata.exif.ExifDirectoryBase.TAG_DATETIME_ORIGINAL;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import static com.drew.metadata.exif.GpsDirectory.TAG_ALTITUDE;
import com.mth.photobackup2.Tools.LogTool;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mth
 */
public class PhotoBuilder {

    private LogTool logTool = new LogTool();

    public PhotoBuilder() {

    }

    public PhotoObject buildAfterUpload(File file) {

        logTool.logToFile("Creating PO for file: " + file.getName());
        PhotoObject po = new PhotoObject(file.getName(), file.getAbsolutePath());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        po.setDateUploaded(formatter.format(date));

        try {
            Double lat = null;
            Double longi = null;
            Double alt = null;
            String dateTaken = new String();
            String altString = new String();

            GpsDirectory gps;
            GeoLocation geo;

            Metadata metadata = ImageMetadataReader.readMetadata(file);

//            System.out.println("\n\n--------Metadata Extraction-------------");
            gps = metadata.getFirstDirectoryOfType(GpsDirectory.class);

            try {
                geo = gps.getGeoLocation();
                lat = geo.getLatitude();
                longi = geo.getLongitude();
            } catch (NullPointerException e) {
//                System.err.println("-------No GPS Data");
            }

            try {
                altString = gps.getDescription(TAG_ALTITUDE);
                altString = altString.substring(0, altString.indexOf(" "));
                alt = Double.parseDouble(altString);
            } catch (NullPointerException e) {
//                System.err.println("-------No Altitude Data");
            }

            ExifSubIFDDirectory exifSub = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            try {
                Date d = exifSub.getDate(TAG_DATETIME_ORIGINAL);
                if (d != null) {
                    dateTaken = formatter.format(d);
                } else {
                    dateTaken = null;
                }
            } catch (NullPointerException e) {
                dateTaken = null;
//                System.out.println("-------No date metadata");
            }

            //To create the gallery on the device, we sort the pictures by date taken
            //If the file do not have metadata about date taken, 
            //we input the upload date.
            if (dateTaken != null) {
                po.setDateTaken(dateTaken);
            }
            if (lat != null && longi != null) {
                po.setLatitude(lat);
                po.setLongitude(longi);
            }
            if (alt != null) {
                po.setAltitude(alt);
            }
        } catch (ImageProcessingException | IOException | NumberFormatException e) {
            System.out.println("-----No metadata to extract");
//            e.printStackTrace();
        }
        logTool.logToFile("Created PO for file: " + file.getName() + " - Result: " + po.toString());
        return po;
    }

    public PhotoObject buildDbRebuild(File file) {

        logTool.logToFile("(REBUILD DB)Creating PO for file: " + file.getName());

        PhotoObject po = new PhotoObject(file.getName(), file.getAbsolutePath());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        po.setDateUploaded(formatter.format(date));
        po.setDateUploaded(formatter.format(file.lastModified()));

        try {
            Double lat = null;
            Double longi = null;
            Double alt = null;
            String dateTaken = new String();
            String altString = new String();

            GpsDirectory gps;
            GeoLocation geo;

            Metadata metadata = ImageMetadataReader.readMetadata(file);

//            System.out.println("\n\n--------Metadata Extraction-------------");
            gps = metadata.getFirstDirectoryOfType(GpsDirectory.class);

            try {
                geo = gps.getGeoLocation();
                lat = geo.getLatitude();
                longi = geo.getLongitude();
            } catch (NullPointerException e) {
//                System.err.println("-------No GPS Data");
            }

            try {
                altString = gps.getDescription(TAG_ALTITUDE);
                altString = altString.substring(0, altString.indexOf(" "));
                alt = Double.parseDouble(altString);
            } catch (NullPointerException e) {
//                System.err.println("-------No Altitude Data");
            }

            ExifSubIFDDirectory exifSub = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            try {
                Date d = exifSub.getDate(TAG_DATETIME_ORIGINAL);
                if (d != null) {
                    dateTaken = formatter.format(d);
                } else {
                    dateTaken = null;
                }
            } catch (NullPointerException e) {
                dateTaken = null;
            }

            //To create the gallery on the device, we sort the pictures by date taken
            //If the file do not have metadata about date taken, 
            //we input the upload date.
            if (dateTaken != null) {
                po.setDateTaken(dateTaken);
            }
            if (lat != null && longi != null) {
                po.setLatitude(lat);
                po.setLongitude(longi);
            }
            if (alt != null) {
                po.setAltitude(alt);
            }
        } catch (ImageProcessingException | IOException | NumberFormatException e) {
            System.out.println("-----No metadata to extract");
//            e.printStackTrace();
        }
        logTool.logToFile("(REBUILD DB)Creating PO for file: " + file.getName() + " - Result: " + po.toString());
        return po;
    }

}
