
package com.mth.photobackup2.Tools;

import com.mth.photobackup2.Config.ServerParameters;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Matthieu Roscio
 */
public class ServerParametersManipulation {

    private LogTool logTool = new LogTool();
    private JsonManipulation jm = new JsonManipulation();
    private String parametersLocation = System.getProperty("user.home") + "\\PhotoBackup\\ServerParameters.json";

    public ServerParametersManipulation() {
    }

    public ServerParameters getParameters() {
        logTool.logToFile("getParameters starts");
        ServerParameters sp = new ServerParameters();
        File file = new File(parametersLocation);
        boolean fileExists = isJSonThere(file);
        boolean jsonValid = false;
        try {
            jsonValid = jm.isJSonValid(file);
        } catch (Exception e) {
            logTool.logToFile("getParameters Error: " + e.getMessage());
        }

        try {
            if (fileExists && jsonValid) {
                logTool.logToFile("getParameters loadSettings");
                sp = jm.loadSettings();
                //Deserialize
            } else {
                logTool.logToFile("getParameters generating default settings");
                sp.setPort(8080);
                sp.setFolder("C:\\PhotoBackup");
                sp.setPassword("password");
                //Serialize
                jm.saveSettings(sp);
            }
        } catch (Exception e) {
            logTool.logToFile("getParameters Error: " + e.getMessage());
            e.printStackTrace();
        }
        return sp;
    }

    public void createSaveLocation() {
        Path location1 = Paths.get(System.getProperty("user.home") + "\\PhotoBackup");
        String location2 = System.getProperty("user.home") + "\\PhotoBackup\\ServerParameters.json";

        File file1 = location1.toFile();
        File file2 = new File(location2);
        try {
            if (!file1.exists()) {
                Files.createDirectory(location1);
            }
            if (!file2.exists()) {
                file2.createNewFile();
            }
        } catch (IOException e) {
            logTool.logToFile("createSaveLocation Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isJSonThere(File file) {
        boolean b = false;
        b = file.exists();
        return b;
    }

}
