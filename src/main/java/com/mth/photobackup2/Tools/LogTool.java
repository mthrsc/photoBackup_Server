/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mth
 */
public class LogTool {

    String filePath;
    File logFile;

    public LogTool() {
        filePath = "photoBackupLog.txt";
        logFile = new File(filePath);

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                System.out.println("-----Could not create log file");
                e.printStackTrace();
            }
        }
    }

    public void logToFile(String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(new Date());
        try {
            FileWriter fr = new FileWriter(logFile, true);
            fr.write(date + " - " + message + "\n");
            fr.close();
        } catch (IOException e) {
            System.out.println("-----Could not write log file");
            e.printStackTrace();
        }
    }
}
