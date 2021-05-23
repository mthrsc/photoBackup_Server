package com.mth.photobackup2.Tools;

import com.mth.photobackup2.Config.ServerParameters;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author Matthieu Roscio
 */
public class JsonManipulation {

    private LogTool logTool = new LogTool();
    private String parametersLocation = System.getProperty("user.home") + "\\PhotoBackup\\ServerParameters.json";

    public JsonManipulation() {
    }

    //Get settings from user folder
    public ServerParameters loadSettings() {
        ServerParameters sp = null;
        Gson g = new Gson();
        String pathToFile = parametersLocation;
        String json = new String();
        try {
            json = new String(Files.readAllBytes(Paths.get(pathToFile)));
        } catch (IOException e) {
            logTool.logToFile("loadSettings finishes - Error: " + e.getMessage());
            e.printStackTrace();
        }
        sp = g.fromJson(json, ServerParameters.class);
        return sp;
    }

    //Save settings to user folder
    public void saveSettings(ServerParameters sp) {
        logTool.logToFile("saveSettings starts");
        try {
            Gson g = new Gson();
            String json = g.toJson(sp);
            String path = parametersLocation;

            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(json);

            writer.close();
        } catch (IOException e) {
            logTool.logToFile("saveSettings Error: " + e.getMessage());
            e.printStackTrace();
        }
        logTool.logToFile("saveSettings Finishes");

    }

    //Basic Json validation 
    public boolean isJSonValid(File file) {
        boolean b = true;
        String json = new String();
        try {
            json = new String(Files.readAllBytes(Paths.get(file.toString())));
        } catch (IOException ex) {
            b = false;
            return b;
        }

        Character first = json.charAt(0);
        Character last = json.charAt(json.length() - 1);

        if (first != '{') {
            b = false;
            return b;
        }
        if (last != '}') {
            b = false;
            return b;
        }
        if (!json.contains("port")) {
            b = false;
            return b;
        }
        if (!json.contains("folder")) {
            b = false;
            return b;
        }
        if (!json.contains("password")) {
            b = false;
            return b;
        }

        System.out.println("------------JSON not Valid");
        return b;
    }

    public String convertToJson(Object o) {
        logTool.logToFile("convertToJson starts");

        String json = new String();
        Gson g = new Gson();
        json = g.toJson(o);

        logTool.logToFile("convertToJson finishes return: " + json);

        return json;
    }
}
